package com.picsauditing.jpa.entities;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.PermissionAware;
import com.picsauditing.access.Permissions;

import javax.persistence.Transient;

public enum RequestedDeclinedAccountStatus implements Translatable, PermissionAware {
	Requested,
	Declined;

	public boolean isRequested() {
        return this == Requested;
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
        return true;
    }
}
