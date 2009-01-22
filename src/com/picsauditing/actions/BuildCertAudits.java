package com.picsauditing.actions;

import java.util.Arrays;
import java.util.List;

import com.picsauditing.PICS.AuditBuilder;
import com.picsauditing.PICS.AuditPercentCalculator;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.ContractorAudit;


@SuppressWarnings( "serial" )
public class BuildCertAudits extends PicsActionSupport {

	protected ContractorAuditDAO auditDao = null;
	protected AuditBuilder auditBuilder = null;
	protected AuditPercentCalculator auditPercentCalculator = null;

	public BuildCertAudits(
			ContractorAuditDAO auditDao, 
			AuditBuilder auditBuilder, AuditPercentCalculator auditPercentCalculator
		) {
		this.auditDao = auditDao;
		this.auditBuilder = auditBuilder;
		this.auditPercentCalculator = auditPercentCalculator;
	}

	public String execute() throws Exception {

		for( String auditid : Arrays.asList("13","14","15","16","20","21","22","23","24")) {
			List<ContractorAudit> audits = auditDao.findWhere(999999,"auditType.id = " + auditid,"");
			int processed = 0;
			for( ContractorAudit audit : audits) {
				auditBuilder.fillAuditCategories(audit);
				auditBuilder.fillAuditOperators(audit.getContractorAccount(), audit);
				auditPercentCalculator.percentCalculateComplete(audit, true);
				System.out.println("processed : " + ++processed + " out of " + audits.size() + " for auditid: " + auditid);
			}
		}
		return SUCCESS;
	}

}
