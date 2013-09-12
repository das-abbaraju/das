package com.picsauditing.actions.auditType;

import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.jpa.entities.AuditType;

public class AuditTypeLegacy {
	private AuditType auditType;
	private AuditTypeDAO auditDao;
	
	public AuditTypeLegacy() {
		auditDao = (AuditTypeDAO) com.picsauditing.util.SpringUtils.getBean("AuditTypeDAO");
	}

	public void setAuditTypeID(String auditTypeIdString) throws Exception {
		if (auditTypeIdString == null || auditTypeIdString.length() == 0)
			throw new Exception("Missing auditTypeID");
		int auditTypeID = Integer.parseInt(auditTypeIdString);
		
		if (auditTypeID == 0)
			throw new Exception("Missing auditTypeID");

		auditType = auditDao.find(auditTypeID);
		if (auditType == null)
			throw new Exception("Failed to find AuditType = "+auditTypeID);
	}

	public int getAuditTypeID() {
		if (auditType == null)
			return 0;
		return auditType.getId();
	}

	public AuditType getAuditType() {
		return auditType;
	}
}
