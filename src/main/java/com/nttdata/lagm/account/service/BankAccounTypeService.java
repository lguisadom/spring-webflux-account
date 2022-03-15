package com.nttdata.lagm.account.service;

import com.nttdata.lagm.account.dto.request.BankAccountTypeRequestDto;
import com.nttdata.lagm.account.model.BankAccountType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BankAccounTypeService {
    Mono<BankAccountType> create(BankAccountTypeRequestDto bankAccountTypeRequestDto);
    Flux<BankAccountType> findAll();
    Mono<BankAccountType> findById(Integer id);
    Mono<BankAccountType> update(BankAccountTypeRequestDto bankAccountTypeRequestDto);
    Mono<Void> delete(Integer id);
}
