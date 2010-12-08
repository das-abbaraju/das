package com.picsauditing.jpa.entities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@SuppressWarnings("serial")
@Entity
@Table(name = "job_site")
public class JobSite extends BaseTable implements Comparable<JobSite> {

	private OperatorAccount operator;
	private String label;
	private String name;
	private String city;
	private State state;
	private Country country;
	private Date projectStart;
	private Date projectStop;
	
	private List<JobSiteTask> tasks = new ArrayList<JobSiteTask>();

	@ManyToOne
	@JoinColumn(name = "opID", nullable = false, updatable = false)
	public OperatorAccount getOperator() {
		return operator;
	}

	public void setOperator(OperatorAccount operator) {
		this.operator = operator;
	}

	@Column(nullable = false)
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@ManyToOne
	@JoinColumn(name = "state")
	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	@ManyToOne
	@JoinColumn(name = "country")
	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public Date getProjectStart() {
		return projectStart;
	}

	public void setProjectStart(Date projectStart) {
		this.projectStart = projectStart;
	}

	public Date getProjectStop() {
		return projectStop;
	}

	public void setProjectStop(Date projectStop) {
		this.projectStop = projectStop;
	}
	
	@OneToMany(mappedBy = "job")
	public List<JobSiteTask> getTasks() {
		return tasks;
	}
	
	public void setTasks(List<JobSiteTask> tasks) {
		this.tasks = tasks;
	}
	
	@Transient
	public boolean isActive(Date date) {
		// Different locales make the same basic date fail the Date.equals method
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		if (date == null)
			date = new Date();
		
		if (projectStart != null && projectStop != null)
			return (sdf.format(projectStart).equals(sdf.format(date)) || projectStart.before(date))
				&& projectStop.after(date);
		if (projectStart != null)
			return projectStart.before(date) || sdf.format(projectStart).equals(sdf.format(date));
		if (projectStop != null)
			return projectStop.after(date);
		
		// Neither are filled in so assume the project is active?
		return true;
	}
	
	@Transient
	@Override
	public int compareTo(JobSite o) {
		if (this.label.equals(o.getLabel()))
			return this.name.compareTo(o.getName());
		
		return this.label.compareTo(o.getLabel());
	}
}
