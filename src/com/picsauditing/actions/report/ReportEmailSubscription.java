package com.picsauditing.actions.report;


@SuppressWarnings("serial")
public class ReportEmailSubscription extends ReportAccount {
	
	@Override
	public void buildQuery() {
		skipPermissions = true;
		
		sql.setType(null);
		sql.addJoin("JOIN users u on u.accountID = a.id");
		sql.addJoin("JOIN email_subscription es on es.userID = u.id");
		sql.addField("u.name AS username");
		sql.addField("es.subscription");
		sql.addField("timePeriod");
		sql.addWhere("u.isActive = 'Yes'");
		sql.addWhere("a.status IN ('Active','Demo')");
		sql.addField("a.name");
	}
}
