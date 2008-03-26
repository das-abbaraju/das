package com.picsauditing.actions.audits;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditOperatorDAO;
import com.picsauditing.jpa.entities.AuditOperator;

public class AuditOperatorSave extends PicsActionSupport {

	private AuditOperator ao = null;
	private AuditOperatorDAO dao;

	public AuditOperatorSave(AuditOperatorDAO dao) {
		this.dao = dao;
	}
	
	public String execute() throws Exception {
		ao = dao.save(ao);
		
		return SUCCESS;
	}

	public AuditOperator getAo() {
		return ao;
	}

	public void setAo(AuditOperator ao) {
		this.ao = ao;
	}
}
