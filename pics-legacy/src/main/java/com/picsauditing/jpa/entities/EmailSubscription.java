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
import javax.persistence.Transient;

import com.picsauditing.access.OpPerms;
import com.picsauditing.mail.Subscription;
import com.picsauditing.mail.SubscriptionTimePeriod;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.fields.ReportField;
import com.picsauditing.report.tables.FieldImportance;
import com.picsauditing.service.ReportService;

@SuppressWarnings("serial")
@Entity
@Table(name = "email_subscription")
public class EmailSubscription extends BaseTable {
	private User user;
	private Subscription subscription;
	private SubscriptionTimePeriod timePeriod = SubscriptionTimePeriod.None;
	private Date lastSent;
	private OpPerms permission;
    private Report report;

	@ManyToOne
	@JoinColumn(name = "userID", nullable = false, updatable = false)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Enumerated(EnumType.STRING)
    @ReportField(type = FieldType.Subscription, importance = FieldImportance.Required)
	public Subscription getSubscription() {
		return subscription;
	}

	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
	}

	@Enumerated(EnumType.STRING)
    @ReportField(type = FieldType.SubscriptionTimePeriod, importance = FieldImportance.Required)
	public SubscriptionTimePeriod getTimePeriod() {
		return timePeriod;
	}

	public void setTimePeriod(SubscriptionTimePeriod timePeriod) {
		this.timePeriod = timePeriod;
	}

	@Temporal(TemporalType.TIMESTAMP)
    @ReportField(type = FieldType.Date, importance = FieldImportance.Required)
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

	@Transient
	public boolean isDisabled() {
		return getTimePeriod().isNone();
	}

	@Transient
	public void enable() {
		setAuditColumns();
		setTimePeriod(getSubscription().getDefaultTimePeriod());
	}

	@Transient
	public void disable() {
		setAuditColumns();
		setTimePeriod(SubscriptionTimePeriod.None);
	}

    @ManyToOne
    @JoinColumn(name = "reportID")
    public Report getReport() {
        if (report == null) {
            report = new Report();
            report.setId(ReportService.DUMMY_REPORT_ID);
        }
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }
}
