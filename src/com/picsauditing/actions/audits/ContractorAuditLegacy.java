package com.picsauditing.actions.audits;

import java.util.TreeMap;
import java.util.Vector;

import com.picsauditing.PICS.pqf.Constants;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.YesNo;

public class ContractorAuditLegacy {
	private ContractorAudit audit;
	private ContractorAuditDAO auditDao;
	
	public ContractorAuditLegacy() {
		auditDao = (ContractorAuditDAO) com.picsauditing.util.SpringUtils.getBean("ContractorAuditDAO");
	}
	
	public void setAuditID(String auditIdString) throws Exception {
		int auditID = Integer.parseInt(auditIdString);
		
		if (auditID == 0)
			throw new Exception("Missing auditID");

		audit = auditDao.find(auditID);
		if (audit == null)
			throw new Exception("Failed to find ContractorAudit");
	}

	public int getAuditID() {
		if (audit == null)
			return 0;
		return audit.getId();
	}

	public void execute() {
		

	}
	
	public boolean isComplete() {
		boolean isComplete = true;
		
		for(AuditCatData catData : this.audit.getCategories()) {
			if (catData.getApplies().equals(YesNo.No) || catData.getPercentCompleted() < 100)
				isComplete = false;
		}
		return isComplete;
	}

	public int getPercentComplete() {
		int requiredQuestions = 0;
		int answeredQuestions = 0;
		
		for(AuditCatData catData : this.audit.getCategories()) {
			requiredQuestions += catData.getNumRequired();
			answeredQuestions += catData.getNumAnswered();
		}
		if (requiredQuestions == 0)
			return 0;
		
		return Math.round(100 * (float)answeredQuestions / (float)requiredQuestions);
	}
	
	public void updatePercentageCompleted(int catID) {
		for(AuditCatData catData : this.audit.getCategories()) {
			if (catData.getCategory().getId() == catID) {
				if (catData.getApplies().equals(YesNo.No)) {
					return;
				}
				break;
			}
		}
	}

	public ContractorAudit getAudit() {
		return audit;
	}
}
