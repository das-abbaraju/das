package com.picsauditing.jpa.entities.builders;

import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.WorkflowStep;

public class WorkflowStepBuilder {
    private WorkflowStep workflowStep = new WorkflowStep();

    public WorkflowStep build() {
        return workflowStep;
    }

    public WorkflowStepBuilder oldStatus(AuditStatus status) {
        workflowStep.setOldStatus(status);
        return this;
    }

    public WorkflowStepBuilder newStatus(AuditStatus status) {
        workflowStep.setNewStatus(status);
        return this;
    }

    public WorkflowStepBuilder noteRequired() {
        workflowStep.setNoteRequired(true);
        return this;
    }
}
