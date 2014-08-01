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
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CompanyFinderServiceTest {

    CompanyFinderService companyFinderService;
    @Mock
    ContractorLocationDAO contractorLocationDAO;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        companyFinderService = new CompanyFinderService();
        Whitebox.setInternalState(companyFinderService, "contractorLocationDAO", contractorLocationDAO);
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
        assertEquals(100.0, contractorLocationInfos.get(0).getCoordinates().getLatitude());
        assertEquals(101.0, contractorLocationInfos.get(0).getCoordinates().getLongitude());
        assertEquals("trade1", contractorLocationInfos.get(0).getTrade());
        assertEquals("someUrl?id=1", contractorLocationInfos.get(0).getLink());

        assertEquals(2, contractorLocationInfos.get(1).getId());
        assertEquals("con2", contractorLocationInfos.get(1).getName());
        assertEquals("fulladdress2", contractorLocationInfos.get(1).getAddress());
        assertEquals(200.0, contractorLocationInfos.get(1).getCoordinates().getLatitude());
        assertEquals(201.0, contractorLocationInfos.get(1).getCoordinates().getLongitude());
        assertEquals("trade2", contractorLocationInfos.get(1).getTrade());
        assertEquals("someUrl?id=2", contractorLocationInfos.get(1).getLink());

        assertEquals(3, contractorLocationInfos.get(2).getId());
        assertEquals("con3", contractorLocationInfos.get(2).getName());
        assertEquals("fulladdress3", contractorLocationInfos.get(2).getAddress());
        assertEquals(300.0, contractorLocationInfos.get(2).getCoordinates().getLatitude());
        assertEquals(301.0, contractorLocationInfos.get(2).getCoordinates().getLongitude());
        assertEquals("trade3", contractorLocationInfos.get(2).getTrade());
        assertEquals("someUrl?id=3", contractorLocationInfos.get(2).getLink());

    }

    private List<ContractorLocation> buildTestContractorLocations() {
        List<ContractorLocation> contractorLocations = new ArrayList<>();

        Trade trade1 = new Trade();
        trade1.setName("trade1");
        ContractorTrade contractorTrade1 = mock(ContractorTrade.class);
        when(contractorTrade1.getTrade()).thenReturn(trade1);
        
        ContractorAccount contractorAccount1 = mock(ContractorAccount.class);
        when(contractorAccount1.getTopTrade()).thenReturn(contractorTrade1);
        when(contractorAccount1.getId()).thenReturn(1);
        when(contractorAccount1.getName()).thenReturn("con1");
        when(contractorAccount1.getFullAddress()).thenReturn("fulladdress1");

        Trade trade2 = new Trade();
        trade2.setName("trade2");
        ContractorTrade contractorTrade2 = mock(ContractorTrade.class);
        when(contractorTrade2.getTrade()).thenReturn(trade2);

        ContractorAccount contractorAccount2 = mock(ContractorAccount.class);
        when(contractorAccount2.getTopTrade()).thenReturn(contractorTrade2);
        when(contractorAccount2.getId()).thenReturn(2);
        when(contractorAccount2.getName()).thenReturn("con2");
        when(contractorAccount2.getFullAddress()).thenReturn("fulladdress2");

        Trade trade3 = new Trade();
        trade3.setName("trade3");
        ContractorTrade contractorTrade3 = mock(ContractorTrade.class);
        when(contractorTrade3.getTrade()).thenReturn(trade3);

        ContractorAccount contractorAccount3 = mock(ContractorAccount.class);
        when(contractorAccount3.getTopTrade()).thenReturn(contractorTrade3);
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
}
