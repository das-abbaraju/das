package com.picsauditing.flagcalculator.entities;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "com.picsauditing.flagcalculator.entities.Workflow")
@Table(name = "workflow")
@SuppressWarnings("serial")
public class Workflow extends BaseTable {
	private List<WorkflowStep> steps = new ArrayList<WorkflowStep>();

	@OneToMany(mappedBy = "workflow", cascade = { CascadeType.ALL })
	public List<WorkflowStep> getSteps() {
		return steps;
	}

	public void setSteps(List<WorkflowStep> steps) {
		this.steps = steps;
	}
}