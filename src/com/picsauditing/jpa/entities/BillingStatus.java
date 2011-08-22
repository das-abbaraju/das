package com.picsauditing.jpa.entities;

import javax.persistence.Transient;

public enum BillingStatus implements Translatable {
	Cancelled, RenewalOverdue, PastDue, Activation, Reactivation, Renewal, Upgrade, Current;

	public boolean isCurrent() {
		return this.equals(Current);
	}

	public boolean isRenewal() {
		return this.equals(Renewal);
	}

	public boolean isUpgrade() {
		return this.equals(Upgrade);
	}

	public boolean isRenewalOverdue() {
		return this.equals(RenewalOverdue);
	}

	public boolean isActivation() {
		return this.equals(Activation);
	}

	public boolean isReactivation() {
		return this.equals(Reactivation);
	}

	public boolean isPastDue() {
		return this.equals(PastDue);
	}

	public boolean isCancelled() {
		return this.equals(Cancelled);
	}

	@Transient
	@Override
	public String getI18nKey() {
		return this.getClass().getSimpleName() + "." + this.name();
	}

	@Transient
	@Override
	public String getI18nKey(String property) {
		return getI18nKey() + "." + property;
	}
}
