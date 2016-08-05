package com.jim.pocketaccounter.finance;

import java.util.ArrayList;

public class AccountManagementObject {
    private Account account;
    private ArrayList<CurrencyAmount> curAmounts;
    public void setAccount(Account account) {
        this.account = account;
    }
    public Account getAccount() {
        return account;
    }
    public void setCurAmounts(ArrayList<CurrencyAmount> curAmounts) {
        this.curAmounts = curAmounts;
    }
    public ArrayList<CurrencyAmount> getCurAmounts() {
        return curAmounts;
    }
}
