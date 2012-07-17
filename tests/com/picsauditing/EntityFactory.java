package com.picsauditing;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AccountLevel;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.FlagCriteriaContractor;
import com.picsauditing.jpa.entities.FlagData;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.State;
import com.picsauditing.jpa.entities.TranslatableString;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserStatus;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.mail.Subscription;
import com.picsauditing.mail.SubscriptionTimePeriod;

/**
 * This generates jpa objects that we can then use in our unit testing
 * 
 * @author Trevor
 * 
 */
public class EntityFactory {

	static private int counter = 1;
	static private HashMap<String, Country> countries;
	static private Map<String, State> states;

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
		countries = new HashMap();
		countries.put("US", makeCountry("US", "United States"));
		countries.put("FR", makeCountry("FR", "France"));
		countries.put("AE", makeCountry("AE", "United Arab Emmerits"));
		countries.put("GB", makeCountry("GB", "United Kingdom"));
		countries.put("CA", makeCountry("CA", "Canada"));
		countries.put("MX", makeCountry("MX", "Mexico"));
		return countries;
	}

	public static Map<String, State> someExampleStates() {
		if (states != null && states.size() > 0) {
			return states;
		}
		states = new HashMap();

		Country unitedStates = mostCommonCountries().get("US");
		states.put("CA", makeState("CA", unitedStates, "California"));
		states.put("TX", makeState("TX", unitedStates, "Texas"));

		Country canada = mostCommonCountries().get("CA");
		states.put("AB", makeState("AB", canada, "Alberta"));
		states.put("BC", makeState("BC", canada, "British Columbia"));

		Country unitedKingdom = mostCommonCountries().get("GB");
		states.put("BU", makeState("BU", unitedKingdom, "Buckinghamshire"));
		states.put("YK", makeState("YK", unitedKingdom, "Yorkshire"));

		return states;
	}

	public static State makeState(String isoCode, Country country, String englishName) {
		State state = new State(isoCode);
		state.setCountry(country);
		state.setEnglish(englishName);
		state.setName(makeEnglishString(isoCode, englishName));
		return state;
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
	 * @param auditTypeID
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

	static public AuditType makeAuditType(int auditTypeID) {
		AuditType auditType = new AuditType();
		auditType.setId(auditTypeID);
		auditType.setName(makeTranslatableString("Unit Test " + auditTypeID));
		auditType.setClassType(AuditTypeClass.Audit);
		auditType.setMonthsToExpire(12);
		return auditType;
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
			permission.setAccountPerms(user);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return permission;
	}

	static public User makeUser() {
		User user = new User(counter++);
		user.setAccount(new Account());
		user.getAccount().setId(1100);
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
}
