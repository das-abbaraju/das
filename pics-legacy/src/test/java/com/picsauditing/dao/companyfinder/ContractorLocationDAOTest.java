package com.picsauditing.dao.companyfinder;

import com.atlassian.crowd.util.Assert;
import com.picsauditing.companyfinder.model.ContractorLocation;
import com.picsauditing.dao.AbstractTransactionalTest;
import com.picsauditing.jpa.entities.Trade;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@Ignore("Uncomment me to run this test (See ApplicationContext.xml and connection_localTest.properties for db props)")
@RunWith(SpringJUnit4ClassRunner.class)
public class ContractorLocationDAOTest extends AbstractTransactionalTest {

    double neLat = 33.82667830089045;
    double neLong = -118.20405049182125;
    double swLat = 33.791020005057;
    double swLong = -118.26842350817867;
    boolean ss = true;
    String tradeName = "welder";

    @Autowired
    ContractorLocationDAO contractorLocationDAO;

    @Test
    public void testFindByViewPortAndTradeAndSafety() throws Exception {
        Trade trade = new Trade();
        trade.setName(tradeName);
        List<ContractorLocation> cList = contractorLocationDAO.findContractorLocations(neLat, neLong, swLat, swLong, trade, ss);
        Assert.notNull(cList);
    }
}
