package com.picsauditing.flagcalculator.entities;

public enum AccountStatus {
    Active/*(false)*/,
    Pending/*(true)*/,
    Requested/*(true)*/,
    Demo/*(true)*/,
    Deleted/*(true)*/,
    Deactivated/*(true)*/,
    Declined/*(true)*/;

//    private boolean enforcePermissions;
//
//    AccountStatus(boolean enforcePermissions) {
//        this.enforcePermissions = enforcePermissions;
//    }
//
//    public boolean isRequested() {
//        return this == Requested;
//    }
//
//    public boolean isActive() {
//        return this == Active;
//    }
//
//    public boolean isPending() {
//        return this == Pending;
//    }
//
//    public boolean isDemo() {
//        return this == Demo;
//    }
//
//    public boolean isDeleted() {
//        return this == Deleted;
//    }
//
//    public boolean isDeactivated() {
//        return this == Deactivated;
//    }
//
//    public boolean isDeclinedDeletedDeactivated() {
//        return this == Deleted || this == Deactivated || this == Declined;
//    }
//
//    @Deprecated
//    // TODO find any usages within JSPs and rename them
//    public boolean isPendingDeactivated() {
//        return isPendingOrDeactivated();
//    }
//
//    public boolean isPendingOrDeactivated() {
//        return this == Pending || this == Deactivated;
//    }
//
//    public boolean isPendingRequestedOrDeactivated() {
//        return this == Pending || this == Deactivated || this == Requested;
//    }
//
//    public boolean isPendingRequestedDeclinedOrDeactivated() {
//        return this == Pending || this == Deactivated || this == Requested || this == Declined;
//    }
//
//    public boolean isPendingDeclinedOrDemo() {
//        return this == Pending || this == Declined || this == Demo;
//    }
//
//    @Deprecated
//    // TODO find any usages within JSPs and rename them
//    public boolean isActiveDemo() {
//        return isActiveOrDemo();
//    }
//
//    public boolean isActiveOrDemo() {
//        return this == Active || this == Demo;
//    }
//
//
//    public boolean isDeclined() {
//        return this == Declined;
//    }
//
//    @Transient
//    @Override
//    public String getI18nKey() {
//        return this.getClass().getSimpleName() + "." + this.name();
//    }
//
//    @Transient
//    @Override
//    public String getI18nKey(String property) {
//        return getI18nKey() + "." + property;
//    }
//
//    public boolean isVisibleTo(Permissions permissions) {
//        if (enforcePermissions)
//            return permissions.hasPermission(OpPerms.AllContractors);
//
//        return true;
//    }
//
//    public boolean isActivePending() {
//        return this == Active || this == Pending;
//    }
//
//    public boolean isActiveDemoPending() {
//        return this == Active || this == Pending || this == Demo;
//    }
//
//    public boolean isActivePendingRequested() {
//        return this == Active || this == Pending || this == Requested;
//    }
//
//    public boolean allowRegistrationToBeMarkedDeclined() {
//        return this == Pending || this == Requested;
//    }
//
//    public boolean allowRegistrationToBeMarkedDuplicated() {
//        return this == Pending || this == Requested;
//    }
}
