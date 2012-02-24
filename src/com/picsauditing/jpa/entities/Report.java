package com.picsauditing.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.json.simple.JSONObject;

import com.picsauditing.report.SimpleReportDefinition;
import com.picsauditing.report.models.ModelType;
import com.picsauditing.util.JSONUtilities;

@SuppressWarnings("serial")
@Entity
@Table(name = "report")
public class Report extends BaseTable {

	private ModelType modelType;
	// TODO rename to name
	private String summary;
	private String description;
	private String parameters;
	private Account sharedWith;
	private String devParams;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public ModelType getModelType() {
		return modelType;
	}

	public void setModelType(ModelType type) {
		this.modelType = type;
	}

	@Column(nullable = false)
	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
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

	public String getDevParams() {
		return devParams;
	}

	public void setDevParams(String devParams) {
		this.devParams = devParams;
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
		obj.put("summary", summary);

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

	public void fromJSON(JSONObject obj) {
		// TODO write this!!

		// if (id == 0)
		// id = (Integer)obj.get("id");
		// createdBy = new User(obj.get("createdBy"));
	}
}
