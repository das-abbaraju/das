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
	
	protected int operatorId;
	protected List<AuditOperator> rawData;
	protected List<BasicDynaBean> operators;
	protected List<AuditType> auditTypes;
	protected int auditId;
	private AuditTypeDAO auditDAO;
	private AuditOperatorDAO dataDAO;
	private List<AuditOperatorDO> data;
	
	protected Facilities facilityUtility = null;
	public AuditOperatorList( Facilities facilityUtility, AuditTypeDAO auditDAO, AuditOperatorDAO dataDAO )
	{
		this.autoLogin = true;
		
		this.facilityUtility = facilityUtility;
		this.auditDAO = auditDAO;
		this.dataDAO = dataDAO;
	}
	
	public String execute() throws Exception {
		if (!getPermissions(OpPerms.ManageOperators, OpType.View))
			return LOGIN;
		
		operators = facilityUtility.listAll("a.type = 'Operator'");
		//auditTypes = auditDAO.findAll();
		auditTypes = new ArrayList<AuditType>();
		
		data = new ArrayList<AuditOperatorDO>();
		if (auditId > 0) {
			rawData = dataDAO.findByAudit(auditId);
			HashMap<Integer, AuditOperator> rawDataIndexed = new HashMap<Integer, AuditOperator>();
			for(AuditOperator row : rawData) {
				rawDataIndexed.put(row.getOpID(), row);
			}
			for(BasicDynaBean row : operators) {
				AuditOperatorDO newRow = new AuditOperatorDO();
				newRow.setAuditTypeID(auditId);
				newRow.setOperatorID(Integer.parseInt(row.get("id").toString()));
				newRow.setOperatorName(row.get("name").toString());
				newRow.setRiskLevel(rawDataIndexed.get(newRow.getOperatorID()).getMinRiskLevel());
				data.add(newRow);
			}
		}
		if (operatorId > 0) {
			rawData = dataDAO.findByOperator(operatorId);
			HashMap<Integer, AuditOperator> rawDataIndexed = new HashMap<Integer, AuditOperator>();
			for(AuditOperator row : rawData) {
				rawDataIndexed.put(row.getAuditTypeID(), row);
			}
			for(AuditType row : auditTypes) {
				AuditOperatorDO newRow = new AuditOperatorDO();
				newRow.setAuditTypeID(row.getAuditTypeID());
				newRow.setAuditName(row.getAuditName());
				newRow.setOperatorID(operatorId);
				newRow.setRiskLevel(rawDataIndexed.get(newRow.getAuditTypeID()).getMinRiskLevel());
				data.add(newRow);
			}
		}
		
		return SUCCESS;
	}

	public int getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(int operatorId) {
		this.operatorId = operatorId;
	}

	public int getAuditId() {
		return auditId;
	}

	public void setAuditId(int auditId) {
		this.auditId = auditId;
	}

	public List<BasicDynaBean> getOperators() {
		return operators;
	}

	public List<AuditOperatorDO> getData() {
		return data;
	}
	
}
