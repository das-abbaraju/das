package com.picsauditing.actions.report;

import static org.junit.Assert.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.dao.ReportDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.SubscriptionTimePeriod;
import com.picsauditing.report.ReportContext;
import com.picsauditing.service.PermissionService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PicsActionTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.service.ReportService;

import java.util.ArrayList;
import java.util.List;

public class ReportApiTest extends PicsActionTest {

    private static final int REPORT_ID = 123;
    private static final int USER_ID = 12345;

    private ReportApi reportApi;

    @Mock
    private PermissionService permissionService;
	@Mock
	private ReportService reportService;
    @Mock
    private ReportDAO reportDAO;
    @Mock
    private UserDAO userDAO;
    @Mock
    private EmailSubscriptionDAO emailSubscriptionDAO;
    @Mock
	private Report report;
    @Mock
    private User user;
    @Mock
    private EmailSubscription emailSubscription;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		reportApi = new ReportApi();
		super.setUp(reportApi);

		PicsTestUtil.autowireDAOsFromDeclaredMocks(reportApi, this);

		Whitebox.setInternalState(reportApi, "reportService", reportService);
		Whitebox.setInternalState(reportApi, "permissionService", permissionService);
		Whitebox.setInternalState(reportApi, "userDAO", userDAO);
		Whitebox.setInternalState(reportApi, "reportDao", reportDAO);
		Whitebox.setInternalState(reportApi, "emailSubscriptionDAO", emailSubscriptionDAO);
	}

	@Test
	public void testExecute() throws Exception {
		String strutsResult = reportApi.execute();

		assertEquals(PicsActionSupport.JSON, strutsResult);
	}

    @Test
    public void testSubscribe_userNull() throws Exception {
        reportApi.setUser(null);
        reportApi.setReportId(REPORT_ID);
        reportApi.setFrequency(SubscriptionTimePeriod.Daily);
        List<EmailSubscription> subscriptions = new ArrayList<>();
        subscriptions.add(emailSubscription);

        when(reportDAO.findById(REPORT_ID)).thenReturn(report);
        when(report.getId()).thenReturn(REPORT_ID);
        when(permissions.getUserId()).thenReturn(USER_ID);
        when(userDAO.find(USER_ID)).thenReturn(user);
        when(permissionService.canUserViewReport(user, report, permissions)).thenReturn(true);
        when(emailSubscriptionDAO.findByUserIdReportId(USER_ID, REPORT_ID)).thenReturn(subscriptions);
        String strutsResult = reportApi.subscribe();

        verify(emailSubscription).setTimePeriod(SubscriptionTimePeriod.Daily);
        verify(emailSubscription).setReport(report);
        verify(emailSubscriptionDAO).save(emailSubscription);
        assertEquals(PicsActionSupport.PLAIN_TEXT, strutsResult);
    }

    @Test
    public void testBuildReportContext_NoUser() throws Exception {
        reportApi.setUser(null);

        ReportContext reportContext = reportApi.buildReportContext(null);

        verify(permissions).getUserId();
    }

    @Test
    public void testBuildReportContext_switchedUser() throws Exception {
        reportApi.setUser(user);
        when(user.getId()).thenReturn(USER_ID);
        when(permissions.getUserId()).thenReturn(USER_ID);

        ReportContext reportContext = reportApi.buildReportContext(null);

        verify(permissions, never()).login(user);
    }


}
