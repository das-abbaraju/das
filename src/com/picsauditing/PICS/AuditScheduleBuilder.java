package com.picsauditing.PICS;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.AuditorAvailabilityDAO;
import com.picsauditing.dao.AuditorScheduleDAO;
import com.picsauditing.dao.AuditorVacationDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditorAvailability;
import com.picsauditing.jpa.entities.AuditorSchedule;
import com.picsauditing.jpa.entities.AuditorVacation;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.Geo;
import com.picsauditing.util.Location;
import com.picsauditing.util.log.PicsLogger;

/**
 * This class should ignore all timezone information and calculate everything
 * based on the server time
 * 
 * @author Trevor
 * 
 */
public class AuditScheduleBuilder {
	private Map<User, Auditor> auditors = new HashMap<User, Auditor>();
	private List<AuditorVacation> holidays = new ArrayList<AuditorVacation>();
	private List<ContractorAudit> scheduledAudits = new ArrayList<ContractorAudit>();

	private AuditorAvailabilityDAO auditorAvailabilityDAO;
	private AuditorScheduleDAO auditorScheduleDAO;
	private AuditorVacationDAO auditorVacationDAO;
	private AppPropertyDAO appPropertyDAO;
	private ContractorAuditDAO contractorAuditDAO;

	public AuditScheduleBuilder(AuditorAvailabilityDAO auditorAvailabilityDAO, AuditorScheduleDAO auditorScheduleDAO,
			AuditorVacationDAO auditorVacationDAO, ContractorAuditDAO contractorAuditDAO, AppPropertyDAO appPropertyDAO) {
		this.auditorAvailabilityDAO = auditorAvailabilityDAO;
		this.auditorScheduleDAO = auditorScheduleDAO;
		this.auditorVacationDAO = auditorVacationDAO;
		this.contractorAuditDAO = contractorAuditDAO;
		this.appPropertyDAO = appPropertyDAO;
	}

	public void build() {
		PicsLogger.start("AuditScheduleBuilder");

		PicsLogger.log("Get all my data into RAM");

		holidays = auditorVacationDAO.findByAuditorID(0);

		List<AuditorSchedule> schedules = auditorScheduleDAO.findAll();
		for (AuditorSchedule schedule : schedules) {
			getAuditor(schedule.getUser()).addSchedule(schedule);
		}

		List<AuditorVacation> vacations = auditorVacationDAO.findAll();
		for (AuditorVacation vacation : vacations) {
			if (vacation.getUser() != null)
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

		scheduledAudits = contractorAuditDAO.findScheduledAudits(0, startDate.getTime(), endDate.getTime());
		PicsLogger.log("Found " + scheduledAudits.size() + " already scheduled audits");

		PicsLogger.log("Building schedules for " + auditors.size() + " auditors between " + startDate.getTime()
				+ " and " + endDate.getTime());
		for (Auditor auditor : auditors.values()) {
			List<AuditorAvailability> list = auditor.buildAvailability(startDate.getTime(), endDate.getTime());
			PicsLogger.log("Saving " + list.size() + " timeslots for " + auditor.getUser().getName());
			for (AuditorAvailability availiability : list) {
				auditorAvailabilityDAO.save(availiability);
			}
		}

		PicsLogger.stop();
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
		private TimeZone userTimeZone;

		public Auditor(User user, List<AuditorVacation> holidays) {
			this.user = user;
			this.userTimeZone = user.getTimezone();
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

		public User getUser() {
			return user;
		}

		/**
		 * For a this user (auditor) create their available time blocks to do
		 * audits between the dates (startDate and endDate) ie between 7 and 60
		 * days from today
		 * 
		 * @param startDate
		 * @param endDate
		 * @return
		 */
		public List<AuditorAvailability> buildAvailability(Date startDate, Date endDate) {
			PicsLogger.start("buildAvailability", "for " + user.getName());
			List<AuditorAvailability> list = new ArrayList<AuditorAvailability>();

			Calendar nextDate = Calendar.getInstance();
			// Start 7 days from today (or whatever the startDate is)
			nextDate.setTime(startDate);
			while (nextDate.getTime().before(endDate)) {
				// For each day during the time period
				// Running Sample: Today is Monday September 21, 2009
				nextDate.add(Calendar.DAY_OF_YEAR, 1);
				PicsLogger.log("nextDate = " + nextDate.getTime());

				// Running Sample: It's a Monday
				int weekDay = nextDate.get(Calendar.DAY_OF_WEEK);

				if (schedules.get(weekDay) != null) {
					// Running Sample: This auditor works on Mondays
					for (AuditorSchedule schedule : schedules.get(weekDay).schedules) {
						// Running Sample: This auditor works at 7 AM, 9 AM, 1
						// PM and 3 PM
						Calendar proposedStartTime = Calendar.getInstance();
						// Set the day
						proposedStartTime.setTime(nextDate.getTime());
						// Set the time
						proposedStartTime.setTimeZone(userTimeZone);
						proposedStartTime.set(Calendar.HOUR_OF_DAY, schedule.getStartTime() / 60);
						proposedStartTime.set(Calendar.MINUTE, schedule.getStartTime() % 60);
						proposedStartTime.set(Calendar.SECOND, 0);
						// Set the end time
						Calendar proposedEndTime = Calendar.getInstance();
						proposedEndTime.setTime(proposedStartTime.getTime());
						proposedEndTime.setTimeZone(userTimeZone);
						proposedEndTime.add(Calendar.MINUTE, schedule.getDuration());

						PicsLogger.log("Proposed " + proposedStartTime.getTime() + " to " + proposedEndTime.getTime()
								+ " for " + user.getName());
						boolean available = true;

						for (AuditorVacation vacation : vacations) {
							if (vacation.getStartDate().before(proposedEndTime.getTime())) {
								Calendar tempEndtime = Calendar.getInstance();
								if (vacation.getEndDate() == null) {
									tempEndtime.setTime(DateBean.getNextDayMidnight(vacation.getStartDate()));
								} else {
									tempEndtime.setTime(vacation.getEndDate());
								}
								if (tempEndtime.getTime().after(proposedStartTime.getTime())) {
									PicsLogger.log("Conflicting vacation " + vacation.getStartDate() + " to "
											+ vacation.getEndDate() + " " + vacation.getDescription());
									available = false;
								}
							}
						}

						ContractorAudit previousAudit = null;
						ContractorAudit nextAudit = null;

						boolean webOnly = false; // gets set to true only if we
						// have a web audit
						// scheduled that day
						boolean onsiteOnly = false; // gets set to true only if
						// we have an onsite audit
						// scheduled that
						// day

						for (ContractorAudit audit : scheduledAudits) {
							Calendar scheduledStarttime = Calendar.getInstance();
							scheduledStarttime.setTime(audit.getScheduledDate());

							if (scheduledStarttime.get(Calendar.DAY_OF_YEAR) == proposedStartTime
									.get(Calendar.DAY_OF_YEAR)
									&& audit.getAuditor().equals(user)) {

								if (audit.isConductedOnsite())
									onsiteOnly = true;
								else
									webOnly = true;

								Calendar scheduledEndtime = Calendar.getInstance();
								scheduledEndtime.setTime(audit.getScheduledDate());
								scheduledEndtime.add(Calendar.MINUTE, 120);

								long millisStartsAfterProposal = audit.getScheduledDate().getTime()
										- proposedEndTime.getTimeInMillis();
								long millisEndingBeforeProposal = proposedStartTime.getTimeInMillis()
										- scheduledEndtime.getTimeInMillis();

								if (millisStartsAfterProposal >= 0) {
									if (millisStartsAfterProposal < (60 * 60 * 1000))
										// This audit starts within 60 minutes
										// of the proposed finishing
										nextAudit = audit;
								} else if (millisEndingBeforeProposal >= 0) {
									if (millisEndingBeforeProposal < (60 * 60 * 1000))
										// This audit ends within 60 minutes of
										// the proposed beginning
										previousAudit = audit;
								} else {
									// This audit overlaps my proposal
									PicsLogger.log("Conflicting audit #" + audit.getId() + " "
											+ audit.getScheduledDate() + " to " + scheduledEndtime.getTime() + " "
											+ audit.getAuditType().getName().toString() + " for "
											+ audit.getContractorAccount().getName());
									available = false;
								}
							}
						}

						if (available) {
							AuditorAvailability availability = new AuditorAvailability();
							availability.setUser(schedule.getUser());
							availability.setAuditColumns(new User(User.SYSTEM));
							proposedStartTime.setTimeZone(userTimeZone);
							availability.setStartDate(proposedStartTime.getTime());
							availability.setDuration(schedule.getDuration());

							if (
									(nextAudit == null && previousAudit == null) ||
									(nextAudit == null && previousAudit.getLocation().equals(new Location(0,0,0))) ||
									(previousAudit == null && nextAudit.getLocation().equals(new Location(0,0,0)))
							){
								availability.setLocation(user.getLocation());
								availability.setMaxDistance(100);
							} else if (nextAudit == null) {
								availability.setLocation(previousAudit.getLocation());
							} else if (previousAudit == null) {
								availability.setLocation(nextAudit.getLocation());
							} else {
								availability.setLocation(Geo.middle(previousAudit.getLocation(), nextAudit.getLocation()));
							}

							availability.setOnsiteOnly(onsiteOnly);
							availability.setWebOnly(webOnly);

							if (user.getId() == 34065) {
								// Hi, I'm Phillip Laraway
								String[] gulfCoastCountrySubdivisions = { "TX", "AL", "LA", "MS" };
								availability.setOnlyInCountrySubdivisions(gulfCoastCountrySubdivisions);
							}

							PicsLogger.log("adding AuditorAvailability for " + availability.getStartDate());
							list.add(availability);
						}
					}
				}

			}
			PicsLogger.stop();
			return list;
		}
	}
}
