package com.picsauditing.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Locale;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PicsActionTest;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.dao.CountrySubdivisionDAO;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.FlagColor;

public class ReportFilterAccountTest extends PicsActionTest {
	public ReportFilterAccount accountFilter;

	@Mock
	private CountrySubdivisionDAO countrySubdivisionDAO;
	@Mock
	private CountryDAO countryDAO;

	@AfterClass
	public static void classTearDown() throws Exception {
		Whitebox.setInternalState(ReportFilter.class, "cache", (I18nCache) null);
		PicsActionTest.classTearDown();
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		accountFilter = new ReportFilterAccount();
		super.setupMocks();

		Whitebox.setInternalState(accountFilter, "countrySubdivisionDAOForTests", countrySubdivisionDAO);
		Whitebox.setInternalState(ReportFilter.class, "cache", i18nCache);
	}

	@Test
	public void testSetPermissions_Null() {
		assertNull(accountFilter.getPermissions());
	}

	@Test
	public void testSetPermissions_NonPics() {
		accountFilter.setPermissions(permissions);
		assertNotNull(accountFilter.getPermissions());
		assertFalse(accountFilter.isShowStatus());
	}

	@Test
	public void testSetPermissions_Pics() {
		when(permissions.isPicsEmployee()).thenReturn(true);

		accountFilter.setPermissions(permissions);

		assertTrue(accountFilter.isShowStatus());
	}

	@Test
	public void testGetTypeList() {
		String[] typeList = accountFilter.getTypeList();

		assertNotNull(typeList);
	}

	@Test
	public void testGetFlagStatusList() {
		FlagColor[] flagColors = FlagColor.values();
		FlagColor[] fromList = accountFilter.getFlagStatusList();

		for (int index = 0; index < flagColors.length; index++) {
			assertEquals(flagColors[index], fromList[index]);
		}
	}

	@Test
	public void testGetCountrySubdivisionList_NoPermissions() {

		accountFilter.getCountrySubdivisionList();

		verify(countrySubdivisionDAO).findAll();
		verify(countrySubdivisionDAO, never()).findByCountries(anyCollectionOf(String.class), anyBoolean());
	}

	@Test
	public void testGetCountrySubdivisionList_NoCountryOnPermissions() {
		accountFilter.setPermissions(permissions);

		accountFilter.getCountrySubdivisionList();

		verify(countrySubdivisionDAO).findAll();
		verify(countrySubdivisionDAO, never()).findByCountries(anyCollectionOf(String.class), anyBoolean());
	}

	@Test
	public void testGetCountrySubdivisionList_CountryOnPermissions() {
		accountFilter.setPermissions(permissions);
		when(permissions.getCountry()).thenReturn("Country");

		accountFilter.getCountrySubdivisionList();

		verify(countrySubdivisionDAO, never()).findAll();
		verify(countrySubdivisionDAO).findByCountries(anyCollectionOf(String.class), anyBoolean());
	}

	@Test
	public void testGetCountrySubdivisionMap_NoPermissions() {
		assertNull(accountFilter.getCountrySubdivisionMap());
	}

	@Test
	public void testGetCountrySubdivisionMap_Permissions() {
		accountFilter.setPermissions(permissions);

		accountFilter.getCountrySubdivisionMap();

		verify(countrySubdivisionDAO).getCountrySubdivisionMap(anyString());
	}

	@Test
	public void testGetCountryList() {
		Whitebox.setInternalState(accountFilter, "countryDAOForTests", countryDAO);

		accountFilter.getCountryList();

		verify(countryDAO).findAll();
	}

	@Test
	public void testIsShowAccountName() {
		assertTrue(accountFilter.isShowAccountName());
		accountFilter.setShowAccountName(false);
		assertFalse(accountFilter.isShowAccountName());
	}

	@Test
	public void testIsShowAddress() {
		assertFalse(accountFilter.isShowAddress());
		accountFilter.setShowAddress(true);
		assertTrue(accountFilter.isShowAddress());
	}

	@Test
	public void testIsShowStatus() {
		assertFalse(accountFilter.isShowStatus());
		accountFilter.setShowStatus(true);
		assertTrue(accountFilter.isShowStatus());
	}

	@Test
	public void testIsShowType() {
		assertFalse(accountFilter.isShowType());
		accountFilter.setShowType(true);
		assertTrue(accountFilter.isShowType());
	}

	@Test
	public void testIsShowPrimaryInformation() {
		assertFalse(accountFilter.isShowPrimaryInformation());
		accountFilter.setShowPrimaryInformation(true);
		assertTrue(accountFilter.isShowPrimaryInformation());
	}

	@Test
	public void testIsShowTradeInformation() {
		assertFalse(accountFilter.isShowTradeInformation());
		accountFilter.setShowTradeInformation(true);
		assertTrue(accountFilter.isShowTradeInformation());
	}

	@Test
	public void testIsShowTitleName() {
		assertFalse(accountFilter.isShowTitleName());
		accountFilter.setShowTitleName(true);
		assertTrue(accountFilter.isShowTitleName());
	}

	@Test
	public void testGetStartsWith() {
		assertNull(accountFilter.getStartsWith());
		accountFilter.setStartsWith("Test");
		assertEquals("Test", accountFilter.getStartsWith());
	}

	@Test
	public void testGetAccountName_Null() {
		accountFilter.setAccountName(null);

		accountFilter.getAccountName();

		verify(i18nCache).getText(eq("global.CompanyName"), (Locale) any());
	}

	@Test
	public void testGetAccountName_NotNull() {
		accountFilter.setAccountName("Account Name");

		accountFilter.getAccountName();

		verify(i18nCache, never()).getText("global.CompanyName", Locale.ENGLISH);
	}

	@Test
	public void testGetCity_Null() {
		accountFilter.setCity(null);

		accountFilter.getCity();

		verify(i18nCache).getText(eq("global.City"), (Locale) any());
	}

	@Test
	public void testGetCity_NotNull() {
		accountFilter.setCity("City");

		accountFilter.getCity();

		verify(i18nCache, never()).getText(anyString(), (Locale) any(Locale.class));
	}

	@Test
	public void testGetLocation() {
		assertNull(accountFilter.getLocation());

		String[] locations = new String[] { "Location" };

		accountFilter.setLocation(locations);

		for (int index = 0; index < locations.length; index++) {
			assertEquals(locations[index], accountFilter.getLocation()[index]);
		}
	}

	@Test
	public void testGetZip_Null() {
		accountFilter.setZip(null);

		accountFilter.getZip();

		verify(i18nCache).getText(eq("global.ZipPostalCode"), (Locale) any());
	}

	@Test
	public void testGetZip_NotNull() {
		accountFilter.setZip("Zip");

		accountFilter.getZip();

		verify(i18nCache, never()).getText(anyString(), (Locale) any(Locale.class));
	}

	@Test
	public void testGetType() {
		assertNull(accountFilter.getType());

		String[] types = new String[] { "Type" };

		accountFilter.setType(types);

		for (int index = 0; index < types.length; index++) {
			assertEquals(types[index], accountFilter.getType()[index]);
		}
	}

	@Test
	public void testGetStatus() {
		assertNull(accountFilter.getStatus());

		AccountStatus[] statuses = new AccountStatus[] { AccountStatus.Active };

		accountFilter.setStatus(statuses);

		for (int index = 0; index < statuses.length; index++) {
			assertEquals(statuses[index], accountFilter.getStatus()[index]);
		}
	}

	@Test
	public void testGetStatusList_NullPermissions() {
		assertNotNull(accountFilter.getStatusList());

		int resultLength = accountFilter.getStatusList().length;
		int originalLength = AccountStatus.values().length;

		assertEquals(originalLength - 1, resultLength);
	}

	@Test
	public void testGetStatusList_Contractor() {
		accountFilter.setPermissions(permissions);
		when(permissions.isContractor()).thenReturn(true);

		assertNotNull(accountFilter.getStatusList());

		int resultLength = accountFilter.getStatusList().length;
		int originalLength = AccountStatus.values().length;

		assertEquals(originalLength - 1, resultLength);
	}

	@Test
	public void testGetStatusList_OperatorNoPermission() {
		accountFilter.setPermissions(permissions);
		when(permissions.isOperatorCorporate()).thenReturn(true);
		assertNotNull(accountFilter.getStatusList());

		int resultLength = accountFilter.getStatusList().length;
		int originalLength = AccountStatus.values().length;

		assertEquals(originalLength - 1, resultLength);
	}

	@Test
	public void testGetStatusList_OperatorWithPermission() {
		accountFilter.setPermissions(permissions);
		when(permissions.isOperatorCorporate()).thenReturn(true);
		when(permissions.hasPermission(OpPerms.RequestNewContractor)).thenReturn(true);

		assertNotNull(accountFilter.getStatusList());

		int resultLength = accountFilter.getStatusList().length;
		int originalLength = AccountStatus.values().length;

		assertEquals(originalLength, resultLength);
	}

	@Test
	public void testGetStatusList_PicsEmployee() {
		accountFilter.setPermissions(permissions);
		when(permissions.isPicsEmployee()).thenReturn(true);

		assertNotNull(accountFilter.getStatusList());

		int resultLength = accountFilter.getStatusList().length;
		int originalLength = AccountStatus.values().length;

		assertEquals(originalLength, resultLength);
	}

	@Test
	public void testIsPrimaryInformation() {
		assertFalse(accountFilter.isPrimaryInformation());
		accountFilter.setPrimaryInformation(true);
		assertTrue(accountFilter.isPrimaryInformation());
	}

	@Test
	public void testIsTradeInformation() {
		assertFalse(accountFilter.isTradeInformation());
		accountFilter.setTradeInformation(true);
		assertTrue(accountFilter.isTradeInformation());
	}

	@Test
	public void testGetTitleName() {
		assertNull(accountFilter.getTitleName());
		accountFilter.setTitleName("Title");
		assertEquals("Title", accountFilter.getTitleName());
	}
}
