package com.picsauditing.importpqf;

public enum ImportComparison {
	None, Same, Or;
	
	public boolean isComparison() {
		return this != None;
	}
	
	public boolean isNoneComparison() {
		return this == None;
	}
	
	public boolean isSameComparison() {
		return this == Same;
	}

	public boolean isOrComparison() {
		return this == Or;
	}
}
