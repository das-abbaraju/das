package com.picsauditing.actions.operators;


import com.picsauditing.PicsActionTest;
import com.picsauditing.auditBuilder.AuditTypeRuleCache;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.jpa.entities.AuditTypeRule;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.PastAuditYear;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.mockito.MockitoAnnotations.Mock;
import org.powermock.reflect.Whitebox;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;

public class OperatorConfigurationTest extends PicsActionTest {
    @Mock
    protected AuditDecisionTableDAO adtDAO;
    @Mock
    protected AuditTypeRuleCache auditTypeRuleCache;

    public OperatorConfiguration operatorConfiguration;

    @Before
    public void setup() throws IllegalAccessException, InstantiationException {
        MockitoAnnotations.initMocks(this);
        operatorConfiguration = new OperatorConfiguration();
        Whitebox.setInternalState(operatorConfiguration, "adtDAO", adtDAO);
        Whitebox.setInternalState(operatorConfiguration, "auditTypeRuleCache", auditTypeRuleCache);
    }

    @Test
    public void testAddAudit() throws Exception {
        OperatorAccount operator = OperatorAccount.builder().id(3).build();
        operatorConfiguration.setOperator(operator);
        operatorConfiguration.setAuditTypeID(1337);
        operatorConfiguration.setButton("Add Audit");

        ArgumentCaptor<AuditTypeRule> auditTypeRule = ArgumentCaptor.forClass(AuditTypeRule.class);

        assertEquals("audit", operatorConfiguration.execute());

        verify(adtDAO).save(auditTypeRule.capture());
        assertEquals(1337, auditTypeRule.getValue().getAuditType().getId());
        assertEquals(3, auditTypeRule.getValue().getOperatorAccount().getId());
        assertEquals(PastAuditYear.Any, auditTypeRule.getValue().getYearToCheck());
    }


}
