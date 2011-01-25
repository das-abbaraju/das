package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "workflow")
@SuppressWarnings("serial")
public class Workflow extends BaseTable {

	public static final int AUDIT_REQUIREMENTS_WORKFLOW = 2;
	public static final int HSE_WORKFLOW = 8;
	public static final int MANUAL_AUDIT_WORKFLOW = 7;

	private String name;
	private boolean hasRequirements;
	private List<WorkflowStep> steps = new ArrayList<WorkflowStep>();

	@Column(nullable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@OneToMany(mappedBy = "workflow", cascade = { CascadeType.ALL })
	public List<WorkflowStep> getSteps() {
		return steps;
	}

	public void setSteps(List<WorkflowStep> steps) {
		this.steps = steps;
	}

	@Transient
	public WorkflowStep getStep(int stepID) {
		for (WorkflowStep step : steps) {
			if (step.getId() == stepID)
				return step;
		}
		return null;
	}

	@Transient
	public WorkflowStep getFirstStep() {
		for (WorkflowStep step : steps) {
			if (step.getOldStatus() == null)
				return step;
		}
		return null;
	}

	@Transient
	public boolean isHasSubmittedStep() {
		for (WorkflowStep step : steps) {
			if (step.getNewStatus().isSubmitted())
				return true;
		}
		return false;
	}

	public boolean isHasRequirements() {
		return hasRequirements;
	}

	public void setHasRequirements(boolean hasRequirements) {
		this.hasRequirements = hasRequirements;
	}

	@Override
	@Transient
	public Workflow clone() {
		Workflow clone = new Workflow();

		clone.createdBy = this.getCreatedBy();
		clone.creationDate = this.getCreationDate();
		clone.id = this.getId();
		clone.name = this.getName();
		clone.updateDate = this.getUpdateDate();
		clone.updatedBy = this.getUpdatedBy();

		return clone;
	}
}
