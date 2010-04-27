package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@SuppressWarnings("serial")
@Entity
@Table(name = "job_task_criteria")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class JobTaskCriteria extends BaseHistory {

	private JobTask task;
	private AssessmentTest assessmentTest;
	private int groupNumber;

	@ManyToOne
	@JoinColumn(name = "taskID", nullable = false, updatable = false)
	public JobTask getTask() {
		return task;
	}

	public void setTask(JobTask task) {
		this.task = task;
	}

	@ManyToOne
	@JoinColumn(name = "assessmentTestID", nullable = false, updatable = false)
	public AssessmentTest getAssessmentTest() {
		return assessmentTest;
	}

	public void setAssessmentTest(AssessmentTest assessmentTest) {
		this.assessmentTest = assessmentTest;
	}

	public int getGroupNumber() {
		return groupNumber;
	}

	public void setGroupNumber(int groupNumber) {
		this.groupNumber = groupNumber;
	}

}
