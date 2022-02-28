package com.nttdata.lagm.account.model;

import lombok.Data;

@Data
public class BankingMovement {
	private Integer id;
	private BankProduct bankProduct;
	private Integer bankingMovementType;
	private String date;
	private String amount;
}
