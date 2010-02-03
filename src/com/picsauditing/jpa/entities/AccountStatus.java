package com.picsauditing.jpa.entities;

public enum AccountStatus {
	Active, Pending, Demo, Deleted, Deactivated;

	public boolean isActive() {
		return this.equals(Active);
	}

	public boolean isPending() {
		return this.equals(Pending);
	}

	public boolean isDemo() {
		return this.equals(Demo);
	}

	public boolean isDeleted() {
		return this.equals(Deleted);
	}

	public boolean isDeactivated() {
		return this.equals(Deactivated);
	}

	public boolean isPendingDeactivated() {
		return this.equals(Pending) || this.equals(Deactivated);
	}

	public boolean isActiveDemo() {
		return this.equals(Active) || this.equals(Demo);
	}
}
