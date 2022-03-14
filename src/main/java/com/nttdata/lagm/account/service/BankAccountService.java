package com.nttdata.lagm.account.service;

import com.nttdata.lagm.account.dto.request.BankAccountRequestDto;
import com.nttdata.lagm.account.dto.response.AvailableBalanceResponseDto;
import com.nttdata.lagm.account.model.BankAccount;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BankAccountService {
	public Mono<BankAccount> create(BankAccountRequestDto bankAccountRequestDto);
	public Flux<BankAccount> findAll();
	public Mono<BankAccount> findById(String id);
	public Mono<BankAccount> delete(String id);
	public Mono<BankAccount> findByAccountNumber(String accountNumber);
	public Mono<BankAccount> updateByAccountNumber(BankAccount bankAccount);
	public Mono<Void> deleteByAccountNumber(String accountNumber);
	public Flux<BankAccount> findAllByCustomerIdAndAccountId(String id, Integer accountTypeId);
	public Mono<BankAccount> updateAmount(String id, String amount);
	public Mono<AvailableBalanceResponseDto> getAvailableBalance(String accountNumber);
//	public Mono<BankAccountDetailResponseDto> getDetailByAccountNumber(String accountNumber);
}
