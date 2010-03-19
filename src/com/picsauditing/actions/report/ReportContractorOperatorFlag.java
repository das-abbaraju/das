package com.picsauditing.actions.report;

import java.util.List;
import java.util.Vector;

import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.Strings;
import com.picsauditing.util.excel.ExcelCellType;
import com.picsauditing.util.excel.ExcelColumn;

@SuppressWarnings("serial")
public class ReportContractorOperatorFlag extends ReportAccount {
	
	public ReportContractorOperatorFlag() {
		setReportName("Contractor Operator Flag");
		orderByDefault = "a.name, operator.name";
	}
	
	@Override
	protected void buildQuery() {
		skipPermissions = true;
		super.buildQuery();
		
		getFilter().setShowFlagStatus(true);
		String opIds;
		List<Integer> ops = new Vector<Integer>();
		if(filterOn(getFilter().getOperator())) {
			opIds = Strings.implode(getFilter().getOperator(), ",");
		}
		else {
			OperatorAccount corporate = (OperatorAccount) getUser().getAccount();
			for( Facility child : corporate.getOperatorFacilities() ) {
				if(child.getOperator().getStatus().isActiveDemo()) {
					ops.add(child.getOperator().getId());
				}
			}
			opIds = Strings.implode(ops, ",");
		}	
		
		sql.addJoin("JOIN generalcontractors gc on gc.subid = a.id");
		sql.addJoin("JOIN accounts operator on operator.id = gc.genid");
		sql.addField("operator.name AS opName");
		sql.addField("operator.id AS opId");
		sql.addField("gc.flag");
		sql.addField("lower(gc.flag) AS lflag");
		sql.addWhere("a.status IN ('Active','Demo')");
		sql.addWhere("operator.id in (" + opIds + ")");
	}
	
	@Override
	protected void addExcelColumns() {
		super.addExcelColumns();
		excelSheet.addColumn(new ExcelColumn("opName", "Operator Name", ExcelCellType.String), 30);
		excelSheet.addColumn(new ExcelColumn("flag", "Flag", ExcelCellType.String), 40);
	}
}	
