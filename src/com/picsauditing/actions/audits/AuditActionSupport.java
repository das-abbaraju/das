package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.picsauditing.PICS.ContractorBean;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.OshaLog;
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
			if (!permissions.getCanSeeAudit().contains(conAudit.getAuditType().getAuditTypeID()))
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
		if (categories != null)
			return categories;

		if (conAudit.getAuditStatus().equals(AuditStatus.Exempt)) {
			categories = new ArrayList<AuditCatData>();
			return categories;
		}

		categories = catDataDao.findByAudit(conAudit, permissions);

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
		Map<Integer, AuditData> answers = auditDataDao.findAnswersByContractor(conAudit.getContractorAccount().getId(),
				ids);
		if (answers == null || answers.size() == 0)
			return null;
		return answers.get(AuditQuestion.MANUAL_PQF);
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
		if (conAudit.getAuditType().isPqf() && conAudit.getAuditStatus().equals(AuditStatus.Submitted))
			if (permissions.isAuditor())
				return true;
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
			if (type.isCanContractorEdit())
				return true;
			else
				return false;
		}

		if (permissions.isCorporate())
			return false;

		if (permissions.isOperator()) {
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
		for (OshaLog osha : conAudit.getContractorAccount().getOshas())
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
						&& (cAudit.getAuditStatus().equals(AuditStatus.Pending) || cAudit.getAuditStatus().equals(
								AuditStatus.Submitted)) && cAudit.getAuditType().isCanContractorView()) {
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

}
