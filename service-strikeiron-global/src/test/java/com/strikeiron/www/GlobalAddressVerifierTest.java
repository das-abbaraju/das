package com.strikeiron.www;

import com.AddressDoctor.validator2.addBatch.Batch.*;
import com.strikeiron.www.api.*;
import com.strikeiron.www.api.holders.SISubscriptionInfoHolder;
import com.strikeiron.www.api.holders.SIWsOutputOfListingHolder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.xml.rpc.ServiceException;

import java.rmi.RemoteException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class GlobalAddressVerifierTest {
    @Mock
    private GlobalAddressVerificationLocator locator;
    @Mock
    private GlobalAddressVerificationSoap siService;

    private GlobalAddressVerifier globalAddressVerifier;

    @Before
    public void setup() throws ServiceException, RemoteException {
        MockitoAnnotations.initMocks(this);
        globalAddressVerifier = new GlobalAddressVerifier(locator);
        when(locator.getGlobalAddressVerificationSoap()).thenReturn(siService);
    }
    @Test
    public void testVerifyAddress() throws ServiceException, RemoteException {
        doAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();

                SIWsOutputOfListingHolder siResponse = (SIWsOutputOfListingHolder) args[13];

                Address address = (Address) args[3];

                siResponse.value = new SIWsOutputOfListing();
                Listing serviceResult = new Listing();
                siResponse.value.setServiceResult(serviceResult);
                serviceResult.setStreetName(address.getStreetAddressLines());
                serviceResult.setLocality(address.getLocality());
                serviceResult.setProvince(address.getProvince());
                serviceResult.setCountry(address.getCountry());
                serviceResult.setPostalCode(address.getPostalCode());
                serviceResult.setResultPercentage("80");

                SIWsStatus serviceStatus = new SIWsStatus(200, "Everything cool");

                siResponse.value.setServiceStatus(serviceStatus);

                return null;
            }
        }).when(siService).advancedVerify(anyString(), anyString(), anyString(), any(Address.class), eq(true), eq(true), eq(true),
                eq(CountryOfOriginType.COO_USA), eq(CountryType.ISO_2), eq(LineSeparatorType.LST_NO_SEPARATOR), eq(ParsedInputType.ONLY_FOR_P),
                eq(PreferredLanguageType.PFL_LANG_EN), eq(CapitalizationType.MIXED_CASE), any(SIWsOutputOfListingHolder.class), any(SISubscriptionInfoHolder.class));
        Address address = globalAddressVerifier.verifyAddress("17701 Cowan", "Irvine", "CA", "US", "92614");
        assertEquals("17701 Cowan", address.getStreetAddressLines());
        assertEquals("Irvine", address.getLocality());
        assertEquals("CA", address.getProvince());
        assertEquals("92614", address.getPostalCode());
        assertEquals("US", address.getCountry());
        assertEquals("80", address.getConfidencePercentage());
        assertEquals("Everything cool", address.getStatusDescription());
    }

    @Test
    public void testVerifyAddress_ThrowException() throws ServiceException, RemoteException {
        doThrow(ServiceException.class).when(siService).advancedVerify(anyString(), anyString(), anyString(), any(Address.class), eq(true), eq(true), eq(true),
                eq(CountryOfOriginType.COO_USA), eq(CountryType.ISO_2), eq(LineSeparatorType.LST_NO_SEPARATOR), eq(ParsedInputType.ONLY_FOR_P),
                eq(PreferredLanguageType.PFL_LANG_EN), eq(CapitalizationType.MIXED_CASE), any(SIWsOutputOfListingHolder.class), any(SISubscriptionInfoHolder.class));
        Address address = globalAddressVerifier.verifyAddress("17701 Cowan", "Irvine", "CA", "US", "92614");
        assertEquals(500, address.getStatusNbr());
        assertEquals("StrikeIron Service is down", address.getStatusDescription());
    }
}
