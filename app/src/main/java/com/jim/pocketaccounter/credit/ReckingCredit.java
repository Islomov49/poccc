package com.jim.pocketaccounter.credit;

/**
 * Created by DEV on 11.06.2016.
 */

public class ReckingCredit {
    private long payDate;
    private double amount;
    private String accountId;
    private long myCredit_id;
    private String comment;

    public ReckingCredit(long payDate, double amount, String accountId, long myCredit_id, String comment) {
        this.payDate = payDate;
        this.amount = amount;
        this.accountId = accountId;
        this.myCredit_id = myCredit_id;
        this.comment = comment;
    }

    public long getPayDate() {
        return payDate;
    }

    public void setPayDate(long payDate) {
        this.payDate = payDate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public long getMyCredit_id() {
        return myCredit_id;
    }

    public void setMyCredit_id(long myCredit_id) {
        this.myCredit_id = myCredit_id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
