package com.picsauditing.service.audit;

import com.picsauditing.PicsTest;
import com.picsauditing.jpa.entities.AuditTypePeriod;
import com.picsauditing.jpa.entities.builders.AuditTypeBuilder;
import com.picsauditing.service.audit.AuditPeriodService;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class AuditPeriodServiceTest extends PicsTest {
    private AuditPeriodService test;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        test = new AuditPeriodService();
    }

    @Test
    public void testGetChildPeriodAuditFors() {
        List<String> answers = null;

        answers = test.getChildPeriodAuditFors("2010");
        assertEquals(4, answers.size());
        assertEquals("2010:1", answers.get(0));
        assertEquals("2010:2", answers.get(1));
        assertEquals("2010:3", answers.get(2));
        assertEquals("2010:4", answers.get(3));

        answers = test.getChildPeriodAuditFors("2010:1");
        assertEquals(3, answers.size());
        assertEquals("2010-01", answers.get(0));
        assertEquals("2010-02", answers.get(1));
        assertEquals("2010-03", answers.get(2));

        answers = test.getChildPeriodAuditFors("2010:2");
        assertEquals(3, answers.size());
        assertEquals("2010-04", answers.get(0));
        assertEquals("2010-05", answers.get(1));
        assertEquals("2010-06", answers.get(2));

        answers = test.getChildPeriodAuditFors("2010:3");
        assertEquals(3, answers.size());
        assertEquals("2010-07", answers.get(0));
        assertEquals("2010-08", answers.get(1));
        assertEquals("2010-09", answers.get(2));

        answers = test.getChildPeriodAuditFors("2010:4");
        assertEquals(3, answers.size());
        assertEquals("2010-10", answers.get(0));
        assertEquals("2010-11", answers.get(1));
        assertEquals("2010-12", answers.get(2));
    }

    @Test
    public void testGetAuditForByDate_AdvanceDays() {
        AuditTypeBuilder auditTypeBuilder = new AuditTypeBuilder();
        auditTypeBuilder.period(AuditTypePeriod.Monthly).maximumActive(1).advanceDays(10);

        Calendar date = Calendar.getInstance();
        date.set(Calendar.YEAR, 2013);
        date.set(Calendar.MONTH, Calendar.MARCH);
        date.set(Calendar.DAY_OF_MONTH, 23);

        List<String> results;

        results = test.getAuditForByDate(auditTypeBuilder.build(), date.getTime());
        assertTrue(results.size() == 1);
        assertTrue(results.get(0).equals("2013-03"));
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
        assertTrue(results.get(0).equals("2012-12"));
        assertTrue(results.get(1).equals("2013-01"));
        assertTrue(results.get(2).equals("2013-02"));
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
        assertTrue(results.get(0).equals("2012:2"));
        assertTrue(results.get(1).equals("2012:3"));
        assertTrue(results.get(2).equals("2012:4"));
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
        assertTrue(results.get(0).equals("2010"));
        assertTrue(results.get(1).equals("2011"));
        assertTrue(results.get(2).equals("2012"));
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
        assertTrue(results.get(0).equals("2011"));
        assertTrue(results.get(1).equals("2012"));
        assertTrue(results.get(2).equals("2013"));

        // test if the custom date is before the current date
        auditTypeBuilder.anchorMonth(2); //February
        results = test.getAuditForByDate(auditTypeBuilder.build(), date.getTime());
        assertTrue(results.size() == 3);
        assertTrue(results.get(0).equals("2010"));
        assertTrue(results.get(1).equals("2011"));
        assertTrue(results.get(2).equals("2012"));
    }

    @Test
    public void testGetEffectiveDateForMonthlyQuarterlyYearly_Monthly() throws Exception {
        AuditTypeBuilder auditTypeBuilder = new AuditTypeBuilder();
        auditTypeBuilder.period(AuditTypePeriod.Monthly);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date result;

        result = test.getEffectiveDateForMonthlyQuarterlyYearly(auditTypeBuilder.build(), "2012-01");
        assertTrue(formatter.format(result).equals("2012-01-01 00:00:00"));

        result = test.getEffectiveDateForMonthlyQuarterlyYearly(auditTypeBuilder.build(), "2012-12");
        assertTrue(formatter.format(result).equals("2012-12-01 00:00:00"));
    }

    @Test
    public void testGetEffectiveDateForMonthlyQuarterlyYearly_Quarterly() throws Exception {
        AuditTypeBuilder auditTypeBuilder = new AuditTypeBuilder();
        auditTypeBuilder.period(AuditTypePeriod.Quarterly);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date result;

        result = test.getEffectiveDateForMonthlyQuarterlyYearly(auditTypeBuilder.build(), "2012:1");
        assertTrue(formatter.format(result).equals("2012-01-01 00:00:00"));

        result = test.getEffectiveDateForMonthlyQuarterlyYearly(auditTypeBuilder.build(), "2012:2");
        assertTrue(formatter.format(result).equals("2012-04-01 00:00:00"));

        result = test.getEffectiveDateForMonthlyQuarterlyYearly(auditTypeBuilder.build(), "2012:3");
        assertTrue(formatter.format(result).equals("2012-07-01 00:00:00"));

        result = test.getEffectiveDateForMonthlyQuarterlyYearly(auditTypeBuilder.build(), "2012:4");
        assertTrue(formatter.format(result).equals("2012-10-01 00:00:00"));
    }

    @Test
    public void testGetEffectiveDateForMonthlyQuarterlyYearly_Yearly() throws Exception {
        AuditTypeBuilder auditTypeBuilder = new AuditTypeBuilder();
        auditTypeBuilder.period(AuditTypePeriod.Yearly);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date result;

        result = test.getEffectiveDateForMonthlyQuarterlyYearly(auditTypeBuilder.build(), "2012");
        assertTrue(formatter.format(result).equals("2012-01-01 00:00:00"));
    }

    @Test
    public void testGetEffectiveDateForMonthlyQuarterlyYearly_CustomDate() throws Exception {
        AuditTypeBuilder auditTypeBuilder = new AuditTypeBuilder();
        auditTypeBuilder.period(AuditTypePeriod.CustomDate).anchorDay(5).anchorMonth(6);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date result;

        result = test.getEffectiveDateForMonthlyQuarterlyYearly(auditTypeBuilder.build(), "2012");
        assertTrue(formatter.format(result).equals("2012-06-05 00:00:00"));
    }

    @Test
    public void testGetExpirationDateForMonthlyQuarterlyYearly_Monthly() throws Exception {
        AuditTypeBuilder auditTypeBuilder = new AuditTypeBuilder();
        auditTypeBuilder.period(AuditTypePeriod.Monthly);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date result;

        result = test.getExpirationDateForMonthlyQuarterlyYearly(auditTypeBuilder.build(), "2012-01");
        assertTrue(formatter.format(result).equals("2012-12-31 23:59:59"));

        result = test.getExpirationDateForMonthlyQuarterlyYearly(auditTypeBuilder.build(), "2012-12");
        assertTrue(formatter.format(result).equals("2013-11-30 23:59:59"));
    }

    @Test
    public void testGetExpirationDateForMonthlyQuarterlyYearly_Quarterly() throws Exception {
        AuditTypeBuilder auditTypeBuilder = new AuditTypeBuilder();
        auditTypeBuilder.period(AuditTypePeriod.Quarterly);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date result;

        result = test.getExpirationDateForMonthlyQuarterlyYearly(auditTypeBuilder.build(), "2012:1");
        assertTrue(formatter.format(result).equals("2012-12-31 23:59:59"));

        result = test.getExpirationDateForMonthlyQuarterlyYearly(auditTypeBuilder.build(), "2012:2");
        assertTrue(formatter.format(result).equals("2013-03-31 23:59:59"));

        result = test.getExpirationDateForMonthlyQuarterlyYearly(auditTypeBuilder.build(), "2012:3");
        assertTrue(formatter.format(result).equals("2013-06-30 23:59:59"));

        result = test.getExpirationDateForMonthlyQuarterlyYearly(auditTypeBuilder.build(), "2012:4");
        assertTrue(formatter.format(result).equals("2013-09-30 23:59:59"));
    }

    @Test
    public void testGetExpirationDateForMonthlyQuarterlyYearly_Yearly() throws Exception {
        AuditTypeBuilder auditTypeBuilder = new AuditTypeBuilder();
        auditTypeBuilder.period(AuditTypePeriod.Yearly);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date result;

        result = test.getExpirationDateForMonthlyQuarterlyYearly(auditTypeBuilder.build(), "2012");
        assertTrue(formatter.format(result).equals("2012-12-31 23:59:59"));
    }

    @Test
    public void testGetExpirationDateForMonthlyQuarterlyYearly_CustomDate() throws Exception {
        AuditTypeBuilder auditTypeBuilder = new AuditTypeBuilder();
        auditTypeBuilder.period(AuditTypePeriod.CustomDate).anchorDay(5).anchorMonth(6);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date result;

        result = test.getExpirationDateForMonthlyQuarterlyYearly(auditTypeBuilder.build(), "2012");
        assertTrue(formatter.format(result).equals("2013-06-04 23:59:59"));
    }

}
