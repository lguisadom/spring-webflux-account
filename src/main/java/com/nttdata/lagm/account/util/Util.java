package com.nttdata.lagm.account.util;

public class Util {
    public static String typeOfAccount(Integer accountTypeId) {
        String description = "";

        switch (accountTypeId) {
            case Constants.ID_BANK_ACCOUNT_SAVING:
                description = Constants.BANK_ACCOUNT_SAVING_DESCRIPTION;
                break;
            case Constants.ID_BANK_ACCOUNT_CURRENT_ACCOUNT:
                description = Constants.BANK_ACCOUNT_CURRENT_ACCOUNT_DESCRIPTION;
                break;
            case Constants.ID_BANK_ACCOUNT_FIXED_TERM:
                description = Constants.BANK_ACCOUNT_FIXED_TERM_DESCRIPTION;
                break;
        }
        return description;
    }
}
