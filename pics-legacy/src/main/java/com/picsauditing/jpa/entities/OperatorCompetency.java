package com.picsauditing.jpa.entities;

import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.fields.ReportField;
import com.picsauditing.report.tables.FieldImportance;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
@Entity
@Table(name = "operator_competency")
public class OperatorCompetency extends BaseTable implements Comparable<OperatorCompetency> {

	private String category;
	private String label;
	private String description;
	private OperatorAccount operator;
	private JobCompetencyStats jobCompentencyStats;
	private List<OperatorCompetencyCourse> courses = new ArrayList<>();
	private List<OperatorCompetencyEmployeeFile> employeeFiles = new ArrayList<>();

	@ReportField(type = FieldType.String)
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@ReportField(type = FieldType.String, importance = FieldImportance.Required)
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@ReportField(type = FieldType.String)
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

	@OneToMany(mappedBy = "competency")
	public List<OperatorCompetencyCourse> getCourses() {
		return courses;
	}

	public void setCourses(List<OperatorCompetencyCourse> courses) {
		this.courses = courses;
	}

	@OneToMany(mappedBy = "competency")
	public List<OperatorCompetencyEmployeeFile> getEmployeeFiles() {
		return employeeFiles;
	}

	public void setEmployeeFiles(List<OperatorCompetencyEmployeeFile> employeeFiles) {
		this.employeeFiles = employeeFiles;
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
		List<String> courseTypeKeys = new ArrayList<>();
		for (OperatorCompetencyCourse course : getCourses()) {
			courseTypeKeys.add(course.getCourseType().getI18nKey());
		}

		final String[] courseTypeKeysArray = (String[]) courseTypeKeys.toArray();

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
		if (this.category.equals(o.getCategory())) {
			return this.label.compareTo(o.getLabel());
		}

		return this.category.compareTo(o.getCategory());
	}

	@Transient
	public boolean isRequiresDocumentation() {
		for (OperatorCompetencyCourse course : courses) {
			if (course.getCourseType().isRequiresDocumentation()) {
				return true;
			}
		}

		return false;
	}
}
