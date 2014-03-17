package com.picsauditing.jpa.entities;

import com.picsauditing.service.ReportService;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by kchase on 3/17/14.
 */
public class EmailSubscriptionTest {
    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testGetReport_Creation() {
        EmailSubscription sub = new EmailSubscription();
        assertNotNull(sub.getReport());
        assertEquals(ReportService.CONTRACTOR_IST_REPORT_ID, sub.getReport().getId());
    }
}
