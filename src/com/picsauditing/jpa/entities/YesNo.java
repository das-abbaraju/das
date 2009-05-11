package com.picsauditing.jpa.entities;

public enum YesNo {
	Yes,No;
	
	public boolean isTrue () {
		return this == Yes;
	}
}
