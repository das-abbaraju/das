package com.picsauditing.jpa.entities;

public enum LcCorPhase {
	RemindMeLater, RemindMeLaterAudit, NoThanks, NoThanksAudit, Done;

	public boolean isAuditPhase() {
		if (this.equals(RemindMeLaterAudit) || this.equals(NoThanksAudit) || this.equals(Done)) {
			return true;
		}

		return false;
	}
}