package com.picsauditing.actions.audits;

public class AuditOperatorDO {
	private int auditOperatorID;
	private int auditTypeID;
	private String auditName;
	private int operatorID;
	private String operatorName;
	private int riskLevel;

	public int getAuditTypeID() {
		return auditTypeID;
	}

	public void setAuditTypeID(int auditTypeID) {
		this.auditTypeID = auditTypeID;
	}

	public String getAuditName() {
		return auditName;
	}

	public void setAuditName(String auditName) {
		this.auditName = auditName;
	}

	public int getOperatorID() {
		return operatorID;
	}

	public void setOperatorID(int operatorID) {
		this.operatorID = operatorID;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public int getRiskLevel() {
		return riskLevel;
	}

	public void setRiskLevel(int riskLevel) {
		this.riskLevel = riskLevel;
	}

	public int getAuditOperatorID() {
		return auditOperatorID;
	}

	public void setAuditOperatorID(int auditOperatorID) {
		this.auditOperatorID = auditOperatorID;
	}

	/**
	 * Unique ID used in HTML. We can't use the auditOperatorID because that may be blank for new records
	 * @return
	 */
	public int getId() {
		return (auditTypeID*100000)+operatorID;
	}
}
