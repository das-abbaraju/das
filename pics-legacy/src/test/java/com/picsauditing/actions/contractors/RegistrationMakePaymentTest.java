package com.picsauditing.actions.contractors;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.picsauditing.EntityFactory;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.audits.OpenAuditsMailer;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.util.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PicsActionTest;
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
    @Mock
    private ContractorAccountDAO contractorAccountDao;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		registrationMakePayment = new RegistrationMakePayment();
		super.setUp(registrationMakePayment);

		nonCorporateOperators = new ArrayList<ContractorOperator>();
		when(contractor.getId()).thenReturn(1);

        Whitebox.setInternalState(registrationMakePayment, "contractorAccountDao", contractorAccountDao);
		Whitebox.setInternalState(registrationMakePayment, "billingNoteModel", billingNoteModel);
	}

    @Test
    public void testSetWorkingStatusOfAutoApprovesRelationship() throws Exception {
        OperatorAccount operator = EntityFactory.makeOperator();
        operator.setAutoApproveRelationships(true);

        ContractorOperator co1 = new ContractorOperator();
        ContractorOperator co2 = new ContractorOperator();

        co1.setOperatorAccount(operator);
        co1.setWorkStatus(ApprovalStatus.P);
        co2.setOperatorAccount(operator);
        co2.setWorkStatus(ApprovalStatus.D);

        List<ContractorOperator> list = new ArrayList<>();
        list.add(co1);
        list.add(co2);
        when(contractor.getOperators()).thenReturn(list);

        Whitebox.setInternalState(registrationMakePayment, "contractor", contractor);
        Whitebox.invokeMethod(registrationMakePayment, "updateWorkStatusForAutoApproveRelationships");

        assertTrue(co1.getWorkStatus().isYes());
        assertFalse(co2.getWorkStatus().isYes());
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
		verify(translationService).hasKey("ContractorRegistrationFinish.message.SelectService", Locale.ENGLISH);

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
		verify(translationService).hasKey("ContractorRegistrationFinish.message.AddFacility", Locale.ENGLISH);
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

    @Test
    public void testCreditCardTypes_UKNoAmex() throws Exception {
        Country uk = new Country();
        uk.setCurrency(Currency.GBP);
        uk.setIsoCode(Country.UK_ISO_CODE);
        when(contractor.getCountry()).thenReturn(uk);
        when(contractorAccountDao.find(1)).thenReturn(contractor);
        when(contractorAccountDao.isContained(contractor)).thenReturn(true);
        when(permissions.hasPermission(OpPerms.AllContractors)).thenReturn(true);
        registrationMakePayment.setId(1);

        List<String> creditcardTypes = registrationMakePayment.getCreditCardTypes();
        String creditCards = Strings.implode(creditcardTypes);

        Assert.assertNotContains("American Express", creditCards);
    }

    @Test
    public void testCreditCardTypes_ZANoAmex() throws Exception {
        Country uk = new Country();
        uk.setCurrency(Currency.ZAR);
        uk.setIsoCode(Country.SOUTH_AFRICA_ISO_CODE);
        when(contractor.getCountry()).thenReturn(uk);
        when(contractorAccountDao.find(1)).thenReturn(contractor);
        when(contractorAccountDao.isContained(contractor)).thenReturn(true);
        when(permissions.hasPermission(OpPerms.AllContractors)).thenReturn(true);
        registrationMakePayment.setId(1);

        List<String> creditcardTypes = registrationMakePayment.getCreditCardTypes();
        String creditCards = Strings.implode(creditcardTypes);

        Assert.assertNotContains("American Express", creditCards);
    }
}
