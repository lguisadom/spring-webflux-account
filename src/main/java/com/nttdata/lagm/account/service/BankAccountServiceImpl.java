package com.nttdata.lagm.account.service;

import com.nttdata.lagm.account.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nttdata.lagm.account.model.BankAccount;
import com.nttdata.lagm.account.proxy.CustomerProxy;
import com.nttdata.lagm.account.repository.BankAccountRepository;
import com.nttdata.lagm.account.util.Constants;

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
				.mergeWith(checkBusinessRuleForCustomerAndAccount(bankAccount.getCustomerId(), bankAccount.getTypeId()))
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
