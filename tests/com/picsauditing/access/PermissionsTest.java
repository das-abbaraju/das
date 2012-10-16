package com.picsauditing.access;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;


public class PermissionsTest {
	private Permissions permissions;
	private Map<Integer, String> groups;

	@Mock
	private HttpServletResponse response;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		permissions = new Permissions();

		groups = new HashMap<Integer, String>();
		Whitebox.setInternalState(permissions, "groups", groups);
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
		Whitebox.setInternalState(permissions, "groups", (Map<Integer, String>) null);

		assertFalse(permissions.hasGroup(1));
	}

	@Test
	public void testHasGroup_FalseEmptyGroups() throws Exception {
		groups.clear();

		assertFalse(permissions.hasGroup(1));
	}

	@Test
	public void testHasGroup_Normal() throws Exception {
		groups.put(1, "one");

		assertTrue(permissions.hasGroup(1));
		assertFalse(permissions.hasGroup(2));
	}

	@Test
	public void testBelongsToGroups_True() throws Exception {
		groups.put(1, "one");

		assertTrue(permissions.belongsToGroups());
	}

	@Test
	public void testBelongsToGroups_EmptyGroupsReturnsFalse() throws Exception {
		groups.clear();

		assertFalse(permissions.belongsToGroups());
	}

	@Test
	public void testBelongsToGroups_NullGroupsReturnsFalse() throws Exception {
		Whitebox.setInternalState(permissions, "groups", (Map<Integer, String>) null);

		assertFalse(permissions.belongsToGroups());
	}

}
