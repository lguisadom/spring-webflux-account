package com.nttdata.lagm.account.dto.request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class BankAccountRequestDto {
	@NotEmpty(message = "You must enter the account number")
	@Size(min = 14, max = 14, message = "The length of the account number must be 14")
	private String accountNumber;
	
	@NotEmpty(message = "You must enter the cci")
	@Size(min = 20, max = 20, message = "The length of the account number must be 20")
	private String cci;
	
	@NotNull(message = "You must enter the customer id")
	private Long customerId; // beneficiary
	
	@NotEmpty(message = "You must enter the amount")
	private String amount;
	
	@NotNull(message = "You must enter the type of account (1:saving, 2:current, 3:fixed term)")
	private Integer typeId; // 1: saving | 2: current | 3: fixed term
	
	private String maintenanceFee;
	private Integer maxLimitMonthlyMovements;
	private Integer dayAllowed;
}
