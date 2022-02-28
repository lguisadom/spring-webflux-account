package com.nttdata.lagm.account.model;

import java.util.List;

import lombok.Data;


@Data
public abstract class BankProduct {
	private Long id;
	private String accountNumber;
	private String cci;
	private String amount;
	private List<BankingMovement> listBankingMovements;
	private Long customerId; // beneficiary // Long
	private Integer typeId;
	
}
