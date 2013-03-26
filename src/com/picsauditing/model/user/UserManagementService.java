package com.picsauditing.model.user;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.model.usergroup.UserGroupManagementStatus;

import java.util.List;

public interface UserManagementService {
    static final String CANNOT_DEACTIVATE_ERROR_KEY = "UsersManage.CannotInactivate";

    static final String CANNOT_DEACTIVATE_NEED_ONE_USER_WITH_PERM = "UsersManage.MustHaveOneUserWithPermission";

    static final String CANNOT_DELETE_PRIMARY_USER = "UsersManage.CannotRemovePrimary";

    static final String CANNOT_MOVE_LAST_USER = "UsersManage.CannotMoveUser";

    static final String TOO_MANY_CONTRACTOR_ADMIN_USERS = "UsersManage.1-3AdminUsers";

    static final String CONTRACTOR_USER_MUST_HAVE_AT_LEAST_ONE_PERMISSION = "UsersManage.AddPermissionToUser";

    static final String NO_USER_FOUND = "UsersManage.NoUserFound";

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

    void deactivate(User user) throws Exception;

    void delete(User user) throws Exception;

    void unlock(User user) throws Exception;
}
