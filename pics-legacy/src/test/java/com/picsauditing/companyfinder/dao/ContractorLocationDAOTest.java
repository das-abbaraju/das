package com.picsauditing.companyfinder.dao;

import com.picsauditing.companyfinder.model.CompanyFinderFilter;
import com.picsauditing.companyfinder.model.TriStateFlag;
import com.picsauditing.companyfinder.model.ViewPort;
import com.picsauditing.companyfinder.model.builder.CompanyFinderFilterBuilder;
import com.picsauditing.model.general.LatLong;
import com.picsauditing.util.Strings;
import edu.emory.mathcs.backport.java.util.Arrays;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;

public class ContractorLocationDAOTest {

    private ContractorLocationDAO contractorLocationDAO;
    double neLat = 33.82667830089045;
    double neLong = -118.20405049182125;
    double swLat = 33.791020005057;
    double swLong = -118.26842350817867;
    boolean ss = true;

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
        String expected = "SELECT distinct cl FROM ContractorLocation cl JOIN cl.contractor ca WHERE (ca.status = :active OR ca.status = :pending ) AND cl.latitude > :swLat AND cl.longitude > :swLong AND cl.latitude < :neLat AND cl.longitude < :neLong";
        assertEquals(expected, sql);
    }

    @Test
    public void testGetSQL_filterWithTrade() throws Exception {

        List<Integer> tradeIds = Arrays.asList(new Integer[]{122, 2333, 344, 423, 545});
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
                .tradeIds(tradeIds)
                .build();
        String sql = contractorLocationDAO.getSQL(filter);
        String expected = "SELECT distinct cl FROM ContractorLocation cl JOIN cl.contractor ca JOIN ca.trades ct WHERE (ca.status = :active OR ca.status = :pending ) AND cl.latitude > :swLat AND cl.longitude > :swLong AND cl.latitude < :neLat AND cl.longitude < :neLong AND ct.trade.id IN :tradeList";
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
                .safetySensitive(TriStateFlag.INCLUDE)
                .build();
        String sql = contractorLocationDAO.getSQL(filter);
        String expected = "SELECT distinct cl FROM ContractorLocation cl JOIN cl.contractor ca WHERE (ca.status = :active OR ca.status = :pending ) AND cl.latitude > :swLat AND cl.longitude > :swLong AND cl.latitude < :neLat AND cl.longitude < :neLong AND ca.safetySensitive = :safetySensitive";
        assertEquals(expected, sql);
    }

    @Test
    public void testGetSQL_filterWithTradeAndSS() throws Exception {
        List<Integer> tradeIds = Arrays.asList(new Integer[]{122, 2333, 344, 423, 545});

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
                .tradeIds(tradeIds)
                .safetySensitive(TriStateFlag.EXCLUDE)
                .build();
        String sql = contractorLocationDAO.getSQL(filter);
        String expected = "SELECT distinct cl FROM ContractorLocation cl JOIN cl.contractor ca JOIN ca.trades ct WHERE (ca.status = :active OR ca.status = :pending ) AND cl.latitude > :swLat AND cl.longitude > :swLong AND cl.latitude < :neLat AND cl.longitude < :neLong AND ct.trade.id IN :tradeList AND ca.safetySensitive = :safetySensitive";
        assertEquals(expected, sql);
    }

    @Test
    public void testGetSQL_filterWithSP() throws Exception {
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
                .soleProprietor(TriStateFlag.INCLUDE)
                .build();
        String sql = contractorLocationDAO.getSQL(filter);
        String expected = "SELECT distinct cl FROM ContractorLocation cl JOIN cl.contractor ca WHERE (ca.status = :active OR ca.status = :pending ) AND cl.latitude > :swLat AND cl.longitude > :swLong AND cl.latitude < :neLat AND cl.longitude < :neLong AND ca.soleProprietor = :soleProprietor";
        assertEquals(expected, sql);
    }

    @Test
    public void testGetSQL_filterWithTradeAndSP() throws Exception {
        List<Integer> tradeIds = Arrays.asList(new Integer[]{122, 2333, 344, 423, 545});

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
                .tradeIds(tradeIds)
                .safetySensitive(TriStateFlag.EXCLUDE)
                .soleProprietor(TriStateFlag.EXCLUDE)
                .build();
        String sql = contractorLocationDAO.getSQL(filter);
        String expected = "SELECT distinct cl FROM ContractorLocation cl JOIN cl.contractor ca JOIN ca.trades ct WHERE (ca.status = :active OR ca.status = :pending ) AND cl.latitude > :swLat AND cl.longitude > :swLong AND cl.latitude < :neLat AND cl.longitude < :neLong AND ct.trade.id IN :tradeList AND ca.safetySensitive = :safetySensitive AND ca.soleProprietor = :soleProprietor";
        assertEquals(expected, sql);
    }

    @Test
    public void testGetSQL_filterWithContractorIds() throws Exception {
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
                .contractorIds(Strings.explodeCommaDelimitedStringOfIds("123,456,78"))
                .build();
        String sql = contractorLocationDAO.getSQL(filter);
        String expected = "SELECT distinct cl FROM ContractorLocation cl JOIN cl.contractor ca WHERE (ca.status = :active OR ca.status = :pending ) AND cl.latitude > :swLat AND cl.longitude > :swLong AND cl.latitude < :neLat AND cl.longitude < :neLong AND ca.id IN :contractorIds";
        assertEquals(expected, sql);
    }

    @Test
    public void testGetSQLSummaryNative_AllFilters() throws Exception {
        List<Integer> tradeIds = Arrays.asList(new Integer[]{122, 2333, 344, 423, 545});
        List<Integer> contractorIds = Arrays.asList(new Integer[]{55, 66, 77});

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
                .tradeIds(tradeIds)
                .safetySensitive(TriStateFlag.INCLUDE)
                .soleProprietor(TriStateFlag.INCLUDE)
                .contractorIds(contractorIds)
                .build();
        String sql = contractorLocationDAO.getSQLNativeSummary(filter);
        String expected = "SELECT distinct cl.* FROM contractor_location cl JOIN contractor_info ca ON cl.conId = ca.id JOIN accounts a ON cl.conId = a.id JOIN contractor_trade ct ON a.id = ct.conId WHERE (a.status = :active OR a.status = :pending ) AND cl.latitude > :swLat AND cl.longitude > :swLong AND cl.latitude < :neLat AND cl.longitude < :neLong AND ct.id IN (:tradeList) AND ca.safetySensitive = :safetySensitive AND ca.soleProprietor = :soleProprietor AND ca.id IN (:contractorIds)";
        assertEquals(expected, sql);
    }


}