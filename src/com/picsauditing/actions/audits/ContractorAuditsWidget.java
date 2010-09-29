package com.picsauditing.actions.audits;

import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.jpa.entities.ContractorAuditOperator;

@SuppressWarnings("serial")
public class ContractorAuditsWidget extends PicsActionSupport {
	protected ContractorAuditOperatorDAO dao;
	private List<ContractorAuditOperator> upcoming;
	private List<ContractorAuditOperator> closed;
	private List<ContractorAuditOperator> assigned;

	public ContractorAuditsWidget(ContractorAuditOperatorDAO dao) {
		this.dao = dao;
	}

	public String execute() throws Exception {
		loadPermissions();
		if (!permissions.isLoggedIn())
			return LOGIN_AJAX;

		return SUCCESS;
	}

	public List<ContractorAuditOperator> getRecentlyClosed() {
		if (closed == null) {
			String where = "cao.status = 'Complete' AND statusChangedDate < NOW()";
			closed = dao.findByCaoStatus(10, permissions , where, "cao.statusChangedDate DESC");
		}	
		return closed;
	}

	public List<ContractorAuditOperator> getUpcoming() {
		if (upcoming == null) {
			String where = "cao.status IN ('Pending', 'Submitted')";
			upcoming = dao.findByCaoStatus(10, permissions , where, "cao.statusChangedDate DESC");
		}	
		return upcoming;
	}

	public List<ContractorAuditOperator> getNewlyAssigned() {
		if (assigned == null) {
			String where = "cao.status IN ('Pending', 'Submitted') AND ca.auditor.id = " + permissions.getUserId();		
			assigned = dao.findByCaoStatus(10, permissions , where, "ca.assignedDate DESC");
		}
		return assigned;
	}
}
