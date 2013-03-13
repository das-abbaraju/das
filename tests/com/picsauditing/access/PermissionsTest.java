package com.picsauditing.access;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.model.i18n.LanguageModel;
import com.picsauditing.util.SpringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.springframework.context.ApplicationContext;


public class PermissionsTest {
	private Permissions permissions;
	private Set<Integer> allInheritedGroupIds;

	@Mock
	private Account account;
	@Mock
	private HttpServletResponse response;
	@Mock
	private LanguageModel languageModel;
	@Mock
	private User user;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		permissions = new Permissions(languageModel);
		allInheritedGroupIds = new HashSet<Integer>();
		Whitebox.setInternalState(permissions, "allInheritedGroupIds", allInheritedGroupIds);

		Country country = mock(Country.class);
		when(account.getCountry()).thenReturn(country);
		when(country.getIsoCode()).thenReturn(LanguageModel.ENGLISH.getCountry());
		when(user.getAccount()).thenReturn(account);
	}

	@Test
	public void testAddReturnToCookieIfGoodUrl_LeadingQuote() throws Exception {
		Whitebox.invokeMethod(permissions, "addReturnToCookieIfGoodUrl", response, "\"/Home.action");

		ArgumentCaptor<Cookie> argument = ArgumentCaptor.forClass(Cookie.class);
		verify(response).addCookie(argument.capture());
		Cookie arg = argument.getValue();

		assertEquals("/Home.action", arg.getValue());
	}

	@Test
	public void testAddReturnToCookieIfGoodUrl_NoLeadingQuote() throws Exception {
		Whitebox.invokeMethod(permissions, "addReturnToCookieIfGoodUrl", response, "/UsersManage.action?user=5484");
		ArgumentCaptor<Cookie> argument = ArgumentCaptor.forClass(Cookie.class);
		verify(response).addCookie(argument.capture());
		Cookie arg = argument.getValue();

		assertEquals("/UsersManage.action?user=5484", arg.getValue());
	}

	@Test
	public void testReturnUrlIsOk() throws Exception {
		List<String> falseUrls = new ArrayList<String>();
		falseUrls.add("ChartXMLWaitingOnCount.action?FCTime=789");
		falseUrls.add("ChartXMLTradeCount.action");
		falseUrls.add("ManageUserPermissionsAjax.action");
		falseUrls.add("AuditCalendarJSON.action");
		falseUrls.add("Autocompleter.action");
		falseUrls.add("NewContractorSearchCSV.action");
		falseUrls.add("ManageImportData.action");
		falseUrls.add("ReportNewReqConImport.action");
		falseUrls.add("ContractorSummaryExternal.action");
		falseUrls.add("DownloadContractorFile.action");
		falseUrls.add("AuditTranslationDownload.action");
		falseUrls.add("CertificateUpload.action");
		falseUrls.add("");
		
		for (String url: falseUrls) {
			assertFalse((Boolean) Whitebox.invokeMethod(permissions, "returnUrlIsOk", url));
		}

		List<String> trueUrls = new ArrayList<String>();
		trueUrls.add("Home.action");
		trueUrls.add("ScheduleAudit.action");
		trueUrls.add("ContractorDashboard.action");
		for (String url : trueUrls) {
			assertTrue((Boolean) Whitebox.invokeMethod(permissions, "returnUrlIsOk", url));
		}
	}
	@Test
	public void testHasGroup_FalseNullGroups() throws Exception {
		Whitebox.setInternalState(permissions, "allInheritedGroupIds", (Set<Integer>) null);

		assertFalse(permissions.hasGroup(1));
	}

	@Test
	public void testHasGroup_FalseEmptyGroups() throws Exception {
		allInheritedGroupIds.clear();

		assertFalse(permissions.hasGroup(1));
	}

	@Test
	public void testHasGroup_Normal() throws Exception {
		allInheritedGroupIds.add(1);

		assertTrue(permissions.hasGroup(1));
		assertFalse(permissions.hasGroup(2));
	}

	@Test
	public void testBelongsToGroups_True() throws Exception {		
		allInheritedGroupIds.add(1);

		assertTrue(permissions.belongsToGroups());
	}

	@Test
	public void testBelongsToGroups_EmptyGroupsReturnsFalse() throws Exception {
		allInheritedGroupIds.clear();

		assertFalse(permissions.belongsToGroups());
	}

	@Test
	public void testBelongsToGroups_NullGroupsReturnsFalse() throws Exception {
		Whitebox.setInternalState(permissions, "allInheritedGroupIds", (Set<Integer>) null);

		assertFalse(permissions.belongsToGroups());
	}

	@Test
	public void testSetStableLocale_LanguageModelIsNotNullAndLocaleIsStable() throws Exception {
		Locale german = Locale.GERMAN;

		when(languageModel.getNearestStableAndBetaLocale(eq(german), anyString())).thenReturn(german);
		when(user.getLocale()).thenReturn(german);

		Whitebox.invokeMethod(permissions, "setStableLocale", user);

		assertEquals(german, permissions.getLocale());
	}

	@Test
	public void testSetStableLocale_LanguageModelIsNotNullAndLocaleIsNotStable() throws Exception {
		Locale german = Locale.GERMAN;
		Locale english = Locale.ENGLISH;

		when(languageModel.getNearestStableAndBetaLocale(eq(german), anyString())).thenReturn(english);
		when(user.getLocale()).thenReturn(german);

		Whitebox.invokeMethod(permissions, "setStableLocale", user);

		assertEquals(english, permissions.getLocale());
	}

	@Test
	public void testSetStableLocale_LanguageModelIsNull() throws Exception {
		when(user.getLocale()).thenReturn(Locale.GERMAN);

		Whitebox.setInternalState(permissions, "languageModel", (LanguageModel) null);
		Whitebox.invokeMethod(permissions, "setStableLocale", user);

		assertEquals(LanguageModel.ENGLISH, permissions.getLocale());
	}
}
