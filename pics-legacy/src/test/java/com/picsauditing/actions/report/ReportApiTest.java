package com.picsauditing.actions.report;

import static org.junit.Assert.*;

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

public class ReportApiTest extends PicsActionTest {

	private ReportApi reportApi;

    @Mock
    private PermissionService permissionService;
	@Mock
	private ReportService reportService;
	@Mock
	private Report report;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		reportApi = new ReportApi();
		super.setUp(reportApi);

		PicsTestUtil.autowireDAOsFromDeclaredMocks(reportApi, this);

		Whitebox.setInternalState(reportApi, "reportService", reportService);
		Whitebox.setInternalState(reportApi, "permissionService", permissionService);
	}

	@Test
	public void testExecute() throws Exception {
		String strutsResult = reportApi.execute();

		assertEquals(PicsActionSupport.JSON, strutsResult);
	}

    @Test
    public void testSubscribe() throws Exception {
        String strutsResult = reportApi.subscribe();

        assertEquals(PicsActionSupport.PLAIN_TEXT, strutsResult);
    }
}
