package com.picsauditing.actions.cron;

import com.picsauditing.audits.AuditBuilderFactory;
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
    private AuditBuilderFactory auditBuilderFactory;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        task = new AuditBuilderAddAuditRenewalsTask();
        task.contractorAuditDAO = contractorAuditDAO;
        task.auditBuilderFactory = auditBuilderFactory;
    }

    @Test
    public void testBasic() throws Exception {
        task.run();
    }
}
