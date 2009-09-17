package com.picsauditing.jpa.entities;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.json.simple.JSONObject;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.util.Strings;

@Entity
@Table(name = "auditor_vacation")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "temp")
public class AuditorVacation extends BaseTable {
	private User user;
	private Date startDate;
	private Date endDate;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject toJSON(boolean full) {
		JSONObject obj = new JSONObject();
		obj.put("id", id);
		obj.put("title", Strings.isEmpty(description) ? "Vacation" : description);

		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		cal.setTimeZone(user.getTimezoneObject());
		obj.put("start", cal.getTimeInMillis());

		if (endDate != null) {
			cal.setTime(endDate);
			obj.put("end", cal.getTimeInMillis());
		}
		
		if (user == null)
			obj.put("className", "monthly-company");

		return obj;
	}
	
	@Override
	public String toString() {
		return id + " " + description + " " + DateBean.format(startDate, "MM/dd/yyyy hh:mm a") + " " + DateBean.format(endDate, "MM/dd/yyyy hh:mm a");
	}

}
