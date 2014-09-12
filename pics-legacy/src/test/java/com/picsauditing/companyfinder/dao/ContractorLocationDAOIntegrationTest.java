package com.picsauditing.companyfinder.dao;

import com.atlassian.crowd.util.Assert;
import com.picsauditing.companyfinder.model.CompanyFinderFilter;
import com.picsauditing.companyfinder.model.ContractorLocation;
import com.picsauditing.companyfinder.model.TriStateFlag;
import com.picsauditing.companyfinder.model.ViewPort;
import com.picsauditing.companyfinder.model.builder.CompanyFinderFilterBuilder;
import com.picsauditing.dao.AbstractTransactionalTest;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.mail.NoUsersDefinedException;
import com.picsauditing.model.general.LatLong;
import edu.emory.mathcs.backport.java.util.Arrays;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@Ignore("Uncomment me to run this test (See ApplicationContext.xml and connection_localTest.properties for db props)")
@RunWith(SpringJUnit4ClassRunner.class)
public class ContractorLocationDAOIntegrationTest extends AbstractTransactionalTest {

    public static final int ID = 709;
    double neLat = 33.82667830089045;
    double neLong = -118.20405049182125;
    double swLat = 33.791020005057;
    double swLong = -118.26842350817867;
    boolean ss = true;
    String tradeName = "welder";

    @Autowired
    ContractorLocationDAO contractorLocationDAO;

    @Autowired
    ContractorAccountDAO contractorAccountDAO;

    @Test
    public void testFindByViewPortAndTradeAndSafety() throws Exception {
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

        List<ContractorLocation> cList = contractorLocationDAO.findContractorLocations(filter);
        Assert.notNull(cList);
    }

    @Test
    public void testFindById() throws Exception {
        ContractorLocation contractorLocation = contractorLocationDAO.find(ContractorLocation.class, ID);
        assertNotNull(contractorLocation);
    }

    @Test
    public void testUpdate() throws Exception {
        Double lat = 33.860607;
        ContractorLocation contractorLocation = contractorLocationDAO.find(ContractorLocation.class, ID);
        assertNotNull(contractorLocation);
        assertEquals(lat, contractorLocation.getLatitude());

        double latModified = lat * 2;
        contractorLocation.setLatitude(latModified);

        contractorLocation = (ContractorLocation) contractorLocationDAO.save(contractorLocation);

        assertEquals(latModified, contractorLocation.getLatitude());
    }

    @Test
    public void testInsert() throws Exception {
        Double lat = 33.860607;
        Double lon = -118.202309;
        ContractorAccount contractorAccount = contractorAccountDAO.find(3);
        assertNotNull(contractorAccount);

        ContractorLocation contractorLocation = new ContractorLocation();
        buildContratorLocation(contractorAccount, contractorLocation, lat, lon);
        assertNotNull(contractorLocation);
        assertEquals(Math.round(lat), Math.round(contractorLocation.getLatitude()));

        double latModified = lat * 2;
        contractorLocation.setLatitude(latModified);

        contractorLocationDAO.save(contractorLocation);

        assertEquals(Math.round(latModified), Math.round(contractorLocation.getLatitude()));
        assertNotNull(contractorLocation.getId());
    }

    private void buildContratorLocation(ContractorAccount contractorAccount, ContractorLocation contractorLocation, Double latitude, Double longitude) throws NoUsersDefinedException {
        contractorLocation.setContractor(contractorAccount);
        contractorLocation.setLatitude(latitude);
        contractorLocation.setLongitude(longitude);
        contractorLocation.setCreatedBy(contractorAccount.getActiveUser());
        contractorLocation.setUpdatedBy(contractorAccount.getActiveUser());
        contractorLocation.setCreationDate(new Date(System.currentTimeMillis()));
        contractorLocation.setUpdateDate(new Date(System.currentTimeMillis()));
    }
}