package com.picsauditing.actions;

import com.picsauditing.PicsActionTest;
import com.picsauditing.actions.cron.CronTaskException;
import org.junit.Assert;
import org.junit.Test;

public class CronTest extends PicsActionTest {
    private Cron cron = new Cron();

    @Test
    public void testInfo() throws Exception {
        cron.setTask("TestTask2");
        try {
            cron.view();
        }
        catch (CronTaskException cte) {
            Assert.assertEquals("TestTask2 is not a registered CronTask", cte.getMessage());
        }
    }
}
