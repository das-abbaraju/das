package com.picsauditing.jpa.entities;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.PermissionAware;
import com.picsauditing.access.Permissions;

import javax.persistence.Transient;

public enum AccountStatus implements Translatable, PermissionAware {
    Active(false),
	Pending(true),
	Requested(true),
	Demo(true),
	Deleted(true),
	Deactivated(true),
	Declined(true);

	private boolean enforcePermissions;

	AccountStatus(boolean enforcePermissions) {
		this.enforcePermissions = enforcePermissions;
	}

	public boolean isRequested() {
        return this == Requested;
    }

    public boolean isActive() {
        return this == Active;
    }

    public boolean isPending() {
        return this == Pending;
    }

    public boolean isDemo() {
        return this == Demo;
    }

    public boolean isDeleted() {
        return this == Deleted;
    }

    public boolean isDeactivated() {
        return this == Deactivated;
    }

    @Deprecated
    // TODO find any usages within JSPs and rename them
    public boolean isPendingDeactivated() {
        return isPendingOrDeactivated();
    }

    public boolean isPendingOrDeactivated() {
        return this == Pending || this == Deactivated;
    }

	public boolean isPendingRequestedOrDeactivated() {
		return this == Pending || this == Deactivated || this == Requested;
	}

    @Deprecated
    // TODO find any usages within JSPs and rename them
    public boolean isActiveDemo() {
        return isActiveOrDemo();
    }

    public boolean isActiveOrDemo() {
        return this == Active || this == Demo;
    }


    public boolean isDeclined() {
        return this == Declined;
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

    public boolean isVisibleTo(Permissions permissions) {
        if (enforcePermissions)
            return permissions.hasPermission(OpPerms.AllContractors);

        return true;
    }

	public boolean isActivePending() {
		return this == Active || this == Pending;
	}
	
	public boolean isActivePendingRequested() {
		return this == Active || this == Pending || this == Requested;
	}
}
