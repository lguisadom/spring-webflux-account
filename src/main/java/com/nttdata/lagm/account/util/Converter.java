package com.nttdata.lagm.account.util;

import com.nttdata.lagm.account.dto.request.BankAccountRequestDto;
import com.nttdata.lagm.account.model.BankAccount;
import com.nttdata.lagm.account.model.Customer;

public class Converter {
	public static BankAccount converToToBankAccount(BankAccountRequestDto bankAccountRequestDto) {
		BankAccount bankAccount = new BankAccount();
		bankAccount.setAccountNumber(bankAccountRequestDto.getAccountNumber());
		bankAccount.setCci(bankAccountRequestDto.getCci());
		Customer customer = new Customer();
		customer.setId(bankAccountRequestDto.getCustomerId());
		bankAccount.setCustomer(customer);
		bankAccount.setAmount(bankAccountRequestDto.getAmount());
		bankAccount.setTypeId(bankAccountRequestDto.getTypeId());
		bankAccount.setMaintenanceFee(bankAccountRequestDto.getMaintenanceFee());
		bankAccount.setMaxLimitMonthlyMovements(bankAccountRequestDto.getMaxLimitMonthlyMovements());
		bankAccount.setDayAllowed(bankAccountRequestDto.getDayAllowed());
		return bankAccount;
	}
}
