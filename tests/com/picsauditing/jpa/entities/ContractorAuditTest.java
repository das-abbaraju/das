package com.picsauditing.jpa.entities;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.picsauditing.EntityFactory;
import com.picsauditing.PICS.DateBean;

public class ContractorAuditTest {
	private ContractorAudit contractorAudit;
	
	@Before
	public void setUp() throws Exception {
		contractorAudit = new ContractorAudit();
	}
	
	@Test
	public void testExpiredUpToAWeekAgo_ExpirationDateIsToday() {
		contractorAudit.setExpiresDate(DateBean.addField(new Date(), Calendar.HOUR, 1));
		assertFalse(contractorAudit.expiredUpToAWeekAgo());
	}
	
	@Test
	public void testWillExpireWithinTwoWeeks_ExpirationDateIsInTwoWeeks() {
		Date oneHourFromNow = DateBean.addField(new Date(), Calendar.HOUR, 1);
		contractorAudit.setExpiresDate(DateBean.addField(oneHourFromNow, Calendar.WEEK_OF_YEAR, 2));
		assertFalse(contractorAudit.willExpireWithinTwoWeeks());
	}
	
	@Test
	public void testIsExpiringSoon_ExpirationIsToday() {
		contractorAudit.setExpiresDate(DateBean.addField(new Date(), Calendar.HOUR, 1));
		assertTrue("testIsExpiringSoon_ExpirationIsToday", contractorAudit.isExpiringSoon());
	}

	@Test
	public void testIsExpiringSoon_ExpirationIsTomorrow() {
		contractorAudit.setExpiresDate(DateBean.addDays(new Date(), 1));
		assertTrue(contractorAudit.isExpiringSoon());
	}

	@Test
	public void testIsExpiringSoon_ExpirationIsYesterday() {
		contractorAudit.setExpiresDate(DateBean.addDays(new Date(), -1));
		assertTrue(contractorAudit.isExpiringSoon());
	}

	@Test
	public void testIsExpiringSoon_ExpirationIsOneMonthFromNow() {
		contractorAudit.setExpiresDate(DateBean.addMonths(new Date(), 1));
		assertFalse(contractorAudit.isExpiringSoon());
	}

	@Test
	public void testIsExpiringSoon_ExpirationIsOneMonthAgo() {
		contractorAudit.setExpiresDate(DateBean.addMonths(new Date(), -1));
		assertFalse(contractorAudit.isExpiringSoon());
	}

	@Test
	public void testIsExpiringRenewableAudit_ExpiresYesterday() {
		contractorAudit.setExpiresDate(DateBean.addDays(new Date(), -1));
		AuditType auditType = new AuditType();
		auditType.setRenewable(true);
		contractorAudit.setAuditType(auditType);
		assertTrue(contractorAudit.isExpiringRenewableAudit());
	}

	@Test
	public void testIsExpiringRenewableAudit_NotRenewable() {
		contractorAudit.setExpiresDate(DateBean.addDays(new Date(), -1));
		AuditType auditType = new AuditType();
		auditType.setRenewable(false);
		contractorAudit.setAuditType(auditType);
		assertFalse(contractorAudit.isExpiringRenewableAudit());
	}
	
	@Test
	public void testWillExpireWithinTwoWeeks_NullExpirationDate() {
		contractorAudit.setExpiresDate(null);
		assertFalse(contractorAudit.willExpireWithinTwoWeeks());
	}
	
	@Test
	public void testWillExpireWithinTwoWeeks_ExpirationDateIsToday() {
		contractorAudit.setExpiresDate(DateBean.addField(new Date(), Calendar.HOUR, 1));
		assertTrue("testWillExpireWithinTwoWeeks_ExpirationDateIsToday", contractorAudit.willExpireWithinTwoWeeks());
	}
	
	@Test
	public void testWillExpireWithinTwoWeeks_ExpirationDateIsTomorrow() {
		contractorAudit.setExpiresDate(DateBean.addField(new Date(), Calendar.DATE, 1));
		assertTrue(contractorAudit.willExpireWithinTwoWeeks());
	}
	
	@Test
	public void testWillExpireWithinTwoWeeks_ExpirationDateIsOneDayBeforeTwoWeeksAgo() {
		contractorAudit.setExpiresDate(DateBean.addField(DateBean.addField(new Date(), Calendar.WEEK_OF_YEAR, 2), Calendar.DATE, -1));
		assertTrue(contractorAudit.willExpireWithinTwoWeeks());
	}
	
	@Test
	public void testExpiredUpToAWeekAgo_NullExpirationDate() {
		contractorAudit.setExpiresDate(null);
		assertFalse(contractorAudit.expiredUpToAWeekAgo());
	}
	
	@Test
	public void testExpiredUpToAWeekAgo_ExpirationDateIsTomorrow() {
		contractorAudit.setExpiresDate(DateBean.addField(new Date(), Calendar.DATE, 1));
		assertFalse(contractorAudit.expiredUpToAWeekAgo());
	}
	
	@Test
	public void testExpiredUpToAWeekAgo_ExpirationDateIsOneWeekAgo() {
		contractorAudit.setExpiresDate(DateBean.addField(new Date(), Calendar.WEEK_OF_YEAR, -1));
		assertFalse(contractorAudit.expiredUpToAWeekAgo());
	}
	
	@Test
	public void testExpiredUpToAWeekAgo_ExpirationDateIsLessThanOneWeekAgo() {
		contractorAudit.setExpiresDate(DateBean.addField(DateBean.addField(new Date(), Calendar.WEEK_OF_YEAR, -1), Calendar.DATE, 1));
		assertTrue(contractorAudit.expiredUpToAWeekAgo());
	}
	
	@Test
	public void testHasCaoStatusAfterFalse() {
		contractorAudit.setOperators(Arrays.asList(EntityFactory.makeContractorAuditOperator(contractorAudit, AuditStatus.Resubmitted)));
		assertFalse(contractorAudit.hasCaoStatusAfter(AuditStatus.Resubmitted));
	}
	
	@Test
	public void testHasCaoStatusAfterTrue() {
		contractorAudit.setOperators(Arrays.asList(EntityFactory.makeContractorAuditOperator(contractorAudit, AuditStatus.Complete)));
		assertTrue(contractorAudit.hasCaoStatusAfter(AuditStatus.Resubmitted));
	}
	
	@Test
	public void testHasCaoStatusBeforeFalse() {
		contractorAudit.setOperators(Arrays.asList(EntityFactory.makeContractorAuditOperator(contractorAudit, AuditStatus.Resubmitted)));
		assertFalse(contractorAudit.hasCaoStatusBefore(AuditStatus.Resubmitted));
	}
	
	@Test
	public void testHasCaoStatusBeforeTrue() {
		contractorAudit.setOperators(Arrays.asList(EntityFactory.makeContractorAuditOperator(contractorAudit, AuditStatus.Resubmit)));
		assertTrue(contractorAudit.hasCaoStatusBefore(AuditStatus.Resubmitted));
	}
	
	@Test
	public void testHasCaoStatusFalse() {
		contractorAudit.setOperators(Arrays.asList(EntityFactory.makeContractorAuditOperator(contractorAudit, AuditStatus.Pending)));
		assertFalse(contractorAudit.hasCaoStatus(AuditStatus.Resubmitted));
	}
	
	@Test
	public void testHasCaoStatusTrue() {
		contractorAudit.setOperators(Arrays.asList(EntityFactory.makeContractorAuditOperator(contractorAudit, AuditStatus.Approved)));
		assertTrue(contractorAudit.hasCaoStatus(AuditStatus.Approved));
	}
}
