package com.picsauditing.actions.report;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.support.DaoSupport;

import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.search.SelectFilter;
import com.picsauditing.search.SelectFilterInteger;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;

public class ReportContractorAudits extends ReportAccount {
	protected int[] auditTypeID;
	protected AuditStatus[] auditStatus;
	protected int auditorId = 0;
	
	protected boolean filterAuditType = true;
	protected boolean filterAuditStatus = true;
	protected boolean filterAuditor = true;

	
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
	
	protected void toggleFilters() {
		super.toggleFilters();
		
		if (permissions.isOperator() || permissions.isCorporate())
			filterAuditor = false;
	}

	public List<AuditType> getAuditTypeList() {
		AuditTypeDAO dao = (AuditTypeDAO) SpringUtils.getBean("AuditTypeDAO");
		return dao.findAll();
	}
	
	public int[] getAuditTypeID() {
		return auditTypeID;
	}

	public void setAuditTypeID(int[] auditTypeID) {
		this.auditTypeID = auditTypeID;
		String auditTypeList = Strings.implode(auditTypeID, ",");
		sql.addWhere("ca.auditTypeID IN ("+auditTypeList+")");
		filtered = true;
	}

	public AuditStatus[] getAuditStatusList() {
		return AuditStatus.values();
	}
	
	public AuditStatus[] getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(AuditStatus[] auditStatus) {
		this.auditStatus = auditStatus;
		String auditStatusList = Strings.implodeForDB(auditStatus, ",");
		sql.addWhere("ca.auditStatus IN ("+auditStatusList+")");
		filtered = true;
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

	public boolean isFilterAuditType() {
		return filterAuditType;
	}

	public boolean isFilterAuditStatus() {
		return filterAuditStatus;
	}

	public boolean isFilterAuditor() {
		return filterAuditor;
	}
	
	
	
	
}
