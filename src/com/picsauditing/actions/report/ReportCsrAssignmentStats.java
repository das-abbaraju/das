package com.picsauditing.actions.report;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.jpa.entities.User;

public class ReportCsrAssignmentStats extends ReportAccount {
	private static final long serialVersionUID = 1317657856130650808L;
	List<User> csrList = new ArrayList<User>();

	public int getNumberOfActiveAssignedContractors(int csrId) {
		return getNumberOfAssignedContractorsByType("Active", csrId);
	}

	public int getNumberOfAssignedRegistrationRequests(int csrId) {
		return getNumberOfAssignedContractorsByType("Active", csrId);
	}

	public int getNumberOfAssignedContractorsByType(String accountType, int csrId) {
		return contractorAccountDAO.findWhere(
				" a.status = '" + accountType + "' AND a.welcomeAuditor_id = " + csrId + " ").size();
	}

	public int getNumberOfAssignedContractorsWithAuditGuard(int csrId) {
		return getNumberOfAssignedContractorsWith("AuditGUARD", csrId);
	}

	public int getNumberOfAssignedContractorsWithInsureGuard(int csrId) {
		return getNumberOfAssignedContractorsWith("InsureGUARD", csrId);
	}

	public int getNumberOfAssignedContractorsWith(String productType, int csrId) {
		// TODO
		return 0;
	}

	public List<User> getCsrList() {
		if (csrList != null) {
			return userDAO.findWhere("u.isActive = 'Yes' and u.accountID = 1100 and u.assignmentCapacity > 0");
		} else {
			return csrList;
		}
	}

	public void setCsrList(List<User> csrList) {
		this.csrList = csrList;
	}
}
