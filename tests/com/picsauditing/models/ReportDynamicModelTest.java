package com.picsauditing.models;

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
import com.picsauditing.report.access.ReportAccessor;
import com.picsauditing.report.models.ModelType;

public class ReportDynamicModelTest {

	private ReportDynamicModel model;

	@Mock private ReportAccessor reportAccessor;
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
	public void canUserViewAndCopy_nullQuery() {
		when(reportAccessor.findOneUserReport(anyInt(), anyInt())).thenReturn(null);

		assertFalse(model.canUserViewAndCopy(23, report));
	}

	@Test
	public void canUserViewAndCopy_emptyQuery() {

		when(reportAccessor.findOneUserReport(anyInt(), anyInt())).thenThrow(new NoResultException());

		assertFalse(model.canUserViewAndCopy(23, report));
	}

	@Test
	public void canUserViewAndCopy_baseReport() {
		assertTrue(model.canUserViewAndCopy(23, 2));
	}

	@Test
	public void canUserViewAndCopy_successfulQuery () {

		when(reportAccessor.findOneUserReport(anyInt(), anyInt())).thenReturn(new ReportUser());

		assertTrue(model.canUserViewAndCopy(23, report));
	}

	@Test
	public void canUserEdit_Negative() {
		ReportUser mockReportUser = mock(ReportUser.class);
		when(mockReportUser.isEditable()).thenReturn(false);
		when(reportAccessor.findOneUserReport(anyInt(), anyInt())).thenReturn(mockReportUser);

		assertFalse(model.canUserEdit(23, report));
	}

	@Test
	public void canUserEdit_Positive() {
		ReportUser mockReportUser = mock(ReportUser.class);
		when(mockReportUser.isEditable()).thenReturn(true);
		when(reportAccessor.findOneUserReport(anyInt(), anyInt())).thenReturn(mockReportUser);

		assertTrue(model.canUserEdit(23, report));
	}

	@Test
	public void canUserDelete_Negative() {
		User mockCreator = mock(User.class);
		when(mockCreator.getId()).thenReturn(5);
		when(report.getCreatedBy()).thenReturn(mockCreator);

		assertFalse(ReportDynamicModel.canUserDelete(23, report));
	}

	@Test
	public void canUserDelete_Positive() {
		User mockCreator = mock(User.class);
		when(mockCreator.getId()).thenReturn(23);
		when(report.getCreatedBy()).thenReturn(mockCreator);

		assertTrue(ReportDynamicModel.canUserDelete(23, report));
	}

	@Test
	public void removeReportFrom_canDelete() throws Exception {
		when(report.getCreatedBy()).thenReturn(user);

		model.removeReportFrom(user, report);

		verify(reportAccessor, never()).removeUserReport(user, report);
		verify(reportAccessor).deleteReport(report);
	}

	@Test
	public void removeReportFrom_cantDelete() throws Exception {
		when(report.getCreatedBy()).thenReturn(new User(5));

		model.removeReportFrom(user, report);

		verify(reportAccessor).removeUserReport(user, report);
		verify(reportAccessor, never()).deleteReport(report);
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
