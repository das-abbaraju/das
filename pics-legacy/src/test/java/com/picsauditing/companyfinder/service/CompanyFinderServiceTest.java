package com.picsauditing.companyfinder.service;

import com.picsauditing.actions.contractors.ContractorDashboard;
import com.picsauditing.companyfinder.dao.ContractorLocationDAO;
import com.picsauditing.companyfinder.model.ContractorLocationSummary;
import com.picsauditing.companyfinder.model.*;
import com.picsauditing.companyfinder.model.builder.CompanyFinderFilterBuilder;
import com.picsauditing.companyfinder.model.builder.ContractorLocationBuilder;
import com.picsauditing.companyfinder.model.builder.ContractorLocationSummaryInfoBuilder;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorTrade;
import com.picsauditing.jpa.entities.LowMedHigh;
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
import java.util.HashMap;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class CompanyFinderServiceTest {

    CompanyFinderService companyFinderService;
    @Mock
    AddressService addressService;
    @Mock
    ContractorLocationDAO contractorLocationDAO;
    @Mock
    OperatorAccountDAO operatorAccountDAO;

    private ViewPort viewPort;
    private double neLat;
    private double neLong;
    private double swLat;
    private double swLong;

    public CompanyFinderServiceTest() {
        super();
    }

    @Before
    public void setUp() throws Exception {
        companyFinderService = new CompanyFinderService();
        neLat = 32.81138652611525;
        neLong = -96.76751698364262;
        swLat = 32.73922009003447;
        swLong = -96.89626301635747;

        viewPort = ViewPort.builder()
                .northEast(LatLong.builder()
                        .lat(neLat)
                        .lng(neLong)
                        .build())
                .southWest(LatLong.builder()
                        .lat(swLat)
                        .lng(swLong)
                        .build())
                .build();

        MockitoAnnotations.initMocks(this);
        Whitebox.setInternalState(companyFinderService, "contractorLocationDAO", contractorLocationDAO);
        Whitebox.setInternalState(companyFinderService, "operatorAccountDAO", operatorAccountDAO);
        Whitebox.setInternalState(companyFinderService, "addressService", addressService);

        when(addressService.formatAddressAsBlock(any(ContractorAccount.class))).thenReturn("someFormattedAddressBlock");
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
    public void testFindContractorLocationInfos_noTrade() throws Exception {
        List<ContractorLocation> contractorLocations = buildTestContractorLocations();
        CompanyFinderFilter filter = new CompanyFinderFilterBuilder().safetySensitive(TriStateFlag.IGNORE).soleProprietor(TriStateFlag.IGNORE).build();
        when(contractorLocationDAO.findContractorLocations(filter)).thenReturn(contractorLocations);

        HashMap<String, String> contractorInfoProps = buildContractorInfoProperties();

        List<ContractorLocationInfo> contractorLocationInfoList = companyFinderService.findContractorLocationInfos(filter, contractorInfoProps);

        assertEquals(3, contractorLocationInfoList.size());
        assertEquals(1, contractorLocationInfoList.get(0).getId());
        assertEquals("con1", contractorLocationInfoList.get(0).getName());
        assertEquals("fulladdress1", contractorLocationInfoList.get(0).getAddress());
        assertNotNull(contractorLocationInfoList.get(0).getFormattedAddressBlock());
        assertEquals(100.0, contractorLocationInfoList.get(0).getCoordinates().getLatitude());
        assertEquals(101.0, contractorLocationInfoList.get(0).getCoordinates().getLongitude());
        assertEquals("trade1", contractorLocationInfoList.get(0).getPrimaryTrade());
        assertEquals(Arrays.asList("foo1", "bar1", "car1"), contractorLocationInfoList.get(0).getTrades());
        assertEquals("/ContractorView.action?id=1", contractorLocationInfoList.get(0).getLink());
    }

    @Test
    public void testFindContractorLocationSummary_viewPortOnly() throws Exception {
        List<ContractorLocationSummary> contractorLocationSummaries = buildTestContractorLocationSummaryInfos();
        CompanyFinderFilter filter = new CompanyFinderFilterBuilder().viewPort(viewPort).safetySensitive(TriStateFlag.IGNORE).soleProprietor(TriStateFlag.IGNORE).build();

        when(contractorLocationDAO.findContractorLocationsSummary(filter)).thenReturn(contractorLocationSummaries);

        List<ContractorLocationInfo> results = companyFinderService.findContractorLocationInfoSummaries(filter);

        assertEquals(3, results.size());
        assertEquals(10, results.get(0).getId());
        assertEquals(100.0, results.get(0).getCoordinates().getLatitude());
        assertEquals(101.0, results.get(0).getCoordinates().getLongitude());

    }

    private HashMap<String, String> buildContractorInfoProperties() {
        HashMap<String, String> contractorInfoProperties = new HashMap<>();
        contractorInfoProperties.put("linkurl", ContractorDashboard.URL);
        contractorInfoProperties.put("opId", String.valueOf(555));
        return contractorInfoProperties;
    }

    private List<ContractorLocation> buildTestContractorLocations() {
        List<ContractorLocation> contractorLocations = new ArrayList<>();

        ContractorTrade primaryContractorTrade1 = buildTestContractorTrade("trade1");
        List<ContractorTrade> contractorTrades1 = buildTestContractorTrades("foo1", "bar1", "car1");

        ContractorAccount contractorAccount1 = mock(ContractorAccount.class);
        when(contractorAccount1.getTopTrade()).thenReturn(primaryContractorTrade1);
        when(contractorAccount1.getTradesSorted()).thenReturn(contractorTrades1);
        when(contractorAccount1.getId()).thenReturn(1);
        when(contractorAccount1.isSafetySensitive()).thenReturn(false);
        when(contractorAccount1.getSafetyRisk()).thenReturn(LowMedHigh.High);
        when(contractorAccount1.getName()).thenReturn("con1");
        when(contractorAccount1.getFullAddress()).thenReturn("fulladdress1");

        ContractorTrade primaryContractorTrade2 = buildTestContractorTrade("trade2");
        List<ContractorTrade> contractorTrades2 = buildTestContractorTrades("foo2", "bar2", "car2");

        ContractorAccount contractorAccount2 = mock(ContractorAccount.class);
        when(contractorAccount2.getTopTrade()).thenReturn(primaryContractorTrade2);
        when(contractorAccount2.getTradesSorted()).thenReturn(contractorTrades2);
        when(contractorAccount2.getId()).thenReturn(2);
        when(contractorAccount2.getName()).thenReturn("con2");
        when(contractorAccount1.isSafetySensitive()).thenReturn(true);
        when(contractorAccount2.getSafetyRisk()).thenReturn(LowMedHigh.High);
        when(contractorAccount2.getFullAddress()).thenReturn("fulladdress2");

        ContractorTrade primaryContractorTrade3 = buildTestContractorTrade("trade3");
        List<ContractorTrade> contractorTrades3 = buildTestContractorTrades("foo3", "bar3", "car3");

        ContractorAccount contractorAccount3 = mock(ContractorAccount.class);
        when(contractorAccount3.getTopTrade()).thenReturn(primaryContractorTrade3);
        when(contractorAccount3.getTradesSorted()).thenReturn(contractorTrades3);
        when(contractorAccount3.getId()).thenReturn(3);
        when(contractorAccount1.isSafetySensitive()).thenReturn(true);
        when(contractorAccount2.getSafetyRisk()).thenReturn(LowMedHigh.High);
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

    private List<ContractorLocationSummary> buildTestContractorLocationSummaryInfos() {
        List<ContractorLocationSummary> contractorLocationSummary = new ArrayList<>();

        ContractorLocationSummary contractorLocationSummary1 =
                new ContractorLocationSummaryInfoBuilder()
                        .id(1)
                        .conId(10)
                        .lat((double) 100)
                        .lng((double) 101)
                        .build();
        contractorLocationSummary.add(contractorLocationSummary1);

        ContractorLocationSummary contractorLocationSummary2 =
                new ContractorLocationSummaryInfoBuilder()
                        .id(2)
                        .conId(20)
                        .lat((double) 200)
                        .lng((double) 202)
                        .build();
        contractorLocationSummary.add(contractorLocationSummary2);

        ContractorLocationSummary contractorLocationSummary3 =
                new ContractorLocationSummaryInfoBuilder()
                        .id(3)
                        .conId(30)
                        .lat((double) 300)
                        .lng((double) 303)
                        .build();
        contractorLocationSummary.add(contractorLocationSummary3);

        return contractorLocationSummary;
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
