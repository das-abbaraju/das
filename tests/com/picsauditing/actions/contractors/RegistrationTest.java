package com.picsauditing.actions.contractors;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.search.Database;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picsauditing.dao.CountrySubdivisionDAO;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.powermock.reflect.Whitebox.setInternalState;


public class RegistrationTest{
	Registration classUnderTest;
	
	@Mock private ContractorAccount contractor;
	@Mock private Country country;
	@Mock private CountrySubdivision countrySubdivision;
	@Mock private CountrySubdivisionDAO countrySubdivisionDAO;
    @Mock private InvoiceFeeDAO feeDAO;
    @Mock private Map<FeeClass, ContractorFee> contractorFees;
    @Mock private I18nCache cache;
    @Mock private Database databaseForTesting;
    @Mock private ThreadLocal mockThreadLocal;
    @Mock private ActionContext mockContext;
    @Mock private User mockUser;

    @AfterClass
    public static void tearDown() {
        Whitebox.setInternalState(I18nCache.class, "databaseForTesting", (Database) null);
        Whitebox.setInternalState(ActionContext.class, "actionContext", new ThreadLocal());
    }

	@Before
	public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        setInternalState(I18nCache.class, "databaseForTesting", databaseForTesting);
        setInternalState(ActionContext.class, "actionContext", mockThreadLocal);
        when(mockThreadLocal.get()).thenReturn(mockContext);
        classUnderTest = new Registration();
        setInternalState(classUnderTest, "countrySubdivisionDAO", countrySubdivisionDAO);
        setInternalState(classUnderTest, "invoiceFeeDAO", feeDAO);
        classUnderTest.setContractor(contractor);
        classUnderTest.setUser(mockUser);
        when(contractor.getFees()).thenReturn(contractorFees);
        when(contractor.getCountry()).thenReturn(country);
        when(contractor.getNaics()).thenReturn(new Naics());
	}

    @Test
    public void testSetupContractorData_Demo_NoSubdivisions() {
        when(contractor.getName()).thenReturn(DEMO_NAME);
        when(country.hasCountrySubdivisions()).thenReturn(false);
        when(contractor.getUsers()).thenReturn(new ArrayList<User>());

        classUnderTest.setupContractorData();

        verify(contractor).setName(anyString());
        verify(contractor).setStatus(AccountStatus.Demo);
        verify(contractor).setCountrySubdivision(null);
        verify(contractorFees, atLeastOnce()).put(any(FeeClass.class), any(ContractorFee.class));
        verify(countrySubdivisionDAO, never()).find(anyString());
        verify(feeDAO, atLeastOnce()).findByNumberOfOperatorsAndClass(any(FeeClass.class), anyInt());
    }

    @Test
    public void testSetupContractorData_noDemo_HasSubdivisions_matchedInput() {
        classUnderTest.setCountrySubdivision(countrySubdivision);
        when(contractor.getName()).thenReturn(REAL_NAME);
        when(country.hasCountrySubdivisions()).thenReturn(true);
        when(contractor.getCountrySubdivision()).thenReturn(countrySubdivision);

        classUnderTest.setupContractorData();

        verify(contractor, never()).setName(anyString());
        verify(contractor, never()).setStatus(any(AccountStatus.class));
        verify(contractor, never()).setCountrySubdivision(any(CountrySubdivision.class));
        verify(countrySubdivisionDAO, never()).find(anyString());
        verify(feeDAO, atLeastOnce()).findByNumberOfOperatorsAndClass(any(FeeClass.class), anyInt());
    }

    @Test
    public void testSetupContractorData_noDemo_HasSubdivisions_unMatchedInput() {
        classUnderTest.setCountrySubdivision(new CountrySubdivision("XX"));
        when(contractor.getName()).thenReturn(REAL_NAME);
        when(country.hasCountrySubdivisions()).thenReturn(true);
        when(contractor.getCountrySubdivision()).thenReturn(countrySubdivision);

        classUnderTest.setupContractorData();

        verify(contractor, never()).setName(anyString());
        verify(contractor, never()).setStatus(any(AccountStatus.class));
        verify(contractor).setCountrySubdivision(any(CountrySubdivision.class));
        verify(countrySubdivisionDAO).find(anyString());
        verify(feeDAO, atLeastOnce()).findByNumberOfOperatorsAndClass(any(FeeClass.class), anyInt());
    }


    private final static String DEMO_NAME = "^^^demo";
    private final static String REAL_NAME = "fooBar";
}
