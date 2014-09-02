package com.picsauditing.companyfinder.service;

import com.atlassian.crowd.util.Assert;
import com.picsauditing.companyfinder.dao.ContractorLocationDao;
import com.picsauditing.companyfinder.model.ContractorGeoLocation;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.GoogleApiService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class ContractorLocationServiceTest {

    public static final String VALID_ADDRESS = "17701 Cowan, Irvine CA 92618";
    public static final String GOOGLE_FORMATTED_ADDRESS = "17701+cowan,+irvine+ca+92618";

    private static final int CONTRACTOR_ID = 3;
    private static final int USER_ID = 12345;
    private static final String JSON_BODY = "{\"results\" : [{\"geometry\" : {\"location\" : {\"lat\" : 33.6954359,\"lng\" : -117.8576966}}}],\"status\" : \"OK\"}";

    private ContractorLocationService locationService;

    @Mock
    ContractorAccountDAO contractorAccountDAO;
    @Mock
    ContractorAccount contractorAccount;
    @Mock
    private User user;
    @Mock
    private ContractorLocationDao contractorLocationDao;

    @Mock
    private GoogleApiService googleApiService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        locationService = new ContractorLocationService();
        contractorAccount.setId(CONTRACTOR_ID);
        Whitebox.setInternalState(locationService, "contractorLocationDao", contractorLocationDao);
        Whitebox.setInternalState(locationService, "googleApiService", googleApiService);
        when(googleApiService.sendRequestToTheGoogles(VALID_ADDRESS)).thenReturn(JSON_BODY);
        when(contractorAccount.getActiveUser()).thenReturn(user);
        when(user.getId()).thenReturn(USER_ID);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testSaveLocation() throws Exception {
        when(contractorAccount.getAddress()).thenReturn(VALID_ADDRESS);

        locationService.saveLocation(contractorAccount);
    }

    @Test
    public void testParseAddress() throws Exception {
        when(contractorAccount.getAddress()).thenReturn(VALID_ADDRESS);
        String address = Whitebox.invokeMethod(locationService, "parseAddress", contractorAccount);

        assertEquals(address, GOOGLE_FORMATTED_ADDRESS);
    }

    @Test
    public void testParseAddress_withLastChar() throws Exception {
        when(contractorAccount.getAddress()).thenReturn(VALID_ADDRESS);
        String address = Whitebox.invokeMethod(locationService, "parseAddress", contractorAccount);

        assertNotSame(address, GOOGLE_FORMATTED_ADDRESS);
    }

    @Test
    public void testParseAddress_inValidAddress() throws Exception {
        when(contractorAccount.getAddress()).thenReturn(VALID_ADDRESS + "++++");
        String address = Whitebox.invokeMethod(locationService, "parseAddress", contractorAccount);

        assertEquals(address, GOOGLE_FORMATTED_ADDRESS);
    }

    @Test
    public void testFetchGeoLocation_validAddress() throws Exception {
        ContractorGeoLocation contractorGeoLocation = locationService.fetchGeoLocation(contractorAccount, VALID_ADDRESS);

        Assert.notNull(contractorGeoLocation);
    }

    @Test
    public void testFetchGeoLocation_nullAddress() throws Exception {
        ContractorGeoLocation contractorGeoLocation = locationService.fetchGeoLocation(contractorAccount, null);

        assertTrue(contractorGeoLocation == null);
    }

    @Test
    public void testFetchGeoLocation_inValidAddress() throws Exception {
        String invalidAddress = "45%#23";
        when(googleApiService.sendRequestToTheGoogles(invalidAddress)).thenReturn(JSON_BODY);

        ContractorGeoLocation contractorGeoLocation = locationService.fetchGeoLocation(contractorAccount, invalidAddress);

        Assert.notNull(contractorGeoLocation);
    }
}