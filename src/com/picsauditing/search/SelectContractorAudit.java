package com.picsauditing.search;

import com.picsauditing.search.SelectAccount.Type;

public class SelectContractorAudit extends SelectSQL {
	private int auditTypeID;

	public SelectContractorAudit() {
		super();
		this.setFromTable("contractor_audit ca");
		this.addField("ca.auditID");
		this.addField("ca.auditTypeID");
		this.addField("ca.conID");
		this.addField("ca.auditStatus");

		this.addJoin("JOIN accounts a ON a.id = ca.conID");
		this.addField("a.name");
	}

	public int getAuditTypeID() {
		return auditTypeID;
	}

	public void setAuditTypeID(int auditTypeID) {
		this.auditTypeID = auditTypeID;
		this.addWhere("ca.auditTypeID="+auditTypeID);
	}
	
}
