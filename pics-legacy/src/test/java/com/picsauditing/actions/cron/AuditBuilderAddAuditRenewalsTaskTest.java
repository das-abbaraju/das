package com.picsauditing.actions.cron;

import org.junit.Test;

public class AuditBuilderAddAuditRenewalsTaskTest {

    @Test
    public void testBasic() throws Exception {
        AuditBuilderAddAuditRenewalsTask task = new AuditBuilderAddAuditRenewalsTask();
        task.run();
    }
}
