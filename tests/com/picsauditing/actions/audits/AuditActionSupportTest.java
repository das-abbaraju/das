package com.picsauditing.actions.audits;


import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.Workflow;
import com.picsauditing.jpa.entities.WorkflowStep;

public class AuditActionSupportTest extends PicsTest {
	private AuditActionSupport test;
	
	private ContractorAccount contractor;
	private OperatorAccount operator;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		
		test = new AuditActionSupport();
		autowireEMInjectedDAOs(test);

		contractor = EntityFactory.makeContractor();
		operator = EntityFactory.makeOperator();
		contractor.getOperatorAccounts().add(operator);
		EntityFactory.addContractorOperator(contractor, operator);
		
	}
	
	@Test
	public void testAuditSetExpiresDate_WCB() throws Exception  {
		ContractorAudit audit = createWCB();
		PicsTestUtil.forceSetPrivateField(test, "conAudit", audit);
		ContractorAuditOperator cao = audit.getOperators().get(0);

		audit.setExpiresDate(null);
		cao.setStatus(AuditStatus.Pending);
		test.auditSetExpiresDate(cao, AuditStatus.Pending);
		assertNull(audit.getExpiresDate());

		audit.setExpiresDate(null);
		cao.setStatus(AuditStatus.Incomplete);
		test.auditSetExpiresDate(cao, AuditStatus.Incomplete);
		assertNull(audit.getExpiresDate());

		audit.setExpiresDate(null);
		cao.setStatus(AuditStatus.Complete);
		test.auditSetExpiresDate(cao, AuditStatus.Complete);
		assertNull(audit.getExpiresDate());

		audit.setExpiresDate(null);
		cao.setStatus(AuditStatus.Resubmitted);
		test.auditSetExpiresDate(cao, AuditStatus.Resubmitted);
		assertNull(audit.getExpiresDate());

		audit.setExpiresDate(null);
		cao.setStatus(AuditStatus.Approved);
		test.auditSetExpiresDate(cao, AuditStatus.Approved);
		assertNotNull(audit.getExpiresDate());
	}
	
	private ContractorAudit createWCB() {
		ContractorAudit audit = EntityFactory.makeContractorAudit(145, contractor);
		audit.setAuditFor("2011");
		EntityFactory.addCao(audit, operator);
		Workflow workflow = new Workflow();
		workflow.setId(3);
		
		List<WorkflowStep> steps = new ArrayList<WorkflowStep>();
		steps.add(createWorkflowStep(null, AuditStatus.Pending));
		steps.add(createWorkflowStep(AuditStatus.Pending, AuditStatus.Submitted));
		steps.add(createWorkflowStep(AuditStatus.Submitted, AuditStatus.Incomplete));
		steps.add(createWorkflowStep(AuditStatus.Submitted, AuditStatus.Complete));
		steps.add(createWorkflowStep(AuditStatus.Complete, AuditStatus.Approved));
		steps.add(createWorkflowStep(AuditStatus.Complete, AuditStatus.Incomplete));
		steps.add(createWorkflowStep(AuditStatus.Incomplete, AuditStatus.Resubmitted));
		steps.add(createWorkflowStep(AuditStatus.Resubmitted, AuditStatus.Incomplete));
		steps.add(createWorkflowStep(AuditStatus.Resubmitted, AuditStatus.Complete));
		
		workflow.setSteps(steps);
		audit.getAuditType().setWorkFlow(workflow);
		
		return audit;
	}
	
	private WorkflowStep createWorkflowStep(AuditStatus oldStatus, AuditStatus status) {
		WorkflowStep step = new WorkflowStep();
		step.setOldStatus(oldStatus);
		step.setNewStatus(status);
		
		return step;
	}

}
