package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.NcmsCategoryDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.NcmsCategory;

public class AuditActionSupport extends ContractorActionSupport {
	protected int auditID = 0;
	protected ContractorAudit conAudit;
	protected AuditDataDAO auditDataDao;

	public AuditActionSupport(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, AuditDataDAO auditDataDao) {
		super(accountDao, auditDao);
		this.auditDataDao = auditDataDao;
	}
	
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		this.findConAudit();

		return SUCCESS;
	}
	
	protected void canSeeAudit() throws NoRightsException {
		if (permissions.isPicsEmployee())
			return;
		if (permissions.isOperator() || permissions.isCorporate()) {
			if (! permissions.getCanSeeAudit().contains(conAudit.getAuditType().getAuditTypeID()))
				throw new NoRightsException(conAudit.getAuditType().getAuditName());
		}
		if (permissions.isContractor()) {
			if (! conAudit.getAuditType().isCanContractorView())
				throw new NoRightsException(conAudit.getAuditType().getAuditName());
		}
	}

	protected void findConAudit() throws Exception {
		conAudit = auditDao.find(auditID);
		if (conAudit == null)
			throw new Exception("Audit for this " + this.auditID + " not found");
		
		if (conAudit.getExpiresDate() != null){
			
		}
		
		this.id = conAudit.getContractorAccount().getId();
		findContractor();
		canSeeAudit();
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


	public boolean isHasSafetyManual() {
		String hasManual = getAnswer(AuditQuestion.MANUAL_PQF);
		if (hasManual == null || hasManual.length() == 0)
			return false;
		return true;
	}
	
	private String getAnswer(int questionID) {
		List<Integer> ids = new ArrayList<Integer>();
		ids.add(questionID);
		Map<Integer, AuditData> answers = auditDataDao.findAnswersByContractor(conAudit.getContractorAccount().getId(), ids);
		if (answers == null || answers.size() == 0)
			return "";
		AuditData data = answers.get(AuditQuestion.MANUAL_PQF);
		return data.getAnswer();
	}
	
	public boolean isCanVerify() {
		if (conAudit.getAuditType().isPqf())
			if (permissions.isAuditor())
				return true;
		return false;
	}
	
	public boolean isCanEdit() {
		if (conAudit.getAuditStatus().equals(AuditStatus.Expired))
			return false;
		
		AuditType type = conAudit.getAuditType();
		
		// Auditors can edit their assigned audits
		if (type.isHasAuditor() && !type.isCanContractorEdit() 
				&& conAudit.getAuditor() != null
				&& permissions.getUserId() == conAudit.getAuditor().getId())
			return true;
		
		if (permissions.isContractor()) {
			if (type.isCanContractorEdit()) return true;
			else return false;
		}
		
		if (permissions.isCorporate())
			return false;
		
		if (permissions.isOperator()) {
			if (conAudit.getRequestingOpAccount() != null)
				if (conAudit.getRequestingOpAccount().getId() == permissions.getAccountId()) return true;
			return false;
		}

		if (permissions.seesAllContractors())
			return true;
		
		return false;
		
	}
}
