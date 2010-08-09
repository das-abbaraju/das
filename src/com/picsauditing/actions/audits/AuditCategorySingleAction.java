package com.picsauditing.actions.audits;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.picsauditing.PICS.AuditBuilder;
import com.picsauditing.PICS.AuditPercentCalculator;
import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.CertificateDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.CaoStatus;
import com.picsauditing.jpa.entities.Certificate;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class AuditCategorySingleAction extends AuditActionSupport {

	protected AuditStatus auditStatus = null;
	protected AuditPercentCalculator auditPercentCalculator;
	protected CertificateDAO certificateDao;
	protected AuditBuilder auditBuilder;
	private boolean hasStatusChanged = false;

	protected int opID;
	protected ContractorAuditOperatorDAO caoDAO;
	protected AuditCategoryDAO categoryDAO;

	public AuditCategorySingleAction(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			ContractorAuditOperatorDAO caoDAO, AuditCategoryDAO categoryDAO, AuditCategoryDataDAO catDataDao,
			AuditDataDAO auditDataDao, AuditPercentCalculator auditPercentCalculator, AuditBuilder auditBuilder,
			CertificateDAO certificateDao) {
		super(accountDao, auditDao, catDataDao, auditDataDao);
		this.auditPercentCalculator = auditPercentCalculator;
		this.auditBuilder = auditBuilder;
		this.caoDAO = caoDAO;
		this.certificateDao = certificateDao;
		this.categoryDAO = categoryDAO;
	}

	public String execute() throws Exception {

		if (auditStatus != null)
			hasStatusChanged = true;

		// Calculate and set the percent complete
		if (conAudit.getLastRecalculation() == null) {
			auditPercentCalculator.percentCalculateComplete(conAudit, true);
			conAudit.setLastRecalculation(new Date());
			auditDao.save(conAudit);
		} else
			auditPercentCalculator.percentCalculateComplete(conAudit, conAudit.getAuditType().getClassType().equals(
					AuditTypeClass.IM));

		if ("Submit".equals(button)) {
			hasStatusChanged = true;
			if (conAudit.getPercentComplete() < 100) {
				addActionError("Please complete the audit before you submit");
				return SUCCESS;
			}
			if (conAudit.getAuditType().getClassType().isPolicy()) {
				if (conAudit.getPercentComplete() == 100 && !conAudit.getAuditStatus().isExpired()) {
					ContractorAuditOperator cao = caoDAO.find(conAudit.getId(), opID);
					if (cao != null) {
						cao.setStatus(CaoStatus.Submitted);
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
			} else if (conAudit.getAuditType().isPqf()) {
				if (conAudit.getAuditStatus().isActive() && conAudit.getPercentVerified() == 100) {
					// If the PQF is being resubmitted, but it's already
					// verified and active, we don't need to reverify
					auditStatus = AuditStatus.Active;
				} else if (conAudit.getAuditStatus().isActiveResubmittedExempt())
					auditStatus = AuditStatus.Resubmitted;
				else
					auditStatus = AuditStatus.Submitted;
			} else if (conAudit.getAuditType().isHasRequirements() || conAudit.getAuditType().isMustVerify())
				auditStatus = AuditStatus.Submitted;
			else
				auditStatus = AuditStatus.Active;
			conAudit.setCompletedDate(new Date());
		}

		if ("Resubmit".equals(button)) {
			// TODO: find out where we use this
			hasStatusChanged = true;
			if (conAudit.getAuditType().getClassType().isPolicy()) {
				ContractorAuditOperator cao = caoDAO.find(conAudit.getId(), opID);
				if (cao != null) {
					cao.setStatus(CaoStatus.Submitted);
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

		if (!hasStatusChanged)
			return SUCCESS;

		// We're changing the status
		if (auditStatus.equals(AuditStatus.Active)) {
			conAudit.setClosedDate(new Date());
			if (!conAudit.getAuditType().isHasMultiple()) {
				// This audit can only have one active audit, expire the
				// previous one
				for (ContractorAudit oldAudit : conAudit.getContractorAccount().getAudits()) {
					if (!oldAudit.equals(conAudit)) {
						if (oldAudit.getAuditType().equals(conAudit.getAuditType())) {
							oldAudit.changeStatus(AuditStatus.Expired, getUser());
							auditDao.save(oldAudit);
						}
					}
				}
			}
		}

		if (auditStatus.equals(AuditStatus.Submitted)) {
			String notes = conAudit.getAuditType().getAuditName() + " Submitted";
			if (!Strings.isEmpty(conAudit.getAuditFor()))
				notes += " for " + conAudit.getAuditFor();

			if (conAudit.getAuditType().getTemplate() != null) {
				EmailBuilder emailBuilder = new EmailBuilder();
				emailBuilder.setTemplate(conAudit.getAuditType().getTemplate());
				emailBuilder.setPermissions(permissions);
				emailBuilder.setConAudit(conAudit);
				if (conAudit.getAuditType().getClassType().isAudit())
					emailBuilder.setFromAddress("\"PICS Auditing\"<audits@picsauditing.com>");
				EmailSender.send(emailBuilder.build());

				notes += " and email sent to " + emailBuilder.getSentTo();
			}

			addNote(conAudit.getContractorAccount(), notes, NoteCategory.Audits, getViewableByAccount(conAudit
					.getAuditType().getAccount()));
		}

		if (auditStatus.equals(AuditStatus.Active)) {
			if (conAudit.getAuditType().isHasRequirements()) {
				EmailBuilder emailBuilder = new EmailBuilder();
				emailBuilder.setTemplate(81); // Audit Completed
				emailBuilder.setPermissions(permissions);
				emailBuilder.setConAudit(conAudit);
				emailBuilder.setFromAddress("\"PICS Auditing\"<audits@picsauditing.com>");
				EmailQueue email = emailBuilder.build();
				email.setViewableById(getViewableByAccount(conAudit.getAuditType().getAccount()));
				EmailSender.send(email);
			}
			addNote(conAudit.getContractorAccount(), "Closed the requirements and Activated the "
					+ conAudit.getAuditType().getAuditName(), NoteCategory.Audits, getViewableByAccount(conAudit
					.getAuditType().getAccount()));
		}

		conAudit.changeStatus(auditStatus, getUser());
		auditDao.save(conAudit);

		ContractorAccount contractorAccount = conAudit.getContractorAccount();
		contractor.incrementRecalculation();
		accountDao.save(contractorAccount);

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
		if (conAudit.getPercentComplete() < 100)
			return false;
		if (conAudit.getAuditStatus().isPending() || conAudit.getAuditStatus().isIncomplete()) {
			if (permissions.isContractor() && !conAudit.getContractorAccount().isPaymentMethodStatusValid()
					&& conAudit.getContractorAccount().isMustPayB()) {
				return false;
			}
			return true;
		}
		if (conAudit.getAuditType().getClassType().isPqf()) {
			// PQFs are perpetual audits and can be renewed
			if (permissions.isContractor()) {
				// We don't allow admins to resubmit audits (only contractors)
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

	public List<Certificate> getCertificates() {
		return certificateDao.findByConId(contractor.getId(), permissions, false);
	}

	public boolean isHasPendingCaos() {
		for (ContractorAuditOperator cao : conAudit.getCurrentOperators()) {
			if (cao.getStatus().isPending())
				return true;
		}
		return false;
	}

	public boolean isHasSubmittedCaos() {
		for (ContractorAuditOperator cao : conAudit.getCurrentOperators()) {
			if (cao.getStatus() == CaoStatus.Submitted)
				return true;
		}
		return false;
	}

	public boolean isHasRejectedCaos() {
		for (ContractorAuditOperator cao : conAudit.getCurrentOperators()) {
			if (cao.getStatus().isRejected())
				return true;
		}
		return false;
	}

	public boolean isCanSubmitPolicy() {
		if (!isCanEdit())
			return false;

		if (permissions.isContractor() && !conAudit.getContractorAccount().isPaymentMethodStatusValid()
				&& conAudit.getContractorAccount().isMustPayB())
			return false;
		return true;

	}

	public List<AuditCategory> getAuditCategories() {
		return categoryDAO.findByAuditTypeID(conAudit.getAuditType().getId());
	}

	public Map<AuditCategory, AuditCatData> getCatDataMap() {
		return catDataDao.findByAuditMap(conAudit, permissions);
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
