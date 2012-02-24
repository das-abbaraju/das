package com.picsauditing.actions.audits;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.PICSFileType;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.dao.ContractorAuditFileDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.dao.ContractorAuditOperatorWorkflowDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditFile;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorAuditOperatorPermission;
import com.picsauditing.jpa.entities.ContractorAuditOperatorWorkflow;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.util.FileUtils;

/**
 * Used by Audit.action to show a list of categories for a given audit. Also allows users to change the status of an
 * audit.
 * 
 */
@SuppressWarnings("serial")
public class ContractorAuditCopy extends AuditActionSupport {

	protected String contractorSelect = "";
	private boolean hasDuplicate = false;
	@Autowired
	private ContractorAuditOperatorWorkflowDAO caowDAO;
	@Autowired
	private ContractorAuditOperatorDAO caoDAO;
	@Autowired
	private ContractorAuditFileDAO contractorAuditFileDAO;

	@RequiredPermission(value = OpPerms.AuditCopy)
	public String execute() throws Exception {
		this.findConAudit();
		int oldconID = conAudit.getContractorAccount().getId();
		if (button != null) {
			ContractorAccount newConAudit = contractorAccountDao.findConID(contractorSelect);
			if (newConAudit == null) {
				addActionError("No Contractor Found");
				return SUCCESS;
			}
			List<ContractorAudit> auditList = newConAudit.getAudits();
			for (ContractorAudit existingAudit : auditList) {
				if (existingAudit.getAuditType().equals(conAudit.getAuditType()) && !existingAudit.isExpired()
						&& !existingAudit.getAuditType().isAnnualAddendum()) {
					// We already have an existing audit that we should delete
					// first
					this.addActionMessage(contractorSelect + " already has a "
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
			auditDao.copy(conAudit, newConAudit, preToPostAuditDataIdMapper);

			copyAuditQuestionFiles(preToPostAuditDataIdMapper);
			
			copyAuditFiles();			

			String notes = conAudit.getAuditType().getName().toString() + " Copied from Contractor " + oldconID;
			addNote(conAudit.getContractorAccount(), notes, NoteCategory.Audits, getViewableByAccount(conAudit
					.getAuditType().getAccount()));

			return "Audit";
		}

		return SUCCESS;
	}

	private void copyAuditQuestionFiles(Map<Integer, AuditData> preToPostAuditDataIdMapper) {
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
	}

	private void copyAuditFiles() {
		List<ContractorAuditFile> auditFiles = contractorAuditFileDAO.findByAudit(auditID);
		for (ContractorAuditFile caf : auditFiles) {
			ContractorAuditFile contractorAuditFile = new ContractorAuditFile();

			contractorAuditFile.setAudit(conAudit);
			contractorAuditFile.setReviewed(caf.isReviewed());
			contractorAuditFile.setDescription(caf.getDescription());
			contractorAuditFile.setFileType(caf.getFileType());
			contractorAuditFile.setAuditColumns(permissions);

			contractorAuditFile = contractorAuditFileDAO.save(contractorAuditFile);

			int fileId = contractorAuditFile.getId();

			String newFileBase = "files/" + FileUtils.thousandize(fileId);
			String newFileName = getFileName(fileId);

			File[] files = getFiles(caf.getId());
			if (files != null && files.length == 1) {
				try {
					FileUtils.copyFile(files[0], getFtpDir(), newFileBase, newFileName, caf.getFileType(), true);
				} catch (Exception couldntCopyTheFile) {
					couldntCopyTheFile.printStackTrace();
				}
			}
		}
	}
	
	private File[] getFiles(int fileID) {
		File dir = new File(getFtpDir() + "/files/" + FileUtils.thousandize(fileID));
		return FileUtils.getSimilarFiles(dir, getFileName(fileID));
	}

	private String getFileName(int fileID) {
		return PICSFileType.audit + "_" + fileID;
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
