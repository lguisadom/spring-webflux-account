package com.nttdata.lagm.account.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BankAccountDetailResponseDto {
	private String id;
	private String accountNumber;
	private String cci;
	private Long customerId;
	private String amount;
	private BankAccountTypeResponseDto accountType;
	private String maintenanceFee;
	private Integer maxLimitMonthlyMovements;
	private Integer dayAllowed;
}
