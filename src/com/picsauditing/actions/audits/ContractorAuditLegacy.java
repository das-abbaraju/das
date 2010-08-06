package com.picsauditing.actions.audits;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.YesNo;

public class ContractorAuditLegacy {
	private ContractorAudit audit;
	private ContractorAuditDAO auditDao;
	private Permissions permissions;
	
	public ContractorAuditLegacy() {
		auditDao = (ContractorAuditDAO) com.picsauditing.util.SpringUtils.getBean("ContractorAuditDAO");
	}
	
	public void setAuditID(String auditIdString) throws IllegalArgumentException {
		int auditID = 0;
		try {
			auditID = Integer.parseInt(auditIdString);
		} catch (Exception e) {}
		
		if (auditID == 0)
			throw new IllegalArgumentException("Missing or invalid auditID " + auditIdString);

		audit = auditDao.find(auditID);
		if (audit == null)
			throw new IllegalArgumentException("Failed to find ContractorAudit " + auditID);
	}

	public int getAuditID() {
		if (audit == null)
			return 0;
		return audit.getId();
	}

	public void execute() {
	}
	
	public void saveAudit() {
		audit = auditDao.save(this.audit);
	}
	
	public boolean isComplete() {
		boolean isComplete = true;
		
		for(AuditCatData catData : this.audit.getCategories()) {
			if (catData.isApplies() || catData.getPercentCompleted() < 100)
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
				if (catData.isApplies()) {
					return;
				}
				break;
			}
		}
	}

	public ContractorAudit getAudit() {
		return audit;
	}
	

	public Permissions getPermissions() {
		return permissions;
	}

	public void setPermissions(Permissions permissions) {
		this.permissions = permissions;
	}
	
	public boolean canView() {
		AuditType type = audit.getAuditType();
		
		if (permissions.isContractor()) {
			if (!type.isCanContractorView())
				return false;
			if (type.isPqf())
				return true;
			// Contractors can't see other audits while they are being filled out
			if (audit.getAuditStatus().equals(AuditStatus.Pending))
				return false;
			return true;
		}
		
		// Operators/Corporate/Auditors/Admins can see all audits for their contractors
		return true;
	}
	
	public boolean canEdit() {
		if (audit.getAuditStatus().equals(AuditStatus.Expired))
			return false;
		
		AuditType type = audit.getAuditType();
		
		// Auditors can edit their assigned audits
		if (type.isHasAuditor() 
				&& audit.getAuditor() != null 
				&& permissions.getUserId() == audit.getAuditor().getId())
			return true;
		
		if (permissions.isContractor()) {
			if (type.isCanContractorEdit()) return true;
			else return false;
		}
		
		if (permissions.isCorporate())
			return false;
		
		if (permissions.isOperator()) {
			if (audit.getRequestingOpAccount() != null 
				&& audit.getRequestingOpAccount().getId() == permissions.getAccountId()) return true;
			return false;
		}

		if (permissions.hasPermission(OpPerms.AllContractors))
			return true;
		
		return false;
	}
	
	/**
	 * Only Auditors can Verify PQF audit data. No other audits are verifiable.
	 * @param permissions
	 * @return
	 */
	public boolean canVerify() {
		if (this.audit.getAuditType().isPqf())
			if (permissions.isAuditor())
				return true;
		return false;
	}
}
