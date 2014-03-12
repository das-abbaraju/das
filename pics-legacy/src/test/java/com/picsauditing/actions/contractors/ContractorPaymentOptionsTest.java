package com.picsauditing.actions.contractors;

import com.opensymphony.xwork2.Action;
import com.picsauditing.PICS.TaxService;
import com.picsauditing.PicsActionTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.billing.BrainTree;
import com.picsauditing.braintree.CreditCard;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.util.PermissionToViewContractor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.math.BigDecimal;
import java.util.HashMap;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class ContractorPaymentOptionsTest extends PicsActionTest {
	private ContractorPaymentOptions contractorPaymentOptions;
	private static final String TEST_CREDIT_CARD_NUMBER = "4111111111111111";

	@Mock
	private ContractorAccountDAO contractorAccountDao;
	@Mock
	private AppPropertyDAO appPropDao;
	@Mock
	private ContractorAccount contractor;
	@Mock
	private PermissionToViewContractor permissionToViewContractor;
    @Mock
    private TaxService taxService;
    @Mock
    private BrainTree paymentService;

    @Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		setupMocks();
		contractorPaymentOptions = new ContractorPaymentOptions();
		setObjectUnderTestState(contractorPaymentOptions);

		PicsTestUtil.autowireDAOsFromDeclaredMocks(contractorPaymentOptions, this);

		when(permissions.isContractor()).thenReturn(true);
		when(permissions.getAccountId()).thenReturn(GREATER_THAN_ONE);
		when(contractorAccountDao.find(anyInt())).thenReturn(contractor);
		when(permissionToViewContractor.check(anyBoolean())).thenReturn(true);
		when(appPropDao.find("brainTree.key")).thenReturn(new AppProperty("key", "key"));
		when(appPropDao.find("brainTree.key_id")).thenReturn(new AppProperty("key_id", "key_id"));
        when(paymentService.getCreditCard(contractor)).thenReturn(new CreditCard(TEST_CREDIT_CARD_NUMBER));

		Whitebox.setInternalState(contractorPaymentOptions, "permissionToViewContractor", permissionToViewContractor);
        Whitebox.setInternalState(contractorPaymentOptions, "taxService", taxService);
        Whitebox.setInternalState(contractorPaymentOptions, "paymentService", paymentService);
	}

	@Test
	public void testChangePaymentToCheck_NoCcOnFile() throws Exception {
		when(contractor.isCcOnFile()).thenReturn(false);

		String actionResult = contractorPaymentOptions.changePaymentToCheck();

		verify(contractor).setPaymentMethod(PaymentMethod.Check);
		verify(contractorAccountDao).save(contractor);
		assertThat(actionResult, is(equalTo(Action.SUCCESS)));
	}

	@Test
	public void testChangePaymentToCheck_CcOnFile() throws Exception {
		when(contractor.isCcOnFile()).thenReturn(true);

		String actionResult = contractorPaymentOptions.changePaymentToCheck();

		verify(contractor).setPaymentMethod(PaymentMethod.Check);
		verify(contractorAccountDao, times(2)).save(contractor);
		assertThat(actionResult, is(equalTo(Action.SUCCESS)));
	}

    @Test
    public void testInitTaxFee() throws Exception {
        contractorPaymentOptions.setContractor(contractor);
        CountrySubdivision mockCountrySubdivision = mock(CountrySubdivision.class);
        Country mockCountry = mock(Country.class);
        ContractorFee mockContractorFee = mock(ContractorFee.class);
        ContractorFee mockContractorFee2 = mock(ContractorFee.class);
        InvoiceFee mockInvoiceFee = mock(InvoiceFee.class);
        InvoiceFee mockInvoiceFee2 = mock(InvoiceFee.class);

        when(contractor.getCountrySubdivision()).thenReturn(mockCountrySubdivision);
        when(contractor.getCountry()).thenReturn(mockCountry);
        when(contractor.getStatus()).thenReturn(AccountStatus.Active);
        HashMap<FeeClass, ContractorFee> fees = new HashMap<FeeClass, ContractorFee>();
        fees.put(FeeClass.DocuGUARD, mockContractorFee);
        fees.put(FeeClass.ImportFee, mockContractorFee2);
        when(contractor.getFees()).thenReturn(fees);
        when(mockContractorFee.getNewLevel()).thenReturn(mockInvoiceFee);
        when(mockContractorFee.getNewAmount()).thenReturn(BigDecimal.TEN);
        when(mockInvoiceFee.isFree()).thenReturn(false);
        when(mockContractorFee.getFeeClass()).thenReturn(FeeClass.DocuGUARD);
        when(mockContractorFee2.getNewLevel()).thenReturn(mockInvoiceFee2);
        when(mockContractorFee2.getNewAmount()).thenReturn(BigDecimal.TEN);
        when(mockInvoiceFee2.isFree()).thenReturn(false);
        when(mockContractorFee2.getFeeClass()).thenReturn(FeeClass.ImportFee);
        when(taxService.getTaxInvoiceFee(FeeClass.CanadianTax, mockCountry, mockCountrySubdivision)).thenReturn(mockInvoiceFee);

        Whitebox.invokeMethod(contractorPaymentOptions, "initTaxFee");

        verify(mockInvoiceFee).getTax(BigDecimal.TEN.setScale(2));

    }

    @Test
    public void testGetPaymentUrl_TestProxiesToPaymentServiceForProperUrl() throws Exception {
        contractorPaymentOptions.getPaymentUrl();

        verify(paymentService).getPaymentUrl();
    }

}
