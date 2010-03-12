package com.picsauditing.actions.report;

import java.util.Set;
import java.util.TreeSet;

import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.FlagCriteriaOperator;
import com.picsauditing.jpa.entities.ListType;
import com.picsauditing.jpa.entities.OperatorAccount;

@SuppressWarnings("serial")
public class OperatorFlagMatrix extends ReportAccount {

	private OperatorAccountDAO operatorDAO;

	private int id;
	private OperatorAccount operator;

	private Set<FlagCriteria> flagCriteria = new TreeSet<FlagCriteria>();

	public OperatorFlagMatrix(OperatorAccountDAO operatorDAO) {
		setReportName("Contractor Operator Flag Matrix");
		this.operatorDAO = operatorDAO;
		this.listType = ListType.Operator;
	}

	@Override
	protected void buildQuery() {
		skipPermissions = true;

		operator = operatorDAO.find(permissions.getAccountId());

		super.buildQuery();

		getFilter().setShowPrimaryInformation(false);
		getFilter().setShowTradeInformation(false);

		sql.addJoin("JOIN generalcontractors gc ON gc.genID = " + permissions.getAccountId()
				+ " AND gc.subID = a.id AND gc.flag IN ('Red', 'Amber')");
		sql.addField("gc.flag overallFlag");

		flagCriteria = new TreeSet<FlagCriteria>();
		for (FlagCriteriaOperator fco : operator.getFlagCriteria()) {
			flagCriteria.add(fco.getCriteria());
		}

		for (FlagCriteria criteria : flagCriteria) {
			if (!criteria.isInsurance()) {
				String joinName = "fd" + criteria.getId();
				sql.addJoin("LEFT JOIN flag_data " + joinName + " ON " + joinName + ".opID = gc.genID AND " + joinName
						+ ".conID = a.id AND " + joinName + ".criteriaID = " + criteria.getId() + " AND " + joinName
						+ ".flag IN ('Red', 'Amber')");

				sql.addField(joinName + ".flag " + "flag" + criteria.getId());
			}
		}

		sql.addWhere("status IN ('Active', 'Demo')");
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public OperatorAccount getOperator() {
		return operator;
	}

	public Set<FlagCriteria> getFlagCriteria() {
		return flagCriteria;
	}

}
