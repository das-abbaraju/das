package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@SuppressWarnings("serial")
@Entity
@Table(name = "operator_competency")
public class OperatorCompetency extends BaseTable implements Comparable<OperatorCompetency> {

	private String category;
	private String label;
	private String description;
	private OperatorAccount operator;
	private JobCompetencyStats jobCompentencyStats;

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@ManyToOne
	@JoinColumn(name = "opID", nullable = false)
	public OperatorAccount getOperator() {
		return operator;
	}

	public void setOperator(OperatorAccount operator) {
		this.operator = operator;
	}

	@Transient
	public JobCompetencyStats getJobCompentencyStats() {
		return jobCompentencyStats;
	}

	public void setJobCompentencyStats(JobCompetencyStats jobCompentencyStats) {
		this.jobCompentencyStats = jobCompentencyStats;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject toJSON(boolean full) {
		JSONObject json = super.toJSON(full);

		json.put("category", category);
		json.put("label", label);
		json.put("description", description);

		return json;
	}

	@SuppressWarnings("unchecked")
	@Transient
	public JSONArray toTableJSON() {
		return new JSONArray() {
			{
				add(id);
				add(category);
				add(label);
				add(description);
			}
		};
	}

	@Override
	public String toString() {
		return category + ": " + label + " (" + id + ")";
	}
	
	@Override
	public int compareTo(OperatorCompetency o) {
		if (this.category.equals(o.getCategory()))
			return this.label.compareTo(o.getLabel());
		
		return this.category.compareTo(o.getCategory());
	}
}
