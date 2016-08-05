package com.jim.pocketaccounter.finance;

public abstract class Category {
	private String name, id, icon;
	public String getName() {return name;}
	public String getIcon() {return icon;}
	public void setIcon(String icon) {this.icon = icon;}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
}