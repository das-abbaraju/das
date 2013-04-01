package com.picsauditing.model.group;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.model.usergroup.UserGroupManagementStatus;
import com.picsauditing.model.usergroup.UserGroupManager;

public class GroupManager extends UserGroupManager implements GroupManagementService {

    @Override
    public User saveWithAuditColumnsAndRefresh(User user, Permissions permissions) throws Exception {
        return super.saveWithAuditColumnsAndRefresh(user, permissions);
    }

    @Override
    public boolean isGroupnameAvailable(User user) {
        if (userDAO.duplicateUsername(generatedGroupname(user), user.getId())) {
            return false;
        }
        return true;
    }

    @Override
    public void setUsernameToGeneratedGroupname(User user) {
        user.setUsername(generatedGroupname(user));
    }

    private String generatedGroupname(User user) {
        return new StringBuffer("GROUP")
                    .append(user.getAccount().getId())
                    .append(user.getName()).toString();
    }

    @Override
    public void resetGroup(User user) throws Exception {
        resetUserOrGroup(user);
    }

    @Override
    public User initializeNewGroup(Account account) {
        User user = initializeNewUserOrGroup(account);
        user.setIsGroup(YesNo.valueOf(true));
        return user;
    }

    @Override
    public UserGroupManagementStatus groupIsDeactivatable(User user) {
        return new UserGroupManagementStatus();
    }

    @Override
    public UserGroupManagementStatus groupIsMovable(User user) {
        UserGroupManagementStatus status = new UserGroupManagementStatus();
        status.isOk = false;
        status.notOkErrorKey = GROUPS_CANNOT_BE_MOVED;
        return status;
    }


    @Override
    public void deactivate(User user) throws Exception {
        super.deactivate(user);
    }

    @Override
    public void delete(User user) throws Exception {
        super.delete(user);
    }

    @Override
    public UserGroupManagementStatus groupIsDeletable(User user) {
        return new UserGroupManagementStatus();
    }
}