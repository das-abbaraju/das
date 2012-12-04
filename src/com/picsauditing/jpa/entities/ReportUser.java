package com.picsauditing.jpa.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.fields.ReportField;
import com.picsauditing.report.tables.FieldImportance;

@SuppressWarnings("serial")
@Entity
@Table(name = "report_user")
public class ReportUser extends BaseTable {

	private User user;
	private Report report;
	private boolean favorite;
	private Date lastViewedDate;
	private int viewCount;
	private int sortOrder;
	private boolean visibleOnMyReports;

	public ReportUser() {
	}

	public ReportUser(int userId, Report report) {
		this.report = report;
		this.user = new User(userId);
		this.favorite = false;
	}

	@ManyToOne
	@JoinColumn(name = "userID", nullable = false, updatable = false)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@ManyToOne
	@JoinColumn(name = "reportID", nullable = false, updatable = false)
	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	@ReportField(importance = FieldImportance.Required, type = FieldType.Boolean)
	public boolean isFavorite() {
		return favorite;
	}

	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@ReportField(importance = FieldImportance.Average, type = FieldType.Date)
	public Date getLastViewedDate() {
		return lastViewedDate;
	}

	public void setLastViewedDate(Date lastViewedDate) {
		this.lastViewedDate = lastViewedDate;
	}

	@ReportField(importance = FieldImportance.Average, type = FieldType.Integer)
	public int getViewCount() {
		return viewCount;
	}

	public void setViewCount(int viewCount) {
		this.viewCount = viewCount;
	}

	@ReportField(importance = FieldImportance.Low, type = FieldType.Integer)
	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}
	
	public boolean isVisibleOnMyReports() {
		return visibleOnMyReports;
	}

	public void setVisibleOnMyReports(boolean visibleOnMyReports) {
		this.visibleOnMyReports = visibleOnMyReports;
	}
	
	@Override
	public String toString() {
		return "{" + report.getName() + "}(" + user.getName() + ")";
	}
}