package com.nttdata.lagm.account.proxy;

import com.nttdata.lagm.account.model.Customer;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerProxy {
	public Flux<Customer> findAll();
	public Mono<Customer> findById(String id);
	Mono<Customer> findByDni(String id);
}
