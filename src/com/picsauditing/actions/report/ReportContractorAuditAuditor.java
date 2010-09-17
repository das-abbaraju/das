package com.picsauditing.actions.report;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.util.SpringUtils;

@SuppressWarnings("serial")
public class ReportContractorAuditAuditor extends ReportContractorAuditOperator {

	@Override
	public void buildQuery() {
		super.buildQuery();

		sql.addWhere("(ca.auditorID=" + permissions.getUserId()+" OR ca.closingAuditorID=" + permissions.getUserId()+")");
		sql.addWhere("cao.status IN ('" + AuditStatus.Pending + "','" + AuditStatus.Submitted + "')");
		sql.addWhere("a.status IN ('Active','Demo')");
		
		orderByDefault = "ca.assignedDate DESC";

		getFilter().setShowAuditor(false);
		getFilter().setShowStatus(false);
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
