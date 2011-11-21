package com.picsauditing.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.json.simple.JSONObject;

@SuppressWarnings("serial")
@Entity
@Table(name = "rpt_model_column")
public class ReportModelColumn extends BaseTable implements JSONable {

	protected ReportModel model;
	protected String columnName;
	protected String columnAlias;
	protected String defaultHeader;
	protected String dataType;

	@ManyToOne
	@JoinColumn(name = "modelID")
	public ReportModel getModel() {
		return model;
	}

	public void setModel(ReportModel model) {
		this.model = model;
	}

	@Column(name = "columnName", length = 100)
	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	@Column(name = "columnAlias", length = 50)
	public String getColumnAlias() {
		return columnAlias;
	}

	public void setColumnAlias(String columnAlias) {
		this.columnAlias = columnAlias;
	}

	@Column(name = "defaultHeader", length = 30)
	public String getDefaultHeader() {
		return defaultHeader;
	}

	public void setDefaultHeader(String defaultHeader) {
		this.defaultHeader = defaultHeader;
	}

	@Column(name = "dataType", length = 20)
	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transient
	public JSONObject toJSON(boolean full) {
		JSONObject obj = super.toJSON(full);
		obj.put("columnName", columnName);
		obj.put("columnAlias", columnAlias);
		obj.put("defaultHeader", defaultHeader);
		obj.put("dataType", dataType);

		return obj;
	}

	@Override
	public void fromJSON(JSONObject obj) {
		super.fromJSON(obj);
		columnName = (String) obj.get("columnName");
		columnAlias = (String) obj.get("columnAlias");
		defaultHeader = (String) obj.get("defaultHeader");
		dataType = (String) obj.get("dataType");
	}
}
