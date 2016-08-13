package com.jim.pocketaccounter.finance;

public class Account {
	private String name, id;
	private int icon;
	private double amount;
	private Currency currency;
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
}