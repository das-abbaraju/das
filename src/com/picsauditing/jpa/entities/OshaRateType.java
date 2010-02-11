package com.picsauditing.jpa.entities;

public enum OshaRateType {
	LWCRAbsolute("Lwcr Absolute"), 
	LWCRNaics("Lwcr Industry Avg"), 
	TRIRAbsolute("Trir Absolute"),
	TRIRNaics("Trir industry Avg"),
	Fatalities("Fatalities"),
	DART("Dart"),
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
