package com.picsauditing.flagcalculator.entities;

public enum AccountStatus {
    Active,
    Pending,
    Requested,
    Demo,
    Deleted,
    Deactivated,
    Declined;

    public boolean isPending() {
        return this == Pending;
    }

    public boolean isDeleted() {
        return this == Deleted;
    }

    public boolean isDeactivated() {
        return this == Deactivated;
    }

    public boolean isDeclined() {
        return this == Declined;
    }
}