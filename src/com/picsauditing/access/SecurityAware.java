package com.picsauditing.access;


public interface SecurityAware {
	boolean isLoggedIn(boolean requiresLogin);

	void tryPermissions(OpPerms opPerm, OpType opType) throws NoRightsException;
}
