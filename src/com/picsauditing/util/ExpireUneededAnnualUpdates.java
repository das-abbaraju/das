package com.picsauditing.util;

import java.util.Date;

import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.util.SpringUtils;


public class ExpireUneededAnnualUpdates {

	public static void calculate(ContractorAccount contractor) {
		int completedAnnualAddendumCount = 0;
		ContractorAudit earliestAnnualUpdate = null;

		for (ContractorAudit audit : contractor.getAudits()) {
			if (isCompletedAnnualAddendum(audit)) {
				completedAnnualAddendumCount++;
				if (earliestAnnualUpdate == null
						|| (audit.getExpiresDate().getTime() < earliestAnnualUpdate.getExpiresDate().getTime()))
					earliestAnnualUpdate = audit;
			}
		}
		
		if (completedAnnualAddendumCount > AuditType.ANNUAL_ADDENDUM_RETENSION_PERIOD_IN_YEARS && earliestAnnualUpdate != null) {
			earliestAnnualUpdate.setExpiresDate(new Date());
			ContractorAuditDAO dao = (ContractorAuditDAO) SpringUtils.getBean("ContractorAuditDAO");
			dao.save(earliestAnnualUpdate);
		}
	}
	
	private static boolean isCompletedAnnualAddendum(ContractorAudit audit) {
		if (!audit.getAuditType().isAnnualAddendum() || audit.isExpired())
			return false;
		for (ContractorAuditOperator cao:audit.getOperators()) {
			if (cao.isVisible()) {
				if (!cao.getStatus().isComplete())
					return false;
			}
		}
		
		return true;
	}
}
