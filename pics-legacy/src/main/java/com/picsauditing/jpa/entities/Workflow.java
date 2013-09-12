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
	public static final int PQF_WORKFLOW = 4;
	public static final int HSE_WORKFLOW = 8;
	public static final int MANUAL_AUDIT_WORKFLOW = 7;

	private String name;
	private boolean hasRequirements;
	private boolean useStateForEdit;
	private List<WorkflowStep> steps = new ArrayList<WorkflowStep>();
	private List<WorkflowState> states = new ArrayList<WorkflowState>();

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

	@OneToMany(mappedBy = "workflow", cascade = { CascadeType.ALL })
	public List<WorkflowState> getStates() {
		return states;
	}

	public void setStates(List<WorkflowState> states) {
		this.states = states;
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
	
	@Transient
	public boolean isHasState(AuditStatus status) {
		for (WorkflowState state : states) {
			if (state.getStatus().equals(status))
				return true;
		}
		return false;
	}

	@Transient
	public boolean isUsingState(AuditStatus status) {
		for (WorkflowStep step : steps) {
			if ((step.getOldStatus() != null && step.getOldStatus().equals(status))
					|| step.getNewStatus().equals(status))
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

	public boolean isUseStateForEdit() {
		return useStateForEdit;
	}

	public void setUseStateForEdit(boolean useStateForEdit) {
		this.useStateForEdit = useStateForEdit;
	}

}
