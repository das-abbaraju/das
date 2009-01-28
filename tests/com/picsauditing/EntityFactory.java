package com.picsauditing;

import java.util.HashMap;
import java.util.HashSet;
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
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagQuestionCriteria;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.YesNo;

/**
 * This generates jpa objects that we can then use in our unit testing
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
		co.setWorkStatus("P");
		contractor.getOperators().add(co);
		return co;
	}
	
	/**
	 * Make an AuditOperator that canSee = true, canEdit = false, AuditStatus = Active, and RiskLevel = Medium
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
		auditType.setAuditTypeID(auditTypeID);
		auditType.setAuditName("Unit Test " + auditTypeID);
		auditType.setClassType(AuditTypeClass.Audit);
		return auditType;
	}
	
	/**
	 * Add an Approved CAO to the passed in ConAudit
	 * @param conAudit
	 */
	static public void addCao(ContractorAudit conAudit, OperatorAccount operator) {
		ContractorAuditOperator cao = new ContractorAuditOperator();
		cao.setAudit(conAudit);
		cao.setOperator(operator);
		cao.setStatus(CaoStatus.Approved);
		cao.setRecommendedStatus(CaoStatus.Approved);
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
}
