package com.picsauditing.jpa.entities;

public enum OshaRateType {
	LwcrAbsolute("LWCR"),
	LwcrNaics("LWCR Industry Avg"),
	TrirAbsolute("TRIR"),
	TrirNaics("TRIR Industry Avg"),
	Fatalities("Fatalities"),
	Dart("DART"),
	SeverityRate("Severity Rate"),
	Cad7("CAD7"),
	Neer("NEER");

	private String description;

	private OshaRateType(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
