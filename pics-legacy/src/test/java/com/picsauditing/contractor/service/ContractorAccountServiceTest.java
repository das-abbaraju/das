package com.picsauditing.contractor.service;

import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.util.TriState;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ContractorAccountServiceTest {

    ContractorAccountService contractorAccountService;
    @Mock
    private ContractorAccount contractorAccount;

    @Before
    public void setUp() throws Exception {
        contractorAccountService = new ContractorAccountService();
        initMocks(this);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsSmallBusiness_nullContractorShouldThrow() throws Exception {
        contractorAccountService.isSmallBusiness(null);
    }

    @Test
    public void testIsSmallBusiness_nullNumberOfEmployeesShouldReturnUnknown() throws Exception {
        when(contractorAccount.getNumberOfEmployees()).thenReturn(null);

        TriState result = contractorAccountService.isSmallBusiness(contractorAccount);

        assertEquals(result, TriState.UNKNOWN);
    }

    @Test
    public void testIsSmallBusiness_US_smallBusiness() throws Exception {
        when(contractorAccount.getBillingCountry()).thenReturn(new Country(Country.US_ISO_CODE));
        when(contractorAccount.getNumberOfEmployees()).thenReturn(2);

        TriState result = contractorAccountService.isSmallBusiness(contractorAccount);

        assertEquals(result, TriState.TRUE);
    }

    @Test
    public void testIsSmallBusiness_US_notSmallBusiness() throws Exception {
        when(contractorAccount.getBillingCountry()).thenReturn(new Country(Country.US_ISO_CODE));
        when(contractorAccount.getNumberOfEmployees()).thenReturn(12);

        TriState result = contractorAccountService.isSmallBusiness(contractorAccount);

        assertEquals(result, TriState.FALSE);
    }
}