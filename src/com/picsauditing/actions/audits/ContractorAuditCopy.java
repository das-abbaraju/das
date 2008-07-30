package com.picsauditing.actions.audits;

import com.picsauditing.PICS.AuditPercentCalculator;
import com.picsauditing.PICS.FlagCalculator2;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.mail.EmailAuditBean;

/**
 * Used by Audit.action to show a list of categories for a given audit. Also
 * allows users to change the status of an audit.
 * 
 * @author Trevor
 * 
 */
public class ContractorAuditCopy extends ContractorAuditAction {
	protected String contractorSelect = "";
	private boolean hasDuplicate = false;

	public ContractorAuditCopy(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			AuditCategoryDataDAO catDataDao, AuditDataDAO auditDataDao, EmailAuditBean emailAuditBean,
			FlagCalculator2 flagCalculator2, AuditPercentCalculator auditPercentCalculator) {
		super(accountDao, auditDao, catDataDao, auditDataDao, emailAuditBean, flagCalculator2, auditPercentCalculator);
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		permissions.tryPermission(OpPerms.AuditCopy);
		this.findConAudit();

		if (button != null) {
			ContractorAccount nConAccount = accountDao.findConID(contractorSelect);

			for (ContractorAudit existingAudit : nConAccount.getAudits()) {
				if (existingAudit.getAuditType().equals(conAudit.getAuditType())
						&& !existingAudit.getAuditStatus().equals(AuditStatus.Expired)) {
					// We already have an existing audit that we should delete
					// first
					this
							.addActionMessage(contractorSelect + " already has a "
									+ conAudit.getAuditType().getAuditName());
					if ("Copy Audit".equals(button)) {
						hasDuplicate = true;
						return SUCCESS;
					}
					// TODO delete the old audit for con2
					// be sure to remove pqfcatdata and pqfdata
					auditDao.clear();
					auditDao.remove(existingAudit.getId());
				}
			}
			// copy audit now
			auditDao.copy(conAudit, nConAccount);
			return "Audit";
		}

		return SUCCESS;
	}

	public String getContractorSelect() {
		return contractorSelect;
	}

	public void setContractorSelect(String contractor_select) {
		this.contractorSelect = contractor_select;
	}

	public boolean isHasDuplicate() {
		return hasDuplicate;
	}
}
