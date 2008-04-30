package com.picsauditing.actions.auditType;

import java.util.List;

import com.picsauditing.access.OpPerms;
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
		loadPermissions();
		permissions.tryPermission(OpPerms.ManageAudits);
		
		auditTypes = auditTypeDAO.findAll();

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
	
	public String getPqfAudit() {
		return com.picsauditing.PICS.pqf.Constants.PQF_TYPE;
	}

	public String getDesktopAudit() {
		return com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE;
	}	

}
