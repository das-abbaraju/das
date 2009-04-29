package com.picsauditing.actions.operators;

import java.util.List;

import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.OperatorAccount;

public class OperatorActionSupport extends PicsActionSupport {
	private static final long serialVersionUID = 8967320010000259378L;
	protected int id;
	protected OperatorAccountDAO operatorDao;
	protected OperatorAccount operator;
	protected String subHeading;
	private List<OperatorAccount> inheritsFlagCriteria = null;
	private List<OperatorAccount> inheritsInsuranceCriteria = null;
	private List<OperatorAccount> inheritsAudits = null;
	private List<OperatorAccount> inheritsInsurance = null;

	public OperatorActionSupport(OperatorAccountDAO operatorDao) {
		this.operatorDao = operatorDao;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void findOperator() throws Exception {
		loadPermissions();
		if (operator == null) {
			
			if (id == 0) {
				// Check the parameter list just in case it hasn't been set yet 
				// ie this is being called from a Prepare method
				id = this.getParameter("id");
			}

			if (permissions.isOperator())
				id = permissions.getAccountId();
			else if (id == 0)
				throw new Exception("Missing operator id");
			else if (permissions.isCorporate()) {
				if (!permissions.getOperatorChildren().contains(id))
					throw new Exception("Corporate account doesn't have access to that operator");
			} else {
				permissions.tryPermission(OpPerms.AllOperators);
			}

			operator = operatorDao.find(id);
		}
	}

	public String getSubHeading() {
		return subHeading;
	}

	public void setSubHeading(String subHeading) {
		this.subHeading = subHeading;
	}

	public OperatorAccount getOperator() {
		return operator;
	}
	
	public List<OperatorAccount> getInheritsFlagCriteria() {
		if (inheritsFlagCriteria == null) {
			inheritsFlagCriteria = operatorDao.findWhere(true, "a.inheritFlagCriteria.id = " + operator.getId());
			inheritsFlagCriteria.remove(operator);
		}
		return inheritsFlagCriteria;
	}

	public List<OperatorAccount> getInheritsInsuranceCriteria() {
		if (inheritsInsuranceCriteria == null) {
			inheritsInsuranceCriteria = operatorDao.findWhere(true, "a.inheritInsuranceCriteria.id = " + operator.getId());
			inheritsInsuranceCriteria.remove(operator);
		}
		return inheritsInsuranceCriteria;
	}

	public List<OperatorAccount> getInheritsAudits() {
		if (inheritsAudits == null) {
			inheritsAudits = operatorDao.findWhere(true, "a.inheritAudits.id = " + operator.getId());
			inheritsAudits.remove(operator);
		}
		return inheritsAudits;
	}

	public List<OperatorAccount> getInheritsInsurance() {
		if (inheritsInsurance == null) {
			inheritsInsurance = operatorDao.findWhere(true, "a.inheritInsurance.id = " + operator.getId());
			inheritsInsurance.remove(operator);
		}
		return inheritsInsurance;
	}

}
