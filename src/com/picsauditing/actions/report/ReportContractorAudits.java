package com.picsauditing.actions.report;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletOutputStream;

import org.apache.commons.beanutils.DynaBean;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.OpPerms;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.ListType;
import com.picsauditing.mail.WizardSession;
import com.picsauditing.search.SelectAccount;
import com.picsauditing.search.SelectContractorAudit;
import com.picsauditing.search.SelectFilter;
import com.picsauditing.search.SelectFilterDate;
import com.picsauditing.util.ReportFilterAudit;
import com.picsauditing.util.Strings;
import com.picsauditing.util.excel.ExcelCellType;
import com.picsauditing.util.excel.ExcelColumn;

@SuppressWarnings("serial")
public class ReportContractorAudits extends ReportAccount {
	protected AuditTypeClass auditTypeClass = AuditTypeClass.Audit;
	private ReportFilterAudit filter = new ReportFilterAudit();

	public ReportContractorAudits() {
		orderByDefault = "ca.creationDate DESC";
	}

	@Override
	protected void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.ContractorDetails);
		if (permissions.isCorporate() || permissions.isOperator()) {
			if (permissions.getCanSeeAudit().size() == 0)
				throw new Exception("Your account does not have access to any audits. Please contact PICS.");
		}
	}

	@Override
	protected void buildQuery() {
		sql = new SelectContractorAudit();
		sql.setType(SelectAccount.Type.Contractor);
		if (!skipPermissions)
			sql.setPermissions(permissions);

		if (download) {
			sql.addField("atype.classType");
			if (auditTypeClass == AuditTypeClass.Policy && permissions.isOperator()) {
				sql.addField("cao.notes");
			}
			
			if (permissions.isAdmin()) { // Only for admins?
				sql.addGroupBy("ca.conID");
				sql.addField("GROUP_CONCAT(atype.auditName ORDER BY atype.auditName ASC SEPARATOR ', ') " +
						"AS groupAuditName");
			}
		}

		addFilterToSQL();

		sql.addField("ca.creationDate createdDate");
		sql.addField("ca.expiresDate");
		sql.addField("ca.scheduledDate");
		sql.addField("ca.completedDate");
		sql.addField("ca.assignedDate");
		sql.addField("ca.closedDate");
		sql.addField("ca.auditLocation");
		sql.addField("ca.percentComplete");
		sql.addField("ca.percentVerified");
		sql.addField("ca.auditorID");
		sql.addField("ca.auditFor");

		sql.addField("atype.isScheduled");
		sql.addField("atype.hasAuditor");

		sql.addJoin("LEFT JOIN users auditor ON auditor.id = ca.auditorID");
		sql.addField("auditor.name auditor_name");

		sql.addJoin("JOIN users contact ON contact.id = a.contactID");
		sql.addField("contact.name AS contactname");
		sql.addField("contact.phone AS contactphone");
		sql.addField("contact.email AS contactemail");

		if (permissions.isCorporate() || permissions.isOperator()) {
			sql.addWhere("atype.id IN (" + Strings.implode(permissions.getCanSeeAudit(), ",") + ")");
		}
		if (auditTypeClass != null) {
			if (auditTypeClass == AuditTypeClass.Audit) {
				sql.addWhere("atype.classType in ( 'Audit', 'IM', 'PQF' ) ");
			} else {
				sql.addWhere("atype.classType = '" + auditTypeClass.toString() + "'");
			}
		}

		if (!permissions.isPicsEmployee())
			getFilter().setShowAuditor(true);
		
		if(permissions.isPicsEmployee())
			getFilter().setShowClosingAuditor(true);
		
		getFilter().setShowAuditFor(true);
	}

	@Override
	protected void addExcelColumns() {
		super.addExcelColumns();
		// Remove fields from the parent that are not accessible/necessary here
		excelSheet.removeColumn("creationDate");
		excelSheet.removeColumn("riskLevel");
		excelSheet.removeColumn("fax");
		excelSheet.removeColumn("phone");

		excelSheet.addColumn(new ExcelColumn("auditID", "Audit ID", ExcelCellType.Integer));
		
		if (permissions.isAdmin())
			excelSheet.addColumn(new ExcelColumn("groupAuditName", "Audit Name"));
		else
			excelSheet.addColumn(new ExcelColumn("auditName", "Audit Name"));
		
		excelSheet.addColumn(new ExcelColumn("auditStatus", "Audit Status"));
		excelSheet.addColumn(new ExcelColumn("createdDate", "Creation Date", ExcelCellType.Date));
		excelSheet.addColumn(new ExcelColumn("completedDate", "Completed Date", ExcelCellType.Date));
		excelSheet.addColumn(new ExcelColumn("scheduledDate", "Schedule Date", ExcelCellType.Date));
		excelSheet.addColumn(new ExcelColumn("closedDate", "Audit Closed Date", ExcelCellType.Date));
		excelSheet.addColumn(new ExcelColumn("expiresDate", "Date Expires", ExcelCellType.Date));
		excelSheet.addColumn(new ExcelColumn("percentComplete", "Percent Complete", ExcelCellType.Integer));
		excelSheet.addColumn(new ExcelColumn("percentVerified", "Percent Verified", ExcelCellType.Integer));
		if (auditTypeClass.isPolicy() && permissions.isOperator()) {
			excelSheet.addColumn(new ExcelColumn("caoStatus", "Op Status"));
			excelSheet.addColumn(new ExcelColumn("notes", "Notes"));
		}

	}

	protected void addFilterToSQL() {
		super.addFilterToSQL();

		ReportFilterAudit f = getFilter();
		
		String auditTypeList = Strings.implode(f.getAuditTypeID(), ",");
		String pqfTypeList = Strings.implode(f.getPqfTypeID(), ",");
		if (filterOn(auditTypeList) && filterOn(pqfTypeList)) {
			auditTypeList += ","+ pqfTypeList;
		}
		else if(filterOn(pqfTypeList)) {
			auditTypeList = pqfTypeList;
		}
		if (filterOn(auditTypeList)) {
			sql.addWhere("ca.auditTypeID IN (" + auditTypeList + ")");
			setFiltered(true);
		}

		String auditStatusList = Strings.implodeForDB(f.getAuditStatus(), ",");
		if (filterOn(auditStatusList)) {
			sql.addWhere("ca.auditStatus IN (" + auditStatusList + ")");
			setFiltered(true);
		}

		String caoStatusList = Strings.implodeForDB(f.getCaoStatus(), ",");
		if (filterOn(caoStatusList)) {
			sql.addWhere("cao.status IN (" + caoStatusList + ")");
			setFiltered(true);
		}

		if (filterOn(f.getRecommendedFlag()))
			report.addFilter(new SelectFilter("recommendedFlag", "cao.flag = '?'", f.getRecommendedFlag()));

		String auditorIdList = Strings.implode(f.getAuditorId(), ",");
		if (filterOn(auditorIdList)) {
			sql.addWhere("ca.auditorID IN (" + auditorIdList + ")");
			setFiltered(true);
		}

		String closingAuditorIdList = Strings.implode(f.getClosingAuditorId(), ",");
		if (filterOn(closingAuditorIdList)) {
			sql.addWhere("ca.closingAuditorID IN (" + closingAuditorIdList + ")");
			setFiltered(true);
		}

		String auditIDList = Strings.implode(f.getAuditID(), ",");
		if (filterOn(auditIDList)) {
			sql.addWhere("ca.id IN (" + auditIDList + ")");
			setFiltered(true);
		}

		String auditFor = Strings.implodeForDB(f.getAuditFor(), ",");
		if (filterOn(auditFor)) {
			sql.addWhere("ca.auditFor IN (" + auditFor + ")");
			setFiltered(true);
		}

		if (filterOn(f.getCreatedDate1())) {
			report.addFilter(new SelectFilterDate("createdDate1", "ca.creationDate >= '?'", DateBean.format(f
					.getCreatedDate1(), "M/d/yy")));
		}

		if (filterOn(f.getCreatedDate2())) {
			report.addFilter(new SelectFilterDate("createdDate2", "ca.creationDate < '?'", DateBean.format(f
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

		if (filterOn(f.getHasClosedDate())) {
			if (f.getHasClosedDate().equals("true"))
				sql.addWhere("ca.closedDate is not null ");
			else
				sql.addWhere("ca.closedDate is null ");
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
			report
					.addFilter(new SelectFilter("percentComplete1", "ca.percentComplete >= '?'", f
							.getPercentComplete1()));
		}

		if (filterOn(f.getPercentComplete2())) {
			report.addFilter(new SelectFilter("percentComplete2", "ca.percentComplete < '?'", f.getPercentComplete2()));
		}
		
		if (filterOn(f.getStatusChangedDate1())) {
			report.addFilter(new SelectFilterDate("statusChangedDate1", "cao.statusChangedDate >= '?'", DateBean.format(f
					.getStatusChangedDate1(), "M/d/yy")));
		}

		if (filterOn(f.getStatusChangedDate2())) {
			report.addFilter(new SelectFilterDate("statusChangedDate2", "cao.statusChangedDate < '?'", DateBean.format(f
					.getStatusChangedDate2(), "M/d/yy")));
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
				Long longID = (Long) dynaBean.get("auditID");
				ids.add(longID.intValue());
			}
			WizardSession wizardSession = new WizardSession(ActionContext.getContext().getSession());
			wizardSession.setIds(ids);
			wizardSession.setListTypes(ListType.Audit);

			ServletActionContext.getResponse().sendRedirect("MassMailer.action");
			this.addActionMessage("Redirected to MassMailer");
			return BLANK;
		}

		if (download) {
			addExcelColumns();
			String filename = this.getClass().getName().replace("com.picsauditing.actions.report.", "");
			excelSheet.setName(filename);
			HSSFWorkbook wb = excelSheet.buildWorkbook(permissions.hasPermission(OpPerms.DevelopmentEnvironment));

			filename += ".xls";

			ServletActionContext.getResponse().setContentType("application/vnd.ms-excel");
			ServletActionContext.getResponse().setHeader("Content-Disposition", "attachment; filename=" + filename);
			ServletOutputStream outstream = ServletActionContext.getResponse().getOutputStream();
			wb.write(outstream);
			outstream.flush();
			ServletActionContext.getResponse().flushBuffer();
			return null;
		}

		return SUCCESS;
	}

	@Override
	public ReportFilterAudit getFilter() {
		return filter;
	}
}
