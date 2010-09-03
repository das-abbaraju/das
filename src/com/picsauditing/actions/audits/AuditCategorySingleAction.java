package com.picsauditing.actions.audits;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.picsauditing.PICS.AuditPercentCalculator;
import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.CertificateDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.Certificate;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class AuditCategorySingleAction extends AuditActionSupport {

	protected AuditStatus auditStatus = null;
	protected AuditPercentCalculator auditPercentCalculator;
	protected CertificateDAO certificateDao;
	private boolean hasStatusChanged = false;

	protected int opID;
	protected ContractorAuditOperatorDAO caoDAO;
	protected AuditCategoryDAO categoryDAO;
	protected OperatorAccountDAO opDAO;

	public AuditCategorySingleAction(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			ContractorAuditOperatorDAO caoDAO, AuditCategoryDAO categoryDAO, AuditCategoryDataDAO catDataDao,
			AuditDataDAO auditDataDao, AuditPercentCalculator auditPercentCalculator, CertificateDAO certificateDao,
			OperatorAccountDAO opDAO) {
		super(accountDao, auditDao, catDataDao, auditDataDao);
		this.auditPercentCalculator = auditPercentCalculator;
		this.caoDAO = caoDAO;
		this.certificateDao = certificateDao;
		this.categoryDAO = categoryDAO;
		this.opDAO = opDAO;
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
			if (cao.getStatus().isSubmitted())
				return true;
		}
		return false;
	}

	public boolean isHasRejectedCaos() {
		for (ContractorAuditOperator cao : conAudit.getCurrentOperators()) {
			if (cao.getStatus().isIncomplete())
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
	
	// Get certificate
	public Certificate getCertificate(String answerCertID) {
		try {
			int certID = Integer.parseInt(answerCertID);
			
			if (certID > 0)
				return certificateDao.find(certID);
		} catch (Exception e) {
		}
		
		return null;
	}
	
	public OperatorAccount findOperatorByName(String name) {
		List<OperatorAccount> ops = opDAO.findWhere(true, "a.name = '" + name + "'", permissions);
		
		if (ops.size() == 1)
			return ops.get(0);
		
		return null;
	}
}
