package com.picsauditing.actions.audits;

import java.util.Map;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditOperatorDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.LowMedHigh;

@SuppressWarnings("serial")
public class AuditOperatorSave extends PicsActionSupport implements Preparable {

	private AuditOperator ao = null;
	private AuditOperatorDAO dao;
	private ContractorAccountDAO cAccountDAO;
	private int operatorID;
	private int auditTypeID;

	public AuditOperatorSave(AuditOperatorDAO dao, ContractorAccountDAO cAccountDAO) {
		this.dao = dao;
		this.cAccountDAO = cAccountDAO;
	}

	public String execute() {
		if (!forceLogin())
			return LOGIN;
		
		cAccountDAO.updateContractorByOperator(ao.getOperatorAccount());

		ao.setAuditColumns(permissions);
		ao = dao.save(ao);

		return SUCCESS;
	}

	public void prepare() throws Exception {
		int id = this.getParameter("ao.id");
		ao = dao.find(id);
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

	public AuditStatus[] getAuditStatusList() {
		AuditStatus[] list = { AuditStatus.Active, AuditStatus.Submitted };
		return list;
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
