package com.picsauditing.jpa.entities;

public enum YesNo {
	Yes,No;
	
	public boolean isTrue () {
		return this == Yes;
	}

	public static YesNo valueOf(boolean value) {
		if (value)
			return Yes;
		else
			return No;
	}
}
