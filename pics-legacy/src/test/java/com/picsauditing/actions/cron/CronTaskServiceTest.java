package com.picsauditing.actions.cron;

import static com.picsauditing.util.Assert.assertContains;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class CronTaskServiceTest {
    private CronTaskService service = new CronTaskService(new MockCronTask());

    @Mock
    CronTask cronTask;

    @Test
    public void testAllTests() {
        Collection<String> allTasks = service.getAllTasks();
        assertEquals(19, allTasks.size());
    }

    @Test
    public void testDescription() {
        assertEquals("this is a test mock", service.getDescription());
    }

    @Test
    public void testGetSteps() {
        List<String> list = new ArrayList<>();
        list.add("next step 1");
        list.add("next step 2");
        assertEquals(list, service.getSteps());
    }

    @Test
    public void testRun() {
        CronTaskResult run = service.run();
        assertTrue(run.wasSuccessful());
        assertEquals("test run was successful", run.getLog());
    }

    @Test
    public void testHandleException() throws Throwable {
        MockitoAnnotations.initMocks(this);

        service = new CronTaskService(cronTask);

        when(cronTask.run()).thenThrow(new Exception("I am a message"));
        CronTaskResult run = service.run();
        assertFalse(run.wasSuccessful());
        assertContains("java.lang.Exception: I am a message", run.getLog());
    }

    public class MockCronTask implements CronTask {
        public String getName() {
            return "Mock Cron Task";
        }

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
