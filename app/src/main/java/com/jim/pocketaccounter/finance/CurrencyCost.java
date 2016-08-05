package com.jim.pocketaccounter.finance;

import java.util.Calendar;

public class CurrencyCost {
	private double cost;
	private Calendar day;
	public double getCost() {
		return cost;
	}
	public void setCost(double cost) {
		this.cost = cost;
	}
	public Calendar getDay() {
		return day;
	}
	public void setDay(Calendar day) {
		this.day = (Calendar) day.clone();
	}
}
