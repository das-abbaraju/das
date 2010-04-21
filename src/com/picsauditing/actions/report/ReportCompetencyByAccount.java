package com.picsauditing.actions.report;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.DoubleMap;

@SuppressWarnings("serial")
public class ReportCompetencyByAccount extends ReportCompetencyByEmployee {

	private ContractorAuditDAO auditDAO;

	DoubleMap<Integer, Integer, ContractorAudit> competencyAudits;

	public ReportCompetencyByAccount(ContractorAuditDAO auditDAO) {
		this.auditDAO = auditDAO;
		orderByDefault = "name";
	}

	@Override
	protected void buildQuery() {
		super.buildQuery();
		sql = new SelectSQL("(" + sql.toString() + ") t");
		sql.addGroupBy("accountID");
		
		sql.addJoin("LEFT JOIN contractor_audit ca99 ON ca99.auditTypeID = 99 AND ca99.conID = accountID");
		sql.addJoin("LEFT JOIN contractor_audit ca100 ON ca100.auditTypeID = 100 AND ca100.conID = accountID");

		sql.addField("name");
		sql.addField("accountID");
		sql.addField("count(*) employeeCount");
		sql.addField("sum(skilled) skilled");
		sql.addField("sum(required) required");
		
		sql.addField("ca99.id ca99ID");
		sql.addField("ca99.auditStatus ca99status");
		sql.addField("ca99.creationDate ca99creationDate");
		sql.addField("ca99.completedDate ca99completedDate");
		sql.addField("ca99.expiresDate ca99expiresDate");
		sql.addField("ca100.id ca100ID");
		sql.addField("ca100.auditStatus ca100status");
		sql.addField("ca100.creationDate ca100creationDate");
		sql.addField("ca100.completedDate ca100completedDate");
		sql.addField("ca100.expiresDate ca100expiresDate");
		
		sql.addField("ca100.id ca100ID");

		filter.setShowFirstName(false);
		filter.setShowLastName(false);
		filter.setShowEmail(false);
		filter.setShowSsn(false);
	}

	public DoubleMap<Integer, Integer, ContractorAudit> getCompetencyAudits() {
		if (competencyAudits == null) {
			competencyAudits = new DoubleMap<Integer, Integer, ContractorAudit>();

			for (BasicDynaBean d : data) {
				int conID = Integer.parseInt(d.get("accountID").toString());
				for (ContractorAudit ca : auditDAO.findByContractor(conID)) {
					if (ca.getAuditType().getId() == 99 || ca.getAuditType().getId() == 100) {
						competencyAudits.put(conID, ca.getAuditType().getId(), ca);
					}
				}
			}
		}

		return competencyAudits;
	}
}
