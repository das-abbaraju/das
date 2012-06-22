package com.picsauditing.actions.operators.gc;

import javax.servlet.ServletOutputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.ServletActionContext;

import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.report.ReportActionSupport;
import com.picsauditing.search.SelectFilter;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.ReportFilterAccount;
import com.picsauditing.util.ReportFilterContractor;
import com.picsauditing.util.Strings;
import com.picsauditing.util.excel.ExcelCellType;
import com.picsauditing.util.excel.ExcelColumn;

@SuppressWarnings("serial")
public class GeneralContractorsList extends ReportActionSupport {
	private SelectSQL sql = new SelectSQL("facilities f");
	private ReportFilterContractor filter = new ReportFilterContractor();

	@Override
	public String execute() throws Exception {
		setDefaultFilters();
		buildQuery();

		run(sql);

		if (download || "download".equals(button)) {
			return download();
		}

		return SUCCESS;
	}

	public String download() throws Exception {
		if (data == null || data.isEmpty()) {
			buildQuery();
			download = true;
			run(sql);
		}

		addExcelColumns();

		String className = this.getClass().getSimpleName();
		String filename = className.substring(className.lastIndexOf(".") + 1);

		HSSFWorkbook wb = buildWorkbook(filename);

		filename += ".xls";
		ServletActionContext.getResponse().setContentType("application/vnd.ms-excel");
		ServletActionContext.getResponse().setHeader("Content-Disposition", "attachment; filename=" + filename);
		ServletOutputStream outstream = ServletActionContext.getResponse().getOutputStream();
		wb.write(outstream);
		outstream.flush();
		ServletActionContext.getResponse().flushBuffer();
		return null;
	}

	public ReportFilterContractor getFilter() {
		return filter;
	}

	public void setFilter(ReportFilterContractor filter) {
		this.filter = filter;
	}

	protected void setDefaultFilters() {
		filter.setShowOperator(false);
		filter.setShowTrade(false);
		filter.setShowTaxID(false);
		filter.setShowTaxID(false);
		filter.setShowRiskLevel(false);
		filter.setShowProductRiskLevel(false);
		filter.setShowService(false);
		filter.setShowRegistrationDate(false);
		filter.setShowSoleProprietership(false);
		filter.setShowGeneralContractors(true);
	}

	protected void buildQuery() {
		sql.addJoin("JOIN accounts a ON a.id = f.opID AND a.status = 'Active' AND a.generalContractor = 1");
		sql.addJoin("JOIN generalcontractors gc ON gc.genID = a.id");
		sql.addJoin("JOIN accounts con ON con.id = gc.subID AND con.status = 'Active'");

		sql.addWhere("f.corporateID = " + permissions.getAccountId());
		sql.addWhere("f.type = 'GeneralContractor'");
		sql.addWhere("gc.subID IN (SELECT subID FROM generalcontractors WHERE genID = " + permissions.getAccountId()
				+ ")");

		sql.addField("a.id");
		sql.addField("a.name");
		sql.addField("count(gc.subID) subsShared");

		sql.addGroupBy("a.id");

		addFilterToSQL();
	}

	protected void addFilterToSQL() {
		if (filterOn(getFilter().getStartsWith())) {
			report.addFilter(new SelectFilter("startsWith", "a.nameIndex LIKE '?%'", getFilter().getStartsWith()));
		}

		if (filterOn(getFilter().getAccountName(), ReportFilterAccount.getDefaultName())) {
			String accountName = getFilter().getAccountName().trim();
			report.addFilter(new SelectFilter("accountName", "a.nameIndex LIKE '%" + Strings.indexName(accountName)
					+ "%' OR a.name LIKE '%?%' OR a.dbaName LIKE '%" + Strings.escapeQuotes(accountName)
					+ "%' OR a.id = '" + Strings.escapeQuotes(accountName) + "'", accountName));
			sql.addField("a.dbaName");
		}

		if (filterOn(getFilter().getGeneralContractor())) {
			sql.addWhere("a.id IN (" + Strings.implode(getFilter().getGeneralContractor()) + ")");
		}
	}

	protected void addExcelColumns() {
		excelSheet.setData(data);

		excelSheet.addColumn(new ExcelColumn("name", getText("FacilitiesEdit.GeneralContractor")));
		excelSheet.addColumn(new ExcelColumn("subsShared", getText("GeneralContractorList.SharedSubcontractors"),
				ExcelCellType.Integer));
	}

	protected HSSFWorkbook buildWorkbook(String filename) throws Exception {
		excelSheet.setName(filename);
		return excelSheet.buildWorkbook(permissions.hasPermission(OpPerms.DevelopmentEnvironment));
	}
}
