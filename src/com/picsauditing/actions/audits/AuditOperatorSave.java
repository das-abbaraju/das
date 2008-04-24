package com.picsauditing.actions.audits;

import java.util.Map;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditOperatorDAO;
import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.LowMedHigh;

public class AuditOperatorSave extends PicsActionSupport {

	private AuditOperator ao = null;
	private AuditOperatorDAO dao;

	public AuditOperatorSave(AuditOperatorDAO dao) {
		this.dao = dao;
	}
	
	public String execute() {
		ao = dao.save(ao);
		
		return SUCCESS;
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
}
