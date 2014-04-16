package com.picsauditing.service.employeeGuard;

import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.provisioning.ProductSubscriptionService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

public class EmployeeGuardRulesServiceTest {

	@Mock
	private ProductSubscriptionService productSubscriptionService;

	private ContractorAccount contractor;
	private EmployeeGuardRulesService employeeGuardRulesService;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		employeeGuardRulesService = new EmployeeGuardRulesService();

		Whitebox.setInternalState(employeeGuardRulesService, "productSubscriptionService", productSubscriptionService);
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
		verify(productSubscriptionService).addEmployeeGUARD(contractor.getId());
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
		verify(productSubscriptionService).addEmployeeGUARD(contractor.getId());
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
		verify(productSubscriptionService).removeEmployeeGUARD(contractor.getId());
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
		verify(productSubscriptionService).removeEmployeeGUARD(contractor.getId());
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
		verify(productSubscriptionService).removeEmployeeGUARD(contractor.getId());
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
		verify(productSubscriptionService).addEmployeeGUARD(contractor.getId());
	}
}
