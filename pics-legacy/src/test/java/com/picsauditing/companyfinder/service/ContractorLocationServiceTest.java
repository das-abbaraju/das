package com.picsauditing.companyfinder.service;

import com.picsauditing.companyfinder.dao.ContractorLocationDao;
import com.picsauditing.dao.AbstractTransactionalTest;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class ContractorLocationServiceTest {

    ContractorLocationService locationService;
    //ContractorAccountDAO contractorAccountDAO;

    int contractorId = 3;

    @Before
    public void setUp() throws Exception {
        locationService = new ContractorLocationService();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testSaveLocation() throws Exception {
      //  locationService.saveLocation(ca);
    }

    @Test
    public void testFetchGeoLocation() throws Exception {

    }
}