package com.jim.pocketaccounter.finance;
import com.jim.pocketaccounter.photocalc.PhotoDetails;

import java.util.ArrayList;
import java.util.Calendar;
public class FinanceRecord {
    private Calendar date;
    private double amount = 0.0;
    private RootCategory category = null;
    private SubCategory subCategory = null;
    private Account account = null;
    private Currency currency = null;
    private String recordId;
    private ArrayList<PhotoDetails> allTickets=null;
    private String comment;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public ArrayList<PhotoDetails> getAllTickets() {
        return allTickets;
    }
    public void setAllTickets(ArrayList<PhotoDetails> allTickets) {
        this.allTickets = allTickets;
    }
    public String getRecordId() {return recordId;}
    public void setRecordId(String recordId) {this.recordId = recordId;}
    public FinanceRecord() {}
    public void setDate(Calendar date) {
        this.date = (Calendar) date.clone();
    }
    public Calendar getDate() {
        return date;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
    public double getAmount() {
        return amount;
    }
    public void setCategory(RootCategory category) {
        this.category = category;
    }
    public RootCategory getCategory() {
        return category;
    }
    public void setSubCategory(SubCategory subCategory) {
        this.subCategory = subCategory;
    }
    public SubCategory getSubCategory() {
        return subCategory;
    }
    public void setAccount(Account account) {
        this.account = account;
    }
    public Account getAccount() {
        return account;
    }
    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
    public Currency getCurrency() {
        return currency;
    }
}
