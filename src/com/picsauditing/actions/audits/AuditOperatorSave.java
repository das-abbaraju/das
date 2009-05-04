package com.picsauditing.actions.audits;

import java.util.Map;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditOperatorDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.OperatorAccount;

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

		if (ao.getId() == 0) {
			if (operatorID > 0) {
				ao.setOperatorAccount(new OperatorAccount());
				ao.getOperatorAccount().setId(operatorID);
				ao.setAuditColumns(this.getUser());
			}
		}
		cAccountDAO.updateContractorByOperator(ao.getOperatorAccount());
		
		ao = dao.save(ao);

		return SUCCESS;
	}

	public void prepare() throws Exception {
		String[] ids = (String[]) ActionContext.getContext().getParameters().get("ao.id");
		int id = new Integer(ids[0]).intValue();
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
		AuditStatus[] list = {AuditStatus.Active, AuditStatus.Submitted};
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
