package com.picsauditing.jpa.entities;

import javax.persistence.Transient;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.PermissionAware;
import com.picsauditing.access.Permissions;

public enum AccountStatus implements Translatable, PermissionAware {
	Active, Pending, Requested, Demo, Deleted, Deactivated;

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
		if (this == Deleted || this == Demo)
			return permissions.hasPermission(OpPerms.AllContractors);
		
		return true;
	}
}
