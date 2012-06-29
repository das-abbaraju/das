package com.picsauditing.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "report_user")
public class ReportUser extends BaseTable {

	private User user;
	private Report report;
	private boolean isEditable;
	private boolean isFavorite;

	public ReportUser() {
	}

	public ReportUser(int userId, Report report) {
		this.user = new User(userId);
		this.report = report;
		this.isEditable = false;
		this.isFavorite = false;
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

	@Column(name = "is_editable", nullable = false)
	public boolean isEditable() {
		return isEditable;
	}

	public void setEditable(boolean canEdit) {
		this.isEditable = canEdit;
	}

	@Column(name = "is_favorite", nullable = false)
	public boolean isFavorite() {
		return isFavorite;
	}

	public void setFavorite(boolean favorite) {
		this.isFavorite = favorite;
	}
}
