package com.picsauditing;

import java.util.Locale;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Account;
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
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.jpa.entities.TranslatableString;
import com.picsauditing.jpa.entities.User;
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
		contractor.setCountry(new Country("US"));
		contractor.setOnsiteServices(true);
		contractor.setZip("99999");
		return contractor;
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

	static public ContractorAudit makeAnnualUpdate(int auditTypeID, ContractorAccount contractor, String auditFor) {
		ContractorAudit conAudit = new ContractorAudit();
		conAudit.setAuditType(makeAuditType(auditTypeID));
		conAudit.setContractorAccount(contractor);
		conAudit.setAuditFor(auditFor);
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
		return question;
	}

	static public AuditData makeAuditData(String answer) {
		return makeAuditData(answer, makeAuditQuestion());
	}

	static public AuditData makeAuditData(String answer, AuditQuestion question) {
		AuditData data = new AuditData();
		data.setQuestion(question);
		data.setAnswer(answer);
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
}
