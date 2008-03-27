package com.picsauditing.jpa.entities;

public enum AuditStatus {
	New,
	Submitted,
	Verified,
	Exempt,
	Expired;
	private String meaning;
	AuditStatus() {
	}
	AuditStatus(String meaning) {
		//AuditStatus.Verified
		this.meaning = meaning;
	}
	public String getMeaning(){
		return meaning;
	}
}
