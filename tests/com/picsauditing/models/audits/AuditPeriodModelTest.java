package com.picsauditing.models.audits;

import com.picsauditing.PicsTest;
import com.picsauditing.jpa.entities.AuditTypePeriod;
import com.picsauditing.jpa.entities.builders.AuditTypeBuilder;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.List;

import static junit.framework.Assert.assertTrue;

public class AuditPeriodModelTest extends PicsTest {
    private AuditPeriodModel test;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        test = new AuditPeriodModel();
    }

    @Test
    public void testGetAuditForByDate_Monthly() {
        AuditTypeBuilder auditTypeBuilder = new AuditTypeBuilder();
        auditTypeBuilder.period(AuditTypePeriod.Monthly).maximumActive(3);

        Calendar date = Calendar.getInstance();
        date.set(Calendar.YEAR, 2013);
        date.set(Calendar.MONTH, Calendar.MARCH);
        date.set(Calendar.DAY_OF_MONTH, 12);

        List<String> results;

        results = test.getAuditForByDate(auditTypeBuilder.build(), date.getTime());
        assertTrue(results.size() == 3);
        assertTrue(results.get(0).equals("2013-02"));
        assertTrue(results.get(1).equals("2013-01"));
        assertTrue(results.get(2).equals("2012-12"));
    }

    @Test
    public void testGetAuditForByDate_Quarterly() {
        AuditTypeBuilder auditTypeBuilder = new AuditTypeBuilder();
        auditTypeBuilder.period(AuditTypePeriod.Quarterly).maximumActive(3);

        Calendar date = Calendar.getInstance();
        date.set(Calendar.YEAR, 2013);
        date.set(Calendar.MONTH, Calendar.MARCH);
        date.set(Calendar.DAY_OF_MONTH, 12);

        List<String> results;

        results = test.getAuditForByDate(auditTypeBuilder.build(), date.getTime());
        assertTrue(results.size() == 3);
        assertTrue(results.get(0).equals("2012:4"));
        assertTrue(results.get(1).equals("2012:3"));
        assertTrue(results.get(2).equals("2012:2"));
    }

    @Test
    public void testGetAuditForByDate_Yearly() {
        AuditTypeBuilder auditTypeBuilder = new AuditTypeBuilder();
        auditTypeBuilder.period(AuditTypePeriod.Yearly).maximumActive(3);

        Calendar date = Calendar.getInstance();
        date.set(Calendar.YEAR, 2013);
        date.set(Calendar.MONTH, Calendar.MARCH);
        date.set(Calendar.DAY_OF_MONTH, 12);

        List<String> results;

        results = test.getAuditForByDate(auditTypeBuilder.build(), date.getTime());
        assertTrue(results.size() == 3);
        assertTrue(results.get(0).equals("2012"));
        assertTrue(results.get(1).equals("2011"));
        assertTrue(results.get(2).equals("2010"));
    }

    @Test
    public void testGetAuditForByDate_Custom() {
        AuditTypeBuilder auditTypeBuilder = new AuditTypeBuilder();
        auditTypeBuilder.period(AuditTypePeriod.CustomDate).maximumActive(3).anchorDay(1);

        Calendar date = Calendar.getInstance();
        date.set(Calendar.YEAR, 2013);
        date.set(Calendar.MONTH, Calendar.MARCH);
        date.set(Calendar.DAY_OF_MONTH, 12);

        List<String> results;

        // test if the custom date is after the current date
        auditTypeBuilder.anchorMonth(4); //April
        results = test.getAuditForByDate(auditTypeBuilder.build(), date.getTime());
        assertTrue(results.size() == 3);
        assertTrue(results.get(0).equals("2013"));
        assertTrue(results.get(1).equals("2012"));
        assertTrue(results.get(2).equals("2011"));

        // test if the custom date is before the current date
        auditTypeBuilder.anchorMonth(2); //February
        results = test.getAuditForByDate(auditTypeBuilder.build(), date.getTime());
        assertTrue(results.size() == 3);
        assertTrue(results.get(0).equals("2012"));
        assertTrue(results.get(1).equals("2011"));
        assertTrue(results.get(2).equals("2010"));
    }

}
