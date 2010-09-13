package com.picsauditing.actions.audits;

import java.util.Date;

import com.picsauditing.PICS.AuditBuilderController;
import com.picsauditing.PICS.AuditPercentCalculator;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RecordNotFoundException;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.dao.OshaAuditDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.jpa.entities.WorkflowStep;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.Strings;
import com.picsauditing.util.log.PicsLogger;

@SuppressWarnings("serial")
public class CaoSave extends AuditActionSupport {

	protected int caoID = 0;
	protected int stepID = 0;
	private String note;

	protected ContractorAuditOperatorDAO caoDAO;
	protected OshaAuditDAO oshaAuditDAO;
	private AuditPercentCalculator auditPercentCalculator;
	private AuditBuilderController auditBuilder;

	public CaoSave(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, AuditCategoryDataDAO catDataDao,
			AuditDataDAO auditDataDao, OshaAuditDAO oshaAuditDAO, ContractorAuditOperatorDAO caoDAO, AuditPercentCalculator auditPercentCalculator, AuditBuilderController auditBuilder) {
		super(accountDao, auditDao, catDataDao, auditDataDao);
		this.caoDAO = caoDAO;
		this.oshaAuditDAO = oshaAuditDAO;
		this.auditPercentCalculator = auditPercentCalculator;
		this.auditBuilder = auditBuilder;
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		findConAudit();

		if (conAudit.isExpired()) {
			addActionError("You can't change an expired " + conAudit.getAuditType().getAuditName());
			return SUCCESS;
		}

		if (caoID > 0) {
			ContractorAuditOperator cao = null;
			for (ContractorAuditOperator cao2 : conAudit.getOperators()) {
				if (cao2.getId() == caoID) {
					cao = cao2;
					break;
				}
			}
			if (cao == null)
				throw new RecordNotFoundException("ContractorAuditOperator");

			WorkflowStep step = conAudit.getAuditType().getWorkFlow().getStep(stepID);

			if (step == null) {
				addAlertMessage("No action specified");
			}

			if (step.getOldStatus().isSubmitted() && step.getNewStatus().isComplete()) {
				if (cao.getPercentVerified() < 100)
					addActionError("Please complete all requirements.");
			}

			if (!cao.getStatus().equals(step.getOldStatus())) {
				addActionError("This action cannot be performed because it is not longer in the " + step.getOldStatus()
						+ " state");
			}

			if (step.isNoteRequired() && Strings.isEmpty(note)) {
				addActionError("You must enter a note");
			}

			if (step.getNewStatus().isSubmittedResubmitted()) {
				if (cao.getPercentComplete() < 100) {
					addActionError("Please complete all required questions.");
				}
				// if (cao.isCanContractorSubmit()) {
				// addActionError("Please enter all required questions before submitting the policy.");
				// }
			}

			if (this.getActionErrors().size() > 0)
				return SUCCESS;

			// TODO stamp notes/caoWorkflow
			cao.changeStatus(step.getNewStatus(), permissions);

			if (step.getNewStatus().isComplete()) {
				if (cao.getAudit().getAuditType().getClassType().isPolicy()
						&& cao.getOperator().isAutoApproveInsurance()) {
					if (cao.getFlag() != null) {
						if (cao.getFlag().isGreen())
							cao.setStatus(AuditStatus.Approved);
						else if (cao.getFlag().isRed())
							cao.setStatus(AuditStatus.Incomplete);
					}
				}
			}

			if (step.getEmailTemplate() != null) {
				EmailBuilder emailBuilder = new EmailBuilder();
				// TODO decide where we're going to store the email template
				emailBuilder.setTemplate(step.getEmailTemplate());
				emailBuilder.setTemplate(conAudit.getAuditType().getTemplate());

				emailBuilder.setPermissions(permissions);
				if (conAudit.getAuditType().getClassType().isAudit())
					emailBuilder.setFromAddress("\"PICS Auditing\"<audits@picsauditing.com>");
				else
					emailBuilder.setFromAddress("\"" + permissions.getName() + "\"<" + permissions.getEmail() + ">");
				// One day we may need to store the from and to into the
				// workflow step

				emailBuilder.setContractor(cao.getAudit().getContractorAccount(), cao.getAudit().getAuditType()
						.getClassType().isPolicy() ? OpPerms.ContractorInsurance : OpPerms.ContractorSafety);
				// or??
				emailBuilder.setConAudit(conAudit);

				emailBuilder.addToken("cao", cao);
				EmailQueue email = emailBuilder.build();
				email.setViewableBy(cao.getOperator());
				EmailSender.send(email);
			}

			if (step.getNewStatus().after(AuditStatus.Submitted)) {
				// Expire previous audits
				int lastYear = DateBean.getCurrentYear() - 1;
				for (ContractorAudit oldAudit : conAudit.getContractorAccount().getAudits()) {
					if (!oldAudit.equals(conAudit) && !oldAudit.isExpired()) {
						if (oldAudit.getAuditType().equals(conAudit.getAuditType())) {
							if (conAudit.getAuditType().isAnnualAddendum()) {
								if (lastYear == Integer.parseInt(conAudit.getAuditFor())
										&& Integer.parseInt(oldAudit.getAuditFor()) < lastYear - 2) {
									oldAudit.setExpiresDate(new Date());
									auditDao.save(oldAudit);
								}
							} else if (!conAudit.getAuditType().isHasMultiple()) {
								oldAudit.setExpiresDate(new Date());
								auditDao.save(oldAudit);
							}
						}
					}
				}

				for (AuditCatData auditCatData : conAudit.getCategories()) {
					if (!auditCatData.isApplies()) {
						PicsLogger.log("removing unused data for category " + auditCatData.getCategory().getName());
						if (conAudit.getAuditType().isAnnualAddendum() && auditCatData.getCategory().isSha()) {
							switch (auditCatData.getCategory().getId()) {
							case AuditCategory.OSHA_AUDIT:
								oshaAuditDAO.removeByType(conAudit.getId(), OshaType.OSHA);
								break;
							case AuditCategory.MSHA:
								oshaAuditDAO.removeByType(conAudit.getId(), OshaType.MSHA);
								break;
							case AuditCategory.CANADIAN_STATISTICS:
								oshaAuditDAO.removeByType(conAudit.getId(), OshaType.COHS);
								break;
							}
						} else {
							auditDataDao.removeDataByCategory(conAudit.getId(), auditCatData.getCategory().getId());
						}
					}
				}

			}

			caoDAO.save(cao);
		}
	
		auditBuilder.fillAuditCategories(conAudit);
		auditPercentCalculator.percentCalculateComplete(conAudit, true);
		return SUCCESS;
	}

	public void temp() {
		// TODO Move this over to the CaoSave class
		/*
		 * findConAudit(); String note = ""; if
		 * (auditStatus.equals(AuditStatus.Active.toString())) {
		 * if(conAudit.getPercentComplete() < 100) return SUCCESS;
		 * 
		 * conAudit.changeStatus(AuditStatus.Active, getUser()); note =
		 * "Verified and Activated the " +
		 * conAudit.getAuditType().getAuditName();
		 * 
		 * if (conAudit.getAuditType().isAnnualAddendum() &&
		 * DateBean.getCurrentYear() - 1 ==
		 * Integer.parseInt(conAudit.getAuditFor())) { // We're activating the
		 * most recent year's audit (ie 2008) for (ContractorAudit audit :
		 * contractor.getAudits()) { if (audit.getAuditType().isAnnualAddendum()
		 * && Integer.parseInt(audit.getAuditFor()) < DateBean.getCurrentYear()
		 * - 3 && !audit.getAuditStatus().isExpired()) { // Any annual audit
		 * before 2006 (ie 2005) audit.setAuditStatus(AuditStatus.Expired);
		 * auditDao.save(audit); } } } } // TODO add a column to auditData to
		 * keep track when the contractor has // changed the answer. if
		 * (auditStatus.equals(AuditStatus.Incomplete.toString())) {
		 * conAudit.changeStatus(AuditStatus.Incomplete, getUser()); if
		 * (conAudit.getAuditType().isPqf()) { List<AuditData> temp =
		 * auditDataDao.findCustomPQFVerifications(conAudit.getId()); for
		 * (AuditData auditData : temp) { AuditCategory auditCategory =
		 * auditData.getQuestion().getCategory(); for (AuditCatData aCatData :
		 * conAudit.getCategories()) { if (aCatData.getCategory() ==
		 * auditCategory && aCatData.getPercentVerified() < 100) {
		 * aCatData.setRequiredCompleted(aCatData.getRequiredCompleted() - 1);
		 * aCatData.setPercentCompleted(99); } } }
		 * conAudit.setPercentComplete(99); auditDao.save(conAudit); } if
		 * (conAudit.getAuditType().isAnnualAddendum()) { for (AuditCatData
		 * aCatData : conAudit.getCategories()) { if
		 * (aCatData.getCategory().getId() == AuditCategory.EMR ||
		 * aCatData.getCategory().getId() == AuditCategory.GENERAL_INFORMATION
		 * || aCatData.getCategory().getId() == AuditCategory.OSHA_AUDIT ||
		 * aCatData.getCategory().getId() == AuditCategory.LOSS_RUN) { if
		 * (aCatData.getPercentVerified() < 100) {
		 * aCatData.setRequiredCompleted(aCatData.getRequiredCompleted() - 1);
		 * aCatData.setPercentCompleted(99);
		 * aCatData.setAuditColumns(permissions); } } }
		 * conAudit.setPercentComplete(99); conAudit.setLastRecalculation(new
		 * Date()); auditDao.save(conAudit); } note = "Rejected " +
		 * conAudit.getAuditType().getAuditName(); }
		 * 
		 * if(!Strings.isEmpty(note)) {
		 * if(!Strings.isEmpty(conAudit.getAuditFor())) note += " " +
		 * conAudit.getAuditFor(); addNote(contractor, note,
		 * NoteCategory.Audits, LowMedHigh.Low, true, Account.EVERYONE,
		 * getUser()); }
		 * 
		 * conAudit = auditDao.save(conAudit); ContractorAccount
		 * contractorAccount = conAudit.getContractorAccount();
		 * contractor.incrementRecalculation();
		 * accountDao.save(contractorAccount);
		 */
	}

	public int getCaoID() {
		return caoID;
	}

	public void setCaoID(int caoID) {
		this.caoID = caoID;
	}

	public void setStepID(int stepID) {
		this.stepID = stepID;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

}
