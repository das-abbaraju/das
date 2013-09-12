package com.picsauditing.jpa.entities;

import javax.persistence.*;
import java.util.Date;

@SuppressWarnings("serial")
@Entity
@Table(name = "password_history")
public class PasswordHistory extends BaseTable {

	private User user;
	private String passwordHash;
	private Date endDate;

	public PasswordHistory() {
	}

	public PasswordHistory(User user, String passwordHash, Date endDate) {
		this.user = user;
		this.passwordHash = passwordHash;
		this.endDate = endDate;
	}

	@ManyToOne
	@JoinColumn(name = "userID", nullable = false, updatable = false)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
}
