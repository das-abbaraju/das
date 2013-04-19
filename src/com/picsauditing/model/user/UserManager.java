package com.picsauditing.model.user;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.UserAccessDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.dao.UserGroupDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.model.usergroup.UserGroupManagementStatus;
import com.picsauditing.model.usergroup.UserGroupManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class UserManager extends UserGroupManager implements UserManagementService {
    private final Logger logger = LoggerFactory.getLogger(UserManager.class);

    @Autowired
    protected UserAccessDAO userAccessDAO;
    @Autowired
    protected UserGroupDAO userGroupDAO;
    @Autowired
    private AccountDAO accountDAO;

    @Override
    public User saveWithAuditColumnsAndRefresh(User user, Permissions permissions) throws Exception {
        return super.saveWithAuditColumnsAndRefresh(user, permissions);
    }

    @Override
    public UserGroupManagementStatus userIsMovable(User user) {
        UserGroupManagementStatus status = new UserGroupManagementStatus();
        if (user == null) {
            status.isOk = false;
            status.notOkErrorKey = NO_USER_FOUND;
        } else if (!usersAccountHasMoreThanOneUser(user)) {
            status.isOk = false;
            status.notOkErrorKey = CANNOT_MOVE_LAST_USER;
        }
        return status;
    }

    private boolean usersAccountHasMoreThanOneUser(User user) {
        if (user.getAccount() == null || user.getAccount().getUsers() == null) {
            return false;
        } else {
            return user.getAccount().getUsers().size() > 1;
        }
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
    public void unlock(User user) throws Exception {
        userDAO.refresh(user);
        user.setLockUntil(null);
        userDAO.save(user);
    }

    @Override
    public UserGroupManagementStatus userIsDeletable(User user) {
        UserGroupManagementStatus status = new UserGroupManagementStatus();
        if (user == null) {
            status.isOk = false;
            status.notOkErrorKey = NO_USER_FOUND;
        } else if (user.equals(user.getAccount().getPrimaryContact())) {
            status.isOk = false;
            status.notOkErrorKey = CANNOT_DELETE_PRIMARY_USER;
            status.errorDetail = user.getAccount().getName();
        }
        return status;
    }

    @Override
    public UserGroupManagementStatus contractorUserIsSavable(User user, Account account, List<OpPerms> permissionsBeingAdded) {
        UserGroupManagementStatus status = new UserGroupManagementStatus();
        if (user.getAccount().isContractor()) {
            if (notAllowedToAddAnotherAdmin(account, permissionsBeingAdded.contains(OpPerms.ContractorAdmin), user.getOwnedOpPerms())) {
                status.isOk = false;
                status.notOkErrorKey = TOO_MANY_CONTRACTOR_ADMIN_USERS;
                status.errorDetail = OpPerms.ContractorAdmin.getDescription();
            } else if (permissionsBeingAdded.size() == 0 && user.isActiveB()) {
                status.isOk = false;
                status.notOkErrorKey = CONTRACTOR_USER_MUST_HAVE_AT_LEAST_ONE_PERMISSION;
            }
        }
        return status;
    }

    private boolean notAllowedToAddAnotherAdmin(Account account, boolean addingContractorAdmin, Set<OpPerms> userPerms) {
        return !userPerms.contains(OpPerms.ContractorAdmin) && addingContractorAdmin && account.getUsersByRole(OpPerms.ContractorAdmin).size() >= 3;
    }

    @Override
    public void resetUser(User user) throws Exception {
        resetUserOrGroup(user);
    }

    @Override
    public void activateUser(User user) throws Exception {
        userDAO.refresh(user);
        user.setActive(true);
        userDAO.save(user);
    }

    @Override
    public User initializeNewUser(Account account) {
        User user = initializeNewUserOrGroup(account);
        user.setIsGroup(YesNo.valueOf(false));
        return user;
    }

    @Override
    public void moveUserToNewAccount(User user, int accountID) throws Exception {
        moveUserToNewAccount(user, accountDAO.find(accountID));
    }

    @Override
    public void moveUserToNewAccount(User user, Account account) throws Exception {
        removeAllUserOpPerms(user);
        removeAllGroupPerms(user);
        doMoveToNewAccount(user, account);
    }

    private void doMoveToNewAccount(User user, Account account) {
        user.setAccount(account);
        userDAO.save(user);
    }

    private void removeAllGroupPerms(User user) {
        List<UserGroup> userGroupList = userGroupDAO.findByUser(user.getId());
        Iterator<UserGroup> ugIter = userGroupList.iterator();
        while (ugIter.hasNext()) {
            UserGroup next = ugIter.next();
            user.getGroups().remove(next);
            ugIter.remove();
            userAccessDAO.remove(next);
        }
    }

    private void removeAllUserOpPerms(User user) {
        List<UserAccess> userAccessList = userAccessDAO.findByUser(user.getId());
        Iterator<UserAccess> uaIter = userAccessList.iterator();
        while (uaIter.hasNext()) {
            UserAccess next = uaIter.next();
            user.getOwnedPermissions().remove(next);
            uaIter.remove();
            userAccessList.remove(next);
            userAccessDAO.remove(next);
        }
    }

    @Override
    public UserGroupManagementStatus userIsDeactivatable(User user, Account account) {
        UserGroupManagementStatus status = new UserGroupManagementStatus();
        if (user.equals(user.getAccount().getPrimaryContact())) {
            status.isOk = false;
            status.notOkErrorKey = CANNOT_DEACTIVATE_ERROR_KEY;
            status.errorDetail = user.getAccount().getName();
        } else if (user.getAccount().isContractor()) {
            Set<OpPerms> userPerms = user.getOwnedOpPerms();
            if (userPerms.contains(OpPerms.ContractorAdmin) && account.getUsersByRole(OpPerms.ContractorAdmin).size() < 2) {
                status.isOk = false;
                status.notOkErrorKey = CANNOT_DEACTIVATE_NEED_ONE_USER_WITH_PERM;
                status.errorDetail = OpPerms.ContractorAdmin.getDescription();
            } else if (userPerms.contains(OpPerms.ContractorBilling) && account.getUsersByRole(OpPerms.ContractorBilling).size() < 2) {
                status.isOk = false;
                status.notOkErrorKey = CANNOT_DEACTIVATE_NEED_ONE_USER_WITH_PERM;
                status.errorDetail = OpPerms.ContractorBilling.getDescription();
            } else if (userPerms.contains(OpPerms.ContractorSafety) && account.getUsersByRole(OpPerms.ContractorSafety).size() < 2) {
                status.isOk = false;
                status.notOkErrorKey = CANNOT_DEACTIVATE_NEED_ONE_USER_WITH_PERM;
                status.errorDetail = OpPerms.ContractorSafety.getDescription();
            } else if (userPerms.contains(OpPerms.ContractorInsurance) && account.getUsersByRole(OpPerms.ContractorInsurance).size() < 2) {
                status.isOk = false;
                status.notOkErrorKey = CANNOT_DEACTIVATE_NEED_ONE_USER_WITH_PERM;
                status.errorDetail = OpPerms.ContractorInsurance.getDescription();
            }
        }
        return status;
    }

    @Override
    public List<User> getAddableGroups(Permissions permissions, Account account, User user) {
        List<User> list = new ArrayList<>();
        if (!permissions.hasPermission(OpPerms.EditUsers, OpType.Edit)) {
            return list;
        }

        list = userDAO.findByAccountID(account.getId(), "Yes", "Yes");

        try {
            if (user.isGroup() && permissions.hasPermission(OpPerms.AllOperators)
                    && permissions.getAccountId() != account.getId()) {
                List<User> ownedByPicsForOperatorsCorpAndContractors = userDAO.findByAccountID(Account.PicsID, "Yes", "Yes");
                for (User group : ownedByPicsForOperatorsCorpAndContractors) {
                    if (!group.getName().startsWith("PICS") && !list.contains(group)) {
                        list.add(group);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());

        }

        try {
            for (UserGroup userGroup : user.getGroups()) {
                // but these groups, have already been added
                list.remove(userGroup.getGroup());
            }
        } catch (Exception e) {
            logger.error("test 2 {}", e.getMessage());
        }
        list.remove(user);
        return list;
    }

    @Override
    public UserGroup addUserToGroup(User user, User group, Permissions permissions) throws Exception {
        UserGroup userGroup = new UserGroup();
        userGroup.setUser(user);
        userGroup.setGroup(group);
        userGroup.setAuditColumns(permissions);
        userGroupDAO.save(userGroup);
        return userGroup;
    }

    @Override
    public UserGroupManagementStatus userIsAddableToGroup(User user, User group) {
        UserGroupManagementStatus status = new UserGroupManagementStatus();
        if (!group.isGroup()) {
            status.isOk = false;
            status.notOkErrorKey = CANNOT_ADD_USER_TO_USER;
        }

        if (user.equals(group)) {
            status.isOk = false;
            status.notOkErrorKey = CANNOT_ADD_GROUP_TO_ITSELF;
        }

        for (UserGroup userGroup : user.getGroups()) {
            if (userGroup.getGroup().equals(group)) {
                // Don't add the same group twice
                status.isOk = false;
                status.notOkErrorKey = USER_ALREADY_MEMBER_OF_GROUP;
                return status;
            }
        }

        if (user.isGroup()) {
            // Make sure the new parent group isn't a descendant of this child group
            if (containsMember(user, group)) {
                status.isOk = false;
                status.notOkErrorKey = group.getName() + " is a descendant of " + user.getName()
                        + ". This action would create an infinite loop.";
            }
        }
        return status;
    }

    private boolean containsMember(User user, User group) {
        for (UserGroup userMember : user.getMembers()) {
            if (userMember.getUser().equals(group)) {
                return true;
            }
            if (containsMember(userMember.getUser(), group)) {
                return true;
            }
        }
        return false;
    }

    public List<UserGroupManagementStatus> updateUserPermissions(User user, Account account, Permissions permissions, Map<OpPerms, Boolean> requestedPermState) {
        List<UserGroupManagementStatus> statuses = new ArrayList<>();
        Set<OpPerms> userPerms = user.getOwnedOpPerms();

        for (OpPerms opPerm : requestedPermState.keySet()) {
            if (!userPerms.contains(opPerm) && requestedPermState.get(opPerm)) {
                user.addOwnedPermissions(opPerm, permissions.getUserId());
            } else if (userPerms.contains(opPerm) && !requestedPermState.get(opPerm)) {
                UserGroupManagementStatus status = removeUserRole(user, account, opPerm);
                if (!status.isOk) {
                    statuses.add(status);
                }
            }
        }
        return statuses;
    }

    private UserGroupManagementStatus removeUserRole(User user, Account account, OpPerms role) {
        UserGroupManagementStatus status = new UserGroupManagementStatus();
        if (account.getUsersByRole(role).size() > 1) {
            removeUserAccess(user, role);
        } else {
            status.isOk = false;
            status.notOkErrorKey = MUST_HAVE_ONE_USER_WITH_PERMISSION;
            status.errorDetail = role.getDescription();
        }
        return status;
    }

    public void removeUserAccess(User user, OpPerms perm) {
        Iterator<UserAccess> permissions = user.getOwnedPermissions().iterator();
        while (permissions.hasNext()) {
            UserAccess ua = permissions.next();
            if (ua.getOpPerm() == perm) {
                permissions.remove();
                userAccessDAO.remove(ua);
            }
        }
    }

}
