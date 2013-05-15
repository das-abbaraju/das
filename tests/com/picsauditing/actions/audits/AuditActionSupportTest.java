package com.picsauditing.actions.audits;

import com.picsauditing.EntityFactory;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.PicsTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.Permissions;
import com.picsauditing.auditBuilder.AuditCategoryRuleCache;
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
import static org.mockito.Mockito.when;

public class AuditActionSupportTest extends PicsTest {
    private static final Integer OPERATOR_GROUP_ID = 44;
    private AuditActionSupport test;

	private ContractorAccount contractor;
	private OperatorAccount operator;

	@Mock
	Permissions permissions;
	@Mock
	protected AuditCategoryRuleCache auditCategoryRuleCache;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		MockitoAnnotations.initMocks(this);

		test = new AuditActionSupport();
		autowireEMInjectedDAOs(test);
		PicsTestUtil.forceSetPrivateField(test, "permissions", permissions);
		PicsTestUtil.forceSetPrivateField(test, "auditCategoryRuleCache", auditCategoryRuleCache);
        Set<Integer> groupIds = new TreeSet<Integer>();
        groupIds.add(OPERATOR_GROUP_ID);
        when(permissions.getAllInheritedGroupIds()).thenReturn(groupIds);


        contractor = EntityFactory.makeContractor();
		operator = EntityFactory.makeOperator();
		contractor.getOperatorAccounts().add(operator);
		EntityFactory.addContractorOperator(contractor, operator);

		PicsTestUtil.forceSetPrivateField(test, "auditEditModel", new AuditEditModel());

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

		//SSIP
		audit.getAuditType().setId(AuditType.SSIP);
		cao.changeStatus(AuditStatus.Resubmitted, null);
		assertTrue(test.isCanViewRequirements());

		// Other
		audit.getAuditType().setId(AuditType.DESKTOP);
		cao.changeStatus(AuditStatus.Submitted, null);
		assertTrue(test.isCanViewRequirements());

	}

	@Test
	public void testIsCanEditCategory_MultipleCaosDifferentStates() throws Exception {
		ContractorAudit audit = EntityFactory.makeContractorAudit(AuditType.ANNUALADDENDUM, contractor);
		audit.getAuditType().setClassType(AuditTypeClass.Audit);
		audit.getAuditType().setHasAuditor(true);
		audit.getAuditType().setCanContractorEdit(false);
		PicsTestUtil.forceSetPrivateField(test, "conAudit", audit);

		OperatorAccount op1 = EntityFactory.makeOperator();
		OperatorAccount op2 = EntityFactory.makeOperator();

		ContractorAuditOperator cao1 = EntityFactory.addCao(audit, op1);
		ContractorAuditOperator cao2 = EntityFactory.addCao(audit, op2);
		audit.getOperators().add(cao1);
		audit.getOperators().add(cao2);
		ContractorAuditOperatorPermission caop1 = new ContractorAuditOperatorPermission();
		ContractorAuditOperatorPermission caop2 = new ContractorAuditOperatorPermission();
		caop1.setOperator(op1);
		caop2.setOperator(op2);
		cao1.getCaoPermissions().add(caop1);
		cao1.getCaoPermissions().add(caop1);

		AuditCategory category = EntityFactory.makeAuditCategory(100);
		category.setAuditType(audit.getAuditType());
		audit.getAuditType().getCategories().add(category);

		AuditCategoryRule rule1 = new AuditCategoryRule();
		AuditCategoryRule rule2 = new AuditCategoryRule();
		List<AuditCategoryRule> rules = new ArrayList<AuditCategoryRule>();
		rules.add(rule1);
		rules.add(rule2);
		rule1.setOperatorAccount(op1);
		rule1.setAuditCategory(category);
		rule1.setInclude(true);
		rule2.setOperatorAccount(op2);
		rule2.setAuditCategory(category);
		rule2.setInclude(true);

		when(permissions.isContractor()).thenReturn(true);
		when(auditCategoryRuleCache.getRules(contractor, audit.getAuditType())).thenReturn(rules);

		cao1.setStatus(AuditStatus.Pending);
		cao2.setStatus(AuditStatus.Pending);
		assertTrue(test.isCanEditCategory(category));

		cao1.setStatus(AuditStatus.Incomplete);
		cao2.setStatus(AuditStatus.Complete);
		assertTrue(test.isCanEditCategory(category));

		cao1.setStatus(AuditStatus.Submitted);
		cao2.setStatus(AuditStatus.Complete);
		assertTrue(test.isCanEditCategory(category));

		cao1.setStatus(AuditStatus.Resubmit);
		cao2.setStatus(AuditStatus.Complete);
		assertTrue(test.isCanEditCategory(category));

		cao1.setStatus(AuditStatus.Resubmitted);
		cao2.setStatus(AuditStatus.Complete);
		assertTrue(test.isCanEditCategory(category));

		cao1.setStatus(AuditStatus.Complete);
		cao2.setStatus(AuditStatus.Complete);
		assertFalse(test.isCanEditCategory(category));
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
