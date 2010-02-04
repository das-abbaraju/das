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



@SuppressWarnings("serial")
public class ManagePQFMatrix extends PicsActionSupport {
	private int[] riskLevels = {1,2,3};
	private int[] operators = null;
	private List<OperatorAccount> operatorAccounts;
	private List<OperatorRisk> columns = new ArrayList<OperatorRisk>();
	private List<AuditCategory> categories;
	
	//			 opId		catId		Flag		on/off
	private Map<Integer, Map<Integer, Map<String, Boolean>>> flagData;
	protected Map<String, Boolean> incoming = null;
	
	protected OperatorAccountDAO operatorAccountDAO;
	protected AuditTypeDAO auditDAO;
	protected AuditCatOperatorDAO auditCatOperatorDAO;
	private Map<OperatorAccount, List<OperatorAccount>> operatorChildren;
	
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
		
		operatorAccounts = operatorAccountDAO.findWhere(true, "id IN ("+Strings.implode(operators, ",")+")");
		for(OperatorAccount opAccount : operatorAccounts) {
			if(operatorChildren == null)
				operatorChildren = new HashMap<OperatorAccount, List<OperatorAccount>>();
			operatorChildren.put(opAccount, getInheritsAuditCategories(opAccount.getId()));
		}
		for(OperatorAccount operator : operatorAccounts) {
			for(int risk : riskLevels) {
				OperatorRisk opRisk = new OperatorRisk(operator, LowMedHigh.values()[risk]);
				columns.add(opRisk);
			}
		}
		categories = auditDAO.find(AuditType.PQF).getCategories();
		
		//load the data map
		flagData = new HashMap<Integer, Map<Integer,Map<String,Boolean>>>();
		List<AuditCatOperator> listData = auditCatOperatorDAO.find(operators, riskLevels);
		for( AuditCatOperator aco : listData) {
			setMapValue(aco.getOperatorAccount().getId(), aco.getCategory().getId(), aco.getRiskLevel(), true);
		}

		if("Save".equals(button) && incoming != null ) {

			for( String key : incoming.keySet() ) {
			
				String[] newData = key.split("_");
				int operatorID = Integer.parseInt(newData[0]);
				int categoryID = Integer.parseInt(newData[1]);
				LowMedHigh risk = LowMedHigh.valueOf(newData[2]);
				boolean newValue = incoming.get(key);
				
				// datamap => operatorID, categoryID, risk
				//persist logic here
				boolean exists = false;
				try {
					exists = flagData.get(operatorID).get(categoryID).get(risk.toString());
				} catch (Exception e) {}
				
				if (newValue && !exists) {
					// Add a new record
					AuditCatOperator row = new AuditCatOperator();
					row.setRiskLevel(risk);
					for(AuditCategory category : categories) {
						if (category.getId() == categoryID) {
							row.setCategory(category);
						}
					}
					for(OperatorAccount operator : operatorAccounts) {
						if (operator.getId() == operatorID) {
							row.setOperatorAccount(operator);
						}
					}
					if (row.getCategory() != null && row.getOperatorAccount() != null) {
						addActionMessage("Added "+row.getCategory().getCategory()+" for "+row.getOperatorAccount().getName()+" - "+risk);
						auditCatOperatorDAO.save(row);
						
						setMapValue(operatorID, categoryID, risk, true);
					}
				} else if (!newValue && exists) {
					// Delete the existing record
					for (AuditCatOperator row : listData) {
						if (row.getCategory().getId() == categoryID
								&& row.getOperatorAccount().getId() == operatorID
								&& row.getRiskLevel().equals(risk)) {
							auditCatOperatorDAO.remove(row);
							setMapValue(operatorID, categoryID, risk, false);
							addActionMessage("Removed "+row.getCategory().getCategory()+" for "+row.getOperatorAccount().getName()+" - "+risk);
						}
					}
				}
			}
			
		}
		
		return SUCCESS;
	}
	
	private void setMapValue(int operatorID, int categoryID, LowMedHigh risk, boolean value) {
		// First initialize the operator level
		if (flagData.get(operatorID) == null)
			flagData.put(operatorID, new HashMap<Integer, Map<String, Boolean>>());
		// Second initialize the category level
		if (flagData.get(operatorID).get(categoryID) == null)
			flagData.get(operatorID).put(categoryID, new HashMap<String, Boolean>());
		
		// finally, set the risk->value
		flagData.get(operatorID).get(categoryID).put(risk.toString(), value);
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

	public List<OperatorAccount> getOperatorList() throws Exception {
		return operatorAccountDAO.findInheritOperators("a.inheritAuditCategories");
	}
	
	public List<OperatorAccount> getInheritsAuditCategories(int opID) {
		return operatorAccountDAO.findWhere(true, "a.status IN ('Active','Demo','Pending') AND a.inheritAuditCategories.id = " + opID);
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

	public Map<Integer, Map<Integer, Map<String, Boolean>>> getFlagData() {
		return flagData;
	}

	public void setFlagData(
			Map<Integer, Map<Integer, Map<String, Boolean>>> flagData) {
		this.flagData = flagData;
	}

	public Map<String, Boolean> getIncoming() {
		return incoming;
	}

	public void setIncoming(Map<String, Boolean> incoming) {
		this.incoming = incoming;
	}

	public Map<OperatorAccount, List<OperatorAccount>> getOperatorChildren() {
		return operatorChildren;
	}

	
}
