package com.picsauditing.actions;

import com.picsauditing.PicsActionTest;
import org.junit.Assert;
import org.junit.Test;

public class CronTest extends PicsActionTest {
    private Cron cron = new Cron();

    @Test
    public void testInfo() throws Exception {
        cron.setTask("TestTask");
        cron.view();
        Assert.assertEquals("This is a test", cron.getTaskDescription());
    }


}
