package com.picsauditing.actions.report;

import static org.mockito.Mockito.when;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PicsActionTest;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.report.ReportService;

public class ReportDynamicTest extends PicsActionTest {
	private ReportDynamic reportDynamic;

	@Mock
	private Report report;
	@Mock
	private ReportService reportService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		reportDynamic = new ReportDynamic();
		super.setUp(reportDynamic);

		when(request.getParameter("report")).thenReturn("123");

		Whitebox.setInternalState(reportDynamic, "report", report);
		when(permissions.getUserId()).thenReturn(941);
		Whitebox.setInternalState(reportDynamic, "reportService", reportService);
	}

//	@Test
//	public void testCopy_ProxiesToReportModelCopy() throws Exception {
//		when(reportService.copy(report, permissions, false)).thenReturn(report);
//
//		reportDynamic.copy();
//
//		verify(reportService).copy(report, permissions, false);
//	}
//
//	@Test
//	public void testSave_ProxiesToReportModelEdit() throws Exception {
//		reportDynamic.save();
//
//		verify(reportService).edit(report, permissions);
//	}
}
