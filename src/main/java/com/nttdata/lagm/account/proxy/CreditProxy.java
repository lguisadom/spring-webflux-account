package com.nttdata.lagm.account.proxy;

import com.nttdata.lagm.account.model.Credit;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CreditProxy {
	public Flux<Credit> findAll();
	public Mono<Credit> findById(String id);
	public Mono<Credit> findByAccountNumber(String accountNumber);
	public Flux<Credit> findByDni(String dni);
}
