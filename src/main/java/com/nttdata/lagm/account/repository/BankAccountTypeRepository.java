package com.nttdata.lagm.account.repository;

import com.nttdata.lagm.account.model.BankAccountType;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankAccountTypeRepository extends ReactiveMongoRepository<BankAccountType, Integer> {
}
