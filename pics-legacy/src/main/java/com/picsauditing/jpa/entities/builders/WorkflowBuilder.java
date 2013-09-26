package com.picsauditing.jpa.entities.builders;

import com.picsauditing.jpa.entities.Workflow;
import com.picsauditing.jpa.entities.WorkflowStep;

import java.util.ArrayList;

public class WorkflowBuilder {
    private Workflow workflow = new Workflow();

    public Workflow build() {
        return workflow;
    }

    public WorkflowBuilder step(WorkflowStep workflowStep) {
        if (workflow.getSteps() == null) {
            workflow.setSteps(new ArrayList<WorkflowStep>());
        }
        workflow.getSteps().add(workflowStep);
        return this;
    }

}
