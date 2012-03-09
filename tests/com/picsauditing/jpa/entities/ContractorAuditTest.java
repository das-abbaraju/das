package com.picsauditing.jpa.entities;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import com.picsauditing.PICS.DateBean;

/**
 * Please keep in mind that some of these tests may fail randomly due to the
 * way that Java does a Date Comparison with the Date.befor() and Date.after()
 * methods. Both of those methods compare the time, in milliseconds, between
 * the dates, which can result in strange behaviors when setting the expiration
 * date and then performing the expiration check.
 */
public class ContractorAuditTest {

	@Test
	public void testIsExpiringSoon_ExpirationIsToday() {
		ContractorAudit contractorAudit = new ContractorAudit();
		contractorAudit.setExpiresDate(new Date());
		assertTrue(contractorAudit.isExpiringSoon());
	}
	
	@Test
	public void testIsExpiringSoon_ExpirationIsTomorrow() {
		ContractorAudit contractorAudit = new ContractorAudit();
		contractorAudit.setExpiresDate(DateBean.addDays(new Date(), 1));
		assertTrue(contractorAudit.isExpiringSoon());
	}
	
	@Test
	public void testIsExpiringSoon_ExpirationIsYesterday() {
		ContractorAudit contractorAudit = new ContractorAudit();
		contractorAudit.setExpiresDate(DateBean.addDays(new Date(), -1));
		assertTrue(contractorAudit.isExpiringSoon());
	}
	
	@Test
	public void testIsExpiringSoon_ExpirationIsOneMonthFromNow() {
		ContractorAudit contractorAudit = new ContractorAudit();
		contractorAudit.setExpiresDate(DateBean.addMonths(new Date(), 1));
		assertFalse(contractorAudit.isExpiringSoon());
	}
	
	@Test
	public void testIsExpiringSoon_ExpirationIsOneMonthAgo() {
		ContractorAudit contractorAudit = new ContractorAudit();
		contractorAudit.setExpiresDate(DateBean.addMonths(new Date(), -1));
		assertFalse(contractorAudit.isExpiringSoon());
	}
	
	@Test
	public void testIsExpiringRenewableAudit_ExpiresYesterday() {
		ContractorAudit contractorAudit = new ContractorAudit();
		contractorAudit.setExpiresDate(DateBean.addDays(new Date(), -1));
		AuditType auditType = new AuditType();
		auditType.setRenewable(true);
		contractorAudit.setAuditType(auditType);
		assertTrue(contractorAudit.isExpiringRenewableAudit());
	}
	
	@Test
	public void testIsExpiringRenewableAudit_NotRenewable() {
		ContractorAudit contractorAudit = new ContractorAudit();
		contractorAudit.setExpiresDate(DateBean.addDays(new Date(), -1));
		AuditType auditType = new AuditType();
		auditType.setRenewable(false);
		contractorAudit.setAuditType(auditType);
		assertFalse(contractorAudit.isExpiringRenewableAudit());
	}
	
	@Test
	public void testWillExpireWithinTwoWeeks_NullExpirationDate() {
		ContractorAudit contractorAudit = new ContractorAudit();
		contractorAudit.setExpiresDate(null);
		assertFalse(contractorAudit.willExpireWithinTwoWeeks());
	}
	
	@Test
	public void testWillExpireWithinTwoWeeks_ExpirationDateIsToday() {
		ContractorAudit contractorAudit = new ContractorAudit();
		contractorAudit.setExpiresDate(new Date());
		assertFalse(contractorAudit.willExpireWithinTwoWeeks());
	}
	
	@Test
	public void testWillExpireWithinTwoWeeks_ExpirationDateIsTomorrow() {
		ContractorAudit contractorAudit = new ContractorAudit();
		contractorAudit.setExpiresDate(DateBean.addField(new Date(), Calendar.DATE, 1));
		assertTrue(contractorAudit.willExpireWithinTwoWeeks());
	}
	
	@Test
	public void testWillExpireWithinTwoWeeks_ExpirationDateIsInTwoWeeks() {
		ContractorAudit contractorAudit = new ContractorAudit();
		contractorAudit.setExpiresDate(DateBean.addField(new Date(), Calendar.WEEK_OF_YEAR, 2));
		assertFalse(contractorAudit.willExpireWithinTwoWeeks());
	}
	
	@Test
	public void testWillExpireWithinTwoWeeks_ExpirationDateIsOneDayBeforeTwoWeeksAgo() {
		ContractorAudit contractorAudit = new ContractorAudit();
		contractorAudit.setExpiresDate(DateBean.addField(DateBean.addField(new Date(), Calendar.WEEK_OF_YEAR, 2), Calendar.DATE, -1));
		assertTrue(contractorAudit.willExpireWithinTwoWeeks());
	}
	
	@Test
	public void testExpiredUpToAWeekAgo_NullExpirationDate() {
		ContractorAudit contractorAudit = new ContractorAudit();
		contractorAudit.setExpiresDate(null);
		assertFalse(contractorAudit.expiredUpToAWeekAgo());
	}
	
	@Test
	public void testExpiredUpToAWeekAgo_ExpirationDateIsToday() {
		ContractorAudit contractorAudit = new ContractorAudit();
		contractorAudit.setExpiresDate(new Date());
		assertFalse(contractorAudit.expiredUpToAWeekAgo());
	}
	
	@Test
	public void testExpiredUpToAWeekAgo_ExpirationDateIsTomorrow() {
		ContractorAudit contractorAudit = new ContractorAudit();
		contractorAudit.setExpiresDate(DateBean.addField(new Date(), Calendar.DATE, 1));
		assertFalse(contractorAudit.expiredUpToAWeekAgo());
	}
	
	@Test
	public void testExpiredUpToAWeekAgo_ExpirationDateIsOneWeekAgo() {
		ContractorAudit contractorAudit = new ContractorAudit();
		contractorAudit.setExpiresDate(DateBean.addField(new Date(), Calendar.WEEK_OF_YEAR, -1));
		assertFalse(contractorAudit.expiredUpToAWeekAgo());
	}
	
	@Test
	public void testExpiredUpToAWeekAgo_ExpirationDateIsLessThanOneWeekAgo() {
		ContractorAudit contractorAudit = new ContractorAudit();
		contractorAudit.setExpiresDate(DateBean.addField(DateBean.addField(new Date(), Calendar.WEEK_OF_YEAR, -1), Calendar.DATE, 1));
		assertTrue(contractorAudit.expiredUpToAWeekAgo());
	}
	
}
