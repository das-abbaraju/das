package com.picsauditing;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.picsauditing.jpa.entities.*;
import org.powermock.reflect.Whitebox;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.access.UserAccess;
import com.picsauditing.mail.Subscription;
import com.picsauditing.mail.SubscriptionTimePeriod;
import com.picsauditing.model.i18n.LanguageModel;

/**
 * This generates jpa objects that we can then use in our unit testing
 * 
 * @author Trevor
 */
public class EntityFactory {

	private static int counter = 100;
	private static HashMap<String, Country> countries;
	private static Map<String, CountrySubdivision> countrySubdivisions;

	public static OperatorAccount makeOperator() {
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
	public static ContractorAccount makeContractor() {
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

	public static Employee makeEmployee(Account account) {
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
		countrySubdivision.setName(englishName);
		return countrySubdivision;
	}

	public static Country makeCountry(String isoCode, String englishName) {
		Country country = new Country(isoCode);
		country.setEnglish(englishName);
		country.setName(englishName);
		return country;
	}

	public static ContractorOperator addContractorOperator(ContractorAccount contractor, OperatorAccount operator) {
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
	public static ContractorAudit makeContractorAudit(int auditTypeID, ContractorAccount contractor) {
		ContractorAudit conAudit = new ContractorAudit();
		conAudit.setAuditType(makeAuditType(auditTypeID));
		conAudit.setContractorAccount(contractor);
		contractor.getAudits().add(conAudit);
		return conAudit;
	}

	/**
	 * make an Active conAudit for the given contractor of the given typeID
	 * 
	 * @param auditType
	 * @return
	 */
	public static ContractorAudit makeContractorAudit(AuditType auditType, ContractorAccount contractor) {
		ContractorAudit conAudit = new ContractorAudit();
		conAudit.setAuditType(auditType);
		conAudit.setContractorAccount(contractor);
		return conAudit;
	}

	public static ContractorAudit makeAnnualUpdate(int auditTypeID, ContractorAccount contractor, String auditFor) {
		ContractorAudit conAudit = new ContractorAudit();
		conAudit.setAuditType(makeAuditType(auditTypeID));
		conAudit.setContractorAccount(contractor);
		conAudit.setAuditFor(auditFor);
		conAudit.setCreationDate(new Date());
		return conAudit;
	}

	public static OshaAudit makeOshaAudit(ContractorAccount contractor, String auditFor) {
		ContractorAudit audit = makeAnnualUpdate(11, contractor, auditFor);
		OshaAudit oshaAudit = new OshaAudit(audit);
		return oshaAudit;
	}

	public static AuditCatData addCategories(ContractorAudit conAudit, int categoryID) {
		AuditCatData auditCatData = new AuditCatData();
		auditCatData.setAudit(conAudit);
		auditCatData.setCategory(new AuditCategory());
		auditCatData.getCategory().setId(categoryID);
		auditCatData.setNumRequired(4);
		return auditCatData;
	}

	public static AuditCategory addCategories(AuditType auditType, int categoryID, String name) {
		AuditCategory auditCategory = new AuditCategory();
		auditCategory.setId(categoryID);
		auditCategory.setAuditType(auditType);
		auditType.getCategories().add(auditCategory);
		auditCategory.setNumber(auditType.getCategories().get(auditType.getCategories().size() - 1).getNumber() + 1);
		auditCategory.setName(name);
		return auditCategory;
	}

	// TODO: FIX ME
	public static OshaAudit makeShaLogs(ContractorAudit conAudit, int manHours) {
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

	public static AuditType makeAuditType(int auditTypeID) {
		AuditType auditType = new AuditType();
		auditType.setId(auditTypeID);
		auditType.setName("Unit Test " + auditTypeID);
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
	public static ContractorAuditOperator addCao(ContractorAudit conAudit, OperatorAccount operator) {
		ContractorAuditOperator cao = new ContractorAuditOperator();
		cao.setAudit(conAudit);
		cao.setOperator(operator);
		cao.changeStatus(AuditStatus.Approved, null);
		cao.setFlag(FlagColor.Green);
		conAudit.getOperators().add(cao);
		return cao;
	}

	public static AuditQuestion makeAuditQuestion() {
		AuditQuestion question = new AuditQuestion();
		question.setId(counter++);
		question.setName("jUnit Question " + question.getId());
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
		auditCategory.setName("Audit Category " + categoryId);
		auditCategory.setNumber(categoryId);
		auditCategory.setAuditType(makeAuditType());
		auditCategory.setEffectiveDate(new Date());
		auditCategory.setExpirationDate(AuditQuestion.END_OF_TIME);
		return auditCategory;
	}

	public static AuditData makeAuditData(String answer) {
		return makeAuditData(answer, makeAuditQuestion());
	}

	public static AuditData makeAuditData(String answer, AuditQuestion question) {
		AuditData data = new AuditData();
		data.setQuestion(question);
		data.setAnswer(answer);
		data.setId(counter++);
		return data;
	}

	public static AuditData makeAuditData(String answer, int id) {
		AuditQuestion question = makeAuditQuestion();
		question.setId(id);
		return makeAuditData(answer, question);
	}

	public static Permissions makePermission() {
		User user = makeUser();
		return makePermission(user);
	}

	public static Permissions makePermission(User user) {
		Permissions permission = new Permissions();
		try {
			LanguageModel languageModel = mock(LanguageModel.class);
			when(languageModel.getClosestVisibleLocale(any(Locale.class), anyString())).thenReturn(Locale.US);
			Whitebox.setInternalState(permission, "languageModel", languageModel);
			permission.login(user);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return permission;
	}

	public static User makeUser() {
		User user = new User(counter++);
		user.setAccount(new Account());
		user.getAccount().setId(Account.PicsID);
		user.getAccount().setCountry(mostCommonCountries().get("US"));
		return user;
	}

	@SuppressWarnings("rawtypes")
	public static User makeUser(Class clazz) {
		User user = new User(counter++);
		if (clazz.equals(OperatorAccount.class)) {
			user.setAccount(makeOperator());
		}
		if (clazz.equals(ContractorAccount.class)) {
			user.setAccount(makeContractor());
		}
		return user;
	}

	public static EmailSubscription makeEmailSubscription(User user, Subscription subscription,
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
		flagCriteria.setCategory(FlagCriteriaCategory.Audits);
		flagCriteria.setInsurance(false);
		return flagCriteria;
	}

	public static FlagCriteria makeFlagCriteriaAuditType() {
		FlagCriteria flagCriteria = makeFlagCriteria();
		flagCriteria.setAuditType(EntityFactory.makeAuditType(counter++));
		flagCriteria.setRequiredStatus(AuditStatus.Complete);

		return flagCriteria;
	}

    public static FlagCriteria makeFlagCriteriaAuditQuestion(AuditType auditType) {
		FlagCriteria flagCriteria = makeFlagCriteria();
        AuditQuestion question = EntityFactory.makeAuditQuestion();
        question.getCategory().setAuditType(auditType);
        flagCriteria.setQuestion(question);
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
