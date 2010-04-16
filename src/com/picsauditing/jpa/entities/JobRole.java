package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@SuppressWarnings("serial")
@Entity
@Table(name = "job_role")
public class JobRole extends BaseTable {

	private Account account;
	private String name;
	private boolean active = true;
	private List<JobCompetency> competencies = new ArrayList<JobCompetency>();
	private List<OperatorCompetency> otherCompetencies = new ArrayList<OperatorCompetency>();

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

	@OneToMany(mappedBy = "jobRole", cascade = { CascadeType.ALL })
	public List<JobCompetency> getCompetencies() {
		return competencies;
	}

	public void setCompetencies(List<JobCompetency> competencies) {
		this.competencies = competencies;
	}

	@Transient
	public List<OperatorCompetency> getOtherCompetencies() {
		return otherCompetencies;
	}

	public void setAllCompetencies(List<OperatorCompetency> allCompetencies) {
		otherCompetencies.clear();
		Set<OperatorCompetency> currentOperatorCompetencies = new HashSet<OperatorCompetency>();
		for (OperatorCompetency jobCompetency : allCompetencies) {
			if (!currentOperatorCompetencies.contains(jobCompetency))
				otherCompetencies.add(jobCompetency);
		}
	}
	

	

}
