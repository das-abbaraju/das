package com.picsauditing.jpa.entities;

public enum Locale {
	en("English"), en_CA("English (Canada)"), en_US("English (United States)"), fr("French"), es("Spanish");

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

	public static Locale valueOf(java.util.Locale l) {
		Locale locale = en;

		try {
			locale = valueOf(l.toString());
		} catch (Exception e) {
			try {
				locale = valueOf(l.getLanguage());
			} catch (Exception e1) {
			}
		}

		return locale;
	}
}
