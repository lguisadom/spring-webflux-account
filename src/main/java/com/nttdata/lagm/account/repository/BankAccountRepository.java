package com.nttdata.lagm.account.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.nttdata.lagm.account.model.BankAccount;

@Repository
public interface BankAccountRepository extends ReactiveMongoRepository<BankAccount, Long>{

}
