package com.picsauditing.service.employeeGuard;

import com.picsauditing.jpa.entities.*;
import com.picsauditing.provisioning.ProductSubscriptionService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class EmployeeGuardRulesServiceTest {

    public static final String NOT_EMPLOYEEGUARD = "not employeeguard";
    public static final String ANOTHER_AUDIT = "another audit";
    @Mock
	private ProductSubscriptionService productSubscriptionService;

	private ContractorAccount contractor;
	private EmployeeGuardRulesService employeeGuardRulesService;
    private List<ContractorAudit> contractorAuditList;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		employeeGuardRulesService = new EmployeeGuardRulesService();
        contractorAuditList = new ArrayList<>();
        contractor = ContractorAccount.builder()
                .audits(contractorAuditList)
                .build();

		Whitebox.setInternalState(employeeGuardRulesService, "productSubscriptionService", productSubscriptionService);
	}

    @Test
    public void testEmployeeGuardRules_HasEmployeeGuardAudit_WithVisibleCao(){
        contractorAuditList.add(
                ContractorAudit.builder()
                        .auditType(AuditType.builder()
                                .slug(EmployeeGuardRulesService.EMPLOYEE_GUARD_AUDIT_SLUG)
                                .build())
                        .cao(ContractorAuditOperator.builder()
                                .visible()
                                .build())
                        .build()
        );

        employeeGuardRulesService.runEmployeeGuardRules(contractor);

        assertTrue(contractor.isHasEmployeeGuard());
        verify(productSubscriptionService).addEmployeeGUARD(contractor.getId());
        verify(productSubscriptionService, never()).removeEmployeeGUARD(contractor.getId());
    }

    @Test
    public void testEmployeeGuardRules_HasEmployeeGuardAudit_WithInvisibleCao(){
        contractorAuditList.add(
                ContractorAudit.builder()
                        .auditType(AuditType.builder()
                                .slug(EmployeeGuardRulesService.EMPLOYEE_GUARD_AUDIT_SLUG)
                                .build())
                        .cao(ContractorAuditOperator.builder()
                                .invisible()
                                .build())
                        .build()
        );

        employeeGuardRulesService.runEmployeeGuardRules(contractor);

        assertFalse(contractor.isHasEmployeeGuard());
        verify(productSubscriptionService, never()).addEmployeeGUARD(contractor.getId());
        verify(productSubscriptionService).removeEmployeeGUARD(contractor.getId());
    }

    @Test
    public void testEmployeeGuardRules_DoesNotHaveEmployeeGuardAudit(){
        contractorAuditList.add(
                ContractorAudit.builder()
                        .auditType(AuditType.builder()
                                .slug(NOT_EMPLOYEEGUARD)
                                .build())
                        .cao(ContractorAuditOperator.builder()
                                .visible()
                                .build())
                        .build()
        );

        employeeGuardRulesService.runEmployeeGuardRules(contractor);

        assertFalse(contractor.isHasEmployeeGuard());
        verify(productSubscriptionService, never()).addEmployeeGUARD(contractor.getId());
        verify(productSubscriptionService).removeEmployeeGUARD(contractor.getId());
    }

    @Test
    public void testEmployeeGuardRules_ContractorAuditIsNull(){
        contractorAuditList.add(null);

        employeeGuardRulesService.runEmployeeGuardRules(contractor);

        assertFalse(contractor.isHasEmployeeGuard());
        verify(productSubscriptionService, never()).addEmployeeGUARD(contractor.getId());
        verify(productSubscriptionService).removeEmployeeGUARD(contractor.getId());
    }

    @Test
    public void testEmployeeGuardRules_HasEmployeeGuardAudit_AndOthers(){
        contractorAuditList.add(
                ContractorAudit.builder()
                        .auditType(AuditType.builder()
                                .slug(NOT_EMPLOYEEGUARD)
                                .build())
                        .cao(ContractorAuditOperator.builder()
                                .visible()
                                .build())
                        .build()
        );
        contractorAuditList.add(
                ContractorAudit.builder()
                        .auditType(AuditType.builder()
                                .slug(EmployeeGuardRulesService.EMPLOYEE_GUARD_AUDIT_SLUG)
                                .build())
                        .cao(ContractorAuditOperator.builder()
                                .visible()
                                .build())
                        .build()
        );
        contractorAuditList.add(
                ContractorAudit.builder()
                        .auditType(AuditType.builder()
                                .slug(ANOTHER_AUDIT)
                                .build())
                        .cao(ContractorAuditOperator.builder()
                                .visible()
                                .build())
                        .build()
        );

        employeeGuardRulesService.runEmployeeGuardRules(contractor);

        assertTrue(contractor.isHasEmployeeGuard());
        verify(productSubscriptionService).addEmployeeGUARD(contractor.getId());
        verify(productSubscriptionService, never()).removeEmployeeGUARD(contractor.getId());
    }
}
