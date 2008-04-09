package com.picsauditing.actions.auditType;

import java.util.List;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.jpa.entities.AuditType;

public class AuditTypeChoose extends PicsActionSupport {
	private int auditTypeID = 0;
	private List<AuditType> auditTypes;
	private AuditTypeDAO auditTypeDAO;

	public AuditTypeChoose(AuditTypeDAO auditTypeDAO) {
		this.auditTypeDAO = auditTypeDAO;
	}
	
	public String execute() throws Exception {
		auditTypes = auditTypeDAO.findAll();
		this.getPermissions(OpPerms.ManageAudits, OpType.View);
		//if (auditTypeID > 0)
			//session.setAttribute("auditTypeID", auditTypeID);

		return SUCCESS;
	}

	public List<AuditType> getAuditTypes() {
		return auditTypes;
	}

	public int getAuditTypeID() {
		return auditTypeID;
	}

	public void setAuditTypeID(int auditTypeID) {
		this.auditTypeID = auditTypeID;
	}
	
}
