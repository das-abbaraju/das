package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.FlagOshaCriteria;
import com.picsauditing.jpa.entities.FlagQuestionCriteria;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.YesNo;

public class ReportFlagCriteria extends ReportAccount {
	private int operatorID;

	protected OperatorAccountDAO operatorAccountDAO;

	public ReportFlagCriteria(OperatorAccountDAO operatorAccountDAO) {
		this.operatorAccountDAO = operatorAccountDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		loadPermissions();

		sql.addField("a.contact");
		sql.addField("a.phone");
		sql.addField("a.email");
		sql.addField("c.main_trade");
		sql.addField("c.trades");
		sql.addField("pqf.auditStatus AS pqfStatus");
		sql.addField("pqf.percentComplete AS pqfCompleted");
		sql.addField("desktop.auditStatus AS desktopStatus");

		if (permissions.hasPermission(OpPerms.AllOperators)) {
			if (operator == null)
				operatorID = 1813;
			else
				operatorID = operator[0];
		} else
			operatorID = permissions.getAccountId();

		OperatorAccount operatorAccount = operatorAccountDAO.find(operatorID);
		for (AuditOperator auditOperator : operatorAccount.getAudits()) {
			String name = auditOperator.getAuditType().getAuditName().toLowerCase();
			int blank = name.indexOf(" ");
			if (blank > 0)
				name = name.substring(0, blank);
			String year = "1";
			if (name.equals("pqf")) {
				year = "year(pqf.createdDate) = 2008";
			}
			sql.addJoin("LEFT JOIN Contractor_audit " + name + " ON " + name + ".conID = a.id AND " + name
					+ ".auditTypeID = " + auditOperator.getAuditType().getAuditTypeID()
					+ " AND pqf.AuditStatus IN ('Pending','Submitted','Active') AND " + year);
		}

		for (FlagQuestionCriteria flagQuestionCriteria : operatorAccount.getFlagQuestionCriteria()) {
			if (flagQuestionCriteria.getChecked() == YesNo.Yes) {
				int questionID = flagQuestionCriteria.getAuditQuestion().getQuestionID();
				sql.addPQFQuestion(questionID);
				sql.addField("q" + questionID + ".verifiedAnswer AS emr_" + questionID);
			}
		}
		boolean hasFatalities = false;
		boolean hasTrir = false;
		boolean hasLwcr = false;
		// TODO handle the osha for 2008.
		for (FlagOshaCriteria flagOshaCriteria : operatorAccount.getFlagOshaCriteria()) {
			if (!hasFatalities && flagOshaCriteria.getFatalities().isRequired()) {
				hasFatalities = true;
				sql.addField("osha.fatalities1 AS fatalities07");
				sql.addField("osha.fatalities2 AS fatalities06");
				sql.addField("osha.fatalities3 AS fatalities05");
			}
			if (!hasTrir && flagOshaCriteria.getTrir().isRequired()) {
				hasTrir = true;
				sql.addField("(osha.recordableTotal1 * 200000 / osha.manHours1) AS trir07");
				sql.addField("(osha.recordableTotal2 * 200000 / osha.manHours2) AS trir06");
				sql.addField("(osha.recordableTotal3 * 200000 / osha.manHours3) AS trir05");
			}
			if (!hasLwcr && flagOshaCriteria.getLwcr().isRequired()) {
				hasLwcr = true;
				sql.addField("(osha.lostWorkCases1 * 200000 / osha.manHours1) AS lwcr07");
				sql.addField("(osha.lostWorkCases2 * 200000 / osha.manHours2) AS lwcr06");
				sql.addField("(osha.lostWorkCases3 * 200000 / osha.manHours3) AS lwcr05");
			}
		}

		sql.addJoin("LEFT JOIN osha ON osha.conID = a.id AND location = 'Corporate'");
		sql.addWhere("a.active = 'Y'");
		sql.addWhere("flags.flag IN ('Red','Amber')");
		return super.execute();
	}
}
