package com.picsauditing;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.access.UserAccess;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.mail.Subscription;
import com.picsauditing.mail.SubscriptionTimePeriod;
import com.picsauditing.model.i18n.LanguageModel;
import org.powermock.reflect.Whitebox;

import java.util.*;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This generates jpa objects that we can then use in our unit testing
 *
 * @author Trevor
 */
public class EntityFactory {

	static private int counter = 1;
	static private HashMap<String, Country> countries;
	static private Map<String, CountrySubdivision> countrySubdivisions;

	static public TranslatableString makeTranslatableString(String value) {
		TranslatableString string = new TranslatableString();
		string.putTranslation(Locale.ENGLISH.getLanguage(), value, false);
		return string;
	}

	static public OperatorAccount makeOperator() {
		OperatorAccount operator = new OperatorAccount();
		operator.setId(counter++);
		operator.setStatus(AccountStatus.Active);
		operator.setName("Operator Unit Test");
		operator.setApprovesRelationships(YesNo.No);
		operator.setCanSeeInsurance(YesNo.Yes);
		operator.setInheritFlagCriteria(operator);
		operator.setVisibleAuditTypes(new HashSet<Integer>());
		operator.getVisibleAuditTypes().add(1);
        operator.setCountry(mostCommonCountries().get("US"));

		return operator;
	}

	public static OperatorAccount makeSuncorOperator() {
		OperatorAccount suncorOperator = EntityFactory.makeOperator();

		OperatorAccount suncorCorporate = new OperatorAccount();
		suncorCorporate.setId(OperatorAccount.SUNCOR);

		suncorOperator.setParent(suncorCorporate);

		return suncorOperator;
	}

	/**
	 * Create an active, Medium Risk Level ContractorAccount named Contractor
	 * Unit Test
	 *
	 * @return
	 */
	static public ContractorAccount makeContractor() {
		ContractorAccount contractor = new ContractorAccount();
		// contractor.setActive('Y');
		contractor.setId(counter++);
		contractor.setStatus(AccountStatus.Active);
		contractor.setName("Contractor Unit Test");
		contractor.setSafetyRisk(LowMedHigh.Med);
		contractor.setProductRisk(LowMedHigh.Med);
		contractor.setCountry(mostCommonCountries().get("US"));
		contractor.setOnsiteServices(true);
		contractor.setZip("99999");
		contractor.setAccountLevel(AccountLevel.Full);
		return contractor;
	}

	static public Employee makeEmployee(Account account) {
		Employee employee = new Employee();
		employee.setId(counter++);
		employee.setStatus(UserStatus.Active);
		employee.setFirstName("Unit");
		employee.setLastName("Tester");
		employee.setTitle("Title");
		employee.setAccount(account);

		return employee;
	}

	public static Map<String, Country> mostCommonCountries() {
		if (countries != null && countries.size() > 0) {
			return countries;
		}
		countries = new HashMap<String, Country>();
		countries.put("US", makeCountry("US", "United States"));
		countries.put("FR", makeCountry("FR", "France"));
		countries.put("AE", makeCountry("AE", "United Arab Emmerits"));
		countries.put("GB", makeCountry("GB", "United Kingdom"));
		countries.put("CA", makeCountry("CA", "Canada"));
		countries.put("MX", makeCountry("MX", "Mexico"));
		return countries;
	}

	public static Map<String, CountrySubdivision> someExampleCountrySubdivisions() {
		if (countrySubdivisions != null && countrySubdivisions.size() > 0) {
			return countrySubdivisions;
		}
		countrySubdivisions = new HashMap<String, CountrySubdivision>();

		Country unitedStates = mostCommonCountries().get("US");
		countrySubdivisions.put("CA", makeCountrySubdivision("CA", unitedStates, "California"));
		countrySubdivisions.put("TX", makeCountrySubdivision("TX", unitedStates, "Texas"));

		Country canada = mostCommonCountries().get("CA");
		countrySubdivisions.put("AB", makeCountrySubdivision("AB", canada, "Alberta"));
		countrySubdivisions.put("BC", makeCountrySubdivision("BC", canada, "British Columbia"));

		Country unitedKingdom = mostCommonCountries().get("GB");
		countrySubdivisions.put("BU", makeCountrySubdivision("BU", unitedKingdom, "Buckinghamshire"));
		countrySubdivisions.put("YK", makeCountrySubdivision("YK", unitedKingdom, "Yorkshire"));

		return countrySubdivisions;
	}

	public static CountrySubdivision makeCountrySubdivision(String isoCode, Country country, String englishName) {
		CountrySubdivision countrySubdivision = new CountrySubdivision(isoCode);
		countrySubdivision.setCountry(country);
		countrySubdivision.setEnglish(englishName);
		countrySubdivision.setName(makeEnglishString(isoCode, englishName));
		return countrySubdivision;
	}

	public static Country makeCountry(String isoCode, String englishName) {
		Country country = new Country(isoCode);
		country.setEnglish(englishName);
		country.setName(makeEnglishString(isoCode, englishName));
		return country;
	}

	public static TranslatableString makeEnglishString(String keyCode, String englishText) {
		TranslatableString translatableString = new TranslatableString();
		translatableString.setKey(keyCode);
		translatableString.putTranslation("en", englishText, true);
		return translatableString;
	}

	static public ContractorOperator addContractorOperator(ContractorAccount contractor, OperatorAccount operator) {
		ContractorOperator co = new ContractorOperator();
		co.setContractorAccount(contractor);
		co.setOperatorAccount(operator);
		contractor.getOperators().add(co);
		return co;
	}

	/**
	 * make an Active conAudit for the given contractor of the given typeID
	 *
	 * @param auditTypeID
	 * @return
	 */
	static public ContractorAudit makeContractorAudit(int auditTypeID, ContractorAccount contractor) {
		ContractorAudit conAudit = new ContractorAudit();
		conAudit.setAuditType(makeAuditType(auditTypeID));
		conAudit.setContractorAccount(contractor);
		return conAudit;
	}

	/**
	 * make an Active conAudit for the given contractor of the given typeID
	 *
	 * @param auditType
	 * @return
	 */
	static public ContractorAudit makeContractorAudit(AuditType auditType, ContractorAccount contractor) {
		ContractorAudit conAudit = new ContractorAudit();
		conAudit.setAuditType(auditType);
		conAudit.setContractorAccount(contractor);
		return conAudit;
	}

	static public ContractorAudit makeAnnualUpdate(int auditTypeID, ContractorAccount contractor, String auditFor) {
		ContractorAudit conAudit = new ContractorAudit();
		conAudit.setAuditType(makeAuditType(auditTypeID));
		conAudit.setContractorAccount(contractor);
		conAudit.setAuditFor(auditFor);
		conAudit.setCreationDate(new Date());
		return conAudit;
	}

	static public OshaAudit makeOshaAudit(ContractorAccount contractor, String auditFor) {
		ContractorAudit audit = makeAnnualUpdate(11, contractor, auditFor);
		OshaAudit oshaAudit = new OshaAudit(audit);
		return oshaAudit;
	}

	static public AuditCatData addCategories(ContractorAudit conAudit, int categoryID) {
		AuditCatData auditCatData = new AuditCatData();
		auditCatData.setAudit(conAudit);
		auditCatData.setCategory(new AuditCategory());
		auditCatData.getCategory().setId(categoryID);
		auditCatData.setNumRequired(4);
		return auditCatData;
	}

	static public AuditCategory addCategories(AuditType auditType, int categoryID, String name) {
		AuditCategory auditCategory = new AuditCategory();
		auditCategory.setId(categoryID);
		auditCategory.setAuditType(auditType);
		auditType.getCategories().add(auditCategory);
		auditCategory.setNumber(auditType.getCategories().get(auditType.getCategories().size() - 1).getNumber() + 1);
		auditCategory.setName(makeTranslatableString(name));
		return auditCategory;
	}

	// TODO: FIX ME
	static public OshaAudit makeShaLogs(ContractorAudit conAudit, int manHours) {
		/*
		 * OshaAudit oshaAudit = new OshaAudit();
		 * oshaAudit.setConAudit(conAudit); oshaAudit.setType(OshaType.OSHA);
		 * oshaAudit.setCorporate(true); oshaAudit.setFatalities(1);
		 * oshaAudit.setManHours(manHours); oshaAudit.setLostWorkCases(12);
		 * oshaAudit.setRecordableTotal(134); return oshaAudit;
		 */
		return null;
	}

	public static AuditType makeAuditType() {
		return makeAuditType(counter++);
	}

	static public AuditType makeAuditType(int auditTypeID) {
		AuditType auditType = new AuditType();
		auditType.setId(auditTypeID);
		auditType.setName(makeTranslatableString("Unit Test " + auditTypeID));
		auditType.setClassType(AuditTypeClass.Audit);
		auditType.setMonthsToExpire(12);
		Workflow workFlow = makeWorkflowNoSubmitted();
		auditType.setWorkFlow(workFlow);
		return auditType;
	}

	public static Workflow makeWorkflowNoSubmitted() {
		Workflow workFlow = new Workflow();
		WorkflowStep step1 = new WorkflowStep();
		step1.setOldStatus(AuditStatus.Pending);
		step1.setNewStatus(AuditStatus.Complete);

		WorkflowStep step2 = new WorkflowStep();
		step2.setOldStatus(AuditStatus.Resubmit);
		step2.setNewStatus(AuditStatus.Complete);

		List<WorkflowStep> steps = new ArrayList<WorkflowStep>();
		steps.add(step1);
		steps.add(step2);
		workFlow.setSteps(steps);
		return workFlow;
	}

	public static Workflow makeWorkflowWithSubmitted() {
		Workflow workFlow = new Workflow();

		WorkflowStep step1 = new WorkflowStep();
		step1.setOldStatus(AuditStatus.Pending);
		step1.setNewStatus(AuditStatus.Submitted);

		WorkflowStep step2 = new WorkflowStep();
		step2.setOldStatus(AuditStatus.Submitted);
		step2.setNewStatus(AuditStatus.Complete);

		List<WorkflowStep> steps = new ArrayList<WorkflowStep>();
		steps.add(step1);
		steps.add(step2);
		workFlow.setSteps(steps);
		return workFlow;
	}

	/**
	 * Add an Approved CAO to the passed in ConAudit
	 *
	 * @param conAudit
	 */
	static public ContractorAuditOperator addCao(ContractorAudit conAudit, OperatorAccount operator) {
		ContractorAuditOperator cao = new ContractorAuditOperator();
		cao.setAudit(conAudit);
		cao.setOperator(operator);
		cao.changeStatus(AuditStatus.Approved, null);
		cao.setFlag(FlagColor.Green);
		conAudit.getOperators().add(cao);
		return cao;
	}

	static public AuditQuestion makeAuditQuestion() {
		AuditQuestion question = new AuditQuestion();
		question.setId(counter++);
		question.setName(makeTranslatableString("jUnit Question " + question.getId()));
		question.setDependentRequired(new ArrayList<AuditQuestion>());
		question.setDependentVisible(new ArrayList<AuditQuestion>());
		long time = (new Date()).getTime();
		question.setEffectiveDate(new Date(time - (24 * 60 * 60 * 1000L)));
		question.setExpirationDate(new Date(time + (24 * 60 * 60 * 1000L)));
		question.setCategory(makeAuditCategory());
		return question;
	}

	public static AuditCategory makeAuditCategory() {
		return makeAuditCategory(counter++);
	}

	public static AuditCategory makeAuditCategory(int categoryId) {
		AuditCategory auditCategory = new AuditCategory();
		auditCategory.setId(categoryId);
		auditCategory.setName(makeTranslatableString("Audit Category " + categoryId));
		auditCategory.setNumber(categoryId);
		auditCategory.setAuditType(makeAuditType());
		return auditCategory;
	}

	static public AuditData makeAuditData(String answer) {
		return makeAuditData(answer, makeAuditQuestion());
	}

	static public AuditData makeAuditData(String answer, AuditQuestion question) {
		AuditData data = new AuditData();
		data.setQuestion(question);
		data.setAnswer(answer);
		data.setId(counter++);
		return data;
	}

	static public AuditData makeAuditData(String answer, int id) {
		AuditQuestion question = makeAuditQuestion();
		question.setId(id);
		return makeAuditData(answer, question);
	}

	static public Permissions makePermission() {
		User user = makeUser();
		return makePermission(user);
	}

	static public Permissions makePermission(User user) {
		Permissions permission = new Permissions();
		try {
            LanguageModel languageModel = mock(LanguageModel.class);
            when(languageModel.getNearestStableAndBetaLocale(any(Locale.class), anyString())).thenReturn(Locale.US);
            Whitebox.setInternalState(permission, "languageModel", languageModel);
			permission.login(user);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return permission;
	}

	static public User makeUser() {
		User user = new User(counter++);
		user.setAccount(new Account());
		user.getAccount().setId(Account.PicsID);
        user.getAccount().setCountry(mostCommonCountries().get("US"));
		return user;
	}

	@SuppressWarnings("rawtypes")
	static public User makeUser(Class clazz) {
		User user = new User(counter++);
		if (clazz.equals(OperatorAccount.class)) {
			user.setAccount(makeOperator());
		}
		if (clazz.equals(ContractorAccount.class)) {
			user.setAccount(makeContractor());
		}
		return user;
	}

	static public EmailSubscription makeEmailSubscription(User user, Subscription subscription,
														  SubscriptionTimePeriod timePeriod) {
		EmailSubscription sub = new EmailSubscription();

		sub.setUser(user);
		sub.setSubscription(subscription);
		sub.setTimePeriod(timePeriod);

		return sub;
	}

	public static AuditCatData makeAuditCatData() {
		AuditCategory category = new AuditCategory();
		AuditCatData catData = new AuditCatData();
		catData.setCategory(category);
		return catData;
	}

	@SuppressWarnings("deprecation")
	public static ContractorAuditOperator makeContractorAuditOperator(ContractorAudit audit, AuditStatus status) {
		ContractorAuditOperator cao = new ContractorAuditOperator();
		cao.setAudit(audit);
		cao.setStatus(status);
		cao.setVisible(true);
		return cao;
	}

	public static ContractorAuditOperator makeContractorAuditOperator(ContractorAudit audit) {
		return makeContractorAuditOperator(audit, AuditStatus.Complete);
	}

	public static Facility makeFacility(OperatorAccount operator, OperatorAccount corporate) {
		Facility facility = new Facility();
		facility.setOperator(operator);
		facility.setCorporate(corporate);
		return facility;
	}

	public static FlagCriteria makeFlagCriteria() {
		FlagCriteria flagCriteria = new FlagCriteria();
		flagCriteria.setId(counter++);
		flagCriteria.setCategory("Test");
		flagCriteria.setInsurance(false);
		return flagCriteria;
	}

	public static FlagCriteria makeFlagCriteriaAuditType() {
		FlagCriteria flagCriteria = makeFlagCriteria();
		flagCriteria.setAuditType(EntityFactory.makeAuditType(counter++));
		flagCriteria.setRequiredStatus(AuditStatus.Complete);

		return flagCriteria;
	}

	public static FlagCriteria makeFlagCriteriaAuditQuestion() {
		FlagCriteria flagCriteria = makeFlagCriteria();
		flagCriteria.setQuestion(EntityFactory.makeAuditQuestion());
		flagCriteria.setComparison("=");
		flagCriteria.setDataType(FlagCriteria.STRING);
		flagCriteria.setDefaultValue("Yes");

		return flagCriteria;
	}

	public static FlagData makeFlagData() {
		FlagData flagData = new FlagData();
		flagData.setId(counter++);
		flagData.setCriteria(makeFlagCriteriaAuditType());
		flagData.setContractor(makeContractor());
		flagData.setFlag(FlagColor.Red);

		return flagData;
	}

	public static FlagCriteriaContractor makeFlagCriteriaContractor(String answer) {
		FlagCriteriaContractor flagCriteriaContractor = new FlagCriteriaContractor();
		flagCriteriaContractor.setId(counter++);
		flagCriteriaContractor.setAnswer(answer);
		flagCriteriaContractor.setContractor(makeContractor());
		flagCriteriaContractor.setCriteria(makeFlagCriteria());

		return flagCriteriaContractor;
	}

	public static FlagCriteriaOperator makeFlagCriteriaOperator(String hurdle) {
		FlagCriteriaOperator flagCriterisOperator = new FlagCriteriaOperator();
		flagCriterisOperator.setId(counter++);
		flagCriterisOperator.setCriteria(makeFlagCriteria());
		flagCriterisOperator.setFlag(FlagColor.Red);
		flagCriterisOperator.setOperator(makeOperator());
		flagCriterisOperator.setHurdle(hurdle);


		return flagCriterisOperator;
	}

	public static void addUserPermission(Permissions permissions, OpPerms opPerm) {
		UserAccess userAccess = new UserAccess();
		userAccess.setOpPerm(opPerm);
		userAccess.setViewFlag(opPerm.usesView());
		userAccess.setEditFlag(opPerm.usesEdit());
		userAccess.setDeleteFlag(opPerm.usesDelete());
		userAccess.setGrantFlag(true);

		permissions.getPermissions().add(userAccess);
	}

}
