package com.picsauditing.auditBuilder;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;

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

public class ContractorAuditCategoriesTest {
	@Mock
	Permissions permissions;

	private int categorySortCounter;

	private List<AuditCatData> categories;

	private AuditType testAuditType;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		testAuditType = EntityFactory.makeAuditType(100);
		categories = new ArrayList<AuditCatData>();
		createCategory();
	}

	@Test
	public void testGetApplicableCategories__WorkHistory_OperatorWithoutPerm() {
		createCategory(AuditCategory.WORK_HISTORY);

		when(permissions.isOperatorCorporate()).thenReturn(true);
		Map<AuditCategory, AuditCatData> answer = ContractorAuditCategories.getApplicableCategories(permissions, null,
				categories);
		assertEquals(1, answer.size());
	}

	@Test
	public void testGetApplicableCategories__WorkHistory_OperatorWithPerm() {
		createCategory(AuditCategory.WORK_HISTORY);

		when(permissions.isOperatorCorporate()).thenReturn(true);
		when(permissions.hasPermission(OpPerms.ViewFullPQF)).thenReturn(true);
		Map<AuditCategory, AuditCatData> answer = ContractorAuditCategories.getApplicableCategories(permissions, null,
				categories);
		assertEquals(2, answer.size());
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
		categories.add(acd);
		return acd;
	}
}
