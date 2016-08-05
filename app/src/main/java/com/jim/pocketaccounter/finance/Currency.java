package com.jim.pocketaccounter.finance;

import java.util.ArrayList;

public class Currency {
	private String name, abbr, id;
	private boolean isMain = false;
	private ArrayList<CurrencyCost> costs;
	public Currency(String name) {
		setMain(false);
		setAbbr("");
		setCosts(new ArrayList<CurrencyCost>());
		setName(name);
	}
	public void setMain(boolean isMain) {
		this.isMain = isMain;
	} 
	public boolean getMain() {
		return isMain;
	}
	public void setAbbr(String abbr) {
		this.abbr = abbr;
	}
	public String getAbbr() {
		return this.abbr;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<CurrencyCost> getCosts() {
		return costs;
	}
	public void setCosts(ArrayList<CurrencyCost> costs) {
		this.costs = costs;
	}
}
