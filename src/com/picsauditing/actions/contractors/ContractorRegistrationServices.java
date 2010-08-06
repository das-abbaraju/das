package com.picsauditing.actions.contractors;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.util.Strings;

import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.LowMedHigh;

@SuppressWarnings("serial")
public class ContractorRegistrationServices extends ContractorActionSupport {

	private int auditID = 0;
	private int catDataID = 0;
	private int requestID = 0;
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
		infoQuestions = auditQuestionDAO.findWhere("subCategory.id = 269");
		for (AuditQuestion q : infoQuestions)
			questionIds.add(q.getId());
		serviceQuestions = auditQuestionDAO.findBySubCategory(40);
		for (AuditQuestion q : serviceQuestions)
			questionIds.add(q.getId());

		answerMap = auditDataDAO.findAnswersByContractor(id, questionIds);

		if ("calculateRisk".equals(button)) {
			if (contractor.getRiskLevel() == null) {
				boolean requiredQuestions = false;
				boolean performServices = false;
				if (answerMap != null) {
					for (AuditQuestion aq : infoQuestions) {
						if (answerMap.get(aq.getId()) == null) {
							requiredQuestions = false;
							break;
						} else
							requiredQuestions = true;
					}
					for (AuditQuestion aq : serviceQuestions) {
						if (answerMap.get(aq.getId()) != null
								&& !Strings.isEmpty(answerMap.get(aq.getId()).getAnswer())) {
							performServices = true;
							break;
						}
					}
				}
				if (!requiredQuestions)
					addActionError("Please answer all the questions on the General Info section");
				if (!performServices)
					addActionError("Please select the services you perform below");
				if (requiredQuestions && performServices) {
					Collection<AuditData> auditList = answerMap.values();
					LowMedHigh riskLevel = LowMedHigh.Low;
					for (AuditData auditData : auditList) {
						AuditQuestion q = auditData.getQuestion();
						if (q.getAuditCategory().getId() == 269) {
							// Subcategory is RISK ASSESSMENT
							AuditData aData = answerMap.get(q.getId());
							riskLevel = getRiskLevel(aData, riskLevel);
						} else if (auditData.getAnswer().startsWith("C")) {
							// Self Performed Services
							riskLevel = getMaxRiskLevel(riskLevel, q.getRiskLevel());
						}
						if (riskLevel.equals(LowMedHigh.High))
							break;
					}
					contractor.setRiskLevel(riskLevel);
					contractor.setAuditColumns(permissions);
					accountDao.save(contractor);
					redirect("ContractorFacilities.action?id=" + contractor.getId() + 
							(requestID > 0 ? "&requestID=" + requestID : ""));
					return BLANK;
				}
			}
		}
		return SUCCESS;
	}

	public int getAuditID() {
		return auditID;
	}

	public int getCatDataID() {
		return catDataID;
	}
	
	public int getRequestID() {
		return requestID;
	}
	
	public void setRequestID(int requestID) {
		this.requestID = requestID;
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

	public LowMedHigh getMaxRiskLevel(LowMedHigh oRiskLevel, LowMedHigh qRiskLevel) {
		if (oRiskLevel.compareTo(qRiskLevel) < 0)
			return qRiskLevel;
		return oRiskLevel;
	}

	public LowMedHigh getRiskLevel(AuditData auditData, LowMedHigh riskLevel) {
		if (auditData != null && !auditData.getAnswer().equals(riskLevel)) {
			if (auditData.getQuestion().getId() == 2442 || auditData.getQuestion().getId() == 2445) {
				// Question : Does your company perform mechanical services
				// OR Services conducted at heights greater than six feet?
				if (auditData.getAnswer().equals("Yes"))
					return LowMedHigh.High;
			}
			if (auditData.getQuestion().getId() == 3793) {
				// Question : Does your company perform mechanical services
				// that require the use of hand/power tools?
				if (auditData.getAnswer().equals("Yes"))
					return getMaxRiskLevel(riskLevel, LowMedHigh.Med);
			}
			if (auditData.getQuestion().getId() == 2443) {
				// Question : Does your company perform all services from only
				// an office?
				if (auditData.getAnswer().equals("No"))
					return getMaxRiskLevel(riskLevel, LowMedHigh.Med);
			}
			if (auditData.getQuestion().getId() == 2444) {
				// Question : What risk level do you believe your company should
				// be rated?
				if (auditData.getAnswer().equals("Med"))
					return getMaxRiskLevel(riskLevel, LowMedHigh.Med);
				if (auditData.getAnswer().equals("High"))
					return LowMedHigh.High;
			}
		}
		return riskLevel;
	}
}
