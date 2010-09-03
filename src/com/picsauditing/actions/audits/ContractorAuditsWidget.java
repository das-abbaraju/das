package com.picsauditing.actions.audits;

import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.ContractorAudit;

@SuppressWarnings("serial")
public class ContractorAuditsWidget extends PicsActionSupport {
	protected ContractorAuditDAO dao;
	private List<ContractorAudit> upcoming;
	private List<ContractorAudit> closed;
	private List<ContractorAudit> assigned;

	public ContractorAuditsWidget(ContractorAuditDAO dao) {
		this.dao = dao;
	}

	public String execute() throws Exception {
		loadPermissions();
		if (!permissions.isLoggedIn())
			return LOGIN_AJAX;

		return SUCCESS;
	}

	public List<ContractorAudit> getRecentlyClosed() {
		if (closed == null)
			closed = dao.findRecentlyClosed(10, permissions);
		return closed;
	}

	public List<ContractorAudit> getUpcoming() {
		if (upcoming == null)
			upcoming = dao.findNew(10, permissions);
		return upcoming;
	}

	public List<ContractorAudit> getNewlyAssigned() {
		if (assigned == null)
			assigned = dao.findNewlyAssigned(10, permissions);
		return assigned;
	}
}
