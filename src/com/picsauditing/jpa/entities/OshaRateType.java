package com.picsauditing.jpa.entities;

public enum OshaRateType {
	LwcrAbsolute("Lwcr Absolute"), 
	LwcrNaics("Lwcr Industry Avg"), 
	TrirAbsolute("Trir Absolute"),
	TrirNaics("Trir industry Avg"),
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
