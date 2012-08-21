package com.picsauditing.jpa.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@SuppressWarnings("serial")
@Entity
@Table(name = "report_user")
public class ReportUser extends BaseTable {

	private User user;
	private Report report;
	private boolean editable;
	private boolean favorite;
	private Date lastOpened;
	private int favoriteSortIndex;

	public ReportUser() {
	}

	public ReportUser(int userId, Report report) {
		this.user = new User(userId);
		this.report = report;
		this.editable = false;
		this.favorite = false;
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
	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	@Column(name = "favorite", nullable = false)
	public boolean isFavorite() {
		return favorite;
	}

	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getLastOpened() {
		return lastOpened;
	}

	public void setLastOpened(Date lastOpened) {
		this.lastOpened = lastOpened;
	}

	@Column(nullable = false)
	public int getFavoriteSortIndex() {
		return favoriteSortIndex;
	}

	public void setFavoriteSortIndex(int favoriteSortIndex) {
		this.favoriteSortIndex = favoriteSortIndex;
	}
}
