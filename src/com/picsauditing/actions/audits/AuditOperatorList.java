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
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.util.AuditTypeCache;

public class AuditOperatorList extends PicsActionSupport {
	private static final long serialVersionUID = -618269908092576272L;

	protected int oID;
	protected int aID = 1;
	
	protected List<OperatorAccount> operators;
	protected List<AuditType> auditTypes;
	protected OperatorAccountDAO operatorDAO;
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


	public AuditOperatorList(OperatorAccountDAO operatorDAO, AuditTypeDAO auditDAO, AuditOperatorDAO dataDAO) {
		this.operatorDAO = operatorDAO;
		this.auditDAO = auditDAO;
		this.dataDAO = dataDAO;
	}

	public String execute() throws Exception {
		getPermissions();
		permissions.tryPermission(OpPerms.ManageOperators);

		operators = operatorDAO.findWhere("");
		
		auditTypes = new AuditTypeCache( auditDAO ).getAuditTypes();
		
		data = new ArrayList<AuditOperator>();
		HashMap<Integer, AuditOperator> rawDataIndexed = new HashMap<Integer, AuditOperator>();
		if (aID > 0) {
			AuditType selectedObject = new AuditType();
			// Query the db for operators using this audit and index them by opID
			List<AuditOperator> rawData = dataDAO.findByAudit(aID);
			for (AuditOperator row : rawData) {
				selectedObject = row.getAuditType();
				//rawDataIndexed.put(row.getOpID(), row);
				rawDataIndexed.put(row.getOperatorAccount().getId(), row);
			}
			rawData = null; // we don't need this anymore

			for (OperatorAccount operator : operators) {
				AuditOperator newRow = rawDataIndexed.get(operator.getId());
				if (newRow == null) {
					newRow = new AuditOperator();
					newRow.setOperatorAccount(operator);
					newRow.setAuditType(selectedObject);
				}
				data.add(newRow);
			}
		}
		if (oID > 0) {
			// Query the db for audits used by this operator and index them by typeID
			OperatorAccount selectedObject = new OperatorAccount();
			List<AuditOperator> rawData = dataDAO.findByOperator(oID);
			for (AuditOperator row : rawData) {
				selectedObject = row.getOperatorAccount();
				rawDataIndexed.put(row.getAuditType().getAuditTypeID(), row);
			}
			rawData = null; // we don't need this anymore
			
			//AuditOperator temp = rawData.get(0);
			for (AuditType aType : auditTypes) {
				AuditOperator newRow = rawDataIndexed.get(aType.getAuditTypeID());
				if (newRow == null) {
					newRow = new AuditOperator();
					newRow.setOperatorAccount(selectedObject);
					newRow.setAuditType(aType);
				}
				data.add(newRow);
			}
		}

		return SUCCESS;
	}

	public List<OperatorAccount> getOperators() {
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
