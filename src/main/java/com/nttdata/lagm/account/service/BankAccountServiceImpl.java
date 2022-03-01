package com.nttdata.lagm.account.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nttdata.lagm.account.model.BankAccount;
import com.nttdata.lagm.account.proxy.CustomerProxy;
import com.nttdata.lagm.account.repository.BankAccountRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BankAccountServiceImpl implements BankAccountService {
	
	private Logger LOGGER = LoggerFactory.getLogger(BankAccountServiceImpl.class);

	@Autowired
	private BankAccountRepository bankAccountRepository;
	
	@Autowired
	private CustomerProxy customerProxy;
	
	@Override
	public void create(BankAccount bankAccount) {
		// Mono<Customer> monoCustomer = customerProxy.findById(bankAccount.getCustomerId());
		LOGGER.info("Saving BackAccount: " + bankAccount.toString());
		bankAccountRepository.save(bankAccount).subscribe();
	}

	@Override
	public Flux<BankAccount> findAll() {
		LOGGER.info("FindAll BackAccount ");
		return bankAccountRepository.findAll();
	}
	
	@Override
	public Mono<BankAccount> findById(Long id) {
		LOGGER.info("FindById: " + id);
		return bankAccountRepository.findById(id);
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
}
