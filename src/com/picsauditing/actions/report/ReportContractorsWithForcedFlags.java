package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;
import com.picsauditing.util.Strings;
import com.picsauditing.util.excel.ExcelCellType;
import com.picsauditing.util.excel.ExcelColumn;

@SuppressWarnings("serial")
public class ReportContractorsWithForcedFlags extends ReportAccount {
	
	@Override
	public void prepare() throws Exception {
		super.prepare();

		getFilter().setShowInsuranceLimits(false);
		getFilter().setShowOpertorTagName(false);
	}
	
	@Override
	public void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.ForcedFlagsReport);
	}
	
	@Override
	protected void buildQuery() {
		skipPermissions = true;
		super.buildQuery();
		
		String forceFlagsJoin = "JOIN (SELECT conid,opid,forcebegin,forceend,forcedby,forceflag, cID FROM"+ 
		" (SELECT gc.subid as conid, gc.genid as opid, gc.forcebegin, gc.forceend, gc.forcedBy, gc.forceflag, 0 cID FROM generalcontractors gc" +
		" WHERE gc.forceFlag IS NOT NULL" +
		" UNION" +
		" SELECT fdo.conid, fdo.opid, fdo.updateDate as forcebegin, fdo.forceend, fdo.updatedBy as forcedby, fdo.forceflag, fdo.criteriaID as cID FROM flag_data_override fdo"+
		" WHERE fdo.forceFlag IS NOT NULL) t"+
		" ) ff ON a.id = ff.conid";
		
		sql.addJoin(forceFlagsJoin);
		sql.addJoin("JOIN accounts o ON o.id = ff.opid");
		sql.addJoin("LEFT JOIN users u ON u.id = ff.forcedBy");
		sql.addJoin("LEFT JOIN accounts fa ON fa.id = u.accountID");
		sql.addJoin("LEFT JOIN generalcontractors gc ON gc.genid = o.id and gc.subid = ff.conid");
		sql.addJoin("LEFT JOIN flag_criteria fc on fc.id = ff.cID");
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
		sql.addField("fc.label AS fLabel");
		sql.addField("o.type AS opType");
		sql.addField("o.id AS opId");
		sql.addField("lower(ff.forceFlag) AS lflag");
		sql.addField("ff.forceend");
		sql.addField("ff.forceBegin");
		sql.addField("u.id as forcedById"); 
		sql.addField("u.name AS forcedBy");
		sql.addField("fa.name AS forcedByAccount");
		
		if (filterOn(getFilter().getOperator())) {
			String list = Strings.implode(getFilter().getOperator(), ",");
			sql.addWhere("o.id IN (" + list + ")");
		}
		
		orderByDefault = "o.name, a.name";
		
		if (download) {
			sql.addField("ff.forceFlag");
		}
	}
	
	@Override
	protected void addExcelColumns() {
		super.addExcelColumns();
		
		if (permissions.isAdmin())
			excelSheet.addColumn(new ExcelColumn("opName"));
		
		excelSheet.addColumn(new ExcelColumn("forceFlag", "Flag"));
		excelSheet.addColumn(new ExcelColumn("fLabel", "Flag Issue"));
		excelSheet.addColumn(new ExcelColumn("forcedBy", "Forced By"));
		excelSheet.addColumn(new ExcelColumn("forceBegin", "Start Date", ExcelCellType.Date));
		excelSheet.addColumn(new ExcelColumn("forceend", "End Date", ExcelCellType.Date));
		excelSheet.addColumn(new ExcelColumn("forceend", "End Date", ExcelCellType.Date));
	}
}
