package com.picsauditing.companyfinder.service;

import com.picsauditing.companyfinder.model.ContractorLocation;
import com.picsauditing.companyfinder.model.ContractorLocationInfo;
import com.picsauditing.companyfinder.model.ViewPort;
import com.picsauditing.companyfinder.model.ViewportLocation;
import com.picsauditing.companyfinder.model.builder.ContractorLocationBuilder;
import com.picsauditing.dao.companyfinder.ContractorLocationDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorTrade;
import com.picsauditing.jpa.entities.Trade;
import com.picsauditing.model.general.LatLong;
import com.picsauditing.service.account.AddressService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CompanyFinderServiceTest {

    CompanyFinderService companyFinderService;
    @Mock
    ContractorLocationDAO contractorLocationDAO;
    @Mock
    AddressService addressService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        companyFinderService = new CompanyFinderService();
        Whitebox.setInternalState(companyFinderService, "contractorLocationDAO", contractorLocationDAO);
        Whitebox.setInternalState(companyFinderService, "addressService", addressService);

        when(addressService.formatAddressAsBlock(any(ContractorAccount.class))).thenReturn("someFormattedAddressBlock");
    }

    @Test
    public void testLatLongFromAddressUnsecure() throws Exception {
        //todo
    }

    @Test
    public void testBuildViewportLocationFromAddressUnsecure() throws Exception {

        String address = "315 Bowery New York, NY";
        ViewportLocation viewportLocation = companyFinderService.buildViewportLocationFromAddressUnsecure(address);

        assertNotNull(viewportLocation);
        assertEquals("315 Bowery New York, NY", viewportLocation.getAddress());
        assertEquals(40.7251397, viewportLocation.getCoordinates().getLatitude());
        assertEquals(-73.99188079999999, viewportLocation.getCoordinates().getLongitude());
        assertNotNull(viewportLocation.getViewPort());
    }

    @Test
    public void testFindContractorLocationInfos_noTrade_Dallas_TX() throws Exception {

        double neLat = 32.81138652611525;
        double neLong = -96.76751698364262;
        double swLat = 32.73922009003447;
        double swLong = -96.89626301635747;

        ViewPort viewPort = ViewPort.builder()
                .northEast(LatLong.builder()
                        .lat(neLat)
                        .lng(neLong)
                        .build())
                .southWest(LatLong.builder()
                        .lat(swLat)
                        .lng(swLong)
                        .build())
                .build();
        Trade trade = null;

        List<ContractorLocation> contractorLocations = buildTestContractorLocations();
        when(contractorLocationDAO.findByViewPort(neLat, neLong, swLat, swLong)).thenReturn(contractorLocations);

        List<ContractorLocationInfo> contractorLocationInfos = companyFinderService.findContractorLocationInfos(viewPort, trade, "someUrl");

        assertEquals(3, contractorLocationInfos.size());

        assertEquals(1, contractorLocationInfos.get(0).getId());
        assertEquals("con1", contractorLocationInfos.get(0).getName());
        assertEquals("fulladdress1", contractorLocationInfos.get(0).getAddress());
        assertNotNull(contractorLocationInfos.get(0).getFormattedAddressBlock());
        assertEquals(100.0, contractorLocationInfos.get(0).getCoordinates().getLatitude());
        assertEquals(101.0, contractorLocationInfos.get(0).getCoordinates().getLongitude());
        assertEquals("trade1", contractorLocationInfos.get(0).getPrimaryTrade());
        assertEquals(Arrays.asList("foo1", "bar1", "car1"), contractorLocationInfos.get(0).getTrades());
        assertEquals("someUrl?id=1", contractorLocationInfos.get(0).getLink());

        assertEquals(2, contractorLocationInfos.get(1).getId());
        assertEquals("con2", contractorLocationInfos.get(1).getName());
        assertEquals("fulladdress2", contractorLocationInfos.get(1).getAddress());
        assertNotNull(contractorLocationInfos.get(1).getFormattedAddressBlock());
        assertEquals(200.0, contractorLocationInfos.get(1).getCoordinates().getLatitude());
        assertEquals(201.0, contractorLocationInfos.get(1).getCoordinates().getLongitude());
        assertEquals("trade2", contractorLocationInfos.get(1).getPrimaryTrade());
        assertEquals(Arrays.asList("foo2", "bar2", "car2"), contractorLocationInfos.get(1).getTrades());
        assertEquals("someUrl?id=2", contractorLocationInfos.get(1).getLink());

        assertEquals(3, contractorLocationInfos.get(2).getId());
        assertEquals("con3", contractorLocationInfos.get(2).getName());
        assertEquals("fulladdress3", contractorLocationInfos.get(2).getAddress());
        assertNotNull(contractorLocationInfos.get(2).getFormattedAddressBlock());
        assertEquals(300.0, contractorLocationInfos.get(2).getCoordinates().getLatitude());
        assertEquals(301.0, contractorLocationInfos.get(2).getCoordinates().getLongitude());
        assertEquals("trade3", contractorLocationInfos.get(2).getPrimaryTrade());
        assertEquals(Arrays.asList("foo3", "bar3", "car3"), contractorLocationInfos.get(2).getTrades());
        assertEquals("someUrl?id=3", contractorLocationInfos.get(2).getLink());

    }

    private List<ContractorLocation> buildTestContractorLocations() {
        List<ContractorLocation> contractorLocations = new ArrayList<>();

        ContractorTrade primaryContractorTrade1 = buildTestContractorTrade("trade1");
        List<ContractorTrade> contractorTrades1 = buildTestContractorTrades("foo1", "bar1", "car1");

        ContractorAccount contractorAccount1 = mock(ContractorAccount.class);
        when(contractorAccount1.getTopTrade()).thenReturn(primaryContractorTrade1);
        when(contractorAccount1.getTradesSorted()).thenReturn(contractorTrades1);
        when(contractorAccount1.getId()).thenReturn(1);
        when(contractorAccount1.getName()).thenReturn("con1");
        when(contractorAccount1.getFullAddress()).thenReturn("fulladdress1");

        ContractorTrade primaryContractorTrade2 = buildTestContractorTrade("trade2");
        List<ContractorTrade> contractorTrades2 = buildTestContractorTrades("foo2", "bar2", "car2");

        ContractorAccount contractorAccount2 = mock(ContractorAccount.class);
        when(contractorAccount2.getTopTrade()).thenReturn(primaryContractorTrade2);
        when(contractorAccount2.getTradesSorted()).thenReturn(contractorTrades2);
        when(contractorAccount2.getId()).thenReturn(2);
        when(contractorAccount2.getName()).thenReturn("con2");
        when(contractorAccount2.getFullAddress()).thenReturn("fulladdress2");

        ContractorTrade primaryContractorTrade3 = buildTestContractorTrade("trade3");
        List<ContractorTrade> contractorTrades3 = buildTestContractorTrades("foo3", "bar3", "car3");

        ContractorAccount contractorAccount3 = mock(ContractorAccount.class);
        when(contractorAccount3.getTopTrade()).thenReturn(primaryContractorTrade3);
        when(contractorAccount3.getTradesSorted()).thenReturn(contractorTrades3);
        when(contractorAccount3.getId()).thenReturn(3);
        when(contractorAccount3.getName()).thenReturn("con3");
        when(contractorAccount3.getFullAddress()).thenReturn("fulladdress3");
        
        ContractorLocation contractorLocation = 
                new ContractorLocationBuilder()
                .contractor(contractorAccount1)
                .lat((double) 100)
                .lng((double) 101)
                .build();
        contractorLocations.add(contractorLocation);
        
        ContractorLocation contractorLocation2 = 
                new ContractorLocationBuilder()
                .contractor(contractorAccount2)
                .lat((double) 200)
                .lng((double) 201)
                .build();
        contractorLocations.add(contractorLocation2);
        
        ContractorLocation contractorLocation3 = 
                new ContractorLocationBuilder()
                .contractor(contractorAccount3)
                .lat((double) 300)
                .lng((double) 301)
                .build();
        contractorLocations.add(contractorLocation3);
        
        return contractorLocations;
    }

    private List<ContractorTrade> buildTestContractorTrades(String trade1Name, String trade2Name, String trade3Name) {
        List<ContractorTrade> testTrades = new ArrayList<>();
        testTrades.add(buildTestContractorTrade(trade1Name));
        testTrades.add(buildTestContractorTrade(trade2Name));
        testTrades.add(buildTestContractorTrade(trade3Name));

        return testTrades;
    }

    private ContractorTrade buildTestContractorTrade(String tradeName) {
        Trade trade = new Trade();
        trade.setName(tradeName);
        ContractorTrade contractorTrade = new ContractorTrade();
        contractorTrade.setTrade(trade);
        return contractorTrade;
    }
}
