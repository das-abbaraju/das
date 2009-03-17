package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "user_switch")
public class UserSwitch extends BaseTable implements java.io.Serializable {
	private User user;
	private User switchTo;

	@ManyToOne(optional=false)
	@JoinColumn(name="userID", nullable=false)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@ManyToOne(optional=false)
	@JoinColumn(name="switchToID", nullable=false)
	public User getSwitchTo() {
		return switchTo;
	}

	public void setSwitchTo(User switchTo) {
		this.switchTo = switchTo;
	}
}
