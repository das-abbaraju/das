package com.picsauditing.util;

import org.junit.Test;
import org.powermock.reflect.Whitebox;

import static com.picsauditing.util.GoogleApiService.GOOGLE_GEOCODE_URL;
import static org.junit.Assert.assertTrue;

public class GoogleApiServiceTest {


    public static final String ADDRESS = "#12345 Main    Street   Irvine CA   ";
    GoogleApiService googleApiService = new GoogleApiService();

    @Test
    public void testSendRequestToTheGoogles() throws Exception {
        String address = googleApiService.sendRequestToTheGoogles(ADDRESS);
        assertTrue(address.contains("lat"));
    }

    @Test
    public void testProcessURL() throws Exception {
        String address = Whitebox.invokeMethod(googleApiService, "processURL", GOOGLE_GEOCODE_URL + ADDRESS);

        assertTrue(address.contains("lat"));
    }

    @Test
    public void testProcessURL_nullAddress() throws Exception {
        String address = Whitebox.invokeMethod(googleApiService, "processURL", "");

        assertTrue(Strings.isEmpty(address));
    }

}