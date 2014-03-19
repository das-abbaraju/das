package com.picsauditing.service.employeeGuard;

import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.OperatorAccount;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.logging.Logger;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EmployeeGuardRulesServiceTest {
    @Mock
    private Logger logger;

    private ContractorAccount contractor;
    private EmployeeGuardRulesService employeeGuardRulesService;

    @Before
    public void setup() {
        employeeGuardRulesService = new EmployeeGuardRulesService();
    }

    @Test
    public void testRunEmployeeGuardRules_OnSiteServices_WorksForEmployeeGuardOperator() {
        contractor = ContractorAccount.builder()
                .onSiteServices()
                .operator(OperatorAccount.builder()
                        .requiresEmployeeGuard()
                        .build())
                .build();

        employeeGuardRulesService.runEmployeeGuardRules(contractor);

        assertTrue(contractor.isHasEmployeeGuard());
    }

    @Test
    public void testRunEmployeeGuardRules_OnSiteServices_WorksForEmployeeGuardOperatorAndAnother() {
        contractor = ContractorAccount.builder()
                .onSiteServices()
                .operator(OperatorAccount.builder()
                        .doesNotRequireEmployeeGuard()
                        .build())
                .operator(OperatorAccount.builder()
                        .requiresEmployeeGuard()
                        .build())
                .build();

        employeeGuardRulesService.runEmployeeGuardRules(contractor);

        assertTrue(contractor.isHasEmployeeGuard());
    }

    @Test
    public void testRunEmployeeGuardRules_OnSiteServices_DoesNotWorkForEmployeeGuardOperator() {
        contractor = ContractorAccount.builder()
                .onSiteServices()
                .operator(OperatorAccount.builder()
                        .doesNotRequireEmployeeGuard()
                        .build())
                .build();

        employeeGuardRulesService.runEmployeeGuardRules(contractor);

        assertFalse(contractor.isHasEmployeeGuard());
    }

    @Test
    public void testRunEmployeeGuardRules_NotOnSiteServices_WorksForEmployeeGuardOperator() {
        contractor = ContractorAccount.builder()
                .doesNotPerformOnSiteServices()
                .operator(OperatorAccount.builder()
                        .doesNotRequireEmployeeGuard()
                        .build())
                .build();

        employeeGuardRulesService.runEmployeeGuardRules(contractor);

        assertFalse(contractor.isHasEmployeeGuard());
    }

    @Test
    public void testRunEmployeeGuardRules_NotOnSiteServices_DoesNotWorkForEmployeeGuardOperator() {
        contractor = ContractorAccount.builder()
                .onSiteServices()
                .operator(OperatorAccount.builder()
                        .doesNotRequireEmployeeGuard()
                        .build())
                .build();

        employeeGuardRulesService.runEmployeeGuardRules(contractor);

        assertFalse(contractor.isHasEmployeeGuard());
    }

    @Test
    public void testRunEmployeeGuardRules_OnsiteServices_ParentOperatorRequiresEmployeeGuard() {
        contractor = ContractorAccount.builder()
                .onSiteServices()
                .operator(OperatorAccount.builder()
                        .doesNotRequireEmployeeGuard()
                        .parentAccount(OperatorAccount.builder()
                                .requiresEmployeeGuard()
                                .build())
                        .build())
                .build();

        employeeGuardRulesService.runEmployeeGuardRules(contractor);

        assertTrue(contractor.isHasEmployeeGuard());
    }
}
