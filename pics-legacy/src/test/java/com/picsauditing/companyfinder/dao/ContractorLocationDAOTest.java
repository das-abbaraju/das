package com.picsauditing.companyfinder.dao;

import com.picsauditing.companyfinder.model.CompanyFinderFilter;
import com.picsauditing.companyfinder.model.SafetySensitive;
import com.picsauditing.companyfinder.model.ViewPort;
import com.picsauditing.companyfinder.model.builder.CompanyFinderFilterBuilder;
import com.picsauditing.jpa.entities.Trade;
import com.picsauditing.model.general.LatLong;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
public class ContractorLocationDAOTest {

    private ContractorLocationDAO contractorLocationDAO;
    double neLat = 33.82667830089045;
    double neLong = -118.20405049182125;
    double swLat = 33.791020005057;
    double swLong = -118.26842350817867;
    boolean ss = true;
    String tradeName = "welder";

    @Before
    public void setUp() throws Exception {
        contractorLocationDAO = new ContractorLocationDAO();
    }

    @Test
    public void testGetSQL_viewPortOnly() throws Exception {
        CompanyFinderFilter filter = new CompanyFinderFilterBuilder()
                .viewPort(
                        ViewPort.builder()
                                .northEast(LatLong.builder()
                                        .lat(neLat)
                                        .lng(neLong)
                                        .build())
                                .southWest(LatLong.builder()
                                        .lat(swLat)
                                        .lng(swLong)
                                        .build())
                                .build())
                .build();
        String sql = contractorLocationDAO.getSQL(filter);
        String expected = "SELECT distinct cl FROM ContractorLocation cl JOIN cl.contractor ca WHERE cl.latitude > :swLat AND cl.longitude > :swLong AND cl.latitude < :neLat AND cl.longitude < :neLong AND (ca.status = :active OR ca.status = :pending )";
        assertEquals(expected, sql);
    }

    @Test
    public void testGetSQL_filterWithTrade() throws Exception {
        Trade trade = new Trade();
        trade.setName(tradeName);
        CompanyFinderFilter filter = new CompanyFinderFilterBuilder()
                .viewPort(
                        ViewPort.builder()
                                .northEast(LatLong.builder()
                                        .lat(neLat)
                                        .lng(neLong)
                                        .build())
                                .southWest(LatLong.builder()
                                        .lat(swLat)
                                        .lng(swLong)
                                        .build())
                                .build())
                .trade(trade)
                .build();
        String sql = contractorLocationDAO.getSQL(filter);
        String expected = "SELECT distinct cl FROM ContractorLocation cl JOIN cl.contractor ca JOIN ca.trades ct WHERE cl.latitude > :swLat AND cl.longitude > :swLong AND cl.latitude < :neLat AND cl.longitude < :neLong AND ct.trade.indexStart <= :tradeStart AND :tradeEnd <= ct.trade.indexEnd AND (ca.status = :active OR ca.status = :pending )";
        assertEquals(expected, sql);
    }

    @Test
    public void testGetSQL_filterWithSS() throws Exception {
        CompanyFinderFilter filter = new CompanyFinderFilterBuilder()
                .viewPort(
                        ViewPort.builder()
                                .northEast(LatLong.builder()
                                        .lat(neLat)
                                        .lng(neLong)
                                        .build())
                                .southWest(LatLong.builder()
                                        .lat(swLat)
                                        .lng(swLong)
                                        .build())
                                .build())
                .safetySensitive(SafetySensitive.INCLUDE)
                .build();
        String sql = contractorLocationDAO.getSQL(filter);
        String expected = "SELECT distinct cl FROM ContractorLocation cl JOIN cl.contractor ca WHERE cl.latitude > :swLat AND cl.longitude > :swLong AND cl.latitude < :neLat AND cl.longitude < :neLong AND ca.safetySensitive = :safetySensitive AND (ca.status = :active OR ca.status = :pending )";
        assertEquals(expected, sql);
    }

    @Test
    public void testGetSQL_filterWithTradeAndSS() throws Exception {
        Trade trade = new Trade();
        trade.setName(tradeName);
        CompanyFinderFilter filter = new CompanyFinderFilterBuilder()
                .viewPort(
                        ViewPort.builder()
                                .northEast(LatLong.builder()
                                        .lat(neLat)
                                        .lng(neLong)
                                        .build())
                                .southWest(LatLong.builder()
                                        .lat(swLat)
                                        .lng(swLong)
                                        .build())
                                .build())
                .trade(trade)
                .safetySensitive(SafetySensitive.EXCLUDE)
                .build();
        String sql = contractorLocationDAO.getSQL(filter);
        String expected = "SELECT distinct cl FROM ContractorLocation cl JOIN cl.contractor ca JOIN ca.trades ct WHERE cl.latitude > :swLat AND cl.longitude > :swLong AND cl.latitude < :neLat AND cl.longitude < :neLong AND ct.trade.indexStart <= :tradeStart AND :tradeEnd <= ct.trade.indexEnd AND ca.safetySensitive = :safetySensitive AND (ca.status = :active OR ca.status = :pending )";
        assertEquals(expected, sql);
    }

}