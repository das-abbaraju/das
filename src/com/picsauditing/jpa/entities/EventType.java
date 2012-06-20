package com.picsauditing.jpa.entities;

public enum EventType {
	All("eventAll"), Locations("eventLocations"), Trades("eventTrades");

	private String uniqueCode;

	EventType(String uniqueCode) {
		this.uniqueCode = uniqueCode;
	}

	public boolean isLocationsEvent() {
		return this.equals(Locations);
	}

	public boolean isTradesEvent() {
		return this.equals(Trades);
	}

	public String getUniqueCode() {
		return uniqueCode;
	}

	public void setUniqueCode(String uniqueCode) {
		this.uniqueCode = uniqueCode;
	}
}
