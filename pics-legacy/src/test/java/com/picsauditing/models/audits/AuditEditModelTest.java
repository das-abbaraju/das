package com.picsauditing.models.audits;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.access.Permissions;
import com.picsauditing.audits.AuditBuilderFactory;
import com.picsauditing.jpa.entities.*;
import edu.emory.mathcs.backport.java.util.Arrays;
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
    @Mock
    private AuditBuilderFactory auditBuilderFactory;
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
        PicsTestUtil.forceSetPrivateField(test, "auditBuilderFactory", auditBuilderFactory);
        Set<Integer> groupIds = new TreeSet<Integer>();
		groupIds.add(OPERATOR_GROUP_ID);
		when(permissions.getAllInheritedGroupIds()).thenReturn(groupIds);

		contractor = EntityFactory.makeContractor();
		operator = EntityFactory.makeOperator();
		contractor.getOperatorAccounts().add(operator);
		EntityFactory.addContractorOperator(contractor, operator);
	}

    @Test
    public void testIsCanEditCategory_PqfCategory() {
        ContractorAudit audit = createAuditWithSingleCao(AuditType.PQF);
        audit.getOperators().get(0).changeStatus(AuditStatus.Resubmitted, null);
        audit.getAuditType().setClassType(AuditTypeClass.PQF);
        AuditCategory category = EntityFactory.addCategories(audit.getAuditType(), 100, "Test");
        category.setAuditType(audit.getAuditType());

        when(permissions.isContractor()).thenReturn(true);
        audit.getOperators().get(0).changeStatus(AuditStatus.Complete, null);
        assertFalse(test.isCanEditCategory(category, audit, permissions));

        audit.getOperators().get(0).changeStatus(AuditStatus.Pending, null);
        assertTrue(test.isCanEditCategory(category, audit, permissions));
    }

    @Test
    public void testIsCanEditCategory_PqfSpecific_CategoryComplete_CompleteAndPendingCaos() throws Exception {
        ContractorAudit audit = buildPqfWithCaos(5, 5, AuditStatus.Complete, AuditStatus.Pending);
        AuditCategory category = EntityFactory.addCategories(audit.getAuditType(), 100, "Test");
        category.setAuditType(audit.getAuditType());

        assertFalse(test.isCanEditCategory(category, audit, permissions));
    }

    @Test
    public void testIsCanEditCategory_PqfSpecific_CategoryIncomplete_CompleteAndPendingCaos() throws Exception {
        ContractorAudit audit = buildPqfWithCaos(5, 5, AuditStatus.Complete, AuditStatus.Pending);
        AuditCategory category = EntityFactory.addCategories(audit.getAuditType(), 100, "Test");
        category.setAuditType(audit.getAuditType());
        AuditCatData catData = new AuditCatData();
        catData.setCategory(category);
        catData.setNumRequired(2);
        catData.setRequiredCompleted(1);
        audit.getCategories().add(catData);
        assertTrue(test.isCanEditCategory(category, audit, permissions));
    }

    @Test
    public void testIsCanEditCategory_AnnualUpdateCategory() {
        ContractorAudit audit = createAuditWithSingleCao(AuditType.ANNUALADDENDUM);
        audit.getOperators().get(0).changeStatus(AuditStatus.Submitted, null);
        audit.getAuditType().setClassType(AuditTypeClass.Audit);
        AuditCategory category = EntityFactory.addCategories(audit.getAuditType(), 100, "Test");
        category.setAuditType(audit.getAuditType());

        when(permissions.isAdmin()).thenReturn(true);
        assertTrue(test.isCanEditCategory(category, audit, permissions));

        when(permissions.isAdmin()).thenReturn(false);
        when(permissions.isContractor()).thenReturn(true);
        when(auditBuilderFactory.isCategoryApplicable(category, audit, audit.getOperators().get(0))).thenReturn(true);
        assertTrue(test.isCanEditCategory(category, audit, permissions));
        audit.getOperators().get(0).changeStatus(AuditStatus.Complete, null);
        assertFalse(test.isCanEditCategory(category, audit, permissions));
    }

    @Test
    public void testIsCanEditCategory_PolicyCategory() {
        ContractorAudit audit = createAuditWithSingleCao(200);
        audit.getOperators().get(0).changeStatus(AuditStatus.Submitted, null);
        audit.getAuditType().setClassType(AuditTypeClass.Policy);
        AuditCategory category = EntityFactory.addCategories(audit.getAuditType(), 100, "Test");
        category.setUniqueCode("policyInformation"); // policyInformation limits

        when(permissions.isAdmin()).thenReturn(true);
        assertTrue(test.isCanEditCategory(category, audit, permissions));
        category.setUniqueCode(""); // policyInformation limits
        assertTrue(test.isCanEditCategory(category, audit, permissions));

        when(permissions.isAdmin()).thenReturn(false);
        when(permissions.isContractor()).thenReturn(true);
        category.setUniqueCode("policyInformation"); // policyInformation limits
        assertFalse(test.isCanEditCategory(category, audit, permissions));
    }

    private List<AuditCategoryRule> createCategoryRule(AuditCategory category) {
        List<AuditCategoryRule> rules = new ArrayList<>();
        AuditCategoryRule rule = new AuditCategoryRule();

        rule.setAuditCategory(category);
        rule.setOperatorAccount(operator);

        rules.add(rule);
        return rules;
    }

    private ContractorAudit createAuditWithSingleCao(int id) {
        ContractorAudit audit = EntityFactory.makeContractorAudit(id, contractor);
        ContractorAuditOperator cao = EntityFactory.addCao(audit, operator);
        cao.changeStatus(AuditStatus.Pending, null);
        ContractorAuditOperatorPermission caop = new ContractorAuditOperatorPermission();
        cao.getCaoPermissions().add(caop);
        caop.setCao(cao);
        caop.setOperator(operator);
        caop.setId(100);

        return audit;
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
		assertTrue(test.isCanEditAudit(audit, permissions));

		audit.getAuditType().setEditAudit(group);
		assertFalse(test.isCanEditAudit(audit, permissions));

		group.setId(OPERATOR_GROUP_ID);
		when(permissions.hasGroup(OPERATOR_GROUP_ID)).thenReturn(true);
		audit.getAuditType().setEditAudit(group);
		assertTrue(test.isCanEditAudit(audit, permissions));
	}

    @Test
    public void testIsCanEditAudit_Contractor_ByAuditType_PqfCompleteCao() {
        ContractorAudit audit = EntityFactory.makeContractorAudit(200, contractor);

        ContractorAuditOperator completedCao = ContractorAuditOperator.builder()
                .audit(audit)
                .status(AuditStatus.Complete)
                .visible()
                .build();

        audit.setOperators(Arrays.asList(new ContractorAuditOperator[]{completedCao}));

        audit.getAuditType().setClassType(AuditTypeClass.PQF);
        audit.getAuditType().setCanContractorEdit(true);

        when(permissions.isContractor()).thenReturn(true);
        assertFalse(test.isCanEditAudit(audit, permissions));
    }

    @Test
    public void testIsCanEditAudit_Contractor_ByAuditType_PqfPendingCao() {
        ContractorAudit audit = EntityFactory.makeContractorAudit(200, contractor);

        ContractorAuditOperator pendingCao = ContractorAuditOperator.builder()
                .audit(audit)
                .status(AuditStatus.Pending)
                .visible()
                .build();

        audit.setOperators(Arrays.asList(new ContractorAuditOperator[]{pendingCao}));

        audit.getAuditType().setClassType(AuditTypeClass.PQF);
        audit.getAuditType().setCanContractorEdit(true);

        User group = new User();
        group.setIsGroup(YesNo.Yes);

        when(permissions.isContractor()).thenReturn(true);
        assertTrue(test.isCanEditAudit(audit, permissions));
    }

    @Test
    public void testIsCanEditAudit_Contractor_ByAuditType_PqfResubmitedCao() {
        ContractorAudit audit = EntityFactory.makeContractorAudit(200, contractor);

        ContractorAuditOperator pendingCao = ContractorAuditOperator.builder()
                .audit(audit)
                .status(AuditStatus.Resubmit)
                .visible()
                .build();

        audit.setOperators(Arrays.asList(new ContractorAuditOperator[]{pendingCao}));

        audit.getAuditType().setClassType(AuditTypeClass.PQF);
        audit.getAuditType().setCanContractorEdit(true);

        User group = new User();
        group.setIsGroup(YesNo.Yes);

        when(permissions.isContractor()).thenReturn(true);
        assertTrue(test.isCanEditAudit(audit, permissions));
    }

    @Test
    public void testIsCanEditAudit_Contractor_ByAuditType_PqfPendingAndCompleteCao() {
        ContractorAudit audit = EntityFactory.makeContractorAudit(200, contractor);

        ContractorAuditOperator completedCao = ContractorAuditOperator.builder()
                .audit(audit)
                .status(AuditStatus.Complete)
                .visible()
                .build();
        ContractorAuditOperator pendingCao = ContractorAuditOperator.builder()
                .audit(audit)
                .status(AuditStatus.Pending)
                .visible()
                .build();

        audit.setOperators(Arrays.asList(new ContractorAuditOperator[]{completedCao, pendingCao}));

        audit.getAuditType().setClassType(AuditTypeClass.PQF);
        audit.getAuditType().setCanContractorEdit(true);

        User group = new User();
        group.setIsGroup(YesNo.Yes);

        when(permissions.isContractor()).thenReturn(true);
        assertTrue(test.isCanEditAudit(audit, permissions));
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
		assertFalse(test.isCanEditAudit(audit, permissions));
	}

	@Test
	public void testIsCanEditAudit_AWCB() {
		ContractorAudit audit = EntityFactory.makeContractorAudit(145, contractor);
		audit.getAuditType().setClassType(AuditTypeClass.Policy);
		audit.getAuditType().setCanContractorEdit(true);
		ContractorAuditOperator cao = EntityFactory.addCao(audit, operator);

		when(permissions.isContractor()).thenReturn(true);

		cao.changeStatus(AuditStatus.Pending, null);
		assertTrue(test.isCanEditAudit(audit, permissions));

		cao.changeStatus(AuditStatus.Submitted, null);
		assertFalse(test.isCanEditAudit(audit, permissions));
	}

	@Test
	public void testCanEditWorkflowContractor() {
		ContractorAudit audit = createWorkFlowAudit();
		ContractorAuditOperator cao = audit.getOperators().get(0);
		when(permissions.isContractor()).thenReturn(true);

		cao.changeStatus(AuditStatus.Pending, null);
		assertTrue(test.isCanEditAudit(audit, permissions));

		cao.changeStatus(AuditStatus.Incomplete, null);
		assertTrue(test.isCanEditAudit(audit, permissions));

		cao.changeStatus(AuditStatus.Submitted, null);
		assertFalse(test.isCanEditAudit(audit, permissions));

		cao.changeStatus(AuditStatus.Complete, null);
		assertFalse(test.isCanEditAudit(audit, permissions));
	}

	@Test
	public void testCanEditWorkflowOperator() {
		ContractorAudit audit = createWorkFlowAudit();
		ContractorAuditOperator cao = audit.getOperators().get(0);
		when(permissions.isOperatorCorporate()).thenReturn(true);
		when(permissions.getAccountId()).thenReturn(operator.getId());

		cao.changeStatus(AuditStatus.Pending, null);
		assertFalse(test.isCanEditAudit(audit, permissions));

		cao.changeStatus(AuditStatus.Incomplete, null);
		assertTrue(test.isCanEditAudit(audit, permissions));

		cao.changeStatus(AuditStatus.Submitted, null);
		assertTrue(test.isCanEditAudit(audit, permissions));

		cao.changeStatus(AuditStatus.Complete, null);
		assertFalse(test.isCanEditAudit(audit, permissions));
	}

	@Test
	public void testIsCanEditCategory_MultipleCaosDifferentStates() throws Exception {
		ContractorAudit audit = EntityFactory.makeContractorAudit(AuditType.ANNUALADDENDUM, contractor);
		audit.getAuditType().setClassType(AuditTypeClass.Audit);
		audit.getAuditType().setHasAuditor(true);
		audit.getAuditType().setCanContractorEdit(false);

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

		when(permissions.isContractor()).thenReturn(true);
        when(auditBuilderFactory.isCategoryApplicable(category, audit, audit.getOperators().get(0))).thenReturn(true);

        cao1.setStatus(AuditStatus.Pending);
		cao2.setStatus(AuditStatus.Pending);
		assertTrue(test.isCanEditCategory(category, audit, permissions));

		cao1.setStatus(AuditStatus.Incomplete);
		cao2.setStatus(AuditStatus.Complete);
		assertTrue(test.isCanEditCategory(category, audit, permissions));

		cao1.setStatus(AuditStatus.Submitted);
		cao2.setStatus(AuditStatus.Complete);
		assertTrue(test.isCanEditCategory(category, audit, permissions));

		cao1.setStatus(AuditStatus.Resubmit);
		cao2.setStatus(AuditStatus.Complete);
		assertTrue(test.isCanEditCategory(category, audit, permissions));

		cao1.setStatus(AuditStatus.Resubmitted);
		cao2.setStatus(AuditStatus.Complete);
		assertTrue(test.isCanEditCategory(category, audit, permissions));

		cao1.setStatus(AuditStatus.Complete);
		cao2.setStatus(AuditStatus.Complete);
		assertFalse(test.isCanEditCategory(category, audit, permissions));
	}

    private ContractorAudit buildPqfWithCaos(int requiredCompleted, int numberRequired, AuditStatus firstCaoStatus, AuditStatus secondCaoStatus) {
        ContractorAudit audit = EntityFactory.makeContractorAudit(111, contractor);
        audit.getAuditType().setClassType(AuditTypeClass.PQF);
        audit.getAuditType().setCanContractorEdit(true);
        when(permissions.isContractor()).thenReturn(true);

        ContractorAuditOperator cao1 = EntityFactory.addCao(audit, EntityFactory.makeOperator());
        ContractorAuditOperator cao2 = EntityFactory.addCao(audit, EntityFactory.makeOperator());
        audit.getOperators().add(cao1);
        audit.getOperators().add(cao2);

        cao1.setStatus(firstCaoStatus);
        cao2.setStatus(secondCaoStatus);
        return audit;
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
