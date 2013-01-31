package com.picsauditing.jpa.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ContractorRegistrationStepTest {

	ContractorAccount contractor;

	private OperatorAccount addOperator(int operatorId) {
		ContractorOperator co = new ContractorOperator();
		co.setContractorAccount(contractor);
		OperatorAccount oa = new OperatorAccount();
		oa.setId(operatorId);
		co.setOperatorAccount(oa);
		contractor.getOperators().add(co);
		return oa;
	}

	@Test
	public void testGetStep_null() {
		assertNull(contractor);
		assertEquals(ContractorRegistrationStep.Register, ContractorRegistrationStep.getStep(contractor));
	}

	@Test
	public void testGetStep_empty() {
		contractor = new ContractorAccount();
		assertEquals(ContractorRegistrationStep.Register, ContractorRegistrationStep.getStep(contractor));
	}

	@Test
	public void testGetStep_hasId() {
		contractor = new ContractorAccount();
		contractor.setId(999);
		assertEquals(ContractorRegistrationStep.Clients, ContractorRegistrationStep.getStep(contractor));
	}

	@Test
	public void testGetStep_demo() {
		contractor = new ContractorAccount();
		contractor.setId(999);
		contractor.setStatus(AccountStatus.Demo);
		assertEquals(ContractorRegistrationStep.Done, ContractorRegistrationStep.getStep(contractor));
	}

	@Test
	public void testGetStep_active() {
		contractor = new ContractorAccount();
		contractor.setId(999);
		contractor.setStatus(AccountStatus.Active);
		assertEquals(ContractorRegistrationStep.Done, ContractorRegistrationStep.getStep(contractor));
	}

	@Test
	public void testGetStep_pending() {
		contractor = new ContractorAccount();
		contractor.setId(999);
		contractor.setStatus(AccountStatus.Pending);
		assertEquals(ContractorRegistrationStep.Clients, ContractorRegistrationStep.getStep(contractor));
	}

	@Test
	public void testGetStep_isMaterialSupplier() {
		contractor = new ContractorAccount();
		contractor.setId(999);
		contractor.setMaterialSupplier(true);
		contractor.setProductRisk(LowMedHigh.None);
		addOperator(888); // anything but SUNCOR

		assertEquals(ContractorRegistrationStep.Risk, ContractorRegistrationStep.getStep(contractor));
	}

	@Test
	public void testGetStep_isMaterialSupplierOnly() {
		contractor = new ContractorAccount();
		contractor.setId(999);
		contractor.setSafetyRisk(LowMedHigh.None);
		addOperator(888); // anything but SUNCOR

		assertFalse(contractor.isMaterialSupplierOnly());
		assertEquals(ContractorRegistrationStep.Risk, ContractorRegistrationStep.getStep(contractor));
	}

	@Test
	public void testGetStep_multipleContractorTypes() {
		contractor = new ContractorAccount();
		contractor.setId(999);
		contractor.setSafetyRisk(LowMedHigh.None);
		contractor.getAccountTypes().add(ContractorType.Supplier);
		contractor.getAccountTypes().add(ContractorType.Onsite);
		addOperator(888); // anything but SUNCOR
		assertFalse(contractor.isMaterialSupplierOnly());
		assertEquals(ContractorRegistrationStep.Risk, ContractorRegistrationStep.getStep(contractor));
	}

	@Test
	public void testGetStep_needsPayment() {
		contractor = new ContractorAccount();
		contractor.setId(999);
		contractor.setSafetyRisk(LowMedHigh.Low);
		contractor.setProductRisk(LowMedHigh.Low);
		contractor.setPayingFacilities(1);
		addOperator(888); // anything but SUNCOR

		assertEquals(ContractorRegistrationStep.Payment, ContractorRegistrationStep.getStep(contractor));
	}

	@Test
	public void testGetStep_done() {
		contractor = new ContractorAccount();
		contractor.setId(999);
		contractor.setSafetyRisk(LowMedHigh.Low);
		contractor.setProductRisk(LowMedHigh.Low);
		contractor.setPayingFacilities(1);
		contractor.setCcOnFile(true);
		// Process changed for new contractor registration
		contractor.setStatus(AccountStatus.Active);
		addOperator(888); // anything but SUNCOR

		assertEquals(ContractorRegistrationStep.Done, ContractorRegistrationStep.getStep(contractor));
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
	}

	@Test
	public void testIsHasNext() {
		assertTrue(ContractorRegistrationStep.Register.isHasNext());
		assertTrue(ContractorRegistrationStep.Clients.isHasNext());
		assertTrue(ContractorRegistrationStep.Risk.isHasNext());
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
