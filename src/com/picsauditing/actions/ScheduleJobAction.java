package com.picsauditing.actions;

import java.util.Hashtable;
import java.util.Map;

import org.quartz.TimeOfDay;

import com.picsauditing.util.schedule.MySampleJob;
import com.picsauditing.util.schedule.PicsJobDetails;
import com.picsauditing.util.schedule.PicsScheduleDetails;
import com.picsauditing.util.schedule.PicsToQuartz;

/**
 * Schedule Job Action - Presents Schedule and Job Info as well as initiate
 */
@SuppressWarnings("serial")
public class ScheduleJobAction extends PicsActionSupport {
	/*
	@Autowired
	private AppPropertyDAO appPropertyDAO;
	*/
	
	static int jobCount = 1;
	
	private String scheduleType = JSP_DAILY;
	private final static String JSP_DAILY = "daily";
	private final static String JSP_DAYS_PER_WEEK = "weekly";
	private final static String JSP_DAYS_PER_MONTH = "monthly";
	private final static Map<String, PicsScheduleDetails.ScheduleType> JspScheduleValuesToPics = new Hashtable<String, PicsScheduleDetails.ScheduleType>();
	static {
		JspScheduleValuesToPics.put(JSP_DAILY, PicsScheduleDetails.ScheduleType.DAILY);
		JspScheduleValuesToPics.put(JSP_DAYS_PER_WEEK, PicsScheduleDetails.ScheduleType.DAYS_PER_WEEK);
		JspScheduleValuesToPics.put(JSP_DAYS_PER_MONTH, PicsScheduleDetails.ScheduleType.DAYS_PER_MONTH);
	}
	
	private int recurrenceInterval = 0;
	
	private int startTimeHour = 1;
	private String startTimeAMPM = "am";
	// 12:00 am is just before 12:01 am  ???
	
	public String execute() throws Exception {
		System.out.println("ScheduleAction.execute()");
		return SUCCESS;
	}

	// TODO - Move startScheduler outside of this class
	public String initSchedule() throws Exception {
		System.out.println("Init the scheduler");
		PicsToQuartz.startScheduler();
	
		return SUCCESS;
	}
	
	public String startJob() throws Exception {
		System.out.println("ScheduleJobAction.startJob");
		
		PicsScheduleDetails picsScheduleDetails;
		PicsJobDetails picsJobDetails;

		picsScheduleDetails = new PicsScheduleDetails();
		picsJobDetails = new PicsJobDetails("Sample Job-" + jobCount++, MySampleJob.class);

		// every day and every hour between 8am to 5pm
		picsScheduleDetails.setScheduleType(JspScheduleValuesToPics.get(scheduleType));
		picsScheduleDetails.setRecurringIntervalSeconds(getRecurrenceInterval());

		picsScheduleDetails.setStartingFireTime(new TimeOfDay(8,00));
		picsScheduleDetails.setEndingFireTime(new TimeOfDay(17,00));
		
		// PicsToQuartz.scheduleJob(picsJobDetails, picsScheduleDetails);
		return SUCCESS;
	}
	
	// getters/setters
	public String getScheduleType() {
		return scheduleType;
	}
	public void setScheduleType(String scheduleT) {
		// TODO - block invalid values
		// throw exception on invalid values
		this.scheduleType = scheduleT;
	}
	
	public int getRecurrenceInterval() {
		return recurrenceInterval;
	}
	public void setRecurrenceInterval(int recurringValue) {
		// TODO - block invalid values
		// throw exception on invalid values
		this.recurrenceInterval = recurringValue;
	}
}
