package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorRegistrationStep;
import com.picsauditing.jpa.entities.ContractorTrade;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ContractorRegistrationServices extends ContractorActionSupport {

	private int pqfID = 0;
	private int catDataID = 0;
	private List<AuditQuestion> infoQuestions = new ArrayList<AuditQuestion>();
	private Set<AuditCategory> categories;
	private Map<Integer, AuditData> answerMap;
	private AuditData auditData;
	private ContractorAudit conAudit;

	@Autowired
	private AuditCategoryDAO auditCategoryDAO;
	@Autowired
	private AuditDataDAO auditDataDAO;

	public ContractorRegistrationServices() {
		this.subHeading = getText("ContractorRegistrationServices.title");
		this.currentStep = ContractorRegistrationStep.Risk;
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
			categoryIds.add(AuditCategory.RISK_ASSESSMENT);
		if (contractor.isMaterialSupplier()) {
			categoryIds.add(1682);
			categoryIds.add(1683);
		}

		Set<Integer> questionIds = new HashSet<Integer>();
		categories = new TreeSet<AuditCategory>(auditCategoryDAO.findWhere("id IN (" + Strings.implode(categoryIds)
				+ ")"));
		for (AuditCategory category : categories) {
			for (AuditQuestion question : category.getQuestions()) {
				infoQuestions.add(question);
				questionIds.add(question.getId());
			}
		}

		answerMap = auditDataDAO.findAnswersByContractor(id, questionIds);

		return SUCCESS;
	}

	@Override
	public String nextStep() throws Exception {
		execute();

		if (contractor.getSafetyRisk().equals(LowMedHigh.None)
				|| (contractor.isMaterialSupplier() && contractor.getProductRisk().equals(LowMedHigh.None))) {
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

			if (!requiredQuestions) {
				addActionError("Please answer all the questions on the General Info section");
				return SUCCESS;
			}

			if (requiredQuestions) {
				Collection<AuditData> auditList = answerMap.values();
				// Calculated assessments
				LowMedHigh safety = LowMedHigh.Low;
				LowMedHigh product = LowMedHigh.Low;
				// Self assessments
				LowMedHigh conSafety = LowMedHigh.Low;
				LowMedHigh conProduct = LowMedHigh.Low;
				LowMedHigh conProductSafety = LowMedHigh.Low;

				for (ContractorTrade trade : contractor.getTrades()) {
					if (trade.getTrade().getSafetyRiskI() != null)
						safety = getMaxRiskLevel(safety, trade.getTrade().getSafetyRiskI());
					if (trade.getTrade().getProductRiskI() != null)
						product = getMaxRiskLevel(product, trade.getTrade().getProductRiskI());
				}

				for (AuditData auditData : auditList) {
					AuditQuestion q = auditData.getQuestion();
					// if (q.getCategory().getId() == 269) {
					if (q.getCategory().getId() == AuditCategory.RISK_ASSESSMENT) {
						AuditData aData = answerMap.get(q.getId());
						safety = getRiskLevel(aData, safety);

						if (q.getId() == AuditQuestion.RISK_LEVEL_ASSESSMENT)
							conSafety = getRiskLevel(aData, conSafety);
					} else if (q.getCategory().getId() == AuditCategory.PRODUCT_CRITICAL
							|| q.getCategory().getId() == AuditCategory.PRODUCT_SAFETY_CRITICAL) {
						AuditData aData = answerMap.get(q.getId());
						product = getRiskLevel(aData, product);

						if (q.getId() == AuditQuestion.PRODUCT_SAFETY_CRITICAL_ASSESSMENT)
							conProductSafety = getRiskLevel(aData, conProductSafety);
						if (q.getId() == AuditQuestion.PRODUCT_CRITICAL_ASSESSMENT)
							conProduct = getRiskLevel(aData, conProduct);
					}
				}

				boolean isSafetyOK = true;
				boolean isProductOK = true;
				// Contractor's assessments are the same (or higher?) than what
				// we've calculated
				if (!contractor.isMaterialSupplierOnly())
					isSafetyOK = conSafety.ordinal() >= safety.ordinal();
				if (contractor.isMaterialSupplier())
					isProductOK = conProductSafety.ordinal() >= product.ordinal()
							&& conProduct.ordinal() >= product.ordinal();

				contractor.setSafetyRisk(safety);
				contractor.setProductRisk(product);

				contractor.setAuditColumns(permissions);
				accountDao.save(contractor);

				if (!isSafetyOK || !isProductOK) {
					String safetyAssessment = safety.toString();
					if (safetyAssessment.equals("Med"))
						safetyAssessment = "Medium";

					String productAssessment = product.toString();
					if (productAssessment.equals("Med"))
						productAssessment = "Medium";

					List<String> increases = new ArrayList<String>();
					if (safety.ordinal() > conSafety.ordinal() && !contractor.isMaterialSupplierOnly())
						increases.add("Service Safety Evaluation to <b>" + safety + "</b>");
					if (product.ordinal() > conProduct.ordinal() && contractor.isMaterialSupplier())
						increases.add("Business Interruption Evaluation to <b>" + productAssessment + "</b>");
					if (product.ordinal() > conProductSafety.ordinal() && contractor.isMaterialSupplier())
						increases.add("Product Safety Evaluation to <b>" + productAssessment + "</b>");

					output = "The answers you have provided indicate higher risk levels than the "
							+ "ratings you have selected. We recommend increasing your "
							+ Strings.implode(increases, ", and your ")
							+ ". You can still continue with the registration process. "
							+ "<br />Please contact PICS with any questions.";

					return SUCCESS;
				}
			}
		}

		redirect(getRegistrationStep().getUrl(contractor.getId()));
		return BLANK;
	}

	public int getAuditID() {
		return pqfID;
	}

	public int getCatDataID() {
		return catDataID;
	}

	public List<AuditQuestion> getInfoQuestions() {
		return infoQuestions;
	}

	public Set<AuditCategory> getCategories() {
		return categories;
	}

	public void setCategories(Set<AuditCategory> categories) {
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
			switch (auditData.getQuestion().getId()) {
			case 2442:
			case 2445:
				// Question : Does your company perform mechanical services
				// OR Services conducted at heights greater than six feet?
				if (auditData.getAnswer().equals("Yes"))
					return LowMedHigh.High;
				break;
			case 3793:
				// Question : Does your company perform mechanical services that
				// require the use of hand/power tools?
				if (auditData.getAnswer().equals("Yes"))
					return getMaxRiskLevel(riskLevel, LowMedHigh.Med);
				break;
			case 2443:
				// Question : Does your company perform all services from only
				// an office?
				if (auditData.getAnswer().equals("No"))
					return getMaxRiskLevel(riskLevel, LowMedHigh.Med);
				break;
			case AuditQuestion.RISK_LEVEL_ASSESSMENT:
			case AuditQuestion.PRODUCT_CRITICAL_ASSESSMENT:
			case AuditQuestion.PRODUCT_SAFETY_CRITICAL_ASSESSMENT:
				// Question : What risk level do you believe your company should
				// be rated?
				if (auditData.getAnswer().equals("Medium"))
					return getMaxRiskLevel(riskLevel, LowMedHigh.Med);
				if (auditData.getAnswer().equals("High"))
					return LowMedHigh.High;
				break;
			case 7660:
			case 7661:
				// Product Critical Assessment
				// 7660: Can failures in your products result in a work stoppage
				// or major business interruption for your
				// customer?
				// 7661: If you fail to deliver your products on-time, can it
				// result in a work stoppage or major
				// business interruption for your customer?
				if (auditData.getAnswer().equals("Yes"))
					return LowMedHigh.High;
				break;
			case 7662:
				// Product Safety Critical
				// Can failures in your products result in bodily injury or
				// illness to your customer or end-user?
				if (auditData.getAnswer().equals("Yes"))
					return LowMedHigh.High;
				break;
			case 7663:
				// Are you required to carry Product Liability Insurance?
				if (auditData.getAnswer().equals("Medium"))
					return getMaxRiskLevel(riskLevel, LowMedHigh.Med);
				if (auditData.getAnswer().equals("High"))
					return LowMedHigh.High;
				break;
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