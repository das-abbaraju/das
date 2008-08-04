package com.picsauditing.actions.auditType;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditCatOperatorDAO;
import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditCatOperator;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.Strings;



public class ManagePQFMatrix extends PicsActionSupport {
	private int[] riskLevels = {1,2,3};
	private int[] operators = null;
	private List<OperatorAccount> operatorAccounts;
	private List<OperatorRisk> columns = new ArrayList<OperatorRisk>();
	private List<AuditCategory> categories;
	private List<AuditCatOperator> data;
	
	protected OperatorAccountDAO operatorAccountDAO;
	protected AuditTypeDAO auditDAO;
	protected AuditCatOperatorDAO auditCatOperatorDAO;
	
	public ManagePQFMatrix(OperatorAccountDAO operatorAccountDAO, AuditTypeDAO auditCategoryDAO, AuditCatOperatorDAO auditCatOperatorDAO) {
		this.operatorAccountDAO = operatorAccountDAO;
		this.auditDAO = auditCategoryDAO;
		this.auditCatOperatorDAO = auditCatOperatorDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		permissions.tryPermission(OpPerms.ManageAudits);
		
		if (riskLevels == null || operators == null || riskLevels.length == 0 || operators.length == 0)
			return SUCCESS;
		
		operatorAccounts = operatorAccountDAO.findWhere(false, "id IN ("+Strings.implode(operators, ",")+")");
		for(OperatorAccount operator : operatorAccounts) {
			for(int risk : riskLevels) {
				OperatorRisk opRisk = new OperatorRisk(operator, LowMedHigh.values()[risk]);
				columns.add(opRisk);
			}
		}
		
		categories = auditDAO.find(AuditType.PQF).getCategories();
		
		data = auditCatOperatorDAO.find(operators, riskLevels);
		
		return SUCCESS;
	}

	public class OperatorRisk {
		private LowMedHigh riskLevel;
		private OperatorAccount operatorAccount;
		public OperatorRisk(OperatorAccount operatorAccount, LowMedHigh riskLevel) {
			this.riskLevel = riskLevel;
			this.operatorAccount = operatorAccount;
		}
		public LowMedHigh getRiskLevel() {
			return riskLevel;
		}
		public OperatorAccount getOperatorAccount() {
			return operatorAccount;
		}
	}

	// GETTERS && SETTERS
	
	public List<AuditCategory> getCategories() {
		return categories;
	}

	public List<AuditCatOperator> getData() {
		return data;
	}
	
	public boolean isChecked(int categoryID, LowMedHigh riskLevel, int operatorID) {
		for(AuditCatOperator dataCell : data) {
			if (dataCell.getCategory().getId() == categoryID
					&& dataCell.getOperatorAccount().getId() == operatorID
					&& dataCell.getRiskLevel().equals(riskLevel))
				return true;
		}
		return false;
	}
	
	public List<OperatorAccount> getOperatorList() throws Exception {
		return operatorAccountDAO.findWhere(false, "active='Y'");
	}

	public int[] getRiskLevels() {
		return riskLevels;
	}

	public void setRiskLevels(int[] riskLevels) {
		this.riskLevels = riskLevels;
	}

	public int[] getOperators() {
		return operators;
	}

	public void setOperators(int[] operators) {
		this.operators = operators;
	}

	public List<OperatorRisk> getColumns() {
		return columns;
	}

	public void setCategories(List<AuditCategory> categories) {
		this.categories = categories;
	}

	public List<OperatorAccount> getOperatorAccounts() {
		return operatorAccounts;
	}
}
