package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.PICS.Facilities;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditOperatorDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.AuditType;

public class AuditOperatorList extends PicsActionSupport {
	private static final long serialVersionUID = -618269908092576272L;

	protected int oID;
	protected int aID = 1;
	protected List<AuditOperator> rawData;
	protected List<BasicDynaBean> operators;
	protected List<AuditType> auditTypes;
	private AuditTypeDAO auditDAO;
	private AuditOperatorDAO dataDAO;
	private List<AuditOperatorDO> data;
	protected Facilities facilityUtility = null;

	public static final HashMap<Integer, String> getRiskLevels() {
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		map.put(0, "None");
		map.put(1, "Low");
		map.put(2, "Med");
		map.put(3, "High");
		return map;
	}


	public AuditOperatorList(Facilities facilityUtility, AuditTypeDAO auditDAO, AuditOperatorDAO dataDAO) {
		this.autoLogin = true;

		this.facilityUtility = facilityUtility;
		this.auditDAO = auditDAO;
		this.dataDAO = dataDAO;
	}

	public String execute() throws Exception {
		if (!getPermissions(OpPerms.ManageOperators, OpType.View))
			return LOGIN;

		operators = facilityUtility.listAll("a.type = 'Operator'");
		auditTypes = auditDAO.findAll();
		// auditTypes = new ArrayList<AuditType>();

		data = new ArrayList<AuditOperatorDO>();
		if (aID > 0) {
			rawData = dataDAO.findByAudit(aID);
			HashMap<Integer, AuditOperator> rawDataIndexed = new HashMap<Integer, AuditOperator>();
			for (AuditOperator row : rawData) {
				//rawDataIndexed.put(row.getOpID(), row);
				rawDataIndexed.put(row.getAccount().getId(), row);
			}

			for (BasicDynaBean row : operators) {
				AuditOperatorDO newRow = new AuditOperatorDO();
				newRow.setAuditTypeID(aID);
				newRow.setOperatorID(Integer.parseInt(row.get("id").toString()));
				newRow.setOperatorName(row.get("name").toString());

				if (rawDataIndexed.get(newRow.getOperatorID()) != null) {
					newRow.setAuditOperatorID(rawDataIndexed.get(newRow.getOperatorID()).getAuditOperatorID());
					newRow.setRiskLevel(rawDataIndexed.get(newRow.getOperatorID()).getMinRiskLevel());
				}
				data.add(newRow);
			}
		}
		if (oID > 0) {
			rawData = dataDAO.findByOperator(oID);
			HashMap<Integer, AuditOperator> rawDataIndexed = new HashMap<Integer, AuditOperator>();
			for (AuditOperator row : rawData) {
				rawDataIndexed.put(row.getAuditType().getAuditTypeID(), row);
			}
			for (AuditType row : auditTypes) {
				AuditOperatorDO newRow = new AuditOperatorDO();
				newRow.setAuditTypeID(row.getAuditTypeID());
				newRow.setAuditName(row.getAuditName());
				newRow.setOperatorID(oID);
				if (rawDataIndexed.containsKey(row.getAuditTypeID())) {
					newRow.setAuditOperatorID(rawDataIndexed.get(newRow.getAuditTypeID()).getAuditOperatorID());
					newRow.setRiskLevel(rawDataIndexed.get(newRow.getAuditTypeID()).getMinRiskLevel());
				}
				data.add(newRow);
			}
		}

		return SUCCESS;
	}

	public List<BasicDynaBean> getOperators() {
		return operators;
	}

	public List<AuditOperatorDO> getData() {
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
		if (this.oID > 0) {
			for(BasicDynaBean row : this.operators)
				if (this.oID == Integer.parseInt(row.get("id").toString()))
					return row.get("name").toString();
		}
		return "";
	}

	public String getAName() {
		if (this.aID > 0) {
			for(AuditType row : this.auditTypes)
				if (this.aID == row.getAuditTypeID())
					return row.getAuditName();
		}
		return "";
	}

	public List<AuditType> getAuditTypes() {
		return auditTypes;
	}

}
