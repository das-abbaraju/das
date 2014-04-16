package com.picsauditing.service.employeeGuard;

import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.provisioning.ProductSubscriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class EmployeeGuardRulesService {

	public static final String SET_FIELD_LOG_MESSAGE = "Setting hasEmployeeGuard for contractor id: {0}";

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

    private boolean requiresEmployeeGuard(ContractorAccount contractor) {
        if (contractor.isOnsiteServices()) {
            for (OperatorAccount operator: contractor.getOperatorAndCorporateAccounts()) {
                if (operatorRequiresEmployeeGuard(operator)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean operatorRequiresEmployeeGuard(OperatorAccount operator) {
        if (operator.isRequiresEmployeeGuard()) {
            return true;
        } else {
            if (parentsRequireEmployeeGuard(operator)) {
                return true;
            }

            return false;
        }
    }

    private boolean parentsRequireEmployeeGuard(OperatorAccount operator) {
        for (Facility facility: operator.getCorporateFacilities()) {
            if (facility.getCorporate().isRequiresEmployeeGuard()) {
                return true;
            }
        }

        return false;
    }
}
