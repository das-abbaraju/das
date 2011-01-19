package com.picsauditing;

import java.math.BigDecimal;

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
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.OshaType;
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
		contractor.setStatus(AccountStatus.Active);
		contractor.setName("Contractor Unit Test");
		contractor.setRiskLevel(LowMedHigh.Med);
		contractor.setCountry(new Country("US"));
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
		auditCategory.setName(name == null ? "Test Category " + categoryID : name);
		auditCategory.setNumber(auditType.getCategories().get(auditType.getCategories().size() - 1).getNumber() + 1);
		return auditCategory;
	}

	static public OshaAudit makeShaLogs(ContractorAudit conAudit, int manHours) {
		OshaAudit oshaAudit = new OshaAudit();
		oshaAudit.setConAudit(conAudit);
		oshaAudit.setType(OshaType.OSHA);
		oshaAudit.setCorporate(true);
		oshaAudit.setFatalities(1);
		oshaAudit.setManHours(manHours);
		oshaAudit.setLostWorkCases(12);
		oshaAudit.setRecordableTotal(134);
		return oshaAudit;
	}

	static public AuditType makeAuditType(int auditTypeID) {
		AuditType auditType = new AuditType();
		auditType.setId(auditTypeID);
		auditType.setAuditName("Unit Test " + auditTypeID);
		auditType.setClassType(AuditTypeClass.Audit);
		return auditType;
	}

	/**
	 * Add an Approved CAO to the passed in ConAudit
	 * 
	 * @param conAudit
	 */
	static public void addCao(ContractorAudit conAudit, OperatorAccount operator) {
		ContractorAuditOperator cao = new ContractorAuditOperator();
		cao.setAudit(conAudit);
		cao.setOperator(operator);
		cao.changeStatus(AuditStatus.Approved, null);
		cao.setFlag(FlagColor.Green);
		conAudit.getOperators().add(cao);
	}

	static public AuditQuestion makeAuditQuestion() {
		AuditQuestion question = new AuditQuestion();
		question.setName("jUnit Question");
		return question;
	}

	static public AuditData makeAuditData(String answer) {
		AuditData data = new AuditData();
		data.setQuestion(EntityFactory.makeAuditQuestion());
		data.setAnswer(answer);
		return data;
	}

	/**
	 * Creates a fee that would normally be stored in the database without
	 * having to access a DAO
	 * 
	 * @param feeID
	 * @return
	 */
	static public InvoiceFee makeInvoiceFee(int feeID) {
		InvoiceFee fee = new InvoiceFee(feeID);
		fee.setFeeClass("Membership");

		if (feeID == InvoiceFee.ACTIVATION || feeID == InvoiceFee.REACTIVATION)
			fee.setFeeClass("Activation");

		int amount = 0;

		switch (feeID) {
		case InvoiceFee.PQFONLY:
			amount = 99;
			break;
		case InvoiceFee.FACILITIES1:
			amount = 399;
			break;
		case InvoiceFee.FACILITIES2:
			amount = 699;
			break;
		case InvoiceFee.FACILITIES5:
			amount = 999;
			break;
		case InvoiceFee.FACILITIES9:
			amount = 1399;
			break;
		case InvoiceFee.FACILITIES13:
			amount = 1699;
			break;
		case InvoiceFee.FACILITIES20:
			amount = 1999;
			break;
		}
		fee.setAmount(new BigDecimal(amount));

		return fee;
	}

	static public EmailSubscription makeEmailSubscription(User user, Subscription subscription,
			SubscriptionTimePeriod timePeriod) {
		EmailSubscription sub = new EmailSubscription();

		sub.setUser(user);
		sub.setSubscription(subscription);
		sub.setTimePeriod(timePeriod);

		return sub;
	}
}
