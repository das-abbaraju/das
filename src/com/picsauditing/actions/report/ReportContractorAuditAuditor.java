package com.picsauditing.actions.report;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.util.SpringUtils;

public class ReportContractorAuditAuditor extends ReportContractorAudits {

	public String execute() throws Exception {
		loadPermissions();
		
		filterAuditor = false;
		
		sql.addWhere("ca.auditorID=" + permissions.getUserId());
		sql.addWhere("ca.auditStatus IN ('" 
				+ AuditStatus.Pending + "','"
				+ AuditStatus.Submitted + "')");
		
		if (orderBy == null)
			orderBy = "ca.assignedDate DESC";

		return super.execute();
	}
	
	public AuditStatus[] getAuditStatusList() {
		AuditStatus[] list = {AuditStatus.Pending, AuditStatus.Submitted};
		return list;
	}
	
	public List<AuditType> getAuditTypeList() {
		AuditTypeDAO dao = (AuditTypeDAO) SpringUtils.getBean("AuditTypeDAO");
		List<AuditType> list = dao.findAll();
		List<AuditType> list2 = new ArrayList<AuditType>();
		
		// Remove the AuditTypes that don't have an audit
		for(AuditType auditType : list)
			if (auditType.isHasAuditor())
				list2.add(auditType);
		return list2;
	}
}
