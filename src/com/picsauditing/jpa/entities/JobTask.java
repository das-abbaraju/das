package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.json.simple.JSONObject;

@SuppressWarnings("serial")
@Entity
@Table(name = "job_task")
@PrimaryKeyJoinColumn(name = "id")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "temp")
public class JobTask extends BaseTable {

	private OperatorAccount operator;
	private String label;
	private String name;
	private boolean active;
	
	private List<JobTaskCriteria> jobTaskCriteria = new ArrayList<JobTaskCriteria>();

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

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject toJSON(boolean full) {
		JSONObject json = super.toJSON(full);

		json.put("label", label);
		json.put("name", name);
		if(full)
			json.put("operator", operator.toJSON());
		
		return json;
	}

	@OneToMany(mappedBy = "task", cascade = { CascadeType.ALL })
	public List<JobTaskCriteria> getCriteria() {
		return jobTaskCriteria;
	}
}
