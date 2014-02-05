package com.picsauditing.jpa.entities;

import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.fields.ReportField;
import com.picsauditing.report.models.ModelType;
import com.picsauditing.report.tables.FieldImportance;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.persistence.Column;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@SuppressWarnings("serial")
@Entity
@Table(name = "report")
@SQLDelete(sql = "UPDATE report SET deleted = 1 WHERE id = ?")
@Where(clause = "deleted = 0")
public class Report extends BaseTable {

	private ModelType modelType;
	private User owner;
	private String name;
	private String description;
	private String filterExpression;
	private String sql;
	private boolean deleted;
	private boolean isPublic;
    private boolean groupBy;

	private List<com.picsauditing.jpa.entities.Column> columns = new ArrayList<com.picsauditing.jpa.entities.Column>();
	private List<Filter> filters = new ArrayList<Filter>();
	private List<Sort> sorts = new ArrayList<Sort>();

	private List<ReportPermissionUser> reportPermissionUsers = new ArrayList<ReportPermissionUser>();
    private List<ReportPermissionAccount> reportPermissionAccounts = new ArrayList<ReportPermissionAccount>();
	private List<ReportUser> reportUsers = new ArrayList<ReportUser>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ownerID", nullable = false)
	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

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

    @ReportField
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

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

    @ReportField
    public String getFilterExpression() {
        return filterExpression;
    }

    public void setFilterExpression(String filterExpression) {
        this.filterExpression = filterExpression;
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

	@Override
	public String toString() {
		return name;
	}

	@OneToMany(mappedBy = "report", cascade = CascadeType.ALL)
	public List<ReportUser> getReportUsers() {
		return reportUsers;
	}

	public void setReportUsers(List<ReportUser> reportUsers) {
		this.reportUsers = reportUsers;
	}

	public boolean hasNoColumns() {
		return CollectionUtils.isEmpty(columns);
	}

	public boolean hasNoModelType() {
		return modelType == null;
	}

	public boolean hasNoOwner() {
		return owner == null || owner.getId() == 0;
	}

	@OneToMany(mappedBy = "report", cascade = CascadeType.ALL)
	public List<ReportPermissionUser> getReportPermissionUsers() {
		return reportPermissionUsers;
	}

	public void setReportPermissionUsers(List<ReportPermissionUser> reportPermissionUsers) {
		this.reportPermissionUsers = reportPermissionUsers;
	}

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL)
    public List<ReportPermissionAccount> getReportPermissionAccounts() {
        return reportPermissionAccounts;
    }

    public void setReportPermissionAccounts(List<ReportPermissionAccount> reportPermissionAccounts) {
        this.reportPermissionAccounts = reportPermissionAccounts;
    }

    public void sortColumns() {
		Collections.sort(columns, new Comparator<com.picsauditing.jpa.entities.Column>() {
			@Override
			public int compare(com.picsauditing.jpa.entities.Column c1, com.picsauditing.jpa.entities.Column c2) {
				if (c1.getSortIndex() == c2.getSortIndex()) {
					return c1.getId() - c2.getId();
				}

				return c1.getSortIndex() - c2.getSortIndex();
			}
		});
	}

    @ReportField(type = FieldType.Boolean)
    public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

    @Transient
    public boolean hasGroupBy() {
        return groupBy;
    }

    public void setGroupBy(boolean groupBy) {
        this.groupBy = groupBy;
    }
}