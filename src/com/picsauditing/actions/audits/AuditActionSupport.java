package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAudit;

public class AuditActionSupport extends ContractorActionSupport {
	protected int auditID = 0;
	protected ContractorAudit conAudit;
	protected List<AuditCatData> categories;
	protected AuditCategoryDataDAO catDataDao;
	protected AuditDataDAO auditDataDao;

	public AuditActionSupport(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, AuditCategoryDataDAO catDataDao, AuditDataDAO auditDataDao) {
		super(accountDao, auditDao);
		this.catDataDao = catDataDao;
		this.auditDataDao = auditDataDao;
	}
	
	public String execute() throws Exception {
		this.findConAudit();

		return SUCCESS;
	}

	protected void findConAudit() throws Exception {
		conAudit = auditDao.find(auditID);
		if (conAudit == null)
			throw new Exception("Audit for this " + this.auditID + " not found");
		
		this.id = conAudit.getContractorAccount().getId();
		findContractor();
		
		// Where are we handling all other perms? ie for operators
		if (permissions.isContractor() && !conAudit.getAuditType().isCanContractorView())
			throw new Exception("Contractors cannot view a "+conAudit.getAuditType().getAuditName());
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

	
	public List<AuditCatData> getCategories() {
		if (conAudit.getAuditStatus().equals(AuditStatus.Exempt))
			return null;

		if (categories == null) {
			categories = catDataDao.findByAudit(conAudit, permissions);
		}
		return categories;
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
		AuditData data = auditDataDao.findAnswersByContractor(conAudit.getContractorAccount().getId(), ids).get(AuditQuestion.MANUAL_PQF);
		return data.getAnswer();
	}
	
	public String getCatUrl() {
		if (conAudit.getAuditStatus().equals(AuditStatus.Pending))
			return "pqf_editMain.jsp";
		if (conAudit.getAuditStatus().equals(AuditStatus.Submitted)) {
			if (isCanVerify())
				return "pqf_verify.jsp";
			else
				return "pqf_view.jsp";
		}
		
		// Active/Exempt/Expired
		return "pqf_view.jsp";
	}
	
	public boolean isCanVerify() {
		if (permissions.isOnlyAuditor() && (permissions.getUserId() == conAudit.getAuditor().getId())
			&& (conAudit.getAuditType().isCanContractorEdit() == false))
			return true;
		if (permissions.seesAllContractors())
			return true;
		return false;
	}
	
	public boolean isCanEdit() {
		if (permissions.seesAllContractors())
			return true;
		
		if (permissions.isOnlyAuditor() 
				&& permissions.getUserId() == conAudit.getAuditor().getId()
				&& !conAudit.getAuditType().isCanContractorEdit())
			return true;
		
		if (permissions.isContractor()
				&& conAudit.getAuditType().isCanContractorEdit()
				&& conAudit.getAuditStatus().equals(AuditStatus.Pending))
			return true;
		return false;
	}

}
