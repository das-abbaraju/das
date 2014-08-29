package com.picsauditing.auditbuilder;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.auditbuilder.dao.AuditDataDAO2;
import com.picsauditing.auditbuilder.dao.ContractorTagDAO2;
import com.picsauditing.auditbuilder.entities.*;
import com.picsauditing.auditbuilder.service.AccountService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

public class DocumentServiceTest extends PicsTest {
    private static int OPERATOR_ID = 100;
    private static int NON_OPERATOR_ID = 200;
    private static int AUDIT_TYPE_ID = 1000;
    private static int NON_AUDIT_TYPE_ID = 2000;
    private static int CATEGORY_ID = 1000;
    private static int NON_CATEGORY_ID = 2000;
    private static int CONTRACTOR_ID =100;

    private DocumentService service;

    @Mock
    private AuditDataDAO2 auditDataDAO;
    @Mock
    private ContractorTagDAO2 contractorTagDAO;
    @Mock
    private OperatorAccount operator;
    @Mock
    private OperatorAccount nonOperator;
    @Mock
    private ContractorAccount contractor;
    @Mock
    private AuditTypeRuleCache2 typeRuleCache;
    @Mock
    private AuditCategoryRuleCache2 categoryRuleCache;

    List<Integer> operatorIds = new ArrayList<>();
    List<ContractorOperator> conOps = new ArrayList<>();
    List<AuditTypeRule> typeRules = new ArrayList<>();
    List<AuditCategoryRule> categoryRules = new ArrayList<>();

    private AuditTypesBuilder auditTypesBuilder;
    private AuditCategoriesBuilder auditCategoriesBuilder;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        service = new DocumentService();

        auditTypesBuilder = new AuditTypesBuilder();
        auditCategoriesBuilder = new AuditCategoriesBuilder();

        PicsTestUtil.forceSetPrivateField(service, "auditDataDAO", auditDataDAO);
        PicsTestUtil.forceSetPrivateField(service, "contractorTagDAO", contractorTagDAO);
        PicsTestUtil.forceSetPrivateField(service, "typeRuleCache", typeRuleCache);
        PicsTestUtil.forceSetPrivateField(service, "categoryRuleCache", categoryRuleCache);
        PicsTestUtil.forceSetPrivateField(service, "auditTypesBuilder", auditTypesBuilder);
        PicsTestUtil.forceSetPrivateField(service, "auditCategoriesBuilder", auditCategoriesBuilder);

        initialize();
    }

    @Test
    public void testGetContractorDocumentTypeDetailIds() throws Exception {
        ContractorOperator conOp = new ContractorOperator();
        conOp.setContractorAccount(contractor);
        conOp.setOperatorAccount(operator);
        conOps.add(conOp);

        Map<Integer, List<Integer>> ids = service.getTypeDetailIds(contractor.getId());
        assertEquals(1, ids.size());
        assertEquals(OPERATOR_ID, ids.get(0).get(0).intValue());
    }

    @Test
    public void testGetContractorSimulatorDocumentTypeDetailIds() throws Exception {
        Map<Integer, List<Integer>> ids = service.getSimulatorTypeDetailIds(contractor, operatorIds);
        assertEquals(1, ids.size());
        assertNotNull(ids.get(AUDIT_TYPE_ID));
    }

    @Test
    public void testGetContractorSimulatorCategoryIds() throws Exception {
        List<Integer> ids = service.getSimulatorCategoryIds(AUDIT_TYPE_ID, contractor, operatorIds);
        assertEquals(1, ids.size());
        assertEquals(CATEGORY_ID, ids.get(0).intValue());
    }

    @Test
    public void testGetContractorSimulatorCategoryIds_ShellCompetency() throws Exception {
        List<Integer> ids = service.getSimulatorCategoryIds(AuditType.SHELL_COMPETENCY_REVIEW, contractor, operatorIds);
        assertEquals(1, ids.size());
        assertEquals(0, ids.get(0).intValue());
    }

    private void initialize() {
        operatorIds.add(OPERATOR_ID);
        when(operator.getId()).thenReturn(OPERATOR_ID);
        when(operator.getType()).thenReturn(AccountService.OPERATOR_ACCOUNT_TYPE);
        when(operator.getStatus()).thenReturn(AccountStatus.Active);

        when(auditDataDAO.find(OperatorAccount.class, OPERATOR_ID)).thenReturn(operator);
        when(auditDataDAO.find(ContractorAccount.class, CONTRACTOR_ID)).thenReturn(contractor);

        when(nonOperator.getId()).thenReturn(NON_OPERATOR_ID);
        when(nonOperator.getType()).thenReturn(AccountService.OPERATOR_ACCOUNT_TYPE);
        when(nonOperator.getStatus()).thenReturn(AccountStatus.Active);

        when(contractor.isSafetySensitive()).thenReturn(false);
        when(contractor.getSafetyRisk()).thenReturn(LowMedHigh.None);
        when(contractor.getProductRisk()).thenReturn(LowMedHigh.None);
        when(contractor.getTransportationRisk()).thenReturn(LowMedHigh.None);
        when(contractor.isOnsiteServices()).thenReturn(true);
        when(contractor.getAccountLevel()).thenReturn(AccountLevel.Full);
        when(contractor.getId()).thenReturn(CONTRACTOR_ID);

        conOps.clear();
        when(contractor.getOperators()).thenReturn(conOps);

        when(typeRuleCache.getRules(contractor)).thenReturn(typeRules);

        typeRules.clear();
        AuditTypeRule rule;

        rule = new AuditTypeRule();
        rule.setAuditType(new AuditType());
        rule.getAuditType().setId(AUDIT_TYPE_ID);
        rule.setOperatorAccount(operator);
        typeRules.add(rule);

        rule = new AuditTypeRule();
        rule.setAuditType(new AuditType());
        rule.getAuditType().setId(NON_AUDIT_TYPE_ID);
        rule.setOperatorAccount(nonOperator);
        typeRules.add(rule);

        AuditType auditType = EntityFactory.makeAuditType(AUDIT_TYPE_ID);
        when(auditDataDAO.find(AuditType.class, AUDIT_TYPE_ID)).thenReturn(auditType);
        AuditCategory goodCat = EntityFactory.addCategories(auditType, CATEGORY_ID, "Good");
        AuditCategory badCat = EntityFactory.addCategories(auditType, NON_CATEGORY_ID, "Bad");

        categoryRules.clear();
        AuditCategoryRule catRule;
        catRule = new AuditCategoryRule();
        catRule.setAuditCategory(goodCat);
        catRule.setOperatorAccount(operator);
        categoryRules.add(catRule);
        catRule = new AuditCategoryRule();
        catRule.setAuditCategory(badCat);
        catRule.setOperatorAccount(nonOperator);
        categoryRules.add(catRule);

        when(categoryRuleCache.getRules(contractor, auditType)).thenReturn(categoryRules);
    }
}
