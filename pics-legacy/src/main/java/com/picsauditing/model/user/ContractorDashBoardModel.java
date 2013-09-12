package com.picsauditing.model.user;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.PermissionBuilder;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.contractors.ContractorDashboard;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class ContractorDashBoardModel {

    @Autowired
    private PermissionBuilder permissionBuilder;

    private static Logger logger = LoggerFactory
            .getLogger(ContractorDashBoardModel.class);

    public List<User> getPermittedUsers(OperatorAccount operator, OpPerms operatorPermission, int limit) throws Exception {
        List<User> permittedUsers = new ArrayList<>();

        for (User user : operator.getUsers()) {
            if (permittedUsers.size() > limit - 1) {
                break;
            }
            //try {
                if (user.isGroup()) {
                    continue;
                }

                Permissions permissions = permissionBuilder.login(user);
                if (permissions.hasPermission(operatorPermission)) {
                    permittedUsers.add(user);
                }
            /*}catch (Exception e) {

                logger.error("Cannot login user", e);
            }*/
        }

        return permittedUsers;
    }
}
