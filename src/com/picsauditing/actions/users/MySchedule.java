package com.picsauditing.actions.users;

import java.util.Date;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditorAvailabilityDAO;
import com.picsauditing.dao.AuditorScheduleDAO;
import com.picsauditing.dao.AuditorVacationDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.AuditorAvailability;
import com.picsauditing.jpa.entities.AuditorSchedule;
import com.picsauditing.jpa.entities.AuditorVacation;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.User;

@SuppressWarnings("serial")
public class MySchedule extends PicsActionSupport implements Preparable {
	private int currentUserID;
	private List<AuditorSchedule> schedules = null;
	private List<AuditorVacation> vacations = null;
	private List<AuditorVacation> holidays = null;
	private List<AuditorAvailability> availability = null;
	private List<ContractorAudit> scheduledAudits = null;

	private AuditorAvailabilityDAO auditorAvailabilityDAO;
	private AuditorScheduleDAO auditorScheduleDAO;
	private AuditorVacationDAO auditorVacationDAO;
	private ContractorAuditDAO contractorAuditDAO;
	private UserDAO userDAO;

	private String type = "Schedule";

	private long start;
	private long end;

	private CalEvent calEvent;
	private ScheduleEvent schedEvent;

	private User currentUser;

	public MySchedule(AuditorAvailabilityDAO auditorAvailabilityDAO, AuditorScheduleDAO auditorScheduleDAO,
			AuditorVacationDAO auditorVacationDAO, ContractorAuditDAO contractorAuditDAO, UserDAO userDAO) {
		this.auditorAvailabilityDAO = auditorAvailabilityDAO;
		this.auditorScheduleDAO = auditorScheduleDAO;
		this.auditorVacationDAO = auditorVacationDAO;
		this.contractorAuditDAO = contractorAuditDAO;
		this.userDAO = userDAO;
	}

	public void prepare() throws Exception {
		int id = this.getParameter("schedule.id");
		if (id > 0)
			auditorScheduleDAO.find(id);

		int calId = this.getParameter("calEvent.id");
		if (calId > -1)
			calEvent = new CalEvent(calId);

		int schedID = this.getParameter("schedEvent.id");
		if (schedID > -1)
			schedEvent = new ScheduleEvent(schedID);
	}

	@SuppressWarnings("unchecked")
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		tryPermissions(OpPerms.MyCalendar);

		if (currentUserID > 0) {
			currentUser = this.getUser(currentUserID);
		} else {
			currentUser = super.getUser();
			currentUserID = currentUser.getId();
		}

		if (button != null) {
			if (button.startsWith("json")) {
				json = new JSONObject();
				JSONArray events = new JSONArray();
				json.put("events", events);
				if (type.equals("weekly")) {
					for (AuditorSchedule schedule : getSchedules()) {
						events.add(schedule.toJSON());
					}
				}
				if (type.equals("Audit")) {
					for (ContractorAudit row : getScheduledAudits()) {
						JSONObject scheduledAudit = new JSONObject();

						scheduledAudit.put("id", "Audit_" + row.getId());
						scheduledAudit.put("title", row.getContractorAccount().getName());
						scheduledAudit.put("start", row.getScheduledDate().getTime());
						scheduledAudit.put("allDay", false);
						scheduledAudit.put("owner", currentUser.getName());
						scheduledAudit.put("url", "ScheduleAudit.action?auditID=" + row.getId());
						if (!row.isConductedOnsite())
							scheduledAudit.put("className", "cal-webcam");
						events.add(scheduledAudit);
					}
				}
				if (type.equals("Vacation")) {

					for (AuditorVacation row : getVacations()) {
						events.add(row.toJSON());
					}
				}
				if (type.equals("Holiday")) {

					for (AuditorVacation row : getHolidays()) {
						events.add(row.toJSON());
					}
				}
				if (type.equals("Availability")) {

					for (AuditorAvailability row : getAvailability()) {
						events.add(row.toJSON());
					}
				}
				return SUCCESS;
			}

			if (button.equals("save")) {
				if (!permissions.hasPermission(OpPerms.ManageCalendars, OpType.Edit)
						&& currentUserID != getUser().getId() && !"Holiday".equals(type)) {
					json = new JSONObject();
					json.put("title", "User Access Error");
					json.put("output", "You do not have permission to modify this account.");
					return SUCCESS;
				}

				if ("Vacation".equals(type) || "Holiday".equals(type)) {
					if ("Holiday".equals(type) && !permissions.hasPermission(OpPerms.Holidays)) {
						json = new JSONObject();
						json.put("title", "User Access Error");
						json.put("output", "You do not have permission to modify holidays.");
						return SUCCESS;
					}
					if (calEvent != null) {
						boolean update = true;
						AuditorVacation vacation = auditorVacationDAO.find(calEvent.id);
						if (vacation == null) {
							vacation = new AuditorVacation();
							update = false;

							if ("Vacation".equals(type))
								vacation.setUser(currentUser);
							else
								vacation.setUser(null);

						} else if (vacation.getUser() == null && !permissions.hasPermission(OpPerms.Holidays)) {
							json = new JSONObject();
							json.put("title", "User Access Error");
							json.put("output", "You do not have permission to modify vacations.");
							return SUCCESS;
						}

						vacation.setDescription(calEvent.title);

						if (calEvent.start > 0)
							vacation.setStartDate(new Date(calEvent.start));

						if (calEvent.end > 0)
							vacation.setEndDate(new Date(calEvent.end));

						vacation.setAuditColumns(permissions);
						auditorVacationDAO.save(vacation);

						output = vacation.toString();

						json = new JSONObject();
						if (update)
							json.put("title", type + " Modified");
						else
							json.put("title", "New " + type + " Added");

						json.put("output", output);
						json.put("calEvent", vacation.toJSON());
						json.put("update", update);
						return SUCCESS;
					}
				}
			}

			if (button.equals("delete")) {
				if (!permissions.hasPermission(OpPerms.ManageCalendars, OpType.Delete)
						&& currentUserID != getUser().getId() && !"Holiday".equals(type)) {
					json = new JSONObject();
					json.put("title", "User Access Error");
					json.put("output", "You do not have permission to modify this account.");
					return SUCCESS;
				}
				if ("Vacation".equals(type) || "Holiday".equals(type)) {
					AuditorVacation vacation = auditorVacationDAO.find(calEvent.id);
					String title = type + " not removed";
					boolean deleted = false;
					if (vacation != null) {
						if (vacation.getUser() != null || permissions.hasPermission(OpPerms.Holidays)) {
							auditorVacationDAO.remove(calEvent.id);
							title = type + " item removed";
							output = "Successfully removed " + calEvent.id + ". " + vacation.toString();
							deleted = true;
						} else {
							output = "You do not have permission to modify Holidays.";
						}
					} else {
						output = type + " does not exist";
					}

					json = new JSONObject();
					json.put("title", title);
					json.put("output", output);
					json.put("deleted", deleted);
					if (vacation != null)
						json.put("calEvent", vacation.toJSON());

					return SUCCESS;
				}
			}

			if (button.equals("saveSchedule")) {
				if (!permissions.hasPermission(OpPerms.ManageCalendars) && currentUserID != getUser().getId()) {
					json = new JSONObject();
					json.put("title", "User Access Error");
					json.put("output", "You do not have permission to modify this account.");
					return SUCCESS;
				}
				if (schedEvent == null) {
					output = "No schedule to save";
					json = new JSONObject();
					json.put("output", output);
					json.put("title", "Schedule not Found");
					return JSON;
				}
				AuditorSchedule schedule = auditorScheduleDAO.find(schedEvent.id);
				if (schedule == null)
					schedule = new AuditorSchedule();

				schedule.setWeekDay(schedEvent.weekDay + 1);
				schedule.setStartTime(schedEvent.startTime);
				schedule.setUser(currentUser);

				List<AuditorSchedule> otherSchedules = auditorScheduleDAO.findByAuditorID(schedule.getUser().getId());
				boolean overlap = false;
				for (AuditorSchedule auditorSchedule : otherSchedules) {
					if (schedule.overlaps(auditorSchedule)) {
						overlap = true;
						break;
					}
				}

				if (overlap) {
					if (schedule.getId() > 0)
						auditorScheduleDAO.refresh(schedule);
					else
						schedule = null;

					output = "That time overlaps with another timeslot. It will not be saved";
				} else {
					schedule.setAuditColumns(permissions);
					auditorScheduleDAO.save(schedule);
					output = "Successfully Saved Timeslot " + schedule.getId() + ". " + schedule;
				}

				json = new JSONObject();
				json.put("output", output);
				json.put("title", "Schedule Save Unsuccessful");
				if (schedule != null) {
					json.put("schedEvent", schedule.toJSON());
					json.put("title", "Schedule saved successfully");
				}
				return JSON;
			}

			if (button.equals("deleteSchedule")) {
				if (!permissions.hasPermission(OpPerms.ManageCalendars) && currentUserID != getUser().getId()) {
					json = new JSONObject();
					json.put("title", "User Access Error");
					json.put("output", "You do not have permission to modify this account.");
					return SUCCESS;
				}
				AuditorSchedule schedule = auditorScheduleDAO.find(schedEvent.id);
				String title = "";
				boolean deleted = true;
				if (schedule == null || schedule.getId() == 0) {
					title = "No schedule found";
					output = "The selected schedule item does not exist.";
					deleted = false;
				} else {
					auditorScheduleDAO.remove(schedule);
					title = "Schedule Item Deleted";
					output = "Successfully removed schedule " + schedule.getId() + ". " + schedule;
				}

				json = new JSONObject();
				json.put("title", title);
				json.put("output", output);
				json.put("deleted", deleted);
				if (deleted)
					json.put("schedEvent", schedule.toJSON());
				return SUCCESS;
			}

		}

		return SUCCESS;
	}

	public List<User> getAuditors() {
		List<User> auditors = userDAO.findAuditors();
		return auditors;
	}

	public List<AuditorSchedule> getSchedules() {
		if (schedules == null) {
			schedules = auditorScheduleDAO.findByAuditorID(currentUser.getId());
		}
		return schedules;
	}

	public List<AuditorVacation> getVacations() {
		if (vacations == null) {
			vacations = auditorVacationDAO.findByAuditorID(currentUser.getId(), new Date(start), new Date(end));
		}
		return vacations;
	}

	public List<AuditorVacation> getHolidays() {
		if (holidays == null) {
			holidays = auditorVacationDAO.findByAuditorID(0, new Date(start), new Date(end));
		}
		return holidays;
	}

	public List<AuditorAvailability> getAvailability() {
		if (availability == null) {
			availability = auditorAvailabilityDAO.findByAuditorID(currentUser.getId());
		}
		return availability;
	}

	public List<ContractorAudit> getScheduledAudits() {
		if (scheduledAudits == null) {
			scheduledAudits = contractorAuditDAO.findScheduledAudits(currentUserID, new Date(start), new Date(end));
		}

		return scheduledAudits;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public CalEvent getCalEvent() {
		return calEvent;
	}

	public void setCalEvent(CalEvent calEvent) {
		this.calEvent = calEvent;
	}

	public ScheduleEvent getSchedEvent() {
		return schedEvent;
	}

	public void setSchedEvent(ScheduleEvent schedEvent) {
		this.schedEvent = schedEvent;
	}

	public class CalEvent {
		public int id;

		// UTC timestamps
		public long start = 0l;
		public long end = 0l;

		public String title = "";

		public CalEvent(int id) {
			this.id = id;
		}
	}

	public class ScheduleEvent {
		public int id;
		public int weekDay;
		public int startTime;
		public int duration;

		public ScheduleEvent(int id) {
			this.id = id;
		}
	}

	public User getCurrentUser() {
		return currentUser;
	}

	public int getCurrentUserID() {
		return currentUserID;
	}

	public void setCurrentUserID(int currentUserID) {
		this.currentUserID = currentUserID;
	}

}
