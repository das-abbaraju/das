package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.ContractorAuditFileDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.ContractorAuditFile;
import com.picsauditing.util.AnswerMap;

@SuppressWarnings("serial")
public class ContractorAuditFileUpload extends AuditActionSupport {

	@Autowired
	protected ContractorAuditFileDAO contractorAuditFileDAO;
	@Autowired
	protected AuditQuestionDAO auditQuestionDAO;

	protected List<AuditData> openReqs = null;
	protected List<AuditData> closedReqs = null;
	protected int fileID;

	public String execute() throws Exception {
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
			loadReqs();
		}
		return openReqs;
	}

	public List<AuditData> getClosedReqs() {
		if (closedReqs == null) {
			loadReqs();
		}
		return closedReqs;
	}
	
	private void loadReqs() {
		Set<AuditData> openReqsSet = new TreeSet<AuditData>(AuditData.getQuestionComparator());
		Set<AuditData> closedReqsSet = new TreeSet<AuditData>(AuditData.getQuestionComparator());
		
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
								if (auditData.isHasRequirements()) {
									if (auditData.isRequirementOpen()) {
										openReqsSet.add(auditData);
									} else {
										closedReqsSet.add(auditData);
									}
								}
							}
						}
					}
				}
			}
		}
		openReqs = new ArrayList<AuditData>(openReqsSet);
		closedReqs = new ArrayList<AuditData>(closedReqsSet);
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