package com.picsauditing.actions.audits;

import java.util.Date;
import java.util.List;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.util.Strings;

/**
 * Class used to edit a ContractorAudit record with virtually no restrictions
 * 
 * @author Trevor
 * 
 */
public class ConAuditSave extends AuditActionSupport {

	protected String auditStatus;

	public ConAuditSave(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, AuditCategoryDataDAO catDataDao,
			AuditDataDAO auditDataDao) {
		super(accountDao, auditDao, catDataDao, auditDataDao);
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		findConAudit();
		String note = "";
		if (auditStatus.equals(AuditStatus.Active.toString())) {
			if(conAudit.getPercentComplete() < 100) 
				return SUCCESS;
			
			conAudit.changeStatus(AuditStatus.Active, getUser());
			note = "Verified and Activated the " + conAudit.getAuditType().getAuditName();
			
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
		}
		// TODO add a column to auditData to keep track when the contractor has
		// changed the answer.
		if (auditStatus.equals(AuditStatus.Incomplete.toString())) {
			conAudit.changeStatus(AuditStatus.Incomplete, getUser());
			if (conAudit.getAuditType().isPqf()) {
				List<AuditData> temp = auditDataDao.findCustomPQFVerifications(conAudit.getId());
				for (AuditData auditData : temp) {
					AuditCategory auditCategory = auditData.getQuestion().getSubCategory().getCategory();
					for (AuditCatData aCatData : conAudit.getCategories()) {
						if (aCatData.getCategory() == auditCategory && aCatData.getPercentVerified() < 100) {
							aCatData.setRequiredCompleted(aCatData.getRequiredCompleted() - 1);
							aCatData.setPercentCompleted(99);
						}
					}
				}
				conAudit.setPercentComplete(99);
				auditDao.save(conAudit);
			}
			if (conAudit.getAuditType().isAnnualAddendum()) {
				for (AuditCatData aCatData : conAudit.getCategories()) {
					if (aCatData.getCategory().getId() == AuditCategory.EMR
							|| aCatData.getCategory().getId() == AuditCategory.GENERAL_INFORMATION
							|| aCatData.getCategory().getId() == AuditCategory.OSHA_AUDIT
							|| aCatData.getCategory().getId() == AuditCategory.LOSS_RUN) {
						if (aCatData.getPercentVerified() < 100) {
							aCatData.setRequiredCompleted(aCatData.getRequiredCompleted() - 1);
							aCatData.setPercentCompleted(99);
							aCatData.setAuditColumns(permissions);
						}
					}
				}
				conAudit.setPercentComplete(99);
				conAudit.setLastRecalculation(new Date());
				auditDao.save(conAudit);
			}
			note = "Rejected " + conAudit.getAuditType().getAuditName();
		}

		if(!Strings.isEmpty(note)) {
			if(!Strings.isEmpty(conAudit.getAuditFor())) 
				note += " " + conAudit.getAuditFor();
			addNote(contractor, note, NoteCategory.Audits, LowMedHigh.Low, true, Account.EVERYONE, getUser());
		}

		conAudit = auditDao.save(conAudit);
		ContractorAccount contractorAccount = conAudit.getContractorAccount();
		contractor.incrementRecalculation();
		accountDao.save(contractorAccount);
		
		return SUCCESS;
	}

	public String getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
	}
}
