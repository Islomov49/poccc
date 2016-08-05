package com.jim.pocketaccounter.helper;

public class LeftMenuItem {
	private String titleName="";
	private int iconId=0, fragment=0;
	private boolean isGroup=true;
	public LeftMenuItem(String titleName, int icon) {
		this.titleName = titleName;
		this.setIconId(icon);
		this.setFragment(fragment);
	}
	public void setTitleName(String titleName) {
		this.titleName = titleName;
	}
	public String getTitleName() {
		return titleName;
	}
	public int getFragment() {
		return fragment;
	}
	public void setFragment(int fragment) {
		this.fragment = fragment;
	}
	public int getIconId() {
		return iconId;
	}
	public void setIconId(int iconId) {
		this.iconId = iconId;
	}
	public boolean isGroup() {
		return isGroup;
	}
	public void setGroup(boolean isGroup) {
		this.isGroup = isGroup;
	}
}
