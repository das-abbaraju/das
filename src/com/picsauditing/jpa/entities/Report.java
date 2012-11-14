package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.json.simple.JSONObject;

import com.picsauditing.report.Definition;
import com.picsauditing.report.fields.ReportField;
import com.picsauditing.report.models.ModelType;
import com.picsauditing.report.tables.FieldImportance;

@SuppressWarnings("serial")
@Entity
@Table(name = "report")
public class Report extends BaseTable {

	private ModelType modelType;
	private String name;
	private String description;
	private String parameters;
	private int rowsPerPage = 50;
	
	private int numTimesFavorited;

	private Definition definition;
	private List<ReportUser> reportUsers = new ArrayList<ReportUser>();
	private String sql;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@ReportField(importance = FieldImportance.Required, width = 200)
	public ModelType getModelType() {
		return modelType;
	}

	public void setModelType(ModelType type) {
		this.modelType = type;
	}

	@Column(nullable = false)
	@ReportField(importance = FieldImportance.Required, width = 200)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@ReportField(importance = FieldImportance.Low, width = 400)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@ReportField(importance = FieldImportance.Low, width = 400)
	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	@SuppressWarnings("unchecked")
	public JSONObject toJSON(boolean full) {
		JSONObject obj = super.toJSON(full);
		obj.put("modelType", modelType.toString());
		obj.put("name", name);

		if (!full)
			return obj;

		obj.put("description", description);

		Definition defaultDefinition = this.definition;
		if (defaultDefinition == null) {
			defaultDefinition = new Definition(parameters);
		}
		
		JSONObject jsonDefinition = defaultDefinition.toJSON(true);
		obj.putAll(jsonDefinition);
		
		return obj;
	}

	@Transient
	public Definition getDefinition() {
		if (definition == null) {
			definition = new Definition(getParameters());
		}

		return definition;
	}

	public void setDefinition(Definition definition) {
		this.definition = definition;
	}
	
	@OneToMany(mappedBy = "report", cascade = { CascadeType.ALL })
	public List<ReportUser> getReportUsers() {
		return reportUsers;
	}

	public void setReportUsers(List<ReportUser> reportUsers) {
		this.reportUsers = reportUsers;
	}
	
	@Transient
	public ReportUser getReportUser(int userId) {
		for (ReportUser reportUser : reportUsers)
			if (userId == reportUser.getUser().getId())
				return reportUser;
		
		return null;
	}

	@Transient
	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	@Transient
	public int getRowsPerPage() {
		return rowsPerPage;
	}

	public void setRowsPerPage(int rowsPerPage) {
		this.rowsPerPage = rowsPerPage;
	}

	@Transient
	public int getNumTimesFavorited() {
		return numTimesFavorited;
	}

	public void setNumTimesFavorited(int numTimesFavorited) {
		this.numTimesFavorited = numTimesFavorited;
	}
	
	@Override
	public String toString() {
		return name;
	}
}