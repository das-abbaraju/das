package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.ContractorAuditFileDAO;
import com.picsauditing.jpa.entities.AuditCatData;
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
	private AnswerMap answerMap = null;
	private Set<AuditData> openReqsSet;
	private Set<AuditData> closedReqsSet;

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
			loadRequirements();
		}
		return openReqs;
	}

	public List<AuditData> getClosedReqs() {
		if (closedReqs == null) {
			loadRequirements();
		}
		return closedReqs;
	}
	
	private void loadRequirements() {
		openReqsSet = new TreeSet<AuditData>(AuditData.getQuestionComparator());
		closedReqsSet = new TreeSet<AuditData>(AuditData.getQuestionComparator());

		if (answerMap == null)
			answerMap = auditDataDAO.findAnswers(auditID);
		
		for (AuditCatData auditCatData : conAudit.getCategories()) {
			AuditCatData parentCatData = getCategories().get(auditCatData.getCategory().getTopParent());
			boolean parentCatDataAppliesOrDoesntExist = (parentCatData == null) ? true : parentCatData.isApplies();
			if (auditCatData.isApplies() && parentCatDataAppliesOrDoesntExist) {
				loadRequirementsQuestions(auditCatData);
			}
		}
		openReqs = new ArrayList<AuditData>(openReqsSet);
		closedReqs = new ArrayList<AuditData>(closedReqsSet);
	}

	private void loadRequirementsQuestions(AuditCatData auditCatData) {
		for (AuditQuestion auditQuestion : auditCatData.getCategory().getQuestions()) {
			AuditData auditData = answerMap.get(auditQuestion.getId());
			if (auditQuestion.isValidQuestion(conAudit.getValidDate()) && auditData != null) {
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