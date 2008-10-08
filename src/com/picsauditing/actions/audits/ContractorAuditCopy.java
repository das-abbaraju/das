package com.picsauditing.actions.audits;

import java.io.File;
import java.util.List;
import java.util.Vector;

import com.picsauditing.PICS.AuditBuilder;
import com.picsauditing.PICS.AuditPercentCalculator;
import com.picsauditing.PICS.ContractorBean;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.FlagCalculator2;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.util.FileUtils;

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
			AuditCategoryDataDAO catDataDao, AuditDataDAO auditDataDao, 
			FlagCalculator2 flagCalculator2, AuditPercentCalculator auditPercentCalculator, AuditBuilder auditBuilder) {
		super(accountDao, auditDao, catDataDao, auditDataDao, flagCalculator2, auditPercentCalculator,
				auditBuilder);
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		permissions.tryPermission(OpPerms.AuditCopy);
		this.findConAudit();
		int oldconID = conAudit.getContractorAccount().getId();
		if (button != null) {
			ContractorAccount nConAccount = accountDao.findConID(contractorSelect);
			List<ContractorAudit> auditList = new Vector<ContractorAudit>(nConAccount.getAudits());
			auditDao.clear();
			for (ContractorAudit existingAudit : auditList) {
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
					// be sure o remove pqfcatdata and pqfdata
					auditDao.remove(existingAudit.getId(), getFtpDir());
				}
			}
			// copy audit now
			findConAudit();
			auditDao.copy(conAudit, nConAccount);

			for (AuditData auditData : conAudit.getData()) {
				if (auditData.getQuestion().getQuestionType().equals("File")) {
					String FileName = getFtpDir() + "/files/pqf/qID_" + auditData.getQuestion().getQuestionID() + "/"
							+ auditData.getQuestion().getQuestionID() + "_";
					File oldFile = new File(FileName + oldconID + "." + auditData.getAnswer());
					File newFile = new File(FileName + conAudit.getContractorAccount().getId() + "."
							+ auditData.getAnswer());
					if (oldFile.exists())
						FileUtils.copyFile(oldFile, newFile);
				}
			}
			ContractorBean cBean = new ContractorBean();
			cBean.setFromDB(conAudit.getContractorAccount().getIdString());
			String notes = conAudit.getAuditType().getAuditName() + " Copied from Contractor " + oldconID;
			cBean.addNote(conAudit.getContractorAccount().getIdString(), permissions.getName(), notes, DateBean
					.getTodaysDate());
			cBean.writeToDB();
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
