package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.User;

@SuppressWarnings("serial")
public class ContractorRegistrationServices extends ContractorActionSupport {

	private int pqfID = 0;
	private int catDataID = 0;
	private int requestID = 0;
	private List<AuditQuestion> infoQuestions = new ArrayList<AuditQuestion>();
	private List<AuditCategory> categories;
	private Map<Integer, AuditData> answerMap;
	private AuditData auditData;
	private ContractorAudit conAudit;

	@Autowired
	private AuditCategoryDAO auditCategoryDAO;
	@Autowired
	private AuditDataDAO auditDataDAO;

	public ContractorRegistrationServices() {
		this.subHeading = getText("ContractorRegistrationServices.title");
		// subHeading = "Services Performed";
	}

	public String execute() throws Exception {
		loadPermissions();

		findContractor();

		auditData = new AuditData();
		for (ContractorAudit ca : contractor.getAudits()) {
			if (ca.getAuditType().isPqf()) {
				conAudit = ca;
				pqfID = ca.getId();
				// Prepare auditdata
				auditData.setAudit(new ContractorAudit());
				auditData.getAudit().setId(pqfID);
				for (AuditCatData catData : ca.getCategories()) {
					if (catData.getCategory().getId() == AuditCategory.SERVICES_PERFORMED) {
						catDataID = catData.getId();
					}
				}
			}
		}

		// Missing PQF and category data -- just create it now
		if (pqfID == 0 && catDataID == 0)
			createPQF();

		Set<Integer> questionIds = new HashSet<Integer>();
		categories = auditCategoryDAO.findWhere("id IN (400)");
		for (AuditCategory category : categories) {
			for (AuditQuestion question : category.getQuestions()) {
				infoQuestions.add(question);
				questionIds.add(question.getId());
			}
		}

		answerMap = auditDataDAO.findAnswersByContractor(id, questionIds);

		// TODO add productRiskLevel ?
		if ("calculateRisk".equals(button)) {
			if (contractor.getSafetyRisk() == null) {
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
				}
				if (!requiredQuestions)
					addActionError("Please answer all the questions on the General Info section");
				if (requiredQuestions && performServices) {
					Collection<AuditData> auditList = answerMap.values();
					LowMedHigh riskLevel = LowMedHigh.Low;
					for (AuditData auditData : auditList) {
						AuditQuestion q = auditData.getQuestion();
						// if (q.getCategory().getId() == 269) {
						if (q.getCategory().getId() == 400) {
							// Subcategory is RISK ASSESSMENT
							AuditData aData = answerMap.get(q.getId());
							riskLevel = getRiskLevel(aData, riskLevel);
						} else if (auditData.getAnswer().startsWith("C")) {
							// TODO: Remove this -- this is related to services performed
							// Self Performed Services
							riskLevel = getMaxRiskLevel(riskLevel, q.getRiskLevel());
						}
						if (riskLevel.equals(LowMedHigh.High))
							break;
					}
					contractor.setSafetyRisk(riskLevel);
					// TODO add productRiskLevel ?
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
		return pqfID;
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
				if (auditData.getAnswer().equals("Medium"))
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
		pqfID = audit.getId();

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

	public boolean isCanEditCategory(AuditCategory category) {
		return true;
	}
}
