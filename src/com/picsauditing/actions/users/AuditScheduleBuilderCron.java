package com.picsauditing.actions.users;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.PICS.AuditScheduleBuilder;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AppPropertyDAO;

@SuppressWarnings("serial")
public class AuditScheduleBuilderCron extends PicsActionSupport {

	private static final String LAST_RUN_KEY = "AuditScheduleBuilderCron.LASTRUNDATE";
	private static final int MINUTES_TO_WAIT = 15;
	
	private AuditScheduleBuilder auditScheduleBuilder;
	//private AppPropertyDAO appPropertyDAO;

	public AuditScheduleBuilderCron(AuditScheduleBuilder auditScheduleBuilder, AppPropertyDAO appPropertyDAO) {
		this.auditScheduleBuilder = auditScheduleBuilder;
		//this.appPropertyDAO = appPropertyDAO;
	}

	public String execute() throws Exception {
		Map session = ActionContext.getContext().getSession();
		if (session.containsKey(LAST_RUN_KEY)) {
			Date lastRun = (Date)session.get(LAST_RUN_KEY);
			if (lastRun != null) {
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.MINUTE, -1 * MINUTES_TO_WAIT);
				// cal time is now MINUTES_TO_WAIT (15) minutes ago
				if (lastRun.after(cal.getTime())) {
					addActionError("Just ran AuditScheduleBuilder, not rebuilding");
					return SUCCESS;
				}
			}
		}
		addActionMessage("Running AuditScheduleBuilder");
		auditScheduleBuilder.build();
		session.put(LAST_RUN_KEY, new Date());
		return SUCCESS;
	}

}
