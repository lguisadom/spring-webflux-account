package com.nttdata.lagm.account.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.nttdata.lagm.account.dto.response.AvailableBalanceResponseDto;
import com.nttdata.lagm.account.model.BankAccount;
import com.nttdata.lagm.account.service.BankAccountService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/api/v1/account")
public class BankAccountController {

	private Logger LOGGER = LoggerFactory.getLogger(BankAccountController.class);

	@Autowired
	private BankAccountService bankAccountService;
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	private Mono<BankAccount> create(@RequestBody BankAccount bankAccount) {
		LOGGER.info("Create: " + bankAccount);
		return bankAccountService.create(bankAccount);
	}
	
	@GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	@ResponseStatus(HttpStatus.OK)
	private Flux<BankAccount> findAll() {
		LOGGER.info("findAll: ");
		return bankAccountService.findAll();
	}
	
	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	private Mono<BankAccount> findById(@PathVariable Long id) {
		LOGGER.info("findById: " + id);
		return bankAccountService.findById(id);
	}
	
	@PutMapping
	@ResponseStatus(HttpStatus.OK)
	private Mono<BankAccount> update(@RequestBody BankAccount bankAccount) {
		LOGGER.info("Update: " + bankAccount);
		return bankAccountService.update(bankAccount);
	}
	
	@PutMapping("/update/{id}/amount/{amount}")
	@ResponseStatus(HttpStatus.OK)
	private Mono<BankAccount> updateAmount(@PathVariable Long id, @PathVariable String amount) {
		LOGGER.info("UpdateAmount: " + id + ", amount: " + amount);
		return bankAccountService.updateAmount(id, amount);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	private Mono<Void> delete(@PathVariable Long id) {
		LOGGER.info("Delete: " + id);
		return bankAccountService.delete(id);
	}
	
	
	@GetMapping("accountNumber/{accountNumber}")
	@ResponseStatus(HttpStatus.OK)
	private Mono<BankAccount> findByAccountNumber(@PathVariable String accountNumber) {
		LOGGER.info("FindByAccountNumber: " + accountNumber);
		return bankAccountService.findByAccountNumber(accountNumber);
	}
	
	@GetMapping("/balance/{accountNumber}")
	@ResponseStatus(HttpStatus.OK)
	private Mono<AvailableBalanceResponseDto> getAvailableBalance(@PathVariable("accountNumber") String accountNumber) {
		LOGGER.info("GetAvailableBalance: " + accountNumber);
		return bankAccountService.getAvailableBalance(accountNumber);
	}
}
