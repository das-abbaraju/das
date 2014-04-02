package com.picsauditing.service.addressverifier;


import com.picsauditing.featuretoggle.Features;
import com.strikeiron.www.Address;
import com.strikeiron.www.GlobalAddressVerifier;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;
import org.togglz.junit.TogglzRule;

import javax.xml.rpc.ServiceException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class StrikeIronAddressVerificationServiceTest {

    private StrikeIronAddressVerificationService strikeIronAddressVerificationService;
    @Mock
    private GlobalAddressVerifier globalAddressVerifier;

    @Rule
    public TogglzRule togglzRule = TogglzRule.allEnabled(Features.class);

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        strikeIronAddressVerificationService = new StrikeIronAddressVerificationService();
        Whitebox.setInternalState(strikeIronAddressVerificationService, "globalAddressVerifier", globalAddressVerifier);
    }

    @Test
    public void testVerify_UsAddress() throws AddressVerificationException, ServiceException {
        AddressRequestHolder picsIrvine = createIrvineAddress();

        Address verified = new Address();
        verified.setStreetAddressLines("17701 Cowan Ste 140");
        verified.setLocality("Irvine");
        verified.setProvince("CA");
        verified.setPostalCode("92614");
        verified.setCountry("US");
        verified.setStatusNbr(200);
        verified.setStatusDescription("Data corrected by web service");

        when(globalAddressVerifier.execute()).thenReturn(verified);

        AddressResponseHolder correctedAddress = strikeIronAddressVerificationService.verify(picsIrvine);

        assertEquals("17701 Cowan Ste 140", correctedAddress.getAddressLine1());
        assertEquals("Irvine", correctedAddress.getCity());
        assertEquals("CA", correctedAddress.getStateOrProvince());
        assertEquals("92614", correctedAddress.getZipOrPostalCode());
        assertEquals("US", correctedAddress.getCountry());
        assertEquals(ResultStatus.SUCCESS, correctedAddress.getResultStatus());
        assertEquals("Data corrected by web service", correctedAddress.getStatusDescription());
    }

    @Test
    public void testVerify_UsAddress_FeatureDisabled() throws AddressVerificationException, ServiceException {
        togglzRule.disable(Features.USE_STRIKEIRON_ADDRESS_VERIFICATION_SERVICE);
        AddressRequestHolder picsIrvine = createIrvineAddress();

        Address verified = new Address();
        verified.setStreetAddressLines("17701 Cowan Ste 140");
        verified.setLocality("Irvine");
        verified.setProvince("CA");
        verified.setPostalCode("92614");
        verified.setCountry("US");

        when(globalAddressVerifier.execute()).thenReturn(verified);

        AddressResponseHolder correctedAddress = strikeIronAddressVerificationService.verify(picsIrvine);

        assertEquals(ResultStatus.SUCCESS, correctedAddress.getResultStatus());
        assertEquals(StrikeIronAddressVerificationService.FEATURE_DISABLED_MESSAGE, correctedAddress.getStatusDescription());
    }

    @Test
    public void testVerify_InternalError() throws AddressVerificationException, ServiceException {
        AddressRequestHolder picsIrvine = createIrvineAddress();

        Address verified = new Address();
        verified.setStatusNbr(500);
        verified.setStatusDescription("Internal Error");

        when(globalAddressVerifier.execute()).thenReturn(verified);

        AddressResponseHolder correctedAddress = strikeIronAddressVerificationService.verify(picsIrvine);

        assertEquals("Internal Error", correctedAddress.getStatusDescription());
    }

    @Test(expected = AddressVerificationException.class)
    public void testVerify_ConnectionError() throws AddressVerificationException, ServiceException {
        AddressRequestHolder picsIrvine = createIrvineAddress();

        when(globalAddressVerifier.execute()).thenThrow(ServiceException.class);

        strikeIronAddressVerificationService.verify(picsIrvine);
    }

    @Test
    public void testParseResultCode() {
        assertEquals(ResultStatus.SUCCESS, StrikeIronAddressVerificationService.parseResultCode(200));
        assertEquals(ResultStatus.DATA_NOT_FOUND, StrikeIronAddressVerificationService.parseResultCode(300));
        assertEquals(ResultStatus.INVALID_INPUT, StrikeIronAddressVerificationService.parseResultCode(400));
        assertEquals(ResultStatus.INTERNAL_ERROR, StrikeIronAddressVerificationService.parseResultCode(500));
        assertEquals(ResultStatus.INTERNAL_ERROR, StrikeIronAddressVerificationService.parseResultCode(199));
    }

    private AddressRequestHolder createIrvineAddress() {
        AddressRequestHolder picsIrvine = new AddressRequestHolder();
        picsIrvine.setAddressBlob("17701 Cowan St.\nSuite 140\nIrvine, CA");
        picsIrvine.setZipOrPostalCode("92612");
        picsIrvine.setCountry("US");
        return picsIrvine;
    }
}
