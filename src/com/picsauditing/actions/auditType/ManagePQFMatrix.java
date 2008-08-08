package com.picsauditing.actions.auditType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditCatOperatorDAO;
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
	
	// opId		catId		Flag		on/off
	private Map<Integer, HashMap<Integer, HashMap<String, Boolean>>> flagData = new HashMap<Integer, HashMap<Integer, HashMap<String, Boolean>>>();
	protected Map<String, Boolean> incoming = null;
	
	protected OperatorAccountDAO operatorAccountDAO;
	protected AuditTypeDAO auditDAO;
	protected AuditCatOperatorDAO auditCatOperatorDAO;
	
	public ManagePQFMatrix(OperatorAccountDAO operatorAccountDAO, AuditTypeDAO auditDAO, AuditCatOperatorDAO auditCatOperatorDAO) {
		this.operatorAccountDAO = operatorAccountDAO;
		this.auditDAO = auditDAO;
		this.auditCatOperatorDAO = auditCatOperatorDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		permissions.tryPermission(OpPerms.ManageAudits);
		
		if ((riskLevels == null || operators == null || riskLevels.length == 0 || operators.length == 0)
				&& (button == null || ! button.equals("save")) )
			return SUCCESS;
		
		operatorAccounts = operatorAccountDAO.findWhere(false, "id IN ("+Strings.implode(operators, ",")+")");
		for(OperatorAccount operator : operatorAccounts) {
			for(int risk : riskLevels) {
				OperatorRisk opRisk = new OperatorRisk(operator, LowMedHigh.values()[risk]);
				columns.add(opRisk);
			}
		}
		categories = auditDAO.find(AuditType.PQF).getCategories();
		
		if( incoming != null ) {

			for( String key : incoming.keySet() ) {
			
				String[] newData = key.split("_");
				int opId = Integer.parseInt(newData[0]);
				int catId = Integer.parseInt(newData[1]);
				LowMedHigh theLevel = LowMedHigh.valueOf(newData[2]);
				boolean newValue = incoming.get(key);
				
				//persist logic here
				
			
			}
			
		}


		
		//load the flags
		for( AuditCatOperator aco : auditCatOperatorDAO.find(operators, riskLevels) ) {
			
			HashMap<Integer, HashMap<String, Boolean>> byCategory = flagData.get(aco.getOperatorAccount().getId());
			
			if( byCategory == null ) {
				byCategory = new HashMap<Integer, HashMap<String, Boolean>>();
				flagData.put(aco.getOperatorAccount().getId(), byCategory);
			}

			HashMap<String, Boolean> byFlag = byCategory.get(aco.getRiskLevel().name());
			
			if( byFlag == null ) {
				byFlag = new HashMap<String, Boolean>();
				byCategory.put(aco.getCategory().getId(), byFlag);
			}
			
			byFlag.put(aco.getRiskLevel().name(), true);
		}
		
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

	public Map<Integer, HashMap<Integer, HashMap<String, Boolean>>> getFlagData() {
		return flagData;
	}

	public void setFlagData(
			Map<Integer, HashMap<Integer, HashMap<String, Boolean>>> flagData) {
		this.flagData = flagData;
	}

	public Map<String, Boolean> getIncoming() {
		return incoming;
	}

	public void setIncoming(Map<String, Boolean> incoming) {
		this.incoming = incoming;
	}

	
}
