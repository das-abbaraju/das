package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@SuppressWarnings("serial")
@Entity
@Table(name = "operator_competency")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class OperatorCompetency extends BaseTable implements Comparable<OperatorCompetency> {

	private String category;
	private String label;
	private String description;
	private OperatorAccount operator;
	//private String helpPage;
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

	/*
	public String getHelpPage() {
		return helpPage;
	}

	public void setHelpPage(String helpPage) {
		this.helpPage = helpPage;
	}

	@Transient
	public String getHelpPageLink() {
		try {
			return "<a href=\"" + URLEncoder.encode(helpPage, "UTF-8") + "\">" + helpPage + "</a>";
		} catch (UnsupportedEncodingException e) {
			return helpPage; // just giving back the unlinked text
		}
	}
	 */

	@Transient
	public JobCompetencyStats getJobCompentencyStats() {
		return jobCompentencyStats;
	}

	public void setJobCompentencyStats(JobCompetencyStats jobCompentencyStats) {
		this.jobCompentencyStats = jobCompentencyStats;
	}

	@Transient
	public String getEditLink() {
		return "<a href=\"#\" onclick=\"show(" + id + "); return false;\" class=\"edit\"></a>";
	}

	/*
	@Transient
	public String getDeleteLink() {
		return "<a href=\"#\" onclick=\"remove(" + id + "); return false;\" class=\"remove\"></a>";
	}
	 */

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject toJSON(boolean full) {
		JSONObject json = super.toJSON(full);

		json.put("category", category);
		json.put("label", label);
		json.put("description", description);
		//json.put("helpPage", helpPage);
		//json.put("helpPageLink", getHelpPageLink());
		json.put("editLink", getEditLink());
		//json.put("deleteLink", getDeleteLink());

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
				//add(getHelpPageLink());
				add(getEditLink());
				//add(getDeleteLink());
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
