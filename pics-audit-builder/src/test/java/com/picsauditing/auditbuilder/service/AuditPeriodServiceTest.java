package com.picsauditing.auditbuilder.service;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTest;
import com.picsauditing.auditbuilder.entities.*;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.*;

public class AuditPeriodServiceTest extends PicsTest {
    private AuditPeriodService test;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        test = new AuditPeriodService();
    }

    @Test
    public void testShouldCreateAudit() {
        AuditType yearlyAuditType = EntityFactory.makeAuditType();
        AuditType quarterlyAuditType = EntityFactory.makeAuditType();
        AuditType monthlyAuditType = EntityFactory.makeAuditType();

        yearlyAuditType.setPeriod(AuditTypePeriod.Yearly);
        quarterlyAuditType.setPeriod(AuditTypePeriod.Quarterly);
        monthlyAuditType.setPeriod(AuditTypePeriod.Monthly);

        List<ContractorAudit> audits = new ArrayList<>();

        // no child audit type
        assertTrue(test.shouldCreateAudit(audits, yearlyAuditType, "2013", null));

        // has child type but no children
        assertFalse(test.shouldCreateAudit(audits, yearlyAuditType, "2013", quarterlyAuditType));

        // has child audit type but no valid children
        audits.add(createAudit(quarterlyAuditType, "2013:1", AuditStatus.NotApplicable));
        assertFalse(test.shouldCreateAudit(audits, yearlyAuditType, "2013", quarterlyAuditType));

        // has child audit type and a valid child
        audits.add(createAudit(quarterlyAuditType, "2013:2", AuditStatus.Complete));
        assertTrue(test.shouldCreateAudit(audits, yearlyAuditType, "2013", quarterlyAuditType));
    }

    private ContractorAudit createAudit(AuditType auditType, String auditFor, AuditStatus status) {
        ContractorAudit audit = new ContractorAudit();
        audit.setAuditType(auditType);
        audit.setAuditFor(auditFor);

        ContractorAuditOperator cao = new ContractorAuditOperator();
        cao.setStatus(status);
        audit.getOperators().add(cao);

        return audit;
    }

    @Test
    public void testGetChildPeriodAuditFors() {
        List<String> answers = null;

        answers = test.getChildPeriodAuditFors(null);
        assertTrue(answers.size() == 0);

        answers = test.getChildPeriodAuditFors("");
        assertTrue(answers.size() == 0);

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
        AuditType auditType = new AuditType();
        auditType.setPeriod(AuditTypePeriod.Monthly);
        auditType.setMaximumActive(1);
        auditType.setAdvanceDays(10);

        Calendar date = Calendar.getInstance();
        date.set(Calendar.YEAR, 2013);
        date.set(Calendar.MONTH, Calendar.MARCH);
        date.set(Calendar.DAY_OF_MONTH, 23);

        List<String> results;

        results = test.getAuditForByDate(auditType, date.getTime());
        assertTrue(results.size() == 1);
        assertTrue(results.get(0).equals("2013-03"));
    }

    @Test
    public void testGetAuditForByDate_Monthly() {
        AuditType auditType = new AuditType();
        auditType.setPeriod(AuditTypePeriod.Monthly);
        auditType.setMaximumActive(3);

        Calendar date = Calendar.getInstance();
        date.set(Calendar.YEAR, 2013);
        date.set(Calendar.MONTH, Calendar.MARCH);
        date.set(Calendar.DAY_OF_MONTH, 12);

        List<String> results;

        results = test.getAuditForByDate(auditType, date.getTime());
        assertTrue(results.size() == 3);
        assertTrue(results.get(0).equals("2012-12"));
        assertTrue(results.get(1).equals("2013-01"));
        assertTrue(results.get(2).equals("2013-02"));
    }

    @Test
    public void testGetAuditForByDate_Quarterly() {
        AuditType auditType = new AuditType();
        auditType.setPeriod(AuditTypePeriod.Quarterly);
        auditType.setMaximumActive(3);

        Calendar date = Calendar.getInstance();
        date.set(Calendar.YEAR, 2013);
        date.set(Calendar.MONTH, Calendar.MARCH);
        date.set(Calendar.DAY_OF_MONTH, 12);

        List<String> results;

        results = test.getAuditForByDate(auditType, date.getTime());
        assertTrue(results.size() == 3);
        assertTrue(results.get(0).equals("2012:2"));
        assertTrue(results.get(1).equals("2012:3"));
        assertTrue(results.get(2).equals("2012:4"));
    }

    @Test
    public void testGetAuditForByDate_Yearly() {
        AuditType auditType = new AuditType();
        auditType.setPeriod(AuditTypePeriod.Yearly);
        auditType.setMaximumActive(3);

        Calendar date = Calendar.getInstance();
        date.set(Calendar.YEAR, 2013);
        date.set(Calendar.MONTH, Calendar.MARCH);
        date.set(Calendar.DAY_OF_MONTH, 12);

        List<String> results;

        results = test.getAuditForByDate(auditType, date.getTime());
        assertTrue(results.size() == 3);
        assertTrue(results.get(0).equals("2010"));
        assertTrue(results.get(1).equals("2011"));
        assertTrue(results.get(2).equals("2012"));
    }

    @Test
    public void testGetAuditForByDate_Custom() {
        AuditType auditType = new AuditType();
        auditType.setPeriod(AuditTypePeriod.CustomDate);
        auditType.setMaximumActive(3);
        auditType.setAnchorDay(1);

        Calendar date = Calendar.getInstance();
        date.set(Calendar.YEAR, 2013);
        date.set(Calendar.MONTH, Calendar.MARCH);
        date.set(Calendar.DAY_OF_MONTH, 12);

        List<String> results;

        // test if the custom date is after the current date
        auditType.setAnchorMonth(4); //April
        results = test.getAuditForByDate(auditType, date.getTime());
        assertTrue(results.size() == 3);
        assertTrue(results.get(0).equals("2011"));
        assertTrue(results.get(1).equals("2012"));
        assertTrue(results.get(2).equals("2013"));

        // test if the custom date is before the current date
        auditType.setAnchorMonth(2); //February
        results = test.getAuditForByDate(auditType, date.getTime());
        assertTrue(results.size() == 3);
        assertTrue(results.get(0).equals("2010"));
        assertTrue(results.get(1).equals("2011"));
        assertTrue(results.get(2).equals("2012"));
    }

    @Test
    public void testGetEffectiveDateForMonthlyQuarterlyYearly_Monthly() throws Exception {
        AuditType auditType = new AuditType();
        auditType.setPeriod(AuditTypePeriod.Monthly);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date result;

        result = test.getEffectiveDateForMonthlyQuarterlyYearly(auditType, "2012-01");
        assertTrue(formatter.format(result).equals("2012-01-01 00:00:00"));

        result = test.getEffectiveDateForMonthlyQuarterlyYearly(auditType, "2012-12");
        assertTrue(formatter.format(result).equals("2012-12-01 00:00:00"));
    }

    @Test
    public void testGetEffectiveDateForMonthlyQuarterlyYearly_Quarterly() throws Exception {
        AuditType auditType = new AuditType();
        auditType.setPeriod(AuditTypePeriod.Quarterly);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date result;

        result = test.getEffectiveDateForMonthlyQuarterlyYearly(auditType, "2012:1");
        assertTrue(formatter.format(result).equals("2012-01-01 00:00:00"));

        result = test.getEffectiveDateForMonthlyQuarterlyYearly(auditType, "2012:2");
        assertTrue(formatter.format(result).equals("2012-04-01 00:00:00"));

        result = test.getEffectiveDateForMonthlyQuarterlyYearly(auditType, "2012:3");
        assertTrue(formatter.format(result).equals("2012-07-01 00:00:00"));

        result = test.getEffectiveDateForMonthlyQuarterlyYearly(auditType, "2012:4");
        assertTrue(formatter.format(result).equals("2012-10-01 00:00:00"));
    }

    @Test
    public void testGetEffectiveDateForMonthlyQuarterlyYearly_Yearly() throws Exception {
        AuditType auditType = new AuditType();
        auditType.setPeriod(AuditTypePeriod.Yearly);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date result;

        result = test.getEffectiveDateForMonthlyQuarterlyYearly(auditType, "2012");
        assertTrue(formatter.format(result).equals("2012-01-01 00:00:00"));
    }

    @Test
    public void testGetEffectiveDateForMonthlyQuarterlyYearly_CustomDate() throws Exception {
        AuditType auditType = new AuditType();
        auditType.setPeriod(AuditTypePeriod.CustomDate);
        auditType.setAnchorDay(5);
        auditType.setAnchorMonth(6);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date result;

        result = test.getEffectiveDateForMonthlyQuarterlyYearly(auditType, "2012");
        assertTrue(formatter.format(result).equals("2012-06-05 00:00:00"));
    }

    @Test
    public void testGetExpirationDateForMonthlyQuarterlyYearly_Monthly() throws Exception {
        AuditType auditType = new AuditType();
        auditType.setPeriod(AuditTypePeriod.Monthly);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date result;

        result = test.getExpirationDateForMonthlyQuarterlyYearly(auditType, "2012-01");
        assertTrue(formatter.format(result).equals("2012-12-31 23:59:59"));

        result = test.getExpirationDateForMonthlyQuarterlyYearly(auditType, "2012-12");
        assertTrue(formatter.format(result).equals("2013-11-30 23:59:59"));
    }

    @Test
    public void testGetExpirationDateForMonthlyQuarterlyYearly_Quarterly() throws Exception {
        AuditType auditType = new AuditType();
        auditType.setPeriod(AuditTypePeriod.Quarterly);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date result;

        result = test.getExpirationDateForMonthlyQuarterlyYearly(auditType, "2012:1");
        assertTrue(formatter.format(result).equals("2012-12-31 23:59:59"));

        result = test.getExpirationDateForMonthlyQuarterlyYearly(auditType, "2012:2");
        assertTrue(formatter.format(result).equals("2013-03-31 23:59:59"));

        result = test.getExpirationDateForMonthlyQuarterlyYearly(auditType, "2012:3");
        assertTrue(formatter.format(result).equals("2013-06-30 23:59:59"));

        result = test.getExpirationDateForMonthlyQuarterlyYearly(auditType, "2012:4");
        assertTrue(formatter.format(result).equals("2013-09-30 23:59:59"));
    }

    @Test
    public void testGetExpirationDateForMonthlyQuarterlyYearly_Yearly() throws Exception {
        AuditType auditType = new AuditType();
        auditType.setPeriod(AuditTypePeriod.Yearly);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date result;

        result = test.getExpirationDateForMonthlyQuarterlyYearly(auditType, "2012");
        assertTrue(formatter.format(result).equals("2012-12-31 23:59:59"));
    }

    @Test
    public void testGetExpirationDateForMonthlyQuarterlyYearly_CustomDate() throws Exception {
        AuditType auditType = new AuditType();
        auditType.setPeriod(AuditTypePeriod.CustomDate);
        auditType.setAnchorDay(5);
        auditType.setAnchorMonth(6);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date result;

        result = test.getExpirationDateForMonthlyQuarterlyYearly(auditType, "2012");
        assertTrue(formatter.format(result).equals("2013-06-04 23:59:59"));
    }

}
