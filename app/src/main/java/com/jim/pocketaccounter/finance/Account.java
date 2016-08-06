package com.jim.pocketaccounter.finance;

import java.util.Calendar;

public class Account {
	private String name, id;
	private int icon;
	private double amount;
	private Currency currency;
	private boolean limited;
	private double limitSum;
	private Calendar calendar;
	public Calendar getCalendar() {return calendar;}
	public void setCalendar(Calendar calendar) {this.calendar = calendar;}
	public double getAmount() {return amount;}
	public void setAmount(double amount) {this.amount = amount;}
	public Currency getCurrency() {return currency;}
	public void setCurrency(Currency currency) {this.currency = currency;}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getIcon() {
		return icon;
	}
	public void setIcon(int icon) {
		this.icon = icon;
	}
	public boolean isLimited() {return limited;}
	public void setLimited(boolean limited) {this.limited = limited;}
	public double getLimitSum() {return limitSum;}
	public void setLimitSum(double limitSum) {this.limitSum = limitSum;}
}