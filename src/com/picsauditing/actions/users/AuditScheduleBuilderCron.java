package com.picsauditing.actions.users;

import java.util.Calendar;
import java.util.Date;

import com.picsauditing.PICS.AuditScheduleBuilder;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AppPropertyDAO;

@SuppressWarnings("serial")
public class AuditScheduleBuilderCron extends PicsActionSupport {

	private static Date lastRun = null;
	private static final int MINUTES_TO_WAIT = 30;
	
	private AuditScheduleBuilder auditScheduleBuilder;

	public AuditScheduleBuilderCron(AuditScheduleBuilder auditScheduleBuilder, AppPropertyDAO appPropertyDAO) {
		this.auditScheduleBuilder = auditScheduleBuilder;
	}

	public String execute() throws Exception {
		if (lastRun != null) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, -1 * MINUTES_TO_WAIT);
			// cal time is now MINUTES_TO_WAIT (15) minutes ago
			if (lastRun.after(cal.getTime())) {
				addActionError("Just ran AuditScheduleBuilder, not rebuilding");
				return SUCCESS;
			}
		}
		addActionMessage("Running AuditScheduleBuilder");
		auditScheduleBuilder.build();
		lastRun = new Date();
		return SUCCESS;
	}

	/**
	 * Usage: AuditScheduleBuilderCron.rerun(ActionContext.getContext());
	 * @param context
	 */
	public static void rerun() {
		lastRun = null;
	}
}
