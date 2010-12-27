package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "job_contractor")
public class JobContractor extends BaseTable {

	private JobSite job;
	private ContractorAccount contractor;

	@ManyToOne
	@JoinColumn(name = "jobID", nullable = false, updatable = false)
	public JobSite getJob() {
		return job;
	}

	public void setJob(JobSite job) {
		this.job = job;
	}

	@ManyToOne
	@JoinColumn(name = "conID", nullable = false, updatable = false)
	public ContractorAccount getContractor() {
		return contractor;
	}

	public void setContractor(ContractorAccount contractor) {
		this.contractor = contractor;
	}
}