package com.picsauditing.actions.audits;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.AuditCategoryRuleCache;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditorAvailabilityDAO;
import com.picsauditing.dao.CertificateDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.dao.InvoiceItemDAO;
import com.picsauditing.dao.UserAccessDAO;
import com.picsauditing.dao.UserAssignmentDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditorAvailability;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.InvoiceItem;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserAccess;
import com.picsauditing.jpa.entities.UserAssignment;
import com.picsauditing.jpa.entities.UserAssignmentType;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ScheduleAudit extends AuditActionSupport implements Preparable {

	static final public String GOOGLE_API_KEY = "ABQIAAAAgozVvI8r_S5nN6njMJJ7aBTTvY0m40rW8_sKxH-4kQuUdYdvuxQdivgdKinXBN5YPCA6h_z5hoeBaA";
	static final public String DATE_FORMAT = "yyyyMMddHHmm";

	private AvailableSet availableSet = new AvailableSet();
	private Date timeSelected;
	private AuditorAvailability availabilitySelected = null;
	private int availabilitySelectedID;
	private boolean confirmed = false;
	private boolean feeOverride = false;

	private String scheduledDateDay;
	private String scheduledDateTime;
	private Date availabilityStartDate = new Date();

	private AuditorAvailabilityDAO auditorAvailabilityDAO;
	private InvoiceDAO invoiceDAO;
	private InvoiceFeeDAO feeDAO;
	private InvoiceItemDAO itemDAO;
	private UserAccessDAO uaDAO;
	private UserAssignmentDAO userAssignmentDAO;

	private User auditor = null;
	private InvoiceFee rescheduling;
	private InvoiceFee expedite;

	public ScheduleAudit(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, AuditCategoryDataDAO catDataDao,
			AuditDataDAO auditDataDao, CertificateDAO certificateDao, AuditorAvailabilityDAO auditorAvailabilityDAO,
			AuditCategoryRuleCache auditCategoryRuleCache, InvoiceDAO invoiceDAO, InvoiceFeeDAO feeDAO,
			InvoiceItemDAO itemDAO, UserAccessDAO uaDAO, UserAssignmentDAO userAssignmentDAO) {
		super(accountDao, auditDao, catDataDao, auditDataDao, certificateDao, auditCategoryRuleCache);
		this.auditorAvailabilityDAO = auditorAvailabilityDAO;
		this.subHeading = "Schedule Audit";

		this.invoiceDAO = invoiceDAO;
		this.feeDAO = feeDAO;
		this.itemDAO = itemDAO;
		this.uaDAO = uaDAO;
		this.userAssignmentDAO = userAssignmentDAO;

		rescheduling = feeDAO.find(InvoiceFee.RESCHEDULING);
		expedite = feeDAO.find(InvoiceFee.EXPEDITE);
	}

	public void prepare() throws Exception {
		this.auditID = getParameter("auditID");
		if (auditID > 0) {
			loadPermissions();
			findConAudit();
		}
		int auditorID = getParameter("auditor.id");
		if (auditorID > 0)
			auditor = getUser(auditorID);
	}

	public String edit() throws Exception {
		if (conAudit.getScheduledDate() != null && conAudit.getScheduledDate().before(new Date()))
			addActionMessage("This audit's scheduled appointment has already passed. ");
		for (ContractorAuditOperator cao : conAudit.getOperators()) {
			if (cao.getStatus().after(AuditStatus.Pending)) {
				addActionMessage("This has already been completed. It does not need to be rescheduled.");
				return "edit";
			}
		}

		return "edit";
	}

	public String save() throws Exception {
		if (!permissions.isAdmin())
			throw new NoRightsException("ScheduleAudits");

		if(auditor==null){
			addActionError("You must select an auditor when scheduling an audit");
			return "edit";
		}
		
		conAudit.setAuditor(auditor);
		conAudit.setClosingAuditor(new User(conAudit.getIndependentClosingAuditor(auditor)));
		
		Date scheduledDateInUserTime = DateBean.parseDateTime(scheduledDateDay + " " + scheduledDateTime);
		if (scheduledDateInUserTime == null) {
			addActionError(scheduledDateTime + " is not a valid time");
			return "edit";
		}
		Date scheduledDateInServerTime = DateBean.convertTime(scheduledDateInUserTime, permissions.getTimezone());

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss z");
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

			conAudit.setScheduledDate(scheduledDateInServerTime);
			conAudit.setContractorConfirm(null);
			if (permissions.getUserId() != conAudit.getAuditor().getId())
				conAudit.setAuditorConfirm(null);
			String shortScheduleDate = DateBean.format(conAudit.getScheduledDate(), "MMMM d");
			sendConfirmationEmail(getText(conAudit.getAuditType().getI18nKey("name")) + " Re-scheduled for "
					+ shortScheduleDate);
		}

		addActionMessage("Audit Saved Successfully");
		// check for a time overlap here
		List<ContractorAudit> conflicts = auditDao.findScheduledAudits(conAudit.getAuditor().getId(),
				DateBean.addField(conAudit.getScheduledDate(), Calendar.MINUTE, -120),
				DateBean.addField(conAudit.getScheduledDate(), Calendar.MINUTE, 120));
		if (conflicts.size() > 1) {
			addActionMessage("This audit may overlap with the following audits:");
			for (ContractorAudit cAudit : conflicts) {
				if (!cAudit.equals(conAudit)) {
					addActionMessage(cAudit.getContractorAccount().getName() + " at "
							+ formatDate(cAudit.getScheduledDate(), "h:mm a z"));
				}
			}
		}

		return "edit";
	}

	public String address() throws Exception {
		if (Strings.isEmpty(conAudit.getContractorContact()))
			addActionError("Contact Name is a required field");
		if (Strings.isEmpty(conAudit.getPhone2()))
			addActionError("Email is a required field");
		if (Strings.isEmpty(conAudit.getPhone()))
			addActionError("Phone Number is a required field");
		if (hasActionErrors()) {
			return "address";
		}
		auditDao.save(conAudit);
		findTimeslots();
		return "select";
	}

	public String select() throws Exception {
		// Look up if there are multiple auditors for this area
		List<UserAssignment> assignments = userAssignmentDAO.findList(conAudit, UserAssignmentType.Auditor,
				conAudit.getAuditType());
		List<User> auditors = new ArrayList<User>();
		if (assignments.size() > 0) {
			for (UserAssignment ua : assignments)
				auditors.add(ua.getUser());
		}

		List<AuditorAvailability> timeslots = auditorAvailabilityDAO.findByTime(timeSelected);
		int maxRank = -999;
		for (AuditorAvailability timeslot : timeslots) {
			int rank = timeslot.rank(conAudit, permissions, auditors);
			if (rank > maxRank) {
				maxRank = rank;
				availabilitySelected = timeslot;
				availabilitySelectedID = availabilitySelected.getId();
			}
		}
		if (availabilitySelectedID > 0) {
			conAudit.setConductedOnsite(availabilitySelected.isConductedOnsite(conAudit));
			conAudit.setNeedsCamera(true); // Assume yes until they say
			// otherwise
			return "confirm";
		}
		addActionError("Failed to select time");
		findTimeslots();
		return "select";
	}

	public String confirm() throws Exception {
		availabilitySelected = auditorAvailabilityDAO.find(availabilitySelectedID);

		if (availabilitySelected == null) {
			addActionError(getText(getScope() + ".message.TimeSlotNotAvailable"));
			return "select";
		}

		if (!confirmed) {
			addActionError(getText(getScope() + ".message.AgreeToTerms"));
			return "confirm";
		}

		boolean needsExpediteFee = isNeedsExpediteFee(availabilitySelected.getStartDate());

		conAudit.setScheduledDate(availabilitySelected.getStartDate());
		conAudit.setAuditor(availabilitySelected.getUser());
		conAudit.setClosingAuditor(new User(conAudit.getIndependentClosingAuditor(availabilitySelected.getUser())));
		if (permissions.isContractor())
			conAudit.setContractorConfirm(new Date());
		conAudit.setConductedOnsite(availabilitySelected.isConductedOnsite(conAudit));
		auditDao.save(conAudit);
		auditorAvailabilityDAO.remove(availabilitySelected);

		String shortScheduleDate = DateBean.format(conAudit.getScheduledDate(), "MMMM d");
		sendConfirmationEmail(getText(conAudit.getAuditType().getI18nKey("name")) + " Scheduled for "
				+ shortScheduleDate);

		addActionMessage(getText(getScope() + ".message.AuditNowScheduled"));

		if (needsExpediteFee) {
			String notes = getText(conAudit.getAuditType().getI18nKey("name"))
					+ " was scheduled within 7 business days, requiring an expedite fee.";

			createInvoice(expedite, notes);

			if (conAudit.isNeedsCamera()) {
				List<UserAccess> webcamUsers = uaDAO.findByOpPerm(OpPerms.WebcamNotification);
				List<String> emails = new ArrayList<String>();
				for (UserAccess ua : webcamUsers) {
					if (!ua.getUser().isGroup() && !Strings.isEmpty(ua.getUser().getEmail()))
						emails.add("\"" + ua.getUser().getName() + "\" <" + ua.getUser().getEmail() + ">");
				}

				EmailQueue email = new EmailQueue();
				email.setSubject("Webcam needed for Rush " + getText(conAudit.getAuditType().getI18nKey("name"))
						+ " for " + conAudit.getContractorAccount().getName());
				email.setBody(conAudit.getContractorContact() + " from " + conAudit.getContractorAccount().getName()
						+ " has requested a Rush " + getText(conAudit.getAuditType().getI18nKey("name")) + " on "
						+ DateBean.format(availabilitySelected.getStartDate(), "MMM dd h:mm a, z")
						+ " and requires a webcam to be sent overnight.\n\nThank you,\nPICS");
				email.setToAddresses(Strings.implode(emails));
				email.setFromAddress("\"PICS Auditing\"<audits@picsauditing.com>");
				email.setViewableById(Account.PicsID);
				EmailSender.send(email);
			}
		}

		return "summary";
	}
	
	public String viewMoreTimes(){
		findTimeslots();
		return "select";
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
				addActionMessage("This audit's scheduled appointment has already passed. ");
				return "address";
			}

			if (permissions.isAdmin())
				// Let the admin reschedule the audit
				return "edit";

			// Contractors can't change upcoming scheduled audits
			return "summary";
		}

		if (button.equals("select")) {
			List<AuditorAvailability> timeslots = auditorAvailabilityDAO.findByTime(timeSelected);
			int maxRank = Integer.MIN_VALUE;
			for (AuditorAvailability timeslot : timeslots) {
				int rank = timeslot.rank(conAudit, permissions);
				if (rank > maxRank) {
					maxRank = rank;
					availabilitySelected = timeslot;
					availabilitySelectedID = availabilitySelected.getId();
				}
			}
			if (availabilitySelectedID > 0) {
				conAudit.setConductedOnsite(availabilitySelected.isConductedOnsite(conAudit));
				conAudit.setNeedsCamera(true); // Assume yes until they say
				// otherwise
				return "confirm";
			}
			addActionError("Failed to select time");
			findTimeslots();
			return "select";
		}

		return "address";
	}

	private void findTimeslots() {
		List<AuditorAvailability> timeslots = null;

		List<UserAssignment> assignments = userAssignmentDAO.findList(conAudit, UserAssignmentType.Auditor,
				conAudit.getAuditType());

		if (conAudit.getAuditor() != null) {
			// There's all ready an auditor set
			timeslots = auditorAvailabilityDAO.findByAuditorID(conAudit.getAuditor().getId());
		} else if (assignments.size() > 0) {
			// There are assignments for the contractor's location
			if (assignments.size() > 1) {
				// There are multiple assignments, select the schedule for all
				// the available auditors
				List<User> auditors = new ArrayList<User>();
				for (UserAssignment ua : assignments)
					auditors.add(ua.getUser());

				timeslots = auditorAvailabilityDAO.findAvailableLocal(availabilityStartDate, auditors);
			} else
				// Just select the auditor's schedule
				timeslots = auditorAvailabilityDAO.findByAuditorID(assignments.get(0).getUser().getId());
		} else
			// Find the schedule for all auditors
			timeslots = auditorAvailabilityDAO.findAvailable(availabilityStartDate);

		for (AuditorAvailability timeslot : timeslots) {
			if (timeslot.isConductedOnsite(conAudit)) {
				if (availableSet.size() >= 8 && !availableSet.contains(timeslot)) {
					break;
				}
				availableSet.add(timeslot);
			}
		}

		if (availableSet.size() > 0) {
			return;
		}

		availableSet = new AvailableSet();
		for (AuditorAvailability timeslot : timeslots) {
			if (availableSet.size() >= 8 && !availableSet.contains(timeslot)) {
				break;
			}
			availableSet.add(timeslot);
		}

		return;
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
		df.applyPattern(DATE_FORMAT);
		this.timeSelected = df.parse(dateString);
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

	public boolean isNeedsExpediteFee(Date date) {
		if (date != null) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);

			Calendar compare = Calendar.getInstance();
			compare.setTime(date);
			compare.set(Calendar.HOUR_OF_DAY, 0);
			compare.set(Calendar.MINUTE, 0);
			compare.set(Calendar.SECOND, 0);

			cal.set(Calendar.ZONE_OFFSET, compare.get(Calendar.ZONE_OFFSET));
			return compare.getTime().after(cal.getTime()) && DateBean.getDateDifference(compare.getTime()) < 14;
		}

		return false;
	}

	private void createInvoice(InvoiceFee fee, String notes) throws Exception {
		Invoice invoice = new Invoice();
		invoice.setAccount(contractor);
		invoice.setCurrency(contractor.getCurrency());
		invoice.setDueDate(new Date());
		invoice.setTotalAmount(fee.getAmount());
		invoice.setNotes(notes + " Thank you for doing business with PICS!");
		invoice.setAuditColumns(permissions);
		invoice.setQbSync(true);
		invoice = invoiceDAO.save(invoice);

		InvoiceItem item = new InvoiceItem();
		item.setAmount(fee.getAmount());
		item.setInvoice(invoice);
		item.setInvoiceFee(fee);
		item.setAuditColumns(permissions);
		item = itemDAO.save(item);

		invoice.getItems().add(item);
		contractor.getInvoices().add(invoice);
		contractor.syncBalance();
		accountDao.save(contractor);

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
			String confirmLink = serverName + "ScheduleAuditUpdate.action?type=c&contractorAudit=" + conAudit.getId() + "&key="
					+ Strings.hashUrlSafe(seed);
			emailBuilder.addToken("confirmLink", confirmLink);
			emailBuilder.setFromAddress("\"PICS Auditing\"<audits@picsauditing.com>");
			EmailQueue email = emailBuilder.build();
			email.setViewableById(getViewableByAccount(conAudit.getAuditType().getAccount()));
			EmailSender.send(email);
		}
		if (conAudit.getAuditorConfirm() == null) {
			EmailBuilder emailBuilder = new EmailBuilder();
			emailBuilder.setPermissions(permissions);
			emailBuilder.setConAudit(conAudit);
			emailBuilder.setTemplate(14);

			String seed = "a" + conAudit.getAuditor().getId() + "id" + conAudit.getId();
			String confirmLink = serverName + "ScheduleAuditUpdate.action?type=a&contractorAudit=" + conAudit.getId() + "&key="
					+ Strings.hashUrlSafe(seed);
			emailBuilder.addToken("confirmLink", confirmLink);
			emailBuilder.setUser(conAudit.getAuditor());
			emailBuilder.setFromAddress("\"Jesse Cota\"<jcota@picsauditing.com>");
			EmailQueue email = emailBuilder.build();
			email.setCcAddresses(null);
			email.setViewableById(Account.PicsID);
			EmailSender.send(email);
		}

		addNote(contractor, summary, NoteCategory.Audits, getViewableByAccount(conAudit.getAuditType().getAccount()));
	}
}
