package com.picsauditing.auditbuilder.entities;

public enum AccountStatus {
    Active,
	Pending,
	Requested,
	Demo,
	Deleted,
	Deactivated,
	Declined;

    public boolean isDeleted() {
        return this == Deleted;
    }

    public boolean isDeactivated() {
        return this == Deactivated;
    }
}