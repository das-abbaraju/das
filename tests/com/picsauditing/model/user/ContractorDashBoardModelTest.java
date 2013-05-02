package com.picsauditing.model.user;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.PermissionBuilder;
import com.picsauditing.access.Permissions;
import com.picsauditing.access.UserAccess;
import com.picsauditing.jpa.entities.ApprovalStatus;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.builders.OperatorAccountBuilder;
import com.picsauditing.toggle.FeatureToggle;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.api.mockito.PowerMockito;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class ContractorDashBoardModelTest {
    @Mock
    private Logger logger;
    @Mock
    private PermissionBuilder permissionBuilder;
    private ContractorDashBoardModel contractorDashBoardModel;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        contractorDashBoardModel = new ContractorDashBoardModel();
        Whitebox.setInternalState(contractorDashBoardModel, "permissionBuilder", permissionBuilder);
    }

    @Test
    public void testGetPermittedUser() throws Exception {
        OperatorAccount operator = OperatorAccount.builder()
                .user(User.builder().permission(OpPerms.ContractorApproval).build())
                .build();

        initializeUserPermissions(operator.getUsers());

        assertEquals(1, contractorDashBoardModel.getPermittedUsers(operator, OpPerms.ContractorApproval, 10).size());
    }

    @Test
    public void testGetPermittedUser_OneUserWithoutPermission() throws Exception {
        OperatorAccount operator = OperatorAccount.builder()
                .user(User.builder().permission(OpPerms.ContractorApproval).build())
                .user(User.builder().denyPermission(OpPerms.ContractorApproval).build())
                .build();

        initializeUserPermissions(operator.getUsers());

        assertEquals(1, contractorDashBoardModel.getPermittedUsers(operator, OpPerms.ContractorApproval, 10).size());
    }

    @Test
    public void testGetPermittedUser_WithUserGroup() throws Exception {
        OperatorAccount operator = OperatorAccount.builder()
                .user(User.builder().permission(OpPerms.ContractorApproval).build())
                .user(User.builder().permission(OpPerms.ContractorApproval).group().build())
                .build();

        initializeUserPermissions(operator.getUsers());

        assertEquals(1, contractorDashBoardModel.getPermittedUsers(operator, OpPerms.ContractorApproval, 10).size());
    }

    @Test
    public void testGetPermittedUser_Limit() throws Exception {
        int limit = 10;
        OperatorAccountBuilder operatorBuilder = OperatorAccount.builder();
        for (int i = 0; i < limit + 3; i++) {
            operatorBuilder.user(User.builder().permission(OpPerms.ContractorApproval).build());
        }
        OperatorAccount operator = operatorBuilder.build();

        initializeUserPermissions(operator.getUsers());

        assertEquals(limit, contractorDashBoardModel.getPermittedUsers(operator, OpPerms.ContractorApproval, limit).size());
    }

    private void initializeUserPermissions(List<User> users) throws Exception {
        for (User user: users) {
            Permissions permissions = new Permissions();
            Set<UserAccess> list = new HashSet<>();
            for (com.picsauditing.jpa.entities.UserAccess permission: user.getPermissions()) {
                com.picsauditing.access.UserAccess userAccess = new UserAccess();
                userAccess.setOpPerm(permission.getOpPerm());
                userAccess.setEditFlag(true);
                userAccess.setViewFlag(true);

                list.add(userAccess);
            }
            Whitebox.setInternalState(permissions, "permissions", list);
            when(permissionBuilder.login(user)).thenReturn(permissions);
        }
    }
}
