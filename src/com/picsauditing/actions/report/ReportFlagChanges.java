package com.picsauditing.actions.report;

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

	private int approveID = 0;

	private List<User> accountManagers;

	public ReportFlagChanges() {
		setReportName("Flag Changes");
		orderByDefault = "a.name, operator.name";
	}

	@Override
	public String execute() throws Exception {
		if (approveID > 0) {
			if (!forceLogin())
				return LOGIN;

			ContractorOperatorDAO dao = (ContractorOperatorDAO) SpringUtils.getBean("ContractorOperatorDAO");
			ContractorOperator co = dao.find(approveID);
			co.resetBaseline(permissions);
			dao.save(co);
			return BLANK;
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
		getFilter().setShowOperator(false);

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
		sql.addField("IFNULL(gc_flag.flagDetail,'{}') flagDetail");
		sql.addField("IFNULL(gc_flag.baselineFlagDetail,'{}') baselineFlagDetail");

		sql.addJoin("JOIN accounts operator on operator.id = gc_flag.genid");
		sql.addField("operator.name AS opName");
		sql.addField("operator.id AS opId");
		sql.addWhere("operator.status IN ('Active') AND operator.type = 'Operator'");

		sql.addField("c.membershipDate");
		sql.addField("TIMESTAMPDIFF(MINUTE, c.lastRecalculation, NOW()) AS lastRecalculation");

		if (!Strings.isEmpty(opIds))
			sql.addWhere("operator.id in (" + opIds + ")");

	}

	@Override
	protected void addFilterToSQL() {
		super.addFilterToSQL();
		
		ReportFilterContractor f = getFilter();
		if (filterOn(f.getAccountManager())) {
			String list = Strings.implode(f.getAccountManager(), ",");
			sql.addWhere("gc_flag.genID IN (SELECT accountID FROM account_user WHERE userID IN (" + list + ") AND role = 'PICSAccountRep' AND startDate < NOW() AND endDate > NOW())");
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
}
