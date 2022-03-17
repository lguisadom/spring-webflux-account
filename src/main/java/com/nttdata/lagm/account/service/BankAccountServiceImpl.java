package com.nttdata.lagm.account.service;

import java.math.BigDecimal;

import com.nttdata.lagm.account.dto.request.BankAccountRequestDto;
import com.nttdata.lagm.account.proxy.CreditProxy;
import com.nttdata.lagm.account.util.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nttdata.lagm.account.dto.response.AvailableBalanceResponseDto;
import com.nttdata.lagm.account.model.BankAccount;
import com.nttdata.lagm.account.proxy.CustomerProxy;
import com.nttdata.lagm.account.repository.BankAccountRepository;
import com.nttdata.lagm.account.repository.BankAccountTypeRepository;
import com.nttdata.lagm.account.util.Constants;
import com.nttdata.lagm.account.util.Util;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BankAccountServiceImpl implements BankAccountService {
	
	private Logger LOGGER = LoggerFactory.getLogger(BankAccountServiceImpl.class);

	@Autowired
	private BankAccountRepository bankAccountRepository;
	
	@Autowired
	private BankAccountTypeRepository bankAccountTypeRepository;
	
	@Autowired
	private CustomerProxy customerProxy;

	@Autowired
	private CreditProxy creditProxy;
	
	@Override
	public Mono<BankAccount> create(BankAccountRequestDto bankAccountRequestDto) {
		BankAccount bankAccount = Converter.convertToBankAccount(bankAccountRequestDto);
		return checkConditions(bankAccount)
			.then(this.bankAccountRepository.save(bankAccount));
	}

	@Override
	public Flux<BankAccount> findAll() {
		LOGGER.info("FindAll BackAccount ");
		return bankAccountRepository.findAll();
	}
	
	@Override
	public Mono<BankAccount> findById(String id) {
		LOGGER.info("FindById: " + id);
		return bankAccountRepository.findById(id);
	}
	
	@Override
	public Mono<BankAccount> delete(String id) {
		LOGGER.info("Deleting BackAccount id: " + id);
		return bankAccountRepository.findById(id).flatMap(bankAccount -> {
			bankAccount.setStatus(false);
			return bankAccountRepository.save(bankAccount);
		});
	}
	
	// Account Number
	@Override
	public Mono<BankAccount> findByAccountNumber(String accountNumber) {
		return bankAccountRepository.findByAccountNumber(accountNumber);
	}
	
	@Override
	public Mono<BankAccount> updateByAccountNumber(BankAccount bankAccount) {
		return bankAccountRepository.findByAccountNumber(bankAccount.getAccountNumber())
			.switchIfEmpty(Mono.error(new Exception("Bank Account not found")))
			.map(b -> {
				b.setAmount(bankAccount.getAmount());
				return b;
			})
			.flatMap(bankAccountRepository::save);
				
	}
	
	@Override
	public Mono<Void> deleteByAccountNumber(String accountNumber) {
		return bankAccountRepository.deleteByAccountNumber(accountNumber);
	}

	@Override
	public Flux<BankAccount> findAllByCustomerIdAndAccountId(String customerId, Integer accountTypeId) {
		return bankAccountRepository.findAll().filter(
			bankAccount -> bankAccount.getCustomer().getId().equals(customerId) && bankAccount.getBankAccountType().getId() == accountTypeId);
	}

	@Override
	public Mono<BankAccount> updateAmount(String id, String strAmount) {
		return bankAccountRepository.findById(id)
			.switchIfEmpty(Mono.error(new Exception("Cuenta bancaria con id: " + id + " no existe")))
			.flatMap(bankAccount -> {
				BigDecimal currentAmount = new BigDecimal(bankAccount.getAmount());
				BigDecimal amount = new BigDecimal(strAmount);
				BigDecimal finalAmount = currentAmount.add(amount);
				bankAccount.setAmount(finalAmount.toString());
				LOGGER.info("current " + currentAmount + " -> final: " + finalAmount);
				return bankAccountRepository.save(bankAccount);
			});
	}

	@Override
	public Mono<AvailableBalanceResponseDto> getAvailableBalance(String accountNumber) {
		return checkAccountNumberExists(accountNumber)
			.then(bankAccountRepository.findByAccountNumber(accountNumber).map(account -> {
				return new AvailableBalanceResponseDto(account.getAccountNumber(), account.getAmount());
			}));
	}

	@Override
	public Flux<BankAccount> findAllByDni(String dni) {
		return customerProxy.findByDni(dni)
			.flatMapMany(customer -> {
				return bankAccountRepository.findAll().filter(bankAccount ->
					bankAccount.getCustomer().getDni().equals(dni));
			});
	}

	@Override
	public Flux<BankAccount> findAllByAccountNumberAndDni(String accountNumber, String dni) {
		return customerProxy.findByDni(dni)
			.flatMapMany(customer -> {
				return bankAccountRepository.findAll().filter(bankAccount ->
					bankAccount.getAccountNumber().equals(accountNumber) &&
					bankAccount.getCustomer().getDni().equals(dni));
			});
	}

	private Mono<Void> checkConditions(BankAccount bankAccount) {
		return checkCustomerExist(bankAccount)
			.mergeWith(checkAccountNumberNotExists(bankAccount.getAccountNumber()))
			.mergeWith(checkMinAmount(bankAccount))
			.mergeWith(checkAccountTypeExist(bankAccount))
			.mergeWith(checkBusinessRuleForCustomerAndAccount(bankAccount.getCustomer().getId(), bankAccount.getBankAccountType().getId()))
			.mergeWith(checkMaintenanceFee(bankAccount))
			.mergeWith(checkMaxLimitMonthlyMovements(bankAccount))
			.mergeWith(checkDayAllowed(bankAccount))
			.mergeWith(checkIfCreditCardIsRequired(bankAccount))
			.then();
	}

	private Mono<Void> checkCustomerExist(BankAccount bankAccount) {
		String customerId = bankAccount.getCustomer().getId();
		return customerProxy.findById(customerId)
			.switchIfEmpty(Mono.error(new Exception("No existe cliente con id: " + customerId)))
			.flatMap(customer -> {
				bankAccount.setCustomer(customer);
				return Mono.empty();
			});

	}

	private Mono<Void> checkAccountNumberNotExists(String accountNumber) {
		return bankAccountRepository.findByAccountNumber(accountNumber)
			.flatMap(bankAccount -> {
				return Mono.error(new Exception("Cuenta bancaria con número de cuenta: " + accountNumber + " ya existe"));
			})
			.then();
	}

	private Mono<Void> checkAccountNumberExists(String accountNumber) {
		return bankAccountRepository.findByAccountNumber(accountNumber)
			.switchIfEmpty(Mono.error(new Exception("Cuenta bancaria con número de cuenta: " + accountNumber + " no existe")))
			.then();
	}

	private Mono<Void> checkMinAmount(BankAccount bankAccount) {
		String strAmount = bankAccount.getAmount();
		BigDecimal amount = new BigDecimal(strAmount);
		if (amount.signum() < 0) {
			return Mono.error(new Exception("Monto mínimo debe ser cero"));
		}
		return Mono.empty();
	}

	private Mono<Void> checkAccountTypeExist(BankAccount bankAccount) {
		Integer bankAccountTypeId = bankAccount.getBankAccountType().getId();
		return bankAccountTypeRepository.findById(bankAccountTypeId)
			.switchIfEmpty(Mono.error(new Exception("No existe tipo de cuenta con id: " + bankAccountTypeId)))
			.flatMap(bankAccountType -> {
				bankAccount.setBankAccountType(bankAccountType);
				return Mono.empty();
			});
	}

	private Mono<Void> checkMaintenanceFee(BankAccount bankAccount) {
		BigDecimal maintenanceFee = new BigDecimal(bankAccount.getMaintenanceFee());
		Integer accountTypeId = bankAccount.getBankAccountType().getId();
		return customerProxy.findById(bankAccount.getCustomer().getId())
			.flatMap(customer -> {
				if (maintenanceFee.signum() < 0) {
					return Mono.error(new Exception("Comisión por mantenimiento no puede ser negativo"));
				} else {
					if ((accountTypeId == Constants.ID_BANK_ACCOUNT_SAVING ||
						accountTypeId == Constants.ID_BANK_ACCOUNT_FIXED_TERM) &&
						maintenanceFee.signum() > 0) {
							return Mono.error(new Exception("La comisión por mantenimiento para una cuenta de tipo " +
								Util.typeOfAccount(accountTypeId) + " debe ser 0 (libre de comisión por mantenimiento)"));
					}

					if (accountTypeId == Constants.ID_BANK_ACCOUNT_CURRENT_ACCOUNT) {
						Integer customerProfileId = customer.getCustomerProfileId();

						if (customerProfileId == Constants.CUSTOMER_PROFILE_REGULAR && maintenanceFee.signum() == 0) {
							return Mono.error(new Exception("La comisión por mantenimiento para una cuenta de tipo " +
								Util.typeOfAccount(accountTypeId) + " debe ser mayor a 0 (posee comisión por mantenimiento)"));
						}

						if (customerProfileId == Constants.CUSTOMER_PROFILE_PYME && maintenanceFee.signum() > 0) {
							return Mono.error(new Exception("La comisión por mantenimiento para una cuenta de tipo " +
								Util.typeOfAccount(accountTypeId) + " para un cliente empresarial PYME debe ser 0"));
						}
					}
				}

				return Mono.empty();
			});
	}

	private Mono<Void> checkMaxLimitMonthlyMovements(BankAccount bankAccount) {
		Integer maxLimitMonthlyMovements = bankAccount.getMaxLimitMonthlyMovements();
		Integer accountTypeId = bankAccount.getBankAccountType().getId();

		if (Constants.ID_BANK_ACCOUNT_SAVING == accountTypeId) {
			if (maxLimitMonthlyMovements == null || maxLimitMonthlyMovements <= 0) {
				return Mono.error(new Exception("El número máximo de movimientos mensuales para la cuenta de tipo " +
					Util.typeOfAccount(accountTypeId) + " debe ser mayor a 0"));
			}
		} else if (Constants.ID_BANK_ACCOUNT_CURRENT_ACCOUNT == accountTypeId) {
			if (maxLimitMonthlyMovements != null) {
				return Mono.error(new Exception("La cuenta de tipo " +
					Util.typeOfAccount(accountTypeId) + " no debe poseer límite de movimientos mensuales"));
			}
		} else if (Constants.ID_BANK_ACCOUNT_FIXED_TERM == accountTypeId) {
			if (maxLimitMonthlyMovements != 1) {
				return Mono.error(new Exception("La cuenta de tipo " +
					Util.typeOfAccount(accountTypeId) + " solo permite un movimiento de retiro o depósito. " +
						"Debe especificar 1 en máximo límite de movimientos mensuales"));
			}
		}

		return Mono.empty();
	}

	private Mono<Void> checkDayAllowed(BankAccount bankAccount) {
		Integer accountTypeId = bankAccount.getBankAccountType().getId();
		Integer dayAllowed = bankAccount.getDayAllowed();

		if ((Constants.ID_BANK_ACCOUNT_SAVING == accountTypeId ||
			Constants.ID_BANK_ACCOUNT_CURRENT_ACCOUNT == accountTypeId) &&
				dayAllowed != null) {
				return Mono.error(new Exception("No debe especificar un día permitido para una cuenta de tipo " +
					Util.typeOfAccount(accountTypeId)));
		} else if (Constants.ID_BANK_ACCOUNT_FIXED_TERM == accountTypeId) {
			if (dayAllowed == null || !(dayAllowed >= 0 && dayAllowed <= 31)) {
				return Mono.error(new Exception("La cuenta de tipo " + Util.typeOfAccount(accountTypeId) +
						" requiere especificar un día válido específico de movimiento del mes"));
			}
		}

		return Mono.empty();
	}

	private Mono<Void> checkIfCreditCardIsRequired(BankAccount bankAccount) {
		Integer accountTypeId = bankAccount.getBankAccountType().getId();
		return this.customerProxy.findById(bankAccount.getCustomer().getId())
			.flatMap(customer -> {
				boolean isVip = 
					accountTypeId == Constants.ID_BANK_ACCOUNT_SAVING &&
					customer.getCustomerTypeId() == Constants.CUSTOMER_TYPE_PERSONAL &&
					customer.getCustomerProfileId() == Constants.CUSTOMER_PROFILE_VIP;

				boolean isPyme = 
					accountTypeId == Constants.ID_BANK_ACCOUNT_CURRENT_ACCOUNT &&
					customer.getCustomerTypeId() == Constants.CUSTOMER_TYPE_BUSINESS &&
					customer.getCustomerProfileId() == Constants.CUSTOMER_PROFILE_PYME;

				if (isVip || isPyme) {
					return creditProxy.findByDni(bankAccount.getCustomer().getDni())
					.switchIfEmpty(Mono.error(new Exception(
						"Se requiere que el cliente tenga al menos una tarjeta de crédito")))
					.then();
				}

				return Mono.empty();
			});
	}

	private Mono<Void> checkBusinessRuleForCustomerAndAccount(String customerId, Integer accountTypeId) {
		return this.customerProxy.findById(customerId)
			.flatMap(customer -> {
				if (Constants.CUSTOMER_TYPE_PERSONAL == customer.getCustomerTypeId()) {
					return this.findAllByCustomerIdAndAccountId(customerId, accountTypeId)
					.flatMap(result -> {
						return Mono.error(new Exception("El cliente " + result.getCustomer().getId() + 
								" es de tipo " + Constants.CUSTOMER_TYPE_PERSONAL_DESCRIPTION + 
								" y ya tiene registrado una cuenta de tipo " +
								Util.typeOfAccount(result.getBankAccountType().getId())));
					})
					.then();
				} else if (Constants.CUSTOMER_TYPE_BUSINESS == customer.getCustomerTypeId() &&
					(accountTypeId == Constants.ID_BANK_ACCOUNT_SAVING ||
					accountTypeId == Constants.ID_BANK_ACCOUNT_FIXED_TERM)) {
					return Mono.error(
						new Exception("El cliente " + customerId + " es de tipo " +
							Constants.CUSTOMER_TYPE_BUSINESS_DESCRIPTION + 
							" y no puede registrar una cuenta de tipo " +
							Util.typeOfAccount(accountTypeId)));
				} else {
					return Mono.empty();
				}
			});
	}
}
