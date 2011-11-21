package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.json.simple.JSONObject;

@SuppressWarnings("serial")
@Entity
@Table(name = "rpt_model")
public class ReportModel extends BaseTable implements JSONable {

	protected ReportModel parent;
	protected String tableName;
	protected String tableAlias;
	protected String name;
	protected String condition;

	protected List<ReportModelColumn> columns = new ArrayList<ReportModelColumn>();

	@Column(name = "parentID", length = 11)
	public ReportModel getParent() {
		return parent;
	}

	public void setParent(ReportModel parent) {
		this.parent = parent;
	}

	@Column(name = "table_name", length = 100)
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	@Column(name = "table_alias", length = 10)
	public String getTableAlias() {
		return tableAlias;
	}

	public void setTableAlias(String tableAlias) {
		this.tableAlias = tableAlias;
	}

	@Column(name = "name", length = 100)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "condition")
	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	// Child tables

	@OneToMany(mappedBy = "model")
	public List<ReportModelColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<ReportModelColumn> columns) {
		this.columns = columns;
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transient
	public JSONObject toJSON(boolean full) {
		JSONObject obj = super.toJSON(full);
		obj.put("parentID", parent);
		obj.put("name", name);
		obj.put("table_name", tableName);
		obj.put("table_alias", tableAlias);
		obj.put("condition", condition);

		return obj;
	}

	@Override
	public void fromJSON(JSONObject obj) {
		super.fromJSON(obj);
		parent = (ReportModel) obj.get("parentID");
		tableName = (String) obj.get("table_name");
		tableAlias = (String) obj.get("table_alias");
		name = (String) obj.get("name");
		condition = (String) obj.get("condition");
	}

	@Override
	public String toString() {
		return name + "(" + id + ")";
	}

	@Transient
	public String getReturnType() {
		return "report";
	}
}
