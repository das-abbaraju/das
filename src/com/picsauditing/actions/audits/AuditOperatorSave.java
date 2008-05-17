package com.picsauditing.actions.audits;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Map;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditOperatorDAO;
import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.OperatorAccount;

public class AuditOperatorSave extends PicsActionSupport implements Preparable {

	private AuditOperator ao = null;
	private AuditOperatorDAO dao;
	private int operatorID;
	private int auditTypeID;

	public AuditOperatorSave(AuditOperatorDAO dao) {
		this.dao = dao;
	}

	public String execute() {
		if (!forceLogin())
			return LOGIN;

		if (ao.getAuditOperatorID() == 0) {
			if (operatorID > 0) {
				ao.setOperatorAccount(new OperatorAccount());
				ao.getOperatorAccount().setId(operatorID);
			}
		}
		
		ao = dao.save(ao);

		return SUCCESS;
	}

	public void prepare() throws Exception {
		String[] ids = (String[]) ActionContext.getContext().getParameters().get("ao.auditOperatorID");
		int id = new Integer(ids[0]).intValue();
		//if (id > 0)
			ao = dao.find(id);
		//else
			//ao = new AuditOperator();
		
	}

	public AuditOperator getAo() {
		return ao;
	}

	public void setAo(AuditOperator ao) {
		this.ao = ao;
	}

	public Map<Integer, LowMedHigh> getRiskLevelList() {
		return LowMedHigh.getMap();
	}

	public FlagColor[] getFlagColorList() {
		return FlagColor.values();
	}

	public int getOperatorID() {
		return operatorID;
	}

	public void setOperatorID(int operatorID) {
		this.operatorID = operatorID;
	}

	public int getAuditTypeID() {
		return auditTypeID;
	}

	public void setAuditTypeID(int auditTypeID) {
		this.auditTypeID = auditTypeID;
	}
}
