package com.picsauditing.model.user;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserGroup;
import com.picsauditing.model.usergroup.UserGroupManagementStatus;

import java.util.List;
import java.util.Map;

public interface UserManagementService {
    static final String CANNOT_DEACTIVATE_ERROR_KEY = "UsersManage.CannotInactivate";
    static final String CANNOT_DEACTIVATE_NEED_ONE_USER_WITH_PERM = "UsersManage.MustHaveOneUserWithPermission";
    static final String CANNOT_DELETE_PRIMARY_USER = "UsersManage.CannotRemovePrimary";
    static final String CANNOT_MOVE_LAST_USER = "UsersManage.CannotMoveUser";
    static final String TOO_MANY_CONTRACTOR_ADMIN_USERS = "UsersManage.1-3AdminUsers";
    static final String CONTRACTOR_USER_MUST_HAVE_AT_LEAST_ONE_PERMISSION = "UsersManage.AddPermissionToUser";
    static final String NO_USER_FOUND = "UsersManage.NoUserFound";
    static final String CANNOT_ADD_USER_TO_USER = "You can only inherit permissions from groups";
    static final String CANNOT_ADD_GROUP_TO_ITSELF = "You can't add a group to itself";
    static final String USER_ALREADY_MEMBER_OF_GROUP = "User is already a member of this group";
    static final String MUST_HAVE_ONE_USER_WITH_PERMISSION = "UsersManage.MustHaveOneUserWithPermission";

    User saveWithAuditColumnsAndRefresh(User user, Permissions permissions) throws Exception;

    void activateUser(User user) throws Exception;

    void resetUser(User user) throws Exception;

    User initializeNewUser(Account account);

    UserGroupManagementStatus userIsDeactivatable(User user, Account account);

    UserGroupManagementStatus userIsDeletable(User user);

    UserGroupManagementStatus contractorUserIsSavable(User user, Account account, List<OpPerms> permissionsBeingAdded);

    void moveUserToNewAccount(User user, int accountID) throws Exception;

    void moveUserToNewAccount(User user, Account account) throws Exception;

    UserGroupManagementStatus userIsMovable(User user);

    void deactivate(User user, Permissions permissions) throws Exception;

    void delete(User user, Permissions permissions) throws Exception;

    void unlock(User user) throws Exception;

    List<User> getAddableGroups(Permissions permissions, Account account, User user);

    UserGroup addUserToGroup(User user, User group, Permissions permissions) throws Exception;

    UserGroupManagementStatus userIsAddableToGroup(User user, User group);

    List<UserGroupManagementStatus> updateUserPermissions(User user, Account account, Permissions permissions, Map<OpPerms, Boolean> requestedPermState);
}
