package com.nttdata.lagm.account.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.nttdata.lagm.account.model.BankAccount;
import com.nttdata.lagm.account.service.BankAccountService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/api/v1/account")
public class BankAccountController {
	@Autowired
	private BankAccountService bankAccountService;
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	private Mono<BankAccount> create(@RequestBody BankAccount bankAccount) {
		return bankAccountService.create(bankAccount);
	}
	
	@GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	@ResponseStatus(HttpStatus.OK)
	private Flux<BankAccount> findAll() {
		return bankAccountService.findAll();
	}
	
	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	private Mono<BankAccount> findById(@PathVariable Long id) {
		return bankAccountService.findById(id);
	}
	
	@PutMapping
	@ResponseStatus(HttpStatus.OK)
	private Mono<BankAccount> update(@RequestBody BankAccount bankAccount) {
		return bankAccountService.update(bankAccount);
	}
	
	@PutMapping("/{id}/deposit/{amount}")
	@ResponseStatus(HttpStatus.OK)
	private Mono<BankAccount> depositAmount(@PathVariable Long id, @PathVariable String amount) {
		return bankAccountService.depositAmount(id, amount);
	}
	
	@PutMapping("/{id}/withdraw/{amount}")
	@ResponseStatus(HttpStatus.OK)
	private Mono<BankAccount> withdrawAmount(@PathVariable Long id, @PathVariable String amount) {
		return bankAccountService.withdrawAmount(id, amount);
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	private Mono<Void> delete(@PathVariable Long id) {
		return bankAccountService.delete(id);
	}
	
	
	@GetMapping("accountNumber/{accountNumber}")
	@ResponseStatus(HttpStatus.OK)
	private Mono<BankAccount> findByAccountNumber(@PathVariable String accountNumber) {
		return bankAccountService.findByAccountNumber(accountNumber);
	}
	
	/*@PutMapping
	@ResponseStatus(HttpStatus.OK)
	private Mono<BankAccount> updateByAccountNumber(@RequestBody BankAccount bankAccount) {
		return bankAccountService.updateByAccountNumber(bankAccount);
	}*/
}
