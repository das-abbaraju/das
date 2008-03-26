package com.picsauditing.actions.audits;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditOperatorDAO;
import com.picsauditing.jpa.entities.AuditOperator;

public class AuditOperatorSave extends PicsActionSupport {
	private int auditOperatorID = 0;
	private int operatorID;
	private int auditTypeID;
	private int riskLevel;
	private AuditOperatorDAO dao;

	public AuditOperatorSave(AuditOperatorDAO dao) {
		this.dao = dao;
	}
	
	public String execute() throws Exception {
		// PrintWriter writer = ServletActionContext.getResponse().getWriter();
		// writer.print("Success");
		// writer.flush();
		AuditOperator o = new AuditOperator();
		o.setAuditOperatorID(auditOperatorID);
		o.setOpID(operatorID);
		o.setAuditTypeID(auditTypeID);
		o.setMinRiskLevel(riskLevel);
		o = dao.save(o);
		this.auditOperatorID = o.getAuditOperatorID();
		
		return SUCCESS;
	}

	public void setOperatorID(int operatorID) {
		this.operatorID = operatorID;
	}

	public void setAuditTypeID(int auditTypeID) {
		this.auditTypeID = auditTypeID;
	}

	public void setRiskLevel(int riskLevel) {
		this.riskLevel = riskLevel;
	}

	public void setAuditOperatorID(int auditOperatorID) {
		if (auditOperatorID > 0)
			this.auditOperatorID = auditOperatorID;
	}

	public int getAuditOperatorID() {
		return auditOperatorID;
	}

	public int getOperatorID() {
		return operatorID;
	}

	public int getAuditTypeID() {
		return auditTypeID;
	}

	public int getRiskLevel() {
		return riskLevel;
	}
	
	public int getId() {
		return (auditTypeID*100000)+operatorID;
	}
}
