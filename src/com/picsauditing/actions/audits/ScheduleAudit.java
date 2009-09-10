package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditorAvailabilityDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditorAvailability;

@SuppressWarnings("serial")
public class ScheduleAudit extends AuditActionSupport implements Preparable {

	private NextAvailable nextAvailable = new NextAvailable();
	private Date timeSelected;
	private AuditorAvailability availabilitySelected;
	private int availabilitySelectedID;
	private boolean confirmed = false;

	private AuditorAvailabilityDAO auditorAvailabilityDAO;

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
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		subHeading = "Schedule " + conAudit.getAuditType().getAuditName();

		if (button == null)
			return "address";

		if (button.equals("address")) {
			auditDao.save(conAudit);
			List<AuditorAvailability> timeslots = auditorAvailabilityDAO.findAvailable();
			for (AuditorAvailability timeslot : timeslots) {
				if (timeslot.isOkFor(conAudit)) {
					nextAvailable.add(timeslot);
					if (nextAvailable.rows.size() > 3) {
						nextAvailable.rows.remove(3);
						return "select";
					}
				}
			}
			return "select";
		}
		if (button.equals("select")) {
			List<AuditorAvailability> timeslots = auditorAvailabilityDAO.findByTime(timeSelected);
			for (AuditorAvailability timeslot : timeslots) {
				availabilitySelected = timeslot;
				availabilitySelectedID = availabilitySelected.getId();
			}
			if (availabilitySelectedID > 0)
				return "confirm";
			addActionError("Failed to select time");
			return "select";
		}
		if (button.equals("confirm")) {
			availabilitySelected = auditorAvailabilityDAO.find(availabilitySelectedID);

			conAudit.setScheduledDate(availabilitySelected.getStartDate());
			conAudit.setAuditor(availabilitySelected.getUser());
			conAudit.setContractorConfirm(new Date());
			conAudit.setConductedOnsite(availabilitySelected.isConductedOnsite(conAudit));
			auditDao.save(conAudit);
			auditorAvailabilityDAO.remove(availabilitySelected);
		}
		return SUCCESS;
	}

	public NextAvailable getNextAvailable() {
		return nextAvailable;
	}

	public AuditorAvailability getAvailabilitySelected() {
		return availabilitySelected;
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

	public Date getLastCancellationTime() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(availabilitySelected.getStartDate());
		cal.add(Calendar.DAY_OF_YEAR, -2);

		while (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
			cal.add(Calendar.DAY_OF_YEAR, -1);
		}
		return cal.getTime();
	}

	public void setTimeSelected(String timeSelected) {
		this.timeSelected = DateBean.parseDateTime(timeSelected);
	}

	public class NextAvailable {
		public final List<AvailableSet> rows = new ArrayList<AvailableSet>();
		private final int MAX_COLUMNS = 4;

		void add(AuditorAvailability timeslot) {
			for (AvailableSet row : rows) {
				if (row.contains(timeslot)) {
					row.add(timeslot);
					return;
				}
				if (row.size() < MAX_COLUMNS) {
					row.add(timeslot);
					return;
				}

			}
			rows.add(new AvailableSet(timeslot));
		}

		public class AvailableSet {
			public final Map<Date, List<AuditorAvailability>> days = new TreeMap<Date, List<AuditorAvailability>>();

			AvailableSet(AuditorAvailability starter) {
				add(starter);
			}

			int size() {
				return days.size();
			}

			void add(AuditorAvailability timeslot) {
				final Date day = stripTimes(timeslot.getStartDate());
				if (days.get(day) == null)
					days.put(day, new ArrayList<AuditorAvailability>());

				for (AuditorAvailability existingTimeSlot : days.get(day)) {
					if (isSameTime(existingTimeSlot.getStartDate(), timeslot.getStartDate()))
						// We don't need to add more than one time slot per starting time
						return;
				}
				days.get(day).add(timeslot);
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
				cal.set(Calendar.HOUR, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				return cal.getTime();
			}

		}
	}
}
