package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.RecordNotFoundException;
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
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.Strings;

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
		for (AuditCatData catData : categories)
			if (conAudit.getAuditType().getClassType().isPqf())
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

		if (conAudit.getAuditType().getClassType()
				.equals(AuditTypeClass.Policy)
				&& conAudit.getAuditStatus().equals(AuditStatus.Submitted)
				&& permissions.hasPermission(OpPerms.InsuranceVerification,
						OpType.Edit)) {
			return true;
		}
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
			if (type.isAnnualAddendum()
					&& conAudit.getAuditStatus().equals(AuditStatus.Active))
				return false;
			if (type.getClassType().isPolicy()) {
				if (conAudit.willExpireSoon())
					// Never let them edit the old policy
					return false;
			}

			if (type.isCanContractorEdit())
				return true;
			else
				return false;
		}

		if (permissions.isCorporate())
			return false;

		if (permissions.isOperator()) {
			if (conAudit.getAuditType().getClassType().equals(
					AuditTypeClass.Policy)) {
				if (conAudit.getAuditStatus().isPending())
					return true;
				if (conAudit.getOperators().size() == 1
						&& conAudit.getOperators().get(0).getOperator().getId() == permissions
								.getAccountId())
					return true;
			}

			if (conAudit.getRequestingOpAccount() != null) {
				for (AuditOperator auditOperator : conAudit
						.getRequestingOpAccount().getAudits()) {
					if (auditOperator.getAuditType().equals(
							conAudit.getAuditType())
							&& auditOperator.isCanEdit())
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
				if (!cAudit.equals(conAudit)
						&& cAudit.getAuditStatus()
								.isPendingSubmittedResubmitted()
						&& cAudit.getAuditType().isCanContractorView()) {
					// this contractor still has open audits to complete...don't
					// send the email
					allActive = false;
				}
			}
			if (allActive) {
				// Sending all the operator Emails too
				Set<String> emailAddresses = new HashSet<String>();
				for (ContractorOperator contractorOperator : contractor.getOperators()) {
					emailAddresses.addAll(Strings.findUniqueEmailAddresses(contractorOperator.getOperatorAccount().getActivationEmails()));
				}
				// Send email to contractor telling them thank you for submitting all the audits
				try {
					EmailBuilder emailBuilder = new EmailBuilder();
					emailBuilder.setTemplate(13); // Audits Thank You
					emailBuilder.setPermissions(permissions);
					emailBuilder.setContractor(contractor);
					emailBuilder.setBccAddresses(Strings.implode(emailAddresses, ",")); 
					EmailSender.send(emailBuilder.build());
					addNote(contractor, "Sent Audits Thank You email to "
							+ emailBuilder.getSentTo(), NoteCategory.Audits);
					// TODO email all the operators as well
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	public List<String> getLegalNames() {
		Set<String> list = new TreeSet<String>();
		boolean canSeeLegalName;
		if (conAudit != null) {
			for (ContractorOperator co : conAudit.getContractorAccount()
					.getOperators()) {
				canSeeLegalName = false;
				if (permissions.isOperator()) {
					if (co.getOperatorAccount().getId() == permissions
							.getAccountId()) {
						canSeeLegalName = true;
					}
				} else {
					canSeeLegalName = true;
				}
				if (canSeeLegalName) {
					for (AccountName legalName : co.getOperatorAccount().getInheritInsurance()
							.getNames()) {
						list.add(legalName.getName());
					}
				}

			}
		}
		List<String> sortedList = new ArrayList<String>();
		sortedList.add("All");
		sortedList.addAll(list);

		return sortedList;
	}

}
