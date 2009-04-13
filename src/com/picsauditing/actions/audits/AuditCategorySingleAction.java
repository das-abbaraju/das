package com.picsauditing.actions.audits;

import java.util.Date;

import com.picsauditing.PICS.AuditBuilder;
import com.picsauditing.PICS.AuditPercentCalculator;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.CaoStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;

@SuppressWarnings("serial")
public class AuditCategorySingleAction extends AuditActionSupport {

	protected AuditStatus auditStatus;
	protected AuditPercentCalculator auditPercentCalculator;
	protected AuditBuilder auditBuilder;
	
	protected int opID;
	protected ContractorAuditOperatorDAO caoDAO;

	public AuditCategorySingleAction(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, ContractorAuditOperatorDAO caoDAO,
			AuditCategoryDataDAO catDataDao, AuditDataDAO auditDataDao, AuditPercentCalculator auditPercentCalculator, AuditBuilder auditBuilder) {
		super(accountDao, auditDao, catDataDao, auditDataDao);
		this.auditPercentCalculator = auditPercentCalculator;
		this.auditBuilder = auditBuilder;
		this.caoDAO = caoDAO;
	}

	public String execute() throws Exception {

		// Calculate and set the percent complete
		if(conAudit.getLastRecalculation() == null) {
			auditPercentCalculator.percentCalculateComplete(conAudit, true);
			conAudit.setLastRecalculation(new Date());
			auditDao.save(conAudit);
		}
		else	
			auditPercentCalculator.percentCalculateComplete(conAudit, conAudit.getAuditType().getClassType().equals(
				AuditTypeClass.IM));

		if ("Submit".equals(button)) {
			if (conAudit.getAuditType().isPqf()) {
				if (conAudit.getAuditStatus().equals(AuditStatus.Active) && conAudit.getPercentVerified() == 100) {
					auditStatus = AuditStatus.Active;
					if (conAudit.isAboutToExpire())
						conAudit.setCompletedDate(new Date());
				} else if (conAudit.getAuditStatus().isActiveResubmittedExempt())
					auditStatus = AuditStatus.Resubmitted;
				else
					auditStatus = AuditStatus.Submitted;
				conAudit.setExpiresDate(DateBean.getMarchOfNextYear(new Date()));
			} else if (conAudit.getAuditType().getClassType().isPolicy()) {
				if (conAudit.getPercentComplete() == 100 && !conAudit.getAuditStatus().isExpired()) {
					ContractorAuditOperator cao = caoDAO.find(conAudit.getId(), opID);
					if (cao != null) {
						cao.setStatus(CaoStatus.Awaiting);
						cao.setAuditColumns(permissions);
						caoDAO.save(cao);
						addActionMessage("The <strong>" + conAudit.getAuditType().getAuditName()
								+ "</strong> Policy has been submitted for <strong>" + cao.getOperator().getName()
								+ "</strong>.");
					}
				} else {
					addActionError("The <strong>" + conAudit.getAuditType().getAuditName() 
							+ "</strong> policy is not complete. Please enter all required answers before submitting.");
				}
			} else if (conAudit.getAuditType().isHasRequirements() || conAudit.getAuditType().isMustVerify())
				auditStatus = AuditStatus.Submitted;
			else
				auditStatus = AuditStatus.Active;
		}

		if ("Resubmit".equals(button)) {
			if (conAudit.getAuditType().getClassType().isPolicy()) {
				ContractorAuditOperator cao = caoDAO.find(conAudit.getId(), opID);
				if (cao != null) {
					cao.setStatus(CaoStatus.Awaiting);
					cao.setAuditColumns(permissions);
					caoDAO.save(cao);
					addActionMessage("The <strong>" + conAudit.getAuditType().getAuditName()
							+ "</strong> Policy has been resubmitted  for <strong>" + cao.getOperator().getName()
							+ "</strong>.");
				}
			} else {
				conAudit.changeStatus(AuditStatus.Submitted, getUser());
				auditDao.save(conAudit);
				return SUCCESS;
			}
		}

		if (auditStatus != null && !auditStatus.equals(conAudit.getAuditStatus())) {
			// We're changing the status
			if (auditStatus.equals(AuditStatus.Active)) {
				conAudit.setClosedDate(new Date());
				if (!conAudit.getAuditType().isHasMultiple()) {
					// This audit can only have one active audit, expire the
					// previous one
					for (ContractorAudit oldAudit : conAudit.getContractorAccount().getAudits()) {
						if (!oldAudit.equals(conAudit)) {
							if (oldAudit.getAuditType().equals(conAudit.getAuditType())
									|| (oldAudit.getAuditType().equals(AuditType.NCMS) && conAudit.getAuditType()
											.equals(AuditType.DESKTOP))) {
								oldAudit.setAuditStatus(AuditStatus.Expired);
								auditDao.save(oldAudit);
							}
						}
					}
				}

				if (conAudit.getAuditType().isAnnualAddendum()
						&& DateBean.getCurrentYear() - 1 == Integer.parseInt(conAudit.getAuditFor())) {
					// We're activating the most recent year's audit (ie 2008)
					for (ContractorAudit audit : contractor.getAudits()) {
						if (audit.getAuditType().isAnnualAddendum()
								&& Integer.parseInt(audit.getAuditFor()) < DateBean.getCurrentYear() - 3
								&& !audit.getAuditStatus().isExpired()) {
							// Any annual audit before 2006 (ie 2005)
							audit.setAuditStatus(AuditStatus.Expired);
							auditDao.save(audit);
						}
					}
				}
				auditBuilder.buildAudits(contractor);
				emailContractorOnAudit();
			}

			if (conAudit.getExpiresDate() == null && conAudit.getCompletedDate() != null) {
				Date dateToExpire = DateBean.addMonths(conAudit.getCompletedDate(), conAudit.getAuditType()
						.getMonthsToExpire());
				conAudit.setExpiresDate(dateToExpire);
			}

			if (auditStatus.equals(AuditStatus.Submitted)) {
				String notes = "";
				if (conAudit.getAuditType().isPqf()) {
					// Add a note...
					// TODO we should probably stop doing this...it's kind of
					// pointless or at least we should do it for other audits
					// too
					notes = conAudit.getContractorAccount().getName() + " Submitted their PQF ";
				}
				int typeID = conAudit.getAuditType().getId();
				if (typeID == AuditType.DESKTOP || typeID == AuditType.DA) {
					EmailBuilder emailBuilder = new EmailBuilder();

					// TODO combine these 2 templates
					if (typeID == AuditType.DESKTOP)
						emailBuilder.setTemplate(7); // Desktop Submission
					else
						emailBuilder.setTemplate(8); // D&A Submission

					emailBuilder.setPermissions(permissions);
					emailBuilder.setConAudit(conAudit);
					EmailSender.send(emailBuilder.build());

					notes = conAudit.getAuditType().getAuditName()
							+ " Submission email sent for outstanding requirements.";
				} else
					notes = conAudit.getAuditType().getAuditName() + " Submitted";

				addNote(conAudit.getContractorAccount(), notes, NoteCategory.Audits);
			}

			// Save the audit status
			conAudit.changeStatus(auditStatus, getUser());
			auditDao.save(conAudit);

			ContractorAccount contractorAccount = conAudit.getContractorAccount();
			contractor.setNeedsRecalculation(true);
			accountDao.save(contractorAccount);
		}

		return SUCCESS;
	}

	/**
	 * Can the current user submit this audit in its current state?
	 * 
	 * @return
	 */
	public boolean isCanSubmit() {
		if (!isCanEdit())
			return false;
		if (conAudit.getPercentComplete() < 100 && !conAudit.getAuditType().getClassType().isPolicy())
			return false;
		if (conAudit.getAuditStatus().equals(AuditStatus.Pending)) {
			if (permissions.isContractor() && !conAudit.getContractorAccount().isPaymentMethodStatusValid()) {
				return false;
			}
			return true;
		}
		if (conAudit.getAuditType().isPqf()) {
			// PQFs are perpetual audits and can be renewed
			if (permissions.isContractor()) {
				// We don't allow admins to resubmit audits (only contractors)
				if (conAudit.getAuditStatus().equals(AuditStatus.Expired))
					return true;
				if (conAudit.isAboutToExpire())
					return true;
			}
		}
		return false;
	}

	/**
	 * Can the current user submit this audit in its current state?
	 * 
	 * @return
	 */
	public boolean isCanClose() {
		if (permissions.isContractor())
			return false;
		if (!isCanEdit())
			return false;
		if (conAudit.getPercentVerified() < 100)
			return false;
		if (conAudit.getAuditType().isMustVerify())
			return false;
		if (conAudit.getAuditStatus().equals(AuditStatus.Submitted)
				|| conAudit.getAuditStatus().equals(AuditStatus.Resubmitted))
			return true;
		return false;
	}

	public boolean isCanResubmitPolicy() {
		if (!isCanEdit())
			return false;
		if (conAudit.getAuditType().getClassType().isPolicy()) {
			for (ContractorAuditOperator cOperator : conAudit.getOperators()) {
				if (cOperator.getStatus().isRejected())
					return true;
			}
		}

		return false;
	}

	public AuditStatus getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(AuditStatus auditStatus) {
		this.auditStatus = auditStatus;
	}

	public int getOpID() {
		return opID;
	}

	public void setOpID(int opID) {
		this.opID = opID;
	}
}
