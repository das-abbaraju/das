package com.picsauditing.actions.report;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.beanutils.DynaBean;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.OpPerms;
import com.picsauditing.jpa.entities.ListType;
import com.picsauditing.mail.WizardSession;
import com.picsauditing.search.SelectContractorAudit;
import com.picsauditing.search.SelectFilter;
import com.picsauditing.search.SelectFilterDate;
import com.picsauditing.util.ReportFilterAudit;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportContractorAudits extends ReportAccount {

	private ReportFilterAudit filter = new ReportFilterAudit();

	public ReportContractorAudits() {
		sql = new SelectContractorAudit();
		orderByDefault = "ca.createdDate DESC";
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		addFilterToSQL();

		permissions.tryPermission(OpPerms.ContractorDetails);
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

		if (!permissions.isPicsEmployee())
			getFilter().setShowAuditor(true);
		
		if (filtered == null)
			filtered = true;
		
		getFilter().setShowCerts(false);
		getFilter().setShowInsuranceStatus(false);

		return super.executeOld();
	}

	protected void addFilterToSQL() {
		ReportFilterAudit f = getFilter();

		String auditTypeList = Strings.implode(f.getAuditTypeID(), ",");
		if (filterOn(auditTypeList)) {
			sql.addWhere("ca.auditTypeID IN (" + auditTypeList + ")");
			filtered = true;
		}

		String auditStatusList = Strings.implodeForDB(f.getAuditStatus(), ",");
		if (filterOn(auditStatusList)) {
			sql.addWhere("ca.auditStatus IN (" + auditStatusList + ")");
			filtered = true;
		}

		String auditorIdList = Strings.implode(f.getAuditorId(), ",");
		if (filterOn(auditorIdList)) {
			sql.addWhere("ca.auditorID IN (" + auditorIdList + ")");
			filtered = true;
		}

		String auditIDList = Strings.implode(f.getAuditID(), ",");
		if (filterOn(auditIDList)) {
			sql.addWhere("ca.auditID IN (" + auditIDList + ")");
			filtered = true;
		}

		if (filterOn(f.getCreatedDate1())) {
			report.addFilter(new SelectFilterDate("createdDate1", "ca.createdDate >= '?'", DateBean.format(f
					.getCreatedDate1(), "M/d/yy")));
		}

		if (filterOn(f.getCreatedDate2())) {
			report.addFilter(new SelectFilterDate("createdDate2", "ca.createdDate < '?'", DateBean.format(f
					.getCreatedDate2(), "M/d/yy")));
		}

		if (filterOn(f.getCompletedDate1())) {
			report.addFilter(new SelectFilterDate("completedDate1", "ca.completedDate >= '?'", DateBean.format(f
					.getCompletedDate1(), "M/d/yy")));
		}

		if (filterOn(f.getCompletedDate2())) {
			report.addFilter(new SelectFilterDate("completedDate2", "ca.completedDate < '?'", DateBean.format(f
					.getCompletedDate2(), "M/d/yy")));
		}

		if (filterOn(f.getClosedDate1())) {
			report.addFilter(new SelectFilterDate("closedDate1", "ca.closedDate >= '?'", DateBean.format(f
					.getClosedDate1(), "M/d/yy")));
		}

		if (filterOn(f.getClosedDate2())) {
			report.addFilter(new SelectFilterDate("closedDate2", "ca.closedDate < '?'", DateBean.format(f
					.getClosedDate2(), "M/d/yy")));
		}

		if (filterOn(f.getExpiredDate1())) {
			report.addFilter(new SelectFilterDate("expiredDate1", "ca.expiresDate >= '?'", DateBean.format(f
					.getExpiredDate1(), "M/d/yy")));
		}

		if (filterOn(f.getExpiredDate2())) {
			report.addFilter(new SelectFilterDate("expiredDate2", "ca.expiresDate < '?'", DateBean.format(f
					.getExpiredDate2(), "M/d/yy")));
		}
		
		if (filterOn(f.getPercentComplete1())) {
			report.addFilter(new SelectFilter("percentComplete1", "ca.percentComplete >= '?'", f.getPercentComplete1()));
		}
		
		if (filterOn(f.getPercentComplete2())) {
			report.addFilter(new SelectFilter("percentComplete2", "ca.percentComplete < '?'", f.getPercentComplete2()));
		}
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

	@Override
	protected String returnResult() throws IOException {
		if (mailMerge) {
			Set<Integer> ids = new HashSet<Integer>();
			for (DynaBean dynaBean : data) {
				Long longID = (Long)dynaBean.get("auditID");
				ids.add(longID.intValue());
			}
			WizardSession wizardSession = new WizardSession(ActionContext.getContext().getSession());
			wizardSession.setIds(ids);
			wizardSession.setListTypes(ListType.Audit);
			
			ServletActionContext.getResponse().sendRedirect("MassMailer.action");
			this.addActionMessage("Redirected to MassMailer");
			return BLANK;
		}
		return SUCCESS;
	}

	@Override
	public ReportFilterAudit getFilter() {
		return filter;
	}
}
