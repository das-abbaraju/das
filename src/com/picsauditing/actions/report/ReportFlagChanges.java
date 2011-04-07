package com.picsauditing.actions.report;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
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
	private int approveID = 0;
	private List<User> accountManagers;
	private int[] approvedChanges;

	public ReportFlagChanges(ContractorOperatorDAO contractorOperatorDAO) {
		this.contractorOperatorDAO = contractorOperatorDAO;
		setReportName("Flag Changes");
		orderByDefault = "a.name, fd.criteriaID, operator.name";
	}

	@Override
	public String execute() throws Exception {
		if (approveID > 0) {
			ContractorOperator co = contractorOperatorDAO.find(approveID);
			co.resetBaseline(permissions);
			contractorOperatorDAO.save(co);
			return BLANK;
		}
		
		if ("Approve Selected".equals(button)) {
			if (approvedChanges != null) {
				List<ContractorOperator> approvedFlagChanges = contractorOperatorDAO.findWhere("id IN ("+Strings.implode(approvedChanges)+")");
				
				for(ContractorOperator co : approvedFlagChanges) {
					co.resetBaseline(permissions);
					contractorOperatorDAO.save(co);
				}
				
				approvedChanges = null;
			} else {
				addActionError("No Flag Changes were selected for Approval.");
			}
		}
		
		return super.execute();
	}

	@Override
	protected void buildQuery() {

		super.buildQuery();

		getFilter().setShowFlagStatus(true);

		getFilter().setShowTaxID(false);
		getFilter().setShowTrade(false);
		getFilter().setShowCcOnFile(false);
		getFilter().setShowPrimaryInformation(false);
		getFilter().setShowTradeInformation(false);
		getFilter().setShowConWithPendingAudits(false);
		getFilter().setShowAddress(false);
		getFilter().setShowOfficeIn(false);
		getFilter().setShowWorksIn(false);
		getFilter().setShowLicensedIn(false);
		getFilter().setShowIndustry(false);
		getFilter().setShowStatus(false);
		getFilter().setShowAccountManager(true);
		getFilter().setShowBidOnlyFlagChanges(true);
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
				if (child.getOperator().getStatus().isActiveDemo()) {
					ops.add(child.getOperator().getId());
				}
			}
			opIds = Strings.implode(ops, ",");
		}

		sql.addWhere("a.status IN ('Active')");

		sql.addJoin("JOIN generalcontractors gc_flag ON gc_flag.subid = a.id AND gc_flag.flag != gc_flag.baselineFlag");
		sql.addField("gc_flag.id gcID");
		sql.addField("gc_flag.flag");
		sql.addField("gc_flag.baselineFlag");
		sql.addField("gc_flag.baselineApproved");
		sql.addField("gc_flag.baselineApprover");
		sql.addField("gc_flag.flagLastUpdated");
		sql.addField("gc_flag.creationDate");
		
		sql.addField("IFNULL(gc_flag.flagDetail,'{}') flagDetail");
		sql.addField("IFNULL(gc_flag.baselineFlagDetail,'{}') baselineFlagDetail");

		sql.addJoin("JOIN accounts operator ON operator.id = gc_flag.genid AND operator.id NOT IN (10403,2723)");
		sql
				.addJoin("LEFT JOIN flag_data fd ON fd.conID = gc_flag.subID AND fd.opID = gc_flag.genID AND fd.baselineFlag != fd.flag");
		sql.addJoin("LEFT JOIN flag_criteria fc ON fd.criteriaID = fc.id");
		sql
				.addJoin("LEFT JOIN contractor_audit ca ON ca.conID = gc_flag.subID AND ca.auditTypeID = fc.auditTypeID AND ca.expiresDate >= NOW()");
		sql.addJoin("LEFT JOIN contractor_audit_operator cao ON cao.auditID = ca.id AND cao.visible = 1");
		sql.addJoin("LEFT JOIN contractor_audit_operator_workflow caow ON caow.caoID = cao.id");
		sql
				.addJoin("LEFT JOIN contractor_audit_operator_permission caop ON cao.id = caop.caoID AND caop.opID = gc_flag.genID");
		sql
				.addJoin("LEFT JOIN contractor_audit_operator_workflow caow2 ON caow.caoID = caow2.caoID AND caow.updateDate < caow2.updateDate");

		Queue<String> expectedChanges = new LinkedList<String>();

		if (getFilter().isBidOnlyFlagChanges())
			expectedChanges.offer("gc_flag.baselineFlag = 'Clear' OR gc_flag.flag = 'Clear'");
		if (getFilter().isAuditStatusFlagChanges())
			expectedChanges
					.offer("(cao.id IS NOT NULL AND fc.requiredStatus = caow.status AND gc_flag.flag = 'Green') OR "
							+ "(cao.id IS NOT NULL AND fc.requiredStatus != caow.status AND gc_flag.flag != 'Green')");
		if (getFilter().isAuditCreationFlagChanges())
			expectedChanges.offer("cao.id IS NOT NULL AND caow.id IS NULL");
		if (getFilter().isAuditQuestionFlagChanges())
			expectedChanges.offer("fc.questionID IS NOT NULL OR (fc.oshaRateType IS NOT NULL AND fc.oshaType IS NOT NULL AND fc.multiYearScope IS NOT NULL)");

		if(!expectedChanges.isEmpty()) {
			String whereExpected = "(" + expectedChanges.remove() + ")";
			for (String expectedChange : expectedChanges)
				whereExpected += " OR (" + expectedChange + ")";
	
			sql.addWhere(whereExpected);
		}
		
		sql.addWhere("caow2.id IS NULL");

		sql.addGroupBy("gc_flag.id");

		sql.addField("operator.name AS opName");
		sql.addField("operator.id AS opId");
		sql.addWhere("operator.status IN ('Active') AND operator.type = 'Operator'");

		sql.addField("c.membershipDate");
		sql.addField("c.lastRecalculation");

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

	public void setApproveID(int approveID) {
		this.approveID = approveID;
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
}
