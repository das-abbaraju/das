package com.picsauditing.access;

public interface PermissionAware {

	boolean isVisibleTo(Permissions permissions);

}
