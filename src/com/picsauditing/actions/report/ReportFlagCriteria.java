package com.picsauditing.actions.report;

import java.util.HashSet;
import java.util.Set;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.FlagOshaCriteria;
import com.picsauditing.jpa.entities.FlagQuestionCriteria;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.YesNo;

public class ReportFlagCriteria extends ReportAccount {
	private int operatorID;
	private boolean hasFatalities = false;
	private boolean hasTrir = false;
	private boolean hasLwcr = false;
	private OperatorAccount operatorAccount;
	private int year;

	protected OperatorAccountDAO operatorAccountDAO;

	public ReportFlagCriteria(OperatorAccountDAO operatorAccountDAO) {
		this.operatorAccountDAO = operatorAccountDAO;
		getFilter().setShowOperatorSingle(true);
		getFilter().setShowFlagStatus(true);
	}

	@Override
	protected boolean runReport() {
		if (!permissions.isOperator() && getFilter().getOperator() == null) {
			addActionMessage("Please select an Operator");
			return false;
		}
		return super.runReport();
	}

	@Override
	protected void buildQuery() {
		super.buildQuery();

		sql.addField("a.contact");
		sql.addField("a.phone");
		sql.addField("a.email");
		sql.addField("c.main_trade");
		sql.addField("c.trades");
		sql.addField("c.riskLevel");

		if (!permissions.isOperator()) {
			operatorID = getFilter().getOperator()[0];
		} else
			operatorID = permissions.getAccountId();

		operatorAccount = operatorAccountDAO.find(operatorID);
		for (AuditOperator auditOperator : operatorAccount.getAudits()) {
			if (auditOperator.isCanSee() && auditOperator.getMinRiskLevel() > 0) {
				String name = auditOperator.getAuditType().getAuditName().toLowerCase();
				int blank = name.indexOf(" ");
				if (blank > 0)
					name = name.substring(0, blank);
				if (auditOperator.getAuditType().getAuditTypeID() == 11) {
					year = getYear();
					name += year;
					sql.addJoin("LEFT JOIN Contractor_audit " + name + " ON " + name + ".conID = a.id AND " + name
							+ ".auditTypeID = " + auditOperator.getAuditType().getAuditTypeID() + " AND " + name
							+ ".auditStatus IN ('Pending','Submitted','Active') ");
					sql.addField(name + ".auditStatus AS '" + name + " Status'");
					sql.addField(name + ".percentComplete AS '" + name + " Completed'");
					sql.addWhere(name + ".auditFor = " + year);
					sql.addJoin("LEFT JOIN osha_audit AS osha" + year + " ON osha" + year + ".auditID = " + name
							+ ".auditID AND osha" + year + ".location = 'Corporate'");

					year = year - 1;
					name = "annual" + year;
					sql.addJoin("LEFT JOIN Contractor_audit " + name + " ON " + name + ".conID = a.id AND " + name
							+ ".auditTypeID = " + auditOperator.getAuditType().getAuditTypeID() + " AND " + name
							+ ".auditStatus IN ('Pending','Submitted','Active') ");
					sql.addField(name + ".auditStatus AS '" + name + " Status'");
					sql.addField(name + ".percentComplete AS '" + name + " Completed'");
					sql.addWhere(name + ".auditFor = " + year);
					sql.addJoin("LEFT JOIN osha_audit AS osha" + year + " ON osha" + year + ".auditID = " + name
							+ ".auditID AND osha" + year + ".location = 'Corporate'");

					year = year - 1;
					name = "annual" + year;
					sql.addJoin("LEFT JOIN Contractor_audit " + name + " ON " + name + ".conID = a.id AND " + name
							+ ".auditTypeID = " + auditOperator.getAuditType().getAuditTypeID() + " AND " + name
							+ ".auditStatus IN ('Pending','Submitted','Active') ");
					sql.addField(name + ".auditStatus AS '" + name + " Status'");
					sql.addField(name + ".percentComplete AS '" + name + " Completed'");
					sql.addWhere(name + ".auditFor = " + year);
					sql.addJoin("LEFT JOIN osha_audit AS osha" + year + " ON osha" + year + ".auditID = " + name
							+ ".auditID AND osha" + year + ".location = 'Corporate'");

				} else {
					sql.addJoin("LEFT JOIN Contractor_audit " + name + " ON " + name + ".conID = a.id AND " + name
							+ ".auditTypeID = " + auditOperator.getAuditType().getAuditTypeID() + " AND " + name
							+ ".auditStatus IN ('Pending','Submitted','Active') ");
					sql.addField(name + ".auditStatus AS '" + auditOperator.getAuditType().getAuditName() + " Status'");
					sql.addField(name + ".percentComplete AS '" + auditOperator.getAuditType().getAuditName()
							+ " Completed'");
				}
			}
		}

		Set<Integer> questionIds = new HashSet<Integer>();
		for (FlagQuestionCriteria flagQuestionCriteria : operatorAccount.getFlagQuestionCriteria()) {
			if (flagQuestionCriteria.getFlagColor().toString().equals(getFilter().getFlagStatus())
					&& flagQuestionCriteria.getChecked() == YesNo.Yes) {
				int questionID = flagQuestionCriteria.getAuditQuestion().getId();
				if (!questionIds.contains(questionID)) {
					questionIds.add(questionID);
					if (flagQuestionCriteria.getAuditQuestion().getId() == AuditQuestion.EMR) {
						sql.addAnnualQuestion(questionID, false, "answer2008", "annual2008");
						sql.addAnnualQuestion(questionID, false, "answer2007", "annual2007");
						sql.addAnnualQuestion(questionID, false, "answer2006", "annual2006");
					} else {
						sql.addPQFQuestion(questionID);
					}
				}
			}
		}
		// TODO handle the osha for 2008.
		for (FlagOshaCriteria flagOshaCriteria : operatorAccount.getFlagOshaCriteria()) {
			if (flagOshaCriteria.getFlagColor().toString().equals(getFilter().getFlagStatus())) {
				if (!hasFatalities && flagOshaCriteria.getFatalities().isRequired()) {
					hasFatalities = true;
					year = getYear();
					sql.addField("osha" + year + ".fatalities AS fatalities08");
					year = year - 1;
					sql.addField("osha" + year + ".fatalities AS fatalities07");
					year = year - 1;
					sql.addField("osha" + year + ".fatalities AS fatalities06");
				}
				if (!hasTrir && flagOshaCriteria.getTrir().isRequired()) {
					hasTrir = true;
					year = getYear();
					sql.addField("(osha" + year + ".recordableTotal * 200000 / osha" + year + ".manHours) AS trir08");
					year = year - 1;
					sql.addField("(osha" + year + ".recordableTotal * 200000 / osha" + year + ".manHours) AS trir07");
					year = year - 1;
					sql.addField("(osha" + year + ".recordableTotal * 200000 / osha" + year + ".manHours) AS trir06");
				}
				if (!hasLwcr && flagOshaCriteria.getLwcr().isRequired()) {
					hasLwcr = true;
					year = getYear();
					sql.addField("(osha" + year + ".lostWorkCases * 200000 / osha" + year + ".manHours) AS lwcr08");
					year = year - 1;
					sql.addField("(osha" + year + ".lostWorkCases * 200000 / osha" + year + ".manHours) AS lwcr07");
					year = year - 1;
					sql.addField("(osha" + year + ".lostWorkCases * 200000 / osha" + year + ".manHours) AS lwcr06");
				}
			}
		}

		if (!permissions.isOperator()) {
			sql.addJoin("LEFT JOIN flags ON flags.conID = a.id AND flags.opID = " + operatorID);
			sql.addField("flags.flag");
			sql.addField("lower(flags.flag) AS lflag");
			if (!sql.hasJoin("generalcontractors gc"))
				sql.addJoin("JOIN generalcontractors gc ON gc.subID = a.id");
			sql.addField("gc.workStatus");
			sql.addWhere("gc.genID = " + operatorID);
		}

		sql.addWhere("a.active = 'Y'");
		sql.addOrderBy("a.name");
	}

	public boolean isHasFatalities() {
		return hasFatalities;
	}

	public void setHasFatalities(boolean hasFatalities) {
		this.hasFatalities = hasFatalities;
	}

	public boolean isHasTrir() {
		return hasTrir;
	}

	public void setHasTrir(boolean hasTrir) {
		this.hasTrir = hasTrir;
	}

	public boolean isHasLwcr() {
		return hasLwcr;
	}

	public void setHasLwcr(boolean hasLwcr) {
		this.hasLwcr = hasLwcr;
	}

	public OperatorAccount getOperatorAccount() {
		return operatorAccount;
	}

	public void setOperatorAccount(OperatorAccount operatorAccount) {
		this.operatorAccount = operatorAccount;
	}

	public int getYear() {
		return DateBean.getCurrentYear() - 1;
	}
}
