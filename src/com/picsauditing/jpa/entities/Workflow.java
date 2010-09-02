package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

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

}
