package com.picsauditing.actions.audits;

import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;

public class ContractorAuditAction extends AuditActionSupport {
	protected AuditStatus auditStatus;
	
	public ContractorAuditAction(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, AuditCategoryDataDAO catDataDao, AuditDataDAO auditDataDao) {
		super(accountDao, auditDao, catDataDao, auditDataDao);
	}
	
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		this.findConAudit();
		
		if (auditStatus != null && !conAudit.getAuditStatus().equals(auditStatus)) {
			// Save the audit status
			// TODO: do anything else associated with Submitting/Activating an audit
			if (conAudit.getAuditStatus().equals(AuditStatus.Active)) {
				// TODO Recalculate the flag color for this contractor
			}
			conAudit.setAuditStatus(auditStatus);
			auditDao.save(conAudit);
		}
		
		if (this.conAudit.getAuditType().getAuditTypeID() == AuditType.NCMS)
			return "NCMS";

		return SUCCESS;
	}

	public AuditStatus getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(AuditStatus auditStatus) {
		this.auditStatus = auditStatus;
	}

}
