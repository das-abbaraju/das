package com.picsauditing.actions.audits;

import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.ContractorAudit;

public class ContractorAuditsWidget extends PicsActionSupport {
	ContractorAuditDAO dao;
	
	public ContractorAuditsWidget(ContractorAuditDAO dao) {
		this.dao = dao;
	}

	public String execute() throws Exception {
		loadPermissions();

		return SUCCESS;
	}

	public List<ContractorAudit> getRecentlyClosed() {
		return dao.findRecentlyClosed(10, permissions);
	}

	public List<ContractorAudit> getUpcoming() {
		return dao.findUpcoming(10, permissions);
	}
}
