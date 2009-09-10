package com.picsauditing.actions.customerservice;

import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.ContractorAudit;

@SuppressWarnings("serial")
public class AssignWebcams extends PicsActionSupport {

	private ContractorAuditDAO auditDAO;
	private List<ContractorAudit> audits;

	public AssignWebcams(ContractorAuditDAO auditDAO) {
		this.auditDAO = auditDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		return SUCCESS;
	}

	public List<ContractorAudit> getAudits() {
		if (audits == null)
			audits = auditDAO.findAuditsNeedingWebcams();
		return audits;
	}
}
