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
import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditorAvailabilityDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditorAvailability;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ScheduleAudit extends AuditActionSupport implements Preparable {

	static final public String GOOGLE_API_KEY = "ABQIAAAAgozVvI8r_S5nN6njMJJ7aBTo4w3vXkjMqCEUz4-xpKEfhElFUxRwXE2trWXRBXZPHCY8N1AgoRkSBw";
	static final public String DATE_FORMAT = "yyyyMMddHHmm";

	private AvailableSet availableSet = new AvailableSet();
	private Date timeSelected;
	private AuditorAvailability availabilitySelected = null;
	private int availabilitySelectedID;
	private boolean confirmed = false;

	private String scheduledDateDay;
	private String scheduledDateTime;
	private Date availabilityStartDate = new Date();

	private AuditorAvailabilityDAO auditorAvailabilityDAO;

	private User auditor = null;

	public ScheduleAudit(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, AuditCategoryDataDAO catDataDao,
			AuditDataDAO auditDataDao, AuditorAvailabilityDAO auditorAvailabilityDAO) {
		super(accountDao, auditDao, catDataDao, auditDataDao);
		this.auditorAvailabilityDAO = auditorAvailabilityDAO;
		this.subHeading = "Schedule Audit";
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

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		if (conAudit == null) {
			addActionError("Missing auditID");
			return BLANK;
		}

		subHeading = "Schedule " + conAudit.getAuditType().getAuditName();

		if (!conAudit.getAuditStatus().isPending())
			// This audit has already been completed. No reason to schedule it
			// now
			return "summary";

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

		if (button.equals("Save")) {
			if (!permissions.isAdmin())
				throw new NoRightsException("ScheduleAudits");

			Date scheduledDateInUserTime = DateBean.parseDateTime(scheduledDateDay + " " + scheduledDateTime);
			if (scheduledDateInUserTime == null) {
				addActionError(scheduledDateTime + " is not a valid time");
				return "edit";
			}
			Date scheduledDateInServerTime = DateBean.convertTime(scheduledDateInUserTime, permissions.getTimezone());
			conAudit.setScheduledDate(scheduledDateInServerTime);
			if (auditor != null)
				conAudit.setAuditor(auditor);
			addActionMessage("Audit Saved Successfully");
			// check for a time overlap here
			List<ContractorAudit> conflicts = auditDao.findScheduledAudits(conAudit.getAuditor().getId(), DateBean
					.addField(conAudit.getScheduledDate(), Calendar.MINUTE, -120), DateBean.addField(conAudit
					.getScheduledDate(), Calendar.MINUTE, 120));
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

		if (button.equals("address")) {
			List<String> errors = new ArrayList<String>();
			if (Strings.isEmpty(conAudit.getContractorContact()))
				errors.add("Contact Name is a required field");
			if (Strings.isEmpty(conAudit.getPhone2()))
				errors.add("Email is a required field");
			if (Strings.isEmpty(conAudit.getPhone()))
				errors.add("Phone Number is a required field");
			if (errors.size() > 0) {
				addActionError("The following errors exist:");
				for (String e : errors)
					addActionError(e);
				return "address";
			}
			auditDao.save(conAudit);
			findTimeslots();
			return "select";
		}
		if (button.equals("select")) {
			List<AuditorAvailability> timeslots = auditorAvailabilityDAO.findByTime(timeSelected);
			int maxRank = -999;
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
		if (button.equals("confirm")) {
			availabilitySelected = auditorAvailabilityDAO.find(availabilitySelectedID);

			if (availabilitySelected == null) {
				addActionError("The time slot you selected is no longer available. Please choose a different time.");
				return "select";
			}

			if (!confirmed) {
				addActionError("You must agree to the terms by checking the box below.");
				return "confirm";
			}

			conAudit.setScheduledDate(availabilitySelected.getStartDate());
			conAudit.setAuditor(availabilitySelected.getUser());
			conAudit.setContractorConfirm(new Date());
			conAudit.setConductedOnsite(availabilitySelected.isConductedOnsite(conAudit));
			auditDao.save(conAudit);
			auditorAvailabilityDAO.remove(availabilitySelected);

			String serverName = getRequestURL().replace(ActionContext.getContext().getName() + ".action", "");

			if (conAudit.getContractorConfirm() == null) {
				EmailBuilder emailBuilder = new EmailBuilder();
				emailBuilder.setPermissions(permissions);
				emailBuilder.setConAudit(conAudit);
				emailBuilder.setTemplate(15);
				emailBuilder.setUser(conAudit.getContractorAccount().getUsers().get(0));

				String seed = "c" + conAudit.getContractorAccount().getId() + "id" + conAudit.getId();
				String confirmLink = serverName + "ScheduleAuditUpdate.action?type=c&auditID=" + conAudit.getId()
						+ "&key=" + Strings.hashUrlSafe(seed);
				emailBuilder.addToken("confirmLink", confirmLink);

				EmailQueue email = emailBuilder.build();
				EmailSender.send(email);
			}
			if (conAudit.getAuditorConfirm() == null) {
				EmailBuilder emailBuilder = new EmailBuilder();
				emailBuilder.setPermissions(permissions);
				emailBuilder.setConAudit(conAudit);
				emailBuilder.setTemplate(14);

				String seed = "a" + conAudit.getAuditor().getId() + "id" + conAudit.getId();
				String confirmLink = serverName + "ScheduleAuditUpdate.action?type=a&auditID=" + conAudit.getId()
						+ "&key=" + Strings.hashUrlSafe(seed);
				emailBuilder.addToken("confirmLink", confirmLink);
				emailBuilder.setUser(conAudit.getAuditor());

				EmailQueue email = emailBuilder.build();
				email.setCcAddresses(null);
				EmailSender.send(email);
			}

			String shortScheduleDate = DateBean.format(conAudit.getScheduledDate(), "MMMM d");
			addNote(contractor, conAudit.getAuditType().getAuditName() + " Scheduled for " + shortScheduleDate,
					NoteCategory.Audits);

			addActionMessage("Congratulations, your audit is now scheduled. You should receive a confirmation email for your records.");
			return "summary";
		}
		return "address";
	}

	private void findTimeslots() {
		List<AuditorAvailability> timeslots = auditorAvailabilityDAO.findAvailable(availabilityStartDate);
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

	public void setScheduledDateDay(String scheduledDateDay) {
		this.scheduledDateDay = scheduledDateDay;
	}

	public void setScheduledDateTime(String scheduledDateTime) {
		this.scheduledDateTime = scheduledDateTime;
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

}
