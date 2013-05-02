package com.picsauditing.model.user;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.jpa.entities.builders.ContractorOperatorBuilder;
import com.picsauditing.jpa.entities.builders.OperatorAccountBuilder;
import com.picsauditing.jpa.entities.builders.UserBuilder;
import com.picsauditing.model.user.ContractorDashboardApprovalMessage.Result;
import edu.emory.mathcs.backport.java.util.Arrays;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ContractorDashboardApprovalMessageTest {

    public static final List EMPTY_LIST = Collections.EMPTY_LIST;
    private static final List<User> APPROVERS = Arrays.asList(new User[]{User.builder().id(1001).build()});


    @Test
    public void testPendingWithApprovalPermission() {
        OperatorAccountBuilder account = OperatorAccount.builder();

        UserBuilder user = standardUser(account).permission(OpPerms.ContractorApproval).permission(OpPerms.ViewUnApproved);
        ContractorOperatorBuilder contractorOperator = standardOperator(account).workStatus(ApprovalStatus.Pending);
        assertDisplayResult(Result.ShowButtons, user, contractorOperator, APPROVERS);
    }

    @Test
    public void testPendingWithoutApprovalPermission() {
        OperatorAccountBuilder account = OperatorAccount.builder();

        UserBuilder user = standardUser(account).denyPermission(OpPerms.ContractorApproval).permission(OpPerms.ViewUnApproved);
        ContractorOperatorBuilder contractorOperator = standardOperator(account).workStatus(ApprovalStatus.Pending);
        assertDisplayResult(Result.ShowListOperator, user, contractorOperator, APPROVERS);
    }

    @Test
    public void testPendingWithoutApprovalPermissionButCorporateOptions() {
        OperatorAccountBuilder account = OperatorAccount.builder();

        UserBuilder user = standardUser(account).denyPermission(OpPerms.ContractorApproval).permission(OpPerms.ViewUnApproved);
        ContractorOperatorBuilder contractorOperator = standardOperator(account).workStatus(ApprovalStatus.Pending);
        assertDisplayResult(Result.ShowListCorporate, user, contractorOperator, EMPTY_LIST, APPROVERS);
    }

    @Test
    public void testPendingWithoutApprovalPermissionAlone() {
        OperatorAccountBuilder account = OperatorAccount.builder();

        UserBuilder user = standardUser(account).denyPermission(OpPerms.ContractorApproval).permission(OpPerms.ViewUnApproved);
        ContractorOperatorBuilder contractorOperator = standardOperator(account).workStatus(ApprovalStatus.Pending);
        assertDisplayResult(Result.ShowListAccountManager, user, contractorOperator, EMPTY_LIST, EMPTY_LIST);
    }

    @Test
    public void testNotApproved() {
        OperatorAccountBuilder account = OperatorAccount.builder();

        UserBuilder user = standardUser(account);
        ContractorOperatorBuilder contractorOperator = standardOperator(account).workStatus(ApprovalStatus.NotApproved);
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

    private void assertDisplayResult(Result expected, UserBuilder user, ContractorOperatorBuilder contractorOperator, List<User> usersWithPermissions) {
        assertDisplayResult(expected, user, contractorOperator, usersWithPermissions, EMPTY_LIST);
    }

    private void assertDisplayResult(Result expected, UserBuilder user, ContractorOperatorBuilder contractorOperator, List<User> usersWithPermissions, List<User> corperateUsersWithPermissions) {
        assertDisplayResult(expected, user.build(), contractorOperator.build(), usersWithPermissions, corperateUsersWithPermissions);
    }

    @Test
    public void testApproved() {
        OperatorAccountBuilder account = OperatorAccount.builder();

        UserBuilder user = standardUser(account);
        ContractorOperatorBuilder contractorOperator = standardOperator(account).workStatus(ApprovalStatus.Approved);
        assertDisplayResult(Result.ShowNothing, user, contractorOperator, EMPTY_LIST);
    }


    private void assertDisplayResult(Result expected, User user, ContractorOperator contractorOperator, List usersWithPermissions, List<User> corperateUsersWithPermissions) {
        Permissions permissions = new Permissions().loginWithoutVerifyingLanguage(user);
        assertEquals(expected, ContractorDashboardApprovalMessage.getMessage(contractorOperator, permissions, usersWithPermissions, corperateUsersWithPermissions));
    }

    @Test
    public void testCorperateNotApproved() {
        ContractorAccount contractor = ContractorAccount.builder().id(3).build();

        OperatorAccountBuilder account = OperatorAccount.builder().corporate().child(createChild(contractor, ApprovalStatus.NotApproved));

        UserBuilder user = standardUser(account).permission(OpPerms.ViewUnApproved);
        ContractorOperatorBuilder contractorOperator = standardOperator(account).workStatus(ApprovalStatus.NotApproved);
        assertDisplayResult(Result.ContractorNotApproved, user, contractorOperator, EMPTY_LIST, APPROVERS);
    }

    private OperatorAccount createChild(ContractorAccount contractor, ApprovalStatus status) {
        return OperatorAccount.builder().operator(ContractorOperator.builder().contractor(contractor).workStatus(status).build()).build();

    }

    @Test
    public void testCorporateNotApprovedWithApprovedSites() {

        ContractorAccount contractor = ContractorAccount.builder().id(3).build();
        OperatorAccountBuilder account = OperatorAccount.builder().corporate()
                .child(createChild(contractor, ApprovalStatus.NotApproved))
                .child(createChild(contractor, ApprovalStatus.Approved));

        UserBuilder user = standardUser(account).permission(OpPerms.ViewUnApproved);
        ContractorOperatorBuilder contractorOperator = standardOperator(account).contractor(contractor).workStatus(ApprovalStatus.NotApproved);
        assertDisplayResult(Result.ContractorNotApprovedExpectSomeSites, user, contractorOperator, EMPTY_LIST, APPROVERS);
    }

    @Test
    public void testPendingCorporate() {
        OperatorAccountBuilder account = OperatorAccount.builder().corporate();

        UserBuilder user = standardUser(account).permission(OpPerms.ViewUnApproved).permission(OpPerms.ContractorApproval);
        ContractorOperatorBuilder contractorOperator = standardOperator(account).workStatus(ApprovalStatus.Pending);
        assertDisplayResult(Result.ShowButtons, user, contractorOperator, EMPTY_LIST, EMPTY_LIST);
    }

    @Test
    public void testPendingCorporateWithoutPermissions() {
        OperatorAccountBuilder account = OperatorAccount.builder().corporate();

        UserBuilder user = standardUser(account).permission(OpPerms.ViewUnApproved).denyPermission(OpPerms.ContractorApproval);
        ContractorOperatorBuilder contractorOperator = standardOperator(account).workStatus(ApprovalStatus.Pending);
        assertDisplayResult(Result.ShowListCorporate, user, contractorOperator, EMPTY_LIST, APPROVERS);
    }

    @Test
    public void testPendingCorporateAloneWithoutPermissions() {
        OperatorAccountBuilder account = OperatorAccount.builder().corporate();

        UserBuilder user = standardUser(account).permission(OpPerms.ViewUnApproved).denyPermission(OpPerms.ContractorApproval);
        ContractorOperatorBuilder contractorOperator = standardOperator(account).workStatus(ApprovalStatus.Pending);
        assertDisplayResult(Result.ShowListAccountManager, user, contractorOperator, EMPTY_LIST, EMPTY_LIST);
    }

    @Test
    public void testApprovedCorporateForAllClientSites() {
               ContractorAccount contractor = ContractorAccount.builder().id(3).build();
        OperatorAccountBuilder account = OperatorAccount.builder().corporate()
                .child(createChild(contractor, ApprovalStatus.Approved))
                .child(createChild(contractor, ApprovalStatus.Approved));

        UserBuilder user = standardUser(account).permission(OpPerms.ViewUnApproved).denyPermission(OpPerms.ContractorApproval);
        ContractorOperatorBuilder contractorOperator = standardOperator(account).workStatus(ApprovalStatus.Approved);
        assertDisplayResult(Result.ShowNothing, user, contractorOperator, EMPTY_LIST, EMPTY_LIST);
    }


    @Test
    public void testApprovedCorporateNotAllClientSitesApproved() {

        ContractorAccount contractor = ContractorAccount.builder().id(3).build();
        OperatorAccountBuilder account = OperatorAccount.builder().corporate()
                .child(createChild(contractor, ApprovalStatus.NotApproved))
                .child(createChild(contractor, ApprovalStatus.Approved));

        UserBuilder user = standardUser(account).permission(OpPerms.ViewUnApproved).denyPermission(OpPerms.ContractorApproval);
        ContractorOperatorBuilder contractorOperator = standardOperator(account).contractor(contractor).workStatus(ApprovalStatus.Approved);
        assertDisplayResult(Result.ShowEverySiteExceptApprovedOnes, user, contractorOperator, EMPTY_LIST, EMPTY_LIST);
    }


}
