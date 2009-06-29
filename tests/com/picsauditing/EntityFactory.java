package com.picsauditing;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.picsauditing.PICS.AuditCriteriaAnswer;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.CaoStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagQuestionCriteria;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.OperatorAccount;
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

	static public OperatorAccount makeOperator() {
		OperatorAccount operator = new OperatorAccount();
		operator.setActive('Y');
		operator.setName("Operator Unit Test");
		operator.setApprovesRelationships(YesNo.No);
		operator.setCanSeeInsurance(YesNo.Yes);
		return operator;
	}

	/**
	 * Create an active, Medium Risk Level ContractorAccount named Contractor Unit Test
	 * 
	 * @return
	 */
	static public ContractorAccount makeContractor() {
		ContractorAccount contractor = new ContractorAccount();
		contractor.setActive('Y');
		contractor.setName("Contractor Unit Test");
		contractor.setRiskLevel(LowMedHigh.Med);
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
	 * Make an AuditOperator that canSee = true, canEdit = false, AuditStatus = Active, and RiskLevel = Medium
	 * 
	 * @param auditTypeID
	 * @param operator
	 * @return
	 */
	static public AuditOperator makeAuditOperator(int auditTypeID, OperatorAccount operator) {
		AuditOperator ao = new AuditOperator();
		ao.setOperatorAccount(operator);
		ao.setAuditType(makeAuditType(auditTypeID));
		ao.setCanSee(true);
		ao.setCanEdit(false);
		ao.setRequiredAuditStatus(AuditStatus.Active);
		ao.setRequiredForFlag(FlagColor.Red);
		ao.setMinRiskLevel(2); // Medium Risk
		operator.getAudits().add(ao);
		return ao;
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
		conAudit.setAuditStatus(AuditStatus.Active);
		return conAudit;
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
		cao.setStatus(CaoStatus.Approved);
		cao.setFlag(FlagColor.Green);
		conAudit.getOperators().add(cao);
	}

	static public void addAuditCriteriaAnswer(ContractorAudit conAudit, AuditQuestion question, String answer) {
		AuditData data = new AuditData();
		data.setAudit(conAudit);
		data.setQuestion(question);
		data.setAnswer(answer);
		Map<FlagColor, FlagQuestionCriteria> criteriaMap = new HashMap<FlagColor, FlagQuestionCriteria>();
		AuditCriteriaAnswer aka = new AuditCriteriaAnswer(data, criteriaMap);
	}

	static public AuditQuestion makeAuditQuestion() {
		AuditQuestion question = new AuditQuestion();
		question.setQuestion("jUnit Question");
		return question;
	}

	static public AuditData makeAuditData(String answer) {
		AuditData data = new AuditData();
		data.setQuestion(EntityFactory.makeAuditQuestion());
		data.setAnswer(answer);
		return data;
	}

	/**
	 * Creates a fee that would normally be stored in the database without having to access a DAO
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
	
	static public EmailSubscription makeEmailSubscription(User user, Subscription subscription, SubscriptionTimePeriod timePeriod) {
		EmailSubscription sub = new EmailSubscription();
		
		sub.setUser(user);
		sub.setSubscription(subscription);
		sub.setTimePeriod(timePeriod);
		
		return sub;
	}
}
