package com.picsauditing.selenium;

public class SeleniumTestingAccount {
	private String name, id, type;
	
	protected SeleniumTestingAccount (String name, String id, String type) {
		this.name = name;
		this.id = id;
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public String getId() {
		return id;
	}
	
	public String getType() {
		return type;
	}	
	
	@Override
	public String toString() {
		return name + " " + id + " " + type;
	}

}
