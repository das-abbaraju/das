package com.picsauditing.actions;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.google.common.collect.Lists;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.search.Database;

public class ContractorCronTest {
	@Mock private Database databaseForTesting;
	
	@AfterClass
	public static void classTearDown() {
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", (Database)null);
	}
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", databaseForTesting);
	}
	
	/**
	 * More Test Cases to be added.
	 * 
	 * Multiple WCB Types, one with overlapping where the overlapping one has a status of Pending,
	 * another where the overlapping one has a status of Approved, another where there isn't one
	 * for this year (none generated for the current year, so no need to add it to the list).
	 */
	
	/**
	 * Test that no WCBs are added to the list if all the WCBs are NOT about to
	 * expire and they all have a status of Submitted further in the workflow.
	 * @throws Exception 
	 */
	@Test
	public void testContractorAccountOnlyWCBs() throws Exception {
		ContractorCron contractorCron = new ContractorCron();
		
		ContractorAccount contractorAccount = setupContractorAccountWithOnlyWCBs();		
		Set<ContractorAudit> audits = Whitebox.invokeMethod(contractorCron, "getExpiringPolicies", contractorAccount);
						
		assertTrue(audits.isEmpty());
	}
	
	/**
	 * The reason for this test is that getExpiringPolicies() only check for expiring
	 * WCBs or Policies.
	 * @throws Exception 
	 */
	@Test
	public void testContractorAccountNoPoliciesOrWCBs() throws Exception {
		ContractorCron contractorCron = new ContractorCron();
		
		ContractorAccount contractorAccount = setupContractorAccountNoPoliciesOrWCBs();
		Set<ContractorAudit> audits = Whitebox.invokeMethod(contractorCron, "getExpiringPolicies", contractorAccount);
		
		assertTrue(audits.isEmpty());
	}
	
	/**
	 * Since the Audit (Policy) expired outside the range we are checking for, the list
	 * returned should be empty.
	 * @throws Exception 
	 */
	@Test
	public void testContractorAccountPolicyExpiredTwoMonthsAgo() throws Exception {
		ContractorCron contractorCron = new ContractorCron();
		
		ContractorAccount contractorAccount = setupContractorAccountPolicyExpiredTwoMonthsAgo();
		Set<ContractorAudit> audits = Whitebox.invokeMethod(contractorCron, "getExpiringPolicies", contractorAccount);
		
		assertTrue(audits.isEmpty());
	}
	
	/**
	 * Since the Audit (WCB) expired outside the range we are checking for, the list
	 * returned should be empty.
	 * @throws Exception 
	 */
	@Test
	public void testContractorAccountWCBExpiredTwoMonthsAgo() throws Exception {
		ContractorCron contractorCron = new ContractorCron();
		
		ContractorAccount contractorAccount = setupContractorAccountWCBExpiredTwoMonthsAgo();
		Set<ContractorAudit> audits = Whitebox.invokeMethod(contractorCron, "getExpiringPolicies", contractorAccount);
		
		assertTrue(audits.isEmpty());
	}
	
	/**
	 * Test to verify that the expired Audit returned in the list is the Policy.
	 * @throws Exception 
	 */
	@Test
	public void testContractorAccountMultipleAuditsExpiredPolicy() throws Exception {
		ContractorCron contractorCron = new ContractorCron();
		
		ContractorAccount contractorAccount = setupContractorAccountMultipleAuditsExpiredPolicy();
		Set<ContractorAudit> audits = Whitebox.invokeMethod(contractorCron, "getExpiringPolicies", contractorAccount);
		
		assertEquals(1, audits.size());
		for (Iterator<ContractorAudit> iterator = audits.iterator(); iterator.hasNext();) {
			ContractorAudit contractorAudit = iterator.next();
			assertEquals(AuditTypeClass.Policy, contractorAudit.getAuditType().getClassType());
		}
	}
	
	@Test
	public void testContractorAccountMultipleAuditsExpiredWCB() throws Exception {
		ContractorCron contractorCron = new ContractorCron();

		ContractorAccount contractorAccount = setupContractorAccountMultipleAuditsExpiredWCB();
		Set<ContractorAudit> audits = Whitebox.invokeMethod(contractorCron, "getExpiringPolicies", contractorAccount);

		assertEquals(1, audits.size());
		for (Iterator<ContractorAudit> iterator = audits.iterator(); iterator.hasNext();) {
			ContractorAudit contractorAudit = iterator.next();
			assertTrue(contractorAudit.getAuditType().isWCB());
		}
	}
	
	/**
	 * If there there are overlapping WCBs, one is expiring and the other has a status of
	 * Submitted or greater and is for the current year, no audit should be in the list.
	 * @throws Exception 
	 */
	@Test
	public void testContractorAccountOverlappingValidWCBs() throws Exception {
		ContractorCron contractorCron = new ContractorCron();
		
		ContractorAccount contractorAccount = setupContractorAccountWCBsForDifferentYears();
		Set<ContractorAudit> audits = Whitebox.invokeMethod(contractorCron, "getExpiringPolicies", contractorAccount);
		
		assertTrue(audits.isEmpty());
	}
	
	/**
	 * If there there are overlapping WCBs, one is expiring and the other has a status of
	 * Incomplete, then it should be in the expiring audit list.
	 * @throws Exception 
	 */
	@Test
	public void testContractorAccountOverlappingWCBsIncomplete() throws Exception {
		ContractorCron contractorCron = new ContractorCron();
		
		ContractorAccount contractorAccount = setupContractorAccountWCBsForDifferentYearsIncomplete();
		Set<ContractorAudit> audits = Whitebox.invokeMethod(contractorCron, "getExpiringPolicies", contractorAccount);
		
		assertEquals(1, audits.size());
		for (Iterator<ContractorAudit> iterator = audits.iterator(); iterator.hasNext();) {
			ContractorAudit contractorAudit = iterator.next();
			assertTrue(areDatesEqual(DateBean.addField(new Date(), Calendar.DATE, -2), contractorAudit.getExpiresDate(), 100));
		}
	}	
	
	/**
	 * If there there are overlapping WCBs, one is expiring and the other has a status of
	 * Pending, then it should be in the expiring audit list.
	 * @throws Exception 
	 */
	@Test
	public void testContractorAccountOverlappingWCBsPending() throws Exception {
		ContractorCron contractorCron = new ContractorCron();

		ContractorAccount contractorAccount = setupContractorAccountWCBsForDifferentYearsPending();
		Set<ContractorAudit> audits = Whitebox.invokeMethod(contractorCron, "getExpiringPolicies", contractorAccount);

		assertEquals(2, audits.size());
		for (Iterator<ContractorAudit> iterator = audits.iterator(); iterator.hasNext();) {
			ContractorAudit contractorAudit = iterator.next();
			assertTrue(areDatesEqual(DateBean.addField(new Date(), Calendar.DAY_OF_YEAR, -2), contractorAudit.getExpiresDate(), 100));
		}
	}
	
	/**
	 * If there there are overlapping WCBs, one is expiring and the other has a status of
	 * Pending, then it should be in the expiring audit list.
	 * @throws Exception 
	 */
	@Test
	public void testContractorAccountOverlappingWCBsComplete() throws Exception {
		ContractorCron contractorCron = new ContractorCron();

		ContractorAccount contractorAccount = setupContractorAccountWCBsForDifferentYearsComplete();
		Set<ContractorAudit> audits = Whitebox.invokeMethod(contractorCron, "getExpiringPolicies", contractorAccount);

		assertTrue(audits.isEmpty());
	}
	
	// TODO: Find out if there is another method that does this
	/**
	 * Return true if the Dates are within the range of the offset. 
	 * 
	 * @param date1
	 * @param date2
	 * @param offset - difference between dates in milliseconds
	 * @return
	 */
	private boolean areDatesEqual(Date date1, Date date2, long offset) {
		long actualOffset = Math.abs(date2.getTime() - date1.getTime());
		return (actualOffset <= offset);
	}
	
	private ContractorAccount setupContractorAccountWCBExpiredTwoMonthsAgo() {
		ContractorAccount contractorAccount = new ContractorAccount();
		List<ContractorAudit> audits = new ArrayList<ContractorAudit>();
		
		ContractorAudit audit = new MockAuditBuilder()
									.id(143)
									.auditTypeClass(AuditTypeClass.Policy)
									.auditStatus(AuditStatus.Approved)
									.expiresDate(DateBean.addMonths(new Date(), -2))
									.build();
		audits.add(audit);
		
		contractorAccount.setAudits(audits);
		
		return contractorAccount;
	}
	
	private ContractorAccount setupContractorAccountPolicyExpiredTwoMonthsAgo() {
		ContractorAccount contractorAccount = new ContractorAccount();
		List<ContractorAudit> audits = new ArrayList<ContractorAudit>();
		
		ContractorAudit audit = new MockAuditBuilder()
									.auditTypeClass(AuditTypeClass.Policy)
									.auditStatus(AuditStatus.Approved)
									.expiresDate(DateBean.addMonths(new Date(), -2))
									.build();
		audits.add(audit);
		
		contractorAccount.setAudits(audits);
		
		return contractorAccount;
	}
	
	private ContractorAccount setupContractorAccountNoPoliciesOrWCBs() {
		ContractorAccount contractorAccount = new ContractorAccount();
		List<ContractorAudit> audits = new ArrayList<ContractorAudit>();
		
		ContractorAudit audit = new MockAuditBuilder()
									.auditTypeClass(AuditTypeClass.PQF)
									.auditStatus(AuditStatus.Pending)
									.expiresDate(DateBean.addMonths(new Date(), 3))
									.build();
		audits.add(audit);
		
		contractorAccount.setAudits(audits);
		
		return contractorAccount;
	}
	
	private ContractorAccount setupContractorAccountWithOnlyWCBs() {
		ContractorAccount contractorAccount = new ContractorAccount();
		List<ContractorAudit> audits = new ArrayList<ContractorAudit>();
		
		// WCB IDs = 143, 170, 261
		
		ContractorAudit audit = new MockAuditBuilder()
									.id(143)
									.auditTypeClass(AuditTypeClass.Policy)
									.auditStatus(AuditStatus.Submitted)
									.expiresDate(DateBean.addMonths(new Date(), 3))
									.build();
		audits.add(audit);		
		
		audit = new MockAuditBuilder()
					.id(170)
					.auditTypeClass(AuditTypeClass.Policy)
					.auditStatus(AuditStatus.Complete)
					.expiresDate(DateBean.addMonths(new Date(), 3))
					.build();
		audits.add(audit);
		
		audit = new MockAuditBuilder()
					.id(261)
					.auditTypeClass(AuditTypeClass.Policy)
					.auditStatus(AuditStatus.Approved)
					.expiresDate(DateBean.addMonths(new Date(), 3))
					.build();
		audits.add(audit);
		
		contractorAccount.setAudits(audits);
		
		return contractorAccount;
	}
	
	private ContractorAccount setupContractorAccountMultipleAuditsExpiredPolicy() {
		ContractorAccount contractorAccount = new ContractorAccount();
		List<ContractorAudit> audits = new ArrayList<ContractorAudit>();
		
		ContractorAudit audit = new MockAuditBuilder()
									.id(143)
									.auditTypeClass(AuditTypeClass.Policy)
									.auditStatus(AuditStatus.Submitted)
									.expiresDate(DateBean.addMonths(new Date(), 3))
									.build();
		audits.add(audit);		
		
		audit = new MockAuditBuilder()
					.auditTypeClass(AuditTypeClass.Policy)
					.auditStatus(AuditStatus.Complete)
					.expiresDate(DateBean.addDays(new Date(), -2))
					.build();
		audits.add(audit);
		
		audit = new MockAuditBuilder()
					.id(261)
					.auditTypeClass(AuditTypeClass.Policy)
					.auditStatus(AuditStatus.Approved)
					.expiresDate(DateBean.addMonths(new Date(), 3))
					.build();
		audits.add(audit);
		
		contractorAccount.setAudits(audits);
		
		return contractorAccount;
	}
	
	private ContractorAccount setupContractorAccountMultipleAuditsExpiredWCB() {
		ContractorAccount contractorAccount = new ContractorAccount();
		List<ContractorAudit> audits = new ArrayList<ContractorAudit>();
		
		ContractorAudit audit = new MockAuditBuilder()
									.id(143)
									.auditTypeClass(AuditTypeClass.Policy)
									.auditStatus(AuditStatus.Submitted)
									.expiresDate(DateBean.addDays(new Date(), -2))
									.build();
		audits.add(audit);		
		
		audit = new MockAuditBuilder()
					.auditTypeClass(AuditTypeClass.Policy)
					.auditStatus(AuditStatus.Complete)
					.expiresDate(DateBean.addMonths(new Date(), 3))
					.build();
		audits.add(audit);
		
		contractorAccount.setAudits(audits);
		
		return contractorAccount;
	}
	
	private ContractorAccount setupContractorAccountWCBsForDifferentYears() {
		ContractorAccount contractorAccount = new ContractorAccount();
		List<ContractorAudit> audits = new ArrayList<ContractorAudit>();
		Date twoDaysAgo = DateBean.addDays(new Date(), -2);
		
		ContractorAudit audit = new MockAuditBuilder()
									.id(143)
									.auditTypeClass(AuditTypeClass.Policy)
									.auditStatus(AuditStatus.Approved)
									.expiresDate(twoDaysAgo)
									.build();
		audits.add(audit);		
		
		audit = new MockAuditBuilder()
					.id(143)
					.auditTypeClass(AuditTypeClass.Policy)
					.auditStatus(AuditStatus.Complete)
					.expiresDate(DateBean.addField(twoDaysAgo, Calendar.YEAR, 1))
					.build();
		audits.add(audit);
		
		contractorAccount.setAudits(audits);
		
		return contractorAccount;
	}
	
	private ContractorAccount setupContractorAccountWCBsForDifferentYearsIncomplete() {
		ContractorAccount contractorAccount = new ContractorAccount();
		List<ContractorAudit> audits = new ArrayList<ContractorAudit>();
		Date twoDaysAgo = DateBean.addDays(new Date(), -2);
		
		ContractorAudit audit = new MockAuditBuilder()
									.id(143)
									.auditTypeClass(AuditTypeClass.Policy)
									.auditStatus(AuditStatus.Approved)
									.expiresDate(twoDaysAgo)
									.build();
		audits.add(audit);		
		
		audit = new MockAuditBuilder()
					.id(143)
					.auditTypeClass(AuditTypeClass.Policy)
					.auditStatus(AuditStatus.Incomplete)
					.expiresDate(DateBean.addField(twoDaysAgo, Calendar.YEAR, 1))
					.build();
		audits.add(audit);
		
		contractorAccount.setAudits(audits);
		
		return contractorAccount;
	}
	
	private ContractorAccount setupContractorAccountWCBsForDifferentYearsPending() {
		ContractorAccount contractorAccount = new ContractorAccount();
		List<ContractorAudit> audits = new ArrayList<ContractorAudit>();
		Date twoDaysAgo = DateBean.addDays(new Date(), -2);
		
		ContractorAudit audit = new MockAuditBuilder()
									.id(143)
									.auditTypeClass(AuditTypeClass.Policy)
									.auditStatus(AuditStatus.Approved)
									.expiresDate(twoDaysAgo)
									.build();
		audits.add(audit);		
		
		audit = new MockAuditBuilder()
					.id(143)
					.auditTypeClass(AuditTypeClass.Policy)
					.auditStatus(AuditStatus.Pending)
					.expiresDate(DateBean.addField(twoDaysAgo, Calendar.YEAR, 1))
					.build();
		audits.add(audit);
		
		contractorAccount.setAudits(audits);
		
		audit = new MockAuditBuilder()
					.id(170)
					.auditTypeClass(AuditTypeClass.Policy)
					.auditStatus(AuditStatus.Approved)
					.expiresDate(twoDaysAgo)
					.build();
		audits.add(audit);		

		audit = new MockAuditBuilder()
					.id(170)
					.auditTypeClass(AuditTypeClass.Policy)
					.auditStatus(AuditStatus.Pending)
					.expiresDate(DateBean.addField(twoDaysAgo, Calendar.YEAR, 1))
					.build();
		audits.add(audit);

		contractorAccount.setAudits(audits);		
		
		
		return contractorAccount;
	}
	
	private ContractorAccount setupContractorAccountWCBsForDifferentYearsComplete() {
		ContractorAccount contractorAccount = new ContractorAccount();
		List<ContractorAudit> audits = new ArrayList<ContractorAudit>();
		Date twoDaysAgo = DateBean.addDays(new Date(), -2);
		
		ContractorAudit audit = new MockAuditBuilder()
									.id(143)
									.auditTypeClass(AuditTypeClass.Policy)
									.auditStatus(AuditStatus.Complete)
									.expiresDate(twoDaysAgo)
									.build();
		audits.add(audit);		
		
		audit = new MockAuditBuilder()
					.id(143)
					.auditTypeClass(AuditTypeClass.Policy)
					.auditStatus(AuditStatus.Complete)
					.expiresDate(DateBean.addField(twoDaysAgo, Calendar.YEAR, 1))
					.build();
		audits.add(audit);
		
		contractorAccount.setAudits(audits);
		
		return contractorAccount;
	}
	
	private static class MockAuditBuilder {
		
		private int id;
		private Date expiresDate;
		private AuditStatus auditStatus;
		private AuditTypeClass auditTypeClass;
		
		public MockAuditBuilder id(int id) {
			this.id = id;
			return this;
		}
		
		public MockAuditBuilder expiresDate(Date expiresDate) {
			this.expiresDate = expiresDate;
			return this;
		}
		
		public MockAuditBuilder auditStatus(AuditStatus auditStatus) {
			this.auditStatus = auditStatus;
			return this;
		}
		
		public MockAuditBuilder auditTypeClass(AuditTypeClass auditTypeClass) {
			this.auditTypeClass = auditTypeClass;
			return this;
		}
		
		@SuppressWarnings("deprecation")
		public ContractorAudit build() {
			ContractorAudit audit = new ContractorAudit();
		
			audit.setExpiresDate(expiresDate);
			ContractorAuditOperator cao = new ContractorAuditOperator();
			cao.setStatus(auditStatus);
			audit.setOperators(Lists.newArrayList(cao));
			
			AuditType auditType = new AuditType(id);
			auditType.setClassType(auditTypeClass);
			
			audit.setAuditType(auditType);
			
			return audit;
		}
	}

}
