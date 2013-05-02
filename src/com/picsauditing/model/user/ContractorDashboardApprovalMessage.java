package com.picsauditing.model.user;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.ApprovalStatus;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;

import java.util.List;

public class ContractorDashboardApprovalMessage {

    public static Result getMessage(ContractorOperator contractorOperator, Permissions permissions, List<User> usersWithPermissions, List<User> corporateUsersWithPermissions) {
        if (!isUserAssociatedWithAccount(contractorOperator.getOperatorAccount(), permissions.getAccountId())) {
            return Result.ShowNothing;
        }
        if (contractorOperator.isWorkingStatus(ApprovalStatus.NotApproved)) {
            return permissions.isCorporate() ? getCorporateNotApprovedStatus(contractorOperator) : Result.ContractorNotApproved;
        }

        if (!basicPermissions(contractorOperator.getOperatorAccount().getId(),
                permissions.getAccountId(), permissions.has(OpPerms.ViewUnApproved),
                permissions.isApprovesRelationships(), permissions.hasPermission(OpPerms.ContractorApproval))) {

            if (contractorOperator.isWorkingStatus(ApprovalStatus.Pending)) {
                if (!usersWithPermissions.isEmpty()) {
                    return Result.ShowListOperator;
                } else if (!corporateUsersWithPermissions.isEmpty()) {
                    return Result.ShowListCorporate;
                } else {
                    return Result.ShowListAccountManager;
                }
            }  else {
                return Result.ShowNothing;
            }
        }
        if (contractorOperator.isWorkingStatus(ApprovalStatus.Pending) && permissions.isOperatorCorporate()) {

            if (canApproveContractors(permissions.isApprovesRelationships(), permissions.hasPermission(OpPerms.ContractorApproval))) {
                return Result.ShowButtons;
            } else {
                if (!usersWithPermissions.isEmpty()) {
                    return Result.ShowListOperator;
                } else if (!corporateUsersWithPermissions.isEmpty()) {
                    return Result.ShowListCorporate;
                } else {
                    return Result.ShowListAccountManager;
                }
            }
        }

        if (contractorOperator.isWorkingStatus(ApprovalStatus.Approved) && permissions.isCorporate()) {
            return getCorporateApprovedStatus(contractorOperator);
        }

        return Result.ShowNothing;
    }

    private static Result getCorporateApprovedStatus(ContractorOperator corporate) {
        List<ContractorOperator> existing = corporate.getChildOperatorAccountsWithStatus(corporate.getContractorAccount(), ApprovalStatus.NotApproved, ApprovalStatus.Pending);
        return (existing.isEmpty()) ? Result.ShowNothing : Result.ShowEverySiteExceptApprovedOnes;
    }

    private static Result getCorporateNotApprovedStatus(ContractorOperator corporate) {
        List<ContractorOperator> existing = corporate.getChildOperatorAccountsWithStatus(corporate.getContractorAccount(), ApprovalStatus.Approved);

        return (existing.isEmpty()) ? Result.ContractorNotApproved : Result.ContractorNotApprovedExpectSomeSites;
    }

    private static boolean isUserAssociatedWithAccount(OperatorAccount operator, int permissionsAccountId) {
        if (operator.getId() == permissionsAccountId)
            return true;

        for (OperatorAccount parent : operator.getParentOperators()) {
            if (parent.getId() == permissionsAccountId) ;
            return true;
        }

        return false;
    }

    private static boolean canApproveContractors(boolean permissionsApprovesRelationships, boolean permissionsHasPermissionContractorApproval) {
        return permissionsApprovesRelationships && permissionsHasPermissionContractorApproval;
    }

    private static boolean basicPermissions(int coOperatorAccountId, int permissionsAccountId, boolean permissionsHasPermissionViewUnApproved,
                                            boolean permissionsApprovesRelationships, boolean permissionsHasPermissionContractorApproval) {
        return (permissionsHasPermissionViewUnApproved ||
                canApproveContractors(permissionsApprovesRelationships, permissionsHasPermissionContractorApproval));
    }


    public enum Result {
        ShowButtons, ShowNothing, ContractorNotApprovedWithAccountManager, ContractorNotApproved, ShowListOperator,
        ShowListCorporate, ContractorNotApprovedExpectSomeSites, ShowEverySiteExceptApprovedOnes, ShowListAccountManager;

        public boolean isShowButtons() {
            return this == ShowButtons;
        }

        public boolean isShowNothing() {
            return this == ShowNothing;
        }

        public boolean isContractorNotApprovedWithAccountManager() {
            return this == ContractorNotApprovedWithAccountManager;
        }

        public boolean isContractorNotApproved() {
            return this == ContractorNotApproved;
        }

        public boolean isShowListOperator() {
            return this == ShowListOperator;
        }

        public boolean isShowListCorporate() {
            return this == ShowListCorporate;
        }

        public boolean isContractorNotApprovedExpectSomeSites() {
            return this == ContractorNotApprovedExpectSomeSites;
        }

        public boolean isShowEverySiteExceptApprovedOnes() {
            return this == ShowEverySiteExceptApprovedOnes;
        }

        public boolean isShowListAccountManager() {
            return this == ShowListAccountManager;
        }
    }
}