package com.picsauditing.jpa.entities;

public enum AccountLevel {
	BidOnly, ListOnly, Full;

	public boolean isBidOnly() {
		return this.equals(BidOnly);
	}

	public boolean isListOnly() {
		return this.equals(ListOnly);
	}

	public boolean isFull() {
		return this.equals(Full);
	}
}
