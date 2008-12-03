package com.picsauditing.actions.report;

import java.util.HashSet;
import java.util.Set;

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

	protected OperatorAccountDAO operatorAccountDAO;

	public ReportFlagCriteria(OperatorAccountDAO operatorAccountDAO) {
		this.operatorAccountDAO = operatorAccountDAO;
		getFilter().setShowOperatorSingle(true);
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
	public void buildQuery() {
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
				String year = "1";
				if (name.equals("pqf")) {
					year = "year(pqf.createdDate) = 2008";
				}
				sql.addJoin("LEFT JOIN Contractor_audit " + name + " ON " + name + ".conID = a.id AND " + name
						+ ".auditTypeID = " + auditOperator.getAuditType().getAuditTypeID() + " AND " + name
						+ ".auditStatus IN ('Pending','Submitted','Active') AND " + year);
				sql.addField(name + ".auditStatus AS '" + auditOperator.getAuditType().getAuditName() + " Status'");
				sql.addField(name + ".percentComplete AS '" + auditOperator.getAuditType().getAuditName()
						+ " Completed'");
			}
		}

		boolean avgQuestionChecked = false;
		for (FlagQuestionCriteria flagQuestionCriteria : operatorAccount.getFlagQuestionCriteria()) {
			if (flagQuestionCriteria.getAuditQuestion().getId() == AuditQuestion.EMR_AVG
					&& flagQuestionCriteria.getChecked() == YesNo.Yes)
				avgQuestionChecked = true;
		}

		Set<Integer> questionIds = new HashSet<Integer>();
		for (FlagQuestionCriteria flagQuestionCriteria : operatorAccount.getFlagQuestionCriteria()) {
			if ((flagQuestionCriteria.getChecked() == YesNo.Yes && flagQuestionCriteria.getAuditQuestion()
					.getId() != AuditQuestion.EMR_AVG)
					|| (avgQuestionChecked && AuditQuestion.EMR == flagQuestionCriteria.getAuditQuestion().getId())) {
				int questionID = flagQuestionCriteria.getAuditQuestion().getId();
				if (!questionIds.contains(questionID)) {
					questionIds.add(questionID);
					sql.addPQFQuestion(questionID);
					sql.addField("q" + questionID + ".answer AS verified" + questionID);
				}
			}
		}
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

		if (!permissions.isOperator()) {
			sql.addJoin("LEFT JOIN flags ON flags.conID = a.id AND flags.opID = " + operatorID);
			sql.addField("flags.flag");
			sql.addField("lower(flags.flag) AS lflag");
			if (!sql.hasJoin("generalcontractors gc"))
				sql.addJoin("JOIN generalcontractors gc ON gc.subID = a.id");
			sql.addField("gc.workStatus");
			sql.addWhere("gc.genID = " + operatorID);
		}

		sql.addJoin("LEFT JOIN osha ON osha.conID = a.id AND location = 'Corporate'");
		sql.addWhere("a.active = 'Y'");
		sql.addWhere("flags.flag IN ('Red','Amber')");
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
}
