package com.picsauditing.actions.audits;

import java.util.List;

import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditorAvailabilityDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditorAvailability;

@SuppressWarnings("serial")
public class ScheduleAudit extends AuditActionSupport {

	private List<AuditorAvailability> nextAvailable;

	private AuditorAvailabilityDAO auditorAvailabilityDAO;

	public ScheduleAudit(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, AuditCategoryDataDAO catDataDao,
			AuditDataDAO auditDataDao, AuditorAvailabilityDAO auditorAvailabilityDAO) {
		super(accountDao, auditDao, catDataDao, auditDataDao);
		this.auditorAvailabilityDAO = auditorAvailabilityDAO;
		this.subHeading = "Schedule Audit";
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		findConAudit();
		
		subHeading = "Schedule " + conAudit.getAuditType().getAuditName();

		nextAvailable = auditorAvailabilityDAO.findByAuditorID(941);

		return SUCCESS;
	}

	public List<AuditorAvailability> getNextAvailable() {
		return nextAvailable;
	}

}
