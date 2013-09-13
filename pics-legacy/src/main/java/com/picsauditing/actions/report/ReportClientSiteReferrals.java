package com.picsauditing.actions.report;

import javax.servlet.ServletOutputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.dao.AccountUserDAO;
import com.picsauditing.dao.BasicDAO;
import com.picsauditing.search.SelectFilter;
import com.picsauditing.search.SelectFilterDate;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.ReportFilterAccount;
import com.picsauditing.util.ReportFilterNewClientSite;

@SuppressWarnings("serial")
public class ReportClientSiteReferrals extends ReportActionSupport {
	@Autowired
	protected AccountUserDAO auDAO;
	@Autowired
	protected BasicDAO clientSiteReferralsDAO;

	protected SelectSQL sql;
	protected ReportFilterNewClientSite filter = new ReportFilterNewClientSite();

	@RequiredPermission(value = OpPerms.ClientSiteReferrals)
	public String execute() throws Exception {
		setDefaultFilterDisplay();
		getFilter().setPermissions(permissions);

		buildQuery();
		addFilterToSQL();
		run(sql);

		if (download) {
			addExcelColumns();
			String filename = this.getClass().getSimpleName();
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

	protected void buildQuery() {
		sql = new SelectSQL("operator_referral orl");
		sql.addJoin("LEFT JOIN accounts source ON source.id = orl.sourceID");
		sql.addJoin("LEFT JOIN users uc ON uc.id = orl.lastContactedBy");
		sql.addJoin("LEFT JOIN accounts op ON op.id = orl.opID");

		sql.addField("orl.id");
		sql.addField("orl.name");
		sql.addField("orl.contact");
		sql.addField("orl.phone");
		sql.addField("orl.email");
		sql.addField("op.id AS opID");
		sql.addField("op.name AS opName");
		sql.addField("source.id AS sourceContractorID");
		sql.addField("source.name AS sourceContractorName");
		sql.addField("orl.sourceContact AS sourceContact");
		sql.addField("orl.sourceEmail AS sourceEmail");
		sql.addField("orl.sourcePhone AS sourcePhone");
		sql.addField("orl.closedOnDate");
		sql.addField("orl.creationDate");
		sql.addField("uc.id AS ContactedByID");
		sql.addField("uc.name AS ContactedBy");
		sql.addField("orl.lastContactDate");
		sql.addField("(orl.contactCountByEmail + orl.contactCountByPhone) AS contactCount");
		sql.addField("orl.notes AS Notes");

		sql.addGroupBy("orl.id");

		orderByDefault = "orl.name";
	}

	protected void addFilterToSQL() {
		ReportFilterNewClientSite f = getFilter();

		if (filterOn(f.getStartsWith()))
			report.addFilter(new SelectFilter("startsWith", "orl.name LIKE '?%'", f.getStartsWith()));

		if (filterOn(f.getAccountName(), ReportFilterAccount.getDefaultName())) {
			String accountName = f.getAccountName().trim();
			report.addFilter(new SelectFilter("accountName", "orl.name LIKE '%?%'", accountName));
		}

		if (filterOn(f.getCreationDate1())) {
			report.addFilter(new SelectFilterDate("creationDate1", "orl.creationDate >= '?'", DateBean.format(
					f.getCreationDate1(), "M/d/yy")));
		}

		if (filterOn(f.getCreationDate2())) {
			report.addFilter(new SelectFilterDate("creationDate2", "orl.creationDate < '?'", DateBean.format(
					f.getCreationDate2(), "M/d/yy")));
		}

		if (filterOn(f.getClosedOnDate1())) {
			report.addFilter(new SelectFilterDate("closedOnDate1", "orl.closedOnDate >= '?'", DateBean.format(
					f.getClosedOnDate1(), "M/d/yy")));
		}

		if (filterOn(f.getClosedOnDate2())) {
			report.addFilter(new SelectFilterDate("closedOnDate2", "orl.closedOnDate < '?'", DateBean.format(
					f.getClosedOnDate2(), "M/d/yy")));
		}

		if (filterOn(f.getCustomAPI()) && permissions.isAdmin())
			sql.addWhere(f.getCustomAPI());

		if (filterOn(f.getReferralStatus())) {
			sql.addWhere("orl.status = '" + f.getReferralStatus() + "'");
		}
	}

	public ReportFilterNewClientSite getFilter() {
		return filter;
	}

	protected void addExcelColumns() {
		excelSheet.setData(data);
		
		excelSheet = addColumnsFromSQL(excelSheet, sql);
	}

	private void setDefaultFilterDisplay() {
		getFilter().setShowStatus(false);
		getFilter().setShowPrimaryInformation(false);
		getFilter().setShowTradeInformation(false);
	}
}
