package com.nttdata.lagm.account.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nttdata.lagm.account.model.BankAccount;
import com.nttdata.lagm.account.proxy.CustomerProxy;
import com.nttdata.lagm.account.repository.BankAccountRepository;
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
	private CustomerProxy customerProxy;
	
	private Mono<Void> checkCustomerExist(Long id) {
		return customerProxy.findById(id)
				.switchIfEmpty(Mono.error(new Exception("No existe cliente con id: " + id)))
				.then();

	}
	
	private Mono<Void> checkBankAccountNotExists(Long id) {
		return bankAccountRepository.findById(id)
				.flatMap(bankAccount -> {
					return Mono.error(new Exception("Cuenta bancaria con id: " + id + " ya existe"));
				})
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

	private Mono<Void> checkAccountTypeId(Integer accountTypeId) {
		if (accountTypeId != Constants.ID_BANK_ACCOUNT_SAVING &&
			accountTypeId != Constants.ID_BANK_ACCOUNT_CURRENT_ACCOUNT &&
			accountTypeId != Constants.ID_BANK_ACCOUNT_FIXED_TERM) {
			return Mono.error(new Exception("Debe ingresar un tipo de cuenta válido. " +
				String.format("%d:%s | %d:%s | %d:%s",
						Constants.ID_BANK_ACCOUNT_SAVING, Util.typeOfAccount(Constants.ID_BANK_ACCOUNT_SAVING),
						Constants.ID_BANK_ACCOUNT_CURRENT_ACCOUNT, Util.typeOfAccount(Constants.ID_BANK_ACCOUNT_CURRENT_ACCOUNT),
						Constants.ID_BANK_ACCOUNT_FIXED_TERM, Util.typeOfAccount(Constants.ID_BANK_ACCOUNT_FIXED_TERM))));
		}
		return Mono.empty();
	}

	private Mono<Void> checkMaintenanceFee(BankAccount bankAccount) {
		BigDecimal maintenanceFee = new BigDecimal(bankAccount.getMaintenanceFee());
		Integer accountTypeId = bankAccount.getTypeId();

		if (maintenanceFee.signum() < 0) {
			return Mono.error(new Exception("Comisión por mantenimiento no puede ser negativo"));
		} else {
			if ((accountTypeId == Constants.ID_BANK_ACCOUNT_SAVING ||
				accountTypeId == Constants.ID_BANK_ACCOUNT_FIXED_TERM) &&
				maintenanceFee.signum() > 0) {
				return Mono.error(new Exception("La comisión por mantenimiento para una cuenta de tipo " +
						Util.typeOfAccount(accountTypeId) + " debe ser 0 (libre de comisión por mantenimiento)"));
			}

			if (accountTypeId == Constants.ID_BANK_ACCOUNT_CURRENT_ACCOUNT && maintenanceFee.signum() == 0) {
				return Mono.error(new Exception("La comisión por mantenimiento para una cuenta de tipo " +
						Util.typeOfAccount(accountTypeId) + " debe ser mayor a 0 (posee comisión por mantenimiento)"));
			}
		}

		return Mono.empty();
	}

	private Mono<Void> checkMaxLimitMonthlyMovements(BankAccount bankAccount) {
		Integer maxLimitMonthlyMovements = bankAccount.getMaxLimitMonthlyMovements();
		Integer accountTypeId = bankAccount.getTypeId();

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
		Integer accountTypeId = bankAccount.getTypeId();
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

	private Mono<Void> checkBusinessRuleForCustomerAndAccount(Long customerId, Integer accountTypeId) {
		return this.customerProxy.findById(customerId)
				.flatMap(customer -> {
					if (Constants.PERSONAL_CUSTOMER == customer.getCustomerTypeId()) {
						return this.findAllByCustomerIdAndAccountId(customerId, accountTypeId)
								.flatMap(result -> {
									return Mono.error(
											new Exception("El cliente " + result.getCustomerId() + " es de tipo " +
													Constants.PERSONAL_CUSTOMER_DESCRIPTION +
													" y ya tiene registrado una cuenta de tipo " +
													Util.typeOfAccount(result.getTypeId())));
								})
								.then();
					} else if (Constants.BUSINESS_CUSTOMER == customer.getCustomerTypeId() &&
							(accountTypeId == Constants.ID_BANK_ACCOUNT_SAVING ||
							accountTypeId == Constants.ID_BANK_ACCOUNT_FIXED_TERM)) {
						return Mono.error(
								new Exception("El cliente " + customerId + " es de tipo " +
										Constants.BUSINESS_CUSTOMER_DESCRIPTION +
										" y no puede registrar una cuenta de tipo " +
										Util.typeOfAccount(accountTypeId)));
					} else {
						return Mono.empty();
					}
				});
	}
	
	@Override
	public Mono<BankAccount> create(BankAccount bankAccount) {
		return checkCustomerExist(bankAccount.getCustomerId())
				.mergeWith(checkBankAccountNotExists(bankAccount.getId()))
				.mergeWith(checkMinAmount(bankAccount))
				.mergeWith(checkAccountTypeId(bankAccount.getTypeId()))
				.mergeWith(checkBusinessRuleForCustomerAndAccount(bankAccount.getCustomerId(), bankAccount.getTypeId()))
				.mergeWith(checkMaintenanceFee(bankAccount))
				.mergeWith(checkMaxLimitMonthlyMovements(bankAccount))
				.mergeWith(checkDayAllowed(bankAccount))
				.then(this.bankAccountRepository.save(bankAccount));
	}

	@Override
	public Flux<BankAccount> findAll() {
		LOGGER.info("FindAll BackAccount ");
		return bankAccountRepository.findAll();
	}
	
	@Override
	public Mono<BankAccount> findById(Long id) {
		LOGGER.info("FindById: " + id);
		return bankAccountRepository.findById(id)
				.switchIfEmpty(Mono.error(new Exception("There is no account with id: " + id)));
	}

	@Override
	public Mono<BankAccount> update(BankAccount bankAccount) {
		LOGGER.info("Updating BackAccount: " + bankAccount.toString());
		//bankAccountRepository.findByAccountNumber(bankAccount.getAccountNumber();
		// si encuentra -> save
		// sino -> lanzar error
		return bankAccountRepository.save(bankAccount);
	}
	
	@Override
	public Mono<Void> delete(Long id) {
		LOGGER.info("Deleting BackAccount id: " + id);
		return bankAccountRepository.deleteById(id);
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
	public Flux<BankAccount> findAllByCustomerIdAndAccountId(Long customerId, Integer accountTypeId) {
		return bankAccountRepository.findAll().filter(
				bankAccount -> bankAccount.getCustomerId() == customerId && bankAccount.getTypeId() == accountTypeId);
	}
}
