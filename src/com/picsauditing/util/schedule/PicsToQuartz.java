package com.picsauditing.util.schedule;

import static org.quartz.DateBuilder.*;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.*;

import org.quartz.CronScheduleBuilder;
import org.quartz.DateBuilder;
import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.JobDetail;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import static org.quartz.TriggerKey.*;

/**
 * Provides service utilities to convert/adapt from displayable entities (PicsScheduleDetails, PicsJobDetails) 
 * to Quartz (TriggerDetails, JobDetails) and vice versa.
 *
 */
public class PicsToQuartz {
	public static final int JOB_PRIORITY = Thread.NORM_PRIORITY;
	private static int jobCount = 0;
	JobDetail jobDetail = null;
	Trigger trigger = null;
	
	public static void startScheduler() {
		QuartzSchedule.startScheduler();
	}

	public static void scheduleJob(PicsJobDetails picsJobDetails, PicsScheduleDetails picsScheduleDetails) throws SchedulerException {
		JobDetail jobDetail = convertJobDetailToQuartz(picsJobDetails);
		Trigger trigger = convertScheduleDetailToQuartz(jobDetail, picsScheduleDetails);
		QuartzSchedule.scheduleJob(jobDetail, trigger);
	}
	
	public static JobDetail convertJobDetailToQuartz(PicsJobDetails picsJobDetails)
	{
		JobDetail jobDetail = newJob(picsJobDetails.getJobClass())
				.withIdentity(picsJobDetails.getJobName(), Scheduler.DEFAULT_GROUP)
				.build();
		return jobDetail;
	}
	
	public static Trigger convertScheduleDetailToQuartz(JobDetail jobDetail, PicsScheduleDetails picsScheduleDetails)
	{
		/*
		 Trigger trigger = newTrigger() 
				 //.withIdentity(triggerKey("myTrigger", "myTriggerGroup"))
				 //.withSchedule(onDaysOfTheWeek(MONDAY, THURSDAY))
				 //.startAt(futureDate(10, MINUTES))
				 .build();
				 */
		 
		 TriggerBuilder<?> triggerBuilder = newTrigger()
				.withIdentity(triggerKey("myTrigger", "myTriggerGroup"))
				.withPriority(JOB_PRIORITY)
				.forJob(jobDetail);
		 // .withSchedule
		 // .startAt
		 
		 Trigger trigger = triggerBuilder.build();
	//	 trigger.getKey()

		/*
		DateBuilder dateBuilder;
		if (picsScheduleDetails.getFirstFireTime() == null) {
			// fire time NOW
			dateBuilder = newDate();
		}
		else {
			dateBuilder = 
		}
		DateBuilder dateBuilder = newDate();
 	    // private void buildTrigger(ScheduleBuilder schedule, Date dateToStartFiring) {
		Trigger trigger = newTrigger()
				.withIdentity("myTrigger")
				.startAt(futureDate(10, IntervalUnit.SECOND))
				.withPriority(6)
				.forJob(jobDetail)
				.withSchedule(simpleSchedule()
						.withIntervalInSeconds(10)
						.repeatForever())
						.build();
						*/
		/*
		Trigger trigger = newTrigger()
			    .withIdentity("myTrigger")
			    .startAt(dateToStartFiring)
			    .withPriority(6)
			    .forJob(jobDetail)
			    .withSchedule(schedule) // buildScheduleFiringTimes()
			    .build();
			    */
		return trigger;
	}
	
	private ScheduleBuilder buildScheduleFiringTimes()
	{
		/*
	  return simpleSchedule()
			  .withIntervalInSeconds(getRecurringIntervalSeconds())
			  .repeatForever();
			  */
		return null;
	}
}
