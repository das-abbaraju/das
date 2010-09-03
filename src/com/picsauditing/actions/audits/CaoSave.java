package com.picsauditing.actions.audits;

import java.util.Date;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RecordNotFoundException;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.WorkflowStep;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class CaoSave extends AuditActionSupport {

	protected int caoID = 0;
	protected int stepID = 0;
	protected ContractorAuditOperatorDAO caoDAO;
	private String note;

	public CaoSave(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, AuditCategoryDataDAO catDataDao,
			AuditDataDAO auditDataDao, ContractorAuditOperatorDAO caoDAO) {
		super(accountDao, auditDao, catDataDao, auditDataDao);
		this.caoDAO = caoDAO;
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

			if (step.getNewStatus().after(AuditStatus.Submitted) && !conAudit.getAuditType().isHasMultiple()) {
				// This audit can only have one active audit, expire the
				// previous one
				for (ContractorAudit oldAudit : conAudit.getContractorAccount().getAudits()) {
					if (!oldAudit.equals(conAudit)) {
						if (oldAudit.getAuditType().equals(conAudit.getAuditType())) {
							oldAudit.setExpiresDate(new Date());
							auditDao.save(oldAudit);
						}
					}
				}
			}

			caoDAO.save(cao);
		}

		return SUCCESS;
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
