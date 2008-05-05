package com.picsauditing.access;

import java.util.ArrayList;

public class Menu {
	private String prompt;
	private ArrayList<MenuItem> items = new ArrayList<MenuItem>();
	private ArrayList<Menu> subMenus = new ArrayList<Menu>();
	private Permissions permissions;
	
	/**
	 * Only return items for which this person has permission to see
	 * @return
	 */
	public ArrayList<MenuItem> getValidItems() throws NoRightsException {
		ArrayList<MenuItem> validItems = new ArrayList<MenuItem>();
		if (permissions == null) return validItems;
		
		for(MenuItem item : items)
			if (item.canSee(permissions))
				validItems.add(item);
		return validItems;
	}
	////////////////////////
	// Getters and Setters
	public String getPrompt() {
		return prompt;
	}
	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	public ArrayList<MenuItem> getItems() {
		return items;
	}
	public void setItems(ArrayList<MenuItem> items) {
		this.items = items;
	}
	public ArrayList<Menu> getSubMenus() {
		return subMenus;
	}
	public void setSubMenus(ArrayList<Menu> subMenus) {
		this.subMenus = subMenus;
	}
	
	private boolean addItem(String url, String prompt, OpPerms perm) {
		return this.items.add(new MenuItem(url, prompt, perm));
	}
	private boolean addItem(String url, String prompt, int inGroup) {
		return this.items.add(new MenuItem(url, prompt, inGroup));
	}
	private boolean addItem(String url, String prompt) {
		return this.items.add(new MenuItem(url, prompt));
	}
	
	public Permissions getPermissions() {
		return permissions;
	}
	public void setPermissions(Permissions permissions) {
		this.permissions = permissions;
	}
}
