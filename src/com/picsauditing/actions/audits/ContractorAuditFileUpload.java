package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.picsauditing.PICS.AuditCategoryRuleCache;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.CertificateDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorAuditFileDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
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
			AuditQuestionDAO auditQuestionDAO, AuditCategoryRuleCache auditCategoryRuleCache) {
		super(accountDao, auditDao, catDataDao, auditDataDao, certificateDao, auditCategoryRuleCache);
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
			Set<AuditData> openReqsSet = new TreeSet<AuditData>(AuditData.getQuestionComparator());
			AnswerMap answerMap = auditDataDao.findAnswers(auditID);
			Date validDate = conAudit.getValidDate();
			for (AuditCatData auditCatData : conAudit.getCategories()) {
				if(auditCatData.isApplies() 
						&& getCategories().get(auditCatData.getCategory().getTopParent()).isApplies()) {
					for (AuditCategory child : auditCatData.getCategory().getChildren()) {
						for (AuditQuestion auditQuestion : child.getQuestions()) {
							if (auditQuestion.isValidQuestion(validDate)) {
								AuditData auditData = answerMap.get(auditQuestion.getId());
								if (auditData != null) {
									if (auditData.isHasRequirements() && auditData.isRequirementOpen()) {
										openReqsSet.add(auditData);
									}
								}
							}
						}
					}
				}
			}
			openReqs = new ArrayList<AuditData>(openReqsSet);
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
		return auditQuestion.getExpandedNumber();
	}
}