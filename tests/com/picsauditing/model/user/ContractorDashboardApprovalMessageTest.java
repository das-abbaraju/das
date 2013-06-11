package com.picsauditing.model.user;

import com.picsauditing.PicsTestUtil;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.jpa.entities.builders.ContractorOperatorBuilder;
import com.picsauditing.jpa.entities.builders.OperatorAccountBuilder;
import com.picsauditing.jpa.entities.builders.UserBuilder;
import com.picsauditing.model.user.ContractorDashboardApprovalMessage.Result;
import edu.emory.mathcs.backport.java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class ContractorDashboardApprovalMessageTest {
    @Mock
    private ContractorOperatorDAO contractorOperatorDAO;

    public static final List EMPTY_LIST = Collections.EMPTY_LIST;
    private static final List<User> APPROVERS = Arrays.asList(new User[]{User.builder().id(1001).build()});
    private ContractorDashboardApprovalMessage contractorDashboardApprovalMessage;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        contractorDashboardApprovalMessage = new ContractorDashboardApprovalMessage();
        PicsTestUtil.autowireDAOsFromDeclaredMocks(contractorDashboardApprovalMessage, this);
    }
    @Test
    public void testPendingWithApprovalPermission() throws Exception {
        OperatorAccountBuilder account = OperatorAccount.builder();

        UserBuilder user = standardUser(account).permission(OpPerms.ContractorApproval).permission(OpPerms.ViewUnApproved);
        ContractorOperatorBuilder contractorOperator = standardOperator(account).workStatus(ApprovalStatus.Pending);
        assertDisplayResult(Result.ShowButtons, user, contractorOperator, APPROVERS);
    }

	@Test
	public void testOperatorAutoApprovesRelationships() throws Exception {
		OperatorAccountBuilder account = OperatorAccount.builder().autoApproveRelationships(true);

		UserBuilder user = standardUser(account);
		ContractorOperatorBuilder contractorOperator = standardOperator(account).workStatus(ApprovalStatus.Pending);
		assertDisplayResult(Result.ShowNothing, user, contractorOperator, APPROVERS);
	}

    @Test
    public void testPendingWithoutApprovalPermission() throws Exception {
        OperatorAccountBuilder account = OperatorAccount.builder();

        UserBuilder user = standardUser(account).denyPermission(OpPerms.ContractorApproval).permission(OpPerms.ViewUnApproved);
        ContractorOperatorBuilder contractorOperator = standardOperator(account).workStatus(ApprovalStatus.Pending);
        assertDisplayResult(Result.ShowListOperator, user, contractorOperator, APPROVERS);
    }

    @Test
    public void testPendingWithoutApprovalPermissionButCorporateOptions() throws Exception {
        OperatorAccountBuilder account = OperatorAccount.builder();

        UserBuilder user = standardUser(account).denyPermission(OpPerms.ContractorApproval).permission(OpPerms.ViewUnApproved);
        ContractorOperatorBuilder contractorOperator = standardOperator(account).workStatus(ApprovalStatus.Pending);
        assertDisplayResult(Result.ShowListCorporate, user, contractorOperator, EMPTY_LIST, APPROVERS);
    }

    @Test
    public void testPendingWithoutApprovalPermissionAlone() throws Exception {
        OperatorAccountBuilder account = OperatorAccount.builder();

        UserBuilder user = standardUser(account).denyPermission(OpPerms.ContractorApproval).permission(OpPerms.ViewUnApproved);
        ContractorOperatorBuilder contractorOperator = standardOperator(account).workStatus(ApprovalStatus.Pending);
        assertDisplayResult(Result.ShowListAccountManager, user, contractorOperator, EMPTY_LIST, EMPTY_LIST);
    }

    @Test
    public void testNotApproved() throws Exception {
        OperatorAccountBuilder account = OperatorAccount.builder();

        UserBuilder user = standardUser(account);
        ContractorOperatorBuilder contractorOperator = standardOperator(account).workStatus(ApprovalStatus.Rejected);
        assertDisplayResult(Result.ContractorNotApproved, user, contractorOperator, new ArrayList<User>());
    }

    private UserBuilder standardUser(OperatorAccountBuilder account) {
        return User.builder().account(account.build());
    }

    private ContractorOperatorBuilder standardOperator(OperatorAccountBuilder account) {
        return ContractorOperator.builder()
                .contractor(ContractorAccount.builder().build())
                .operator(account.build());
    }

    private void assertDisplayResult(Result expected, UserBuilder user, ContractorOperatorBuilder contractorOperator, List<User> usersWithPermissions) throws Exception {
        assertDisplayResult(expected, user, contractorOperator, usersWithPermissions, EMPTY_LIST);
    }

    private void assertDisplayResult(Result expected, UserBuilder user, ContractorOperatorBuilder contractorOperator, List<User> usersWithPermissions, List<User> corperateUsersWithPermissions) throws Exception {
        assertDisplayResult(expected, user.build(), contractorOperator.build(), usersWithPermissions, corperateUsersWithPermissions);
    }

    @Test
    public void testApproved() throws Exception {
        OperatorAccountBuilder account = OperatorAccount.builder();

        UserBuilder user = standardUser(account);
        ContractorOperatorBuilder contractorOperator = standardOperator(account).workStatus(ApprovalStatus.Approved);
        assertDisplayResult(Result.ShowNothing, user, contractorOperator, EMPTY_LIST);
    }


    private void assertDisplayResult(Result expected, User user, ContractorOperator contractorOperator, List usersWithPermissions, List<User> corperateUsersWithPermissions) throws Exception{
        Permissions permissions = new Permissions().loginWithoutVerifyingLanguage(user);
        assertEquals(expected, contractorDashboardApprovalMessage.getMessage(contractorOperator, permissions, usersWithPermissions, corperateUsersWithPermissions));
    }

    @Test
    public void testCorperateNotApproved() throws Exception {
        ContractorAccount contractor = ContractorAccount.builder().id(3).build();

        OperatorAccountBuilder account = OperatorAccount.builder().corporate().child(createChild(contractor, ApprovalStatus.Rejected));

        UserBuilder user = standardUser(account).permission(OpPerms.ViewUnApproved);
        ContractorOperatorBuilder contractorOperator = standardOperator(account).workStatus(ApprovalStatus.Rejected);
        assertDisplayResult(Result.ContractorNotApproved, user, contractorOperator, EMPTY_LIST, APPROVERS);
    }

    private OperatorAccount createChild(ContractorAccount contractor, ApprovalStatus status) {
        return OperatorAccount.builder().operator(ContractorOperator.builder().contractor(contractor).workStatus(status).build()).build();

    }

    @Test
    public void testCorporateNotApprovedWithApprovedSites() throws Exception {
        ContractorAccount contractor = ContractorAccount.builder().id(3).build();
        OperatorAccountBuilder account = OperatorAccount.builder().corporate()
                .child(createChild(contractor, ApprovalStatus.Rejected))
                .child(createChild(contractor, ApprovalStatus.Approved));

        UserBuilder user = standardUser(account).permission(OpPerms.ViewUnApproved);
        ContractorOperatorBuilder contractorOperator = standardOperator(account).contractor(contractor).workStatus(ApprovalStatus.Rejected);

        List<ContractorOperator> childAccounts = new ArrayList<>();
        childAccounts.add(contractorOperator.build());
        when(contractorOperatorDAO.findByContractorAndWorkStatus(contractor, ApprovalStatus.Approved)).thenReturn(childAccounts);

        assertDisplayResult(Result.ContractorNotApprovedExpectSomeSites, user, contractorOperator, EMPTY_LIST, APPROVERS);
    }

    @Test
    public void testPendingCorporate() throws Exception {
        OperatorAccountBuilder account = OperatorAccount.builder().corporate();

        UserBuilder user = standardUser(account).permission(OpPerms.ViewUnApproved).permission(OpPerms.ContractorApproval);
        ContractorOperatorBuilder contractorOperator = standardOperator(account).workStatus(ApprovalStatus.Pending);
        assertDisplayResult(Result.ShowButtons, user, contractorOperator, EMPTY_LIST, EMPTY_LIST);
    }

    @Test
    public void testPendingCorporateWithoutPermissions() throws Exception {
        OperatorAccountBuilder account = OperatorAccount.builder().corporate();

        UserBuilder user = standardUser(account).permission(OpPerms.ViewUnApproved).denyPermission(OpPerms.ContractorApproval);
        ContractorOperatorBuilder contractorOperator = standardOperator(account).workStatus(ApprovalStatus.Pending);
        assertDisplayResult(Result.ShowListCorporate, user, contractorOperator, EMPTY_LIST, APPROVERS);
    }

    @Test
    public void testPendingCorporateAloneWithoutPermissions() throws Exception {
        OperatorAccountBuilder account = OperatorAccount.builder().corporate();

        UserBuilder user = standardUser(account).permission(OpPerms.ViewUnApproved).denyPermission(OpPerms.ContractorApproval);
        ContractorOperatorBuilder contractorOperator = standardOperator(account).workStatus(ApprovalStatus.Pending);

        assertDisplayResult(Result.ShowListAccountManager, user, contractorOperator, EMPTY_LIST, EMPTY_LIST);
    }

    @Test
    public void testApprovedCorporateForAllClientSites() throws Exception {
               ContractorAccount contractor = ContractorAccount.builder().id(3).build();
        OperatorAccountBuilder account = OperatorAccount.builder().corporate()
                .child(createChild(contractor, ApprovalStatus.Approved))
                .child(createChild(contractor, ApprovalStatus.Approved));

        UserBuilder user = standardUser(account).permission(OpPerms.ViewUnApproved).denyPermission(OpPerms.ContractorApproval);
        ContractorOperatorBuilder contractorOperator = standardOperator(account).workStatus(ApprovalStatus.Approved);

        assertDisplayResult(Result.ShowNothing, user, contractorOperator, EMPTY_LIST, EMPTY_LIST);
    }


    @Test
    public void testApprovedCorporateNotAllClientSitesApproved() throws Exception {

        ContractorAccount contractor = ContractorAccount.builder().id(3).build();
        OperatorAccountBuilder account = OperatorAccount.builder().corporate()
                .child(createChild(contractor, ApprovalStatus.Rejected))
                .child(createChild(contractor, ApprovalStatus.Approved));

        UserBuilder user = standardUser(account).permission(OpPerms.ViewUnApproved).denyPermission(OpPerms.ContractorApproval);
        ContractorOperatorBuilder contractorOperator = standardOperator(account).contractor(contractor).workStatus(ApprovalStatus.Approved);
        List<ContractorOperator> childAccounts = new ArrayList<>();
        childAccounts.add(contractorOperator.build());
        when(contractorOperatorDAO.findByContractorAndWorkStatus(contractor, ApprovalStatus.Rejected, ApprovalStatus.Pending)).thenReturn(childAccounts);
        assertDisplayResult(Result.ShowEverySiteExceptApprovedOnes, user, contractorOperator, EMPTY_LIST, EMPTY_LIST);
    }


}
