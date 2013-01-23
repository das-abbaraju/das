package com.picsauditing.actions.audits;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.TreeSet;

import com.picsauditing.PICS.InvoiceService;
import org.apache.struts2.ServletActionContext;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AuditorAvailabilityDAO;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.dao.InvoiceItemDAO;
import com.picsauditing.dao.UserAccessDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditorAvailability;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.FeeClass;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.InvoiceItem;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserAccess;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.strutsutil.AjaxUtils;
import com.picsauditing.util.EmailAddressUtils;
import com.picsauditing.util.PicsDateFormat;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ScheduleAudit extends AuditActionSupport implements Preparable {

	// TODO Move this to a common location
	static final public String GOOGLE_API_KEY = "AIzaSyBuCaFEPZ4Uzi9Y5HK0nUJUirHaVXSLBrk";

	private TimeZone selectedTimeZone = TimeZone.getTimeZone("CST");

	private AvailableSet availableSet = new AvailableSet();
	private Date timeSelected;
	private AuditorAvailability availabilitySelected = null;
	private int availabilitySelectedID;
	private boolean confirmed = false;
	private boolean feeOverride = false;
	private boolean readInstructions = false;

	private String scheduledDateDay;
	private String scheduledDateTime;
	private Date availabilityStartDate = new Date();

	private Set<User> auditorList;

	@Autowired
	private AuditorAvailabilityDAO auditorAvailabilityDAO;
	@Autowired
	private InvoiceService invoiceService;
	@Autowired
	private InvoiceFeeDAO feeDAO;
	@Autowired
	private InvoiceItemDAO itemDAO;
	@Autowired
	private UserAccessDAO uaDAO;
	@Autowired
	private EmailSender emailSender;

	private User auditor = null;
	private InvoiceFee rescheduling;
	private InvoiceFee expedite;

	public void prepare() throws Exception {
		this.auditID = getParameter("auditID");
		if (auditID > 0) {
			loadPermissions();
			findConAudit();
		}
		int auditorID = getParameter("auditor.id");
		if (auditorID > 0)
			auditor = getUser(auditorID);

		rescheduling = feeDAO.findByNumberOfOperatorsAndClass(FeeClass.ReschedulingFee, 0);
		expedite = feeDAO.findByNumberOfOperatorsAndClass(FeeClass.ExpediteFee, 0);
	}

	public String execute() throws Exception {
		if (conAudit == null) {
			addActionError("Missing auditID");
			return BLANK;
		}

		subHeading = "Schedule " + getText(conAudit.getAuditType().getI18nKey("name"));

		for (ContractorAuditOperator cao : conAudit.getOperators()) {
			if (cao.getStatus().after(AuditStatus.Pending)) {
				return "summary";
			}
		}

		if (button == null) {
			if (conAudit.getScheduledDate() == null)
				// We need to schedule this audit
				return "address";

			if (conAudit.getScheduledDate().before(new Date())) {
				// This audit has already passed (and we missed it?)
				addAlertMessage(getText("ScheduleAudit.message.AppointmentPassed"));
				return "edit";
			}

			if (permissions.isAdmin())
				// Let the admin reschedule the audit
				return "edit";

			// Contractors can't change upcoming scheduled audits
			return "summary";
		}

		return "address";
	}

	public String edit() throws Exception {
		for (ContractorAuditOperator cao : conAudit.getOperators()) {
			if (cao.getStatus().after(AuditStatus.Pending)) {
				return "summary";
			}
		}

		if (conAudit.getScheduledDate() != null && conAudit.getScheduledDate().before(new Date()))
			addActionMessage(getText("ScheduleAudit.message.AuditAppointmentPassed"));
		for (ContractorAuditOperator cao : conAudit.getOperators()) {
			if (cao.getStatus().after(AuditStatus.Pending)) {
				addActionMessage(getText("ScheduleAudit.message.AuditCompleted"));
				return "edit";
			}
		}

		return "edit";
	}

	public String save() throws Exception {
		if (!permissions.isAdmin())
			throw new NoRightsException("ScheduleAudits");

		if (auditor == null) {
			addActionError(getText("ScheduleAudit.error.SelectAnAuditor"));
			return "edit";
		}

		// We're looking for a change in the safety professional.
		// Was the auditor just set?
		// Is the new auditor the same person as the previous auditor?
		boolean changedAuditor = (conAudit.getAuditor() == null && auditor != null)
				|| !conAudit.getAuditor().equals(auditor);

		conAudit.setAuditor(auditor);
		conAudit.setClosingAuditor(new User(conAudit.getIndependentClosingAuditor(auditor)));
		conAudit.setAssignedDate(new Date());

		Date scheduledDateInUserTime = DateBean.parseDateTime(scheduledDateDay + " " + scheduledDateTime);
		if (scheduledDateInUserTime == null) {
			addActionError(getText("ScheduleAudit.error.NotValidTime", new Object[] { scheduledDateTime }));
			return "edit";
		}
		Date scheduledDateInServerTime = DateBean.convertTime(scheduledDateInUserTime, permissions.getTimezone());

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
		if (conAudit.getScheduledDate() == null
				|| !sdf.format(scheduledDateInServerTime).equals(sdf.format(conAudit.getScheduledDate()))) {
			if (isNeedsReschedulingFee() && !feeOverride) {
				// Create invoice
				String notes = "Fee for rescheduling " + getText(conAudit.getAuditType().getI18nKey("name"))
						+ " within 48 hours of original scheduled date "
						+ DateBean.format(conAudit.getScheduledDate(), "MMM dd, yyyy") + " to "
						+ DateBean.format(scheduledDateInServerTime, "MMM dd, yyyy");

				createInvoice(rescheduling, notes);
			}

			if (isNeedsExpediteFee(scheduledDateInServerTime))
				createExpediteInvoiceAndEmail(scheduledDateInServerTime);

			conAudit.setScheduledDate(scheduledDateInServerTime);
			conAudit.setContractorConfirm(null);
		}

		// When a new auditor gets selected, that auditor should get a
		// confirmation email
		if (permissions.getUserId() != conAudit.getAuditor().getId() || changedAuditor)
			conAudit.setAuditorConfirm(null);

		if (conAudit.getAuditorConfirm() == null || conAudit.getContractorConfirm() == null)
			sendConfirmationEmail(getText(conAudit.getAuditType().getI18nKey("name")) + " Re-scheduled for "
					+ DateBean.format(conAudit.getScheduledDate(), "MMMM d"));

		addActionMessage("Audit Saved Successfully");
		// check for a time overlap here
		List<ContractorAudit> conflicts = auditDao.findScheduledAudits(conAudit.getAuditor().getId(),
				DateBean.addField(conAudit.getScheduledDate(), Calendar.MINUTE, -120),
				DateBean.addField(conAudit.getScheduledDate(), Calendar.MINUTE, 120));
		if (conflicts.size() > 1) {
			addActionMessage(getText("ScheduleAudit.message.Overlap"));
			for (ContractorAudit cAudit : conflicts) {
				if (!cAudit.equals(conAudit)) {
					addActionMessage(cAudit.getContractorAccount().getName() + " at "
							+ formatDate(cAudit.getScheduledDate(), "h:mm a z"));
				}
			}
		}

		return setUrlForRedirect("ScheduleAudit!edit.action?auditID=" + conAudit.getId());
	}

	public String address() throws Exception {
		if (Strings.isEmpty(conAudit.getContractorContact()))
			addActionError(getText("ScheduleAudit.error.ContactNameRequired"));
		if (Strings.isEmpty(conAudit.getPhone2()))
			addActionError(getText("ScheduleAudit.error.EmailRequired"));
		if (Strings.isEmpty(conAudit.getPhone()))
			addActionError(getText("ScheduleAudit.error.PhoneRequired"));
		if (hasActionErrors()) {
			return "address";
		}
		auditDao.save(conAudit);
		findTimeslots();
		return "select";
	}

	public String changeSelectedTimeZone() throws Exception {
		findTimeslots();
		return "picker";
	}

	public String selectTime() {
		List<AuditorAvailability> timeslots = auditorAvailabilityDAO.findByTime(timeSelected);
		int maxRank = Integer.MIN_VALUE;
		for (AuditorAvailability timeslot : timeslots) {
			int rank = timeslot.rank(conAudit, permissions);
			if (rank > maxRank) {
				maxRank = rank;
				availabilitySelected = timeslot;
				// availabilitySelected.setTimezone(permissions.getTimezone());
				availabilitySelectedID = availabilitySelected.getId();
			}
		}
		if (availabilitySelectedID > 0) {
			conAudit.setConductedOnsite(availabilitySelected.isConductedOnsite(conAudit));
			conAudit.setNeedsCamera(true); // Assume yes until they say
											// otherwise
			return "confirm";
		}
		addActionError(getText("ScheduleAudit.error.FailedToSelectTime"));
		findTimeslots();
		return "select";
	}

	public String viewMoreTimes() {
		findTimeslots();
		return "picker";
	}

	private void findTimeslots() {
		List<AuditorAvailability> timeslots = null;

		timeslots = auditorAvailabilityDAO.findAvailable(availabilityStartDate, this.getContractor().getCountry()
				.getIsoCode());

		availableSet = new AvailableSet();
		for (AuditorAvailability timeslot : timeslots) {
			if (availableSet.size() >= 15 && !availableSet.contains(timeslot)) {
				break;
			}
			availableSet.add(timeslot);
		}

		return;
	}

	public String confirm() throws Exception {
		availabilitySelected = auditorAvailabilityDAO.find(availabilitySelectedID);

		if (availabilitySelected == null) {
			addActionError(getText("ScheduleAudit.message.TimeSlotNotAvailable"));
			return "select";
		}

		if (!readInstructions) {
			addActionError(getText("ScheduleAudit.message.AcknowledgeInstructions"));
			return "confirm";
		}

		if (!confirmed) {
			addActionError(getText("ScheduleAudit.message.AgreeToTerms"));
			return "confirm";
		}

		boolean needsExpediteFee = isNeedsExpediteFee(availabilitySelected.getStartDate());

		conAudit.setScheduledDate(availabilitySelected.getStartDate());
		conAudit.setAuditor(availabilitySelected.getUser());
		conAudit.setAssignedDate(new Date());
		conAudit.setClosingAuditor(new User(conAudit.getIndependentClosingAuditor(availabilitySelected.getUser())));
		if (permissions.isContractor())
			conAudit.setContractorConfirm(new Date());
		conAudit.setConductedOnsite(availabilitySelected.isConductedOnsite(conAudit));
		auditDao.save(conAudit);
		auditorAvailabilityDAO.remove(availabilitySelected);

		String shortScheduleDate = DateBean.format(conAudit.getScheduledDate(), "MMMM d");
		sendConfirmationEmail(conAudit.getAuditType().getName() + " Scheduled for " + shortScheduleDate);

		addActionMessage(getText("ScheduleAudit.message.AuditNowScheduled"));

		if (needsExpediteFee) {
			createExpediteInvoiceAndEmail(availabilitySelected.getStartDate());
		}

		return "summary";
	}

	public String ajaxScheduleAuditExpediteModal() {
		// TODO:
		if (!AjaxUtils.isAjax(ServletActionContext.getRequest())) {
			throw new RuntimeException("forward 404");
		}

		return "ScheduleAuditExpediteModal";
	}

	public void createExpediteInvoiceAndEmail(Date startTime) throws Exception {
		String notes = String.format("%s was scheduled within 10 business days, requiring an expedite fee.", conAudit
				.getAuditType().getName());

		createInvoice(expedite, notes);

		if (conAudit.isNeedsCamera()) {
			List<UserAccess> webcamUsers = uaDAO.findByOpPerm(OpPerms.WebcamNotification);
			List<String> emails = new ArrayList<String>();
			for (UserAccess ua : webcamUsers) {
				if (!ua.getUser().isGroup() && !Strings.isEmpty(ua.getUser().getEmail()))
					emails.add("\"" + ua.getUser().getName() + "\" <" + ua.getUser().getEmail() + ">");
			}

			// TODO We should pull this out into an email template.
			EmailQueue email = new EmailQueue();
			email.setSubject("Webcam needed for Rush " + conAudit.getAuditType().getName() + " for "
					+ conAudit.getContractorAccount().getName());
			email.setBody(conAudit.getContractorContact() + " from " + conAudit.getContractorAccount().getName()
					+ " has requested a Rush " + getText(conAudit.getAuditType().getI18nKey("name")) + " on "
					+ DateBean.format(startTime, "MMM dd h:mm a, z")
					+ " and requires a webcam to be sent overnight.\n\nThank you,\nPICS");
			email.setToAddresses(Strings.implode(emails));
			email.setFromAddress(EmailAddressUtils.PICS_AUDIT_EMAIL_ADDRESS_WITH_NAME);
			email.setViewableById(Account.PicsID);
			emailSender.send(email);
		}
	}

	public class AvailableSet {
		public final Map<Date, List<AuditorAvailability>> days = new TreeMap<Date, List<AuditorAvailability>>();
		private Date latest;

		int size() {
			return days.size();
		}

		void add(AuditorAvailability timeslot) {
			final Date day = stripTimes(timeslot.getStartDate());
			if (days.get(day) == null)
				days.put(day, new ArrayList<AuditorAvailability>());

			for (AuditorAvailability existingTimeSlot : days.get(day)) {
				if (isSameTime(existingTimeSlot.getStartDate(), timeslot.getStartDate()))
					// We don't need to add more than one time slot per
					// starting time
					return;
			}
			days.get(day).add(timeslot);
			latest = DateBean.getLatestDate(latest, timeslot.getEndDate());
		}

		@SuppressWarnings("deprecation")
		private boolean isSameTime(Date time1, Date time2) {
			if (time1.getHours() != time2.getHours())
				return false;
			if (time1.getMinutes() != time2.getMinutes())
				return false;
			if (time1.getSeconds() != time2.getSeconds())
				return false;
			return true;
		}

		boolean contains(AuditorAvailability timeslot) {
			final Date day = stripTimes(timeslot.getStartDate());
			return days.get(day) != null;
		}

		private Date stripTimes(Date value) {
			final Calendar cal = Calendar.getInstance();
			cal.setTime(value);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			return cal.getTime();
		}

		public Date getLatest() {
			if (latest == null)
				return null;
			return DateBean.getNextDayMidnight(latest);
		}
	}

	public static Date changeTimeZone(TimeZone inputTimeZone, TimeZone outputTimeZone, String inputDateString,
			String inputFormat) throws Exception {
		if (inputFormat == null)
			inputFormat = "yyyy-MM-dd HH:mm:ss";

		Date inputDate = null;

		DateFormat inputDateFormat = new SimpleDateFormat(inputFormat);
		inputDateFormat.setTimeZone(inputTimeZone);

		inputDate = inputDateFormat.parse(inputDateString);

		DateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
		outputDateFormat.setTimeZone(outputTimeZone);

		return outputDateFormat.parse(outputDateFormat.format(inputDate));
	}

	public AuditorAvailability getAvailabilitySelected() {
		return availabilitySelected;
	}

	public AvailableSet getAvailableSet() {
		return availableSet;
	}

	public void setAvailableSet(AvailableSet availableSet) {
		this.availableSet = availableSet;
	}

	public int getAvailabilitySelectedID() {
		return availabilitySelectedID;
	}

	public void setAvailabilitySelectedID(int availabilitySelectedID) {
		this.availabilitySelectedID = availabilitySelectedID;
	}

	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}

	public boolean isReadInstructions() {
		return readInstructions;
	}

	public void setReadInstructions(boolean readInstructions) {
		this.readInstructions = readInstructions;
	}

	public boolean isFeeOverride() {
		return feeOverride;
	}

	public void setFeeOverride(boolean feeOverride) {
		this.feeOverride = feeOverride;
	}

	public void setScheduledDateDay(String scheduledDateDay) {
		this.scheduledDateDay = scheduledDateDay;
	}

	public void setScheduledDateTime(String scheduledDateTime) {
		this.scheduledDateTime = scheduledDateTime;
	}

	public InvoiceFee getRescheduling() {
		return rescheduling;
	}

	public void setRescheduling(InvoiceFee rescheduling) {
		this.rescheduling = rescheduling;
	}

	public InvoiceFee getExpedite() {
		return expedite;
	}

	public void setExpedite(InvoiceFee expedite) {
		this.expedite = expedite;
	}

	public Date getLastCancellationTime() {
		Calendar cal = Calendar.getInstance();
		if (availabilitySelected != null)
			cal.setTime(availabilitySelected.getStartDate());
		else if (conAudit.getScheduledDate() != null)
			cal.setTime(conAudit.getScheduledDate());
		else
			// Something is probably wrong here
			return cal.getTime();

		cal.add(Calendar.DAY_OF_YEAR, -2);

		while (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
			cal.add(Calendar.DAY_OF_YEAR, -1);
		}
		return cal.getTime();
	}

	public void setTimeSelected(String dateString) throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat();
		df.setLenient(false);
		df.applyPattern(PicsDateFormat.ScheduleAudit);
		this.timeSelected = df.parse(dateString);
	}

	public void setAvailabilityStartDate(Date availabilityStartDate) {
		this.availabilityStartDate = availabilityStartDate;
	}

	public void setAuditor(User auditor) {
		this.auditor = auditor;
	}

	public User getAuditor() {
		return auditor;
	}

	public boolean isNeedsReschedulingFee() {
		if (conAudit.getScheduledDate() != null) {
			Date now = new Date();
			long diff = conAudit.getScheduledDate().getTime() - now.getTime();

			return diff < (3600 * 48 * 1000);
		}

		return false;
	}

	public boolean isNeedsExpediteFee(Date selectedDate) {
		if (selectedDate != null) {
			if (conAudit.getScheduledDate() != null && selectedDate.after(conAudit.getScheduledDate()))
				return false;

			LocalDate selectedDateJoda = new LocalDate(selectedDate);
			Days days = Days.daysBetween(new LocalDate(), selectedDateJoda);
			return (days.getDays() < 14);
		}

		return false;
	}

	private void createInvoice(InvoiceFee fee, String notes) throws Exception {
		Invoice invoice = new Invoice();
		invoice.setAccount(contractor);
		invoice.setCurrency(contractor.getCountry().getCurrency());
		invoice.setDueDate(new Date());
		invoice.setTotalAmount(contractor.getCountry().getAmount(fee));
		invoice.setNotes(notes);
		invoice.setAuditColumns(permissions);
		invoice.setQbSync(true);
		invoice = invoiceService.saveInvoice(invoice);

		InvoiceItem item = new InvoiceItem();
		item.setAmount(contractor.getCountry().getAmount(fee));
		item.setInvoice(invoice);
		item.setInvoiceFee(fee);
		item.setAuditColumns(permissions);
		item = itemDAO.save(item);

		invoice.getItems().add(item);
		contractor.getInvoices().add(invoice);
		contractor.syncBalance();
		contractorAccountDao.save(contractor);

		addNote(contractor, notes, NoteCategory.Audits, getViewableByAccount(conAudit.getAuditType().getAccount()));
	}

	private void sendConfirmationEmail(String summary) throws Exception {
		String serverName = getRequestURL().replace(ActionContext.getContext().getName() + ".action", "");

		if (conAudit.getContractorConfirm() == null) {
			EmailBuilder emailBuilder = new EmailBuilder();
			emailBuilder.setPermissions(permissions);
			emailBuilder.setConAudit(conAudit);
			emailBuilder.setTemplate(15);
			ContractorAccount contractor = conAudit.getContractorAccount();
			emailBuilder.setUser((contractor.getPrimaryContact() != null) ? contractor.getPrimaryContact() : conAudit
					.getContractorAccount().getUsers().get(0));

			String seed = "c" + conAudit.getContractorAccount().getId() + "id" + conAudit.getId();
			String confirmLink = serverName + "ScheduleAuditUpdate.action?type=c&contractorAudit=" + conAudit.getId()
					+ "&key=" + Strings.hashUrlSafe(seed);
			emailBuilder.addToken("confirmLink", confirmLink);
			emailBuilder.setFromAddress(EmailAddressUtils.PICS_AUDIT_EMAIL_ADDRESS_WITH_NAME);
			EmailQueue email = emailBuilder.build();
			email.setViewableById(getViewableByAccount(conAudit.getAuditType().getAccount()));
			emailSender.send(email);
		}
		
		if (conAudit.getAuditorConfirm() == null) {
			EmailBuilder emailBuilder = new EmailBuilder();
			emailBuilder.setPermissions(permissions);
			emailBuilder.setConAudit(conAudit);
			emailBuilder.setTemplate(14);

			String seed = "a" + conAudit.getAuditor().getId() + "id" + conAudit.getId();
			String confirmLink = serverName + "ScheduleAuditUpdate.action?type=a&contractorAudit=" + conAudit.getId()
					+ "&key=" + Strings.hashUrlSafe(seed);
			emailBuilder.addToken("confirmLink", confirmLink);
			emailBuilder.setUser(conAudit.getAuditor());
			emailBuilder.setFromAddress(EmailAddressUtils.PICS_AUDIT_EMAIL_ADDRESS_WITH_NAME);
			EmailQueue email = emailBuilder.build();
			email.setViewableById(Account.PicsID);
			emailSender.send(email);
		}

		addNote(contractor, summary, NoteCategory.Audits, getViewableByAccount(conAudit.getAuditType().getAccount()));
	}

	@Override
	public Set<User> getAuditorList() {
		// This page we only want to pull up auditors and not CSRs.
		if (auditorList == null) {
			auditorList = new TreeSet<User>();
			UserDAO dao = (UserDAO) SpringUtils.getBean("UserDAO");
			auditorList.addAll(dao.findByGroup(User.GROUP_AUDITOR));
		}
		return auditorList;
	}

	public TimeZone getSelectedTimeZone() {
		return selectedTimeZone;
	}

	public void setSelectedTimeZone(TimeZone selectedTimeZone) {
		this.selectedTimeZone = selectedTimeZone;
	}

	public String setUrlForRedirect(String url) throws IOException {
		ServletActionContext.getResponse().sendRedirect(url);
		return BLANK;
	}

	public String cancelAudit() {
		conAudit.setLatitude(0);
		conAudit.setLongitude(0);
		conAudit.setScheduledDate(null);
		auditDao.save(conAudit);

		String noteSummary = permissions.getName() + " canceled the " + conAudit.getAuditType().getName();
		addNote(contractor, noteSummary, NoteCategory.Audits,
				getViewableByAccount(conAudit.getAuditType().getAccount()));

		return "edit";
	}
}
