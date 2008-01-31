package com.picsauditing.access;

public class MenuItem {
	private String url;
	private String prompt;
	/**
	 * Provided for backwards compatibility with Admins and Operators
	 * @deprecated
	 */
	private int inGroup;
	private OpPerms permission;
	private OpType permType = OpType.View;
	
	public MenuItem(String url, String prompt, OpPerms perm) {
		this.url = url;
		this.prompt = prompt;
		this.permission = perm;
	}
	/**
	 * @param url
	 * @param prompt
	 * @param inGroup
	 */
	public MenuItem(String url, String prompt, int inGroup) {
		this.url = url;
		this.prompt = prompt;
		this.inGroup = inGroup;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getPrompt() {
		return prompt;
	}
	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}
	public OpPerms getPermission() {
		return permission;
	}
	public void setPermission(OpPerms permission) {
		this.permission = permission;
	}
	public OpType getPermType() {
		return permType;
	}
	public void setPermType(OpType permType) {
		this.permType = permType;
	}
	public int getInGroup() {
		return inGroup;
	}
	public void setInGroup(int inGroup) {
		this.inGroup = inGroup;
	}
}
