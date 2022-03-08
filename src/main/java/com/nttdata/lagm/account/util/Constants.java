package com.nttdata.lagm.account.util;

public class Constants {
	// Types of Banking Movements 
	public static final Integer ID_BANK_PRODUCT_ACCOUNT_DEPOSIT = 1;
	public static final Integer ID_BANK_PRODUCT_ACCOUNT_WITHDRAWAL = 2;
	public static final Integer ID_BANK_PRODUCT_CREDIT_PAYMENT = 3;
	public static final Integer ID_BANK_PRODUCT_CREDIT_CHARGE = 4;
	
	// Types of Bank Product
	public static final Integer ID_BANK_PRODUCT_ACCOUNT = 1;
	public static final Integer ID_BANK_PRODUCT_CREDIT = 2;
	
	// Types of Bank Account
	public static final int ID_BANK_ACCOUNT_SAVING = 1;
	public static final int ID_BANK_ACCOUNT_CURRENT_ACCOUNT = 2;
	public static final int ID_BANK_ACCOUNT_FIXED_TERM = 3;
	
	// Description Bank Account
	public static final String BANK_ACCOUNT_SAVING_DESCRIPTION = "Cuenta de Ahorro";
	public static final String BANK_ACCOUNT_CURRENT_ACCOUNT_DESCRIPTION = "Cuenta corriente";
	public static final String BANK_ACCOUNT_FIXED_TERM_DESCRIPTION = "Plazo Fijo";

	// Description Bank Account
	public static final String PERSONAL_CUSTOMER_DESCRIPTION = "Personal";
	public static final String BUSINESS_CUSTOMER_DESCRIPTION = "Empresarial";
	
	// Types of Customer
	public static final Integer PERSONAL_CUSTOMER = 1;
	public static final Integer BUSINESS_CUSTOMER = 2;
}
