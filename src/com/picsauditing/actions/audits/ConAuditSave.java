package com.picsauditing.actions.audits;

import java.util.Date;

import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditStatus;

/**
 * Class used to edit a ContractorAudit record with virtually no restrictions
 * 
 * @author Trevor
 * 
 */
public class ConAuditSave extends AuditActionSupport {

	protected ContractorAuditDAO contractorAuditDAO;
	protected String auditStatus;
	public ConAuditSave(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, AuditCategoryDataDAO catDataDao,
			AuditDataDAO auditDataDao, ContractorAuditDAO contractorAuditDAO) {
		super(accountDao, auditDao, catDataDao, auditDataDao);
		this.contractorAuditDAO = contractorAuditDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
			
			findConAudit();
			if(auditStatus.equals(AuditStatus.Active.toString())) {
				conAudit.setAuditStatus(AuditStatus.Active);
				emailContractorOnAudit();
			}	
			if(auditStatus.equals(AuditStatus.Pending.toString())) {
				conAudit.setAuditStatus(AuditStatus.Pending);
			}	
				
			contractorAuditDAO.save(conAudit);

		return SUCCESS;
	}

	public String getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
	}
}
