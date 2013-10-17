package com.picsauditing.actions.report;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.util.PermissionQueryBuilderEmployee;
import com.picsauditing.util.excel.ExcelCellType;
import com.picsauditing.util.excel.ExcelColumn;

@SuppressWarnings("serial")
public class ReportEmployeeList extends ReportEmployee {
	@Autowired
	protected ContractorOperatorDAO contractorOperatorDAO;

	private Set<Integer> viewableContractors;

	@Override
	protected void buildQuery() {
		orderByDefault = "a.type DESC, a.name, e.firstname, e.lastName";

		super.buildQuery();

		sql.addField("a.dbaName");
		sql.addField("a.type accountType");

		if (permissions.isContractor())
			getFilter().setShowAccountName(false);

		if (permissions.isOperatorCorporate()) {
			if (permissions.isOperator()) {
				String where = "a.id IN (SELECT co.conID FROM contractor_operator co WHERE co.conID = a.id "
						+ "AND co.opID = %1$d) OR a.id = %1$d";

				if (permissions.isRequiresCompetencyReview()) {
					where = "a.id IN (SELECT f.opID FROM facilities f JOIN facilities c "
							+ "ON c.corporateID = f.corporateID AND c.opID = %1$d AND c.corporateID > 10) OR " + where;
				}

				sql.addWhere(String.format(where, permissions.getAccountId()));
			}

			if (permissions.isCorporate()) {
				PermissionQueryBuilderEmployee query = new PermissionQueryBuilderEmployee(permissions);
				sql.addWhere(query.toString());
			}
		}
	}

	public boolean isCanViewContractor(int conID) {
		if (permissions.isAdmin())
			return true;
		if (permissions.isContractor() && permissions.getAccountId() == conID)
			return true;
		// Only for Operators/Corporate
		if (viewableContractors == null) {
			if (viewableContractors == null)
				viewableContractors = new HashSet<Integer>();

			List<ContractorOperator> contractors = Collections.emptyList();
			if (permissions.isOperator())
				contractors = contractorOperatorDAO.findWhere("operatorAccount.id = " + permissions.getAccountId());

			if (permissions.isCorporate()) {
				contractors = contractorOperatorDAO.findWhere("operatorAccount.parent.id = "
						+ permissions.getAccountId() + " GROUP BY contractorAccount");
			}

			for (ContractorOperator co : contractors) {
				if (co.getContractorAccount().getStatus().isActive()
						|| (permissions.getAccountStatus().isDemo() && co.getContractorAccount().getStatus().isDemo()))
					viewableContractors.add(co.getContractorAccount().getId());
			}
		}

		return viewableContractors.contains(conID);
	}

	protected void addExcelColumns() {
		super.addExcelColumns();

		excelSheet.addColumn(new ExcelColumn("title", getText("Employee.title")));
		excelSheet.addColumn(new ExcelColumn("classification", getText("EmployeeClassification"),
				ExcelCellType.Translated));
		excelSheet.addColumn(new ExcelColumn("hireDate", getText("Employee.hireDate"), ExcelCellType.Date));
		excelSheet.addColumn(new ExcelColumn("email", getText("Employee.email")));
		excelSheet.addColumn(new ExcelColumn("phone", getText("Employee.phone")));
		excelSheet.addColumn(new ExcelColumn("twicExpiration", getText("Employee.twicExpiration"), ExcelCellType.Date));
	}
}