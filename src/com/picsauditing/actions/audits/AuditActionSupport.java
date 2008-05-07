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
}
