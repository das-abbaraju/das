package com.picsauditing.actions;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.ContractorAudit;

public class AuditActionSupport extends PicsActionSupport {
	protected int auditID = 0;
	protected ContractorAudit conAudit;
	@Autowired
	protected ContractorAuditDAO contractorAuditDAO;

	protected void findConAudit() throws Exception {
		getPermissions();
		conAudit = contractorAuditDAO.find(auditID);
		if (conAudit == null)
			throw new Exception("Audit for this " + this.auditID + " not found");
	}

	public int getAuditID() {
		return auditID;
	}

	public void setAuditID(int id) {
		this.auditID = id;
	}

	public ContractorAudit getConAudit() {
		return conAudit;
	}

}
