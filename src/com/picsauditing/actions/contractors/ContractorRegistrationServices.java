package com.picsauditing.actions.contractors;

import java.util.List;
import java.util.Map;

import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.ContractorAudit;

@SuppressWarnings("serial")
public class ContractorRegistrationServices extends ContractorActionSupport {

	private int auditID = 0;
	private int catDataID = 0;
	private List<AuditQuestion> questions;
	private Map<Integer, AuditData> answerMap;

	private AuditQuestionDAO auditQuestionDAO;
	private AuditDataDAO auditDataDAO;

	public ContractorRegistrationServices(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			AuditQuestionDAO auditQuestionDAO, AuditDataDAO auditDataDAO) {
		super(accountDao, auditDao);
		this.auditQuestionDAO = auditQuestionDAO;
		this.auditDataDAO = auditDataDAO;
		subHeading = "Services Performed";
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		findContractor();
		
		for(ContractorAudit ca : contractor.getAudits()) {
			if (ca.getAuditType().isPqf()) {
				auditID = ca.getId();
				for(AuditCatData catData : ca.getCategories()) {
					if (catData.getCategory().getId() == AuditCategory.SERVICES_PERFORMED) {
						catDataID = catData.getId();
					}
				}
			}
		}
		if (auditID == 0)
			addActionError("PQF hasn't been created yet");
		if (catDataID == 0)
			addActionError("PQF Category for Services Performed hasn't been created yet");

		questions = auditQuestionDAO.findBySubCategory(40);
		answerMap = auditDataDAO.findServicesPerformed(this.id);

		return SUCCESS;
	}

	public int getAuditID() {
		return auditID;
	}

	public int getCatDataID() {
		return catDataID;
	}

	public List<AuditQuestion> getQuestions() {
		return questions;
	}

	public Map<Integer, AuditData> getAnswerMap() {
		return answerMap;
	}

}
