package com.picsauditing.PICS;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.CaoStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.WaitingOn;
import com.picsauditing.jpa.entities.YesNo;

public class FlagCalculatorTest extends TestCase {
	/* Create the main variables */
	private FlagCalculatorSingle calculator;
	private ContractorAccount contractor;
	private OperatorAccount operator;
	private List<ContractorAudit> conAudits;
	private List<AuditCriteriaAnswer> acaList;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		/* Create the main variables */
		calculator = new FlagCalculatorSingle();

		contractor = new ContractorAccount();
		contractor.setActive('Y');
		contractor.setName("Contractor Unit Test");
		contractor.setRiskLevel(LowMedHigh.Med);
		
		operator = new OperatorAccount();
		contractor.setActive('Y');
		operator.setName("Operator Unit Test");
		operator.setApprovesRelationships(YesNo.No);
		
		ContractorOperator co = new ContractorOperator();
		co.setContractorAccount(contractor);
		co.setOperatorAccount(operator);
		co.setWorkStatus("P");
		contractor.getOperators().add(co);
		
		conAudits = new ArrayList<ContractorAudit>();
		acaList = new ArrayList<AuditCriteriaAnswer>();
		
		/* Initialize the calculator */
		calculator.setContractor(contractor);
		calculator.setOperator(operator);
		calculator.setConAudits(conAudits);
		calculator.setAcaList(acaList);
	}
	
	/***** Unit Tests *************/
	
	public void testEmptyContractor() {
		// Contractors with no requirements at all should default to Green
		assertEquals(FlagColor.Green, calculator.calculate());
		assertEquals(WaitingOn.None, calculator.calculateWaitingOn());
		
		operator.setApprovesRelationships(YesNo.Yes);
		assertEquals(WaitingOn.Operator, calculator.calculateWaitingOn());

		contractor.getOperators().get(0).setWorkStatus("Y");
		assertEquals(WaitingOn.None, calculator.calculateWaitingOn());

		contractor.setActive('N');
		assertEquals(WaitingOn.Contractor, calculator.calculateWaitingOn());
		
    }
	
	public void testAudits() {
		// We're missing the required PQF
		operator.getAudits().add(createAuditOperator(1));
		contractor.setRiskLevel(LowMedHigh.High);
		assertEquals(FlagColor.Red, calculator.calculate());

		contractor.setRiskLevel(LowMedHigh.Low);
		assertEquals(FlagColor.Green, calculator.calculate());
		contractor.setRiskLevel(LowMedHigh.High);

		ContractorAudit conAudit = createContractorAudit(1);
		conAudits.add(conAudit);
		// Now we have a valid PQF
		assertEquals(FlagColor.Green, calculator.calculate());
		assertEquals(WaitingOn.None, calculator.calculateWaitingOn());

		conAudit.setAuditStatus(AuditStatus.Exempt);
		assertEquals(FlagColor.Green, calculator.calculate());
		assertEquals(WaitingOn.None, calculator.calculateWaitingOn());

		conAudit.setAuditStatus(AuditStatus.Resubmitted);
		assertEquals(FlagColor.Green, calculator.calculate());
		assertEquals(WaitingOn.PICS, calculator.calculateWaitingOn());

		conAudit.setAuditStatus(AuditStatus.Pending);
		assertEquals(FlagColor.Red, calculator.calculate());
		assertEquals(WaitingOn.Contractor, calculator.calculateWaitingOn());

		conAudit.setAuditStatus(AuditStatus.Submitted);
		assertEquals(FlagColor.Red, calculator.calculate());
		assertEquals(WaitingOn.PICS, calculator.calculateWaitingOn());

		operator.getAudits().get(0).setRequiredForFlag(FlagColor.Amber);
		assertEquals(FlagColor.Amber, calculator.calculate());
		assertEquals(WaitingOn.PICS, calculator.calculateWaitingOn());
		
		operator.getAudits().get(0).setRequiredAuditStatus(AuditStatus.Submitted);
		assertEquals(FlagColor.Green, calculator.calculate());
		assertEquals(WaitingOn.None, calculator.calculateWaitingOn());
		
		operator.getAudits().get(0).setCanSee(false);
		assertEquals(FlagColor.Green, calculator.calculate());
		assertEquals(WaitingOn.None, calculator.calculateWaitingOn());
	}
	
	public void testPolicies() {
		operator.getAudits().add(createAuditOperator(1));
		operator.getAudits().get(0).getAuditType().setClassType(AuditTypeClass.Policy);

		ContractorAudit conAudit = createContractorAudit(1);
		conAudits.add(conAudit);
		conAudit.getAuditType().setClassType(AuditTypeClass.Policy);
		
		assertEquals(FlagColor.Red, calculator.calculate());
		
		addCao(conAudit); // Approved
		ContractorAuditOperator cao = conAudit.getOperators().get(0);
		assertEquals(FlagColor.Green, calculator.calculate());
		
		cao.setStatus(CaoStatus.Rejected);
		assertEquals(FlagColor.Red, calculator.calculate());
		
		cao.setRecommendedStatus(CaoStatus.NotApplicable);
		assertEquals(FlagColor.Green, calculator.calculate());
		
		conAudit.setAuditStatus(AuditStatus.Pending);
		assertEquals("Pending policies where the operator said the cao was N/A are not required", 
				FlagColor.Green, calculator.calculate());

	}
	
	public void testAuditAnswers() {
		
	}
	
	/******* Helper Methods ************/
	
	/**
	 * Create an AuditOperator that is Visible, and  
	 * requires an Active audit for Contractors of Medium risk level
	 * @param auditTypeID
	 * @return
	 */
	private AuditOperator createAuditOperator(int auditTypeID) {
		AuditOperator ao = new AuditOperator();
		ao.setOperatorAccount(operator);
		ao.setAuditType(createAuditType(auditTypeID));
		ao.setCanSee(true);
		ao.setCanEdit(false);
		ao.setRequiredAuditStatus(AuditStatus.Active);
		ao.setRequiredForFlag(FlagColor.Red);
		ao.setMinRiskLevel(2); // Medium Risk
		return ao;
	}
	
	/**
	 * Create an Active conAudit
	 * @param auditTypeID
	 * @return
	 */
	private ContractorAudit createContractorAudit(int auditTypeID) {
		ContractorAudit conAudit = new ContractorAudit();
		conAudit.setAuditType(createAuditType(auditTypeID));
		conAudit.setContractorAccount(contractor);
		conAudit.setAuditStatus(AuditStatus.Active);
		return conAudit;
	}
	
	private AuditType createAuditType(int auditTypeID) {
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
	private void addCao(ContractorAudit conAudit) {
		ContractorAuditOperator cao = new ContractorAuditOperator();
		cao.setAudit(conAudit);
		cao.setOperator(operator);
		cao.setStatus(CaoStatus.Approved);
		cao.setRecommendedStatus(CaoStatus.Approved);
		conAudit.getOperators().add(cao);
	}
}
