package com.nttdata.lagm.account.model;

import lombok.Data;

@Data
public abstract class BankProduct {
	private Integer id;
	private String accountNumber;
	private String cci;
	private String amount;
	// private List<BankingMovement> listBankingMovements;
	private Long customerId; // beneficiary	
}