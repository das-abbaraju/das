package com.picsauditing.actions.report;

import java.util.List;
import java.util.Vector;

import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
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

		sql.addJoin("JOIN generalcontractors gc on gc.subid = a.id");
		sql.addJoin("JOIN accounts operator on operator.id = gc.genid");
		
		if (permissions.isAdmin()) {
			sql.addField("GROUP_CONCAT(operator.name ORDER BY operator.name ASC SEPARATOR ', ') AS opName");
			sql.addGroupBy("a.name, flag");
		}
		else
			sql.addField("operator.name AS opName");
		
		sql.addField("operator.id AS opId");
		sql.addField("gc.flag");
		sql.addField("lower(gc.flag) AS lflag");
		sql.addWhere("a.status IN ('Active','Demo')");
		sql.addWhere("operator.type = 'Operator'");
		if(!Strings.isEmpty(opIds))
			sql.addWhere("operator.id in (" + opIds + ")");
	}

	@Override
	protected void addExcelColumns() {
		super.addExcelColumns();
		
		if (permissions.isAdmin())
			excelSheet.addColumn(new ExcelColumn("opName", "Operators", ExcelCellType.String), 30);
		else
			excelSheet.addColumn(new ExcelColumn("opName", "Operator Name", ExcelCellType.String), 30);
		
		excelSheet.addColumn(new ExcelColumn("flag", "Flag", ExcelCellType.String), 40);
	}
}
