package com.picsauditing.actions.operators;

import java.util.List;
import java.util.Map;

import com.picsauditing.actions.AccountActionSupport;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AmBest;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.FlagCriteriaOperator;
import com.picsauditing.jpa.entities.OperatorAccount;

public class OperatorActionSupport extends AccountActionSupport {
	private static final long serialVersionUID = 8967320010000259378L;
	protected OperatorAccountDAO operatorDao;
	protected OperatorAccount operator;
	private List<OperatorAccount> inheritsFlagCriteria = null;
	private List<OperatorAccount> inheritsInsuranceCriteria = null;
	private List<OperatorAccount> inheritsAudits = null;
	private List<OperatorAccount> inheritsInsurance = null;

	public OperatorActionSupport(OperatorAccountDAO operatorDao) {
		this.operatorDao = operatorDao;
	}

	public String execute() throws Exception {
		findOperator();
		return SUCCESS;
	}

	protected void findOperator() throws Exception {
		loadPermissions();
		if (operator == null) {

			if (id == 0) {
				// Check the parameter list just in case it hasn't been set yet
				// ie this is being called from a Prepare method
				id = this.getParameter("id");
			}

			if (permissions.isOperatorCorporate())
				id = permissions.getAccountId();

			if (id == 0)
				throw new Exception("Missing operator id");
			// else if (permissions.isCorporate()) {
			// if (!permissions.getOperatorChildren().contains(id))
			// throw new
			// Exception("Corporate account doesn't have access to that operator");

			operator = operatorDao.find(id);
			account = operator;
		}
	}

	public OperatorAccount getOperator() {
		return operator;
	}

	public List<OperatorAccount> getInheritsFlagCriteria() {
		if (inheritsFlagCriteria == null) {
			inheritsFlagCriteria = operatorDao.findWhere(true,
					"a.status IN ('Active','Demo') AND a.inheritFlagCriteria.id = " + operator.getId());
			inheritsFlagCriteria.remove(operator);
		}
		return inheritsFlagCriteria;
	}

	public List<OperatorAccount> getInheritsInsuranceCriteria() {
		if (inheritsInsuranceCriteria == null) {
			inheritsInsuranceCriteria = operatorDao.findWhere(true,
					"a.status IN ('Active','Demo') AND a.inheritInsuranceCriteria.id = " + operator.getId());
			inheritsInsuranceCriteria.remove(operator);
		}
		return inheritsInsuranceCriteria;
	}

	public Map<Integer, String> getAMBestClassList() {
		return AmBest.financialMap;
	}

	public Map<Integer, String> getAMBestRatingsList() {
		return AmBest.ratingMap;
	}

	public FlagCriteriaOperator getFlagCriteriaOperatorByAudit(int auditTypeID) {
		for (FlagCriteriaOperator flagCriteriaOperator : operator.getFlagAuditCriteriaInherited()) {
			if (flagCriteriaOperator.getCriteria().getAuditType().getId() == auditTypeID) {
				return flagCriteriaOperator;
			}
		}
		return null;
	}

	public String getOperatorIds() {
		String where = "";
		if (operator.isCorporate()) {
			for (Facility facility : operator.getOperatorFacilities()) {
				where += "&filter.operator=" + facility.getOperator().getId();
			}
		} else {
			where += "&filter.operator=" + operator.getId();
		}
		return where;
	}
}
