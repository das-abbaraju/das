package com.picsauditing.actions.audits;

import com.picsauditing.EntityFactory;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.PicsTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.*;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

public class AuditActionSupportTest extends PicsTest {
    private static final Integer OPERATOR_GROUP_ID = 44;
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
        Set<Integer> groupIds = new TreeSet<Integer>();
        groupIds.add(OPERATOR_GROUP_ID);
        when(permissions.getAllInheritedGroupIds()).thenReturn(groupIds);


        contractor = EntityFactory.makeContractor();
		operator = EntityFactory.makeOperator();
		contractor.getOperatorAccounts().add(operator);
		EntityFactory.addContractorOperator(contractor, operator);

	}

	@Test
	public void testIsCanEditAudit() {
		ContractorAudit audit = EntityFactory.makeContractorAudit(200, contractor);
		audit.getAuditType().setClassType(AuditTypeClass.Audit);
		audit.getAuditType().setHasAuditor(true);
		audit.getAuditType().setCanContractorEdit(false);
		PicsTestUtil.forceSetPrivateField(test, "conAudit", audit);

		User group = new User();
		group.setIsGroup(YesNo.Yes);

		when(permissions.isAdmin()).thenReturn(true);
		when(permissions.isOperatorCorporate()).thenReturn(true);
		assertTrue(test.isCanEditAudit());

		audit.getAuditType().setEditAudit(group);
		assertFalse(test.isCanEditAudit());

		group.setId(OPERATOR_GROUP_ID);
		when(permissions.hasGroup(OPERATOR_GROUP_ID)).thenReturn(true);
		audit.getAuditType().setEditAudit(group);
		assertTrue(test.isCanEditAudit());
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
	public void testIsUserPermittedToAssignAudit() {
		ContractorAudit conAudit = EntityFactory.makeContractorAudit(EntityFactory.makeAuditType(), contractor);
		User assignerGroup = EntityFactory.makeUser();
		assignerGroup.setIsGroup(YesNo.Yes);
		conAudit.getAuditType().setAssignAudit(assignerGroup);
		conAudit.getAuditType().setHasAuditor(true);
		PicsTestUtil.forceSetPrivateField(test, "conAudit", conAudit);

		User otherGroup = EntityFactory.makeUser();
		otherGroup.setIsGroup(YesNo.Yes);

		when(permissions.hasPermission(OpPerms.AssignAudits, OpType.Edit)).thenReturn(false);
		assertFalse(test.isUserPermittedToAssignAudit());

		when(permissions.hasPermission(OpPerms.AssignAudits, OpType.Edit)).thenReturn(true);
		when(permissions.hasGroup(conAudit.getAuditType().getAssignAudit().getId())).thenReturn(false);
		assertFalse(test.isUserPermittedToAssignAudit());

		when(permissions.hasPermission(OpPerms.AssignAudits, OpType.Edit)).thenReturn(true);
		when(permissions.hasGroup(conAudit.getAuditType().getAssignAudit().getId())).thenReturn(true);
		assertTrue(test.isUserPermittedToAssignAudit());
	}

	@Test
	public void testCanPerformAction() throws Exception {
		ContractorAudit audit = createWCB();
		PicsTestUtil.forceSetPrivateField(test, "conAudit", audit);
		ContractorAuditOperator cao = audit.getOperators().get(0);
		cao.setPercentComplete(100);
		when(permissions.seesAllContractors()).thenReturn(true);

		WorkflowStep step = createWorkflowStep(AuditStatus.Submitted, AuditStatus.Approved);
		Boolean value = Whitebox.invokeMethod(test, "canPerformAction", audit.getOperators().get(0), step);
		assertTrue(value);

        audit = createOperatorAudit(OPERATOR_GROUP_ID);
        PicsTestUtil.forceSetPrivateField(test, "conAudit", audit);
        cao = audit.getOperators().get(0);
        cao.setPercentComplete(100);
        when(permissions.seesAllContractors()).thenReturn(false);
        when(permissions.isOperatorCorporate()).thenReturn(true);
        step = createWorkflowStep(AuditStatus.Pending, AuditStatus.Submitted);
        value = Whitebox.invokeMethod(test, "canPerformAction", cao, step);
        assertTrue(value);

        audit = createOperatorAudit(1);
        PicsTestUtil.forceSetPrivateField(test, "conAudit", audit);
        cao = audit.getOperators().get(0);
        cao.setPercentComplete(100);
        when(permissions.seesAllContractors()).thenReturn(false);
        when(permissions.isOperatorCorporate()).thenReturn(true);
        step = createWorkflowStep(AuditStatus.Pending, AuditStatus.Submitted);
        value = Whitebox.invokeMethod(test, "canPerformAction", cao, step);
        assertFalse(value);
    }

    private ContractorAudit createOperatorAudit(int auditorGroupId) {
        ContractorAudit audit = EntityFactory.makeContractorAudit(200, contractor);

        User group = EntityFactory.makeUser();
        group.setId(auditorGroupId);
        group.setIsGroup(YesNo.Yes);
        audit.getAuditType().setEditAudit(group);

        EntityFactory.addCao(audit, operator);
        Workflow workflow = new Workflow();
        workflow.setId(3);

        List<WorkflowStep> steps = new ArrayList<WorkflowStep>();
        steps.add(createWorkflowStep(null, AuditStatus.Pending));
        steps.add(createWorkflowStep(AuditStatus.Pending, AuditStatus.Submitted));
        steps.add(createWorkflowStep(AuditStatus.Submitted, AuditStatus.Complete));

        workflow.setSteps(steps);
        audit.getAuditType().setWorkFlow(workflow);

        return audit;
    }

}
