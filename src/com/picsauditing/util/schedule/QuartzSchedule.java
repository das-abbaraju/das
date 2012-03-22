package com.picsauditing.util.schedule;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

public class QuartzSchedule {
	static Log logger = LogFactory.getLog(QuartzSchedule.class);
	
	private QuartzSchedule() {
	}
	
	private static Scheduler scheduler = null;
	public static Scheduler getScheduler() {
		return scheduler;
	}
	public static void setScheduler(Scheduler scheduler) {
		QuartzSchedule.scheduler = scheduler;
	}

	public static void startScheduler() {
		if (scheduler != null) {
			System.out.println("Scheduler already running!!");
			return;
		}
		
		System.out.println("Going to start the scheduler");

		try {
			// note: best practice is StdSchedulerFactory and not DirectSchedulerFactory
			scheduler = StdSchedulerFactory.getDefaultScheduler();
	
			scheduler.start();
			
			System.out.println("Scheduler started at " + new Date());
			logger.info("Scheduler started at " + new Date());
		}
		catch (SchedulerException ex) {
			logger.error(ex);
		}
	}
	
	public static void scheduleJob(JobDetail jobDetail, Trigger trigger) throws SchedulerException {
		getScheduler().scheduleJob(jobDetail, trigger);
	}
}