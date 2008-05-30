package com.picsauditing.actions.report;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.search.SelectFilter;
import com.picsauditing.search.SelectFilterInteger;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;

public class ReportContractorAudits extends ReportAccount {
	protected int[] auditTypeID;
	protected String[] auditStatus;
	protected int auditorId = 0;
	
	
	
	public String execute() throws Exception {
		sql.addJoin("JOIN contractor_audit ca ON ca.conID = a.id");
		sql.addField("ca.auditID");
		sql.addField("ca.createdDate");
		sql.addField("ca.auditStatus");
		sql.addField("ca.expiresDate");
		sql.addField("ca.scheduledDate");
		sql.addField("ca.completedDate");
		sql.addField("ca.assignedDate");
		sql.addField("ca.closedDate");
		sql.addField("ca.auditLocation");
		sql.addField("ca.percentComplete");
		sql.addField("ca.percentVerified");
		sql.addField("ca.auditorID");
		
		
		sql.addJoin("LEFT JOIN audit_type atype ON atype.auditTypeID = ca.auditTypeID");
		sql.addField("atype.auditTypeID");
		sql.addField("atype.auditName");
		sql.addField("atype.isScheduled");
		sql.addField("atype.hasAuditor");
		sql.addField("atype.hasRequirements");
		
		sql.addJoin("LEFT JOIN users auditor ON auditor.id = ca.auditorID");
		sql.addField("auditor.name auditor_name");
		
		if (orderBy == null)
			orderBy = "ca.createdDate DESC";
		return super.execute();
	}

	public List<AuditType> getAuditTypeList() {
		List<AuditType> list = new ArrayList<AuditType>();
		list.add(new AuditType(AuditType.DEFAULT_AUDITTYPE));
		AuditTypeDAO dao = (AuditTypeDAO) SpringUtils.getBean("AuditTypeDAO");
		list.addAll(dao.findAll());
		return list;
	}
	
	public int[] getAuditTypeID() {
		return auditTypeID;
	}

	public void setAuditTypeID(int[] auditTypeID) {
		String auditTypeList = Strings.implode(auditTypeID, ",");
		if (auditTypeList.equals("0"))
			return;
		report.addFilter(new SelectFilterInteger("auditTypeID",
				"ca.auditTypeID IN (?)", Integer.valueOf(auditTypeList)));
		this.auditTypeID = auditTypeID;
	}

	public ArrayList<String> getAuditStatusList() {
		return AuditStatus.getValuesWithDefault();
	}
	
	public String[] getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(String[] auditStatus) {
		String auditStatusList = Strings.implode(auditStatus, ",");
		if (auditStatusList.equals("0"))
			return;
		report.addFilter(new SelectFilter("auditStatus",
				"ca.auditStatus IN (?)", auditStatusList, AuditStatus.DEFAULT, AuditStatus.DEFAULT));
		this.auditStatus = auditStatus;
	}

	public int getAuditorId() {
		return auditorId;
	}

	public void setAuditorId(int auditorId) {
		if( auditorId != 0)
		{
			report.addFilter(new SelectFilterInteger("auditorId",
					"ca.auditorID = '?'", auditorId));
		}
		
		this.auditorId = auditorId;
	}
	
	
	
	
}
