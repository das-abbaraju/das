package com.picsauditing.actions;

import com.opensymphony.xwork2.ActionSupport;
import com.picsauditing.access.Anonymous;
import com.picsauditing.actions.cron.CronTaskException;
import com.picsauditing.actions.cron.CronTaskResult;
import com.picsauditing.actions.cron.CronTaskService;

import java.util.Collection;
import java.util.List;

@SuppressWarnings("serial")
public class Cron extends ActionSupport {
    private CronTaskService service;
    private String task;
    private CronTaskResult result;

    /**
     * Default to list mode
     */
    @Anonymous
    public String execute() {
        return SUCCESS;
    }

    /**
     * Show a list of all tasks on the class path
     */
    @Anonymous
    public String list() {
        return "list";
    }

    public Collection<String> getAllTasks() {
        return CronTaskService.getAllTasks();
    }

    /**
     * Show a specific task, name, description and possible next steps
     */
    @Anonymous
    public String view() throws CronTaskException {
        service = new CronTaskService(task);
        return "view";
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getTaskDescription() {
        return service.getDescription();
    }

    public List<String> getSteps() {
        return service.getSteps();
    }

    /**
     * Run the given task
     */
    @Anonymous
    public String run() throws CronTaskException {
        service = new CronTaskService(task);
        result = service.run();
        return "run";
    }

    public String getOutput() {
        return "RESULT = " + (result.wasSuccessful() ? "SUCCESS" : "FAILURE") + " . . . (" + service.getRunTime() + " millis) \n" +
                result.getLog() + "\n";
    }
}
