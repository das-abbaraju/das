package com.picsauditing.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "report_user_report")
public class ReportUserReport extends BaseTable {

	private User user;
	private Report report;
	private boolean canEdit;
	private boolean favorite;

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

	@Column(nullable = false)
	public boolean isCanEdit() {
		return canEdit;
	}

	public void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
	}

	@Column(nullable = false)
	public boolean isFavorite() {
		return favorite;
	}

	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}
}