package com.picsauditing.actions.audits;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditorAvailabilityDAO;

@SuppressWarnings("serial")
public class ScheduleAudit extends PicsActionSupport {
	private AuditorAvailabilityDAO auditorAvailabilityDAO;

	public ScheduleAudit(AuditorAvailabilityDAO auditorAvailabilityDAO) {
		this.auditorAvailabilityDAO = auditorAvailabilityDAO;
	}

	public String execute() throws Exception {
		return SUCCESS;
	}

}
