package com.picsauditing.jpa.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.picsauditing.actions.users.UserAccountRole;

@Entity
@Table(name = "account_user")
public class AccountUser extends BaseTable {
	private Account account;
	private User user;
	private UserAccountRole role;
	private Date startDate;
	private Date endDate;
	private int ownerPercent = 100;

	@ManyToOne
	@JoinColumn(name = "accountID", nullable = false, updatable = false)
	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	@ManyToOne
	@JoinColumn(name = "userID", nullable = false, updatable = false)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Enumerated(EnumType.STRING)
	public UserAccountRole getRole() {
		return role;
	}

	public void setRole(UserAccountRole role) {
		this.role = role;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public int getOwnerPercent() {
		return ownerPercent;
	}

	public void setOwnerPercent(int ownerPercent) {
		this.ownerPercent = ownerPercent;
	}
	
	@Transient
	public boolean isCurrent() {
		Date now = new Date();
		if (startDate != null && startDate.after(now)) {
			// This hasn't started yet
			return false;
		}
		if (endDate != null && endDate.before(now)) {
			// This already ended
			return false;
		}
		return true;
	}
}
