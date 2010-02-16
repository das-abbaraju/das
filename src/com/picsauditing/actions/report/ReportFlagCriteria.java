package com.picsauditing.actions.report;

import java.util.HashSet;
import java.util.Set;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.FlagOshaCriteria;
import com.picsauditing.jpa.entities.FlagQuestionCriteria;
import com.picsauditing.jpa.entities.MultiYearScope;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.excel.ExcelCellType;
import com.picsauditing.util.excel.ExcelColumn;

public class ReportFlagCriteria extends ReportAccount {
	private int operatorID;
	private boolean hasFatalities = false;
	private boolean hasTrir = false;
	private boolean hasLwcr = false;
	private boolean hasTrirAvg = false;
	private boolean hasLwcrAvg = false;
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
		if(getFilter().getFlagStatus() == null) {
			addActionMessage("Please select one Flag Color");
			return false;
		}
		
		return super.runReport();
	}

	protected String sanitize(String input) {

		int blank = input.indexOf(" ");
		if (blank > 0)
		
			input = input.substring(0, blank);

		return input.toLowerCase().replaceAll("'", "_apos_").replace("&", "_and_").replace("/", "_");

	}

	@Override
	protected void buildQuery() {
		super.buildQuery();

		sql.addField("a.contact");
		sql.addField("a.phone");
		sql.addField("a.email");
		sql.addField("c.main_trade");
		sql.addField("c.riskLevel");
		sql.addField("c.emrAverage");
		sql.addField("c.trirAverage");
		sql.addField("c.lwcrAverage");

		if (!permissions.isOperator()) {
			operatorID = getFilter().getOperator()[0];
		} else
			operatorID = permissions.getAccountId();

		operatorAccount = operatorAccountDAO.find(operatorID);
		for (AuditOperator auditOperator : operatorAccount.getVisibleAudits()) {
			if (auditOperator.getMinRiskLevel() > 0) {
				String name = auditOperator.getAuditType().getAuditName();
				name = sanitize(name);
				if (auditOperator.getAuditType().getId() == 11) {
					year = getYear();
					name += year;
					sql.addJoin("LEFT JOIN Contractor_audit " + name + " ON " + name + ".conID = a.id AND " + name
							+ ".auditTypeID = " + auditOperator.getAuditType().getId() + " AND " + name
							+ ".auditStatus IN ('Pending','Incomplete','Submitted','Active') AND " + name + ".auditFor = " + year);
					sql.addField(name + ".auditStatus AS '" + name + " Status'");
					sql.addField(name + ".percentComplete AS '" + name + " Completed'");
					sql.addJoin("LEFT JOIN osha_audit AS osha" + year + " ON osha" + year + ".auditID = " + name
							+ ".id AND osha" + year + ".location = 'Corporate' AND osha" + year + ".SHAType = 'OSHA' ");

					year = year - 1;
					name = "annual" + year;
					sql.addJoin("LEFT JOIN Contractor_audit " + name + " ON " + name + ".conID = a.id AND " + name
							+ ".auditTypeID = " + auditOperator.getAuditType().getId() + " AND " + name
							+ ".auditStatus IN ('Pending','Incomplete','Submitted','Active') AND " + name + ".auditFor = " + year);
					sql.addField(name + ".auditStatus AS '" + name + " Status'");
					sql.addField(name + ".percentComplete AS '" + name + " Completed'");
					sql.addJoin("LEFT JOIN osha_audit AS osha" + year + " ON osha" + year + ".auditID = " + name
							+ ".id AND osha" + year + ".location = 'Corporate' AND osha" + year + ".SHAType = 'OSHA'");

					year = year - 1;
					name = "annual" + year;
					sql.addJoin("LEFT JOIN Contractor_audit " + name + " ON " + name + ".conID = a.id AND " + name
							+ ".auditTypeID = " + auditOperator.getAuditType().getId() + " AND " + name
							+ ".auditStatus IN ('Pending','Incomplete','Submitted','Active') AND " + name + ".auditFor = " + year);
					sql.addField(name + ".auditStatus AS '" + name + " Status'");
					sql.addField(name + ".percentComplete AS '" + name + " Completed'");
					sql.addJoin("LEFT JOIN osha_audit AS osha" + year + " ON osha" + year + ".auditID = " + name
							+ ".id AND osha" + year + ".location = 'Corporate' AND osha" + year + ".SHAType = 'OSHA'");

					year = year - 1;
					name = "annual" + year;
					sql.addJoin("LEFT JOIN Contractor_audit " + name + " ON " + name + ".conID = a.id AND " + name
							+ ".auditTypeID = " + auditOperator.getAuditType().getId() + " AND " + name
							+ ".auditStatus IN ('Pending','Incomplete','Submitted','Active') AND " + name + ".auditFor = " + year);
					sql.addField(name + ".auditStatus AS '" + name + " Status'");
					sql.addField(name + ".percentComplete AS '" + name + " Completed'");
					sql.addJoin("LEFT JOIN osha_audit AS osha" + year + " ON osha" + year + ".auditID = " + name
							+ ".id AND osha" + year + ".location = 'Corporate' AND osha" + year + ".SHAType = 'OSHA'");
				} else {
					if (auditOperator.getAuditType().getId() > 1)
						name += auditOperator.getAuditType().getId();
					sql.addJoin("LEFT JOIN Contractor_audit " + name + " ON " + name + ".conID = a.id AND " + name
							+ ".auditTypeID = " + auditOperator.getAuditType().getId() + " AND " + name
							+ ".auditStatus IN ('Pending','Incomplete','Submitted','Active') ");
					if (auditOperator.getAuditType().getClassType().isPolicy()) {
						sql.addJoin("LEFT JOIN Contractor_audit_operator cao" + name + " ON " + "cao" + name
								+ ".auditID = " + name + ".id " + " AND cao" + name
								+ ".opID = (SELECT inheritInsuranceCriteria from operators where id = "
								+ operatorID + " )" + " AND cao" + name + ".visible = 1");
						sql.addField("cao" + name + ".status AS '" + auditOperator.getAuditType().getAuditName()
								+ " Status'");
					} else {
						sql.addField(name + ".auditStatus AS '" + auditOperator.getAuditType().getAuditName()
								+ " Status'");
					}
					sql.addField(name + ".percentComplete AS '" + auditOperator.getAuditType().getAuditName()
							+ " Completed'");
				}
			}
		}
		Set<Integer> questionIds = new HashSet<Integer>();
		for (FlagQuestionCriteria flagQuestionCriteria : operatorAccount.getInheritFlagCriteria()
				.getFlagQuestionCriteria()) {
			if (flagQuestionCriteria.getFlagColor().toString().equals(getFilter().getFlagStatus()[0])
					&& flagQuestionCriteria.isChecked()
					&& !flagQuestionCriteria.getAuditQuestion().getAuditType().getClassType().isPolicy()) {
				int questionID = flagQuestionCriteria.getAuditQuestion().getId();
				if (!questionIds.contains(questionID)) {
					questionIds.add(questionID);
					if (flagQuestionCriteria.getAuditQuestion().getId() == AuditQuestion.EMR) {
						year = getYear();
						sql.addAnnualQuestion(questionID, false, "answer" + year, "annual" + year);
						year = year - 1;
						sql.addAnnualQuestion(questionID, false, "answer" + year, "annual" + year);
						year = year - 1;
						sql.addAnnualQuestion(questionID, false, "answer" + year, "annual" + year);
						year = year - 1;
						sql.addAnnualQuestion(questionID, false, "answer" + year, "annual" + year);
					} else {
						sql.addPQFQuestion(questionID);
					}
				}
			}
		}
		// TODO handle the osha for 2008.
		for (FlagOshaCriteria flagOshaCriteria : operatorAccount.getInheritFlagCriteria().getFlagOshaCriteria()) {
			if (flagOshaCriteria.getFlagColor().toString().equals(getFilter().getFlagStatus()[0])
					&& flagOshaCriteria.isRequired()) {
				if (!hasFatalities && flagOshaCriteria.getFatalities().isRequired()) {
					hasFatalities = true;
					year = getYear();
					sql.addField("osha" + year + ".fatalities AS fatalities" + year);
					year = year - 1;
					sql.addField("osha" + year + ".fatalities AS fatalities" + year);
					year = year - 1;
					sql.addField("osha" + year + ".fatalities AS fatalities" + year);
					year = year - 1;
					sql.addField("osha" + year + ".fatalities AS fatalities" + year);
				}
				if (!hasTrir && !hasTrirAvg && flagOshaCriteria.getTrir().isRequired()) {
					if(flagOshaCriteria.getTrir().isTimeAverage()) {
						hasTrirAvg = true;
					}
					else {
						hasTrir = true;
						year = getYear();
						sql.addField("(osha" + year + ".recordableTotal * 200000 / osha" + year + ".manHours) AS trir"
								+ year);
						year = year - 1;
						sql.addField("(osha" + year + ".recordableTotal * 200000 / osha" + year + ".manHours) AS trir"
								+ year);
						year = year - 1;
						sql.addField("(osha" + year + ".recordableTotal * 200000 / osha" + year + ".manHours) AS trir"
								+ year);
						year = year - 1;
						sql.addField("(osha" + year + ".recordableTotal * 200000 / osha" + year + ".manHours) AS trir"
								+ year);
						}
				}
				if (!hasLwcr && !hasLwcrAvg && flagOshaCriteria.getLwcr().isRequired()) {
					if(flagOshaCriteria.getLwcr().isTimeAverage()) {
						hasLwcrAvg = true;
					}
					else {
						hasLwcr = true;
						year = getYear();
						sql
								.addField("(osha" + year + ".lostWorkCases * 200000 / osha" + year + ".manHours) AS lwcr"
										+ year);
						year = year - 1;
						sql
								.addField("(osha" + year + ".lostWorkCases * 200000 / osha" + year + ".manHours) AS lwcr"
										+ year);
						year = year - 1;
						sql
								.addField("(osha" + year + ".lostWorkCases * 200000 / osha" + year + ".manHours) AS lwcr"
										+ year);
						year = year - 1;
						sql
								.addField("(osha" + year + ".lostWorkCases * 200000 / osha" + year + ".manHours) AS lwcr"
										+ year);
					}
				}
			}
		}

		if (!permissions.isOperator()) {
			if (!sql.hasJoin("generalcontractors gc"))
				sql.addJoin("JOIN generalcontractors gc ON gc.subID = a.id");
			sql.addField("gc.workStatus");
			sql.addField("gc.flag");
			sql.addField("lower(gc.flag) AS lflag");
			sql.addWhere("gc.genID = " + operatorID);
		}

		sql.addWhere("a.status IN ('Active','Demo')");
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

	public boolean isHasTrirAvg() {
		return hasTrirAvg;
	}

	public void setHasTrirAvg(boolean hasTrirAvg) {
		this.hasTrirAvg = hasTrirAvg;
	}

	public boolean isHasLwcrAvg() {
		return hasLwcrAvg;
	}

	public void setHasLwcrAvg(boolean hasLwcrAvg) {
		this.hasLwcrAvg = hasLwcrAvg;
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

	public int getOperatorID() {
		return operatorID;
	}

	public void setOperatorID(int operatorID) {
		this.operatorID = operatorID;
	}

	@Override
	protected void addExcelColumns() {
		super.addExcelColumns();
		if (!permissions.isOperator())
			excelSheet.addColumn(new ExcelColumn("flag", "Flag", ExcelCellType.String), 30);
		int i = 40;
		for (AuditOperator aOperator : operatorAccount.getVisibleAudits()) {
			if (aOperator.getMinRiskLevel() > 0) {
				String name = aOperator.getAuditType().getAuditName();
				name = sanitize(name);

				if (aOperator.getAuditType().getId() == 11) {
					year = getYear();
					name += year;
					excelSheet.addColumn(new ExcelColumn(name + " Status", name + " Status", ExcelCellType.String), i++);

					year = year - 1;
					name = "annual" + year;
					excelSheet
							.addColumn(new ExcelColumn(name + " Status", name + " Status", ExcelCellType.String), i++);

					year = year - 1;
					name = "annual" + year;
					excelSheet
							.addColumn(new ExcelColumn(name + " Status", name + " Status", ExcelCellType.String), i++);

					year = year - 1;
					name = "annual" + year;
					excelSheet
							.addColumn(new ExcelColumn(name + " Status", name + " Status", ExcelCellType.String), i++);
				} else {
					if (aOperator.getAuditType().getId() > 1)
						name += aOperator.getAuditType().getId();
					excelSheet.addColumn(new ExcelColumn(aOperator.getAuditType().getAuditName() + " Status", aOperator
							.getAuditType().getAuditName()
							+ " Status", ExcelCellType.String), i++);
					i++;
				}
			}
		}
		for (FlagQuestionCriteria flagQuestionCriteria : operatorAccount.getInheritFlagCriteria()
				.getFlagQuestionCriteria()) {
			if (flagQuestionCriteria.getFlagColor().toString().equals(getFilter().getFlagStatus()[0])
					&& flagQuestionCriteria.isChecked()
					&& !flagQuestionCriteria.getAuditQuestion().getAuditType().getClassType().isPolicy()) {
				int questionID = flagQuestionCriteria.getAuditQuestion().getId();
				if (flagQuestionCriteria.getAuditQuestion().getId() == AuditQuestion.EMR) {
					if(flagQuestionCriteria.getMultiYearScope().equals(MultiYearScope.ThreeYearAverage)) {
						excelSheet.addColumn(new ExcelColumn("emrAverage", "EMR Average", ExcelCellType.Double), i++);
					} else {	
						year = getYear();
						excelSheet.addColumn(new ExcelColumn("answer" + year, "EMR" + year, ExcelCellType.String), i++);
						if(flagQuestionCriteria.getMultiYearScope().equals(MultiYearScope.AllThreeYears)) {
							year = year - 1;
							excelSheet.addColumn(new ExcelColumn("answer" + year, "EMR" + year, ExcelCellType.String), i++);
							year = year - 1;
							excelSheet.addColumn(new ExcelColumn("answer" + year, "EMR" + year, ExcelCellType.String), i++);
							year = year - 1;
							excelSheet.addColumn(new ExcelColumn("answer" + year, "EMR" + year, ExcelCellType.String), i++);
						}
					}
				} else {
					excelSheet.addColumn(new ExcelColumn("answer" + questionID, flagQuestionCriteria.getAuditQuestion()
							.getColumnHeaderOrQuestion(), ExcelCellType.String), i);
					i++;
				}
			}
		}
		for (FlagOshaCriteria flagOshaCriteria : operatorAccount.getInheritFlagCriteria().getFlagOshaCriteria()) {
			if (flagOshaCriteria.getFlagColor().toString().equals(getFilter().getFlagStatus()[0])
					&& flagOshaCriteria.isRequired()) {
				if (flagOshaCriteria.getFatalities().isRequired()) {
					year = getYear();
					excelSheet.addColumn(
							new ExcelColumn("fatalities" + year, "fatalities" + year, ExcelCellType.String), i++);
					year = year - 1;
					excelSheet.addColumn(
							new ExcelColumn("fatalities" + year, "fatalities" + year, ExcelCellType.String), i++);
					year = year - 1;
					excelSheet.addColumn(
							new ExcelColumn("fatalities" + year, "fatalities" + year, ExcelCellType.String), i++);
					year = year - 1;
					excelSheet.addColumn(
							new ExcelColumn("fatalities" + year, "fatalities" + year, ExcelCellType.String), i++);
				}
				if (flagOshaCriteria.getTrir().isRequired()) {
					if(flagOshaCriteria.getTrir().isTimeAverage()) {
						excelSheet.addColumn(new ExcelColumn("trirAverage", "trir Average", ExcelCellType.Double), i++);
					}
					else {
						year = getYear();
						excelSheet.addColumn(new ExcelColumn("trir" + year, "trir" + year, ExcelCellType.String), i++);
						year = year - 1;
						excelSheet.addColumn(new ExcelColumn("trir" + year, "trir" + year, ExcelCellType.String), i++);
						year = year - 1;
						excelSheet.addColumn(new ExcelColumn("trir" + year, "trir" + year, ExcelCellType.String), i++);
						year = year - 1;
						excelSheet.addColumn(new ExcelColumn("trir" + year, "trir" + year, ExcelCellType.String), i++);
					}
				}
				if (flagOshaCriteria.getLwcr().isRequired()) {
					if(flagOshaCriteria.getLwcr().isTimeAverage()) {
						excelSheet.addColumn(new ExcelColumn("lwcrAverage", "lwcr Average", ExcelCellType.Double), i++);
					}
					else {
						year = getYear();
						excelSheet.addColumn(new ExcelColumn("lwcr" + year, "lwcr" + year, ExcelCellType.String), i++);
						year = year - 1;
						excelSheet.addColumn(new ExcelColumn("lwcr" + year, "lwcr" + year, ExcelCellType.String), i++);
						year = year - 1;
						excelSheet.addColumn(new ExcelColumn("lwcr" + year, "lwcr" + year, ExcelCellType.String), i++);
						year = year - 1;
						excelSheet.addColumn(new ExcelColumn("lwcr" + year, "lwcr" + year, ExcelCellType.String), i++);
					}
				}
			}
		}
	}
}
