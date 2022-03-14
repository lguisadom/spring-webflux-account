package com.nttdata.lagm.account.dto.request;

import lombok.Data;

@Data
public class BankAccountRequestDto {
	private String accountNumber;
	private String cci;
	private String amount;
	private String customerId;
	private Integer typeId;
	private String maintenanceFee;
	private Integer maxLimitMonthlyMovements;
	private Integer dayAllowed;
}
