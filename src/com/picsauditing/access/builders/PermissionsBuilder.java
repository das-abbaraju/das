package com.picsauditing.access.builders;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.access.UserAccess;
import com.picsauditing.jpa.entities.OperatorAccount;

public class PermissionsBuilder {
    private Permissions permissions = new Permissions();

    public Permissions build() {

        return permissions ;
    }

    public PermissionsBuilder operator(OperatorAccount account) {
        permissions.setAccountType("Operator");
        permissions.setAccountId(account.getId());
        return this;
    }

    public PermissionsBuilder permission(OpPerms p) {
        UserAccess access = new UserAccess();
        access.setOpPerm(p);
        permissions.getPermissions().add(access);
        return this;
    }
}
