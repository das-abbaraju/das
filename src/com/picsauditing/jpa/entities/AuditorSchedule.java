package com.picsauditing.jpa.entities;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.json.simple.JSONObject;

@Entity
@Table(name = "auditor_schedule")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "temp")
public class AuditorSchedule extends BaseTable {
	private User user;
	private int weekDay;
	private int startTime;
	private int duration = 120; // two hours

	@ManyToOne
	@JoinColumn(name = "userID", nullable = false, updatable = false)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * The day of the week Sunday (1) through Saturday (7) See
	 * 
	 * @return
	 */
	@Column(nullable = false)
	public int getWeekDay() {
		return weekDay;
	}

	public void setWeekDay(int weekDay) {
		this.weekDay = weekDay;
	}
	
	public void setWeekDay(long time) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		
		this.weekDay = cal.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * Minutes into the day
	 * @return
	 */
	@Column(nullable = false)
	public int getStartTime() {
		return startTime;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	@Column(nullable = false)
	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	@Transient
	public void setStartTime(long startTime) {
		// convert from milliseconds to minutes
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(startTime);
		this.startTime = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE);
	}

	@Transient
	public void setEndTime(long endTime) {
		// convert from milliseconds to minutes
		// subtract to find duration
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(endTime);
		this.duration = ((cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)) - startTime);
	}

	@Override
	public void fromJSON(JSONObject obj) {
		super.fromJSON(obj);
	}

	@Override
	public JSONObject toJSON() {
		return toJSON(true);
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject toJSON(boolean full) {
		JSONObject obj = new JSONObject();
		obj.put("id", id);

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_WEEK, weekDay);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, startTime);
		obj.put("start", cal.getTimeInMillis());

		cal.add(Calendar.MINUTE, duration);
		obj.put("end", cal.getTimeInMillis());

		return obj;
	}

}
