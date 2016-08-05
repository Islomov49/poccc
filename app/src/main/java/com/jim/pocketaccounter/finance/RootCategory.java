package com.jim.pocketaccounter.finance;

import java.util.ArrayList;

public class RootCategory extends Category {
	private int type;
	private ArrayList<SubCategory> subCategories;
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public ArrayList<SubCategory> getSubCategories() {
		return subCategories;
	}
	public void setSubCategories(ArrayList<SubCategory> subCategories) {this.subCategories = subCategories;}
}