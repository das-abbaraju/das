package com.picsauditing.jpa.entities;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ContractorRegistrationStepTest {

	private List<OperatorAccount> generalContractors;
	private List<ContractorOperator> operators;

	@Mock
	private ContractorAccount contractor;
	@Mock
	private OperatorAccount operator;
	@Mock
	private ContractorOperator contractorOperator;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		generalContractors = new ArrayList<OperatorAccount>();
		generalContractors.add(operator);

		operators = new ArrayList<ContractorOperator>();
		operators.add(contractorOperator);
		when(contractorOperator.getOperatorAccount()).thenReturn(operator);
		when(operator.isOperator()).thenReturn(true);

		when(contractor.getId()).thenReturn(999);
		when(contractor.getGeneralContractorOperatorAccounts()).thenReturn(generalContractors);
	}

	@Test
	public void testGetStep_NullContractorIsRegister() {
		contractor = null;
		assertNull(contractor);
		assertEquals(ContractorRegistrationStep.Register, ContractorRegistrationStep.getStep(contractor));
	}

	@Test
	public void testGetStep_IdOfZeroIsRegister() {
		when(contractor.getId()).thenReturn(0);
		assertEquals(ContractorRegistrationStep.Register, ContractorRegistrationStep.getStep(contractor));
	}

	@Test
	public void testGetStep_containsAtLeastOneClientSiteForGCFree_ReturnsClients() {
		when(contractor.getStatus()).thenReturn(AccountStatus.Pending);
		when(operator.getDoContractorsPay()).thenReturn("No");
		assertEquals(ContractorRegistrationStep.Clients, ContractorRegistrationStep.getStep(contractor));
	}

	@Test
	public void testGetStep_NoOperators_ReturnsClients() {
		when(contractor.getStatus()).thenReturn(AccountStatus.Pending);
		when(contractor.getGeneralContractorOperatorAccounts()).thenReturn(new ArrayList<OperatorAccount>());
		when(contractor.getOperatorAccounts()).thenReturn(new ArrayList<OperatorAccount>());
		assertEquals(ContractorRegistrationStep.Clients, ContractorRegistrationStep.getStep(contractor));
	}

	@Test
	public void testGetStep_DemoReturnsDone() {
		when(contractor.getStatus()).thenReturn(AccountStatus.Demo);
		assertEquals(ContractorRegistrationStep.Done, ContractorRegistrationStep.getStep(contractor));
	}

	@Test
	public void testGetStep_ActiveReturnsDone() {
		when(contractor.getStatus()).thenReturn(AccountStatus.Active);
		assertEquals(ContractorRegistrationStep.Done, ContractorRegistrationStep.getStep(contractor));
	}

	@Test
	public void testGetStep_NoSafetyRishAndNotOnlyMaterialSupplierAndNotTransportationReturnsRisk() {
		when(contractor.getStatus()).thenReturn(AccountStatus.Pending);
		// get past containsOperator block
		when(contractor.getOperators()).thenReturn(operators);
		// get past containsAtLeastOneClientSiteForGCFree block
		when(operator.getDoContractorsPay()).thenReturn("Yes");
		// get past first risk block
		when(contractor.getSafetyRisk()).thenReturn(LowMedHigh.None);
		when(contractor.isMaterialSupplierOnly()).thenReturn(false);
		when(contractor.isTransportationServices()).thenReturn(false);

		assertEquals(ContractorRegistrationStep.Risk, ContractorRegistrationStep.getStep(contractor));
	}

	@Test
	public void testGetStep_NoSafetyRishAndIsOnlyMaterialSupplierAndNotTransportationReturnsPayment() {
		when(contractor.getStatus()).thenReturn(AccountStatus.Pending);
		// get past containsOperator block
		when(contractor.getOperators()).thenReturn(operators);
		// get past containsAtLeastOneClientSiteForGCFree block
		when(operator.getDoContractorsPay()).thenReturn("Yes");
		// get past first risk block
		when(contractor.getSafetyRisk()).thenReturn(LowMedHigh.None);
		when(contractor.isMaterialSupplierOnly()).thenReturn(true);
		when(contractor.isTransportationServices()).thenReturn(false);

		assertEquals(ContractorRegistrationStep.Payment, ContractorRegistrationStep.getStep(contractor));
	}

	@Test
	public void testGetStep_NoSafetyRishAndNotOnlyMaterialSupplierAndTransportationReturnsPayment() {
		when(contractor.getStatus()).thenReturn(AccountStatus.Pending);
		// get past containsOperator block
		when(contractor.getOperators()).thenReturn(operators);
		// get past containsAtLeastOneClientSiteForGCFree block
		when(operator.getDoContractorsPay()).thenReturn("Yes");
		// get past first risk block
		when(contractor.getSafetyRisk()).thenReturn(LowMedHigh.None);
		when(contractor.isMaterialSupplierOnly()).thenReturn(false);
		when(contractor.isTransportationServices()).thenReturn(true);

		assertEquals(ContractorRegistrationStep.Payment, ContractorRegistrationStep.getStep(contractor));
	}
	
	@Test
	public void testGetStep_MaterialSupplierAndNoProductRiskReturnsRisk() {
		when(contractor.getStatus()).thenReturn(AccountStatus.Pending);
		// get past containsOperator block
		when(contractor.getOperators()).thenReturn(operators);
		// get past containsAtLeastOneClientSiteForGCFree block
		when(operator.getDoContractorsPay()).thenReturn("Yes");
		// get past first risk block
		when(contractor.getSafetyRisk()).thenReturn(LowMedHigh.Low);
		when(contractor.isMaterialSupplier()).thenReturn(true);
		when(contractor.getProductRisk()).thenReturn(LowMedHigh.None);

		assertEquals(ContractorRegistrationStep.Risk, ContractorRegistrationStep.getStep(contractor));
	}

	@Test
	public void testGetStep_MaterialSupplierAndHasProductRiskReturnsPayment() {
		when(contractor.getStatus()).thenReturn(AccountStatus.Pending);
		// get past containsOperator block
		when(contractor.getOperators()).thenReturn(operators);
		// get past containsAtLeastOneClientSiteForGCFree block
		when(operator.getDoContractorsPay()).thenReturn("Yes");
		// get past first risk block
		when(contractor.getSafetyRisk()).thenReturn(LowMedHigh.Low);
		when(contractor.isMaterialSupplier()).thenReturn(true);
		when(contractor.getProductRisk()).thenReturn(LowMedHigh.Low);

		assertEquals(ContractorRegistrationStep.Payment, ContractorRegistrationStep.getStep(contractor));
	}
	
	@Test
	public void testGetStep_NotFreeClientsRiskCompleteButDeactivatedNeedsPayment() {
		when(contractor.getStatus()).thenReturn(AccountStatus.Deactivated);
		// get past containsOperator block
		when(contractor.getOperators()).thenReturn(operators);
		// get past containsAtLeastOneClientSiteForGCFree block
		when(operator.getDoContractorsPay()).thenReturn("Yes");
		// get past first risk block
		when(contractor.getSafetyRisk()).thenReturn(LowMedHigh.Low);
		when(contractor.isMaterialSupplier()).thenReturn(false);
		when(contractor.isHasFreeMembership()).thenReturn(false);

		assertEquals(ContractorRegistrationStep.Payment, ContractorRegistrationStep.getStep(contractor));
	}

	@Test
	public void testGetStep_NotFreeClientsRiskCompleteButPendingNeedsPayment() {
		when(contractor.getStatus()).thenReturn(AccountStatus.Pending);
		// get past containsOperator block
		when(contractor.getOperators()).thenReturn(operators);
		// get past containsAtLeastOneClientSiteForGCFree block
		when(operator.getDoContractorsPay()).thenReturn("Yes");
		// get past first risk block
		when(contractor.getSafetyRisk()).thenReturn(LowMedHigh.Low);
		when(contractor.isMaterialSupplier()).thenReturn(false);
		when(contractor.isHasFreeMembership()).thenReturn(false);

		assertEquals(ContractorRegistrationStep.Payment, ContractorRegistrationStep.getStep(contractor));
	}

	@Test
	public void testGetStep_FreeClientsRiskCompleteButPendingIsDone() {
		when(contractor.getStatus()).thenReturn(AccountStatus.Pending);
		// get past containsOperator block
		when(contractor.getOperators()).thenReturn(operators);
		// get past containsAtLeastOneClientSiteForGCFree block
		when(operator.getDoContractorsPay()).thenReturn("Yes");
		// get past first risk block
		when(contractor.getSafetyRisk()).thenReturn(LowMedHigh.Low);
		when(contractor.isMaterialSupplier()).thenReturn(false);
		when(contractor.isHasFreeMembership()).thenReturn(true);

		assertEquals(ContractorRegistrationStep.Done, ContractorRegistrationStep.getStep(contractor));
	}

	@Test
	public void testGetStep_NotFreeClientsRiskComplete_RequestedIsDone() {
		when(contractor.getStatus()).thenReturn(AccountStatus.Requested);
		// get past containsOperator block
		when(contractor.getOperators()).thenReturn(operators);
		// get past containsAtLeastOneClientSiteForGCFree block
		when(operator.getDoContractorsPay()).thenReturn("Yes");
		// get past first risk block
		when(contractor.getSafetyRisk()).thenReturn(LowMedHigh.Low);
		when(contractor.isMaterialSupplier()).thenReturn(false);
		when(contractor.isHasFreeMembership()).thenReturn(false);

		assertEquals(ContractorRegistrationStep.Payment, ContractorRegistrationStep.getStep(contractor));
	}

	@Test
	public void testGetUrl() {
		assertEquals("Registration.action", ContractorRegistrationStep.Register.getUrl());
		assertEquals("RegistrationAddClientSite.action", ContractorRegistrationStep.Clients.getUrl());
		assertEquals("RegistrationServiceEvaluation.action", ContractorRegistrationStep.Risk.getUrl());
		assertEquals("RegistrationMakePayment.action", ContractorRegistrationStep.Payment.getUrl());
		assertEquals("ContractorView.action", ContractorRegistrationStep.Done.getUrl());
	}

	@Test
	public void testIsDone() {
		assertTrue(ContractorRegistrationStep.Done.isDone());
		assertFalse(ContractorRegistrationStep.Payment.isDone());
	}

	@Test
	public void testIsHasNext() {
		assertTrue(ContractorRegistrationStep.Register.isHasNext());
		assertTrue(ContractorRegistrationStep.Clients.isHasNext());
		assertTrue(ContractorRegistrationStep.Risk.isHasNext());
		assertTrue(ContractorRegistrationStep.Payment.isHasNext());
		assertTrue(ContractorRegistrationStep.Payment.isHasNext());
		assertFalse(ContractorRegistrationStep.Done.isHasNext());
	}

	@Test
	public void testIsHasPrevious() {
		assertFalse(ContractorRegistrationStep.Register.isHasPrevious());
		assertFalse(ContractorRegistrationStep.Clients.isHasPrevious());
		assertTrue(ContractorRegistrationStep.Risk.isHasPrevious());
		assertTrue(ContractorRegistrationStep.Payment.isHasPrevious());
		assertTrue(ContractorRegistrationStep.Done.isHasPrevious());
	}

}
