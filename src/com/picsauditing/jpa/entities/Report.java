package com.picsauditing.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.json.simple.JSONObject;

import com.picsauditing.report.Definition;
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

	private Definition definition;
	private int rowsPerPage = 50;

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

	@SuppressWarnings("unchecked")
	public JSONObject toJSON(boolean full) {
		JSONObject obj = super.toJSON(full);
		obj.put("modelType", modelType.toString());
		obj.put("name", name);

		if (full) {
			obj.put("description", description);

			Definition defaultDefinition = this.definition;
			if (defaultDefinition == null) {
				defaultDefinition = new Definition(parameters);
			}

			if (defaultDefinition.getColumns().size() > 0)
				obj.put("columns", JSONUtilities.convertFromList(defaultDefinition.getColumns()));
			if (defaultDefinition.getFilters().size() > 0)
				obj.put("filters", JSONUtilities.convertFromList(defaultDefinition.getFilters()));
			if (defaultDefinition.getSorts().size() > 0)
				obj.put("sorts", JSONUtilities.convertFromList(defaultDefinition.getSorts()));
		}
		return obj;
	}

	@Transient
	public Definition getDefinition() {
		return definition;
	}

	public void setDefinition(Definition definition) {
		this.definition = definition;
	}

	@Transient
	public int getRowsPerPage() {
		return rowsPerPage;
	}

	public void setRowsPerPage(int rowsPerPage) {
		this.rowsPerPage = rowsPerPage;
	}
}
