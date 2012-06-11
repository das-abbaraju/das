package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.util.Strings;
import com.picsauditing.util.excel.ExcelCellType;
import com.picsauditing.util.excel.ExcelColumn;

@SuppressWarnings("serial")
public class ReportContractorsWithForcedFlags extends ReportAccount {
	private static AccountStatus[] initialAccountStatus = {AccountStatus.Active, AccountStatus.Demo, AccountStatus.Pending };
	
	@Override
	public void prepare() throws Exception {
		super.prepare();

		getFilter().setShowStatus(true);
		getFilter().setShowInsuranceLimits(false);
		getFilter().setShowOpertorTagName(false);
		getFilter().setShowFlagOverrideHistory(true);
		getFilter().setStatus(initialAccountStatus);
	}
	
	@Override
	public void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.ForcedFlagsReport);
	}
	
	@Override
	protected void buildQuery() {
		skipPermissions = true;
		super.buildQuery();
		
		String forceFlagsJoin = "JOIN (SELECT conid,opid,forcebegin,forceend,forcedby,forceflag,label, flagActive FROM"+ 
		" (SELECT gc.subid as conid, gc.genid as opid, gc.forcebegin, gc.forceend, gc.forcedBy, gc.forceflag, 'FlagCriteria.Overall' AS label, 'ReportContractorsWithForcedFlags.FlagStatus.Active' AS flagActive FROM generalcontractors gc" +
		" WHERE gc.forceFlag IS NOT NULL" +
		" UNION" +
		" SELECT fdo.conid, fdo.opid, fdo.updateDate as forcebegin, fdo.forceend, fdo.updatedBy as forcedby, fdo.forceflag, CONCAT('FlagCriteria.', fc1.id, '.label') as label, 'ReportContractorsWithForcedFlags.FlagStatus.Active' AS flagActive FROM flag_data_override fdo"+
		" JOIN flag_criteria fc1 ON fdo.criteriaID = fc1.id"+
		" WHERE fdo.forceFlag IS NOT NULL";
		
		if (getFilter().isFlagOverrideHistory()) {
			forceFlagsJoin += " UNION" +
			" SELECT foh.conid, foh.opid, foh.forcebegin as forcebegin, foh.updateDate as forceend, foh.forceBy as forcedby, foh.forceflag, 'FlagCriteria.Overall' as label , " +
			" CASE foh.deleted WHEN 0 THEN 'ReportContractorsWithForcedFlags.FlagStatus.Expired' ELSE 'ReportContractorsWithForcedFlags.FlagStatus.Canceled' END AS flagActive FROM flag_override_history foh " +
			" WHERE foh.criteriaID is NULL" +
			" UNION" +
			" SELECT foh.conid, foh.opid, foh.forcebegin as forcebegin, foh.updateDate as forceend, foh.forceBy as forcedby, foh.forceflag, CONCAT('FlagCriteria.', fc1.id, '.label') as label, " +
			" CASE foh.deleted WHEN 0 THEN 'ReportContractorsWithForcedFlags.FlagStatus.Expired' ELSE 'ReportContractorsWithForcedFlags.FlagStatus.Canceled' END AS flagActive FROM flag_override_history foh" + 
			" JOIN flag_criteria fc1 ON foh.criteriaID = fc1.id ";
		}
		
		forceFlagsJoin += " ) t ) ff ON a.id = ff.conid";
		
		sql.addJoin(forceFlagsJoin);
		sql.addJoin("JOIN accounts o ON o.id = ff.opid");
		sql.addJoin("LEFT JOIN users u ON u.id = ff.forcedBy");
		sql.addJoin("LEFT JOIN accounts fa ON fa.id = u.accountID");
		sql.addJoin("LEFT JOIN generalcontractors gc ON gc.genid = o.id and gc.subid = ff.conid");
		sql.addWhere("ff.forceFlag IS NOT NULL");
		if(permissions.isOperatorCorporate()) {
			String opIds = " ff.opid = " + permissions.getAccountId() + " OR ";
			if(permissions.isOperator()) 
				opIds += " ff.opid IN (SELECT corporateID from facilities where opID = " + permissions.getAccountId()+")";
			else 
				opIds += " ff.opid IN (SELECT opID from facilities where corporateID = " + permissions.getAccountId()+")";
			sql.addWhere(opIds);
			sql.addWhere("a.status != 'Demo'");
		}
		sql.addField("o.name AS opName");
		sql.addField("o.type AS opType");
		sql.addField("o.id AS opId");
		sql.addField("lower(ff.forceFlag) AS flag");
		sql.addField("label AS fLabel");
		sql.addField("ff.forceend");
		sql.addField("ff.forceBegin");
		sql.addField("u.id as forcedById"); 
		sql.addField("u.name AS forcedBy");
		sql.addField("fa.name AS forcedByAccount");
		sql.addField("gc.workStatus");
		sql.addField("ff.flagActive");
		
		if (filterOn(getFilter().getOperator())) {
			String list = Strings.implode(getFilter().getOperator(), ",");
			sql.addWhere("o.id IN (" + list + ")");
		}
		
		orderByDefault = "o.name, a.name";
		
		if (download) {
			sql.addField("ff.forceFlag");
			sql.addField("flagActive");
		}
	}
	
	@Override
	protected void addExcelColumns() {
		super.addExcelColumns();
		
		excelSheet.addColumn(new ExcelColumn("opName"));
		excelSheet.addColumn(new ExcelColumn("forceFlag", "Flag"));
		excelSheet.addColumn(new ExcelColumn("fLabel", "Flag Issue", ExcelCellType.Translated));
		excelSheet.addColumn(new ExcelColumn("flagActive", "Flag Status", ExcelCellType.Translated));
		excelSheet.addColumn(new ExcelColumn("forcedBy", "Forced By"));
		excelSheet.addColumn(new ExcelColumn("forceBegin", "Start Date", ExcelCellType.Date));
		excelSheet.addColumn(new ExcelColumn("forceend", "End Date", ExcelCellType.Date));
		excelSheet.addColumn(new ExcelColumn("forceend", "End Date", ExcelCellType.Date));
	}
}
