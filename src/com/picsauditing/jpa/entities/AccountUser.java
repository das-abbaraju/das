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
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.fields.ReportField;
import com.picsauditing.report.tables.FieldCategory;
import com.picsauditing.report.tables.FieldImportance;

@SuppressWarnings("serial")
@Entity
@Table(name = "account_user")
/**
 * The name implies that this generically shows a relationship between an account and a user.  But, in practice,
 * this object is only used to show the relationship between an Operator account, and either an Account Manager or a
 * Sales Representative, i.e. a person who is representing the account currently, or did at some point in the past.
 */
public class AccountUser extends BaseTable {
	private Account account;
	private User user;
	private UserAccountRole role;
	private Date startDate;
	private Date endDate;
	private int ownerPercent = 100;
	private String serviceLevel;

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
	@ReportField(type = FieldType.AccountUser, category = FieldCategory.Commission, importance = FieldImportance.Average)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Enumerated(EnumType.STRING)
	@ReportField(type = FieldType.UserAccountRole, category = FieldCategory.Commission, importance = FieldImportance.Average)
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

	public String getServiceLevel() {
		return serviceLevel;
	}

	public void setServiceLevel(String serviceLevel) {
		this.serviceLevel = serviceLevel;
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

    @Override
    public Object clone() {
        AccountUser accountUser = new AccountUser();
        // purposefully NOT copying the ID across
        accountUser.setUser(this.getUser());
        accountUser.setOwnerPercent(this.getOwnerPercent());
        accountUser.setRole(this.getRole());
        accountUser.setStartDate(this.getStartDate());
        accountUser.setEndDate(this.getEndDate());
        accountUser.setAccount(this.getAccount());
        return accountUser;
    }

    @Override
    public boolean equals(Object that) {
        if (!(that instanceof AccountUser)) {
			return false;
		}

        AccountUser accountUser = (AccountUser) that;

        // See if we are missing key information that prevents from even knowing what this object represents,
        // much less that the two objects represent the same thing.
        if (accountUser.getAccount() == null || this.getAccount() == null || accountUser.getUser() == null || this
                .getUser() == null ) {
			return false;
		}

        // Note: We are purposefully NOT caring if the ID is the same or not
        if (this.getAccount().getId() != accountUser.getAccount().getId()) {
			return false;
		}

        if (this.getUser().getId() != accountUser.getUser().getId()) {
			return false;
		}

        if (this.getRole() != accountUser.getRole()) {
			return false;
		}

        if (this.getStartDate() != accountUser.getStartDate()) {
			return false;
		}

        if (this.getEndDate() != accountUser.getEndDate()) {
			return false;
		}

        if (this.getOwnerPercent() != accountUser.getOwnerPercent()) {
			return false;
		}

        return true;
    }
}
