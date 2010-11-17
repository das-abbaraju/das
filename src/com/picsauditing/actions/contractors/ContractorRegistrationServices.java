package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.util.Strings;

import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.User;

@SuppressWarnings("serial")
public class ContractorRegistrationServices extends ContractorActionSupport {

	private int auditID = 0;
	private int catDataID = 0;
	private int requestID = 0;
	private List<AuditQuestion> infoQuestions = new ArrayList<AuditQuestion>();
	private List<AuditQuestion> serviceQuestions = new ArrayList<AuditQuestion>();
	private List<AuditCategory> categories;
	private Map<Integer, AuditData> answerMap;
	private AuditData auditData;
	private ContractorAudit conAudit;

	private AuditCategoryDAO auditCateoryDAO;
	private AuditQuestionDAO auditQuestionDAO;
	private AuditDataDAO auditDataDAO;

	public ContractorRegistrationServices(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			AuditCategoryDAO auditCategoryDAO, AuditQuestionDAO auditQuestionDAO, AuditDataDAO auditDataDAO) {
		super(accountDao, auditDao);
		this.auditCateoryDAO = auditCategoryDAO;
		this.auditQuestionDAO = auditQuestionDAO;
		this.auditDataDAO = auditDataDAO;
		subHeading = "Services Performed";
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		findContractor();

		auditData = new AuditData();
		for (ContractorAudit ca : contractor.getAudits()) {
			if (ca.getAuditType().isPqf()) {
				conAudit = ca;
				auditID = ca.getId();
				// Prepare auditdata
				auditData.setAudit(new ContractorAudit());
				auditData.getAudit().setId(auditID);
				for (AuditCatData catData : ca.getCategories()) {
					if (catData.getCategory().getId() == AuditCategory.SERVICES_PERFORMED) {
						catDataID = catData.getId();
					}
				}
			}
		}

		// Missing PQF and category data -- just create it now
		if (auditID == 0 && catDataID == 0)
			createPQF();

		Set<Integer> questionIds = new HashSet<Integer>();
		categories = auditCateoryDAO.findWhere("id IN (400, 422)");
		for (AuditCategory category : categories) {
			if (category.getId() == 400) {
				for (AuditQuestion question : category.getQuestions()) {
					infoQuestions.add(question);
					questionIds.add(question.getId());
				}
			} else if (category.getId() == 422) {
				for (AuditQuestion question : category.getQuestions()) {
					serviceQuestions.add(question);
					questionIds.add(question.getId());
				}
			}
		}

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
						if (q.getCategory().getId() == 269) {
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
					redirect("ContractorFacilities.action?id=" + contractor.getId()
							+ (requestID > 0 ? "&requestID=" + requestID : ""));
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

	public List<AuditCategory> getCategories() {
		return categories;
	}

	public void setCategories(List<AuditCategory> categories) {
		this.categories = categories;
	}

	public Map<Integer, AuditData> getAnswerMap() {
		return answerMap;
	}

	public AuditData getAuditData() {
		return auditData;
	}

	public void setAuditData(AuditData auditData) {
		this.auditData = auditData;
	}

	public ContractorAudit getConAudit() {
		return conAudit;
	}

	public boolean isViewBlanks() {
		return true;
	}

	public String getMode() {
		return "Edit";
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

	private void createPQF() {
		// Create a blank PQF for this contractor
		ContractorAudit audit = new ContractorAudit();
		audit.setContractorAccount(contractor);
		audit.setAuditType(new AuditType(1));
		audit.setAuditColumns(new User(User.SYSTEM));
		addAuditCategories(audit, 2); // COMPANY INFORMATION
		addAuditCategories(audit, 8); // GENERAL INFORMATION
		addAuditCategories(audit, AuditCategory.SERVICES_PERFORMED);
		addAuditCategories(audit, 184); // SUPPLIER DIVERSITY
		conAudit = auditDao.save(audit);
		auditID = audit.getId();

		auditData.setAudit(audit);

		for (AuditCatData data : audit.getCategories()) {
			if (data.getCategory().getId() == AuditCategory.SERVICES_PERFORMED) {
				catDataID = data.getId();
				break;
			}
		}
	}

	private void addAuditCategories(ContractorAudit audit, int CategoryID) {
		AuditCatData catData = new AuditCatData();
		catData.setCategory(new AuditCategory());
		catData.getCategory().setId(CategoryID);
		catData.setAudit(audit);
		catData.setApplies(true);
		catData.setOverride(false);
		catData.setNumRequired(1);
		catData.setAuditColumns(new User(User.SYSTEM));
		audit.getCategories().add(catData);
	}
}
