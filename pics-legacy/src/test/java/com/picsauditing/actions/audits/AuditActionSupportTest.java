package com.picsauditing.actions.audits;

import com.google.common.collect.ArrayListMultimap;
import com.picsauditing.EntityFactory;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.PicsTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.models.audits.AuditEditModel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuditActionSupportTest extends PicsTest {
	private static final Integer OPERATOR_GROUP_ID = 44;
	private AuditActionSupport test;

	private ContractorAccount contractor;
	private OperatorAccount operator;

	@Mock
	Permissions permissions;
	@Mock
	protected ContractorAudit conAudit;
	@Mock
	protected AuditType auditType;
	@Mock
	protected Workflow workflow;
    @Mock
    protected AuditDataDAO auditDataDAO;


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

		PicsTestUtil.forceSetPrivateField(test, "auditEditModel", new AuditEditModel());
        PicsTestUtil.forceSetPrivateField(test, "auditDataDAO", auditDataDAO);
	}

    @Test
    public void testgGetDataForSafetyManual_SafetyManual() throws Exception {
        ContractorAudit audit = EntityFactory.makeContractorAudit(AuditType.MANUAL_AUDIT, contractor);
        PicsTestUtil.forceSetPrivateField(test, "conAudit", audit);

        test.getDataForSafetyManual();
        verify(auditDataDAO).findAnswersForSafetyManual(contractor.getId(), AuditQuestion.MANUAL_PQF);
        verify(auditDataDAO, never()).findAnswersForCaseManagementPlan(contractor.getId());
   }

    @Test
    public void testgGetDataForSafetyManual_CaseManagement() throws Exception {
        ContractorAudit audit = EntityFactory.makeContractorAudit(AuditType.BPIISNCASEMGMT, contractor);
        PicsTestUtil.forceSetPrivateField(test, "conAudit", audit);

        test.getDataForSafetyManual();
        verify(auditDataDAO, never()).findAnswersForSafetyManual(contractor.getId(), AuditQuestion.MANUAL_PQF);
        verify(auditDataDAO).findAnswersForCaseManagementPlan(contractor.getId());
    }

    @Test
    public void testDisplayMultiStatusDropDown_Invalid_NotCSRGroup() {
        ArrayListMultimap<AuditStatus, Integer> actionStatus = ArrayListMultimap.create();
        PicsTestUtil.forceSetPrivateField(test, "actionStatus", actionStatus);
        PicsTestUtil.forceSetPrivateField(test, "contractor", contractor);

        actionStatus.put(AuditStatus.Complete, 1);

        ContractorTrade contractorTrade = new ContractorTrade();
        Trade trade = new Trade();
        trade.setName("Test Trade");
        contractorTrade.setTrade(trade);

        contractor.getTrades().add(contractorTrade);
        when(permissions.hasGroup(User.GROUP_CSR)).thenReturn(false);

        // not in CSR group
        assertFalse(test.displayMultiStatusDropDown());
    }

    @Test
    public void testDisplayMultiStatusDropDown_Invalid_NoAuditStatus() {
        ArrayListMultimap<AuditStatus, Integer> actionStatus = ArrayListMultimap.create();
        PicsTestUtil.forceSetPrivateField(test, "actionStatus", actionStatus);
        PicsTestUtil.forceSetPrivateField(test, "contractor", contractor);

        ContractorTrade contractorTrade = new ContractorTrade();
        Trade trade = new Trade();
        trade.setName("Test Trade");
        contractorTrade.setTrade(trade);

        contractor.getTrades().add(contractorTrade);
        when(permissions.hasGroup(User.GROUP_CSR)).thenReturn(true);

        assertFalse(test.displayMultiStatusDropDown());
    }

    @Test
    public void testDisplayMultiStatusDropDown_Invalid_NoTrades() {
        ArrayListMultimap<AuditStatus, Integer> actionStatus = ArrayListMultimap.create();
        PicsTestUtil.forceSetPrivateField(test, "actionStatus", actionStatus);
        PicsTestUtil.forceSetPrivateField(test, "contractor", contractor);

        actionStatus.put(AuditStatus.Complete, 1);
        when(permissions.hasGroup(User.GROUP_CSR)).thenReturn(true);

        assertFalse(test.displayMultiStatusDropDown());
    }

    @Test
    public void testDisplayMultiStatusDropDown_Valid() {
        ArrayListMultimap<AuditStatus, Integer> actionStatus = ArrayListMultimap.create();
        PicsTestUtil.forceSetPrivateField(test, "actionStatus", actionStatus);
        PicsTestUtil.forceSetPrivateField(test, "contractor", contractor);

        actionStatus.put(AuditStatus.Complete, 1);

        ContractorTrade contractorTrade = new ContractorTrade();
        Trade trade = new Trade();
        trade.setName("Test Trade");
        contractorTrade.setTrade(trade);

        contractor.getTrades().add(contractorTrade);

        when(permissions.hasGroup(User.GROUP_CSR)).thenReturn(true);

        assertTrue(test.displayMultiStatusDropDown());
    }

    @Test
	public void testCalculateRefreshAudit() throws Exception {
		PicsTestUtil.forceSetPrivateField(test, "conAudit", conAudit);
		when(conAudit.getAuditType()).thenReturn(auditType);
		when(auditType.getWorkFlow()).thenReturn(workflow);

		// normal audit
		when(workflow.isUseStateForEdit()).thenReturn(false);
		when(auditType.getClassType()).thenReturn(AuditTypeClass.Audit);
		when(conAudit.hasCaoStatusAfter(AuditStatus.Incomplete)).thenReturn(false);
		test.setRefreshAudit(false);
		Whitebox.invokeMethod(test, "calculateRefreshAudit");
		assertFalse(test.isRefreshAudit());

		// work state audit
		when(workflow.isUseStateForEdit()).thenReturn(true);
		when(auditType.getClassType()).thenReturn(AuditTypeClass.Audit);
		when(conAudit.hasCaoStatusAfter(AuditStatus.Incomplete)).thenReturn(false);
		test.setRefreshAudit(false);
		Whitebox.invokeMethod(test, "calculateRefreshAudit");
		assertTrue(test.isRefreshAudit());

		// policy nobody submitted
		when(workflow.isUseStateForEdit()).thenReturn(false);
		when(auditType.getClassType()).thenReturn(AuditTypeClass.Policy);
		when(conAudit.hasCaoStatusAfter(AuditStatus.Incomplete)).thenReturn(false);
		test.setRefreshAudit(false);
		Whitebox.invokeMethod(test, "calculateRefreshAudit");
		assertTrue(test.isRefreshAudit());

		// policy someone submitted
		when(workflow.isUseStateForEdit()).thenReturn(false);
		when(auditType.getClassType()).thenReturn(AuditTypeClass.Policy);
		when(conAudit.hasCaoStatusAfter(AuditStatus.Incomplete)).thenReturn(true);
		test.setRefreshAudit(false);
		Whitebox.invokeMethod(test, "calculateRefreshAudit");
		assertFalse(test.isRefreshAudit());
	}

	@Test
	public void testIsCanViewRequirements() {
		ContractorAudit audit = EntityFactory.makeContractorAudit(200, contractor);
		audit.getAuditType().setClassType(AuditTypeClass.Audit);

		Workflow workflow = new Workflow();
		workflow.setHasRequirements(true);
		audit.getAuditType().setWorkFlow(workflow);

		ContractorAuditOperator cao = EntityFactory.addCao(audit, EntityFactory.makeOperator());

		PicsTestUtil.forceSetPrivateField(test, "conAudit", audit);

		// COR
		audit.getAuditType().setId(AuditType.COR);
		cao.changeStatus(AuditStatus.Submitted, null);
		assertTrue(test.isCanViewRequirements());

		// SSIP
		audit.getAuditType().setId(AuditType.SSIP);
		cao.changeStatus(AuditStatus.Resubmitted, null);
		assertTrue(test.isCanViewRequirements());

		// Other
		audit.getAuditType().setId(AuditType.MANUAL_AUDIT);
		cao.changeStatus(AuditStatus.Submitted, null);
		assertTrue(test.isCanViewRequirements());

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
		when(permissions.getAllInheritedGroupIds()).thenReturn(new TreeSet<Integer>());
		assertFalse(test.isUserPermittedToAssignAudit());

		TreeSet<Integer> set = new TreeSet<Integer>();
		set.add(conAudit.getAuditType().getAssignAudit().getId());
		when(permissions.hasPermission(OpPerms.AssignAudits, OpType.Edit)).thenReturn(true);
		when(permissions.getAllInheritedGroupIds()).thenReturn(set);
		assertTrue(test.isUserPermittedToAssignAudit());
	}

    @Test
    public void testCanPerformAxtion_CompleteForWorkflowsNeedingVerification_NotVerified() {
        WorkflowStep complete = getCompleteWorkflowStep();
        AuditType auditType = createAuditTypendWorkflow(AuditType.PQF, Workflow.PQF_WORKFLOW);
        ContractorAuditOperator cao = getContractorAuditOperator();

        PicsTestUtil.forceSetPrivateField(test, "conAudit", conAudit);
        when(permissions.seesAllContractors()).thenReturn(true);

        assertFalse(test.canPerformAction(cao, complete));
    }

    @Test
    public void testCanPerformAxtion_CompleteForWorkflowsNeedingVerification_Verified() {
        WorkflowStep complete = getCompleteWorkflowStep();
        AuditType auditType = createAuditTypendWorkflow(AuditType.PQF, Workflow.PQF_WORKFLOW);
        ContractorAuditOperator cao = getContractorAuditOperator();

        cao.setPercentVerified(100);

        PicsTestUtil.forceSetPrivateField(test, "conAudit", conAudit);
        when(permissions.seesAllContractors()).thenReturn(true);

        assertTrue(test.canPerformAction(cao, complete));
    }

    @Test
    public void testCanPerformAxtion_CompleteForWorkflowsNotNeedingVerification_NotVerified() {
        WorkflowStep complete = getCompleteWorkflowStep();
        AuditType auditType = createAuditTypendWorkflow(AuditType.PQF, test.workflowIdsNotNeedingVerification.get(0));
        ContractorAuditOperator cao = getContractorAuditOperator();

        PicsTestUtil.forceSetPrivateField(test, "conAudit", conAudit);
        when(permissions.seesAllContractors()).thenReturn(true);

        assertTrue(test.canPerformAction(cao, complete));
    }

    private ContractorAuditOperator getContractorAuditOperator() {
        ContractorAuditOperator cao = new ContractorAuditOperator();
        cao.setPercentComplete(100);
        cao.setAudit(conAudit);
        cao.changeStatus(AuditStatus.Submitted, null);
        return cao;
    }

    private WorkflowStep getCompleteWorkflowStep() {
        WorkflowStep complete = new WorkflowStep();
        complete.setOldStatus(AuditStatus.Submitted);
        complete.setNewStatus(AuditStatus.Complete);
        return complete;
    }

    private AuditType createAuditTypendWorkflow(int auditTypeId, int workflowId) {
        AuditType auditType = new AuditType(auditTypeId);
        auditType.setClassType(AuditTypeClass.PQF);
        Workflow workflow = new Workflow();
        workflow.setId(workflowId);
        auditType.setWorkFlow(workflow);

        WorkflowState pending = new WorkflowState();
        pending.setStatus(AuditStatus.Pending);
        WorkflowState submitted = new WorkflowState();
        pending.setStatus(AuditStatus.Submitted);
        WorkflowState complete = new WorkflowState();
        pending.setStatus(AuditStatus.Complete);
        List<WorkflowState> states = new ArrayList<>();
        states.add(pending);
        states.add(submitted);
        states.add(complete);
        workflow.setStates(states);

        WorkflowStep initial = new WorkflowStep();
        initial.setOldStatus(null);
        initial.setNewStatus(AuditStatus.Pending);
        WorkflowStep submit = new WorkflowStep();
        submit.setOldStatus(AuditStatus.Pending);
        submit.setNewStatus(AuditStatus.Submitted);
        WorkflowStep completed = new WorkflowStep();
        completed.setOldStatus(AuditStatus.Submitted);
        completed.setNewStatus(AuditStatus.Complete);
        List<WorkflowStep> steps = new ArrayList<>();
        steps.add(initial);
        steps.add(submit);
        steps.add(completed);
        workflow.setSteps(steps);

        when(conAudit.getAuditType()).thenReturn(auditType);

        return auditType;
    }

    @Test
	public void testCanPerformAction() throws Exception {
		ContractorAudit audit = createWCB();
		PicsTestUtil.forceSetPrivateField(test, "conAudit", audit);
		ContractorAuditOperator cao = audit.getOperators().get(0);
		cao.setPercentComplete(100);
		when(permissions.seesAllContractors()).thenReturn(true);
		when(permissions.getAccountId()).thenReturn(operator.getId());

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

    @Test
    public void testCanPerformAction_stateEdit() throws Exception {
        Boolean value;

        ContractorAudit audit = EntityFactory.makeContractorAudit(200, contractor);
        audit.getAuditType().setCanContractorEdit(true);
        EntityFactory.addCao(audit, operator);
        createEditStateWorkFlow(audit);
        ContractorAuditOperator cao = audit.getOperators().get(0);
        cao.setPercentComplete(100);
        cao.setPercentVerified(100);
        ContractorAuditOperatorPermission caop = new ContractorAuditOperatorPermission();
        caop.setOperator(cao.getOperator());
        caop.setCao(cao);
        cao.getCaoPermissions().add(caop);

        PicsTestUtil.forceSetPrivateField(test, "conAudit", audit);

        // test contractor
        when(permissions.isContractor()).thenReturn(true);
        cao.changeStatus(AuditStatus.Pending, null);
        value = Whitebox.invokeMethod(test, "canPerformAction", cao, createWorkflowStep(AuditStatus.Pending, AuditStatus.Submitted));
        assertTrue(value);

        cao.changeStatus(AuditStatus.Submitted, null);
        value = Whitebox.invokeMethod(test, "canPerformAction", cao, createWorkflowStep(AuditStatus.Submitted, AuditStatus.Incomplete));
        assertFalse(value);

        cao.changeStatus(AuditStatus.Incomplete, null);
        value = Whitebox.invokeMethod(test, "canPerformAction", cao, createWorkflowStep(AuditStatus.Incomplete, AuditStatus.Resubmitted));
        assertTrue(value);

        cao.changeStatus(AuditStatus.Resubmitted, null);
        value = Whitebox.invokeMethod(test, "canPerformAction", cao, createWorkflowStep(AuditStatus.Resubmitted, AuditStatus.Complete));
        assertFalse(value);

        // test operator
        when(permissions.isContractor()).thenReturn(false);
        when(permissions.isOperatorCorporate()).thenReturn(true);
        when(permissions.getAccountId()).thenReturn(caop.getOperator().getId());
        cao.changeStatus(AuditStatus.Pending, null);
        value = Whitebox.invokeMethod(test, "canPerformAction", cao, createWorkflowStep(AuditStatus.Pending, AuditStatus.Submitted));
        assertFalse(value);

        cao.changeStatus(AuditStatus.Submitted, null);
        value = Whitebox.invokeMethod(test, "canPerformAction", cao, createWorkflowStep(AuditStatus.Submitted, AuditStatus.Incomplete));
        assertTrue(value);

        cao.changeStatus(AuditStatus.Incomplete, null);
        value = Whitebox.invokeMethod(test, "canPerformAction", cao, createWorkflowStep(AuditStatus.Incomplete, AuditStatus.Resubmitted));
        assertTrue(value);

        cao.changeStatus(AuditStatus.Resubmitted, null);
        value = Whitebox.invokeMethod(test, "canPerformAction", cao, createWorkflowStep(AuditStatus.Resubmitted, AuditStatus.Complete));
        assertTrue(value);
    }

    private void createEditStateWorkFlow(ContractorAudit audit) {
        Workflow workflow = new Workflow();
        workflow.setId(3);
        workflow.setUseStateForEdit(true);

        List<WorkflowStep> steps = new ArrayList<WorkflowStep>();
        steps.add(createWorkflowStep(null, AuditStatus.Pending));
        steps.add(createWorkflowStep(AuditStatus.Pending, AuditStatus.Submitted));
        steps.add(createWorkflowStep(AuditStatus.Submitted, AuditStatus.Incomplete));
        steps.add(createWorkflowStep(AuditStatus.Submitted, AuditStatus.Complete));
        steps.add(createWorkflowStep(AuditStatus.Incomplete, AuditStatus.Resubmitted));
        steps.add(createWorkflowStep(AuditStatus.Resubmitted, AuditStatus.Incomplete));
        steps.add(createWorkflowStep(AuditStatus.Resubmitted, AuditStatus.Complete));
        workflow.setSteps(steps);

        List<WorkflowState> states = new ArrayList<WorkflowState>();
        states.add(createWorkflowState(AuditStatus.Pending, true, false));
        states.add(createWorkflowState(AuditStatus.Submitted, false, true));
        states.add(createWorkflowState(AuditStatus.Incomplete, true, true));
        states.add(createWorkflowState(AuditStatus.Resubmitted, false, true));
        states.add(createWorkflowState(AuditStatus.Complete, false, false));
        workflow.setStates(states);

        audit.getAuditType().setWorkFlow(workflow);
    }

    private WorkflowState createWorkflowState(AuditStatus status, boolean contractorEdit, boolean operatorEdit) {
        WorkflowState state = new WorkflowState();
        state.setStatus(status);
        state.setContractorCanEdit(contractorEdit);
        state.setOperatorCanEdit(operatorEdit);
        return state;
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

	@Test
	public void testHasClosingAuditor() throws Exception {
		ContractorAudit audit = EntityFactory.makeContractorAudit(AuditType.PQF, contractor);
		PicsTestUtil.forceSetPrivateField(test, "conAudit", audit);

		assertFalse(test.isHasClosingAuditor());

		audit.setId(AuditType.ANNUALADDENDUM);
		assertFalse(test.isHasClosingAuditor());

		audit.setId(200);
		audit.getAuditType().setClassType(AuditTypeClass.Policy);
		assertFalse(test.isHasClosingAuditor());

		audit.getAuditType().setClassType(AuditTypeClass.Audit);
		audit.getAuditType().setHasAuditor(false);
		assertFalse(test.isHasClosingAuditor());
	}
}
