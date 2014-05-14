package com.picsauditing.access.permissions.service;

import com.picsauditing.access.permissions.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.permissions.entities.User;
import com.picsauditing.access.permissions.entities.UserAccess;
import com.picsauditing.access.permissions.entities.UserGroup;
import com.picsauditing.access.permissions.entities.YesNo;
import org.apache.commons.lang3.StringUtils;
import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public class UserService {
	public static Set<UserAccess> getPermissions(User user) {
		// Our permissions are empty, so go get some
		Set<UserAccess> permissions = new TreeSet<>();

		Logger logger = LoggerFactory.getLogger("org.perf4j.TimingLogger");
		StopWatch stopwatch = new Slf4JStopWatch(logger);

		stopwatch.start("User.getPermissions()", "userID = " + user.getId());

		if (user.getId() == User.GROUP_SU) {
			// This is the Super User Group, which should have grant ability on
			// ALL permissions
			// Also grant view/edit/delete on EditUsers
			// SuperUser group does not inherit from parent groups
			for (OpPerms accessType : OpPerms.values()) {
				UserAccess perm = new UserAccess();
				perm.setOpPerm(accessType);
				if (accessType.equals(OpPerms.EditUsers)) {
					perm.setViewFlag(true);
					perm.setEditFlag(true);
					perm.setDeleteFlag(true);
				} else {
					perm.setViewFlag(false);
					perm.setEditFlag(false);
					perm.setDeleteFlag(false);
				}
				perm.setGrantFlag(true);
				permissions.add(perm);
			}

			stopwatch.stop();
			return Collections.unmodifiableSet(permissions);
		}

		// get all the groups this user (or group) is a part of
		for (UserGroup userGroup : user.getGroups()) {
			if (userGroup.getGroup().getIsGroup() == YesNo.Yes) {
				Set<UserAccess> tempPerms = UserService.getPermissions(userGroup.getGroup());
				for (UserAccess perm : tempPerms) {
					add(permissions, perm, false);
				}
			}
		}

		// READ the permissions assigned directly to this THIS user/group
		for (UserAccess perm : user.getOwnedPermissions()) {
			if (perm != null) {
				add(permissions, perm, true);
			}
		}

		stopwatch.stop();
        return Collections.unmodifiableSet(permissions);

    }

	/**
	 * @param permissions
	 *            The new set of permission for this user (transient version of
	 *            user.permissions)
	 * @param connectPerm
	 *            The actual UserAccess object owned by either the current user
	 *            or one of its parent groups.
	 * @param overrideBoth
	 *            True if perm is from "this", false if perm is from a parent
	 */
    private static void add(Set<UserAccess> permissions, UserAccess connectPerm, boolean overrideBoth) {
		if (connectPerm == null || connectPerm.getOpPerm() == null) {
			return;
		}

		// Create a disconnected copy of UserAccess (perm)
		UserAccess perm = new UserAccess(connectPerm);

		for (UserAccess origPerm : permissions) {
			if (origPerm.getOpPerm().equals(perm.getOpPerm())) {
				if (overrideBoth) {
					// Override the previous settings, regardless if Granting or
					// Revoking
					if (perm.getViewFlag() != null) {
						origPerm.setViewFlag(perm.getViewFlag());
					}
					if (perm.getEditFlag() != null) {
						origPerm.setEditFlag(perm.getEditFlag());
					}
					if (perm.getDeleteFlag() != null) {
						origPerm.setDeleteFlag(perm.getDeleteFlag());
					}
					if (perm.getGrantFlag() != null) {
						origPerm.setGrantFlag(perm.getGrantFlag());
					}
				} else {
					// Optimistic Granting
					// if the user has two groups with the same perm type,
					// and one grants but the other revokes, then the users WILL
					// be granted the right
					if (perm.getViewFlag() != null && perm.getViewFlag()) {
						origPerm.setViewFlag(true);
					}
					if (perm.getEditFlag() != null && perm.getEditFlag()) {
						origPerm.setEditFlag(true);
					}
					if (perm.getDeleteFlag() != null && perm.getDeleteFlag()) {
						origPerm.setDeleteFlag(true);
					}
					if (perm.getGrantFlag() != null && perm.getGrantFlag()) {
						origPerm.setGrantFlag(true);
					}
				}
				return;
			}
		}

		// add the parent group's permissions to the user's permissions
		permissions.add(perm);
		return;
	}

    public static boolean hasPermission(User user, OpPerms opPerm) {
		return hasPermission(user, opPerm, OpType.View);
	}

	public static boolean hasPermission(User user, OpPerms opPerm, OpType oType) {
		for (UserAccess perm : UserService.getPermissions(user)) {
			if (opPerm.isForContractor() && AccountService.isContractor(user.getAccount()) && perm.getOpPerm() == OpPerms.ContractorAdmin) {
				return true;
			}
			if (opPerm == perm.getOpPerm()) {
				if (oType == OpType.Edit) {
					return perm.getEditFlag();
				} else if (oType == OpType.Delete) {
					return perm.getDeleteFlag();
				} else if (oType == OpType.Grant) {
					return perm.getGrantFlag();
				}
				// Default to OpType.View
                return UserService.falseIfNull(perm.getViewFlag());
			}
		}
		return false;
	}

    private static boolean falseIfNull(Boolean viewFlag) {
        return viewFlag == null ? false : viewFlag;
    }

	/**
	 * In UsersManage, another user (non-group) is inserted into this user's
	 * groups for shadowing
	 *
	 * @return shadowed user or null
	 */
	public static User getShadowedUser(User user) {
		// Pull up any non-group this user is shadowing
		for (UserGroup ug : user.getGroups()) {
			if (ug.getGroup().getIsGroup() != YesNo.Yes) {
				return ug.getGroup();
			}
		}

		return null;
	}

    public static String getUsername(User user) {
        return user.getAppUser().getUsername();
    }

    public static void setUsername(User user, String username) {
        if (StringUtils.isNotEmpty(username)) {
            username = username.trim();
        }
        user.getAppUser().setUsername(username);
    }

}
