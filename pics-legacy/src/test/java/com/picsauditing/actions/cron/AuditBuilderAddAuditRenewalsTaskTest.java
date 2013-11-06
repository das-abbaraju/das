package com.picsauditing.actions.cron;

import com.picsauditing.auditBuilder.AuditBuilder;
import com.picsauditing.dao.ContractorAuditDAO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AuditBuilderAddAuditRenewalsTaskTest {
    private AuditBuilderAddAuditRenewalsTask task;
    @Mock
    private ContractorAuditDAO contractorAuditDAO;
    @Mock
    private AuditBuilder auditBuilder;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        task = new AuditBuilderAddAuditRenewalsTask();
        task.contractorAuditDAO = contractorAuditDAO;
        task.auditBuilder = auditBuilder;
    }

    @Test
    public void testBasic() throws Exception {
        task.run();
    }
}
