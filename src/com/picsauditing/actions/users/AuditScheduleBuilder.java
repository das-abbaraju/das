package com.picsauditing.actions.users;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.AuditorAvailabilityDAO;
import com.picsauditing.dao.AuditorScheduleDAO;
import com.picsauditing.dao.AuditorVacationDAO;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.jpa.entities.AuditorAvailability;
import com.picsauditing.jpa.entities.AuditorSchedule;
import com.picsauditing.jpa.entities.AuditorVacation;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.log.PicsLogger;

@SuppressWarnings("serial")
public class AuditScheduleBuilder extends PicsActionSupport {
	private Map<User, Auditor> auditors = new HashMap<User, Auditor>();
	private List<AuditorVacation> holidays = new ArrayList<AuditorVacation>();

	private AuditorAvailabilityDAO auditorAvailabilityDAO;
	private AuditorScheduleDAO auditorScheduleDAO;
	private AuditorVacationDAO auditorVacationDAO;
	private AppPropertyDAO appPropertyDAO;

	public AuditScheduleBuilder(AuditorAvailabilityDAO auditorAvailabilityDAO, AuditorScheduleDAO auditorScheduleDAO,
			AuditorVacationDAO auditorVacationDAO, AppPropertyDAO appPropertyDAO) {
		this.auditorAvailabilityDAO = auditorAvailabilityDAO;
		this.auditorScheduleDAO = auditorScheduleDAO;
		this.auditorVacationDAO = auditorVacationDAO;
		this.appPropertyDAO = appPropertyDAO;
	}

	public String execute() throws Exception {
		PicsLogger.start("AuditScheduleBuilder", true);

		PicsLogger.log("Get all my data into RAM");
		List<AuditorSchedule> schedules = auditorScheduleDAO.findAll();
		for (AuditorSchedule schedule : schedules) {
			getAuditor(schedule.getUser()).addSchedule(schedule);
		}

		List<AuditorVacation> vacations = auditorVacationDAO.findAll();
		for (AuditorVacation vacation : vacations) {
			if (vacation.getUser() == null)
				holidays.add(vacation);
			else
				getAuditor(vacation.getUser()).addVacation(vacation);
		}

		PicsLogger.log("Parse data and create availability");

		auditorAvailabilityDAO.removeAll();

		int minDaysAway = Integer.parseInt(appPropertyDAO.find("schedule.mindays").getValue());
		int maxDaysAway = Integer.parseInt(appPropertyDAO.find("schedule.maxdays").getValue());

		Calendar startDate = Calendar.getInstance();
		startDate.add(Calendar.DAY_OF_YEAR, minDaysAway);
		Calendar endDate = Calendar.getInstance();
		endDate.add(Calendar.DAY_OF_YEAR, maxDaysAway);

		PicsLogger.log("Building schedules for " + auditors.size() + " auditors between " + startDate.getTime()
				+ " and " + endDate.getTime());
		for (Auditor auditor : auditors.values()) {
			for (AuditorAvailability availiability : auditor.buildAvailability(startDate.getTime(), endDate.getTime())) {
				auditorAvailabilityDAO.save(availiability);
			}
		}

		PicsLogger.stop();
		return SUCCESS;
	}

	private Auditor getAuditor(User user) {
		if (auditors.get(user) == null) {
			auditors.put(user, new Auditor(user, holidays));
		}
		return auditors.get(user);
	}

	private class Auditor {
		private Map<Integer, WeekDays> schedules = new HashMap<Integer, WeekDays>();
		private List<AuditorVacation> vacations = new ArrayList<AuditorVacation>();
		private User user;

		public Auditor(User user, List<AuditorVacation> holidays) {
			this.user = user;
			this.vacations.addAll(holidays);
		}

		private class WeekDays {
			public List<AuditorSchedule> schedules = new ArrayList<AuditorSchedule>();
		}

		public void addSchedule(AuditorSchedule schedule) {
			int weekDay = schedule.getWeekDay();
			if (schedules.get(weekDay) == null)
				schedules.put(weekDay, new WeekDays());
			schedules.get(weekDay).schedules.add(schedule);
		}

		public void addVacation(AuditorVacation vacation) {
			vacations.add(vacation);
		}

		public List<AuditorAvailability> buildAvailability(Date startDate, Date endDate) {
			List<AuditorAvailability> list = new ArrayList<AuditorAvailability>();

			Calendar nextDate = Calendar.getInstance();
			nextDate.setTime(startDate);
			while (nextDate.getTime().before(endDate)) {
				// Running Sample: Today is Monday September 21, 2009
				nextDate.add(Calendar.DAY_OF_YEAR, 1);
				PicsLogger.log("nextDate = " + nextDate.getTime());

				// Running Sample: It's a Monday
				int weekDay = nextDate.get(Calendar.DAY_OF_WEEK);

				if (schedules.get(weekDay) != null) {
					// Running Sample: This auditor works on Mondays
					for (AuditorSchedule schedule : schedules.get(weekDay).schedules) {
						// Running Sample: This auditor works at 7 AM, 9 AM, 1 PM and 3 PM
						Calendar proposedStartTime = Calendar.getInstance();
						// Set the day
						proposedStartTime.setTime(nextDate.getTime());
						// Set the time
						proposedStartTime.set(Calendar.HOUR_OF_DAY, schedule.getStartTime() / 60);
						proposedStartTime.set(Calendar.MINUTE, schedule.getStartTime() % 60);
						proposedStartTime.set(Calendar.SECOND, 0);
						// Set the end time
						Calendar proposedEndTime = Calendar.getInstance();
						proposedEndTime.setTime(proposedStartTime.getTime());
						proposedEndTime.add(Calendar.MINUTE, schedule.getDuration());

						PicsLogger.log("Proposed " + proposedStartTime.getTime() + " to " + proposedEndTime.getTime());
						boolean available = true;

						for (AuditorVacation vacation : vacations) {
							if (vacation.getStartDate().before(proposedEndTime.getTime())
									&& vacation.getEndDate().after(proposedStartTime.getTime())) {
								PicsLogger.log("Conflicting event " + vacation.getStartDate() + " to "
										+ vacation.getEndDate() + " " + vacation.getDescription());
								available = false;
							}
						}

						if (available) {
							AuditorAvailability availability = new AuditorAvailability();
							availability.setUser(schedule.getUser());
							availability.setAuditColumns(new User(User.SYSTEM));
							availability.setStartDate(proposedStartTime.getTime());
							availability.setDuration(schedule.getDuration());
							list.add(availability);
							PicsLogger.log("adding AuditorAvailability for " + availability.getStartDate());
						}
					}
				}

			}
			return list;
		}
	}
}
