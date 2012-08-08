package com.picsauditing.actions.report;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Matchers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.ReportDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.search.Database;

public class ManageReportsTest {

	ManageReports manageReports;

	@Mock private ReportDAO reportDao;
	@Mock private Permissions permissions;
	@Mock private I18nCache i18nCache;
	@Mock private Database databaseForTesting;

	private static final int USER_ID = 37;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", databaseForTesting);

		manageReports = new ManageReports();

		setUpI18nCacheText();

		Whitebox.setInternalState(manageReports, "reportDao", reportDao);
		when(permissions.getUserId()).thenReturn(USER_ID);
		Whitebox.setInternalState(manageReports, "permissions", permissions);
		Whitebox.setInternalState(manageReports, "i18nCache", i18nCache);
	}

	@Test
	public void testExecute_GoesToMyReportsByDefault() {
		List<ReportUser> userReports = new ArrayList<ReportUser>();
		userReports.add(new ReportUser());
		when(reportDao.findAllUserReports(USER_ID)).thenReturn(userReports);

		String result = manageReports.execute();

		assertEquals("myReports", result);
	}

	@Test
	public void testFavorites_ReturnsExpectedResult() {
		List<ReportUser> userReports = new ArrayList<ReportUser>();
		userReports.add(new ReportUser());
		when(reportDao.findFavoriteUserReports(USER_ID)).thenReturn(userReports);

		String result = manageReports.favorites();

		assertEquals("favorites", result);
	}

	@Test
	public void testFavorites_DoesntLeaveUserReportsNull() {
		when(reportDao.findFavoriteUserReports(USER_ID)).thenReturn(null);

		manageReports.favorites();

		assertNotNull(Whitebox.getInternalState(manageReports, "userReports"));
	}

	@Test
	public void testMyReports_ReturnsExpectedResult() {
		List<ReportUser> userReports = new ArrayList<ReportUser>();
		userReports.add(new ReportUser());
		when(reportDao.findAllUserReports(USER_ID)).thenReturn(userReports);

		String result = manageReports.myReports();

		assertEquals("myReports", result);
	}

	@Test
	public void testMyReports_DoesntLeaveUserReportsNull() {
		when(reportDao.findAllUserReports(USER_ID)).thenReturn(null);

		manageReports.myReports();

		assertNotNull(Whitebox.getInternalState(manageReports, "userReports"));
	}

	@Test
	public void testSearch_ReturnsExpectedResult() {
		List<ReportUser> userReports = new ArrayList<ReportUser>();
		userReports.add(new ReportUser());
		when(reportDao.findAllUserReports(USER_ID)).thenReturn(userReports);
		when(reportDao.findPublicReports()).thenReturn(new ArrayList<Report>());

		String result = manageReports.search();

		assertEquals("search", result);
	}

	@Test
	public void testSearch_DoesntLeaveUserReportsNull() {
		when(reportDao.findAllUserReports(USER_ID)).thenReturn(null);
		when(reportDao.findPublicReports()).thenReturn(new ArrayList<Report>());

		manageReports.search();

		assertNotNull(Whitebox.getInternalState(manageReports, "userReports"));
	}

	private void setUpI18nCacheText() {
		when(i18nCache.hasKey(anyString(), eq(Locale.ENGLISH)))
				.thenReturn(true);

		when(i18nCache.getText(eq("ManageReports.message.NoFavorites"), eq(Locale.ENGLISH), any()))
				.thenReturn("No Favorites.");
	}
}
