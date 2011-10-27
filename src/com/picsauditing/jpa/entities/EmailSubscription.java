package com.picsauditing.jpa.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.picsauditing.access.OpPerms;
import com.picsauditing.mail.Subscription;
import com.picsauditing.mail.SubscriptionTimePeriod;

@SuppressWarnings("serial")
@Entity
@Table(name = "email_subscription")
public class EmailSubscription extends BaseTable {
	private User user;
	private Subscription subscription;
	private SubscriptionTimePeriod timePeriod = SubscriptionTimePeriod.None;
	private Date lastSent;
	private OpPerms permission;

	@ManyToOne
	@JoinColumn(name = "userID", nullable = false, updatable = false)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Enumerated(EnumType.STRING)
	public Subscription getSubscription() {
		return subscription;
	}

	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
	}

	@Enumerated(EnumType.STRING)
	public SubscriptionTimePeriod getTimePeriod() {
		return timePeriod;
	}

	public void setTimePeriod(SubscriptionTimePeriod timePeriod) {
		this.timePeriod = timePeriod;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getLastSent() {
		return lastSent;
	}

	public void setLastSent(Date lastSent) {
		this.lastSent = lastSent;
	}

	@Enumerated(EnumType.STRING)
	public OpPerms getPermission() {
		return permission;
	}

	public void setPermission(OpPerms permission) {
		this.permission = permission;
	}

}
