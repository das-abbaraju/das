package com.picsauditing.actions.report;

import java.util.List;
import java.util.Vector;

import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportFlagChanges extends ReportAccount {

	private int approveID = 0;

	public ReportFlagChanges() {
		setReportName("Flag Changes");
		orderByDefault = "a.name, operator.name";
	}

	@Override
	public String execute() throws Exception {
		if (approveID > 0) {
			if (!forceLogin())
				return LOGIN;
			
			String updateSQL = "UPDATE generalcontractors SET baselineFlag = flag WHERE id = " + approveID;
			// TODO execute SQL
			return BLANK;
		}
		return super.execute();
	}

	@Override
	protected void buildQuery() {

		super.buildQuery();

		getFilter().setShowFlagStatus(true);
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
		if (permissions.hasGroup(User.GROUP_CSR)) {
			sql.addWhere("c.welcomeAuditor_id = " + permissions.getUserId());
		}

		sql.addWhere("a.status IN ('Active')");
		
		sql.addJoin("JOIN generalcontractors gc ON gc.subid = a.id AND gc.flag != gc.baselineFlag");
		sql.addField("gc.id gcID");
		sql.addField("gc.flag");
		sql.addField("gc.baselineFlag");
		
		sql.addJoin("JOIN accounts operator on operator.id = gc.genid");
		sql.addField("operator.name AS opName");
		sql.addField("operator.id AS opId");
		sql.addWhere("operator.status IN ('Active') AND operator.type = 'Operator'");
		
		sql.addField("TIMESTAMPDIFF(MINUTE, c.lastRecalculation, NOW()) AS lastRecalculation");

		if (!Strings.isEmpty(opIds))
			sql.addWhere("operator.id in (" + opIds + ")");

	}

	public void setApproveID(int approveID) {
		this.approveID = approveID;
	}
}
