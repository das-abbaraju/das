package com.picsauditing.actions.contractors;

import java.math.BigDecimal;
import java.util.*;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.jpa.entities.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.BillingCalculatorSingle;
import com.picsauditing.actions.contractors.risk.ServiceRiskCalculator;
import com.picsauditing.actions.contractors.risk.ServiceRiskCalculator.RiskCategory;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class RegistrationServiceEvaluation extends RegistrationAction {
	@Autowired
	private AuditDataDAO auditDataDAO;
	@Autowired
	private AuditQuestionDAO questionDao = null;
	@Autowired
	private BillingCalculatorSingle billingService;
	@Autowired
	private AppPropertyDAO appPropertyDAO;

	private List<AuditQuestion> infoQuestions = new ArrayList<AuditQuestion>();

	private Map<Integer, AuditData> answerMap = new HashMap<>();
	private Map<Integer, AuditData> ssipAnswerMap = new HashMap<>();
	private ContractorAudit conAudit;
	private ContractorAudit ssipAudit;
	private boolean showBidOnly;

	private boolean showCompetitor;
	private boolean isSoleProprietor;
	private boolean isBidOnly;
	private boolean hasTransportationQuestions;
	private boolean requireOnsite;

	private boolean requireOffsite;
	private boolean requireMaterialSupplier;
	private boolean requireTransportation;
	private String servicesHelpText = "";

	private List<ContractorType> conTypes = new ArrayList<ContractorType>();

	private String readyToProvideSsipDetails;

	private int yearOfLastSsipMemberAudit;
	private int monthOfLastSsipMemberAudit;
	private int dayOfLastSsipMemberAudit;

	private int yearOfSsipMembershipExpiration;
	private int monthOfSsipMembershipExpiration;
	private int dayOfSsipMembershipExpiration;

	public static final int QUESTION_ID_REGISTERED_WITH_SSIP = 16914;
	public static final int QUESTION_ID_SSIP_AUDIT_DATE = 16915;
	public static final int QUESTION_ID_SSIP_EXPIRATION_DATE = 16916;
	public static final int QUESTION_ID_SSIP_SCHEME = 16948;

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

		loadPqfQuestionsAndAudit();
		loadSsipAudit();

		buildAndLoadSsipDatesIntoAnswerMap();

		loadPqfAnswers();
		loadSsipAnswers();

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

		loadPqfQuestionsAndAudit();
		loadSsipAudit();

		if (!validatePqfAuditAnswers() || !validateSsipAuditAnswers()) {
			addActionError("All Questions Must Be Answered.");
			return SUCCESS;
		}

		saveAnswersForPqfAudit();
		saveAnswersForSsipAudit();

		loadPqfAnswers();
		loadSsipAnswers();

		calculateRiskLevels();
		setAccountLevelByListOnlyEligibility();

		contractor.syncBalance();
		billingService.calculateContractorInvoiceFees(contractor);
		contractorAccountDao.save(contractor);

		// Free accounts should just be activated
		if (contractor.isHasFreeMembership() && contractor.getStatus().isPendingOrDeactivated()) {
			contractor.setStatus(AccountStatus.Active);
			contractor.setAuditColumns(permissions);
			contractor.setMembershipDate(new Date());

			if (contractor.getBalance() == null) {
				contractor.setBalance(BigDecimal.ZERO);
			}

			contractorAccountDao.save(contractor);
		}

		return setUrlForRedirect(getRegistrationStep().getUrl());
	}

	private boolean validateSsipAuditAnswers() {
		if (!shouldShowSsip()) {
			return true;
		}

		buildAndLoadSsipDatesIntoAnswerMap();

		AuditData registeredWithSsipData = answerMap.get(QUESTION_ID_REGISTERED_WITH_SSIP);
		if (registeredWithSsipData == null) {
			return false;
		}

		String registeredAnswer = registeredWithSsipData.getAnswer();
		if (YesNo.No.toString().equals(registeredAnswer)) {
			return true;
		}

		if (readyToProvideSsipDetails == null) {
			return false;
		}

		if (YesNo.No.toString().equals(readyToProvideSsipDetails)) {
			return true;
		}

		AuditData auditDateData = ssipAnswerMap.get(QUESTION_ID_SSIP_AUDIT_DATE);
		if (auditDateData == null || auditDateData.getAnswer() == null) {
			return false;
		}

		if (DateBean.parseDate(auditDateData.getAnswer()) == null) {
			return false;
		}

		AuditData expirationDateData = ssipAnswerMap.get(QUESTION_ID_SSIP_EXPIRATION_DATE);
		if (expirationDateData == null || expirationDateData.getAnswer() == null) {
			return false;
		}

		if (DateBean.parseDate(expirationDateData.getAnswer()) == null) {
			return false;
		}

		AuditData ssipSchemeData = ssipAnswerMap.get(QUESTION_ID_SSIP_SCHEME);
		if (ssipSchemeData == null) {
			return false;
		}
		String ssipSchemeAnswer = ssipSchemeData.getAnswer();
		// KLUDGE: This is better than matching a magic string
		// Detects whether the user has selected a scheme, in which case the answer will
		// start with a letter. If they haven't selected an option, the value will be
		// something like "- Please select a scheme -".
		if (ssipSchemeAnswer == null || ssipSchemeAnswer.trim().startsWith("-")) {
			return false;
		}

		return true;
	}

	public boolean validatePqfAuditAnswers() {
		Set<Integer> requiredPqfCategoryIds = buildRequiredPqfCategoryIds();
		ContractorAudit conAudit = getContractorPQF(requiredPqfCategoryIds);

		Map<AuditCategory, AuditCatData> pqfCategories = new HashMap<>();
		for (AuditCatData catData : conAudit.getCategories()) {
			if (requiredPqfCategoryIds.contains(catData.getCategory().getId())) {
				pqfCategories.put(catData.getCategory(), catData);
			}
		}

		List<AuditQuestion> requiredQuestions = new ArrayList<>();
		for (AuditCategory pqfCategory : pqfCategories.keySet()) {
			for (AuditQuestion question : pqfCategory.getQuestions()) {
				if (question.isValidQuestion(new Date())) {
					requiredQuestions.add(question);
				}
			}
		}

		for (AuditQuestion question : requiredQuestions) {
			if (!answerMap.containsKey(question.getId())) {
				return false;
			}
		}

		return true;
	}

	private Set<Integer> buildRequiredPqfCategoryIds() {
		Set<Integer> catIds = new HashSet<>();

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

		return catIds;
	}

	public Map<Integer, AuditData> getAnswerMap() {
		return answerMap;
	}

	public ContractorAudit getConAudit() {
		return conAudit;
	}

	public ContractorAudit getSsipAudit() {
		return ssipAudit;
	}

	public void setSsipAudit(ContractorAudit ssipAudit) {
		this.ssipAudit = ssipAudit;
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
		if (infoQuestions == null || infoQuestions.size() == 0) {
			loadPqfQuestionsAndAudit();
        }

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

    public boolean isHasTransportationQuestions() {
        return hasTransportationQuestions;
    }

	public int getDayOfSsipMembershipExpiration() {
		return dayOfSsipMembershipExpiration;
	}

	public void setDayOfSsipMembershipExpiration(int dayOfSsipMembershipExpiration) {
		this.dayOfSsipMembershipExpiration = dayOfSsipMembershipExpiration;
	}

	public int getMonthOfSsipMembershipExpiration() {
		return monthOfSsipMembershipExpiration;
	}

	public void setMonthOfSsipMembershipExpiration(int monthOfSsipMembershipExpiration) {
		this.monthOfSsipMembershipExpiration = monthOfSsipMembershipExpiration;
	}

	public int getYearOfSsipMembershipExpiration() {
		return yearOfSsipMembershipExpiration;
	}

	public void setYearOfSsipMembershipExpiration(int yearOfSsipMembershipExpiration) {
		this.yearOfSsipMembershipExpiration = yearOfSsipMembershipExpiration;
	}

	public int getDayOfLastSsipMemberAudit() {
		return dayOfLastSsipMemberAudit;
	}

	public void setDayOfLastSsipMemberAudit(int dayOfLastSsipMemberAudit) {
		this.dayOfLastSsipMemberAudit = dayOfLastSsipMemberAudit;
	}

	public int getMonthOfLastSsipMemberAudit() {
		return monthOfLastSsipMemberAudit;
	}

	public void setMonthOfLastSsipMemberAudit(int monthOfLastSsipMemberAudit) {
		this.monthOfLastSsipMemberAudit = monthOfLastSsipMemberAudit;
	}

	public int getYearOfLastSsipMemberAudit() {
		return yearOfLastSsipMemberAudit;
	}

	public void setYearOfLastSsipMemberAudit(int yearOfLastSsipMemberAudit) {
		this.yearOfLastSsipMemberAudit = yearOfLastSsipMemberAudit;
	}

	public String isReadyToProvideSsipDetails() {
		return readyToProvideSsipDetails;
	}

	public void setReadyToProvideSsipDetails(String readyToProvideSsipDetails) {
		this.readyToProvideSsipDetails = readyToProvideSsipDetails;
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

	private void loadPqfQuestionsAndAudit() {
		Set<Integer> pqfCategoryIds = new HashSet<>();
		// First SSIP question is saved in the pqf
		pqfCategoryIds.add(AuditCategory.SSIP);
		pqfCategoryIds.add(AuditCategory.SERVICE_SAFETY_EVAL);
		pqfCategoryIds.add(AuditCategory.PRODUCT_SAFETY_EVAL);
		pqfCategoryIds.add(AuditCategory.BUSINESS_INTERRUPTION_EVAL);
		pqfCategoryIds.add(AuditCategory.TRANSPORTATION_SAFETY_EVAL);

		conAudit = getContractorPQF(pqfCategoryIds);

		Map<AuditCategory, AuditCatData> pqfCategories = buildCategoriesMap(pqfCategoryIds, conAudit);

		// find the questions for all the categories
		for (AuditCategory category : pqfCategories.keySet()) {
			for (AuditQuestion question : category.getQuestions()) {
				if (question.isValidQuestion(new Date())) {
                    infoQuestions.add(question);

                    if (question.getCategory().getId() == AuditCategory.TRANSPORTATION_SAFETY_EVAL) {
                        hasTransportationQuestions = true;
                    }
				}
			}
		}
	}

	private void loadSsipAudit() {
		Set<Integer> ssipCategoryIds = new HashSet<>();
		ssipCategoryIds.add(AuditCategory.SSIP_EVALUATION);
		ssipAudit = getSSIPVerification(ssipCategoryIds);
	}

	private Map<AuditCategory, AuditCatData> buildCategoriesMap(Set<Integer> categoryIds, ContractorAudit audit) {
		Map<AuditCategory, AuditCatData> categories = new HashMap<>();

		for (AuditCatData catData : audit.getCategories()) {
			if (categoryIds.contains(catData.getCategory().getId())) {
				categories.put(catData.getCategory(), catData);
			}
		}

		return categories;
	}

	private void loadPqfAnswers() {
		Set<Integer> questionIds = new HashSet<>();

		for (AuditQuestion question : infoQuestions) {
			questionIds.add(question.getId());
		}

		// find the answers to the pqf questions
		answerMap = auditDataDAO.findAnswersByContractor(id, questionIds);

		for (AuditQuestion question : infoQuestions) {
			if (answerMap.get(question.getId()) == null) {
				AuditData auditData = new AuditData();
				auditData.setAnswer("");
				auditData.setAudit(conAudit);
				auditData.setQuestion(question);
				auditData.setAuditColumns(permissions);
				answerMap.put(question.getId(), auditData);
			}
		}
	}

	private void loadSsipAnswers() {
		Set<Integer> questionIds = new HashSet<>();
		questionIds.add(QUESTION_ID_SSIP_AUDIT_DATE);
		questionIds.add(QUESTION_ID_SSIP_EXPIRATION_DATE);
		questionIds.add(QUESTION_ID_SSIP_SCHEME);

		// find the answers to the ssip questions
		ssipAnswerMap = auditDataDAO.findAnswersByContractor(id, questionIds);

		for (int questionId : questionIds) {
			if (ssipAnswerMap.get(questionId) != null) {
				continue;
			}

			AuditData auditData = new AuditData();
			auditData.setAnswer("");
			auditData.setAudit(ssipAudit);
			auditData.setQuestion(questionDao.find(questionId));
			auditData.setAuditColumns(permissions);
			ssipAnswerMap.put(questionId, auditData);
		}
	}

	private void saveAnswersForPqfAudit() throws Exception {
		for (Integer qid : answerMap.keySet()) {
			AuditData auditData = answerMap.get(qid);
			AuditData newData = auditDataDAO.findAnswerByAuditQuestion(conAudit.getId(), qid);

			if (!Strings.isEmpty(auditData.getAnswer())) {
				if (newData != null) {
					auditData.setId(newData.getId());
				}

				auditData.setAudit(conAudit);
				auditData.setQuestion(questionDao.find(qid));
				auditData.setAuditColumns(permissions);
				auditDataDAO.save(auditData);
 			}
		}
	}

	private void saveAnswersForSsipAudit() throws Exception {
		if (!shouldPersistSsipQuestions()) {
			return;
		}

		for (Integer qid : ssipAnswerMap.keySet()) {
			AuditData auditData = ssipAnswerMap.get(qid);
			AuditData newData = auditDataDAO.findAnswerByAuditQuestion(ssipAudit.getId(), qid);

			if (!Strings.isEmpty(auditData.getAnswer())) {
				if (newData != null) {
					auditData.setId(newData.getId());
				}

				auditData.setAudit(ssipAudit);
				auditData.setQuestion(questionDao.find(qid));
				auditData.setAuditColumns(permissions);
				auditDataDAO.save(auditData);
			}
		}
	}

	private boolean shouldPersistSsipQuestions() {
		AuditData registeredWithSsip = answerMap.get(QUESTION_ID_REGISTERED_WITH_SSIP);

		if (registeredWithSsip != null && YesNo.No.name().equals(registeredWithSsip.getAnswer())) {
			return false;
		}

		if (readyToProvideSsipDetails != null && YesNo.No.name().equals(readyToProvideSsipDetails)) {
			return false;
		}

		return true;
	}

	private void buildAndLoadSsipDatesIntoAnswerMap() {
		Map<Integer, AuditData> answerMapForDates = new HashMap<>();

		String membershipDateString = null;

		if (userHasFilledOutMembershipDate()) {
			String year = "" + yearOfLastSsipMemberAudit;

			String monthLeadingZeroIfNeeded = (monthOfLastSsipMemberAudit < 10) ? "0" : "";
			String month = monthLeadingZeroIfNeeded + monthOfLastSsipMemberAudit;

			String dayLeadingZeroIfNeeded = (dayOfLastSsipMemberAudit < 10) ? "0" : "";
			String day = dayLeadingZeroIfNeeded + dayOfLastSsipMemberAudit;

			membershipDateString = year + "-" + month + "-" + day;
		}

		AuditData membershipAuditData = new AuditData();
		membershipAuditData.setAnswer(membershipDateString);
		membershipAuditData.setAudit(ssipAudit);
		answerMapForDates.put(QUESTION_ID_SSIP_AUDIT_DATE, membershipAuditData);

		String expirationDateString = null;

		if (hasFilledOutMembershipExpiration()) {
			String year = "" + yearOfSsipMembershipExpiration;

			String monthLeadingZeroIfNeeded = (monthOfSsipMembershipExpiration < 10) ? "0" : "";
			String month = monthLeadingZeroIfNeeded + monthOfSsipMembershipExpiration;

			String dayLeadingZeroIfNeeded = (dayOfSsipMembershipExpiration < 10) ? "0" : "";
			String day = dayLeadingZeroIfNeeded + dayOfSsipMembershipExpiration;

			expirationDateString = year + "-" + month + "-" + day;
		}

		AuditData expirationAuditData = new AuditData();
		expirationAuditData.setAnswer(expirationDateString);
		expirationAuditData.setAudit(ssipAudit);
		answerMapForDates.put(QUESTION_ID_SSIP_EXPIRATION_DATE, expirationAuditData);

		ssipAnswerMap.putAll(answerMapForDates);
	}

	private boolean hasFilledOutMembershipExpiration() {
		return yearOfSsipMembershipExpiration != 0
				&& monthOfSsipMembershipExpiration != 0
				&& dayOfSsipMembershipExpiration != 0;
	}

	private boolean userHasFilledOutMembershipDate() {
		return yearOfLastSsipMemberAudit != 0
				&& monthOfLastSsipMemberAudit != 0
				&& dayOfLastSsipMemberAudit != 0;
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
		ContractorAudit audit = null;

		for (ContractorAudit conAudit : contractor.getAudits()) {
			if (conAudit.getAuditType().isPicsPqf()) {
				audit = conAudit;
				break;
			}
		}

		// The pqf doesn't exist yet, it should be created.
		if (audit == null) {
			audit = new ContractorAudit();
			audit.setContractorAccount(contractor);
			audit.setAuditType(new AuditType(AuditType.PQF));
			audit.setAuditColumns(new User(User.SYSTEM));
		}

		// Add the categories that are required for this contractor
		Set<Integer> categoriesToAdd = new HashSet<Integer>(categoryIds);
		for (AuditCatData catData : audit.getCategories()) {
			int catID = catData.getCategory().getId();
			if (categoriesToAdd.contains(catID)) {
				categoriesToAdd.remove(catID);
			}
		}

		// If there are categories left in the categoryIds set, then they need
		// to be added now.
		for (Integer catID : categoriesToAdd) {
			addAuditCategories(audit, catID);
		}

		auditDao.save(audit);

		// refresh the audit to force the categories to reload
		auditDao.refresh(conAudit);

		return audit;
	}

	private ContractorAudit getSSIPVerification(Set<Integer> categoryIds) {
		ContractorAudit ssip = null;

		for (ContractorAudit audit : contractor.getAudits()) {
			if (audit.getAuditType().isSsip()) {
				ssip = audit;
				break;
			}
		}

		// The ssip audit doesn't exist yet, it should be created.
		if (ssip == null) {
			ssip = new ContractorAudit();
			ssip.setContractorAccount(contractor);
			ssip.setAuditType(new AuditType(AuditType.SSIP));
			ssip.setAuditColumns(new User(User.SYSTEM));
		}

		// Add the categories that are required for this contractor
		Set<Integer> categoriesToAdd = new HashSet<Integer>(categoryIds);
		for (AuditCatData catData : ssip.getCategories()) {
			int catID = catData.getCategory().getId();
			if (categoriesToAdd.contains(catID)) {
				categoriesToAdd.remove(catID);
			}
		}

		// If there are categories left in the categoryIds set, then they need
		// to be added now.
		for (Integer catID : categoriesToAdd) {
			addAuditCategories(ssip, catID);
		}

		auditDao.save(ssip);

		// refresh the audit to force the categories to reload
		auditDao.refresh(ssipAudit);

		return ssip;
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

	public boolean shouldShowSsip() {
		AppProperty ssipClientSiteIdsProperty = appPropertyDAO.find(AppProperty.SSIP_CLIENT_SITE_ID_LIST_KEY);
		if (ssipClientSiteIdsProperty == null) {
			return false;
		}

		String value = ssipClientSiteIdsProperty.getValue();
		String[] ssipClientSiteIds = (value != null) ? value.split(",") : new String[]{};

		ContractorAccount account = (ContractorAccount) getAccount();

		for (OperatorAccount operator : account.getOperatorAccounts()) {
			for (String ssipClientSiteId : ssipClientSiteIds) {
				if (ssipClientSiteId.trim().equals("" + operator.getId())) {
					return true;
				}
			}
		}

		return false;
	}

	public List<AuditOptionValue> getSsipMemberSchemes() {
		AuditQuestion question = questionDao.find(QUESTION_ID_SSIP_SCHEME);

		AuditOptionGroup auditOptionGroup = question.getOption();

		List<AuditOptionValue> optionValues = auditOptionGroup.getValues();

		return optionValues;
	}

	public Map<Integer, AuditData> getSsipAnswerMap() {
		return ssipAnswerMap;
	}

	public void setSsipAnswerMap(Map<Integer, AuditData> ssipAnswerMap) {
		this.ssipAnswerMap = ssipAnswerMap;
	}
}