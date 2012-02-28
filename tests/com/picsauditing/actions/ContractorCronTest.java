package com.picsauditing.actions;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;

public class ContractorCronTest {

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
	 */
	@Test
	public void testContractorAccountOnlyWCBs() {
		ContractorCron contractorCron = new ContractorCron();
		
		ContractorAccount contractorAccount = getContractorAccountWithOnlyWCBs();		
		Set<ContractorAudit> audits = contractorCron.getExpiringPolicies(contractorAccount);
						
		assertTrue(audits.isEmpty());
	}
	
	/**
	 * The reason for this test is that getExpiringPolicies() only check for expiring
	 * WCBs or Policies.
	 */
	@Test
	public void testContractorAccountNoPoliciesOrWCBs() {
		ContractorCron contractorCron = new ContractorCron();
		
		ContractorAccount contractorAccount = getContractorAccountNoPoliciesOrWCBs();
		Set<ContractorAudit> audits = contractorCron.getExpiringPolicies(contractorAccount);
		
		assertTrue(audits.isEmpty());
	}
	
	/**
	 * Since the Audit (Policy) expired outside the range we are checking for, the list
	 * returned should be empty.
	 */
	@Test
	public void testContractorAccountPolicyExpiredTwoMonthsAgo() {
		ContractorCron contractorCron = new ContractorCron();
		
		ContractorAccount contractorAccount = getContractorAccountPolicyExpiredTwoMonthsAgo();
		Set<ContractorAudit> audits = contractorCron.getExpiringPolicies(contractorAccount);
		
		assertTrue(audits.isEmpty());
	}
	
	/**
	 * Since the Audit (WCB) expired outside the range we are checking for, the list
	 * returned should be empty.
	 */
	@Test
	public void testContractorAccountWCBExpiredTwoMonthsAgo() {
		ContractorCron contractorCron = new ContractorCron();
		
		ContractorAccount contractorAccount = getContractorAccountWCBExpiredTwoMonthsAgo();
		Set<ContractorAudit> audits = contractorCron.getExpiringPolicies(contractorAccount);
		
		assertTrue(audits.isEmpty());
	}
	
	/**
	 * Test to verify that the expired Audit returned in the list is the Policy.
	 */
	@Test
	public void testContractorAccountMultipleAuditsExpiredPolicy() {
		ContractorCron contractorCron = new ContractorCron();
		
		ContractorAccount contractorAccount = getContractorAccountMultipleAuditsExpiredPolicy();
		Set<ContractorAudit> audits = contractorCron.getExpiringPolicies(contractorAccount);
		
		assertEquals(1, audits.size());
		ContractorAudit[] ary = audits.toArray(new ContractorAudit[audits.size()]);
		assertEquals(AuditTypeClass.Policy, ary[0].getAuditType().getClassType());
	}
	
//	@Test
//	public void testContractorAccountMultipleAuditsExpiredWCB() {
//		ContractorCron contractorCron = new ContractorCron();
//		
//		ContractorAccount contractorAccount = getContractorAccountMultipleAuditsExpiredWCB();
//		Set<ContractorAudit> audits = contractorCron.getExpiringPolicies(contractorAccount);
//		
//		assertEquals(1, audits.size());
//		ContractorAudit[] ary = audits.toArray(new ContractorAudit[audits.size()]);
//		assertTrue(ary[0].getAuditType().isWCB());
//	}
	
	/**
	 * If there there are overlapping WCBs, one is expiring and the other has a status of
	 * Submitted or greater and is for the current year, no audit should be in the list.
	 */
	@Test
	public void testContractorAccountOverlappingValidWCBs() {
		ContractorCron contractorCron = new ContractorCron();
		
		ContractorAccount contractorAccount = getContractorAccountWCBsForDifferentYears();
		Set<ContractorAudit> audits = contractorCron.getExpiringPolicies(contractorAccount);
		
		assertEquals(0, audits.size());
	}
	
	/**
	 * If there there are overlapping WCBs, one is expiring and the other has a status of
	 * Incomplete, then it should be in the expiring audit list.
	 */
//	@Test
//	public void testContractorAccountOverlappingWCBsIncomplete() {
//		ContractorCron contractorCron = new ContractorCron();
//		
//		ContractorAccount contractorAccount = getContractorAccountWCBsForDifferentYearsIncomplete();
//		Set<ContractorAudit> audits = contractorCron.getExpiringPolicies(contractorAccount);
//		
//		assertEquals(1, audits.size());
//		ContractorAudit[] ary = audits.toArray(new ContractorAudit[audits.size()]);
//		assertEquals(DateBean.addField(getDateTwoDaysAgo(), Calendar.YEAR, 1), ary[0].getExpiresDate());
//	}	
	
	/**
	 * If there there are overlapping WCBs, one is expiring and the other has a status of
	 * Pending, then it should be in the expiring audit list.
	 */
//	@Test
//	public void testContractorAccountOverlappingWCBsPending() {
//		ContractorCron contractorCron = new ContractorCron();
//		
//		ContractorAccount contractorAccount = getContractorAccountWCBsForDifferentYearsPending();
//		Set<ContractorAudit> audits = contractorCron.getExpiringPolicies(contractorAccount);
//		
//		assertEquals(1, audits.size());
//		ContractorAudit[] ary = audits.toArray(new ContractorAudit[audits.size()]);
//		assertEquals(DateBean.addField(getDateTwoDaysAgo(), Calendar.YEAR, 1), ary[0].getExpiresDate());
//	}	
	
	private ContractorAccount getContractorAccountWCBExpiredTwoMonthsAgo() {
		ContractorAccount contractorAccount = new ContractorAccount();
		List<ContractorAudit> audits = new ArrayList<ContractorAudit>();
		
		ContractorAudit audit = new MockAuditBuilder()
									.id(143)
									.auditTypeClass(AuditTypeClass.Policy)
									.auditStatus(AuditStatus.Approved)
									.expiresDate(getDateTwoMonthsAgo())
									.build();
		audits.add(audit);
		
		contractorAccount.setAudits(audits);
		
		return contractorAccount;
	}
	
	private ContractorAccount getContractorAccountPolicyExpiredTwoMonthsAgo() {
		ContractorAccount contractorAccount = new ContractorAccount();
		List<ContractorAudit> audits = new ArrayList<ContractorAudit>();
		
		ContractorAudit audit = new MockAuditBuilder()
									.auditTypeClass(AuditTypeClass.Policy)
									.auditStatus(AuditStatus.Approved)
									.expiresDate(getDateTwoMonthsAgo())
									.build();
		audits.add(audit);
		
		contractorAccount.setAudits(audits);
		
		return contractorAccount;
	}
	
	private ContractorAccount getContractorAccountNoPoliciesOrWCBs() {
		ContractorAccount contractorAccount = new ContractorAccount();
		List<ContractorAudit> audits = new ArrayList<ContractorAudit>();
		
		ContractorAudit audit = new MockAuditBuilder()
									.auditTypeClass(AuditTypeClass.PQF)
									.auditStatus(AuditStatus.Pending)
									.expiresDate(getDateThreeMonthsFromNow())
									.build();
		audits.add(audit);
		
		contractorAccount.setAudits(audits);
		
		return contractorAccount;
	}
	
	private ContractorAccount getContractorAccountWithOnlyWCBs() {
		ContractorAccount contractorAccount = new ContractorAccount();
		List<ContractorAudit> audits = new ArrayList<ContractorAudit>();
		
		// WCB IDs = 143, 170, 261
		
		ContractorAudit audit = new MockAuditBuilder()
									.id(143)
									.auditTypeClass(AuditTypeClass.Policy)
									.auditStatus(AuditStatus.Submitted)
									.expiresDate(getDateThreeMonthsFromNow())
									.build();
		audits.add(audit);		
		
		audit = new MockAuditBuilder()
					.id(170)
					.auditTypeClass(AuditTypeClass.Policy)
					.auditStatus(AuditStatus.Complete)
					.expiresDate(getDateThreeMonthsFromNow())
					.build();
		audits.add(audit);
		
		audit = new MockAuditBuilder()
					.id(261)
					.auditTypeClass(AuditTypeClass.Policy)
					.auditStatus(AuditStatus.Approved)
					.expiresDate(getDateThreeMonthsFromNow())
					.build();
		audits.add(audit);
		
		contractorAccount.setAudits(audits);
		
		return contractorAccount;
	}
	
	private ContractorAccount getContractorAccountMultipleAuditsExpiredPolicy() {
		ContractorAccount contractorAccount = new ContractorAccount();
		List<ContractorAudit> audits = new ArrayList<ContractorAudit>();
		
		ContractorAudit audit = new MockAuditBuilder()
									.id(143)
									.auditTypeClass(AuditTypeClass.Policy)
									.auditStatus(AuditStatus.Submitted)
									.expiresDate(getDateThreeMonthsFromNow())
									.build();
		audits.add(audit);		
		
		audit = new MockAuditBuilder()
					.auditTypeClass(AuditTypeClass.Policy)
					.auditStatus(AuditStatus.Complete)
					.expiresDate(getDateTwoDaysAgo())
					.build();
		audits.add(audit);
		
		audit = new MockAuditBuilder()
					.id(261)
					.auditTypeClass(AuditTypeClass.Policy)
					.auditStatus(AuditStatus.Approved)
					.expiresDate(getDateThreeMonthsFromNow())
					.build();
		audits.add(audit);
		
		contractorAccount.setAudits(audits);
		
		return contractorAccount;
	}
	
	private ContractorAccount getContractorAccountMultipleAuditsExpiredWCB() {
		ContractorAccount contractorAccount = new ContractorAccount();
		List<ContractorAudit> audits = new ArrayList<ContractorAudit>();
		
		ContractorAudit audit = new MockAuditBuilder()
									.id(143)
									.auditTypeClass(AuditTypeClass.Policy)
									.auditStatus(AuditStatus.Submitted)
									.expiresDate(getDateTwoDaysAgo())
									.build();
		audits.add(audit);		
		
		audit = new MockAuditBuilder()
					.auditTypeClass(AuditTypeClass.Policy)
					.auditStatus(AuditStatus.Complete)
					.expiresDate(getDateThreeMonthsFromNow())
					.build();
		audits.add(audit);
		
		contractorAccount.setAudits(audits);
		
		return contractorAccount;
	}
	
	private ContractorAccount getContractorAccountWCBsForDifferentYears() {
		ContractorAccount contractorAccount = new ContractorAccount();
		List<ContractorAudit> audits = new ArrayList<ContractorAudit>();
		Date twoDaysAgo = getDateTwoDaysAgo();
		
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
	
	private ContractorAccount getContractorAccountWCBsForDifferentYearsIncomplete() {
		ContractorAccount contractorAccount = new ContractorAccount();
		List<ContractorAudit> audits = new ArrayList<ContractorAudit>();
		Date twoDaysAgo = getDateTwoDaysAgo();
		
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
	
	private ContractorAccount getContractorAccountWCBsForDifferentYearsPending() {
		ContractorAccount contractorAccount = new ContractorAccount();
		List<ContractorAudit> audits = new ArrayList<ContractorAudit>();
		Date twoDaysAgo = getDateTwoDaysAgo();
		
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
		
		return contractorAccount;
	}
	
	private Date getDateTwoDaysAgo() {
		return DateBean.addDays(new Date(), -2);
	}
	
	private Date getDateThreeMonthsFromNow() {
		return DateBean.addMonths(new Date(), 3);
	}
	
	private Date getDateTwoMonthsAgo() {
		return DateBean.addMonths(new Date(), -2);
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
			audit.setOperators(Utilities.toList(cao));
			
			AuditType auditType = new AuditType(id);
			auditType.setClassType(auditTypeClass);
			
			audit.setAuditType(auditType);
			
			return audit;
		}
	}

}
