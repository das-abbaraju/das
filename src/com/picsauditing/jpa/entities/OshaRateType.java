package com.picsauditing.jpa.entities;

public enum OshaRateType {
	LwcrAbsolute("LWCR Absolute"), 
	LwcrNaics("LWCR Industry Avg"), 
	TrirAbsolute("TRIR Absolute"),
	TrirNaics("TRIR industry Avg"),
	Fatalities("Fatalities"),
	Dart("Dart"),
	SeverityRate("Severity Rate"),
	Cad7("Cad7"),
	Neer("Neer");
	
	private String description;
	private OshaRateType(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
}
