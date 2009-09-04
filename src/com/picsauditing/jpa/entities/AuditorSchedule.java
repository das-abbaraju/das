package com.picsauditing.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

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
	 * The day of the week Sunday (1) through Saturday (7)
	 * See 
	 * @return
	 */
	@Column(nullable = false)
	public int getWeekDay() {
		return weekDay;
	}
	
	public void setWeekDay(int weekDay) {
		this.weekDay = weekDay;
	}

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

}
