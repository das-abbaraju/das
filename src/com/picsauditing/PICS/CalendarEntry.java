package com.picsauditing.PICS;

import java.util.Date;

public class CalendarEntry {
	private Date entryDate;
	private int conID;
	private String conName;
	private int auditID;
	private int auditTypeName;
	private String auditorName;
	private String auditLocation;

	public Date getEntryDate() {
		return entryDate;
	}

	public void setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
	}

	public int getConID() {
		return conID;
	}

	public void setConID(int conID) {
		this.conID = conID;
	}

	public String getConName() {
		return conName;
	}

	public void setConName(String conName) {
		this.conName = conName;
	}

	public int getAuditID() {
		return auditID;
	}

	public void setAuditID(int auditID) {
		this.auditID = auditID;
	}

	public int getAuditTypeName() {
		return auditTypeName;
	}

	public void setAuditTypeName(int auditTypeName) {
		this.auditTypeName = auditTypeName;
	}

	public String getAuditorName() {
		return auditorName;
	}

	public void setAuditorName(String auditorName) {
		this.auditorName = auditorName;
	}

	public String getAuditLocation() {
		return auditLocation;
	}

	public void setAuditLocation(String auditLocation) {
		this.auditLocation = auditLocation;
	}

}
