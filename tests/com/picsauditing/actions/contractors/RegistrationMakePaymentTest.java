package com.picsauditing.actions.contractors;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PicsActionTest;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.model.billing.BillingNoteModel;
import com.picsauditing.util.Strings;

public class RegistrationMakePaymentTest extends PicsActionTest {
	private RegistrationMakePayment registrationMakePayment;
	private List<ContractorOperator> nonCorporateOperators;

	@Mock
	private ContractorAccount contractor;
	@Mock
	private ContractorOperator contractorOperator;
	@Mock
	private BillingNoteModel billingNoteModel;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		registrationMakePayment = new RegistrationMakePayment();
		super.setUp(registrationMakePayment);

		nonCorporateOperators = new ArrayList<ContractorOperator>();
		when(contractor.getId()).thenReturn(1);

		Whitebox.setInternalState(registrationMakePayment, "billingNoteModel", billingNoteModel);
	}

	// if they have not specified a safety risk
	// and they are not only a material supplier or transportation services
	// make them specify one
	@Test
	public void testContractorRiskUrl_RequiresServiceEval() throws Exception {
		when(contractor.getSafetyRisk()).thenReturn(LowMedHigh.None);
		when(contractor.isMaterialSupplierOnly()).thenReturn(false);
		when(contractor.isTransportationServices()).thenReturn(false);

		registrationMakePayment.setContractor(contractor);

		String url = Whitebox.invokeMethod(registrationMakePayment, "contractorRiskUrl");

		assertThat(url, startsWith("RegistrationServiceEvaluation"));
		verify(i18nCache).hasKey("ContractorRegistrationFinish.message.SelectService", Locale.ENGLISH);

		when(contractor.isTransportationServices()).thenReturn(true);
		when(contractor.getProductRisk()).thenReturn(LowMedHigh.None);
		when(contractor.isMaterialSupplier()).thenReturn(true);
		url = Whitebox.invokeMethod(registrationMakePayment, "contractorRiskUrl");
		assertThat(url, startsWith("RegistrationServiceEvaluation"));

	}

	@Test
	public void testContractorRiskUrl_RequiresClientSite() throws Exception {
		when(contractor.getSafetyRisk()).thenReturn(LowMedHigh.Low);
		when(contractor.isMaterialSupplierOnly()).thenReturn(false);
		when(contractor.isTransportationServices()).thenReturn(false);
		when(contractor.getNonCorporateOperators()).thenReturn(nonCorporateOperators);

		registrationMakePayment.setContractor(contractor);

		String url = Whitebox.invokeMethod(registrationMakePayment, "contractorRiskUrl");

		assertThat(url, startsWith("AddClientSite"));
		verify(i18nCache).hasKey("ContractorRegistrationFinish.message.AddFacility", Locale.ENGLISH);
	}

	@Test
	public void testContractorRiskUrl_NoWorkflowStepsToComplete() throws Exception {
		when(contractor.getSafetyRisk()).thenReturn(LowMedHigh.Low);
		when(contractor.isMaterialSupplierOnly()).thenReturn(false);
		when(contractor.isTransportationServices()).thenReturn(false);
		nonCorporateOperators.add(contractorOperator);
		when(contractor.getNonCorporateOperators()).thenReturn(nonCorporateOperators);

		registrationMakePayment.setContractor(contractor);

		String url = Whitebox.invokeMethod(registrationMakePayment, "contractorRiskUrl");

		assertTrue(Strings.isEmpty(url));
	}
}
