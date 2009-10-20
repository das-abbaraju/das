package com.picsauditing.actions.audits;

import java.util.Date;
import java.util.List;

import org.jboss.util.Strings;

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
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.NoteCategory;

/**
 * Class used to edit a ContractorAudit record with virtually no restrictions
 * 
 * @author Trevor
 * 
 */
public class ConAuditSave extends AuditActionSupport {

	protected ContractorAuditDAO contractorAuditDAO;
	protected String auditStatus;

	public ConAuditSave(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, AuditCategoryDataDAO catDataDao,
			AuditDataDAO auditDataDao, ContractorAuditDAO contractorAuditDAO) {
		super(accountDao, auditDao, catDataDao, auditDataDao);
		this.contractorAuditDAO = contractorAuditDAO;
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
		conAudit = contractorAuditDAO.save(conAudit);
		ContractorAccount contractorAccount = conAudit.getContractorAccount();
		contractor.setNeedsRecalculation(true);
		accountDao.save(contractorAccount);
		
		if(!Strings.isEmpty(note)) {
			if(!Strings.isEmpty(conAudit.getAuditFor())) 
				note += " " + conAudit.getAuditFor();
			addNote(contractor, note, NoteCategory.Audits, LowMedHigh.Low, true, Account.PicsID, getUser());
		}
		
		
		return SUCCESS;
	}

	public String getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
	}
}
