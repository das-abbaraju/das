package com.picsauditing.util.schedule;

import org.quartz.Job;

public interface ScheduleControls {

	public void startScheduler();

	public void scheduleJob(String jobname, Class jobClass, PicsScheduleDetails picsScheduleDetails);

}