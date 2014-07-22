package com.picsauditing.service.account;

import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.SlickEnhancedContractorOperatorDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.util.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class WaitingOnService {
    private SlickEnhancedContractorOperatorDAO contractorOperatorDAO;
    private Map<FlagCriteria, List<FlagCriteriaOperator>> operatorCriteria = null;

    private final Logger logger = LoggerFactory.getLogger(WaitingOnService.class);

    public WaitingOn calculateWaitingOn(ContractorOperator co) {
        ContractorAccount contractor = co.getContractorAccount();
        OperatorAccount operator = co.getOperatorAccount();
        setOperatorCriteria(operator.getFlagAuditCriteriaInherited());

        if (!contractor.isMaterialSupplierOnly() && contractor.getSafetyRisk() == null) {
            return WaitingOn.Contractor;
        }

        if (contractor.isMaterialSupplier() && contractor.getProductRisk() == null) {
            return WaitingOn.Contractor;
        }

        if (!contractor.getStatus().isActiveOrDemo()) {
            return WaitingOn.Contractor; // This contractor is delinquent
        }

        // If Bid Only Account
        if (contractor.getAccountLevel().isBidOnly()) {
            return WaitingOn.Operator;
        }

        // Operator Relationship Approval
        if (!operator.isAutoApproveRelationships()) {
            if (contractorOperatorDAO().workStatusIsPending(co)) {
                // Operator needs to approve/reject this contractor
                return WaitingOn.Operator;
            }
            if (contractorOperatorDAO().workStatusIsRejected(co)) {
                // Operator has already rejected this
                // contractor, and there's nothing else
                // they can do
                return WaitingOn.None;
            }
        }

        // Billing
        if (contractor.isPaymentOverdue())
        {
            return WaitingOn.Contractor; // The contractor has an unpaid
            // invoice due
        }

        return checkOtherParties(contractor, operator);
    }

    private WaitingOn checkOtherParties(ContractorAccount contractor, OperatorAccount operator) {
        // If waiting on contractor, immediately exit, otherwise track the
        // other parties
        boolean waitingOnPics = false;
        boolean waitingOnOperator = false;

        for (FlagCriteria key : operatorCriteria.keySet()) {
            FlagCriteriaOperator fOperator = operatorCriteria.get(key).get(0);
            if (!fOperator.getFlag().equals(FlagColor.Green)) {
                for (ContractorAudit conAudit : contractor.getAudits()) {
                    if (key.getAuditType().equals(conAudit.getAuditType())) {
                        if (!conAudit.isExpired()) {
                            // There could be multiple audits for the same
                            // operator
                            for (ContractorAuditOperator cao : getCaosForOperator(conAudit, operator)) {
                                if (cao.getStatus().before(AuditStatus.Submitted)) {
                                    if (conAudit.getAuditType().isCanContractorEdit()) {
                                        return WaitingOn.Contractor;
                                    }
                                    OpPerms editPerm = conAudit.getAuditType().getEditPermission();
                                    if (conAudit.getAuditType().getEditPermission() != null) {
                                        if (editPerm.isForOperator()) {
                                            waitingOnOperator = true;
                                        } else {
                                            waitingOnPics = true;
                                        }
                                    } else {
                                        // Assuming that a null permission means
                                        // "Only PICS" can edit
                                        if (conAudit.getAuditType().isImplementation()) {
                                            Date scheduledDate = conAudit.getScheduledDate();
                                            if (scheduledDate == null) {
                                                return WaitingOn.Contractor;
                                            } else {
                                                return WaitingOn.None;
                                            }
                                        } else {
                                            waitingOnPics = true;
                                        }
                                    }
                                } else {
                                    AuditStatus requiredStatus = key.getRequiredStatus();

                                    if (cao.getStatus().before(requiredStatus)) {
                                        if (cao.getStatus().isComplete()) {
                                            waitingOnOperator = true;
                                        } else if (conAudit.getAuditType().getId() == AuditType.IMPLEMENTATION_AUDIT) {
                                            // either needs to schedule the
                                            // audit or
                                            // close out RQs
                                            return WaitingOn.Contractor;
                                        } else if (conAudit.getAuditType().getId() == AuditType.MANUAL_AUDIT
                                                && cao.getStatus().isSubmitted()) {
                                            // contractor needs to close out RQs
                                            return WaitingOn.Contractor;
                                        } else {
                                            waitingOnPics = true;
                                        }
                                    } else {
                                        if (conAudit.getAuditType().isImplementation()
                                                && cao.getPercentVerified() != 100) {
                                            return WaitingOn.Contractor;
                                        }
                                    }
                                }
                            }
                        }
                    }
                } // for
            }
        }
        if (waitingOnPics) {
            return WaitingOn.PICS;
        }
        if (waitingOnOperator) {
            // only show the operator if contractor and pics are all done
            return WaitingOn.Operator;
        }

        return WaitingOn.None;
    }

    private SlickEnhancedContractorOperatorDAO contractorOperatorDAO() {
        if (contractorOperatorDAO == null) {
            return SpringUtils.getBean(SpringUtils.CONTRACTOR_OPERATOR_DAO);
        }
        return contractorOperatorDAO ;
    }

    private void setOperatorCriteria(Collection<FlagCriteriaOperator> list) {
        operatorCriteria = new HashMap<FlagCriteria, List<FlagCriteriaOperator>>();
        for (FlagCriteriaOperator value : list) {
            if (operatorCriteria.get(value.getCriteria()) == null) {
                operatorCriteria.put(value.getCriteria(), new ArrayList<FlagCriteriaOperator>());
            }

            operatorCriteria.get(value.getCriteria()).add(value);
        }
    }

    private List<ContractorAuditOperator> getCaosForOperator(ContractorAudit conAudit, OperatorAccount operator) {
        List<ContractorAuditOperator> caos = new ArrayList<ContractorAuditOperator>();

        for (ContractorAuditOperator cao : conAudit.getOperators()) {
            for (ContractorAuditOperatorPermission caop : cao.getCaoPermissions()) {
                if (caop.getOperator().equals(operator) && cao.isVisible()) {
                    caos.add(cao);
                }
            }
        }

        if (caos.size() > 1) {
            logger.warn("WARNING: Found " + caos.size() + " matching caos for " + operator.toString()
                    + " on auditID = " + conAudit.getId());
        }

        return caos;
    }

}
