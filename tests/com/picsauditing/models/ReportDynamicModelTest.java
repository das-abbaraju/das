package com.picsauditing.models;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.util.reflection.Whitebox.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.report.access.ReportAdministration;

public class ReportDynamicModelTest {

	private ReportDynamicModel model;

	@Mock private ReportAdministration reportAccessor;
	@Mock private Report report;
	@Mock private User user;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		model = new ReportDynamicModel();

		setInternalState(model, "reportAccessor", reportAccessor);

		when(report.getId()).thenReturn(555);
		when(user.getId()).thenReturn(23);
	}

	@Test
	public void canUserViewAndCopy_nullQuery () {
		when(reportAccessor.queryReportUser(anyInt(), anyInt())).thenReturn(null);

		assertFalse(model.canUserViewAndCopy(23, report));
	}

	@Test
	public void canUserViewAndCopy_emptyQuery () {
		List<ReportUser> emptyList = new ArrayList<ReportUser>();
		when(reportAccessor.queryReportUser(anyInt(), anyInt())).thenReturn(emptyList);

		assertFalse(model.canUserViewAndCopy(23, report));
	}

	@Test
	public void canUserViewAndCopy_successfulQuery () {
		List<ReportUser> reportUserList = new ArrayList<ReportUser>();
		reportUserList.add(new ReportUser());
		when(reportAccessor.queryReportUser(anyInt(), anyInt())).thenReturn(reportUserList);

		assertTrue(model.canUserViewAndCopy(23, report));
	}

	@Test
	public void canUserEdit_nullResponse () {
		when(reportAccessor.queryReportUser(anyInt(), anyInt())).thenReturn(null);

		assertFalse(model.canUserEdit(23, report));
	}

	@Test
	public void canUserEdit_Negative () {
		ReportUser mockReportUser = mock(ReportUser.class);
		when(mockReportUser.isEditable()).thenReturn(false);
		List<ReportUser> reportUserList = new ArrayList<ReportUser>();
		reportUserList.add(mockReportUser);
		when(reportAccessor.queryReportUser(anyInt(), anyInt())).thenReturn(reportUserList);

		assertFalse(model.canUserEdit(23, report));
	}

	@Test
	public void canUserEdit_Positive () {
		ReportUser mockReportUser = mock(ReportUser.class);
		when(mockReportUser.isEditable()).thenReturn(true);
		List<ReportUser> reportUserList = new ArrayList<ReportUser>();
		reportUserList.add(mockReportUser);
		when(reportAccessor.queryReportUser(anyInt(), anyInt())).thenReturn(reportUserList);

		assertTrue(model.canUserEdit(23, report));
	}

	@Test
	public void canUserDelete_Negative () {
		User mockCreator = mock(User.class);
		when(mockCreator.getId()).thenReturn(5);
		when(report.getCreatedBy()).thenReturn(mockCreator);

		assertFalse(model.canUserDelete(23, report));
	}

	@Test
	public void canUserDelete_Positive () {
		User mockCreator = mock(User.class);
		when(mockCreator.getId()).thenReturn(23);
		when(report.getCreatedBy()).thenReturn(mockCreator);

		assertTrue(model.canUserDelete(23, report));
	}
}
