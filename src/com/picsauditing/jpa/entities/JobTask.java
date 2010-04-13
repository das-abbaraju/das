package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

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
	private String taskType;
	
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
	
	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
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
	public List<JobTaskCriteria> getJobTaskCriteria() {
		return jobTaskCriteria;
	}

	public void setJobTaskCriteria(List<JobTaskCriteria> jobTaskCriteria) {
		this.jobTaskCriteria = jobTaskCriteria;
	}
	
	@Transient
	public Map<Integer, Set<JobTaskCriteria>> getJobTaskCriteriaMap() {
		Map<Integer, Set<JobTaskCriteria>> criteriaGroup = new HashMap<Integer, Set<JobTaskCriteria>>();
		for(JobTaskCriteria criteria : getJobTaskCriteria()){
			if(criteriaGroup.containsKey(criteria.getGroupNumber()))
				criteriaGroup.get(criteria.getGroupNumber()).add(criteria);
			else {
				Set<JobTaskCriteria> set = new HashSet<JobTaskCriteria>();
				set.add(criteria);
				criteriaGroup.put(criteria.getGroupNumber(), set);
			}
		}
		return criteriaGroup;
	}
}
