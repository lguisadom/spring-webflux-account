package com.nttdata.lagm.account.service;

import com.nttdata.lagm.account.model.BankAccount;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BankAccountService {
	public void create(BankAccount bankAccount);
	public Flux<BankAccount> findAll();
	public Mono<BankAccount> findById(Long id);
	public Mono<BankAccount> update(BankAccount bankAccount);
	public Mono<Void> delete(Long id);
	public Mono<BankAccount> findByAccountNumber(String accountNumber);
	public Mono<BankAccount> updateByAccountNumber(BankAccount bankAccount);
	public Mono<Void> deleteByAccountNumber(String accountNumber);
}
