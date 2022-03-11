package com.nttdata.lagm.account.builder;

import com.nttdata.lagm.account.dto.request.BankAccountRequestDto;
import com.nttdata.lagm.account.model.BankAccount;

public class ConvertBuilderAccount {
	public BankAccount convertDtoToEntity(BankAccountRequestDto bankAccountRequest) {
		return BankAccount.builder()
				.accountNumber(bankAccountRequest.getAccountNumber())
				.cci(bankAccountRequest.getCci())
				.amount(bankAccountRequest.getAmount())
				.customerId(bankAccountRequest.getCustomerId())
				.typeId(bankAccountRequest.getTypeId())
				.maintenanceFee(bankAccountRequest.getMaintenanceFee())
				.maxLimitMonthlyMovements(bankAccountRequest.getMaxLimitMonthlyMovements())
				.build();
	}
}
