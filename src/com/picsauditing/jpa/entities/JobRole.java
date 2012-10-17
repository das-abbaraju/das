package com.picsauditing.jpa.entities;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@SuppressWarnings("serial")
@Entity
@Table(name = "job_role")
public class JobRole extends BaseTable implements Comparable<JobRole> {

	private Account account;
	private String name;
	private boolean active = true;
	private List<JobCompetency> jobCompetencies;

	@ManyToOne
	@JoinColumn(name = "accountID", nullable = false, updatable = false)
	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@OneToMany(mappedBy = "jobRole")
	public List<JobCompetency> getJobCompetencies() {
		return jobCompetencies;
	}
	
	public void setJobCompetencies(List<JobCompetency> jobCompetencies) {
		this.jobCompetencies = jobCompetencies;
	}

	@Override
	@Transient
	public int compareTo(JobRole o) {
		if (!this.account.equals(o.getAccount())) {
			return this.getName().compareTo(o.getName());
		} else {
			return this.account.compareTo(o.getAccount());
		}
	}
}
