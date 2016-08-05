package com.jim.pocketaccounter.report;

import com.jim.pocketaccounter.finance.Currency;
import com.jim.pocketaccounter.finance.RootCategory;
import com.jim.pocketaccounter.finance.SubCategory;

public class IncomeExpanseDayDetails {
    private RootCategory category;
    private SubCategory subCategory;
    private double amount;
    private Currency currency;
    public Currency getCurrency() {return currency;}
    public void setCurrency(Currency currency) {this.currency = currency;}
    public RootCategory getCategory() {return category;}
    public void setCategory(RootCategory category) {this.category = category;}
    public SubCategory getSubCategory() {return subCategory;}
    public void setSubCategory(SubCategory subCategory) {this.subCategory = subCategory;}
    public double getAmount() {return amount;}
    public void setAmount(double amount) {this.amount = amount;}
}
