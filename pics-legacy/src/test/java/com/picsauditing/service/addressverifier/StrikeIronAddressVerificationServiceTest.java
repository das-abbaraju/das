package com.picsauditing.service.addressverifier;


import com.strikeiron.www.Address;
import com.strikeiron.www.GlobalAddressVerifier;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.xml.rpc.ServiceException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class StrikeIronAddressVerificationServiceTest {

    private StrikeIronAddressVerificationService strikeIronAddressVerificationService;
    @Mock
    private GlobalAddressVerifier globalAddressVerifier;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        strikeIronAddressVerificationService = new StrikeIronAddressVerificationService(globalAddressVerifier);
    }

    @Test
    public void testVerify_UsAddress() throws AddressVerificationException, ServiceException {
        AddressHolder picsIrvine = new AddressHolder();
        picsIrvine.setAddressLine1("17701 Cowan St.");
        picsIrvine.setAddressLine2("Suite 140");
        picsIrvine.setCity("Irvine");
        picsIrvine.setStateOrProvince("CA");
        picsIrvine.setZipOrPostalCode("92612");
        picsIrvine.setCountry("US");

        Address verified = new Address();
        verified.setStreetAddressLines("17701 Cowan Ste 140");
        verified.setLocality("Irvine");
        verified.setProvince("CA");
        verified.setPostalCode("92614");
        verified.setCountry("US");
        verified.setStatusNbr(200);
        verified.setStatusDescription("Data corrected by web service");

        when(globalAddressVerifier.verifyAddress(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(verified);

        AddressHolder correctedAddress = strikeIronAddressVerificationService.verify(picsIrvine);

        assertEquals("17701 Cowan Ste 140", correctedAddress.getAddressLine1());
        assertEquals("Irvine", correctedAddress.getCity());
        assertEquals("CA", correctedAddress.getStateOrProvince());
        assertEquals("92614", correctedAddress.getZipOrPostalCode());
        assertEquals("US", correctedAddress.getCountry());
        assertEquals(ResultStatus.SUCCESS, correctedAddress.getResultStatus());
        assertEquals("Data corrected by web service", correctedAddress.getStatusDescription());
    }

    @Test(expected = AddressVerificationException.class)
    public void testVerify_InternalError() throws AddressVerificationException, ServiceException {
        AddressHolder picsIrvine = new AddressHolder();
        picsIrvine.setAddressLine1("17701 Cowan St.");
        picsIrvine.setAddressLine2("Suite 140");
        picsIrvine.setCity("Irvine");
        picsIrvine.setStateOrProvince("CA");
        picsIrvine.setZipOrPostalCode("92612");
        picsIrvine.setCountry("US");

        Address verified = new Address();
        verified.setStatusNbr(500);
        verified.setStatusDescription("Internal Error");

        when(globalAddressVerifier.verifyAddress(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(verified);

        AddressHolder correctedAddress = strikeIronAddressVerificationService.verify(picsIrvine);

        assertEquals("Internal Error", correctedAddress.getStatusDescription());
    }

    @Test(expected = AddressVerificationException.class)
    public void testVerify_ConnectionError() throws AddressVerificationException, ServiceException {
        AddressHolder picsIrvine = new AddressHolder();
        picsIrvine.setAddressLine1("17701 Cowan St.");
        picsIrvine.setAddressLine2("Suite 140");
        picsIrvine.setCity("Irvine");
        picsIrvine.setStateOrProvince("CA");
        picsIrvine.setZipOrPostalCode("92612");
        picsIrvine.setCountry("US");

        when(globalAddressVerifier.verifyAddress(anyString(), anyString(), anyString(), anyString(), anyString())).thenThrow(ServiceException.class);

        strikeIronAddressVerificationService.verify(picsIrvine);
    }
}
