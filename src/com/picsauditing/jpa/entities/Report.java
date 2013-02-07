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

import org.apache.commons.collections.CollectionUtils;

import com.picsauditing.report.fields.ReportField;
import com.picsauditing.report.models.ModelType;
import com.picsauditing.report.tables.FieldImportance;

@SuppressWarnings("serial")
@Entity
@Table(name = "report")
public class Report extends BaseTable {

	private ModelType modelType;
	private String name;
	private int numTimesFavorited;

	@Deprecated
	private String parameters;
	private String description;

	private String sql;
	private List<com.picsauditing.jpa.entities.Column> columns = new ArrayList<com.picsauditing.jpa.entities.Column>();
	private List<Filter> filters = new ArrayList<Filter>();
	private List<Sort> sorts = new ArrayList<Sort>();
	private String filterExpression;
	private boolean editable;
	private boolean favorite;

	private List<ReportUser> reportUsers = new ArrayList<ReportUser>();

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

	@Deprecated
	@ReportField(importance = FieldImportance.Low, width = 400)
	public String getParameters() {
		return parameters;
	}

	@Deprecated
	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	@Transient
	// @ReportField(importance = FieldImportance.Average, width = 10)
	public int getNumTimesFavorited() {
		return numTimesFavorited;
	}

	public void setNumTimesFavorited(int numTimesFavorited) {
		this.numTimesFavorited = numTimesFavorited;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Transient
	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	@OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<com.picsauditing.jpa.entities.Column> getColumns() {
		return columns;
	}

	public void setColumns(List<com.picsauditing.jpa.entities.Column> columns) {
		this.columns = columns;
	}

	public void addColumn(com.picsauditing.jpa.entities.Column column) {
		column.setReport(this);
		columns.add(column);
	}

	@OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<Filter> getFilters() {
		return filters;
	}

	public void setFilters(List<Filter> filters) {
		this.filters = filters;
	}

	public void addFilter(Filter filter) {
		filter.setReport(this);
		filters.add(filter);
	}

	@OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<Sort> getSorts() {
		return sorts;
	}

	public void setSorts(List<Sort> sorts) {
		this.sorts = sorts;
	}

	public void addSort(Sort sort) {
		sort.setReport(this);
		sorts.add(sort);
	}

	public String getFilterExpression() {
		return filterExpression;
	}

	public void setFilterExpression(String filterExpression) {
		this.filterExpression = filterExpression;
	}

	@Transient
	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	@Transient
	public boolean isFavorite() {
		return favorite;
	}

	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}

	@Override
	public String toString() {
		return name;
	}

	@Deprecated
	// TODO this should not be used here
	@OneToMany(mappedBy = "report", cascade = { CascadeType.ALL })
	public List<ReportUser> getReportUsers() {
		return reportUsers;
	}

	public void setReportUsers(List<ReportUser> reportUsers) {
		this.reportUsers = reportUsers;
	}

	@Transient
	@Deprecated
	// TODO this should not be used here
	public ReportUser getReportUser(int userId) {
		for (ReportUser reportUser : reportUsers) {
			if (userId == reportUser.getUser().getId()) {
				return reportUser;
			}
		}

		return null;
	}

	public boolean hasNoColumns() {
		return CollectionUtils.isEmpty(columns);
	}

	public boolean hasNoColumnsFiltersOrSorts() {
		return CollectionUtils.isEmpty(columns) && CollectionUtils.isEmpty(filters) && CollectionUtils.isEmpty(sorts);
	}

	public boolean hasNoModelType() {
		return modelType == null;
	}

	@Deprecated
	public boolean hasParameters() {
		return parameters != null;
	}

}