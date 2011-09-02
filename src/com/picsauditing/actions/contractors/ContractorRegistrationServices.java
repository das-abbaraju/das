package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

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

	@Autowired
	private AuditDataDAO auditDataDAO;

	private List<AuditQuestion> infoQuestions = new ArrayList<AuditQuestion>();
	private Map<AuditCategory, AuditCatData> categories = new HashMap<AuditCategory, AuditCatData>();
	private Map<Integer, AuditData> answerMap;
	private ContractorAudit conAudit;

	public ContractorRegistrationServices() {
		this.subHeading = getText("ContractorRegistrationServices.title");
		this.currentStep = ContractorRegistrationStep.Risk;
	}

	public String execute() throws Exception {
		findContractor();

		// get the categories for a contractor based on their Onsite/Offsite/Material Supplier status
		Set<Integer> categoryIds = new HashSet<Integer>();
		if (contractor.isOnsiteServices() || contractor.isOffsiteServices()) {
			categoryIds.add(AuditCategory.RISK_ASSESSMENT);
		}
		if (contractor.isMaterialSupplier()) {
			categoryIds.add(AuditCategory.PRODUCT_CRITICAL);
			categoryIds.add(AuditCategory.PRODUCT_SAFETY_CRITICAL);
		}

		conAudit = getContractorPQF(categoryIds);

		for (AuditCatData catData : conAudit.getCategories()) {
			if (categoryIds.contains(catData.getCategory().getId())) {
				categories.put(catData.getCategory(), catData);
			}
		}

		// find the questions for the above categories
		Set<Integer> questionIds = new HashSet<Integer>();
		for (AuditCategory category : categories.keySet()) {
			for (AuditQuestion question : category.getQuestions()) {
				if (question.isValidQuestion(new Date())) {
					infoQuestions.add(question);
					questionIds.add(question.getId());
				}
			}
		}

		// find the answers to the questions
		answerMap = auditDataDAO.findAnswersByContractor(id, questionIds);

		return SUCCESS;
	}

	@Override
	public String nextStep() throws Exception {
		execute();

		if ((contractor.getSafetyRisk().equals(LowMedHigh.None) && !contractor.isMaterialSupplierOnly())
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
				addActionError(getText("ContractorRegistrationServices.error.AnswerAll"));
				return SUCCESS;
			} else {
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

				if (!contractor.isMaterialSupplierOnly())
					contractor.setSafetyRisk(safety);
				if (contractor.isMaterialSupplier())
					contractor.setProductRisk(product);

				contractor.setAuditColumns(permissions);
				accountDao.save(contractor);

				if (!isSafetyOK || !isProductOK) {
					String safetyAssessment = safety.toString();
					if (safetyAssessment.equals("Med"))
						safetyAssessment = getText("LowMedHigh.Med");

					String productAssessment = product.toString();
					if (productAssessment.equals("Med"))
						productAssessment = getText("LowMedHigh.Med");

					List<String> increases = new ArrayList<String>();
					if (safety.ordinal() > conSafety.ordinal() && !contractor.isMaterialSupplierOnly())
						increases.add(getTextParameterized("ContractorRegistrationServices.message.ServiceEvaluation",
								safety));
					if (product.ordinal() > conProduct.ordinal() && contractor.isMaterialSupplier())
						increases.add(getTextParameterized("ContractorRegistrationServices.message.BusinessEvaluation",
								productAssessment));
					if (product.ordinal() > conProductSafety.ordinal() && contractor.isMaterialSupplier())
						increases.add(getTextParameterized("ContractorRegistrationServices.message.ProductEvaluation",
								productAssessment));

					output = getTextParameterized("ContractorRegistrationServices.message.RiskLevels",
							Strings.implode(increases, getText("ContractorRegistrationServices.message.AndYours")));

					return SUCCESS;
				}
			}
		}

		redirect(getRegistrationStep().getUrl(contractor.getId()));
		return BLANK;
	}

	public Map<Integer, AuditData> getAnswerMap() {
		return answerMap;
	}

	public ContractorAudit getConAudit() {
		return conAudit;
	}

	public Map<AuditCategory, AuditCatData> getCategories() {
		return categories;
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
		if (auditData != null && !Strings.isEmpty(auditData.getAnswer())
				&& !auditData.getAnswer().equals(riskLevel.toString())) {
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

	/**
	 * This method finds a contractor's PQF. If it does not exists, it will create a new one and save it to the
	 * database.
	 * 
	 * It will also add all categories required for registration.
	 * 
	 * @param categoryIds
	 *            The categories required by the contractor based on the types of trades selected.
	 */
	private ContractorAudit getContractorPQF(Set<Integer> categoryIds) {
		ContractorAudit pqf = null;
		for (ContractorAudit audit : contractor.getAudits()) {
			if (audit.getAuditType().isPqf()) {
				pqf = audit;
				break;
			}
		}

		// The pqf doesn't exist yet, it should be created.
		if (pqf == null) {
			pqf = new ContractorAudit();
			pqf.setContractorAccount(contractor);
			pqf.setAuditType(new AuditType(1));
			pqf.setAuditColumns(new User(User.SYSTEM));
		}

		// Add the categories that are required for this contractor
		Set<Integer> categoriesToAdd = new HashSet<Integer>(categoryIds);
		for (AuditCatData catData : pqf.getCategories()) {
			int catID = catData.getCategory().getId();
			if (categoriesToAdd.contains(catID)) {
				categoriesToAdd.remove(catID);
			}
		}

		// If there are categories left in the categoryIds set, then they need to be added now.
		for (Integer catID : categoriesToAdd) {
			addAuditCategories(pqf, catID);
		}

		auditDao.save(pqf);

		// refresh the audit to force the categories to reload
		auditDao.refresh(conAudit);

		return pqf;
	}

	/**
	 * Adds an AuditCatData to an audit.
	 * 
	 * @param audit
	 * @param categoryId
	 */
	private void addAuditCategories(ContractorAudit audit, int categoryId) {
		AuditCatData catData = new AuditCatData();
		catData.setCategory(new AuditCategory());
		catData.getCategory().setId(categoryId);
		catData.setAudit(audit);
		catData.setApplies(true);
		catData.setOverride(false);
		catData.setAuditColumns(new User(User.SYSTEM));
		audit.getCategories().add(catData);
	}

	public boolean isCanEditCategory(AuditCategory category) {
		return true;
	}

}