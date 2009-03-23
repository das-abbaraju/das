package com.picsauditing.actions.audits;

import java.util.List;

import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditStatus;

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
		if (auditStatus.equals(AuditStatus.Active.toString())) {
			conAudit.changeStatus(AuditStatus.Active, getUser());
			emailContractorOnAudit();
		}
		// TODO add a column to auditData to keep track when the contractor has
		// changed the answer.
		if (auditStatus.equals(AuditStatus.Pending.toString())) {
			conAudit.changeStatus(AuditStatus.Pending, getUser());
			if (conAudit.getAuditType().isPqf()) {
				List<AuditData> temp = auditDataDao.findCustomPQFVerifications(conAudit.getId());
				for (AuditData auditData : temp) {
					AuditCategory auditCategory = auditData.getQuestion().getSubCategory().getCategory();
					for (AuditCatData aCatData : conAudit.getCategories()) {
						if (aCatData.getCategory() == auditCategory && aCatData.getPercentVerified() < 100) {
							aCatData.setRequiredCompleted(aCatData.getRequiredCompleted() - 1);
							aCatData.setPercentCompleted(99);
							catDataDao.save(aCatData);
						}
					}
				}
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
							catDataDao.save(aCatData);
						}
					}
				}
			}
		}
		contractorAuditDAO.save(conAudit);

		return SUCCESS;
	}

	public String getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
	}
}
