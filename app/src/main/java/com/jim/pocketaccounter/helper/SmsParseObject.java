package com.jim.pocketaccounter.helper;

import com.jim.pocketaccounter.finance.Account;
import com.jim.pocketaccounter.finance.Currency;

public class SmsParseObject {
    private String number= "",
            incomeWords = "",
            expenseWords = "",
            amountWords = "";
    private Account account;
    private Currency currency;
    private int type;
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getIncomeWords() {
        return incomeWords;
    }

    public void setIncomeWords(String incomeWords) {
        this.incomeWords = incomeWords;
    }

    public String getExpenseWords() {
        return expenseWords;
    }

    public void setExpenseWords(String expenseWords) {
        this.expenseWords = expenseWords;
    }

    public String getAmountWords() {
        return amountWords;
    }

    public void setAmountWords(String amountWords) {
        this.amountWords = amountWords;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
