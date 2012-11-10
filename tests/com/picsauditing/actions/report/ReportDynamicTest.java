package com.picsauditing.actions.report;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PicsActionTest;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.model.report.ReportModel;

public class ReportDynamicTest extends PicsActionTest {
	private ReportDynamic reportDynamic;

	@Mock
	private Report report;
	@Mock
	private ReportModel reportModel;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		reportDynamic = new ReportDynamic();
		super.setUp(reportDynamic);

		when(request.getParameter("report")).thenReturn("123");

		reportDynamic.setReport(report);
		when(permissions.getUserId()).thenReturn(941);
		Whitebox.setInternalState(reportDynamic, "reportModel", reportModel);
	}

	@Test
	public void testCopy_ProxiesToReportModelCopy() throws Exception {
		when(reportModel.copy(report, permissions)).thenReturn(report);

		reportDynamic.copy();

		verify(reportModel).copy(report, permissions);
	}

	@Test
	public void testSave_ProxiesToReportModelEdit() throws Exception {
		reportDynamic.save();

		verify(reportModel).edit(report, permissions);
	}
}
