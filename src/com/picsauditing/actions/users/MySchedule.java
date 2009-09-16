package com.picsauditing.actions.users;

import java.util.Date;
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

	private long start;
	private long end;

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

	@SuppressWarnings("unchecked")
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

			if (button.equals("saveVacation")) {
				if (calEvent != null) {
					boolean update = true;
					AuditorVacation vacation = auditorVacationDAO.find(calEvent.id);
					if (vacation == null) {
						vacation = new AuditorVacation();
						update = false;
					} else if (vacation.getUser() == null) {
						// TODO make a permission for editing global holidays
						json = new JSONObject();
						json.put("title", "Event Not Saved");
						json.put("output", "You do not have permission to edit company level holidays.");
						return SUCCESS;
					}

					vacation.setDescription(calEvent.title);

					if (calEvent.start > 0)
						vacation.setStartDate(new Date(calEvent.start));

					if (calEvent.end > 0)
						vacation.setEndDate(new Date(calEvent.end));

					// TODO change to currentUser
					vacation.setUser(getUser());

					auditorVacationDAO.save(vacation);

					output = vacation.toString();

					json = new JSONObject();
					if (update)
						json.put("title", "Vacation Modified");
					else
						json.put("title", "New Vacation Added");

					json.put("output", output);
					json.put("calEvent", vacation.toJSON());
					json.put("update", update);
					return SUCCESS;
				}
			}

			if (button.equals("deleteVacation")) {
				// if (vacation == null || vacation.getId() == 0) {
				// addActionError("No vacation found");
				// return SUCCESS;
				// }
				AuditorVacation vacation = auditorVacationDAO.find(calEvent.id);
				String title = "Vacation not removed";
				boolean deleted = false;
				if (vacation != null) {
					if (vacation.getUser() != null) {
						auditorVacationDAO.remove(calEvent.id);
						title = "Vacation item removed";
						output = "Successfully removed " + calEvent.id;
						deleted = true;
					} else {
						output = "Cannot remove corporate level events";
					}
				} else {
					output = "Vacation does not exist";
				}

				json = new JSONObject();
				json.put("title", title);
				json.put("output", output);
				json.put("deleted", deleted);
				if (vacation != null)
					json.put("calEvent", vacation.toJSON());

				return SUCCESS;
			}

			if (button.equalsIgnoreCase("save")) {
				if (calEvent == null) {
					output = "No schedule to save";
					json = new JSONObject();
					json.put("output", output);
					return JSON;
				}
				schedule = auditorScheduleDAO.find(calEvent.id);
				if (schedule == null)
					schedule = new AuditorSchedule();

				schedule.setWeekDay(calEvent.start);
				schedule.setStartTime(calEvent.start);
				// schedule.setEndTime(calEvent.end);
				schedule.setDuration(120);
				if (schedule.getUser() == null)
					schedule.setUser(getUser());

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
				if (schedule != null)
					json.put("calEvent", schedule.toJSON());
				return JSON;
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
			vacations = auditorVacationDAO.findByAuditorID(auditorID, new Date(start), new Date(end));
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

	public class CalEvent {
		public int id;

		// UTC timestamps
		public long start = 0l;
		public long end = 0l;

		public String title;

		public CalEvent(int id) {
			this.id = id;
		}
	}
}
