package com.picsauditing.actions.audits;

import java.util.List;
import java.util.Map;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditOperatorDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.FlagCriteriaOperatorDAO;
import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagCriteriaOperator;
import com.picsauditing.jpa.entities.LowMedHigh;

@SuppressWarnings("serial")
public class AuditOperatorSave extends PicsActionSupport implements Preparable {

	private AuditOperator ao = null;
	private AuditOperatorDAO dao;
	private ContractorAccountDAO cAccountDAO;
	private FlagCriteriaOperatorDAO fcoDAO;
	private int operatorID;
	private int auditTypeID;

	public AuditOperatorSave(AuditOperatorDAO dao, ContractorAccountDAO cAccountDAO, FlagCriteriaOperatorDAO fcoDAO) {
		this.dao = dao;
		this.cAccountDAO = cAccountDAO;
		this.fcoDAO = fcoDAO;
	}

	public String execute() {
		if (!forceLogin())
			return LOGIN;
		
		cAccountDAO.updateContractorByOperator(ao.getOperatorAccount());
		boolean foundCriteria = false;
		
		try {
			List<FlagCriteriaOperator> opCriteria = ao.getOperatorAccount().getFlagCriteriaInherited();
			
			for (FlagCriteriaOperator fco : opCriteria) {
				if (fco.getCriteria().getAuditType() != null && fco.getCriteria().getAuditType().equals(ao.getAuditType())) {
					foundCriteria = true;
					fco.setMinRiskLevel(LowMedHigh.getMap().get(ao.getMinRiskLevel()));
					fcoDAO.save(fco);
				}
			}
		} catch (Exception e) {
			// Do nothing?
		}
		
		ao.setAuditColumns(permissions);
		ao = dao.save(ao);

		if (!foundCriteria) {
			String message = ao.getOperatorAccount().getName() + " has no flag criteria for "
				+ ao.getAuditType().getAuditName();
			
			if (permissions.hasPermission(OpPerms.ManageOperators))
				message += "<br />To set up flag criteria, <a href=\"ManageFlagCriteria.action\">click here</a>";
			
			addActionMessage(message);
		}
		
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
