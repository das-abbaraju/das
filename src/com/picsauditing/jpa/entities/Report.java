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
import com.picsauditing.report.access.ReportUtil;
import com.picsauditing.report.models.AbstractModel;
import com.picsauditing.report.models.ModelFactory;
import com.picsauditing.report.models.ModelType;
import com.picsauditing.report.tables.AbstractTable;
import com.picsauditing.util.JSONUtilities;
import com.picsauditing.util.Strings;

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

		if (!full)
			return obj;

		obj.put("description", description);

		Definition defaultDefinition = this.definition;
		if (defaultDefinition == null) {
			defaultDefinition = new Definition(parameters);
		}

		String filterExpresion = defaultDefinition.getFilterExpression();
		if (!Strings.isEmpty(filterExpresion)) {
			obj.put(ReportUtil.FILTER_EXPRESSION, filterExpresion);
		}

		if (defaultDefinition.getColumns().size() > 0) {
			obj.put("columns", JSONUtilities.convertFromList(defaultDefinition.getColumns()));
		}
		if (defaultDefinition.getFilters().size() > 0) {
			obj.put("filters", JSONUtilities.convertFromList(defaultDefinition.getFilters()));
		}
		if (defaultDefinition.getSorts().size() > 0) {
			obj.put("sorts", JSONUtilities.convertFromList(defaultDefinition.getSorts()));
		}

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