package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

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
	private int displayOrder;

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

	public int getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject toJSON(boolean full) {
		JSONObject json = super.toJSON(full);

		json.put("label", label);
		json.put("name", name);
		if (full)
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
	/**
	 * Return a map of groups and a set of criteria effective right now
	 */
	public Map<Integer, Set<JobTaskCriteria>> getJobTaskCriteriaMap() {
		return getJobTaskCriteriaMap(new Date());
	}

	@Transient
	/**
	 * Return a map of groups and a set of criteria effective as of the given date
	 */
	public Map<Integer, Set<JobTaskCriteria>> getJobTaskCriteriaMap(Date now) {
		Map<Integer, Set<JobTaskCriteria>> criteriaGroup = new HashMap<Integer, Set<JobTaskCriteria>>();
		for (JobTaskCriteria criteria : getJobTaskCriteria()) {
			if (criteria.isCurrent(now)) {

				if (criteriaGroup.containsKey(criteria.getGroupNumber()))
					criteriaGroup.get(criteria.getGroupNumber()).add(criteria);
				else {
					Set<JobTaskCriteria> set = new HashSet<JobTaskCriteria>();
					set.add(criteria);
					criteriaGroup.put(criteria.getGroupNumber(), set);
				}
			}
		}
		return criteriaGroup;
	}

	@Transient
	/**
	 * Recreate the history of this employee's qualifications for this task over time
	 */
	public Map<Date, Boolean> reconstructQualificationHistory(List<AssessmentResult> testResults) {
		Set<Date> markers = new TreeSet<Date>();
		for (AssessmentResult results : testResults) {
			if (results.getEffectiveDate() != null)
				markers.add(results.getEffectiveDate());
			if (results.getExpirationDate() != null)
				markers.add(results.getExpirationDate());
		}
		for (JobTaskCriteria criteria : jobTaskCriteria) {
			if (criteria.getEffectiveDate() != null)
				markers.add(criteria.getEffectiveDate());
			if (criteria.getExpirationDate() != null)
				markers.add(criteria.getExpirationDate());
		}
		if (markers.size() == 0)
			markers.add(new Date());

		Map<Date, Boolean> series = new TreeMap<Date, Boolean>();
		Boolean previousValue = null;
		for (Date date : markers) {
			Boolean qualified = isQualified(date, testResults);
			if ((previousValue == null && qualified != null)
					|| (previousValue != null && !previousValue.equals(qualified))) {
				series.put(date, qualified);
			}
			previousValue = qualified;
		}
		return series;
	}

	@Transient
	/**
	 * Determine if the employee with the given testResults is qualified to perform this task on this date
	 */
	private Boolean isQualified(Date now, List<AssessmentResult> testResults) {
		Map<AssessmentTest, AssessmentResult> resultMap = new HashMap<AssessmentTest, AssessmentResult>();
		for (AssessmentResult results : testResults) {
			if (results.isCurrent(now))
				resultMap.put(results.getAssessmentTest(), results);
		}

		Collection<Set<JobTaskCriteria>> taskCriteria = getJobTaskCriteriaMap(now).values();
		if (taskCriteria.size() == 0)
			// No criteria exist
			return null;

		for (Set<JobTaskCriteria> group : taskCriteria) {
			// If ALL of the criteria for this group are met, then the employee
			// can perform the task
			boolean qualified = true;
			for (JobTaskCriteria jobTaskCriteria : group) {
				AssessmentResult testResult = resultMap.get(jobTaskCriteria.getAssessmentTest());
				if (testResult == null)
					qualified = false;
				// Otherwise we assume they passed the test
			}

			if (qualified)
				return true;
			// One or more criteria for this group wasn't met, so let's check
			// the next one
		}

		// No criteria groups we completely met
		return false;
	}

	@Override
	public String toString() {
		return label + " (" + id + ")";
	}
}
