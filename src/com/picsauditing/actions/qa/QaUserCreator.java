package com.picsauditing.actions.qa;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import com.picsauditing.access.*;
import com.picsauditing.actions.PicsApiSupport;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.model.user.UserManagementService;
import com.picsauditing.model.usergroup.UserGroupManagementStatus;
import com.picsauditing.security.EncodedKey;
import com.picsauditing.util.Strings;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class QaUserCreator extends PicsApiSupport {
    private static final Logger logger = LoggerFactory.getLogger(QaUserCreator.class);
    private Account account;
    private Set<Integer> groupId = new HashSet<>();
    private Set<String> opPerm = new HashSet<>();

    @Autowired
    private UserManagementService userManagementService;

    // needs OpPerms.EditUsers with edit true and OpPerms.AllOperators
    @RequiredPermission(value = OpPerms.CreateTestUser)
    @ApiRequired
    public String execute() throws Exception {
        json = new JSONObject();
        if (account == null) {
            json.put("status", "ERROR: You must specify an accountId");
        } else {
            User user = createUser();

            Map<User, Boolean> addGroupStatus = addUserToGroups(user);
            Map<String, Boolean> addPermStatus = addPermsToUser(user);

            // the password encryption only works if the user already has an id, so we have to save twice
            // plus we're saving twice for the perms (if any)
            String password = EncodedKey.randomPassword();
            user.setEncryptedPassword(password);
            userManagementService.saveWithAuditColumnsAndRefresh(user, permissions);

            setSaveStatusInJson(addGroupStatus, addPermStatus);
            json.put("username", user.getUsername());
            json.put("password", password);
        }

        return JSON;
    }

    private void setSaveStatusInJson(Map<User, Boolean> addStatus, Map<String, Boolean> addPermStatus) {
        StringBuffer added = new StringBuffer();
        StringBuffer skipped = new StringBuffer();
        stringifyGroups(addStatus, added, skipped);
        stringifyPerms(addPermStatus, added, skipped);
        if (Strings.isEmpty(skipped.toString()) && Strings.isEmpty(added.toString())) {
            json.put("status", "SUCCESS: no groups or perms added");
        } else if (Strings.isEmpty(skipped.toString())) {
            json.put("status", "SUCCESS: added " + added.toString());
        } else {
            json.put("status", "PARTIAL: added " + added.toString() + "; skipped " + skipped.toString());
        }
    }

    private void stringifyGroups(Map<User, Boolean> addStatus, StringBuffer added, StringBuffer skipped) {
        for (User user : addStatus.keySet()) {
            if (addStatus.get(user)) {
                if (!Strings.isEmpty(added.toString())) {
                    added.append(", ");
                }
                added.append("group ").append(user.getId());
            } else {
                if (!Strings.isEmpty(skipped.toString())) {
                    skipped.append(", ");
                }
                skipped.append("group ").append(user.getId());
            }
        }
    }

    private void stringifyPerms(Map<String, Boolean> addPermStatus, StringBuffer added, StringBuffer skipped) {
        for (String perm : addPermStatus.keySet()) {
            if (addPermStatus.get(perm)) {
                if (!Strings.isEmpty(added.toString())) {
                    added.append(", ");
                }
                added.append("perm ").append(perm);
            } else {
                if (!Strings.isEmpty(skipped.toString())) {
                    skipped.append(", ");
                }
                skipped.append("perm ").append(perm);
            }
        }
    }

    private Map<String, Boolean> addPermsToUser(User user) throws Exception {
        Map<OpPerms, Boolean> requestedPermState = new HashMap<>();
        Map<String, Boolean> addStatus = new HashMap<>();
        Set<String> perms = getOpPerm();
        for (String perm : perms) {
            OpPerms op = OpPerms.valueOf(perm);
            if (op == null) {
                addStatus.put(perm, Boolean.FALSE);
            } else {
                addStatus.put(perm, Boolean.TRUE);
                requestedPermState.put(op, Boolean.TRUE);
            }
        }

        userManagementService.updateUserPermissions(user, account, permissions, requestedPermState);

        return addStatus;
    }

    private Map<User, Boolean> addUserToGroups(User user) throws Exception {
        Map<User, Boolean> addStatus = new HashMap<>();
        List<User> okGroups = groupsAvailableToThisAccount(user);
        for(Integer groupId : getGroupId()) {
            User group = userDAO.find(groupId);
            if (okGroups.contains(group) && userIsAddableToGroup(user, group)) {
                userManagementService.addUserToGroup(user, group, permissions);
                addStatus.put(group, Boolean.TRUE);
            } else {
                addStatus.put(group, Boolean.FALSE);
            }
        }
        return addStatus;
    }

    private boolean userIsAddableToGroup(User user, User group) {
        UserGroupManagementStatus status = userManagementService.userIsAddableToGroup(user, group);
        return status.isOk;
    }

    private User createUser() throws Exception {
        User user = userManagementService.initializeNewUser(account);
        user.setUsername(generateTestUserUsername());
        user.setName("Selenium Test User");
        user.setFirstName("Selenium");
        user.setLastName("Test User");
        user.setEmail("tester@picsauditing.com");
        user.setLocale(Locale.ENGLISH);
        userManagementService.saveWithAuditColumnsAndRefresh(user, permissions);
        return user;
    }

    private String generateTestUserUsername() {
        return "Selenium-" + guid();
    }

    private List<User> groupsAvailableToThisAccount(User user) {
        return userManagementService.getAddableGroups(permissions, account, user);
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Set<Integer> getGroupId() {
        return groupId;
    }

    public void setGroupId(Set<Integer> groupIds) {
        this.groupId = groupIds;
    }

    public Set<String> getOpPerm() {
        return opPerm;
    }

    public void setOpPerm(Set<String> opPerm) {
        this.opPerm = opPerm;
    }

    private String guid() {
        EthernetAddress ethernetAddress = EthernetAddress.fromInterface();
        TimeBasedGenerator uuid_gen = Generators.timeBasedGenerator(ethernetAddress);
        UUID uuid = uuid_gen.generate();
        return uuid.toString();
    }

}
