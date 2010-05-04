package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;

@SuppressWarnings("serial")
public class ReportContractorsWithForcedFlags extends ReportAccount {
	
	@Override
	public void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.ForcedFlagsReport);
	}
	
	@Override
	protected void buildQuery() {
		skipPermissions = true;
		super.buildQuery();
		
		String forceFlagsJoin = "JOIN (SELECT conid,opid,forcebegin,forceend,forcedby,forceflag FROM"+ 
		" (SELECT gc.subid as conid, gc.genid as opid, gc.forcebegin, gc.forceend, gc.forcedBy, gc.forceflag FROM generalcontractors gc" +
		" WHERE gc.forceFlag IS NOT NULL" +
		" UNION" +
		" SELECT fdo.conid, fdo.opid, fdo.updateDate as forcebegin, fdo.forceend, fdo.updatedBy as forcedby, fdo.forceflag FROM flag_data_override fdo"+
		" WHERE fdo.forceFlag IS NOT NULL) t"+
		" ) ff ON a.id = ff.conid";
		
		sql.addJoin(forceFlagsJoin);
		sql.addJoin("JOIN accounts o ON o.id = ff.opid");
		sql.addJoin("LEFT JOIN users u ON u.id = ff.forcedBy");
		sql.addJoin("LEFT JOIN accounts fa ON fa.id = u.accountID");
		
		sql.addWhere("a.status = 'Active'");
		sql.addWhere("o.status = 'Active'");
		sql.addWhere("ff.forceFlag IS NOT NULL");
		if(permissions.isOperatorCorporate()) {
			String opIds = " ff.opid = " + permissions.getAccountId() + " OR ";
			if(permissions.isOperator()) 
				opIds += " ff.opid IN (SELECT corporateID from facilities where opID = " + permissions.getAccountId()+")";
			else 
				opIds += " ff.opid IN (SELECT opID from facilities where corporateID = " + permissions.getAccountId()+")";
			sql.addWhere(opIds);
		}
		sql.addField("o.name AS opName");
		sql.addField("o.type AS opType");
		sql.addField("o.id AS opId");
		sql.addField("lower(ff.forceFlag) AS lflag");
		sql.addField("ff.forceend");
		sql.addField("ff.forceBegin");
		sql.addField("u.name AS forcedBy");
		sql.addField("fa.name AS forcedByAccount");
		orderByDefault = "o.name, a.name";
	}
}
