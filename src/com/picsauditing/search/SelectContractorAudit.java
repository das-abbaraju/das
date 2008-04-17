package com.picsauditing.search;

public class SelectContractorAudit extends SelectAccount {

	public SelectContractorAudit() {
		super();
		this.setType(Type.Contractor);
		
		this.addJoin("JOIN contractor_audit ca ON ca.conID = a.id");
		this.addJoin("JOIN audit_type at ON ca.auditTypeID = at.auditTypeID");

		this.addField("ca.auditID");
		this.addField("ca.auditTypeID");
		this.addField("at.auditName");
		// use a.id so it will be compatible with SelectAccount
		this.addField("ca.auditStatus");

	}

	public void setAuditTypeID(int auditTypeID) {
		this.addWhere("ca.auditTypeID="+auditTypeID);
	}
}
