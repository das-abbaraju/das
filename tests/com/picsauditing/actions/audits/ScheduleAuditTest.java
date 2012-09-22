package com.picsauditing.actions.audits;

import static org.mockito.Mockito.*;
import static org.mockito.Matchers.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletResponse;

import com.picsauditing.dao.*;
import org.apache.struts2.ServletActionContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.PicsActionTest;
import com.picsauditing.PicsTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.FeeClass;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.InvoiceItem;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.PicsDateFormat;

public class ScheduleAuditTest extends PicsActionTest {
	private ScheduleAudit scheduleAudit;

	@Mock
	private AuditorAvailabilityDAO auditorAvailabilityDAO;
	@Mock
	private InvoiceDAO invoiceDAO;
	@Mock
	private InvoiceFeeDAO feeDAO;
	@Mock
	private InvoiceItemDAO itemDAO;
	@Mock
	private UserAccessDAO uaDAO;
	@Mock
	private ContractorAuditDAO auditDao;
	@Mock
	private ContractorAccountDAO contractorAccountDao;
    @Mock
	private NoteDAO dao;
	@Mock
	private EmailSender emailSender;
	@Mock
	private InvoiceFee rescheduling;
	@Mock
	private InvoiceFee expedite;
	@Mock
	private User auditor;
	@Mock
	private ContractorAudit conAudit;
	@Mock
	private ContractorAccount contractor;
	@Mock
	private AuditType auditType;
	@Mock
	private Country country;
	@Mock
	private Invoice invoice;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		scheduleAudit = new ScheduleAudit();
		super.setUp(scheduleAudit);

		PicsTestUtil.autowireDAOsFromDeclaredMocks(scheduleAudit, this);

		Whitebox.setInternalState(scheduleAudit, "conAudit", conAudit);
		Whitebox.setInternalState(scheduleAudit, "emailSender", emailSender);
		Whitebox.setInternalState(scheduleAudit, "expedite", expedite);
		Whitebox.setInternalState(scheduleAudit, "rescheduling", rescheduling);

		parameters.put("auditor.id", 941);

		when(feeDAO.findByNumberOfOperatorsAndClass(FeeClass.ReschedulingFee, 0)).thenReturn(rescheduling);
		when(feeDAO.findByNumberOfOperatorsAndClass(FeeClass.ExpediteFee, 0)).thenReturn(expedite);
	}

	@Test(expected = NoRightsException.class)
	public void testSave_UserIsNotAdmin() throws Exception {
		when(permissions.isAdmin()).thenReturn(false);

		scheduleAudit.save();
	}

	@Test
	public void testSave_NullAuditorSendsBackToEdit() throws Exception {
		when(permissions.isAdmin()).thenReturn(true);
		Whitebox.setInternalState(scheduleAudit, "auditor", (User) null);

		String strutsAction = scheduleAudit.save();

		assertThat(strutsAction, is(equalTo("edit")));
	}

	@Test
	public void testSave_NullScheduledDateSendsBackToEdit() throws Exception {
		when(permissions.isAdmin()).thenReturn(true);
		Whitebox.setInternalState(scheduleAudit, "auditor", auditor);

		String strutsAction = scheduleAudit.save();

		assertThat(strutsAction, is(equalTo("edit")));
	}

	@Test
	public void testSave_NothingChanged() throws Exception {
		Calendar timeScheduledViaUI = Calendar.getInstance(TimeZone.getDefault());
		timeScheduledViaUI.set(2012, 8, 5, 8, 0, 0);
		Date dateScheduledViaUI = timeScheduledViaUI.getTime();
		when(permissions.isAdmin()).thenReturn(true);
		Whitebox.setInternalState(scheduleAudit, "auditor", auditor);

		SimpleDateFormat sdf = new SimpleDateFormat(PicsDateFormat.American);
		scheduleAudit.setScheduledDateDay(sdf.format(dateScheduledViaUI));
		sdf.applyPattern(PicsDateFormat.Time12Hour);
		scheduleAudit.setScheduledDateTime(sdf.format(dateScheduledViaUI));

		when(permissions.getTimezone()).thenReturn(TimeZone.getDefault());
		when(permissions.getUserId()).thenReturn(941);
		when(conAudit.getAuditor()).thenReturn(new User(941));
		when(auditDao.findScheduledAudits(eq(941), (Date) any(), (Date) any())).thenReturn(
				new ArrayList<ContractorAudit>());
		when(conAudit.getAuditorConfirm()).thenReturn(new Date());
		when(conAudit.getContractorConfirm()).thenReturn(new Date());
		when(conAudit.getScheduledDate()).thenReturn(dateScheduledViaUI);

		String strutsAction = scheduleAudit.save();

		assertThat(strutsAction, is(equalTo("blank")));
		verify(conAudit, never()).setScheduledDate((Date) any());
		verify(response).sendRedirect(anyString());
	}

	@Test
	public void testSave_ChangedScheduledDateNoFeeRequired() throws Exception {
		Calendar timeScheduledViaUI = Calendar.getInstance(TimeZone.getDefault());
		timeScheduledViaUI.add(Calendar.MONTH, 2);
		timeScheduledViaUI.set(Calendar.SECOND, 0);
		Date dateScheduledViaUI = timeScheduledViaUI.getTime();

		Calendar originalScheduledTime = Calendar.getInstance(TimeZone.getDefault());
		originalScheduledTime.add(Calendar.MONTH, 1);
		originalScheduledTime.set(Calendar.SECOND, 0);
		Date originalScheduledDate = originalScheduledTime.getTime();

		SimpleDateFormat sdf = new SimpleDateFormat(PicsDateFormat.American);
		scheduleAudit.setScheduledDateDay(sdf.format(dateScheduledViaUI));
		sdf.applyPattern(PicsDateFormat.Time12Hour);
		scheduleAudit.setScheduledDateTime(sdf.format(dateScheduledViaUI));

		when(permissions.isAdmin()).thenReturn(true);
		Whitebox.setInternalState(scheduleAudit, "auditor", auditor);
		when(auditor.getId()).thenReturn(941);
		when(permissions.getTimezone()).thenReturn(TimeZone.getDefault());
		when(permissions.getUserId()).thenReturn(941);
		when(conAudit.getAuditor()).thenReturn(new User(941));
		when(auditDao.findScheduledAudits(eq(941), (Date) any(), (Date) any())).thenReturn(
				new ArrayList<ContractorAudit>());
		when(conAudit.getAuditorConfirm()).thenReturn(new Date());
		when(conAudit.getContractorConfirm()).thenReturn(new Date());
		when(conAudit.getScheduledDate()).thenReturn(originalScheduledDate);

		String strutsAction = scheduleAudit.save();

		assertThat(strutsAction, is(equalTo("blank")));
		verify(conAudit).setScheduledDate((Date) any());
		verify(conAudit).setContractorConfirm(null);
		verify(contractorAccountDao, never()).save((ContractorAccount) any());
		verify(response).sendRedirect(anyString());
	}

	@Test
	public void testSave_ChangedScheduledDateFeeRequiredBecauseScheduledDateTomorrow() throws Exception {
		Calendar timeScheduledViaUI = Calendar.getInstance(TimeZone.getDefault());
		timeScheduledViaUI.add(Calendar.MONTH, 1);
		timeScheduledViaUI.set(Calendar.SECOND, 0);
		Date dateScheduledViaUI = timeScheduledViaUI.getTime();

		Calendar originalScheduledTime = Calendar.getInstance(TimeZone.getDefault());
		originalScheduledTime.add(Calendar.DAY_OF_YEAR, 1);
		originalScheduledTime.set(Calendar.SECOND, 0);
		Date originalScheduledDate = originalScheduledTime.getTime();

		setupForSaveTest(dateScheduledViaUI, originalScheduledDate);

		String strutsAction = scheduleAudit.save();

		assertThat(strutsAction, is(equalTo("blank")));
		verify(conAudit).setScheduledDate((Date) any());
		verify(conAudit).setContractorConfirm(null);
		verify(contractorAccountDao).save((ContractorAccount) any());
		verify(invoiceDAO).save((Invoice) any());
		ArgumentCaptor<InvoiceItem> captor = ArgumentCaptor.forClass(InvoiceItem.class);
		verify(itemDAO).save(captor.capture());
		InvoiceItem itemSaved = captor.getValue();
		InvoiceFee fee = itemSaved.getInvoiceFee();
		assertThat(rescheduling, is(equalTo(fee)));
		verify(dao).save((Note) any());
		verify(response).sendRedirect(anyString());
	}

	@Test
	public void testSave_ChangedScheduledDateFeeRequiredForExpedite() throws Exception {
		Calendar timeScheduledViaUI = Calendar.getInstance(TimeZone.getDefault());
		timeScheduledViaUI.add(Calendar.DAY_OF_YEAR, 1);
		timeScheduledViaUI.set(Calendar.SECOND, 0);
		Date dateScheduledViaUI = timeScheduledViaUI.getTime();

		Calendar originalScheduledTime = Calendar.getInstance(TimeZone.getDefault());
		originalScheduledTime.add(Calendar.MONTH, 1);
		originalScheduledTime.set(Calendar.SECOND, 0);
		Date originalScheduledDate = originalScheduledTime.getTime();

		setupForSaveTest(dateScheduledViaUI, originalScheduledDate);
		when(feeDAO.findByNumberOfOperatorsAndClass(FeeClass.ExpediteFee, 0)).thenReturn(expedite);

		String strutsAction = scheduleAudit.save();

		assertThat(strutsAction, is(equalTo("blank")));
		verify(conAudit).setScheduledDate((Date) any());
		verify(conAudit).setContractorConfirm(null);
		verify(contractorAccountDao).save((ContractorAccount) any());
		verify(invoiceDAO).save((Invoice) any());
		ArgumentCaptor<InvoiceItem> captor = ArgumentCaptor.forClass(InvoiceItem.class);
		verify(itemDAO).save(captor.capture());
		InvoiceItem itemSaved = captor.getValue();
		InvoiceFee fee = itemSaved.getInvoiceFee();
		assertThat(expedite, is(equalTo(fee)));
		verify(dao).save((Note) any());
		verify(response).sendRedirect(anyString());
	}

	// this class obviously has too many responsibilities and dependencies for a
	// simple test to require this
	// much setup.
	private void setupForSaveTest(Date dateScheduledViaUI, Date originalScheduledDate) {
		SimpleDateFormat sdf = new SimpleDateFormat(PicsDateFormat.American);
		scheduleAudit.setScheduledDateDay(sdf.format(dateScheduledViaUI));
		sdf.applyPattern(PicsDateFormat.Time12Hour);
		scheduleAudit.setScheduledDateTime(sdf.format(dateScheduledViaUI));

		when(permissions.isAdmin()).thenReturn(true);
		Whitebox.setInternalState(scheduleAudit, "auditor", auditor);
		when(auditor.getId()).thenReturn(941);
		when(permissions.getTimezone()).thenReturn(TimeZone.getDefault());
		when(permissions.getUserId()).thenReturn(941);
		when(conAudit.getAuditor()).thenReturn(new User(941));
		when(auditDao.findScheduledAudits(eq(941), (Date) any(), (Date) any())).thenReturn(
				new ArrayList<ContractorAudit>());
		when(conAudit.getAuditorConfirm()).thenReturn(new Date());
		when(conAudit.getContractorConfirm()).thenReturn(new Date());
		when(conAudit.getScheduledDate()).thenReturn(originalScheduledDate);
		when(conAudit.getAuditType()).thenReturn(auditType);
		when(auditType.getI18nKey("name")).thenReturn("test");
		Whitebox.setInternalState(scheduleAudit, "contractor", contractor);
		when(contractor.getCountry()).thenReturn(country);
		when(invoiceDAO.save((Invoice) any())).thenReturn(invoice);
	}
}
