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

	public static boolean toBoolean(YesNo yesNo) {
		if (yesNo == null) {
			return false;
		}

		if (yesNo.equals(Yes)) {
			return true;
		}

		return false;
	}
}
