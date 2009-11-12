package com.picsauditing.jpa.entities;

public enum Country {
	USA("US"), Canada("CA");

	private String iso;

	Country(String iso) {
		this.iso = iso;
	}
}
