package com.jim.pocketaccounter.report;

import com.jim.pocketaccounter.finance.Currency;
import com.jim.pocketaccounter.finance.RootCategory;
import com.jim.pocketaccounter.finance.SubCategory;

import java.util.Calendar;

/**
 * Created by ismoi on 6/17/2016.
 */

public class AccountDataRow {
    private int type;
    private Calendar date;
    private RootCategory category;
    private SubCategory subCategory;
    private Currency currency;
    private double amount;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public RootCategory getCategory() {
        return category;
    }

    public void setCategory(RootCategory category) {
        this.category = category;
    }

    public SubCategory getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(SubCategory subCategory) {
        this.subCategory = subCategory;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
