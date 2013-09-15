package com.picsauditing.actions.report;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.Strings;
import com.picsauditing.util.excel.ExcelCellType;
import com.picsauditing.util.excel.ExcelColumn;

@SuppressWarnings("serial")
public class ReportContractorOperatorFlagMatrix extends ReportAccount {
	private Map<ContractorAccount, Map<OperatorAccount, String>> reportData = null;
	private SortedSet<OperatorAccount> operators = null;

	@Override
	protected void buildQuery() {
		setReportName(getText("ReportContractorOperatorFlagMatrix.title"));

		skipPermissions = true;
		super.buildQuery();

		getFilter().setShowPrimaryInformation(false);
		getFilter().setShowTradeInformation(false);

		List<Integer> ops = new Vector<Integer>();

		if (getAccount().isOperator()) {
			OperatorAccount op = (OperatorAccount) getAccount();

			if (op.getCorporateFacilities().size() > 0) {
				for (Facility facility : op.getCorporateFacilities()) {
					OperatorAccount corporate = facility.getCorporate();
					if (corporate.getId() > Account.PICS_CORPORATE_ID) {
						for (Facility child : corporate.getOperatorFacilities()) {
							ops.add(child.getOperator().getId());
						}
					}
				}
			}

			ops.add(permissions.getAccountId());
		}

		else if (getAccount().isCorporate()) {
			OperatorAccount corporate = (OperatorAccount) getAccount();
			for (Facility child : corporate.getOperatorFacilities()) {
				ops.add(child.getOperator().getId());
			}
		}

		sql.addJoin("JOIN generalcontractors gc on gc.subid = a.id");
		sql.addJoin("JOIN accounts operator on operator.id = gc.genid");

		if (permissions.isOperatorCorporate()) {
			if (download) {
				sql.addJoin("LEFT JOIN contractor_tag cg ON cg.conID = a.id");
				sql.addJoin("LEFT JOIN operator_tag ot ON ot.id = cg.tagID AND ot.opID = " + permissions.getAccountId());
				sql.addField("GROUP_CONCAT(DISTINCT ot.tag ORDER BY ot.tag SEPARATOR ', ') AS tag");
			}
		}

		sql.addField("operator.name AS opName");
		sql.addField("operator.id AS opId");
		sql.addField("gc.flag as flag");
		sql.addField("workStatus");
		sql.addWhere("a.status IN ('Active','Demo')");
		sql.addWhere("operator.id in (" + Strings.implode(ops, ",") + ")");
		orderByDefault = "a.name, operator.name";

		report.setLimit(-1);
		sql.addGroupBy("operator.id, c.id");

	}

	public Map<ContractorAccount, Map<OperatorAccount, String>> getReportData() {
		if (reportData == null) {
			buildData();
		}

		return reportData;
	}

	private void buildData() {
		Comparator<Account> compByName = new Comparator<Account>() {
			public int compare(Account o1, Account o2) {
				if (o2 == null || o2.getName() == null)
					return 1;
				if (o1 == null || o1.getName() == null)
					return -1;
				return o1.getName().compareTo(o2.getName());
			}
		};

		reportData = new TreeMap<ContractorAccount, Map<OperatorAccount, String>>(compByName);

		operators = new TreeSet<OperatorAccount>(compByName);

		for (Iterator<BasicDynaBean> iterator = data.iterator(); iterator.hasNext();) {

			BasicDynaBean row = iterator.next();

			iterator.remove();

			OperatorAccount operator = new OperatorAccount();

			operator.setId((Integer) row.get("opId"));
			operator.setName((String) row.get("opName"));
			operators.add(operator);

			ContractorAccount contractor = new ContractorAccount();
			contractor.setId((Integer) row.get("id"));
			contractor.setName((String) row.get("name"));

			Map<OperatorAccount, String> dataForThisContractor = reportData.get(contractor);

			if (dataForThisContractor == null) {
				dataForThisContractor = new TreeMap<OperatorAccount, String>(compByName);
				reportData.put(contractor, dataForThisContractor);
			}

			dataForThisContractor.put(operator, (String) row.get("flag"));
		}
	}

	public SortedSet<OperatorAccount> getOperatorList() {
		if (operators == null) {
			buildData();
		}
		return operators;
	}

	@Override
	protected void addExcelColumns() {
		super.addExcelColumns();
		excelSheet.addColumn(new ExcelColumn("opName", "Operator Name", ExcelCellType.String), 30);
		if (permissions.isCorporate())
			excelSheet.addColumn(new ExcelColumn("flag", "Flag", ExcelCellType.String), 40);

		if (permissions.isOperatorCorporate())
			excelSheet.addColumn(new ExcelColumn("tag", "Contractor Tag"));
	}
}