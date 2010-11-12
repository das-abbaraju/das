package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.PICS.AuditRuleCache;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.CertificateDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorAuditFileDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.ContractorAuditFile;
import com.picsauditing.util.AnswerMap;

@SuppressWarnings("serial")
public class ContractorAuditFileUpload extends AuditActionSupport {

	protected ContractorAuditFileDAO contractorAuditFileDAO;
	protected AuditQuestionDAO auditQuestionDAO;

	protected List<AuditData> openReqs = null;
	protected int fileID;

	public ContractorAuditFileUpload(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			AuditCategoryDataDAO catDataDao, AuditDataDAO auditDataDao, CertificateDAO certificateDao, ContractorAuditFileDAO contractorAuditFileDAO,
			AuditQuestionDAO auditQuestionDAO, AuditRuleCache auditRuleCache) {
		super(accountDao, auditDao, catDataDao, auditDataDao, certificateDao, auditRuleCache);
		this.contractorAuditFileDAO = contractorAuditFileDAO;
		this.auditQuestionDAO = auditQuestionDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		this.findConAudit();

		if ("Review".equals(button)) {
			if (fileID > 0) {
				ContractorAuditFile contractorAuditFile = contractorAuditFileDAO.find(fileID);
				contractorAuditFile.setReviewed(true);
				contractorAuditFile.setAuditColumns(permissions);
				contractorAuditFileDAO.save(contractorAuditFile);
			}
		}

		return SUCCESS;
	}

	public List<ContractorAuditFile> getAuditFiles() {
		return contractorAuditFileDAO.findByAudit(conAudit.getId());
	}

	public List<AuditData> getOpenReqs() {
		if (openReqs == null) {
			openReqs = new ArrayList<AuditData>();
			AnswerMap answerMap = auditDataDao.findAnswers(auditID);
			for (AuditCatData auditCatData : conAudit.getCategories()) {
				if(auditCatData.isApplies() 
						&& getCategories().get(auditCatData.getCategory().getTopParent()).isApplies()) {
					for (AuditQuestion auditQuestion : auditCatData.getCategory().getQuestions()) {
						if (auditQuestion.isCurrent()) {
							AuditData auditData = answerMap.get(auditQuestion.getId());
							if (auditData != null) {
								if (auditData.isHasRequirements() && auditData.isRequirementOpen()) {
									openReqs.add(auditData);
								}
							}
						}
					}
				}
			}
		}
		return openReqs;
	}

	public int getFileID() {
		return fileID;
	}

	public void setFileID(int fileID) {
		this.fileID = fileID;
	}

	public String getFileDesc(AuditQuestion auditQuestion) {
		return auditQuestion.getCategory().getTopParent().getNumber() + "."
				+ auditQuestion.getCategory().getNumber() + "." + auditQuestion.getNumber();
	}
}