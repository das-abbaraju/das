package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.AuditOperatorDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.AuditType;

public class AuditOperatorList extends PicsActionSupport {
	private static final long serialVersionUID = -618269908092576272L;

	protected int oID;
	protected int aID = 1;
	
	protected List<Account> operators;
	protected List<AuditType> auditTypes;
	protected AccountDAO operatorDAO = null;
	private AuditTypeDAO auditDAO;
	private AuditOperatorDAO dataDAO;
	private List<AuditOperator> data;

	public static final HashMap<Integer, String> getRiskLevels() {
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		map.put(0, "None");
		map.put(1, "Low");
		map.put(2, "Med");
		map.put(3, "High");
		return map;
	}


	public AuditOperatorList(AccountDAO operatorDAO, AuditTypeDAO auditDAO, AuditOperatorDAO dataDAO) {
		this.operatorDAO = operatorDAO;
		this.auditDAO = auditDAO;
		this.dataDAO = dataDAO;
	}

	public String execute() throws Exception {
		if (!getPermissions(OpPerms.ManageOperators, OpType.View))
			return LOGIN;

		operators = operatorDAO.findOperators();
		auditTypes = auditDAO.findAll();

		data = new ArrayList<AuditOperator>();
		HashMap<Integer, AuditOperator> rawDataIndexed = new HashMap<Integer, AuditOperator>();
		if (aID > 0) {
			AuditType selectedObject = new AuditType();
			// Query the db for operators using this audit and index them by opID
			List<AuditOperator> rawData = dataDAO.findByAudit(aID);
			for (AuditOperator row : rawData) {
				selectedObject = row.getAuditType();
				//rawDataIndexed.put(row.getOpID(), row);
				rawDataIndexed.put(row.getAccount().getId(), row);
			}
			rawData = null; // we don't need this anymore

			for (Account operator : operators) {
				AuditOperator newRow = rawDataIndexed.get(operator.getId());
				if (newRow == null) {
					newRow = new AuditOperator();
					newRow.setAccount(operator);
					newRow.setAuditType(selectedObject);
				}
				data.add(newRow);
			}
		}
		if (oID > 0) {
			// Query the db for audits used by this operator and index them by typeID
			Account selectedObject = new Account();
			List<AuditOperator> rawData = dataDAO.findByOperator(oID);
			for (AuditOperator row : rawData) {
				selectedObject = row.getAccount();
				rawDataIndexed.put(row.getAuditType().getAuditTypeID(), row);
			}
			rawData = null; // we don't need this anymore
			
			//AuditOperator temp = rawData.get(0);
			for (AuditType aType : auditTypes) {
				AuditOperator newRow = rawDataIndexed.get(aType.getAuditTypeID());
				if (newRow == null) {
					newRow = new AuditOperator();
					newRow.setAccount(selectedObject);
					newRow.setAuditType(aType);
				}
				data.add(newRow);
			}
		}

		return SUCCESS;
	}

	public List<Account> getOperators() {
		return operators;
	}

	public List<AuditOperator> getData() {
		return data;
	}

	public int getOID() {
		return oID;
	}

	public void setOID(int oid) {
		aID = 0;
		oID = oid;
	}

	public int getAID() {
		return aID;
	}

	public void setAID(int aid) {
		oID = 0;
		aID = aid;
	}

	public String getOName() {
		if (this.oID > 0)
			for(Account row : this.operators)
				if (this.oID == row.getId())
					return row.getName();
		return "";
	}

	public String getAName() {
		if (this.aID > 0)
			for(AuditType row : this.auditTypes)
				if (this.aID == row.getAuditTypeID())
					return row.getAuditName();
		return "";
	}

	public List<AuditType> getAuditTypes() {
		return auditTypes;
	}
}
