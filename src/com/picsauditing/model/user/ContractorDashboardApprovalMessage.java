package com.picsauditing.model.user;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.jpa.entities.ApprovalStatus;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ContractorDashboardApprovalMessage {
	@Autowired
	private ContractorOperatorDAO contractorOperatorDAO;
	private final Logger profiler = LoggerFactory.getLogger("org.perf4j.DebugTimingLogger");

	public Result getMessage(ContractorOperator contractorOperator, Permissions permissions, List<User> usersWithPermissions, List<User> corporateUsersWithPermissions) {
		StopWatch stopwatch = new Slf4JStopWatch(profiler);
		stopwatch.start();
		if (contractorOperator.getOperatorAccount().isAutoApproveRelationships()) {
			return Result.ShowNothing;
		}

		if (!isUserAssociatedWithAccount(contractorOperator.getOperatorAccount(), permissions.getAccountId())) {
			return Result.ShowNothing;
		}
		if (contractorOperator.isWorkingStatus(ApprovalStatus.Rejected)) {
			return permissions.isCorporate() ? getCorporateNotApprovedStatus(contractorOperator) : Result.ContractorNotApproved;
		}

		if (!basicPermissions(permissions.has(OpPerms.ViewUnApproved), permissions.isApprovesRelationships(),
				permissions.hasPermission(OpPerms.ContractorApproval))) {

			if (contractorOperator.isWorkingStatus(ApprovalStatus.Pending)) {
				if (!usersWithPermissions.isEmpty()) {
					return Result.ShowListOperator;
				} else if (!corporateUsersWithPermissions.isEmpty()) {
					return Result.ShowListCorporate;
				} else {
					return Result.ShowListAccountManager;
				}
			} else {
				return Result.ShowNothing;
			}
		}

		if ((contractorOperator.isWorkingStatus(ApprovalStatus.Pending) || contractorOperator.getOperatorAccount().getId() != permissions.getAccountId()) && permissions.isOperatorCorporate()) {

			if (!contractorOperator.isWorkingStatus(ApprovalStatus.Pending)) {
				return Result.ShowNothing;
			}
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
		stopwatch.stop();
		profiler.debug("ContractorDashboardApprovalMessage.getMessage took " + stopwatch.getElapsedTime() + "ms");
		return Result.ShowNothing;
	}

	private Result getCorporateApprovedStatus(ContractorOperator corporate) {
		StopWatch stopwatch = new Slf4JStopWatch(profiler);
		stopwatch.start();
		List<ContractorOperator> existing = contractorOperatorDAO.findByContractorAndWorkStatus(corporate.getContractorAccount(), ApprovalStatus.Rejected, ApprovalStatus.Pending);
		stopwatch.stop();
		profiler.debug("ContractorDashboardApprovalMessage.getMessage took " + stopwatch.getElapsedTime() + "ms");
		return (existing.isEmpty()) ? Result.ShowNothing : Result.ShowEverySiteExceptApprovedOnes;
	}

	private Result getCorporateNotApprovedStatus(ContractorOperator corporate) {
		StopWatch stopwatch = new Slf4JStopWatch(profiler);
		stopwatch.start();
		List<ContractorOperator> existing = contractorOperatorDAO.findByContractorAndWorkStatus(corporate.getContractorAccount(), ApprovalStatus.Approved);
		stopwatch.stop();
		profiler.debug("ContractorDashboardApprovalMessage.getMessage took " + stopwatch.getElapsedTime() + "ms");
		return (existing.isEmpty()) ? Result.ContractorNotApproved : Result.ContractorNotApprovedExpectSomeSites;
	}

	private boolean isUserAssociatedWithAccount(OperatorAccount operator, int permissionsAccountId) {

		if (operator.getId() == permissionsAccountId)
			return true;

		for (OperatorAccount parent : operator.getParentOperators()) {
			if (parent.getId() == permissionsAccountId) ;
			return true;
		}

		return false;
	}

	private boolean canApproveContractors(boolean permissionsApprovesRelationships, boolean permissionsHasPermissionContractorApproval) {
		return permissionsApprovesRelationships && permissionsHasPermissionContractorApproval;
	}

	private boolean basicPermissions(boolean permissionsHasPermissionViewUnApproved,
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