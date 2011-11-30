package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.json.simple.JSONObject;

@SuppressWarnings("serial")
@Entity
@Table(name = "rpt_query")
public class ReportQuery extends BaseTable implements JSONable {

	protected ReportModel model;
	protected String name;
	protected int pageLimit;
	protected int reportLimit;
	protected String condition;

	protected List<ReportQueryColumn> columns = new ArrayList<ReportQueryColumn>();
	
	@ManyToOne
	@JoinColumn(name = "modelID")
	public ReportModel getModel() {
		return model;
	}

	public void setModel(ReportModel model) {
		this.model = model;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPageLimit() {
		return pageLimit;
	}

	public void setPageLimit(int pageLimit) {
		this.pageLimit = pageLimit;
	}

	public int getReportLimit() {
		return reportLimit;
	}

	public void setReportLimit(int reportLimit) {
		this.reportLimit = reportLimit;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	// Child tables

	@OneToMany(mappedBy = "query")
	public List<ReportQueryColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<ReportQueryColumn> columns) {
		this.columns = columns;
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transient
	public JSONObject toJSON(boolean full) {
		JSONObject obj = super.toJSON(full);
		obj.put("model", model);
		obj.put("name", name);
		obj.put("pageLimit", pageLimit);
		obj.put("table_alias", reportLimit);
		obj.put("condition", condition);

		return obj;
	}

	@Override
	public void fromJSON(JSONObject obj) {
		super.fromJSON(obj);
		model = (ReportModel) obj.get("model");
		name = (String) obj.get("name");
		pageLimit = (Integer) obj.get("pageLimit");
		reportLimit = (Integer) obj.get("reportLimit");
		condition = (String) obj.get("condition");
	}

	@Override
	public String toString() {
		return name + "(" + id + ")";
	}
}
