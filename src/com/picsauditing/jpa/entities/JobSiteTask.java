package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "job_site_task")
public class JobSiteTask extends BaseHistory {

	private JobSite job;
	private JobTask task;

	@ManyToOne
	@JoinColumn(name = "jobID", nullable = false, updatable = false)
	public JobSite getJob() {
		return job;
	}

	public void setJob(JobSite job) {
		this.job = job;
	}

	@ManyToOne
	@JoinColumn(name = "taskID", nullable = false, updatable = false)
	public JobTask getTask() {
		return task;
	}

	public void setTask(JobTask task) {
		this.task = task;
	}

}
