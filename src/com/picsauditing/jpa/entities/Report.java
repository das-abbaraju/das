package com.picsauditing.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.json.simple.JSONObject;

import com.picsauditing.report.Definition;
import com.picsauditing.report.models.AbstractModel;
import com.picsauditing.report.models.ModelFactory;
import com.picsauditing.report.models.ModelType;
import com.picsauditing.report.tables.AbstractTable;

@SuppressWarnings("serial")
@Entity
@Table(name = "report")
public class Report extends BaseTable {

	private ModelType modelType;
	private String name;
	private String description;
	private String parameters;
	private int rowsPerPage = 50;
	
	private boolean isPrivate;
	private int numTimesFavorited;

	private Definition definition;

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

	public boolean isPrivate() {
		return isPrivate;
	}

	public void setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
	}

	@Transient
	public boolean isPublic() {
		return !isPrivate;
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

	@Transient
	public int getRowsPerPage() {
		return rowsPerPage;
	}

	public void setRowsPerPage(int rowsPerPage) {
		this.rowsPerPage = rowsPerPage;
	}

	@Transient
	public AbstractModel getModel() {
		return ModelFactory.build(modelType);
	}

	@Transient
	public AbstractTable getTable() {
		if (getModel() != null) {
			return getModel().getRootTable();
		}

		return null;
	}

	@Transient
	public int getNumTimesFavorited() {
		return numTimesFavorited;
	}

	public void setNumTimesFavorited(int numTimesFavorited) {
		this.numTimesFavorited = numTimesFavorited;
	}
}
