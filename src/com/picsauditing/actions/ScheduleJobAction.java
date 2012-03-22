package com.picsauditing.actions;

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
	
	private String scheduleType = "Daily";
	public String getScheduleT() {
		return scheduleType;
	}
	public void setScheduleT(String scheduleT) {
		this.scheduleType = scheduleT;
	}

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
		picsScheduleDetails.setScheduleAsDaily();
		picsScheduleDetails.setRecurringIntervalSeconds(PicsScheduleDetails.SECONDS_PER_HOUR);
		picsScheduleDetails.setStartingFireTime(new TimeOfDay(8,00));
		picsScheduleDetails.setEndingFireTime(new TimeOfDay(17,00));
		
		// PicsToQuartz.scheduleJob(picsJobDetails, picsScheduleDetails);
		return SUCCESS;
	}
}
