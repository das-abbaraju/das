package com.picsauditing.jpa.entities;

public enum Locale {
	en("English"),en_CA("English (Canada)"),en_US("English (United States)"),fr("French"),es("Spanish");
	
	public String description;
	
	Locale(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
