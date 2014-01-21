package com.picsauditing.actions.operators;


import com.picsauditing.PicsActionTest;
import com.picsauditing.auditBuilder.AuditCategoryRuleCache;
import com.picsauditing.auditBuilder.AuditTypeRuleCache;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.jpa.entities.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.mockito.MockitoAnnotations.Mock;
import org.powermock.reflect.Whitebox;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;

public class OperatorConfigurationTest extends PicsActionTest {
    public static final int OPERATOR_ID = 3;
    @Mock
    protected AuditDecisionTableDAO adtDAO;
    @Mock
    protected AuditTypeRuleCache auditTypeRuleCache;
    @Mock
    protected AuditCategoryRuleCache auditCategoryRuleCache;

    public OperatorConfiguration operatorConfiguration;

    @Before
    public void setup() throws IllegalAccessException, InstantiationException {
        MockitoAnnotations.initMocks(this);
        operatorConfiguration = new OperatorConfiguration();
        Whitebox.setInternalState(operatorConfiguration, "adtDAO", adtDAO);
        Whitebox.setInternalState(operatorConfiguration, "auditTypeRuleCache", auditTypeRuleCache);
        Whitebox.setInternalState(operatorConfiguration, "auditCategoryRuleCache", auditCategoryRuleCache);
    }

    @Test
    public void testAddAudit() throws Exception {
        OperatorAccount operator = OperatorAccount.builder().id(OPERATOR_ID).build();
        int auditTypeId = 1337;
        operatorConfiguration.setOperator(operator);
        operatorConfiguration.setAuditTypeID(auditTypeId);
        operatorConfiguration.setButton(OperatorConfiguration.BUTTON_ADD_AUDIT);

        ArgumentCaptor<AuditTypeRule> auditTypeRuleCaptor = ArgumentCaptor.forClass(AuditTypeRule.class);

        assertEquals(OperatorConfiguration.RESULT_AUDIT, operatorConfiguration.execute());

        verify(adtDAO).save(auditTypeRuleCaptor.capture());
        AuditTypeRule auditTypeRule = auditTypeRuleCaptor.getValue();
        assertEquals(auditTypeId, auditTypeRule.getAuditType().getId());
        assertEquals(OPERATOR_ID, auditTypeRule.getOperatorAccount().getId());
        assertEquals(PastAuditYear.Any, auditTypeRule.getYearToCheck());
    }

    @Test
    public void testAddCategory() throws Exception {
        OperatorAccount operator = OperatorAccount.builder().id(OPERATOR_ID).build();
        int categoryId = 1337;
        operatorConfiguration.setOperator(operator);
        operatorConfiguration.setCatID(categoryId);
        operatorConfiguration.setButton(OperatorConfiguration.BUTTON_AUDIT_CAT);

        ArgumentCaptor<AuditCategoryRule> auditCategoryRuleCaptor = ArgumentCaptor.forClass(AuditCategoryRule.class);

        assertEquals(OperatorConfiguration.RESULT_CATEGORY, operatorConfiguration.execute());

        verify(adtDAO).save(auditCategoryRuleCaptor.capture());
        AuditCategoryRule auditCategoryRule = auditCategoryRuleCaptor.getValue();
        assertEquals(AuditType.PQF, auditCategoryRule.getAuditType().getId());
        assertEquals(categoryId, auditCategoryRule.getAuditCategory().getId());
        assertEquals(OPERATOR_ID, auditCategoryRule.getOperatorAccount().getId());
        assertEquals(PastAuditYear.Any, auditCategoryRule.getYearToCheck());
    }
}
