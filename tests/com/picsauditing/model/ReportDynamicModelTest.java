package com.picsauditing.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.util.reflection.Whitebox.*;

import javax.persistence.NoResultException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picsauditing.access.ReportValidationException;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.model.ReportDynamicModel;
import com.picsauditing.provider.ReportProvider;
import com.picsauditing.report.models.ModelType;

public class ReportDynamicModelTest {

	private ReportDynamicModel model;

	@Mock private ReportProvider reportProvider;
	@Mock private Report report;
	@Mock private User user;

	private static final int USER_ID = 23;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		model = new ReportDynamicModel();

		setInternalState(model, "reportProvider", reportProvider);

		when(report.getId()).thenReturn(555);
		when(user.getId()).thenReturn(USER_ID);
	}

	@Test
	public void canUserViewAndCopy_nullQuery() {
		when(reportProvider.findOneUserReport(anyInt(), anyInt())).thenReturn(null);

		assertFalse(model.canUserViewAndCopy(USER_ID, report));
	}

	@Test
	public void canUserViewAndCopy_emptyQuery() {

		when(reportProvider.findOneUserReport(anyInt(), anyInt())).thenThrow(new NoResultException());

		assertFalse(model.canUserViewAndCopy(USER_ID, report));
	}

	@Test
	public void canUserViewAndCopy_baseReport() {
		assertTrue(model.canUserViewAndCopy(USER_ID, 2));
	}

	@Test
	public void canUserViewAndCopy_successfulQuery () {

		when(reportProvider.findOneUserReport(anyInt(), anyInt())).thenReturn(new ReportUser());

		assertTrue(model.canUserViewAndCopy(USER_ID, report));
	}

	@Test
	public void canUserEdit_Negative() {
		ReportUser mockReportUser = mock(ReportUser.class);
		when(mockReportUser.isEditable()).thenReturn(false);
		when(reportProvider.findOneUserReport(anyInt(), anyInt())).thenReturn(mockReportUser);

		assertFalse(model.canUserEdit(USER_ID, report));
	}

	@Test
	public void canUserEdit_Positive() {
		ReportUser mockReportUser = mock(ReportUser.class);
		when(mockReportUser.isEditable()).thenReturn(true);
		when(reportProvider.findOneUserReport(anyInt(), anyInt())).thenReturn(mockReportUser);

		assertTrue(model.canUserEdit(USER_ID, report));
	}

	@Test
	public void canUserDelete_Negative() {
		User mockUser = mock(User.class);
		when(mockUser.getId()).thenReturn(5);
		when(report.getCreatedBy()).thenReturn(mockUser);

		assertFalse(ReportDynamicModel.canUserDelete(USER_ID, report));
	}

	@Test
	public void canUserDelete_Positive() {
		User mockUser = mock(User.class);
		when(mockUser.getId()).thenReturn(USER_ID);
		when(report.getCreatedBy()).thenReturn(mockUser);

		assertTrue(ReportDynamicModel.canUserDelete(USER_ID, report));
	}

	@Test(expected = ReportValidationException.class)
		public void testValidate_NullReport() throws ReportValidationException {
			Report report = null;

			ReportDynamicModel.validate(report);
		}

	@Test(expected = ReportValidationException.class)
		public void testValidate_NullModelType() throws ReportValidationException {
			Report report = new Report();
			report.setModelType(null);

			ReportDynamicModel.validate(report);
		}

	@Test(expected = ReportValidationException.class)
		public void testValidate_InvalidReportParameters() throws ReportValidationException {
			Report report = new Report();
			report.setModelType(ModelType.Accounts);
			report.setParameters("NOT_A_REPORT");

			ReportDynamicModel.validate(report);
		}

	@Test
		public void testValidate_ValidReportParameters() throws ReportValidationException {
			Report report = new Report();
			report.setModelType(ModelType.Accounts);
			report.setParameters("{}");

			ReportDynamicModel.validate(report);
		}
}
