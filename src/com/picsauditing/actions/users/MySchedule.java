package com.picsauditing.actions.users;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditorAvailabilityDAO;
import com.picsauditing.dao.AuditorScheduleDAO;
import com.picsauditing.dao.AuditorVacationDAO;
import com.picsauditing.jpa.entities.AuditorAvailability;
import com.picsauditing.jpa.entities.AuditorSchedule;
import com.picsauditing.jpa.entities.AuditorVacation;

@SuppressWarnings("serial")
public class MySchedule extends PicsActionSupport implements Preparable {
	private int auditorID;
	private AuditorSchedule schedule = null;
	private List<AuditorSchedule> schedules = null;
	private List<AuditorVacation> vacations = null;
	private List<AuditorAvailability> availability = null;

	private AuditorAvailabilityDAO auditorAvailabilityDAO;
	private AuditorScheduleDAO auditorScheduleDAO;
	private AuditorVacationDAO auditorVacationDAO;
	private JSONObject json;

	private CalEvent calEvent;

	public MySchedule(AuditorAvailabilityDAO auditorAvailabilityDAO, AuditorScheduleDAO auditorScheduleDAO,
			AuditorVacationDAO auditorVacationDAO) {
		this.auditorAvailabilityDAO = auditorAvailabilityDAO;
		this.auditorScheduleDAO = auditorScheduleDAO;
		this.auditorVacationDAO = auditorVacationDAO;
	}

	public void prepare() throws Exception {
		int id = this.getParameter("schedule.id");
		if (id > 0)
			auditorScheduleDAO.find(id);

		int calId = this.getParameter("calEvent.id");
		if (calId > -1)
			calEvent = new CalEvent(calId);
	}

	public String execute() throws Exception {
		loadPermissions();
		auditorID = permissions.getUserId();

		if (button != null) {
			if (button.startsWith("json")) {
				json = new JSONObject();
				JSONArray events = new JSONArray();
				json.put("events", events);
				if (button.equals("jsonSchedule")) {
					for (AuditorSchedule row : getSchedules()) {
						events.add(row.toJSON());
					}
				}
				if (button.equals("jsonVacation")) {

					for (AuditorVacation row : getVacations()) {
						events.add(row.toJSON());
					}
				}
				if (button.equals("jsonAvailability")) {

					for (AuditorAvailability row : getAvailability()) {
						events.add(row.toJSON());
					}
				}
				return SUCCESS;
			}

			if (button.equals("deleteSchedule")) {
				schedule = auditorScheduleDAO.find(calEvent.id);
				if (schedule == null || schedule.getId() == 0) {
					output = "No schedule found.";
					return SUCCESS;
				}

				auditorScheduleDAO.remove(schedule);
				output = "Successfully removed schedule " + schedule.getId() + ". " + schedule;
				return BLANK;
			}

			if (button.equals("deleteVacation")) {
				// if (vacation == null || vacation.getId() == 0) {
				// addActionError("No vacation found");
				// return SUCCESS;
				// }
			}

			if (button.equalsIgnoreCase("save")) {
				if (calEvent == null) {
					output = "No schedule to save";
					return BLANK;
				}
				schedule = auditorScheduleDAO.find(calEvent.id);
				if (schedule == null)
					schedule = new AuditorSchedule();

				schedule.setStartTime(calEvent.start);
				schedule.setEndTime(calEvent.end);
				schedule.setWeekDay(calEvent.start);

				if (schedule.getUser() == null)
					schedule.setUser(getUser());
				schedule.setAuditColumns(permissions);
				auditorScheduleDAO.save(schedule);
				output = "Successfully Saved Timeslot " + schedule.getId() + ". " + schedule;

				return BLANK;
			}

		}

		return SUCCESS;
	}

	public AuditorSchedule getSchedule() {
		return schedule;
	}

	public void setSchedule(AuditorSchedule schedule) {
		this.schedule = schedule;
	}

	public List<AuditorSchedule> getSchedules() {
		if (schedules == null) {
			schedules = auditorScheduleDAO.findByAuditorID(auditorID);
		}
		return schedules;
	}

	public List<AuditorVacation> getVacations() {
		if (vacations == null) {
			vacations = auditorVacationDAO.findByAuditorID(auditorID);
		}
		return vacations;
	}

	public List<AuditorAvailability> getAvailability() {
		if (availability == null) {
			availability = auditorAvailabilityDAO.findByAuditorID(auditorID);
		}
		return availability;
	}

	public JSONObject getJson() {
		return json;
	}

	public void setJson(JSONObject json) {
		this.json = json;
	}

	public CalEvent getCalEvent() {
		return calEvent;
	}

	public void setCalEvent(CalEvent calEvent) {
		this.calEvent = calEvent;
	}

	public class CalEvent {
		public int id;
		public long start;
		public long end;

		public CalEvent(int id) {
			this.id = id;
		}
	}
}
