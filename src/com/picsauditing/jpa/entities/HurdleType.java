package com.picsauditing.jpa.entities;

public enum HurdleType {
	None, Absolute, NAICS;

	public boolean isNone() {
		return this.equals(None);
	}
	
	public boolean isAbsolute() {
		return this.equals(Absolute);
	}
	
	public boolean isNaics() {
		return this.equals(NAICS);
	}
}
