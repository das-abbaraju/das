package com.picsauditing.models.audits;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.auditBuilder.AuditCategoriesBuilder;
import com.picsauditing.auditBuilder.AuditCategoryRuleCache;
import com.picsauditing.jpa.entities.*;

import java.util.HashSet;
import java.util.Set;

/**
 * This model determines if an audit can be edited
 */
public class AuditEditModel {
    AuditCategory category;
	ContractorAudit conAudit;
	Permissions permissions;
    AuditCategoryRuleCache auditCategoryRuleCache;

    public boolean isCanEditCategory(AuditCategory category, ContractorAudit conAudit, Permissions permissions, AuditCategoryRuleCache auditCategoryRuleCache) {
        this.category= category;
        this.conAudit= conAudit;
        this.permissions = permissions;
        this.auditCategoryRuleCache = auditCategoryRuleCache;

        boolean result = true;

		/*
         * This is hardcoded for the HSE Competency Review. Contractors are only
		 * allowed to edit the sub-categories of these audits.
		 */
        if (permissions.isContractor() && category.getAuditType().getId() == AuditType.HSE_COMPETENCY
                && category.getParent() != null) {
            result = false;
        } else if (permissions.isContractor() && conAudit.getAuditType().isAnnualAddendum()) {
            result = contractorCanEditAnnualUpdate(category);
        } else if (permissions.isContractor() && conAudit.getAuditType().isPqf()) {
            result = contractorCanEditPqf(category);
        } else if (conAudit.getAuditType().getClassType().isPolicy()) {
            result = userCanEditInsurancePolicy(category, result);
        }
        /*
		 * Non-policy audits do not have restrictions on a per category basis.
		 * If the user can see the category and has the 'Edit' view, they are
		 * allowed to edit the audit.
		 */
        else if (!conAudit.getAuditType().getClassType().isPolicy()) {
            result = true;
        }

        return result;
    }

    private boolean contractorCanEditPqf(AuditCategory category) {
        boolean result;
        boolean atLeastOneCaoAfterSubmitted = false;
        for (ContractorAuditOperator cao: conAudit.getOperatorsVisible()) {
            if (cao.getStatus().after(AuditStatus.Resubmitted)) {
                atLeastOneCaoAfterSubmitted = true;
                break;
            }
        }

        if (atLeastOneCaoAfterSubmitted) {
            AuditCatData foundAuditCatData = null;
            for (AuditCatData auditCatData: conAudit.getCategories()) {
                if (auditCatData.getCategory().equals(category)) {
                    foundAuditCatData = auditCatData;
                }
            }
            // Contractor should be able to edit a category if it's not completely filled out, regardless of the cao status
            if (foundAuditCatData != null && foundAuditCatData.getRequiredCompleted() - foundAuditCatData.getNumRequired() < 0) {
                result = true;
            } else {
                result = false;
            }
        } else {
            result = true;
        }
        return result;
    }

    private boolean userCanEditInsurancePolicy(AuditCategory category, boolean result) {
        if (permissions.isAdmin()) {
            return true;
        }
    /*
     * Single CAO audits (in this case, policies) are editable by the owners
     * of that CAO
     */
        if (conAudit.getOperatorsVisible().size() == 1
                && conAudit.getOperatorsVisible().get(0).hasCaop(permissions.getAccountId())) {
            result = true;
        }

            /*
             * Contractors are only allowed to edit the limits and policy
             * information BEFORE the policy is submitted. Once the policy is
             * submitted we "lock" down these categories to prevent contractors from
             * changing them.
             *
             * Contractors are still allowed to edit the attached certificates of
             * this policy. For example, when the contractors add a new facility
             * they should be allowed to add their certificate to that operator's
             * insurance category.
             */
        if (category.isPolicyInformationCategory() || category.isPolicyLimitsCategory()) {
            if (conAudit.hasCaoStatusAfter(AuditStatus.Incomplete, true) && !permissions.isAdmin()) {
                result = false;
            }
        }

        // check policy category fro cao after incomplete
        AuditCategoriesBuilder builder = new AuditCategoriesBuilder(auditCategoryRuleCache, conAudit.getContractorAccount());
        for (ContractorAuditOperator cao : conAudit.getOperatorsVisible()) {
            setCategoryBuilderToSpecificCao(builder, cao);
            if (builder.isCategoryApplicable(category, cao) && cao.getStatus().after(AuditStatus.Incomplete)) {
                result = false;
            }
        }
        return result;
    }

    private boolean contractorCanEditAnnualUpdate(AuditCategory category) {
        AuditCategoriesBuilder builder = new AuditCategoriesBuilder(auditCategoryRuleCache,
                conAudit.getContractorAccount());
        for (ContractorAuditOperator cao : conAudit.getOperators()) {
            setCategoryBuilderToSpecificCao(builder, cao);

            if (cao.getStatus().before(AuditStatus.Complete) && builder.isCategoryApplicable(category, cao)) {
                return true;
            }
        }

        return false;
    }

    private void setCategoryBuilderToSpecificCao(AuditCategoriesBuilder builder, ContractorAuditOperator cao) {
        Set<OperatorAccount> operators = new HashSet<OperatorAccount>();
        for (ContractorAuditOperatorPermission caop : cao.getCaoPermissions()) {
            operators.add(caop.getOperator());
        }
        builder.calculate(conAudit, operators);
    }

    public boolean isCanEditAudit(ContractorAudit conAudit, Permissions permissions) {
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
            boolean nothingPendingOrIncomplete = true;
			for (ContractorAuditOperator cao : conAudit.getOperatorsVisible()) {
				if (cao.getStatus().before(AuditStatus.Submitted) || cao.getStatus().equals(AuditStatus.Resubmit)) {
                    nothingPendingOrIncomplete = false;
					break;
				}
			}
            if (nothingPendingOrIncomplete) {
                canEdit = false;
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
