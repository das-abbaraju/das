package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@SuppressWarnings("serial")
@Entity
@Table(name = "job_competency")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "temp")
public class JobCompetency extends BaseTable {

	private JobRole jobRole;
	private OperatorCompetency competency;

	@ManyToOne
	@JoinColumn(name = "jobRoleID", nullable = false, updatable = false)
	public JobRole getJobRole() {
		return jobRole;
	}

	public void setJobRole(JobRole jobRole) {
		this.jobRole = jobRole;
	}

	@ManyToOne
	@JoinColumn(name = "competencyID", nullable = false, updatable = false)
	public OperatorCompetency getCompetency() {
		return competency;
	}

	public void setCompetency(OperatorCompetency competency) {
		this.competency = competency;
	}

}
