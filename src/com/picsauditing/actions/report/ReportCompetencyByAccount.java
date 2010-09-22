package com.picsauditing.actions.report;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.ContractorAudit;
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
		// sql = new SelectSQL("(" + sql.toString() + ") t");
		// sql.addGroupBy("accountID");
		//
		// sql.addJoin("LEFT JOIN contractor_audit ca99 ON ca99.auditTypeID = 99 AND ca99.conID = accountID");
		// sql.addJoin("LEFT JOIN contractor_audit ca100 ON ca100.auditTypeID = 100 AND ca100.conID = accountID");
		//
		// sql.addField("t.name");
		// sql.addField("t.accountID");
		// sql.addField("COUNT(*) employeeCount");
		// sql.addField("SUM(skilled) skilled");
		// sql.addField("SUM(required) required");
		//
		// sql.addField("ca99.id ca99ID");
		// sql.addField("ca99.auditStatus ca99status");
		// sql.addField("ca99.creationDate ca99creationDate");
		// sql.addField("ca99.completedDate ca99completedDate");
		// sql.addField("ca99.expiresDate ca99expiresDate");
		// sql.addField("ca100.id ca100ID");
		// sql.addField("ca100.auditStatus ca100status");
		// sql.addField("ca100.creationDate ca100creationDate");
		// sql.addField("ca100.completedDate ca100completedDate");
		// sql.addField("ca100.expiresDate ca100expiresDate");

		String sqlString = "select a.id, a.name, IFNULL(jr.total,0) jobRoleCount, IFNULL(e.total,0) employeeCount, IFNULL(totalComp.competencyTotal,0) required, IFNULL(totalComp.skilledTotal,0) skilled,  ca99.id ca99ID, cao99.status ca99status, ca99.creationDate ca99creationDate, ca99.completedDate ca99completedDate, ca99.expiresDate ca99expiresDate, ca100.id ca100ID, cao100.status ca100status, ca100.creationDate ca100creationDate, ca100.completedDate ca100completedDate, ca100.expiresDate ca100expiresDate "
				+ "from accounts a "
				+ "JOIN contractor_tag ct ON a.id = ct.conID and ct.tagID = 142 "
				+ "left join (select accountID, count(*) total FROM job_role group by accountID) jr on jr.accountID = a.id "
				+ "left join (SELECT e2.accountID, count(*) as total from (select e1.accountID, e1.id, count(*) as totalComp from employee e1 join employee_competency ec on ec.employeeID = e1.id where e1.active = 1 and ec.skilled = 1 group by e1.id) e2 group by e2.accountID) e on e.accountID = a.id "
				+ "left join ( "
				+ "select accountID, count(competencyID) competencyTotal, count(skilled) skilledTotal from ( "
				+ "select e.id, e.accountID, jc.competencyID, ec.id skilled FROM employee e "
				+ "join employee_role er on er.employeeID = e.id "
				+ "JOIN job_competency jc ON jc.jobRoleID = er.jobRoleID "
				+ "LEFT JOIN employee_competency ec ON ec.competencyID = jc.competencyID AND e.id = ec.employeeID and ec.skilled = 1 "
				+ "group by e.id, jc.competencyID "
				+ ") e group by e.accountID) totalComp on totalComp.accountID = a.id "
				+ "LEFT JOIN contractor_audit ca99 ON ca99.auditTypeID = 99 AND ca99.conID = a.id "
				+ "LEFT JOIN contractor_audit_operator cao99 ON cao99.auditID = ca99.id AND cao99.visible = 1 "
				+ "LEFT JOIN contractor_audit ca100 ON ca100.auditTypeID = 100 AND ca100.conID = a.id "
				+ "LEFT JOIN contractor_audit_operator cao100 ON cao100.auditID = ca100.id AND cao100.visible = 1 "
				+ "ORDER BY name";

		sql.setFullClause(sqlString);

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
