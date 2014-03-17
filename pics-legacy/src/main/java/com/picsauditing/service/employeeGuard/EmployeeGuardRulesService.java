package com.picsauditing.service.employeeGuard;

import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.OperatorAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmployeeGuardRulesService {
    public static final String SET_FIELD_LOG_MESSAGE = "Setting hasEmployeeGuard for contractor id: {0}";
    private final Logger logger = LoggerFactory.getLogger(EmployeeGuardRulesService.class);

    public void runEmployeeGuardRules(ContractorAccount contractor) {
        if (requiresEmployeeGuard(contractor)) {
            logger.info(SET_FIELD_LOG_MESSAGE, contractor.getId());
            contractor.setHasEmployeeGuard(true);
        }
    }

    private boolean requiresEmployeeGuard(ContractorAccount contractor) {
        if (contractor.isOnsiteServices()) {
            for (OperatorAccount operator: contractor.getOperatorAndCorporateAccounts()) {
                if (operator.isRequiresEmployeeGuard()) {
                    return true;
                }
            }
        }
        return false;
    }
}
