package com.picsauditing.PICS;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import com.picsauditing.EntityFactory;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.CaoStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.Naics;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.jpa.entities.WaitingOn;
import com.picsauditing.jpa.entities.YesNo;

public class FlagCalculatorTest extends TestCase {
	/* Create the main variables */
	private FlagCalculatorSingle calculator;
	private ContractorAccount contractor;
	private OperatorAccount operator;
	private List<ContractorAudit> conAudits;
	
	@Override
	/**
	 * Create a contractor, operator, and an empty conAudits and acaList
	 */
	protected void setUp() throws Exception {
		super.setUp();

		/* Create the main variables */
		contractor = EntityFactory.makeContractor();
		operator = EntityFactory.makeOperator();
		
		EntityFactory.addContractorOperator(contractor, operator);
		
		conAudits = new ArrayList<ContractorAudit>();
		
		/* Initialize the calculator */
		calculator = new FlagCalculatorSingle();
		calculator.setContractor(contractor);
		calculator.setOperator(operator);
		calculator.setConAudits(conAudits);
	}
	
	/***** Unit Tests *************/
	
	public void testEmptyContractor() {
		// Contractors with no requirements at all should default to Green
		operator.getAudits().add(EntityFactory.makeAuditOperator(1, operator));
		assertEquals(FlagColor.Green, calculator.calculate());
		assertEquals(WaitingOn.None, calculator.calculateWaitingOn());
		
		operator.setApprovesRelationships(YesNo.Yes);
		assertEquals(WaitingOn.Operator, calculator.calculateWaitingOn());

		contractor.getOperators().get(0).setWorkStatus("Y");
		assertEquals(WaitingOn.None, calculator.calculateWaitingOn());

		//contractor.setActive('N');
		contractor.setStatus(AccountStatus.Deactivated);
		assertEquals(WaitingOn.Contractor, calculator.calculateWaitingOn());
		
    }
	
	public void testAudits() {
		// We're missing the required PQF
		operator.getAudits().add(EntityFactory.makeAuditOperator(1, operator));
		contractor.setRiskLevel(LowMedHigh.High);
		assertEquals(FlagColor.Red, calculator.calculate());

		contractor.setRiskLevel(LowMedHigh.Low);
		assertEquals(FlagColor.Green, calculator.calculate());
		contractor.setRiskLevel(LowMedHigh.High);

		ContractorAudit conAudit = EntityFactory.makeContractorAudit(1, contractor);
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

//		operator.getAudits().get(0).setRequiredForFlag(FlagColor.Amber);
		assertEquals(FlagColor.Amber, calculator.calculate());
		assertEquals(WaitingOn.PICS, calculator.calculateWaitingOn());
		
//		operator.getAudits().get(0).setRequiredAuditStatus(AuditStatus.Submitted);
		assertEquals(FlagColor.Green, calculator.calculate());
		assertEquals(WaitingOn.None, calculator.calculateWaitingOn());
		
		operator.getAudits().get(0).setCanSee(false);
		assertEquals(FlagColor.Green, calculator.calculate());
		assertEquals(WaitingOn.None, calculator.calculateWaitingOn());
	}
	
	public void testPolicies() {
		// Require the Policy 1
		AuditOperator ao = EntityFactory.makeAuditOperator(1, operator);
		ao.getAuditType().setClassType(AuditTypeClass.Policy);

		// Add the Policy 1 to the contractor
		// Make sure that the flag remains red no matter what the AuditStatus
		ContractorAudit conAudit = EntityFactory.makeContractorAudit(1, contractor);
		conAudit.getAuditType().setClassType(AuditTypeClass.Policy);
		conAudits.add(conAudit);
		assertEquals(FlagColor.Red, calculator.calculate());

		conAudit.setAuditStatus(AuditStatus.Submitted);
		assertEquals(FlagColor.Red, calculator.calculate());
		
		conAudit.setAuditStatus(AuditStatus.Pending);
		assertEquals(FlagColor.Red, calculator.calculate());
		
		// Now have the operator approve this audit
		EntityFactory.addCao(conAudit, operator); // Approved
		assertEquals(FlagColor.Green, calculator.calculate());
		
		ContractorAuditOperator cao = conAudit.getOperators().get(0);
		cao.setStatus(CaoStatus.Rejected);
		assertEquals(FlagColor.Red, calculator.calculate());
		
		cao.setFlag(FlagColor.Red);
		assertEquals(FlagColor.Red, calculator.calculate());

		cao.setStatus(CaoStatus.NotApplicable);
		assertEquals(FlagColor.Green, calculator.calculate());

		conAudit.setAuditStatus(AuditStatus.Pending);
		assertEquals("Pending policies where the operator said the cao was N/A are not required", 
				FlagColor.Green, calculator.calculate());

	}
	
	public void testAuditAnswers() {
		// Add the PQF
		operator.getAudits().add(EntityFactory.makeAuditOperator(1, operator));
		ContractorAudit conAudit = EntityFactory.makeContractorAudit(1, contractor);
		conAudits.add(conAudit);

	}
}
