package com.picsauditing.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.json.simple.JSONObject;

import com.picsauditing.report.SimpleReportDefinition;
import com.picsauditing.report.models.ModelType;
import com.picsauditing.util.JSONUtilities;

@SuppressWarnings("serial")
@Entity
@Table(name = "report")
public class Report extends BaseTable {

	private ModelType modelType;
	private String name;
	private String description;
	private String parameters;
	private Account sharedWith;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public ModelType getModelType() {
		return modelType;
	}

	public void setModelType(ModelType type) {
		this.modelType = type;
	}

	@Column(nullable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	@ManyToOne
	@JoinColumn(name = "sharedWith")
	public Account getSharedWith() {
		return sharedWith;
	}

	public void setSharedWith(Account sharedWith) {
		this.sharedWith = sharedWith;
	}

	@SuppressWarnings("unchecked")
	public JSONObject toJSON(boolean full) {
		JSONObject obj = super.toJSON(full);
		obj.put("modelType", modelType.toString());
		obj.put("name", name);

		if (full) {
			obj.put("description", description);
			SimpleReportDefinition definition = new SimpleReportDefinition(parameters);
			if (definition.getColumns().size() > 0)
				obj.put("columns", JSONUtilities.convertFromList(definition.getColumns()));
			if (definition.getFilters().size() > 0)
				obj.put("filters", JSONUtilities.convertFromList(definition.getFilters()));
			if (definition.getOrderBy().size() > 0)
				obj.put("sorts", JSONUtilities.convertFromList(definition.getOrderBy()));
		}
		return obj;
	}
}
