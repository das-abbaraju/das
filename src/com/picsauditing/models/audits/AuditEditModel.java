package com.picsauditing.models.audits;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.*;

/**
 * This model determines if an audit can be edited
 */
public class AuditEditModel {

	ContractorAudit conAudit;
	Permissions permissions;

	public boolean canEdit(ContractorAudit conAudit, Permissions permissions) {
		this.conAudit= conAudit;
		this.permissions = permissions;

		AuditType type = conAudit.getAuditType();

		if (type.getClassType().isPolicy() && permissions.isAdmin()) {
			return true;
		}
		if (conAudit.isExpired()) {
			return false;
		}

		if (permissions.isContractor() && type.isWCB() && conAudit.hasCaoStatusAfter(AuditStatus.Incomplete)) {
			return false;
		}

		if (type.getClassType().isPolicy()) {
			// we don't want the contractors to edit the effective dates on the
			// old policy
			if (conAudit.willExpireSoon()) {
				if (conAudit.hasCaoStatusAfter(AuditStatus.Submitted)) {
					return false;
				}
			}
		}

		// Auditors can edit their assigned audits
		if (type.isHasAuditor() && !type.isCanContractorEdit() && conAudit.getAuditor() != null
				&& permissions.getUserId() == conAudit.getAuditor().getId()) {
			return true;
		}

		if (permissions.hasPermission(OpPerms.ImportPQF) && type.isPicsPqf()) {
			return true;
		}

		if (permissions.seesAllContractors()) {
			return true;
		}

		if (permissions.isContractor())
			return canContractorEdit();

		if (permissions.isOperatorCorporate()) {
			return canOperatorEdit();
		}

		if (type.getEditPermission() != null) {
			if (permissions.hasPermission(type.getEditPermission())) {
				return true;
			}
		}

		return false;
	}

	private boolean canOperatorEdit() {
		if (conAudit.getAuditType().getWorkFlow().isUseStateForEdit()) {
			return canOperatorEditByWorkFlow();
		} else {
			return canOperatorEditByAuditType();
		}
	}

	private boolean canOperatorEditByAuditType() {
		AuditType type = conAudit.getAuditType();

		if (type.getClassType().equals(AuditTypeClass.Audit)) {
			if (type.isAnnualAddendum())
				return false;
			if (type.getEditAudit() == null)
				return true;
			if (permissions.hasGroup(type.getEditAudit().getId())) {
				return true;
			} else {
				return false;
			}
		}

		if (type.getEditPermission() != null) {
			if (permissions.hasPermission(type.getEditPermission())) {
				return true;
			}
		}

		return false;
	}

	private boolean canOperatorEditByWorkFlow() {
		boolean canEdit = false;
		for (ContractorAuditOperator cao:conAudit.getOperatorsVisible()) {
			if (isValidCao(cao)) {
				WorkflowState state = findWorkflowState(cao.getStatus());
				if (state.isOperatorCanEdit()) {
					AuditType type = conAudit.getAuditType();
					if (type.getEditAudit() == null) {
						canEdit = true;
					}else if (permissions.hasGroup(type.getEditAudit().getId())) {
						canEdit =  true;
					}
				}
			}

			if (canEdit) break;
		}
		return canEdit;
	}

	private WorkflowState findWorkflowState(AuditStatus status) {
		for (WorkflowState state : conAudit.getAuditType().getWorkFlow().getStates()) {
			if (state.getStatus().equals(status)) {
				return state;
			}
		}

		return null;
	}

	private boolean isValidCao(ContractorAuditOperator cao) {
		boolean foundValidCao = false;
		for (ContractorAuditOperatorPermission caop:cao.getCaoPermissions()) {
			if (caop.getOperator().isOrIsDescendantOf(permissions.getAccountId())) {
				foundValidCao = true;
				break;
			}
		}
		return foundValidCao;
	}

	private boolean canContractorEdit() {
		if (conAudit.getAuditType().getWorkFlow().isUseStateForEdit()) {
			return canContractorEditByWorkFlow();
		} else {
			return canContractorEditByAuditType();
		}
	}

	private boolean canContractorEditByAuditType() {
		AuditType type = conAudit.getAuditType();
		boolean canEdit = type.isCanContractorEdit();

		if (conAudit.getAuditType().getClassType().equals(AuditTypeClass.PQF) && !conAudit.getAuditType().isPicsPqf()) {
			for (ContractorAuditOperator cao : conAudit.getOperatorsVisible()) {
				if (cao.getStatus().after(AuditStatus.Submitted)) {
					canEdit = false;
					break;
				}
			}
		}
		if (conAudit.getAuditType().getWorkFlow().getId() == 5
				|| conAudit.getAuditType().getWorkFlow().getId() == 3) {
			if (canEdit) {
				canEdit = false;
				for (ContractorAuditOperator cao : conAudit.getOperatorsVisible()) {
					if (cao.getStatus().before(AuditStatus.Submitted)) {
						canEdit = true;
						break;
					}
				}
			}
		}

		return canEdit;
	}

	private boolean canContractorEditByWorkFlow() {
		for (ContractorAuditOperator cao:conAudit.getOperatorsVisible())
			for (WorkflowState state : conAudit.getAuditType().getWorkFlow().getStates())
				if (state.getStatus().equals(cao.getStatus()) && state.isContractorCanEdit()) {
					return true;
				}

		return false;
	}
}
