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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.picsauditing.email.SubscriptionTimePeriod;

@Entity
@Table(name = "email_subscription")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "temp")
public class EmailSubscription extends BaseTable {
	private User user;
	private String subscription;
	private SubscriptionTimePeriod timePeriod;
	private Date lastSent;
	private String permission;

	@ManyToOne
	@JoinColumn(name = "userID", nullable = false, updatable = false)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getSubscription() {
		return subscription;
	}

	public void setSubscription(String subscription) {
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

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

}
