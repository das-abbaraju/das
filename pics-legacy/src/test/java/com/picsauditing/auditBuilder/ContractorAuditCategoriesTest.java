package com.picsauditing.auditBuilder;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.*;

import com.picsauditing.jpa.entities.ContractorAudit;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picsauditing.EntityFactory;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.util.test.TranslatorFactorySetup;

public class ContractorAuditCategoriesTest {
	@Mock
	Permissions permissions;
    @Mock
    ContractorAudit audit;

	private int categorySortCounter;

	private List<AuditCatData> categories;

	private AuditType testAuditType;

	@AfterClass
	public static void classTearDown() {
		TranslatorFactorySetup.resetTranslatorFactoryAfterTest();
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		TranslatorFactorySetup.setupTranslatorFactoryForTest();
		testAuditType = EntityFactory.makeAuditType(100);
		categories = new ArrayList<AuditCatData>();
		createCategory();
	}

	@Test
	public void testGetApplicableCategories__WorkHistory_OperatorWithoutPerm() {
		Set<AuditCategory> requiredCategories = new HashSet<AuditCategory>();
		AuditCatData acd2 = createCategory(AuditCategory.WORK_HISTORY);
		requiredCategories.add(acd2.getCategory());

		when(permissions.isOperatorCorporate()).thenReturn(true);
		Map<AuditCategory, AuditCatData> answer = ContractorAuditCategories.getApplicableCategories(permissions,
				requiredCategories, categories);
		assertEquals(0, answer.size());
	}

	@Test
	public void testGetApplicableCategories__WorkHistory_OperatorWithPerm() {
		Set<AuditCategory> requiredCategories = new HashSet<AuditCategory>();
		AuditCatData acd2 = createCategory(AuditCategory.WORK_HISTORY);
		requiredCategories.add(acd2.getCategory());

		when(permissions.isOperatorCorporate()).thenReturn(true);
		when(permissions.hasPermission(OpPerms.ViewFullPQF)).thenReturn(true);
		Map<AuditCategory, AuditCatData> answer = ContractorAuditCategories.getApplicableCategories(permissions,
				requiredCategories, categories);
		assertEquals(1, answer.size());
	}

    @Test
    public void testGetApplicableCategories_EffectiveCategories() {
        Set<AuditCategory> requiredCategories = new HashSet<AuditCategory>();
        AuditCatData acd2 = createCategory(2);
        requiredCategories.add(acd2.getCategory());

        when(permissions.isOperatorCorporate()).thenReturn(false);
        when(permissions.isContractor()).thenReturn(true);

        Map<AuditCategory, AuditCatData> answer;

        Calendar date = Calendar.getInstance();
        int year = date.get(Calendar.YEAR);
        int month = date.get(Calendar.MONTH);
        int day = date.get(Calendar.DAY_OF_MONTH);
        Date originalDate;

        // audit no effective date, category effective date wide open
        answer = ContractorAuditCategories.getApplicableCategories(permissions,
                requiredCategories, categories);
        assertEquals(2, answer.size());

        // audit no effective date, one category not yet effective
        originalDate = setCategoryEffectiveDate(1, year + 1, month, day);
        answer = ContractorAuditCategories.getApplicableCategories(permissions,
                requiredCategories, categories);
        assertEquals(1, answer.size());
        categories.get(1).getCategory().setEffectiveDate(originalDate);

        // audit no effective date, one category expired
        originalDate = setCategoryExpirationDate(1, year - 1, month, day);
        answer = ContractorAuditCategories.getApplicableCategories(permissions,
                requiredCategories, categories);
        assertEquals(1, answer.size());
        categories.get(1).getCategory().setExpirationDate(originalDate);

        // set effective and expiration date of audit
        Calendar auditDate = Calendar.getInstance();
        auditDate.add(Calendar.MONDAY, -1);
        Date effectiveDate = auditDate.getTime();
        auditDate.add(Calendar.MONDAY, +4);
        Date expirationDate = auditDate.getTime();
        when(audit.getEffectiveDate()).thenReturn(effectiveDate);
        when(audit.getExpiresDate()).thenReturn(expirationDate);

        // audit effective date, category effective date wide open
        answer = ContractorAuditCategories.getApplicableCategories(permissions,
                requiredCategories, categories);
        assertEquals(2, answer.size());

        // audit effective date, one category not yet effective
        originalDate = setCategoryEffectiveDate(1, year + 1, month, day);
        answer = ContractorAuditCategories.getApplicableCategories(permissions,
                requiredCategories, categories);
        assertEquals(1, answer.size());
        categories.get(1).getCategory().setEffectiveDate(originalDate);

        // audit effective date, one category expired
        originalDate = setCategoryExpirationDate(1, year - 1, month, day);
        answer = ContractorAuditCategories.getApplicableCategories(permissions,
                requiredCategories, categories);
        assertEquals(1, answer.size());
    }

    private Date setCategoryEffectiveDate(int index, int year, int month, int day) {
        AuditCatData acd = categories.get(index);
        Calendar date = Calendar.getInstance();
        date.set(year, month, day);
        Date originalDate = acd.getCategory().getEffectiveDate();
        acd.getCategory().setEffectiveDate(date.getTime());
        return originalDate;
    }

    private Date setCategoryExpirationDate(int index, int year, int month, int day) {
        AuditCatData acd = categories.get(index);
        Calendar date = Calendar.getInstance();
        date.set(year, month, day);
        Date originalDate = acd.getCategory().getExpirationDate();
        acd.getCategory().setExpirationDate(date.getTime());
        return originalDate;
    }

    @Test
	public void testGetApplicableCategories__LimitCategories() {
		Set<AuditCategory> requiredCategories = new HashSet<AuditCategory>();
		AuditCatData acd2 = createCategory(2);
		requiredCategories.add(acd2.getCategory());

		when(permissions.isOperatorCorporate()).thenReturn(true);
		Map<AuditCategory, AuditCatData> answer = ContractorAuditCategories.getApplicableCategories(permissions,
				requiredCategories, categories);
		assertEquals(1, answer.size());
	}

	@Test
	public void testGetApplicableCategories_LimitCategoriesMultipleCategories() {
		Set<AuditCategory> requiredCategories = new HashSet<AuditCategory>();
		AuditCatData acd1 = createCategory(1);
		AuditCatData acd2 = createCategory(2);
		requiredCategories.add(acd2.getCategory());
		categories.add(acd1);

		when(permissions.isOperatorCorporate()).thenReturn(true);
		Map<AuditCategory, AuditCatData> answer = ContractorAuditCategories.getApplicableCategories(permissions,
				requiredCategories, categories);
		assertEquals(1, answer.size());
	}

	@Test
	public void testGetApplicableCategories__WorkHistory_Contractor() {
		createCategory(AuditCategory.WORK_HISTORY);

		when(permissions.isContractor()).thenReturn(true);
		Map<AuditCategory, AuditCatData> answer = ContractorAuditCategories.getApplicableCategories(permissions, null,
				categories);
		assertEquals(2, answer.size());
	}

	@Test
	public void testGetApplicableCategories__WorkHistory_Admin() {
		createCategory(AuditCategory.WORK_HISTORY);

		when(permissions.isAdmin()).thenReturn(true);
		Map<AuditCategory, AuditCatData> answer = ContractorAuditCategories.getApplicableCategories(permissions, null,
				categories);
		assertEquals(1, answer.size());
	}

	@Test
	public void testGetApplicableCategories__WorkHistory_AdminWithFullPQF() {
		createCategory(AuditCategory.WORK_HISTORY);

		when(permissions.isAdmin()).thenReturn(true);
		when(permissions.hasPermission(OpPerms.ViewFullPQF)).thenReturn(true);
		Map<AuditCategory, AuditCatData> answer = ContractorAuditCategories.getApplicableCategories(permissions, null,
				categories);
		assertEquals(2, answer.size());
	}

	@Test
	public void testGetApplicableCategories__SortedCategories() {
		createCategory().getCategory().setNumber(3);
		createCategory().getCategory().setNumber(2);

		when(permissions.isContractor()).thenReturn(true);
		NavigableSet<AuditCategory> answer = ContractorAuditCategories.getApplicableCategories(permissions, null,
				categories).navigableKeySet();

		assertEquals(1, answer.first().getNumber());
		assertEquals(3, answer.last().getNumber());
	}

	private AuditCatData createCategory() {
		return createCategory(0);
	}

	private AuditCatData createCategory(int categoryID) {
		AuditCatData acd = EntityFactory.makeAuditCatData();
		categorySortCounter++;
		acd.getCategory().setId(categoryID);
		acd.getCategory().setNumber(categorySortCounter);
		acd.getCategory().setAuditType(testAuditType);
        Calendar date = Calendar.getInstance();
        date.set(2001, 0, 1);
        acd.getCategory().setEffectiveDate(date.getTime());
        date.set(4000, 0, 1);
        acd.getCategory().setExpirationDate(date.getTime());
		categories.add(acd);
        acd.setAudit(audit);
		return acd;
	}
}
