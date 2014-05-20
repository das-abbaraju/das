package com.picsauditing.service.employeeGuard;

import com.picsauditing.auditBuilder.AuditTypesBuilder.AuditTypeDetail;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.provisioning.ProductSubscriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

public class EmployeeGuardRulesService {

	public static final String SET_FIELD_LOG_MESSAGE = "Setting hasEmployeeGuard for contractor id: {0}";
    public static final String EMPLOYEE_GUARD_AUDIT_SLUG= "employeeguard";
    private static final Logger logger = LoggerFactory.getLogger(EmployeeGuardRulesService.class);

    @Autowired
	private ProductSubscriptionService productSubscriptionService;

    public void runEmployeeGuardRules(ContractorAccount contractor) {
        int contractorId = contractor.getId();

		if (requiresEmployeeGuard(contractor)) {
			logger.info(SET_FIELD_LOG_MESSAGE, contractorId);

            contractor.setHasEmployeeGuard(true);
			productSubscriptionService.addEmployeeGUARD(contractorId);
        } else {
            contractor.setHasEmployeeGuard(false);
			productSubscriptionService.removeEmployeeGUARD(contractorId);
        }
    }

    public void runEmployeeGuardRules(ContractorAccount contractor, Set<AuditTypeDetail> auditTypeDetails) {
        int contractorId = contractor.getId();

        if (requiresEmployeeGuard(auditTypeDetails)) {
            logger.info(SET_FIELD_LOG_MESSAGE, contractorId);

            contractor.setHasEmployeeGuard(true);
            productSubscriptionService.addEmployeeGUARD(contractorId);
        } else {
            contractor.setHasEmployeeGuard(false);
            productSubscriptionService.removeEmployeeGUARD(contractorId);
        }
    }

    private boolean requiresEmployeeGuard(ContractorAccount contractor) {
        for (ContractorAudit contractorAudit: contractor.getAudits()) {
            if (isApplicableEmployeeGuardAudit(contractorAudit)) {
                return true;
            }
        }
        return false;
    }

    private boolean isApplicableEmployeeGuardAudit(ContractorAudit contractorAudit) {
        return contractorAudit != null && contractorAudit.getAuditType().getSlug().equals(EMPLOYEE_GUARD_AUDIT_SLUG)
                && contractorAudit.getOperatorsVisible().size() >= 1;
    }

    private boolean requiresEmployeeGuard(Set<AuditTypeDetail> auditTypeDetails) {
        for (AuditTypeDetail detail : auditTypeDetails) {
            if (detail.rule.getAuditType().getSlug().equals(EMPLOYEE_GUARD_AUDIT_SLUG)) {
                return true;
            }
        }
        return false;
    }
}
