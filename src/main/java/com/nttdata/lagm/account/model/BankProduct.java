package com.nttdata.lagm.account.model;

import lombok.Data;

@Data
public abstract class BankProduct {
	private Long id;
	private String accountNumber;
	private String cci;
	// private List<BankingMovement> listBankingMovements;
	private Long customerId; // beneficiary	
	private String amount;
}