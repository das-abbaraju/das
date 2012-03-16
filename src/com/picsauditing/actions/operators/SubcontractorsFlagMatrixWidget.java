package com.picsauditing.actions.operators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import com.picsauditing.actions.report.ReportActionSupport;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.search.SelectSQL;

@SuppressWarnings("serial")
public class SubcontractorsFlagMatrixWidget extends ReportActionSupport {
	private SelectSQL sql = new SelectSQL("generalcontractors gc");
	private OperatorAccount operator;

	private List<ContractorAccount> subcontractors = new ArrayList<ContractorAccount>();
	private List<OperatorAccount> gcContractorOperators = new ArrayList<OperatorAccount>();
	private Table<Integer, Integer, FlagColor> table = TreeBasedTable.create();

	public String execute() throws Exception {
		if (!permissions.isLoggedIn())
			return LOGIN_AJAX;

		operator = dao.find(OperatorAccount.class, permissions.getAccountId());

		if (operator.isGeneralContractor()) {
			buildQuery();
			run(sql);
			buildMap();
		}

		return SUCCESS;
	}

	public List<ContractorAccount> getSubcontractors() {
		return subcontractors;
	}

	public List<OperatorAccount> getGcContractorOperators() {
		return gcContractorOperators;
	}

	public Table<Integer, Integer, FlagColor> getTable() {
		return table;
	}

	private void buildQuery() {
		sql.addJoin("JOIN generalcontractors subCon ON subCon.genID = gc.genID");
		sql.addJoin("JOIN generalcontractors gcOp ON subCon.subID = gcOp.subID AND gcOp.genID = "
				+ permissions.getAccountId());
		sql.addJoin("JOIN accounts o ON o.id = gc.genID AND o.status = 'Active' AND o.type = 'Operator'");
		sql.addJoin("JOIN accounts c ON c.id = subCon.subID AND c.status = 'Active'");

		sql.addField("o.id opID");
		sql.addField("o.name opName");
		sql.addField("c.id conID");
		sql.addField("c.name conName");
		sql.addField("subCon.flag");

		sql.addWhere("gc.subID = " + operator.getGcContractor().getContractorAccount().getId());
		sql.addWhere("gcOp.workStatus IN ('Y','P')");

		sql.addOrderBy("o.name");
		sql.addOrderBy("c.name");
	}

	private void buildMap() {
		for (BasicDynaBean bean : data) {
			OperatorAccount operator = new OperatorAccount();
			operator.setId(Integer.parseInt(bean.get("opID").toString()));
			operator.setName(bean.get("opName").toString());
			operator.setType("Operator");

			if (!gcContractorOperators.contains(operator))
				gcContractorOperators.add(operator);

			ContractorAccount contractor = new ContractorAccount();
			contractor.setId(Integer.parseInt(bean.get("conID").toString()));
			contractor.setName(bean.get("conName").toString());
			contractor.setType("Contractor");

			if (!subcontractors.contains(contractor))
				subcontractors.add(contractor);

			FlagColor flag = FlagColor.valueOf(bean.get("flag").toString());
			table.put(operator.getId(), contractor.getId(), flag);
		}
		
		Collections.sort(gcContractorOperators);
		Collections.sort(subcontractors);
	}
}
