package com.picsauditing.actions.cron;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CronTaskServiceTest {
    private CronTaskService service = new CronTaskService(new MockCronTask());

    @Test
    public void testAllTests() {
        Collection<String> allTasks = service.getAllTasks();
        Assert.assertEquals(18, allTasks.size());
    }

    @Test
    public void testDescription() {
        Assert.assertEquals("this is a test mock", service.getDescription());
    }

    @Test
    public void testGetSteps() {
        List<String> list = new ArrayList<>();
        list.add("next step 1");
        list.add("next step 2");
        Assert.assertEquals(list, service.getSteps());
    }

    @Test
    public void testRun() {
        CronTaskResult run = service.run();
        Assert.assertTrue(run.wasSuccessful());
        Assert.assertEquals("test run was successful", run.getLog());
    }

    public class MockCronTask implements CronTask {
        public String getDescription() {
            return "this is a test mock";
        }

        public List<String> getSteps() {
            List<String> list = new ArrayList<>();
            list.add("next step 1");
            list.add("next step 2");
            return list;
        }

        public CronTaskResult run() throws Throwable {
            return new CronTaskResult(true, "test run was successful");
        }
    }
}
