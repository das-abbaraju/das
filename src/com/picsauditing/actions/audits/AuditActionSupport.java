package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.RecordNotFoundException;
import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.OshaType;

public class AuditActionSupport extends ContractorActionSupport {
	protected int auditID = 0;
	protected ContractorAudit conAudit;
	protected AuditCategoryDataDAO catDataDao;
	protected AuditDataDAO auditDataDao;
	protected List<AuditCatData> categories;
	protected String descriptionOsMs;
	private Map<Integer, AuditData> hasManual;

	public AuditActionSupport(ContractorAccountDAO accountDao,
			ContractorAuditDAO auditDao, AuditCategoryDataDAO catDataDao,
			AuditDataDAO auditDataDao) {
		super(accountDao, auditDao);
		this.catDataDao = catDataDao;
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
			if (!permissions.getCanSeeAudit().contains(
					conAudit.getAuditType().getId()))
				throw new NoRightsException(conAudit.getAuditType()
						.getAuditName());
		}
		if (permissions.isContractor()) {
			if (!conAudit.getAuditType().isCanContractorView())
				throw new NoRightsException(conAudit.getAuditType()
						.getAuditName());
		}
	}

	protected void findConAudit() throws Exception {
		conAudit = auditDao.find(auditID);
		if (conAudit == null)
			throw new RecordNotFoundException("Audit " + this.auditID);

		if (conAudit.getExpiresDate() != null) {
			if (DateBean.getDateDifference(conAudit.getExpiresDate()) < 1) {
				conAudit.setAuditStatus(AuditStatus.Expired);
				auditDao.save(conAudit);
			}
		}

		contractor = conAudit.getContractorAccount();
		id = contractor.getId();
		if (permissions.isContractor() && id != permissions.getAccountId())
			throw new Exception("Contractors can only view their own audits");

		if (!checkPermissionToView())
			throw new NoRightsException("No Rights to View this Contractor");

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

	public boolean isSingleCat() {
		return getCategories().size() == 1;
	}

	public List<AuditCatData> getCategories() {
		if (categories != null)
			return categories;

		if (conAudit.getAuditStatus().equals(AuditStatus.Exempt)) {
			categories = new ArrayList<AuditCatData>();
			return categories;
		}

		categories = catDataDao.findByAudit(conAudit, permissions);

		// For PQFs the valid date is today, for all other audits we use the
		// creation date
		// This is important when we figure out which questions should be
		// display
		// And therefore which subcategories have valid questions,
		// and which categories have subcategories
		// We don't actually loop through the all the questions just yet, that's
		// later
		for (AuditCatData catData : categories) {
			if (conAudit.getAuditType().getClassType().isPqf())
				catData.getCategory().setValidDate(new Date());
			else
				catData.getCategory().setValidDate(conAudit.getCreationDate());
			if(permissions.isPicsEmployee())
				catData.getCategory().setCountries(contractor.getCountries());
			else
				catData.getCategory().setCountries(permissions.getAccountCountries());
		}

		return categories;
	}

	public boolean isHasSafetyManual() {
		hasManual = getDataForSafetyManual();
		if (hasManual == null || hasManual.size() == 0)
			return false;
		return true;
	}

	public Map<Integer, AuditData> getDataForSafetyManual() {
		Map<Integer, AuditData> answers = auditDataDao
				.findAnswersForSafetyManual(conAudit.getContractorAccount()
						.getId(), AuditQuestion.MANUAL_PQF);
		if (answers == null || answers.size() == 0)
			return null;
		return answers;
	}

	public Map<Integer, AuditData> getSafetyManualLink() {
		if (hasManual != null)
			return hasManual;
		else
			hasManual = getDataForSafetyManual();
		return hasManual;
	}

	public boolean isCanVerify() {
		if (!conAudit.getAuditType().isMustVerify())
			return false;
		if (conAudit.getAuditType().isPqf()
				&& conAudit.getAuditStatus().isActiveSubmitted())
			if (permissions.isAuditor())
				return true;

		return false;
	}

	public boolean isCanEdit() {
		if (conAudit.getAuditStatus().isExpired())
			return false;
		
		AuditType type = conAudit.getAuditType();

		if (type.getClassType().isPolicy()) {
			if (conAudit.willExpireSoon())
				// Never let them edit the old policy
				// But should we allow for exceptions?
				return false;
		}

		// Auditors can edit their assigned audits
		if (type.isHasAuditor() && !type.isCanContractorEdit()
				&& conAudit.getAuditor() != null
				&& permissions.getUserId() == conAudit.getAuditor().getId())
			return true;

		if (permissions.isContractor()) {
			if ((type.isAnnualAddendum() || type.getId() == 99)
					&& conAudit.getAuditStatus().isActiveSubmitted())
				// contractors can't modify annual updates that are already verified or submitted
				return false;

			return type.isCanContractorEdit();
		}

		if (permissions.isOperatorCorporate()) {
			if (permissions.getCanEditAudits().contains(type.getId())) {
				if (type.getClassType().isPolicy() && isPolicyWithOtherOperators())
					return false;
				
				return true;
			}
			return false;
		}

		if (permissions.seesAllContractors())
			return true;

		return false;

	}

	/**
	 * 
	 * @return true if the current users is an operator and there is a visible cao belonging to another operator
	 */
	public boolean isPolicyWithOtherOperators() {
		for (ContractorAuditOperator cao : conAudit.getOperators()) {
			if (cao.isVisible()) {
				if (!permissions.getVisibleCAOs().contains(cao.getOperator().getId())) {
					// This logic is somewhat complex so here's an example:
					// BASF Freeport Hub has access to many operators 
					// who use either BASF Corporate and BASF Catalyst insurance requirements
					// If this contractor policy is visible (needed) for Paramount, 
					// then the policy is locked down.
					// One potential flaw is that if the other CAO happens to be BASF Canada, 
					// which is not part of the Freeport Hub, then the policy will be locked for BASF Freeport.
					return true;
				}
			}
		}

		return false;
	}

	public String getDescriptionOsMs() {
		String descriptionText = "OSHA Recordable";
		for (OshaAudit osha : conAudit.getOshas())
			if (osha.getType().equals(OshaType.MSHA))
				descriptionText = "MSHA Reportable";
			else
				descriptionText = "OSHA Recordable";
		return descriptionText;
	}
}
