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
import com.picsauditing.jpa.entities.ContractorTrade;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.Strings;

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

		Set<Integer> categoryIds = new HashSet<Integer>();
		if (contractor.isOnsiteServices() || contractor.isOffsiteServices())
			categoryIds.add(400);
		if (contractor.isMaterialSupplier()) {
			categoryIds.add(1682);
			categoryIds.add(1683);
		}

		Set<Integer> questionIds = new HashSet<Integer>();
		categories = auditCategoryDAO.findWhere("id IN (" + Strings.implode(categoryIds) + ")");
		for (AuditCategory category : categories) {
			for (AuditQuestion question : category.getQuestions()) {
				infoQuestions.add(question);
				questionIds.add(question.getId());
			}
		}

		answerMap = auditDataDAO.findAnswersByContractor(id, questionIds);

		return SUCCESS;
	}

	public String calculateRisk() throws Exception {
		execute();

		if (contractor.getSafetyRisk() == null && contractor.getProductRisk() == null) {
			boolean requiredQuestions = false;
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
			if (requiredQuestions) {
				Collection<AuditData> auditList = answerMap.values();
				LowMedHigh safetyRisk = LowMedHigh.Low;
				LowMedHigh productRisk = LowMedHigh.Low;
				LowMedHigh conSafetyAssessment = LowMedHigh.Low;

				for (ContractorTrade trade : contractor.getTrades()) {
					if (trade.getTrade().getSafetyRisk() != null)
						safetyRisk = getMaxRiskLevel(safetyRisk, trade.getTrade().getSafetyRisk());
					if (trade.getTrade().getProductRisk() != null)
						productRisk = getMaxRiskLevel(productRisk, trade.getTrade().getProductRisk());
				}

				for (AuditData auditData : auditList) {
					AuditQuestion q = auditData.getQuestion();
					// if (q.getCategory().getId() == 269) {
					if (q.getCategory().getId() == 400) {
						// Subcategory is RISK ASSESSMENT
						AuditData aData = answerMap.get(q.getId());
						safetyRisk = getRiskLevel(aData, safetyRisk);

						if (q.getId() == 2444)
							conSafetyAssessment = getRiskLevel(aData, conSafetyAssessment);
					} else if (q.getCategory().getId() == 1682 || q.getCategory().getId() == 1683) {
						// 1682: Product Critical Assessment
						// 1683: Product Safety Critical
						AuditData aData = answerMap.get(q.getId());
						productRisk = getRiskLevel(aData, productRisk);
					}
				}
				// Contractor's safety assessment is the same (or higher?) than
				// what we've calculated
				if (conSafetyAssessment.ordinal() >= safetyRisk.ordinal()) {
					if (contractor.isOnsiteServices() || contractor.isOffsiteServices())
						contractor.setSafetyRisk(safetyRisk);
					if (contractor.isMaterialSupplier())
						contractor.setProductRisk(productRisk);

					contractor.setAuditColumns(permissions);
					accountDao.save(contractor);
				} else {
					String risk = safetyRisk.toString();
					if (risk.equals("Med"))
						risk = "Medium";

					addActionError("The answers you have provided indicate a higher risk level than the "
							+ "rating you have selected. We recommend increasing your risk ranking to " + risk
							+ ".<br />Please contact PICS with any questions.");
					return SUCCESS;
				}
			}
		}

		redirect("ContractorFacilities.action?id=" + contractor.getId()
				+ (requestID > 0 ? "&requestID=" + requestID : ""));
		return BLANK;
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
			// Product Critical Assessment
			if (auditData.getQuestion().getId() == 7660 || auditData.getQuestion().getId() == 7661) {
				// 7660: Can failures in your products result in a work stoppage
				// or major business interruption for your customer?
				// 7661: If you fail to deliver your products on-time, can it
				// result in a work stoppage or major business interruption for
				// your customer?
				if (auditData.getAnswer().equals("Yes"))
					return LowMedHigh.High;
			}
			// Product Safety Critical
			if (auditData.getQuestion().getId() == 7662) {
				// Can failures in your products result in bodily injury or
				// illness to your customer or end-user?
				if (auditData.getAnswer().equals("Yes"))
					return LowMedHigh.High;
			}
			if (auditData.getQuestion().getId() == 7663) {
				// Are you required to carry Product Liability Insurance?
				if (auditData.getAnswer().equals("Medium"))
					return getMaxRiskLevel(riskLevel, LowMedHigh.Med);
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
