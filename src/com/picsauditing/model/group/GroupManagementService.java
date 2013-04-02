package com.picsauditing.model.group;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.model.usergroup.UserGroupManagementStatus;

public interface GroupManagementService {
    final String GROUPS_CANNOT_BE_MOVED = "UsersManage.GroupsCannotBeMoved";

    User saveWithAuditColumnsAndRefresh(User user, Permissions permissions) throws Exception;

    void setUsernameToGeneratedGroupname(User user);

    void resetGroup(User user) throws Exception;

    User initializeNewGroup(Account account);

    UserGroupManagementStatus groupIsDeactivatable(User user);

    UserGroupManagementStatus groupIsDeletable(User user);

    UserGroupManagementStatus groupIsMovable(User user);

    void deactivate(User user) throws Exception;

    void delete(User user) throws Exception;

    boolean isGroupnameAvailable(User user);
}
