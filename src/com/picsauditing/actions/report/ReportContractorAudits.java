package com.picsauditing.actions.report;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.search.SelectContractorAudit;
import com.picsauditing.search.SelectFilterDate;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;

public class ReportContractorAudits extends ReportAccount {
	protected int[] auditTypeID;
	protected AuditStatus[] auditStatus;
	protected int[] auditorId;

	protected boolean filterAuditType = true;
	protected boolean filterAuditStatus = true;
	protected boolean filterAuditor = true;
	protected boolean filterCreatedDate = true;
	protected boolean filterCompletedDate = true;
	protected boolean filterClosedDate = true;
	protected boolean filterExpiredDate = true;
	protected Date createdDate1;
	protected Date createdDate2;
	protected Date completedDate1;
	protected Date completedDate2;
	protected Date closedDate1;
	protected Date closedDate2;
	protected Date expiredDate1;
	protected Date expiredDate2;

	public ReportContractorAudits() {
		sql = new SelectContractorAudit();
	}

	public String execute() throws Exception {
		loadPermissions();
		sql.addField("ca.createdDate");
		sql.addField("ca.expiresDate");
		sql.addField("ca.scheduledDate");
		sql.addField("ca.completedDate");
		sql.addField("ca.assignedDate");
		sql.addField("ca.closedDate");
		sql.addField("ca.auditLocation");
		sql.addField("ca.percentComplete");
		sql.addField("ca.percentVerified");
		sql.addField("ca.auditorID");

		sql.addField("atype.isScheduled");
		sql.addField("atype.hasAuditor");
		sql.addField("atype.hasRequirements");

		sql.addJoin("LEFT JOIN users auditor ON auditor.id = ca.auditorID");
		sql.addField("auditor.name auditor_name");
		if (permissions.isCorporate() || permissions.isOperator()) {
			if (permissions.getCanSeeAudit().size() == 0) {
				this.addActionError("Your account does not have access to any audits. Please contact PICS.");
				return SUCCESS;
			}
			sql.addWhere("atype.auditTypeID IN (" + Strings.implode(permissions.getCanSeeAudit(), ",") + ")");
		}
		if (orderBy == null)
			orderBy = "ca.createdDate DESC";

		if (!permissions.isPicsEmployee())
			filterAuditor = true;

		if (filtered == null)
			filtered = true;

		return super.execute();
	}

	/**
	 * 
	 * @return List of AuditTypes the current user can see
	 */
	public List<AuditType> getAuditTypeList() {
		AuditTypeDAO dao = (AuditTypeDAO) SpringUtils.getBean("AuditTypeDAO");
		List<AuditType> list = new ArrayList<AuditType>();
		for (AuditType aType : dao.findAll()) {
			if (permissions.canSeeAudit(aType))
				list.add(aType);
		}
		return list;
	}

	public int[] getAuditTypeID() {
		return auditTypeID;
	}

	public void setAuditTypeID(int[] auditTypeID) {
		String auditList = Strings.implode(auditTypeID, ",");
		if (auditList.equals("0"))
			return;

		this.auditTypeID = auditTypeID;
		String auditTypeList = Strings.implode(auditTypeID, ",");
		sql.addWhere("ca.auditTypeID IN (" + auditTypeList + ")");
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
		sql.addWhere("ca.auditStatus IN (" + auditStatusList + ")");
		filtered = true;
	}

	public int[] getAuditorId() {
		return auditorId;
	}

	public void setAuditorId(int[] auditorId) {
		String auditorIdList = Strings.implode(auditorId, ",");

		sql.addWhere("ca.auditorID IN (" + auditorIdList + ")");

		this.auditorId = auditorId;
		filtered = true;
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

	public String getBetterDate(String value, String format) {
		String response = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			Date d = new Date(sdf.parse(value).getTime());

			response = new SimpleDateFormat("MM/dd/yy").format(d);
		} catch (Exception e) {
		}

		return response;
	}

	public String getBetterTime(String value, String format) {
		String response = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			Date d = new Date(sdf.parse(value).getTime());

			response = new SimpleDateFormat("hh:mm a").format(d);

		} catch (Exception e) {
		}

		return response;
	}

	public Date getCreatedDate1() {
		return createdDate1;
	}

	public void setCreatedDate1(Date createdDate1) {
		report.addFilter(new SelectFilterDate("createdDate1", "ca.createdDate >= '?'", DateBean.format(createdDate1,
				"M/d/yy")));
		this.createdDate1 = createdDate1;
	}

	public Date getCreatedDate2() {
		return createdDate2;
	}

	public void setCreatedDate2(Date createdDate2) {
		report.addFilter(new SelectFilterDate("createdDate2", "ca.createdDate < '?'", DateBean.format(createdDate2,
				"M/d/yy")));
		this.createdDate2 = createdDate2;
	}

	public boolean isFilterCreatedDate() {
		return filterCreatedDate;
	}

	public Date getCompletedDate1() {
		return completedDate1;
	}

	public void setCompletedDate1(Date completedDate1) {
		report.addFilter(new SelectFilterDate("completedDate1", "ca.completedDate >= '?'", DateBean.format(
				completedDate1, "M/d/yy")));
		this.completedDate1 = completedDate1;
	}

	public Date getCompletedDate2() {
		return completedDate2;
	}

	public void setCompletedDate2(Date completedDate2) {
		report.addFilter(new SelectFilterDate("completedDate2", "ca.completedDate < '?'", DateBean.format(
				completedDate2, "M/d/yy")));
		this.completedDate2 = completedDate2;
	}

	public Date getClosedDate1() {
		return closedDate1;
	}

	public void setClosedDate1(Date closedDate1) {
		report.addFilter(new SelectFilterDate("closedDate1", "ca.closedDate >= '?'", DateBean.format(closedDate1,
				"M/d/yy")));
		this.closedDate1 = closedDate1;
	}

	public Date getClosedDate2() {
		return closedDate2;
	}

	public void setClosedDate2(Date closedDate2) {
		report.addFilter(new SelectFilterDate("closedDate2", "ca.closedDate < '?'", DateBean.format(closedDate2,
				"M/d/yy")));
		this.closedDate2 = closedDate2;
	}

	public Date getExpiredDate1() {
		return expiredDate1;
	}

	public void setExpiredDate1(Date expiredDate1) {
		report.addFilter(new SelectFilterDate("expiredDate1", "ca.expiresDate >= '?'", DateBean.format(expiredDate1,
				"M/d/yy")));
		this.expiredDate1 = expiredDate1;
	}

	public Date getExpiredDate2() {
		return expiredDate2;
	}

	public void setExpiredDate2(Date expiredDate2) {
		report.addFilter(new SelectFilterDate("expiredDate2", "ca.expiresDate < '?'", DateBean.format(expiredDate2,
				"M/d/yy")));
		this.expiredDate2 = expiredDate2;
	}

	public boolean isFilterCompletedDate() {
		return filterCompletedDate;
	}

	public boolean isFilterClosedDate() {
		return filterClosedDate;
	}

	public boolean isFilterExpiredDate() {
		return filterExpiredDate;
	}

}
