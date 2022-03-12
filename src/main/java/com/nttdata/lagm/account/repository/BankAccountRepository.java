package com.nttdata.lagm.account.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.nttdata.lagm.account.model.BankAccount;

import reactor.core.publisher.Mono;

@Repository
public interface BankAccountRepository extends ReactiveMongoRepository<BankAccount, String>{
	Mono<BankAccount> findByAccountNumber(String accountNumber);
	Mono<Void> deleteByAccountNumber(String accountNumber);
}
