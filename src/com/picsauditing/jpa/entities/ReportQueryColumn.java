package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.json.simple.JSONObject;

@SuppressWarnings("serial")
@Entity
@Table(name = "rpt_query_column")
public class ReportQueryColumn extends BaseTable implements Comparable<ReportQueryColumn>, JSONable {

	protected ReportQuery query;
	protected ReportModelColumn modelColumn;
	protected String options;
	protected int groupBy;
	protected int orderBy;
	protected boolean orderByDesc;

	@ManyToOne
	@JoinColumn(name = "queryID")
	public ReportQuery getQuery() {
		return query;
	}

	public void setQuery(ReportQuery query) {
		this.query = query;
	}

	@ManyToOne
	@JoinColumn(name = "modelColumnID")
	public ReportModelColumn getModelColumn() {
		return modelColumn;
	}

	public void setModelColumn(ReportModelColumn modelColumn) {
		this.modelColumn = modelColumn;
	}

	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
	}

	public int getGroupBy() {
		return groupBy;
	}

	public void setGroupBy(int groupBy) {
		this.groupBy = groupBy;
	}

	public int getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(int orderBy) {
		this.orderBy = orderBy;
	}

	public boolean isOrderByDesc() {
		return orderByDesc;
	}

	public void setOrderByDesc(boolean orderByDesc) {
		this.orderByDesc = orderByDesc;
	}


	@Override
	@SuppressWarnings("unchecked")
	@Transient
	public JSONObject toJSON(boolean full) {
		JSONObject obj = super.toJSON(full);
		obj.put("query", query);
		obj.put("modelColumn", modelColumn);
		obj.put("options", options);
		obj.put("groupBy", groupBy);
		obj.put("orderBy", orderBy);
		obj.put("orderByDesc", orderByDesc);

		return obj;
	}

	@Override
	public void fromJSON(JSONObject obj) {
		super.fromJSON(obj);
		query = (ReportQuery) obj.get("query");
		modelColumn = (ReportModelColumn) obj.get("modelColumn");
		options = (String) obj.get("options");
		groupBy = (Integer) obj.get("groupBy");
		orderBy = (Integer) obj.get("orderBy");
		orderByDesc = (Boolean) obj.get("orderByDesc");
	}

	@Override
	public int compareTo(ReportQueryColumn o) {
		if (o == null) {
			return 1;
		}

		int cmp = new Integer(getOrderBy()).compareTo(new Integer(o.getOrderBy()));

		if (cmp != 0)
			return cmp;
		
		return new Integer(getGroupBy()).compareTo(new Integer(o.getGroupBy()));
	}
}
