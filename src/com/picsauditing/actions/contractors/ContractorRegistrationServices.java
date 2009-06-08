package com.picsauditing.actions.contractors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	private List<AuditQuestion> infoQuestions;
	private List<AuditQuestion> serviceQuestions;
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

		if ("calculateRisk".equals(button)) {
			// TODO calculate the risk level
			// we may need to move some of the below items before

			// TODO redirect to facilities
			redirect("ContractorFacilities.action");
			return BLANK;
		}

		for (ContractorAudit ca : contractor.getAudits()) {
			if (ca.getAuditType().isPqf()) {
				auditID = ca.getId();
				for (AuditCatData catData : ca.getCategories()) {
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

		Set<Integer> questionIds = new HashSet<Integer>();
		infoQuestions = auditQuestionDAO.findWhere("subCategory.id = 269 OR id = 69");
		for(AuditQuestion q : infoQuestions)
			questionIds.add(q.getId());
		serviceQuestions = auditQuestionDAO.findBySubCategory(40);
		for(AuditQuestion q : serviceQuestions)
			questionIds.add(q.getId());

		Map<Integer, Map<String, AuditData>> indexedResult = auditDataDAO.findAnswersByContractor(id, questionIds);
		answerMap = new HashMap<Integer, AuditData>();
		for (Integer questionID : indexedResult.keySet())
			answerMap.put(questionID, indexedResult.get(questionID).get(""));
		return SUCCESS;
	}

	public int getAuditID() {
		return auditID;
	}

	public int getCatDataID() {
		return catDataID;
	}

	public List<AuditQuestion> getInfoQuestions() {
		return infoQuestions;
	}

	public List<AuditQuestion> getServiceQuestions() {
		return serviceQuestions;
	}

	public Map<Integer, AuditData> getAnswerMap() {
		return answerMap;
	}

}
