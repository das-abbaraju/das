package com.picsauditing.jpa.entities;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.json.simple.JSONObject;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.util.PicsDateFormat;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
@Entity
@Table(name = "auditor_vacation")
public class AuditorVacation extends BaseTable {
	private User user;
	private Date startDate;
	private Date endDate;
	private boolean allDay;
	private String description = null;

	@ManyToOne
	@JoinColumn(name = "userID", nullable = true, updatable = false)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Column(nullable = false)
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public boolean isAllDay() {
		return allDay;
	}

	public void setAllDay(boolean allDay) {
		this.allDay = allDay;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Transient
	public String getDescriptionClean() {
		return Strings.isEmpty(description) ? (user == null ? "Holiday" : "Vacation") : description;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject toJSON(boolean full) {
		JSONObject obj = new JSONObject();
		if (user != null) {
			obj.put("id", "Vacation_" + id);
			obj.put("owner", user.getName());
		} else {
			obj.put("id", "Holiday_" + id);
			obj.put("owner", "All Users");
		}
		obj.put("title", getDescriptionClean());

		Calendar start = Calendar.getInstance();
		Calendar end = null;
		start.setTime(startDate);
		if (user != null)
			start.setTimeZone(user.getTimezone());

		obj.put("start", start.getTimeInMillis());

		if (endDate != null) {
			end = Calendar.getInstance();
			end.setTime(endDate);
			obj.put("end", end.getTimeInMillis());
		}

		obj.put("allDay", allDay);

		if (user == null)
			obj.put("className", "cal-holiday");
		else
			obj.put("className", "cal-vacation");

		obj.put("editable", false);

		return obj;
	}

	@Override
	public String toString() {
		return "\"" + getDescriptionClean() + "\" " + DateBean.format(startDate, PicsDateFormat.Datetime12Hour) + " "
				+ DateBean.format(endDate, PicsDateFormat.Datetime12Hour);
	}

}