package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;


/**
 * Used by operators to search for new contractors
 * @author Trevor
 *
 */
public class ReportNewContractorSearch extends ReportAccount {
	protected boolean inParentCorporation = false;
	
	public ReportNewContractorSearch() {
		this.skipPermissions = true;
		this.filtered = true;
	}
	
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		permissions.tryPermission(OpPerms.SearchContractors);
		
		if (permissions.isOperator()) {
			// Anytime we query contractor accounts as an operator,
			// get the flag color/status at the same time
			sql.addJoin("LEFT JOIN flags ON flags.conID = a.id AND flags.opID = " + permissions.getAccountId());
			sql.addField("flags.flag");
			sql.addField("lower(flags.flag) AS lflag");
			sql.addJoin("LEFT JOIN generalcontractors gc ON gc.subID = a.id AND gc.genID = " + permissions.getAccountId());
			sql.addField("gc.genID");
			sql.addField("gc.workStatus");
		}


		if ((accountName == null || DEFAULT_NAME.equals(accountName) || accountName.length() < 3) &&
			(trade == null || trade.length == 0)) {
			this.addActionMessage("Please enter a contractor name with atleast 3 characters or select a trade");
			return SUCCESS;
		}
		if (this.orderBy == null || orderBy.length() == 0)
			this.orderBy = "a.name";
		
		sql.addField("a.contact");
		sql.addField("a.city");
		sql.addField("a.state");
		sql.addField("a.phone");
		sql.addField("a.phone2");
		sql.addWhere("a.active = 'Y'");
		return super.execute();
	}

	public boolean isInParentCorporation() {
		return inParentCorporation;
	}

	public void setInParentCorporation(boolean inParentCorporation) {
		filtered = true;
		
		this.inParentCorporation = inParentCorporation;
	}

}
