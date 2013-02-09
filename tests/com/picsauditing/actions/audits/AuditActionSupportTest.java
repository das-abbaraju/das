package com.picsauditing.actions.audits;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.Permissions;
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

	@Mock
	Permissions permissions;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		MockitoAnnotations.initMocks(this);

		test = new AuditActionSupport();
		autowireEMInjectedDAOs(test);
		PicsTestUtil.forceSetPrivateField(test, "permissions", permissions);

		contractor = EntityFactory.makeContractor();
		operator = EntityFactory.makeOperator();
		contractor.getOperatorAccounts().add(operator);
		EntityFactory.addContractorOperator(contractor, operator);

	}

	@SuppressWarnings("deprecation")
	@Test
	public void testAuditSetExpiresDate_WCB() throws Exception {
		ContractorAudit audit = createWCB();
		PicsTestUtil.forceSetPrivateField(test, "conAudit", audit);

		ContractorAuditOperator cao = audit.getOperators().get(0);

		cao.setStatus(AuditStatus.Pending);
		test.auditSetExpiresDate(cao, AuditStatus.Pending);
		assertTrue(compareDates(DateBean.getWCBExpirationDate(audit.getAuditFor()), audit.getExpiresDate()));

		cao.setStatus(AuditStatus.Approved);
		test.auditSetExpiresDate(cao, AuditStatus.Approved);
		assertTrue(compareDates(DateBean.getWCBExpirationDate(audit.getAuditFor()), audit.getExpiresDate()));
	}

	private ContractorAudit createWCB() {
		ContractorAudit audit = EntityFactory.makeContractorAudit(145, contractor);
		audit.setAuditFor("2011");
		audit.setExpiresDate(DateBean.getWCBExpirationDate("2011"));
		EntityFactory.addCao(audit, operator);
		Workflow workflow = new Workflow();
		workflow.setId(3);

		List<WorkflowStep> steps = new ArrayList<WorkflowStep>();
		steps.add(createWorkflowStep(null, AuditStatus.Pending));
		steps.add(createWorkflowStep(AuditStatus.Pending, AuditStatus.Submitted));
		steps.add(createWorkflowStep(AuditStatus.Submitted, AuditStatus.Incomplete));
		steps.add(createWorkflowStep(AuditStatus.Submitted, AuditStatus.Approved));
		steps.add(createWorkflowStep(AuditStatus.Incomplete, AuditStatus.Resubmitted));
		steps.add(createWorkflowStep(AuditStatus.Resubmitted, AuditStatus.Incomplete));
		steps.add(createWorkflowStep(AuditStatus.Resubmitted, AuditStatus.Approved));

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

	/**
	 * Compare the Year, Month, and Day of two dates (not the times)
	 */
	@SuppressWarnings("deprecation")
	private boolean compareDates(Date date1, Date date2) {
		if (date1 == null && date2 == null) {
			return true;
		} else if (date1 == null || date2 == null) {
			return false;
		}

		return (date1.getYear() == date2.getYear() && date1.getMonth() == date2.getMonth() && date1.getDate() == date2
				.getDate());
	}

	@Test
	public void testCanPerformAction() throws Exception {
		ContractorAudit audit = createWCB();
		PicsTestUtil.forceSetPrivateField(test, "conAudit", audit);
		ContractorAuditOperator cao = audit.getOperators().get(0);
		cao.setPercentComplete(100);
		when(permissions.seesAllContractors()).thenReturn(true);

		WorkflowStep step = createWorkflowStep(AuditStatus.Submitted, AuditStatus.Approved);
		Boolean value = Whitebox.invokeMethod(test, "canPerformAction", cao, step);
		assertTrue(value);
	}

}
