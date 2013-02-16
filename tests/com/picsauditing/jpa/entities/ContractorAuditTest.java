package com.picsauditing.jpa.entities;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picsauditing.EntityFactory;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.Permissions;

public class ContractorAuditTest {
	private ContractorAudit contractorAudit;

	@Mock
	Permissions permissions;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
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
		contractorAudit.setExpiresDate(DateBean.addField(DateBean.addField(new Date(), Calendar.WEEK_OF_YEAR, 2),
				Calendar.DATE, -1));
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
		contractorAudit.setExpiresDate(DateBean.addField(DateBean.addField(new Date(), Calendar.WEEK_OF_YEAR, -1),
				Calendar.DATE, 1));
		assertTrue(contractorAudit.expiredUpToAWeekAgo());
	}

	@Test
	public void testHasCaoStatusAfterFalse() {
		contractorAudit.setOperators(Arrays.asList(EntityFactory.makeContractorAuditOperator(contractorAudit,
				AuditStatus.Resubmitted)));
		assertFalse(contractorAudit.hasCaoStatusAfter(AuditStatus.Resubmitted));
	}

	@Test
	public void testHasCaoStatusAfterTrue() {
		contractorAudit.setOperators(Arrays.asList(EntityFactory.makeContractorAuditOperator(contractorAudit,
				AuditStatus.Complete)));
		assertTrue(contractorAudit.hasCaoStatusAfter(AuditStatus.Resubmitted));
	}

	@Test
	public void testHasCaoStatusBeforeFalse() {
		contractorAudit.setOperators(Arrays.asList(EntityFactory.makeContractorAuditOperator(contractorAudit,
				AuditStatus.Resubmitted)));
		assertFalse(contractorAudit.hasCaoStatusBefore(AuditStatus.Resubmitted));
	}

	@Test
	public void testHasCaoStatusBeforeTrue() {
		contractorAudit.setOperators(Arrays.asList(EntityFactory.makeContractorAuditOperator(contractorAudit,
				AuditStatus.Resubmit)));
		assertTrue(contractorAudit.hasCaoStatusBefore(AuditStatus.Resubmitted));
	}

	@Test
	public void testHasCaoStatusFalse() {
		contractorAudit.setOperators(Arrays.asList(EntityFactory.makeContractorAuditOperator(contractorAudit,
				AuditStatus.Pending)));
		assertFalse(contractorAudit.hasCaoStatus(AuditStatus.Resubmitted));
	}

	@Test
	public void testHasCaoStatusTrue() {
		contractorAudit.setOperators(Arrays.asList(EntityFactory.makeContractorAuditOperator(contractorAudit,
				AuditStatus.Approved)));
		assertTrue(contractorAudit.hasCaoStatus(AuditStatus.Approved));
	}

	@Test
	public void testHasCaoStatusAfterIgnoreNotApplicable() {
		ContractorAuditOperator cao1 = EntityFactory
				.makeContractorAuditOperator(contractorAudit, AuditStatus.Submitted);
		ContractorAuditOperator cao2 = EntityFactory.makeContractorAuditOperator(contractorAudit,
				AuditStatus.NotApplicable);
		contractorAudit.getOperators().add(cao1);
		contractorAudit.getOperators().add(cao2);

		assertFalse(contractorAudit.hasCaoStatusAfter(AuditStatus.Submitted, true));
		assertTrue(contractorAudit.hasCaoStatusAfter(AuditStatus.Submitted, false));
	}

	@Test
	public void testGetVisibleCategories_ParentNotApplicable() throws Exception {
		AuditCategory parent = EntityFactory.makeAuditCategory();
		parent.setId(1);
		AuditCatData parentData = new AuditCatData();
		parentData.setApplies(false);
		parentData.setCategory(parent);

		AuditCategory child = EntityFactory.makeAuditCategory();
		child.setId(2);
		child.setParent(parent);
		AuditCatData childData = new AuditCatData();
		childData.setApplies(true);
		childData.setCategory(child);

		List<AuditCatData> categoryData = new ArrayList<AuditCatData>();
		categoryData.add(parentData);
		categoryData.add(childData);

		contractorAudit.setCategories(categoryData);

		Set<AuditCategory> visibleCategories = contractorAudit.getVisibleCategories();

		assertNotNull(visibleCategories);
		assertTrue(visibleCategories.isEmpty());
	}

	@Test
	public void testGetVisibleCategories_ParentApplicable() throws Exception {
		AuditCategory parent = EntityFactory.makeAuditCategory();
		parent.setId(1);
		AuditCatData parentData = new AuditCatData();
		parentData.setApplies(true);
		parentData.setCategory(parent);

		AuditCategory child = EntityFactory.makeAuditCategory();
		child.setId(2);
		child.setParent(parent);
		AuditCatData childData = new AuditCatData();
		childData.setApplies(true);
		childData.setCategory(child);

		List<AuditCatData> categoryData = new ArrayList<AuditCatData>();
		categoryData.add(parentData);
		categoryData.add(childData);

		contractorAudit.setCategories(categoryData);

		Set<AuditCategory> visibleCategories = contractorAudit.getVisibleCategories();

		assertNotNull(visibleCategories);
		assertFalse(visibleCategories.isEmpty());
		assertTrue(visibleCategories.contains(parent));
		assertTrue(visibleCategories.contains(child));
	}

	@Test
	public void testGetVisibleCategories_ChildNotApplicable() throws Exception {
		AuditCategory parent = EntityFactory.makeAuditCategory();
		parent.setId(1);
		AuditCatData parentData = new AuditCatData();
		parentData.setApplies(true);
		parentData.setCategory(parent);

		AuditCategory child = EntityFactory.makeAuditCategory();
		child.setId(2);
		child.setParent(parent);
		AuditCatData childData = new AuditCatData();
		childData.setApplies(false);
		childData.setCategory(child);

		List<AuditCatData> categoryData = new ArrayList<AuditCatData>();
		categoryData.add(parentData);
		categoryData.add(childData);

		contractorAudit.setCategories(categoryData);

		Set<AuditCategory> visibleCategories = contractorAudit.getVisibleCategories();

		assertNotNull(visibleCategories);
		assertFalse(visibleCategories.isEmpty());
		assertTrue(visibleCategories.contains(parent));
		assertFalse(visibleCategories.contains(child));
	}

	@Test
	public void testGetVisibleCategories_Cyclical() throws Exception {
		AuditCategory parent = EntityFactory.makeAuditCategory();
		parent.setId(1);
		AuditCatData parentData = new AuditCatData();
		parentData.setApplies(true);
		parentData.setCategory(parent);

		AuditCategory child = EntityFactory.makeAuditCategory();
		child.setId(2);
		child.setParent(parent);
		AuditCatData childData = new AuditCatData();
		childData.setApplies(true);
		childData.setCategory(child);

		parent.setParent(child);

		List<AuditCatData> categoryData = new ArrayList<AuditCatData>();
		categoryData.add(parentData);
		categoryData.add(childData);

		contractorAudit.setCategories(categoryData);

		Set<AuditCategory> visibleCategories = contractorAudit.getVisibleCategories();

		assertNotNull(visibleCategories);
		assertFalse(visibleCategories.isEmpty());
		assertTrue(visibleCategories.contains(parent));
		assertTrue(visibleCategories.contains(child));
	}

	@Test
	public void testIsOkayToChangeCaoStatus() {
		AuditType auditType = EntityFactory.makeAuditType(AuditType.PQF);
		ContractorAuditOperator cao = EntityFactory.makeContractorAuditOperator(contractorAudit);
		AuditQuestion question = EntityFactory.makeAuditQuestion();
		AuditData data = EntityFactory.makeAuditData("Yes", question);

		contractorAudit.setAuditType(auditType);
		contractorAudit.getData().add(data);

		cao.setPercentVerified(100);
		question.setId(AuditQuestion.MANUAL_PQF);
		data.setVerified(true);
		assertTrue(contractorAudit.isOkayToChangeCaoStatus(cao));

		cao.setPercentVerified(100);
		question.setId(AuditQuestion.MANUAL_PQF);
		data.setVerified(false);
		assertFalse(contractorAudit.isOkayToChangeCaoStatus(cao));

		cao.setPercentVerified(100);
		question.setId(21);
		data.setVerified(true);
		assertTrue(contractorAudit.isOkayToChangeCaoStatus(cao));

		cao.setPercentVerified(99);
		question.setId(AuditQuestion.MANUAL_PQF);
		data.setVerified(true);
		assertFalse(contractorAudit.isOkayToChangeCaoStatus(cao));

	}
}