package com.picsauditing.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.fields.ReportField;
import com.picsauditing.report.tables.FieldImportance;

@SuppressWarnings("serial")
@Entity
@Table(name = "report_permission_user")
public class ReportPermissionUser extends BaseTable {

	private Report report;
	private User user;
	private boolean editable;

	public ReportPermissionUser() {
	}

	public ReportPermissionUser(int userId, Report report) {
		this.report = report;
		this.user = new User(userId);
		this.editable = false;
	}

	@ManyToOne
	@JoinColumn(name = "userID", nullable = false)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@ManyToOne
	@JoinColumn(name = "reportID", nullable = false)
	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	@Column(name = "editable", nullable = false)
	@ReportField(importance = FieldImportance.Required, type = FieldType.Boolean)
	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	@Transient
	public boolean isFavorite() {
		for (ReportUser reportUser : report.getReportUsers()) {
			if (reportUser.getUser().getId() == user.getId()) {
				return reportUser.isFavorite();
			}
		}
		
		return false;
	}
}
