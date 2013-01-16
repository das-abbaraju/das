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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.report.fields.ReportField;
import com.picsauditing.report.models.ModelType;
import com.picsauditing.report.tables.FieldImportance;

@SuppressWarnings("serial")
@Entity
@Table(name = "report")
//@EntityListeners(value = Report.class)
public class Report extends BaseTable {

	private ModelType modelType;
	private String name;
	private int numTimesFavorited;
	private String parameters;
	private String description;

	private String sql;
	private List<com.picsauditing.jpa.entities.Column> columns = new ArrayList<com.picsauditing.jpa.entities.Column>();
	private List<Filter> filters = new ArrayList<Filter>();
	private List<Sort> sorts = new ArrayList<Sort>();
	private String filterExpression;
	private boolean editable;
	private boolean favorite;
	
	private static final Logger logger = LoggerFactory.getLogger(Report.class);

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

	@ReportField(importance = FieldImportance.Low, width = 400)
	public String getParameters() {
		return parameters;
	}

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

	// TODO Make this transient
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

	@OneToMany(mappedBy = "report", cascade = CascadeType.ALL)
	public List<com.picsauditing.jpa.entities.Column> getColumns() {
		return columns;
	}

	public void setColumns(List<com.picsauditing.jpa.entities.Column> columns) {
		this.columns = columns;
	}

	@OneToMany(mappedBy = "report", cascade = CascadeType.ALL)
	public List<Filter> getFilters() {
		return filters;
	}

	public void setFilters(List<Filter> filters) {
		this.filters = filters;
	}

	@OneToMany(mappedBy = "report", cascade = CascadeType.ALL)
	public List<Sort> getSorts() {
		return sorts;
	}

	public void setSorts(List<Sort> sorts) {
		this.sorts = sorts;
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

	private List<ReportUser> reportUsers = new ArrayList<ReportUser>();

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
	
	/**
	 * TODO: Remove after the next release
	 * 
	 * We assume that we always overwrite the JSON String in the parameters field with the changes 
	 * made by the user in the columns/filters/sorts
	 * @throws Exception 
	 */
//	@PrePersist
//	public void convertOnSave() throws Exception {
//		FeatureToggle featureToggle = SpringUtils.getBean("FeatureToggle");
//		if (!featureToggle.isFeatureEnabled(FeatureToggle.TOGGLE_DR_STORAGE_BACKWARDS_COMPATIBILITY)) {
//			return;
//		}
//		
//		try {
//			JSONObject json = LegacyReportConverter.toJSON(this);
//			if (json != null) {
//				this.parameters = json.toString();
//			}
//		} catch (Exception e) {
//			logger.error("An error occurred while converting the report to a JSON String for report id = {}", id, e);
//			throw new Exception(e);
//		}
//	}
	
	/** 
	 * TODO: Remove after the next release
	 * 
	 * We will assume that every read from the database will involve over-writing the columns, filters
	 * and sorts from those in the JSON String.
	 * @throws Exception 
	 */
//	@PostLoad	
//	public void convertOnRead() throws Exception {
//		FeatureToggle featureToggle = SpringUtils.getBean("FeatureToggle");
//		if (!featureToggle.isFeatureEnabled(FeatureToggle.TOGGLE_DR_STORAGE_BACKWARDS_COMPATIBILITY)) {
//			return;
//		}
//		
//		try {
//			LegacyReportConverter.fillParameters(this);
//		} catch (ReportValidationException rve) {
//			logger.error("Error converting from the Legacy JSON into the report object for reportId = {}", id, rve);
//			throw new Exception(rve.getMessage());
//		}		
//	}
}