package com.picsauditing.util.schedule;

import java.util.Date;

import org.quartz.TimeOfDay;

/**
 * Provide PICS style scheduling details.
 * 
 * This is later used to convert the schedule AND the job info
 * to a quartz (or whatever) job schedule.
 * 
 * The initial setting is fire once immediately
 * 
 * @author SWetzel
 *
 */
public class PicsScheduleDetails {
	public static final int SECONDS_PER_HOUR = 3600;
	public static final int SECONDS_PER_MINUTE = 60;
	enum ScheduleType { DAILY, DAYS_PER_WEEK, DAYS_PER_MONTH };
	ScheduleType scheduleType = ScheduleType.DAILY;
	int recurringIntervalInSeconds = 0;  // 0 means just once for the day/weekdays/monthdays
	TimeOfDay startingFireTime = null;
	TimeOfDay endingFireTime = null;  // meaningless if recurringIntervalInSeconds is 0
	
	// TODO -- Redo to capture schedules for DAYS_PER_WEEK and DAYS_PER_MONTH
	
	public PicsScheduleDetails() {
		super();
	}
	
	private String scheduleT = "Daily";
	public String getScheduleT() {
		return scheduleT;
	}
	public void setScheduleT(String scheduleT) {
		this.scheduleT = scheduleT;
	}

	public ScheduleType getScheduleType() {
		return scheduleType;
	}
	public void setScheduleType(ScheduleType scheduleType) {
		this.scheduleType = scheduleType;
	}
	public void setScheduleAsDaily() {
		setScheduleType(ScheduleType.DAILY);
	}
	public void setScheduleAsDayOfWeek() {
		setScheduleType(ScheduleType.DAYS_PER_WEEK);
	}
	public void setScheduleAsDayOfMonth() {
		setScheduleType(ScheduleType.DAYS_PER_MONTH);
	}
	
	// Recurring Time getters/setters for Displays and Quartz
	public boolean isRecurringIntervalTimeOnce() {
		return recurringIntervalInSeconds == 0;
	}
	public boolean isRecurringIntervalTimeMinutely() {
		if (isRecurringIntervalTimeOnce()) {
			return false;
		}
		return !isRecurringIntervalTimeHourly();
	}
	public boolean isRecurringIntervalTimeHourly() {
		if (isRecurringIntervalTimeOnce()) {
			return false;
		}
		if (recurringIntervalInSeconds % SECONDS_PER_HOUR == 0) {
			return true;
		}
		return false;
	}
	public int getRecurringIntervalSeconds() {
		return recurringIntervalInSeconds;
	}
	public void setRecurringIntervalSeconds(int recurringIntervalSeconds) {
		this.recurringIntervalInSeconds = recurringIntervalSeconds;
	}
	
	public TimeOfDay getStartingFireTime() {
		return startingFireTime;
	}
	public void setStartingFireTime(TimeOfDay fireTime) {
		this.startingFireTime = fireTime;
	}
	public TimeOfDay getEndingFireTime() {
		return endingFireTime;
	}
	public void setEndingFireTime(TimeOfDay fireTime) {
		this.endingFireTime = fireTime;
	}
	

}
