package com.picsauditing.actions.report;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletOutputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.dao.AccountUserDAO;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.search.SelectFilter;
import com.picsauditing.search.SelectFilterDate;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.ReportFilterAccount;
import com.picsauditing.util.ReportFilterNewContractor;
import com.picsauditing.util.Strings;
import com.picsauditing.util.excel.ExcelCellType;
import com.picsauditing.util.excel.ExcelColumn;

@SuppressWarnings("serial")
public class ReportNewRequestedContractor extends ReportActionSupport {
	@Autowired
	protected AccountUserDAO auDAO;

	protected SelectSQL sql;
	protected ReportFilterNewContractor filter = new ReportFilterNewContractor();

	@RequiredPermission(value = OpPerms.RequestNewContractor)
	public String execute() throws Exception {
		getFilter().setShowOperator(false);
		getFilter().setShowLicensedIn(false);
		getFilter().setShowWorksIn(false);
		getFilter().setShowOfficeIn(false);
		getFilter().setShowTaxID(false);
		getFilter().setShowRiskLevel(false);
		getFilter().setShowRegistrationDate(false);
		getFilter().setShowStatus(false);
		getFilter().setShowPrimaryInformation(false);
		getFilter().setShowTradeInformation(false);
		getFilter().setShowConWithPendingAudits(false);
		getFilter().setShowSoleProprietership(false);
		getFilter().setShowProductRiskLevel(false);
		getFilter().setShowTrade(false);
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
		sql = new SelectSQL("contractor_registration_request cr");
		sql.addJoin("JOIN accounts op ON op.id = cr.requestedByID");
		sql.addJoin("LEFT JOIN users u ON u.id = cr.requestedByUserID");
		sql.addJoin("LEFT JOIN users uc ON uc.id = cr.lastContactedBy");
		sql.addJoin("LEFT JOIN accounts con ON con.id = cr.conID");

		sql.addField("cr.id");
		sql.addField("cr.name");
		sql.addField("cr.contact AS Contact");
		sql.addField("cr.phone AS Phone");
		sql.addField("cr.email AS Email");
		sql.addField("cr.taxID AS TaxID");
		sql.addField("cr.address AS Address");
		sql.addField("cr.city AS City");
		sql.addField("cr.state AS State");
		sql.addField("cr.zip AS Zip");
		sql.addField("cr.country AS Country");
		sql.addField("cr.notes AS notes");
		sql.addField("op.name AS RequestedBy");
		sql.addField("op.id AS RequestedByID");
		sql.addField("u.name AS RequestedUser");
		sql.addField("u.id AS RequestedUserID");
		sql.addField("cr.requestedByUser AS RequestedByUserOther");
		sql.addField("cr.deadline");
		sql.addField("uc.name AS ContactedBy");
		sql.addField("uc.id AS ContactedByID");
		sql.addField("cr.lastContactDate");
		sql.addField("cr.contactCount");
		sql.addField("cr.matchCount");
		sql.addField("cr.handledBy");
		sql.addField("cr.creationDate");
		sql.addField("con.id AS conID");
		sql.addField("con.name AS contractorName");
		sql.addField("cr.notes AS Notes");

		orderByDefault = "cr.deadline, cr.name";

		if (permissions.isOperatorCorporate()) {
			getFilter().setShowConAuditor(true);

			if (permissions.isCorporate()) {
				getFilter().setShowOperator(true);
				sql.addWhere("op.id IN (" + Strings.implode(permissions.getOperatorChildren()) + ","
						+ permissions.getAccountId() + ")");
			} else
				sql.addWhere("op.id = " + permissions.getAccountId());
		}

		if (permissions.isPicsEmployee()) {
			getFilter().setShowViewAll(true);
			getFilter().setShowOperator(true);

			if (permissions.hasGroup(User.GROUP_CSR) && !getFilter().isViewAll()) {
				if (!filterOn(getFilter().getHandledBy()))
					getFilter().setHandledBy("PICS");

				sql.addJoin("JOIN user_assignment ua ON ua.country = cr.country AND ua.userID = "
						+ permissions.getUserId());
				sql.addWhere("(cr.state = ua.state OR cr.zip BETWEEN ua.postal_start AND ua.postal_end)");
			}

			if (isAmSales() && !getFilter().isViewAll()) {
				sql.addJoin("JOIN account_user au ON au.accountID = op.id AND au.startDate < NOW() "
						+ "AND au.endDate > NOW() AND au.userID = " + permissions.getUserId());
			}

			if (!permissions.hasGroup(User.GROUP_CSR)) { // Everyone but CSRs
				getFilter().setShowConAuditor(true);
				getFilter().setShowState(true);
				getFilter().setShowCountry(true);
			}
		}
	}

	protected void addFilterToSQL() {
		ReportFilterNewContractor f = getFilter();

		if (filterOn(f.getStartsWith()))
			report.addFilter(new SelectFilter("startsWith", "cr.name LIKE '?%'", f.getStartsWith()));

		if (filterOn(f.getAccountName(), ReportFilterAccount.DEFAULT_NAME)) {
			String accountName = f.getAccountName().trim();
			report.addFilter(new SelectFilter("accountName", "cr.name LIKE '%?%'", accountName));
		}

		String stateList = Strings.implodeForDB(f.getState(), ",");
		if (filterOn(stateList)) {
			sql.addWhere("cr.state IN (" + stateList + ")");
			setFiltered(true);
		}

		String countryList = Strings.implodeForDB(f.getCountry(), ",");
		if (filterOn(countryList) && !filterOn(stateList)) {
			sql.addWhere("cr.country IN (" + countryList + ")");
			setFiltered(true);
		}

		if (filterOn(f.getOperator())) {
			String list = Strings.implode(f.getOperator(), ",");
			sql.addWhere("op.id IN (" + list + ")");
			setFiltered(true);
		}

		if (filterOn(f.getOpen())) {
			sql.addWhere("cr.open = " + f.getOpen());
			setFiltered(true);
		}

		if (filterOn(f.getHandledBy())) {
			sql.addWhere("cr.handledBy = '" + f.getHandledBy() + "'");
			setFiltered(true);
		}

		if (filterOn(f.getConAuditorId())) {
			sql.addJoin("JOIN user_assignment ua ON ua.country = cr.country AND ua.userID IN ("
					+ Strings.implode(f.getConAuditorId()) + ")");
			sql.addWhere("CASE WHEN (cr.zip IS NULL OR ua.postal_start IS NULL) THEN cr.state = ua.state "
					+ "ELSE cr.zip BETWEEN ua.postal_start AND ua.postal_end END");
			setFiltered(true);
		}

		if (filterOn(f.getFollowUpDate())) {
			sql.addWhere("cr.deadline IS NULL OR cr.deadline < '" + DateBean.format(f.getFollowUpDate(), "yyyy-MM-dd")
					+ "'");
			setFiltered(true);
		}

		if (filterOn(f.getCreationDate1())) {
			report.addFilter(new SelectFilterDate("creationDate1", "cr.creationDate >= '?'", DateBean.format(
					f.getCreationDate1(), "M/d/yy")));
		}

		if (filterOn(f.getCreationDate2())) {
			report.addFilter(new SelectFilterDate("creationDate2", "cr.creationDate < '?'", DateBean.format(
					f.getCreationDate2(), "M/d/yy")));
		}

		if (filterOn(f.getCustomAPI()) && permissions.isAdmin())
			sql.addWhere(f.getCustomAPI());
	}

	public ReportFilterNewContractor getFilter() {
		return filter;
	}

	protected void addExcelColumns() {
		excelSheet.setData(data);

		excelSheet.addColumn(new ExcelColumn("name", "Account Name"));
		excelSheet.addColumn(new ExcelColumn("Contact", "Contact Name"));
		excelSheet.addColumn(new ExcelColumn("Phone", "Phone Number"));
		excelSheet.addColumn(new ExcelColumn("Email", "Email Address"));
		excelSheet.addColumn(new ExcelColumn("TaxID", "Tax ID"));
		excelSheet.addColumn(new ExcelColumn("Address", "Address"));
		excelSheet.addColumn(new ExcelColumn("City", "City"));
		excelSheet.addColumn(new ExcelColumn("State", "State"));
		excelSheet.addColumn(new ExcelColumn("Zip", "Zip", ExcelCellType.Integer));
		excelSheet.addColumn(new ExcelColumn("Country", "Country"));

		if (!permissions.isOperatorCorporate()) {
			excelSheet.addColumn(new ExcelColumn("RequestedByID", "Requested By", ExcelCellType.Integer));
			excelSheet.addColumn(new ExcelColumn("RequestedUserID", "Requested By User", ExcelCellType.Integer));
			excelSheet.addColumn(new ExcelColumn("RequestedByUserOther", "Requested By User (Other)"));
		}

		excelSheet.addColumn(new ExcelColumn("deadline", "Deadline Date", ExcelCellType.Date));

		if (permissions.isOperatorCorporate())
			excelSheet.addColumn(new ExcelColumn("ContactedBy", "Contacted By"));
		else
			excelSheet.addColumn(new ExcelColumn("ContactedByID", "Contacted By"));

		excelSheet.addColumn(new ExcelColumn("lastContactDate", "On", ExcelCellType.Date));
		excelSheet.addColumn(new ExcelColumn("contactCount", "Attempts", ExcelCellType.Integer));
		excelSheet.addColumn(new ExcelColumn("matchCount", "Matches", ExcelCellType.Integer));
		excelSheet.addColumn(new ExcelColumn("conID", "PICS ID", ExcelCellType.Integer));
		excelSheet.addColumn(new ExcelColumn("contractorName", "Contractor Name"));
		excelSheet.addColumn(new ExcelColumn("Notes", "Notes"));
	}

	public boolean isAmSales() {
		return auDAO.findByUserSalesAM(permissions.getUserId()).size() > 0;
	}

	public Date getClosedDate(String notes) throws Exception {
		String[] lines = notes.split("\r\n");
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		
		for (String line : lines) {
			if (line.contains("Closed the request.")) {
				String[] pieces = line.split(" - ");
				return sdf.parse(pieces[0]);
			}
		}
		
		return null;
	}
}
