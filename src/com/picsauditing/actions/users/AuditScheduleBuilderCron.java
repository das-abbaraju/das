package com.picsauditing.actions.users;

import com.picsauditing.PICS.AuditScheduleBuilder;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AppPropertyDAO;

@SuppressWarnings("serial")
public class AuditScheduleBuilderCron extends PicsActionSupport {

	private AuditScheduleBuilder auditScheduleBuilder;

	public AuditScheduleBuilderCron(AuditScheduleBuilder auditScheduleBuilder, AppPropertyDAO appPropertyDAO) {
		this.auditScheduleBuilder = auditScheduleBuilder;
	}

	public String execute() throws Exception {
		addActionMessage("Running AuditScheduleBuilder");
		auditScheduleBuilder.build();
		return SUCCESS;
	}

}
