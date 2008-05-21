package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.NcmsCategoryDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.NcmsCategory;

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
		if (!forceLogin())
			return LOGIN;
		this.findConAudit();

		if (this.conAudit.getAuditType().getAuditTypeID() == AuditType.NCMS)
			return "NCMS";
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

	
	public List<AuditCatData> getCategories() {
		if (conAudit.getAuditStatus().equals(AuditStatus.Exempt))
			return null;

		if (categories == null) {
			categories = catDataDao.findByAudit(conAudit, permissions);
		}
		return categories;
	}

	public List<NcmsCategory> getNcmsCategories() {
		try {
			NcmsCategoryDAO dao = new NcmsCategoryDAO();
			return dao.findCategories(this.id);
		} catch (Exception e) {
			List<NcmsCategory> error = new ArrayList<NcmsCategory>();
			NcmsCategory cat = new NcmsCategory();
			cat.setName("Error retrieving list");
			error.add(cat);
			return error;
		}
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
		if (!isCanEdit())
			return "pqf_view.jsp";
		
		if (conAudit.getAuditStatus().equals(AuditStatus.Pending))
			return "pqf_edit.jsp";
		
		if (conAudit.getAuditStatus().equals(AuditStatus.Submitted)) {
			if (isCanVerify())
				return "pqf_edit.jsp";
			else
				return "pqf_view.jsp";
		}
		
		// Active/Exempt/Expired
		return "pqf_view.jsp";
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
