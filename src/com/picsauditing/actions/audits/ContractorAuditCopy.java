package com.picsauditing.actions.audits;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.picsauditing.PICS.AuditCategoryRuleCache;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.CertificateDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.dao.ContractorAuditOperatorWorkflowDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorAuditOperatorPermission;
import com.picsauditing.jpa.entities.ContractorAuditOperatorWorkflow;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.util.FileUtils;

/**
 * Used by Audit.action to show a list of categories for a given audit. Also
 * allows users to change the status of an audit.
 * 
 */
@SuppressWarnings("serial")
public class ContractorAuditCopy extends AuditActionSupport {

	protected String contractorSelect = "";
	private boolean hasDuplicate = false;
	private ContractorAuditOperatorWorkflowDAO caowDAO;
	private ContractorAuditOperatorDAO caoDAO;

	public ContractorAuditCopy(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			AuditCategoryDataDAO catDataDao, AuditDataDAO auditDataDao, CertificateDAO certificateDao,
			AuditCategoryRuleCache auditCategoryRuleCache, ContractorAuditOperatorWorkflowDAO caowDAO,
			ContractorAuditOperatorDAO caoDAO) {
		super(accountDao, auditDao, catDataDao, auditDataDao, certificateDao, auditCategoryRuleCache);
		this.caowDAO = caowDAO;
		this.caoDAO = caoDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		permissions.tryPermission(OpPerms.AuditCopy);
		this.findConAudit();
		int oldconID = conAudit.getContractorAccount().getId();
		if (button != null) {
			ContractorAccount nConAccount = accountDao.findConID(contractorSelect);
			if (nConAccount == null) {
				addActionError("No Contractor Found");
				return SUCCESS;
			}
			List<ContractorAudit> auditList = nConAccount.getAudits();
			for (ContractorAudit existingAudit : auditList) {
				if (existingAudit.getAuditType().equals(conAudit.getAuditType()) && !existingAudit.isExpired()
						&& !existingAudit.getAuditType().isAnnualAddendum()) {
					// We already have an existing audit that we should delete
					// first
					this
							.addActionMessage(contractorSelect + " already has a "
									+ conAudit.getAuditType().getName().toString());
					if ("Copy Audit".equals(button)) {
						hasDuplicate = true;
						return SUCCESS;
					}
					for (Iterator<ContractorAuditOperator> caoIT = existingAudit.getOperators().iterator(); caoIT
							.hasNext();) {
						ContractorAuditOperator cao = caoIT.next();
						List<ContractorAuditOperatorWorkflow> caowList = cao.getCaoWorkflow();
						for (Iterator<ContractorAuditOperatorWorkflow> it = caowList.iterator(); it.hasNext();) {
							ContractorAuditOperatorWorkflow t = it.next();
							it.remove();
							caowDAO.remove(t);
						}

						for (Iterator<ContractorAuditOperatorPermission> it = cao.getCaoPermissions().iterator(); it
								.hasNext();) {
							ContractorAuditOperatorPermission t = it.next();
							it.remove();
							caoDAO.remove(t);
						}
						caoIT.remove();
						caoDAO.remove(cao);
					}
					auditDao.clear();
					auditDao.remove(existingAudit.getId(), getFtpDir());
				}
			}

			// copy audit now
			findConAudit();

			Map<Integer, AuditData> preToPostAuditDataIdMapper = new HashMap<Integer, AuditData>();
			auditDao.copy(conAudit, nConAccount, preToPostAuditDataIdMapper);

			ContractorAudit oldConAudit = auditDao.find(auditID);

			for (AuditData auditData : oldConAudit.getData()) {

				if (auditData.getQuestion().getQuestionType().equals("File")) {

					AuditData newAnswer = preToPostAuditDataIdMapper.get(auditData.getId());

					String newFileBase = "files/"
							+ FileUtils.thousandize(preToPostAuditDataIdMapper.get(auditData.getId()).getId());
					String newFileName = "data_" + auditData.getId();

					String oldFileBase = "files/" + FileUtils.thousandize(auditData.getId());
					String oldFileName = "data_" + auditData.getId() + "." + newAnswer.getAnswer();

					File oldFile = new File(getFtpDir() + "/" + oldFileBase, oldFileName);
					try {
						FileUtils.copyFile(oldFile, getFtpDir(), newFileBase, newFileName, newAnswer.getAnswer(), true);
					} catch (Exception couldntCopyTheFile) {
						couldntCopyTheFile.printStackTrace();
					}
				}
			}

			String notes = conAudit.getAuditType().getName().toString() + " Copied from Contractor " + oldconID;
			addNote(conAudit.getContractorAccount(), notes, NoteCategory.Audits, getViewableByAccount(conAudit
					.getAuditType().getAccount()));

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
