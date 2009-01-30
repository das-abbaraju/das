package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.picsauditing.PICS.ContractorBean;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AccountName;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;

public class AuditActionSupport extends ContractorActionSupport {
	protected int auditID = 0;
	protected ContractorAudit conAudit;
	protected AuditCategoryDataDAO catDataDao;
	protected AuditDataDAO auditDataDao;
	protected List<AuditCatData> categories;
	protected String descriptionOsMs;
	private Map<Integer, AuditData> hasManual;

	public AuditActionSupport(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			AuditCategoryDataDAO catDataDao, AuditDataDAO auditDataDao) {
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
			if (!permissions.getCanSeeAudit().contains(conAudit.getAuditType().getId()))
				throw new NoRightsException(conAudit.getAuditType().getAuditName());
		}
		if (permissions.isContractor()) {
			if (!conAudit.getAuditType().isCanContractorView())
				throw new NoRightsException(conAudit.getAuditType().getAuditName());
		}
	}

	protected void findConAudit() throws Exception {
		conAudit = auditDao.find(auditID);
		if (conAudit == null)
			throw new Exception("Audit " + this.auditID + " not found");

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
		for (AuditCatData catData : categories)
			if (conAudit.getAuditType().isPqf())
				catData.getCategory().setValidDate(new Date());
			else
				catData.getCategory().setValidDate(conAudit.getCreationDate());

		return categories;
	}

	public boolean isHasSafetyManual() {
		hasManual = getDataForSafetyManual();
		if (hasManual == null || hasManual.size() == 0)
			return false;
		return true;
	}

	private String getAnswer(int questionID) {
		AuditData data = getAuditData(questionID);

		if (data == null) {
			return "";
		} else {
			return data.getAnswer();
		}
	}

	public AuditData getAuditData(int questionID) {
		List<Integer> ids = new ArrayList<Integer>();
		ids.add(questionID);
		Map<Integer, Map<String, AuditData>> answers = null;
		answers = auditDataDao.findAnswersByContractor(conAudit.getContractorAccount().getId(), ids);
		if (answers == null)
			return null;

		for (AuditData answer : answers.get(questionID).values())
			return answer;
		return null;
	}

	public Map<Integer, AuditData> getDataForSafetyManual() {
		Map<Integer, AuditData> answers = auditDataDao.findAnswersForSafetyManual(conAudit.getContractorAccount()
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
		if (conAudit.getAuditType().isPqf() && conAudit.getAuditStatus().isActiveSubmitted())
			if (permissions.isAuditor())
				return true;

		if (conAudit.getAuditType().getClassType().equals(AuditTypeClass.Policy)
				&& conAudit.getAuditStatus().equals(AuditStatus.Submitted)
				&& permissions.hasPermission(OpPerms.InsuranceVerification, OpType.Edit)) {
			return true;
		}
		return false;
	}

	public boolean isCanEdit() {
		if (conAudit.getAuditStatus().equals(AuditStatus.Expired))
			return false;

		AuditType type = conAudit.getAuditType();

		// Auditors can edit their assigned audits
		if (type.isHasAuditor() && !type.isCanContractorEdit() && conAudit.getAuditor() != null
				&& permissions.getUserId() == conAudit.getAuditor().getId())
			return true;

		if (permissions.isContractor()) {
			if (type.isAnnualAddendum() && conAudit.getAuditStatus().equals(AuditStatus.Active))
				return false;
			if(type.getClassType().equals(AuditTypeClass.Policy) && conAudit.willExpireSoon())
				return false;
			
			if (type.isCanContractorEdit())
				return true;
			else
				return false;
		}

		if (permissions.isCorporate())
			return false;

		if (permissions.isOperator()) {
			if (conAudit.getAuditType().getClassType().equals(AuditTypeClass.Policy)) {
				if(conAudit.willExpireSoon())
					return false;
				if (conAudit.getOperators().size() == 1
						&& conAudit.getOperators().get(0).getOperator().getId() == permissions.getAccountId())
					return true;
			}

			if (conAudit.getRequestingOpAccount() != null) {
				for (AuditOperator auditOperator : conAudit.getRequestingOpAccount().getAudits()) {
					if (auditOperator.getAuditType().equals(conAudit.getAuditType()) && auditOperator.isCanEdit())
						return true;
				}
			}
			return false;
		}

		if (permissions.seesAllContractors())
			return true;

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

	protected void emailContractorOnAudit() {
		if (conAudit.getAuditType().isCanContractorView()) {
			boolean allActive = true;
			for (ContractorAudit cAudit : getActiveAudits()) {
				// We have to check (cAudit != conAudit) because we haven't set
				// the status yet...it happens later
				if (!cAudit.equals(conAudit) && cAudit.getAuditStatus().isPendingSubmittedResubmitted()
						&& cAudit.getAuditType().isCanContractorView()) {
					// this contractor still has open audits to complete...don't
					// send the email
					allActive = false;
				}
			}
			if (allActive) {
				// Send email to contractor telling them thank you for playing
				try {
					EmailBuilder emailBuilder = new EmailBuilder();
					emailBuilder.setTemplate(13); // Audits Thank You
					emailBuilder.setPermissions(permissions);
					emailBuilder.setContractor(contractor);
					EmailSender.send(emailBuilder.build());
					ContractorBean.addNote(contractor.getId(), permissions, "Sent Audits Thank You email to "
							+ emailBuilder.getSentTo());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	public Set<String> getLegalNames() {
		Set<String> list = new TreeSet<String>();
		if (conAudit != null) {
			for (ContractorOperator co : conAudit.getContractorAccount().getOperators())
				for (AccountName legalName : co.getOperatorAccount().getNames())
					list.add(legalName.getName());
		}
		return list;
	}
}
