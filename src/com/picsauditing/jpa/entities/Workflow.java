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

	private String name;
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
}
