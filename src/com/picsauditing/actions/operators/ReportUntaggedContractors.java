package com.picsauditing.actions.operators;

import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.report.ReportAccount;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportUntaggedContractors extends ReportAccount {

	private int opID = 0;
	private OperatorAccount operator;
	private OperatorAccountDAO operatorAccountDAO;

	public ReportUntaggedContractors(OperatorAccountDAO operatorAccountDAO) {
		this.operatorAccountDAO = operatorAccountDAO;
	}

	@Override
	protected void buildQuery() {
		super.buildQuery();

		String whereClause = "";
		int counter = 0;
		for (String tagSet : operator.getRequiredTags().split("\\|")) {
			if (!Strings.isEmpty(tagSet)) {
				if (counter > 0)
					whereClause += " OR ";
				counter++;
				sql.addJoin("LEFT JOIN contractor_tag t" + counter + " ON t" + counter + ".conID = a.id AND t"
						+ counter + ".tagID IN (" + tagSet + ")");
				whereClause += "t" + counter + ".id IS NULL";
			}
		}
		sql.addWhere(whereClause);
	}

	@Override
	public String execute() throws Exception {
		loadPermissions();
		if (!permissions.hasPermission(OpPerms.AllOperators))
			opID = permissions.getAccountId();

		if (opID == 0) {
			addActionMessage("OperatorID is required");
			return BLANK;
		}

		operator = operatorAccountDAO.find(opID);
		if (Strings.isEmpty(operator.getRequiredTags())) {
			addActionMessage("No Required Tags are defined. Please contact PICS to configure this option.");
			return BLANK;
		}

		return super.execute();
	}

	@Override
	protected void checkPermissions() throws Exception {
		super.checkPermissions();
		permissions.tryPermission(OpPerms.ContractorTags);
	}

	public int getOpID() {
		return opID;
	}

	public void setOpID(int opID) {
		this.opID = opID;
	}

}
