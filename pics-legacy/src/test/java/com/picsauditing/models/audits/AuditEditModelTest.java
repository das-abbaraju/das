package com.picsauditing.models.audits;

import org.junit.Before;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.audits.AuditActionSupport;
import com.picsauditing.jpa.entities.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class AuditEditModelTest  extends PicsTest {
	private static final Integer OPERATOR_GROUP_ID = 44;

	@Mock
	private Permissions permissions;
	private AuditEditModel test;
	private ContractorAccount contractor;
	private OperatorAccount operator;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		MockitoAnnotations.initMocks(this);

		test = new AuditEditModel();
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
	public void testIsCanEditAuditByAuditType() {
		ContractorAudit audit = EntityFactory.makeContractorAudit(200, contractor);
		audit.getAuditType().setClassType(AuditTypeClass.Audit);
		audit.getAuditType().setHasAuditor(true);
		audit.getAuditType().setCanContractorEdit(false);

		User group = new User();
		group.setIsGroup(YesNo.Yes);

		when(permissions.isAdmin()).thenReturn(true);
		when(permissions.isOperatorCorporate()).thenReturn(true);
		assertTrue(test.canEdit(audit, permissions));

		audit.getAuditType().setEditAudit(group);
		assertFalse(test.canEdit(audit, permissions));

		group.setId(OPERATOR_GROUP_ID);
		when(permissions.hasGroup(OPERATOR_GROUP_ID)).thenReturn(true);
		audit.getAuditType().setEditAudit(group);
		assertTrue(test.canEdit(audit, permissions));
	}

	@Test
	public void testIsCanEditAudit_AnnualUpdate() {
		ContractorAudit audit = EntityFactory.makeContractorAudit(AuditType.ANNUALADDENDUM, contractor);
		audit.getAuditType().setClassType(AuditTypeClass.Audit);
		audit.getAuditType().setHasAuditor(true);
		audit.getAuditType().setCanContractorEdit(true);

		User group = new User();
		group.setIsGroup(YesNo.Yes);

		when(permissions.isOperatorCorporate()).thenReturn(true);
		assertFalse(test.canEdit(audit, permissions));
	}

	@Test
	public void testIsCanEditAudit_AWCB() {
		ContractorAudit audit = EntityFactory.makeContractorAudit(145, contractor);
		audit.getAuditType().setClassType(AuditTypeClass.Policy);
		audit.getAuditType().setCanContractorEdit(true);
		ContractorAuditOperator cao = EntityFactory.addCao(audit, operator);

		when(permissions.isContractor()).thenReturn(true);

		cao.changeStatus(AuditStatus.Pending, null);
		assertTrue(test.canEdit(audit, permissions));

		cao.changeStatus(AuditStatus.Submitted, null);
		assertFalse(test.canEdit(audit, permissions));
	}

	@Test
	public void testCanEditWorkflowContractor() {
		ContractorAudit audit = createWorkFlowAudit();
		ContractorAuditOperator cao = audit.getOperators().get(0);
		when(permissions.isContractor()).thenReturn(true);

		cao.changeStatus(AuditStatus.Pending, null);
		assertTrue(test.canEdit(audit, permissions));

		cao.changeStatus(AuditStatus.Incomplete, null);
		assertTrue(test.canEdit(audit, permissions));

		cao.changeStatus(AuditStatus.Submitted, null);
		assertFalse(test.canEdit(audit, permissions));

		cao.changeStatus(AuditStatus.Complete, null);
		assertFalse(test.canEdit(audit, permissions));
	}

	@Test
	public void testCanEditWorkflowOperator() {
		ContractorAudit audit = createWorkFlowAudit();
		ContractorAuditOperator cao = audit.getOperators().get(0);
		when(permissions.isOperatorCorporate()).thenReturn(true);
		when(permissions.getAccountId()).thenReturn(operator.getId());

		cao.changeStatus(AuditStatus.Pending, null);
		assertFalse(test.canEdit(audit, permissions));

		cao.changeStatus(AuditStatus.Incomplete, null);
		assertTrue(test.canEdit(audit, permissions));

		cao.changeStatus(AuditStatus.Submitted, null);
		assertTrue(test.canEdit(audit, permissions));

		cao.changeStatus(AuditStatus.Complete, null);
		assertFalse(test.canEdit(audit, permissions));
	}

	private ContractorAudit createWorkFlowAudit() {
		ContractorAudit audit = EntityFactory.makeContractorAudit(200, contractor);
		audit.getAuditType().setClassType(AuditTypeClass.Audit);
		audit.getAuditType().setWorkFlow(createEditWorkFlow());
		ContractorAuditOperator cao = EntityFactory.addCao(audit, operator);
		cao.setVisible(true);
		ContractorAuditOperatorPermission caop = new ContractorAuditOperatorPermission();
		caop.setOperator(operator);
		caop.setCao(cao);
		cao.getCaoPermissions().add(caop);

		return audit;
	}

	private Workflow createEditWorkFlow() {
		Workflow workflow = new Workflow();
		workflow.setUseStateForEdit(true);

		WorkflowState state;
		state = new WorkflowState();
		state.setStatus(AuditStatus.Pending);
		state.setContractorCanEdit(true);
		workflow.getStates().add(state);

		state = new WorkflowState();
		state.setStatus(AuditStatus.Incomplete);
		state.setContractorCanEdit(true);
		state.setOperatorCanEdit(true);
		workflow.getStates().add(state);

		state = new WorkflowState();
		state.setStatus(AuditStatus.Submitted);
		state.setOperatorCanEdit(true);
		workflow.getStates().add(state);

		state = new WorkflowState();
		state.setStatus(AuditStatus.Complete);
		workflow.getStates().add(state);

		return workflow;
	}
}
