package com.picsauditing.actions.users;

import java.util.List;

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
	}

	public String execute() throws Exception {
		loadPermissions();
		auditorID = permissions.getUserId();
		
		if (button != null) {
			if (button.equals("deleteSchedule")) {
				if (schedule == null || schedule.getId() == 0) {
					addActionError("No schedule found");
					return SUCCESS;
				}
			}

			if (button.equals("deleteVacation")) {
//				if (vacation == null || vacation.getId() == 0) {
//					addActionError("No vacation found");
//					return SUCCESS;
//				}
			}

			if (button.equalsIgnoreCase("save")) {
				if (schedule == null) {
					addActionError("No schedule to save");
					return SUCCESS;
				}
				if (schedule.getUser() == null)
					schedule.setUser(getUser());
				schedule.setAuditColumns(permissions);
				auditorScheduleDAO.save(schedule);
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
}
