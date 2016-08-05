package com.jim.pocketaccounter.debt;

/**
 * Created by user on 6/8/2016.
 */

public class Recking {
    private String payDate;
    private double amount;
    private String accountId;
    private String id;
    private String comment;

    public Recking(String payDate, double amount, String id, String accountId, String comment) {
        this.payDate = payDate;
        this.amount = amount;
        this.id = id;
        this.accountId = accountId;
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Recking() {
    }

    public String getPayDate() {
        return payDate;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
