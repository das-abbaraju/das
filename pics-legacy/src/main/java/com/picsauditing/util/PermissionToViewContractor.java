package com.picsauditing.util;

import java.util.List;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorOperator;

public class PermissionToViewContractor {

	private int id;
	private Permissions permissions = null;

	private List<ContractorOperator> operators;
	private List<ContractorAudit> activeAudits;

	/**
	 * 
	 * @param id
	 *            Contractor ID
	 * @param permissions
	 *            Currently logged in user
	 */
	public PermissionToViewContractor(int id, Permissions permissions) {
		this.id = id;
		this.permissions = permissions;
	}

	public boolean check(boolean limitedView) {
		if (id == 0)
			return false;

		if (permissions == null)
			return false;

		if (permissions.hasPermission(OpPerms.AllContractors))
			return true;

		if (permissions.isContractor()) {
			return permissions.getAccountId() == this.id;
		}

		if (limitedView || permissions.hasPermission(OpPerms.ImportPQF))
			// Basically, if all we're doing is searching for contractors
			// and looking at their summary page, then it's OK
			return true;

		else if (!permissions.hasPermission(OpPerms.ContractorDetails)) {
			return false;
		}

		if (permissions.isOperatorCorporate()) {
			// If we want to look at their detail, like PQF data
			// Then we have to add them first (generalContractors).
			if (permissions.isCorporate()) {
				for (ContractorOperator co : operators) {
					int opID = co.getOperatorAccount().getId();
					if (permissions.getOperatorChildren().contains(opID))
						return true;
				}
				return false;
			}
			// To see anything other than the summary, you need to be on their
			// list
			for (ContractorOperator operator : operators)
				if (operator.getOperatorAccount().getIdString().equals(permissions.getAccountIdString()))
					return true;
		}

		// Now check to see if the User is assigned to an active audit
		for (ContractorAudit audit : activeAudits) {
			if (audit.getAuditor() != null && audit.getAuditor().getId() == permissions.getUserId()) {
				for (ContractorAuditOperator cao : audit.getOperators()) {
					if (cao.getStatus().before(AuditStatus.Complete))
						return true;
				}
			}
		}

		return false;
	}

	public void setOperators(List<ContractorOperator> operators) {
		this.operators = operators;
	}

	public void setActiveAudits(List<ContractorAudit> activeAudits) {
		this.activeAudits = activeAudits;
	}

}
