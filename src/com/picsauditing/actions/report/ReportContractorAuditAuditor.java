package com.picsauditing.actions.report;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.util.SpringUtils;

@SuppressWarnings("serial")
public class ReportContractorAuditAuditor extends ReportContractorAudits {

	@Override
	public void buildQuery() {
		super.buildQuery();
		
		sql.addWhere("ca.auditorID=" + permissions.getUserId());
		sql.addWhere("ca.auditStatus IN ('" + AuditStatus.Pending + "','" + AuditStatus.Submitted + "')");
		sql.addWhere("a.active = 'Y'");
		
		orderByDefault = "ca.assignedDate DESC";

		getFilter().setShowAuditor(false);
		getFilter().setShowVisible(false);
		getFilter().setShowPrimaryInformation(false);
		getFilter().setShowTradeInformation(false);
	}
	
	public AuditStatus[] getAuditStatusList() {
		AuditStatus[] list = { AuditStatus.Pending, AuditStatus.Submitted };
		return list;
	}

	public List<AuditType> getAuditTypeList() {
		AuditTypeDAO dao = (AuditTypeDAO) SpringUtils.getBean("AuditTypeDAO");
		List<AuditType> list = dao.findAll();
		List<AuditType> list2 = new ArrayList<AuditType>();

		// Remove the AuditTypes that don't have an audit
		for (AuditType auditType : list)
			if (auditType.isHasAuditor())
				list2.add(auditType);
		return list2;
	}
}
