package com.picsauditing.actions.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.ReportFilterContractor;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportFlagChanges extends ReportAccount {
	private ContractorOperatorDAO contractorOperatorDAO;
	private List<User> accountManagers;
	private int[] approvedChanges;
	private int totalFlagChanges;
	private int totalOperatorsAffected;

	public ReportFlagChanges(ContractorOperatorDAO contractorOperatorDAO) {
		this.contractorOperatorDAO = contractorOperatorDAO;
		setReportName("Flag Changes");
		orderByDefault = "baselineEnum, flagEnum DESC, a.name, operator.name";
	}

	@Override
	public String execute() throws Exception {
		if (approvedChanges != null) {
			List<ContractorOperator> approvedFlagChanges = contractorOperatorDAO.findWhere("id IN ("
					+ Strings.implode(approvedChanges) + ")");

			for (ContractorOperator co : approvedFlagChanges) {
				co.resetBaseline(permissions);
				contractorOperatorDAO.save(co);
			}

			approvedChanges = null;
			return BLANK;
		}
		
		totalFlagChanges = contractorOperatorDAO.getTotalFlagChanges();
		totalOperatorsAffected = contractorOperatorDAO.getOperatorsAffectedByFlagChanges();
		
		return super.execute();
	}

	@Override
	protected void buildQuery() {

		super.buildQuery();

		getFilter().setShowFlagStatus(true);

		getFilter().setShowTaxID(false);
		getFilter().setShowCcOnFile(false);
		getFilter().setShowPrimaryInformation(false);
		getFilter().setShowTradeInformation(false);
		getFilter().setShowConWithPendingAudits(false);
		getFilter().setShowLocation(false);
		getFilter().setShowStatus(false);
		getFilter().setShowAccountManager(true);
		getFilter().setShowCaoChangesFlagChanges(true);
		getFilter().setShowAuditCreationFlagChanges(true);
		getFilter().setShowAuditStatusFlagChanges(true);
		getFilter().setShowAuditQuestionFlagChanges(true);

		String opIds = "";
		List<Integer> ops = new Vector<Integer>();

		if (filterOn(getFilter().getOperator())) {
			opIds = Strings.implode(getFilter().getOperator(), ",");
		} else if (permissions.isCorporate()) {
			OperatorAccount corporate = (OperatorAccount) getAccount();
			for (Facility child : corporate.getOperatorFacilities()) {
				if (child.getOperator().getStatus().isActiveOrDemo()) {
					ops.add(child.getOperator().getId());
				}
			}
			opIds = Strings.implode(ops, ",");
		}

		sql.addJoin("JOIN generalcontractors gc_flag ON gc_flag.subid = a.id AND gc_flag.flag != gc_flag.baselineFlag");

		sql.addJoin("JOIN accounts operator ON operator.id = gc_flag.genid AND operator.id NOT IN (10403,2723)");

		if (getFilter().isCaoChangesFlagChanges()) {
			sql.addJoin("JOIN flag_data fd ON fd.conID = gc_flag.subID "
					+ "AND fd.opID = gc_flag.genID AND fd.baselineFlag != fd.flag");
			sql.addJoin("JOIN flag_criteria fc ON fd.criteriaID = fc.id");
			sql.addJoin("JOIN contractor_audit ca ON ca.conID = gc_flag.subID "
					+ "AND ca.auditTypeID = fc.auditTypeID AND ca.expiresDate >= NOW()");
			sql.addJoin("JOIN contractor_audit_operator cao ON cao.auditID = ca.id AND cao.visible = 1");
			sql.addJoin("JOIN contractor_audit_operator_permission caop ON cao.id = caop.caoID "
					+ "AND caop.opID = gc_flag.genID");

			sql.addField("caop.caoID");
			sql.addField("caop.previousCaoID as previousCaoID");
		} else if (getFilter().isAuditStatusFlagChanges() || getFilter().isAuditCreationFlagChanges()
				|| getFilter().isAuditQuestionFlagChanges()) {
			sql.addJoin("LEFT JOIN flag_data fd ON fd.conID = gc_flag.subID "
					+ "AND fd.opID = gc_flag.genID AND fd.baselineFlag != fd.flag");
			sql.addJoin("LEFT JOIN flag_criteria fc ON fd.criteriaID = fc.id");
			sql.addJoin("LEFT JOIN contractor_audit ca ON ca.conID = gc_flag.subID "
					+ "AND ca.auditTypeID = fc.auditTypeID AND ca.expiresDate >= NOW()");
			sql.addJoin("LEFT JOIN contractor_audit_operator cao ON cao.auditID = ca.id AND cao.visible = 1");
			sql.addJoin("LEFT JOIN contractor_audit_operator_workflow caow ON caow.caoID = cao.id");
			sql.addJoin("LEFT JOIN contractor_audit_operator_permission caop ON cao.id = caop.caoID "
					+ "AND caop.opID = gc_flag.genID");
			sql.addJoin("LEFT JOIN contractor_audit_operator_workflow caow2 ON caow.caoID = caow2.caoID "
					+ "AND caow.updateDate < caow2.updateDate");

			List<String> expectedChanges = new ArrayList<String>();

			if (getFilter().isAuditStatusFlagChanges()) {
				expectedChanges.add("(cao.id IS NOT NULL AND fc.requiredStatus = caow.status"
						+ " AND gc_flag.flag = 'Green')");
				expectedChanges.add("(cao.id IS NOT NULL AND fc.requiredStatus != caow.status"
						+ " AND gc_flag.flag != 'Green')");
			}
			if (getFilter().isAuditCreationFlagChanges())
				expectedChanges.add("(cao.id IS NOT NULL AND caow.id IS NULL)");
			if (getFilter().isAuditQuestionFlagChanges()) {
				expectedChanges.add("(fc.questionID IS NOT NULL)");
				expectedChanges.add("(fc.oshaRateType IS NOT NULL AND fc.oshaType IS NOT NULL"
						+ " AND fc.multiYearScope IS NOT NULL)");
			}
			sql.addWhere(Strings.implode(expectedChanges, " OR "));
			sql.addWhere("caow2.id IS NULL");
		}

		sql.addField("IFNULL(gc_flag.flagDetail,'{}') flagDetail");
		sql.addField("IFNULL(gc_flag.baselineFlagDetail,'{}') baselineFlagDetail");
		sql.addField("GROUP_CONCAT(DISTINCT operator.name SEPARATOR ', ') AS opName");
		sql.addField("GROUP_CONCAT(DISTINCT CAST(gc_flag.id as CHAR)) AS gcIDs");
		sql.addField("operator.id AS opId");
		sql.addField("c.membershipDate");
		sql.addField("c.lastRecalculation");
		sql.addField("gc_flag.id gcID");
		sql.addField("gc_flag.flag");
		sql.addField("gc_flag.baselineFlag");
		sql.addField("gc_flag.baselineApproved");
		sql.addField("gc_flag.baselineApprover");
		sql.addField("gc_flag.flagLastUpdated");
		sql.addField("gc_flag.creationDate");
		sql.addField("case when gc_flag.baselineFlag = 'Green' then 1 when gc_flag.baselineFlag is null then "
				+ "2 when gc_flag.baselineFlag = 'Amber' then 3 when gc_flag.baselineFlag = 'Red' then 4 "
				+ "when gc_flag.baselineFlag = 'Clear' then 5 end as `baselineEnum`");
		sql.addField("case when gc_flag.flag = 'Green' then 1 when gc_flag.flag is null then "
				+ "2 when gc_flag.flag = 'Amber' then 3 when gc_flag.flag = 'Red' then 4 "
				+ "when gc_flag.flag = 'Clear' then 5 end as `flagEnum`");
		sql.addWhere("a.status IN ('Active')");
		sql.addWhere("operator.status IN ('Active') AND operator.type = 'Operator'");
		sql.addWhere("a.creationDate < DATE_SUB(NOW(), INTERVAL 2 WEEK)");
		sql.addWhere("gc_flag.baselineFlag != 'Clear'");
		sql.addWhere("gc_flag.flag != 'Clear'");
		sql.addWhere("gc_flag.creationDate < DATE_SUB(NOW(), INTERVAL 2 WEEK)");
		sql.addWhere("gc_flag.forceFlag IS NULL OR NOW() >= gc_flag.forceEnd");
		sql.addGroupBy("c.id, gc_flag.flag, gc_flag.baselineFlag, gc_flag.flagDetail, gc_flag.baselineFlagDetail");

		if (!Strings.isEmpty(opIds))
			sql.addWhere("operator.id in (" + opIds + ")");
	}

	@Override
	protected void addFilterToSQL() {
		super.addFilterToSQL();

		ReportFilterContractor f = getFilter();
		if (filterOn(f.getAccountManager())) {
			String list = Strings.implode(f.getAccountManager(), ",");
			sql.addWhere("gc_flag.genID IN (SELECT accountID FROM account_user WHERE userID IN (" + list
					+ ") AND role = 'PICSAccountRep' AND startDate < NOW() AND endDate > NOW())");
			setFiltered(true);
		}
	}

	public List<User> getAccountManagers() {
		if (accountManagers == null) {
			UserDAO dao = (UserDAO) SpringUtils.getBean("UserDAO");
			accountManagers = dao.findByGroup(User.GROUP_MARKETING);
		}

		return accountManagers;
	}

	public int[] getApprovedChanges() {
		return approvedChanges;
	}

	public void setApprovedChanges(int[] approvedChanges) {
		this.approvedChanges = approvedChanges;
	}

	public int getTotalFlagChanges() {
		return totalFlagChanges;
	}

	public void setTotalFlagChanges(int totalFlagChanges) {
		this.totalFlagChanges = totalFlagChanges;
	}

	public int getTotalOperatorsAffected() {
		return totalOperatorsAffected;
	}

	public void setTotalOperatorsAffected(int totalOperatorsAffected) {
		this.totalOperatorsAffected = totalOperatorsAffected;
	}
}
