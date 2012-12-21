package com.picsauditing.actions.contractors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.BillingCalculatorSingle;
import com.picsauditing.actions.contractors.risk.ServiceRiskCalculator;
import com.picsauditing.actions.contractors.risk.ServiceRiskCalculator.RiskCategory;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.jpa.entities.AccountLevel;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorRegistrationStep;
import com.picsauditing.jpa.entities.ContractorType;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class RegistrationServiceEvaluation extends ContractorActionSupport {
	@Autowired
	private AuditDataDAO auditDataDAO;
	@Autowired
	private AuditQuestionDAO questionDao = null;
	@Autowired
	private BillingCalculatorSingle billingService;

	private List<AuditQuestion> infoQuestions = new ArrayList<AuditQuestion>();
	private Map<AuditCategory, AuditCatData> categories = new HashMap<AuditCategory, AuditCatData>();
	private Map<Integer, AuditData> answerMap = new HashMap<Integer, AuditData>();
	private ContractorAudit conAudit;
	private boolean showBidOnly;
	private boolean showCompetitor;
	private boolean isSoleProprietor;
	private boolean isBidOnly;

	private boolean requireOnsite;
	private boolean requireOffsite;
	private boolean requireMaterialSupplier;
	private boolean requireTransportation;

	private String servicesHelpText = "";

	private List<ContractorType> conTypes = new ArrayList<ContractorType>();

	public RegistrationServiceEvaluation() {
		this.subHeading = getText("ContractorRegistrationServices.title");
		this.currentStep = ContractorRegistrationStep.Risk;
	}

	public String execute() throws Exception {
		findContractor();
		if (redirectIfNotReadyForThisStep()) {
			return SUCCESS;
		}

		showBidOnly = true;
		showCompetitor = true;

		for (OperatorAccount operator : contractor.getOperatorAccounts()) {
			if (!operator.isAcceptsBids()) {
				showBidOnly = false;
			}
		}

		setRequiredContractorTypes();
		setServicesHelpText();

		if (contractor.getRequestedBy() != null && !contractor.getRequestedBy().isDescendantOf(OperatorAccount.SUNCOR))
			showCompetitor = false;

		loadQuestions();
		loadAnswers();

		return SUCCESS;
	}

	public void setRequiredContractorTypes() {
		requireOnsite = contractor.isContractorTypeRequired(ContractorType.Onsite);
		requireOffsite = contractor.isContractorTypeRequired(ContractorType.Offsite);
		requireMaterialSupplier = contractor.isContractorTypeRequired(ContractorType.Supplier);
		requireTransportation = contractor.isContractorTypeRequired(ContractorType.Transportation);
	}

	public boolean conTypesOK() {
		boolean conTypesOK = true;
		boolean meetsOperatorsRequirements = false;
		for (OperatorAccount operator : contractor.getOperatorAccounts()) {
			meetsOperatorsRequirements = false;
			for (ContractorType conType : operator.getAccountTypes()) {
				if (conTypes.contains(conType))
					meetsOperatorsRequirements = true;
			}

			if (!meetsOperatorsRequirements) {
				String msg = operator.getName() + " requires you to select "
						+ StringUtils.join(operator.getAccountTypes(), " or ");
				addActionError(msg);
				conTypesOK = false;
			}
		}
		return conTypesOK;
	}

	@Override
	public String nextStep() throws Exception {
		findContractor();
		if (redirectIfNotReadyForThisStep())
			return BLANK;

		contractor.setSoleProprietor(isSoleProprietor);
		if (isBidOnly)
			contractor.setAccountLevel(AccountLevel.BidOnly);
		else if (contractor.getAccountLevel().equals(AccountLevel.BidOnly))
			contractor.setAccountLevel(AccountLevel.Full);

		setRequiredContractorTypes();

		// account for disabled checkboxes not coming though
		if (requireOnsite)
			conTypes.add(ContractorType.Onsite);
		if (requireOffsite)
			conTypes.add(ContractorType.Offsite);
		if (requireMaterialSupplier)
			conTypes.add(ContractorType.Supplier);
		if (requireTransportation)
			conTypes.add(ContractorType.Transportation);

		contractor.setAccountTypes(conTypes);
		contractor.resetRisksBasedOnTypes();

		if (!conTypesOK()) {
			return SUCCESS;
		}

		loadQuestions();

		if (!validateAnswers()) {
			return SUCCESS;
		}

		saveAnswers();
		loadAnswers();
		calculateRiskLevels();
		setAccountLevelByListOnlyEligibility();
		contractor.syncBalance();
		billingService.calculateAnnualFees(contractor);
		contractorAccountDao.save(contractor);

		// Free accounts should just be activated
		if (contractor.isHasFreeMembership() && contractor.getStatus().isPendingDeactivated()) {
			contractor.setStatus(AccountStatus.Active);
			contractor.setAuditColumns(permissions);
			contractor.setMembershipDate(new Date());
			if (contractor.getBalance() == null)
				contractor.setBalance(BigDecimal.ZERO);
			contractorAccountDao.save(contractor);
		}

		return setUrlForRedirect(getRegistrationStep().getUrl());
	}

	public boolean validateAnswers() {
		Set<Integer> catIds = new HashSet<Integer>();
		Map<AuditCategory, AuditCatData> cats = new HashMap<AuditCategory, AuditCatData>();
		ContractorAudit ca = new ContractorAudit();
		List<AuditQuestion> questions = new ArrayList<AuditQuestion>();

		if (contractor.isOnsiteServices() || contractor.isOffsiteServices()) {
			catIds.add(AuditCategory.SERVICE_SAFETY_EVAL);
		}

		if (contractor.isMaterialSupplier()) {
			catIds.add(AuditCategory.PRODUCT_SAFETY_EVAL);
			catIds.add(AuditCategory.BUSINESS_INTERRUPTION_EVAL);
		}

		if (contractor.isTransportationServices()) {
			catIds.add(AuditCategory.TRANSPORTATION_SAFETY_EVAL);
		}

		ca = getContractorPQF(catIds);

		for (AuditCatData catData : ca.getCategories()) {
			if (catIds.contains(catData.getCategory().getId())) {
				cats.put(catData.getCategory(), catData);
			}
		}

		for (AuditCategory cat : cats.keySet()) {
			for (AuditQuestion q : cat.getQuestions()) {
				if (q.isValidQuestion(new Date())) {
					questions.add(q);
				}
			}
		}

		for (AuditQuestion q : questions) {
			if (!answerMap.containsKey(q.getId())) {
				addActionError("All Questions Must Be Answered.");
				return false;
			}
		}

		return true;
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

	public boolean isCanEditCategory(AuditCategory category) {
		return true;
	}

	public List<AuditQuestion> getInfoQuestions() {
		if (infoQuestions == null || infoQuestions.size() == 0)
			loadQuestions();
		return infoQuestions;
	}

	public void setInfoQuestions(List<AuditQuestion> infoQuestions) {
		this.infoQuestions = infoQuestions;
	}

	public void setAnswerMap(Map<Integer, AuditData> answerMap) {
		this.answerMap = answerMap;
	}

	public boolean isShowBidOnly() throws Exception {
		showBidOnly = true;

		for (OperatorAccount operator : contractor.getOperatorAccounts()) {
			if (!operator.isAcceptsBids()) {
				showBidOnly = false;
				break;
			}
		}

		return showBidOnly;
	}

	public void setShowBidOnly(boolean showBidOnly) {
		this.showBidOnly = showBidOnly;
	}

	public boolean isShowCompetitor() {
		showCompetitor = true;

		if (!contractor.getRequestedBy().isDescendantOf(OperatorAccount.SUNCOR))
			showCompetitor = false;

		return showCompetitor;
	}

	public void setShowCompetitor(boolean showCompetitor) {
		this.showCompetitor = showCompetitor;
	}

	public boolean isRequireOnsite() {
		return requireOnsite;
	}

	public void setRequireOnsite(boolean requireOnsite) {
		this.requireOnsite = requireOnsite;
	}

	public boolean isRequireOffsite() {
		return requireOffsite;
	}

	public void setRequireOffsite(boolean requireOffsite) {
		this.requireOffsite = requireOffsite;
	}

	public boolean isRequireMaterialSupplier() {
		return requireMaterialSupplier;
	}

	public void setRequireMaterialSupplier(boolean requireMaterialSupplier) {
		this.requireMaterialSupplier = requireMaterialSupplier;
	}

	public boolean isRequireTransportation() {
		return requireTransportation;
	}

	public void setRequireTransportation(boolean requireTransportation) {
		this.requireTransportation = requireTransportation;
	}

	public List<ContractorType> getConTypes() {
		return conTypes;
	}

	public void setConTypes(List<ContractorType> conTypes) {
		this.conTypes = conTypes;
	}

	public String getServicesHelpText() {
		return servicesHelpText;
	}

	public void setServicesHelpText(String servicesHelpText) {
		this.servicesHelpText = servicesHelpText;
	}

	public boolean isSoleProprietor() {
		return isSoleProprietor;
	}

	public void setSoleProprietor(boolean isSoleProprietor) {
		this.isSoleProprietor = isSoleProprietor;
	}

	public boolean isBidOnly() {
		return isBidOnly;
	}

	public void setBidOnly(boolean isBidOnly) {
		this.isBidOnly = isBidOnly;
	}

	public boolean isShowSafetyAssessment() {
		if (contractor != null
				&& (contractor.isOnsiteServices() || contractor.isOffsiteServices() || contractor.isMaterialSupplier() || contractor
						.isTransportationServices())) {
			return true;
		}

		if (requireOnsite || requireOffsite || requireMaterialSupplier || requireTransportation) {
			return true;
		}

		return false;
	}

	private void setServicesHelpText() {
		if (requireOnsite) {
			servicesHelpText += getTextParameterized("RegistrationServiceEvaluation.OnlyServiceAllowed",
					getText(ContractorType.Onsite.getI18nKey()), StringUtils.join(
							contractor.getOperatorsNamesThatRequireContractorType(ContractorType.Onsite), ", "));
		}

		if (requireOffsite) {
			servicesHelpText += getTextParameterized("RegistrationServiceEvaluation.OnlyServiceAllowed",
					getText(ContractorType.Offsite.getI18nKey()), StringUtils.join(
							contractor.getOperatorsNamesThatRequireContractorType(ContractorType.Offsite), ", "));
		}

		if (requireMaterialSupplier) {
			servicesHelpText += getTextParameterized("RegistrationServiceEvaluation.OnlyServiceAllowed",
					getText(ContractorType.Supplier.getI18nKey()), StringUtils.join(
							contractor.getOperatorsNamesThatRequireContractorType(ContractorType.Supplier), ", "));
		}

		if (requireTransportation) {
			servicesHelpText += getTextParameterized(
					"RegistrationServiceEvaluation.OnlyServiceAllowed",
					getText(ContractorType.Transportation.getI18nKey()),
					StringUtils.join(
							contractor.getOperatorsNamesThatRequireContractorType(ContractorType.Transportation), ", "));
		}
	}

	private void loadQuestions() {
		// get the categories for a contractor based on their
		// Onsite/Offsite/Material Supplier status
		Set<Integer> categoryIds = new HashSet<Integer>();

		categoryIds.add(AuditCategory.SERVICE_SAFETY_EVAL);
		categoryIds.add(AuditCategory.PRODUCT_SAFETY_EVAL);
		categoryIds.add(AuditCategory.BUSINESS_INTERRUPTION_EVAL);
		categoryIds.add(AuditCategory.TRANSPORTATION_SAFETY_EVAL);

		conAudit = getContractorPQF(categoryIds);

		for (AuditCatData catData : conAudit.getCategories()) {
			if (categoryIds.contains(catData.getCategory().getId())) {
				categories.put(catData.getCategory(), catData);
			}
		}

		// find the questions for the above categories
		for (AuditCategory category : categories.keySet()) {
			for (AuditQuestion question : category.getQuestions()) {
				if (question.isValidQuestion(new Date())) {
					infoQuestions.add(question);
				}
			}
		}
	}

	private void loadAnswers() {
		Set<Integer> questionIds = new HashSet<Integer>();
		for (AuditQuestion question : infoQuestions) {
			questionIds.add(question.getId());
		}

		// find the answers to the questions
		answerMap = auditDataDAO.findAnswersByContractor(id, questionIds);

		for (AuditQuestion question : infoQuestions) {
			if (answerMap.get(question.getId()) == null) {
				AuditData answer = new AuditData();
				answer.setAnswer("");
				answer.setAudit(conAudit);
				answer.setQuestion(question);
				answer.setAuditColumns(permissions);
				answerMap.put(question.getId(), answer);
			}
		}
	}

	private void saveAnswers() throws Exception {
		for (Integer qid : answerMap.keySet()) {
			AuditData data = answerMap.get(qid);
			AuditData newData = auditDataDAO.findAnswerByAuditQuestion(conAudit.getId(), qid);
			if (!Strings.isEmpty(data.getAnswer())) {
				if (newData != null)
					data.setId(newData.getId());
				data.setAudit(conAudit);
				data.setQuestion(questionDao.find(qid));
				data.setAuditColumns(permissions);
				auditDataDAO.save(data);
			}
		}
	}

	private void calculateRiskLevels() {
		Collection<AuditData> auditList = answerMap.values();
		ServiceRiskCalculator serviceRiskCalculator = new ServiceRiskCalculator();
		Map<RiskCategory, LowMedHigh> highestRisks = serviceRiskCalculator.getHighestRiskLevelMap(auditList);

		// Calculated assessments
		LowMedHigh safety = highestRisks.get(RiskCategory.SAFETY);
		LowMedHigh product = highestRisks.get(RiskCategory.PRODUCT);
		LowMedHigh transportation = highestRisks.get(RiskCategory.TRANSPORTATION);
		// Self assessments
		LowMedHigh safetySelfRating = highestRisks.get(RiskCategory.SELF_SAFETY);
		LowMedHigh productSelfRating = highestRisks.get(RiskCategory.SELF_PRODUCT);

		// Contractor's assessments are the same (or higher?) than what
		// we've calculated
		if (!contractor.isMaterialSupplierOnly()) {
			if (safetySelfRating.ordinal() < safety.ordinal()) {
				contractor.setSafetyRisk(safety);
			} else {
				contractor.setSafetyRisk(safetySelfRating);
			}
		}

		if (contractor.isMaterialSupplier()) {
			if (productSelfRating.ordinal() < product.ordinal()) {
				contractor.setProductRisk(product);
			} else {
				contractor.setProductRisk(productSelfRating);
			}
		}

		if (contractor.isTransportationServices()) {
			// Safety risk also now includes transportation calculations
			if (safety.ordinal() < transportation.ordinal()) {
				contractor.setSafetyRisk(transportation);
			}

			contractor.setTransportationRisk(transportation);
		}

		contractor.setAuditColumns(permissions);
		contractorAccountDao.save(contractor);
	}

	private void setAccountLevelByListOnlyEligibility() {
		if (contractor.isListOnlyEligible() && contractor.getStatus().isPending()
				&& contractor.getAccountLevel().isFull()) {
			boolean canBeListed = true;

			for (ContractorOperator conOp : contractor.getNonCorporateOperators()) {
				if (!conOp.getOperatorAccount().isAcceptsList())
					canBeListed = false;
			}

			if (canBeListed) {
				contractor.setAccountLevel(AccountLevel.ListOnly);
			}
		} else if (contractor.getAccountLevel().isListOnly())
			contractor.setAccountLevel(AccountLevel.Full);
	}

	/**
	 * This method finds a contractor's PQF. If it does not exists, it will
	 * create a new one and save it to the database.
	 * 
	 * It will also add all categories required for registration.
	 * 
	 * @param categoryIds
	 *            The categories required by the contractor based on the types
	 *            of trades selected.
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
			pqf.setAuditType(new AuditType(AuditType.PQF));
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

		// If there are categories left in the categoryIds set, then they need
		// to be added now.
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
		catData.setCategory(dao.find(AuditCategory.class, categoryId));
		catData.setAudit(audit);
		catData.setApplies(true);
		catData.setOverride(false);
		catData.setAuditColumns(new User(User.SYSTEM));
		audit.getCategories().add(catData);
	}
}