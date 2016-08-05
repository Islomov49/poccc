package com.jim.pocketaccounter.finance;

public class SubCategory extends Category {
	private String parentId;
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public void setParent(String parentId) {
		this.parentId = parentId;
	}
}
