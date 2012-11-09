package com.picsauditing.actions.report;

import javax.servlet.ServletOutputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.dao.AccountUserDAO;
import com.picsauditing.dao.ContractorRegistrationRequestDAO;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.search.SelectFilter;
import com.picsauditing.search.SelectFilterDate;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.ReportFilterAccount;
import com.picsauditing.util.ReportFilterNewContractor;
import com.picsauditing.util.Strings;
import com.picsauditing.util.excel.ExcelCellType;
import com.picsauditing.util.excel.ExcelColumn;

@Deprecated
@SuppressWarnings("serial")
public class ReportNewRequestedContractor extends ReportActionSupport {
	@Autowired
	protected AccountUserDAO auDAO;
	@Autowired
	protected ContractorRegistrationRequestDAO contractorRegistrationRequestDAO;

	protected SelectSQL sql;
	protected ReportFilterNewContractor filter = new ReportFilterNewContractor();

	@Deprecated
	@RequiredPermission(value = OpPerms.RequestNewContractor)
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

	@Deprecated
	public static SelectSQL buildLegacyQuery() {
		SelectSQL sql = new SelectSQL("contractor_registration_request cr");
		sql.addJoin("JOIN accounts op ON op.id = cr.requestedByID");
		sql.addJoin("LEFT JOIN users u ON u.id = cr.requestedByUserID");
		sql.addJoin("LEFT JOIN users uc ON uc.id = cr.lastContactedBy");
		sql.addJoin("LEFT JOIN accounts con ON con.id = cr.conID");
		sql.addJoin("LEFT JOIN operator_tag ot ON FIND_IN_SET(ot.id, cr.operatorTags) > 0");

		sql.addField("cr.id");
		sql.addField("cr.name");
		sql.addField("cr.contact AS Contact");
		sql.addField("cr.phone AS Phone");
		sql.addField("cr.email AS Email");
		sql.addField("cr.taxID AS TaxID");
		sql.addField("cr.address AS Address");
		sql.addField("cr.city AS City");
		sql.addField("cr.countrySubdivision AS CountrySubdivision");
		sql.addField("cr.zip AS Zip");
		sql.addField("cr.country AS Country");
		sql.addField("op.name AS RequestedBy");
		sql.addField("op.id AS RequestedByID");
		sql.addField("u.name AS RequestedUser");
		sql.addField("u.id AS RequestedUserID");
		sql.addField("cr.requestedByUser AS RequestedByUserOther");
		sql.addField("cr.deadline");
		sql.addField("uc.name AS ContactedBy");
		sql.addField("uc.id AS ContactedByID");
		sql.addField("cr.lastContactDate");
		sql.addField("(cr.contactCountByEmail + cr.contactCountByPhone) AS contactCount");
		sql.addField("cr.matchCount");
		sql.addField("cr.closedOnDate");
		sql.addField("cr.creationDate");
		sql.addField("con.id AS conID");
		sql.addField("con.name AS contractorName");
		sql.addField("cr.notes AS Notes");
		sql.addField("GROUP_CONCAT(ot.tag SEPARATOR ', ') AS operatorTags");
		sql.addField("'CRR' AS systemType");

		sql.addGroupBy("cr.id");

		return sql;
	}

	@Deprecated
	private void buildQuery() {
		sql = buildLegacyQuery();

		if (permissions.isOperatorCorporate()) {
			if (permissions.isCorporate()) {
				getFilter().setShowOperator(true);
				sql.addWhere("op.id IN (" + Strings.implode(permissions.getOperatorChildren()) + ","
						+ permissions.getAccountId() + ")");
			} else
				sql.addWhere("op.id = " + permissions.getAccountId());
		}

		if (permissions.isPicsEmployee()) {
			if (permissions.hasGroup(User.GROUP_CSR) && !getFilter().isViewAll()) {
				sql.addJoin("JOIN user_assignment ua ON ua.country = cr.country AND ua.userID = "
						+ permissions.getUserId());
				sql.addWhere("(cr.countrySubdivision = ua.countrySubdivision OR cr.zip BETWEEN ua.postal_start AND ua.postal_end)");
			}

			if (isAmSales() && !getFilter().isViewAll()) {
				sql.addJoin("JOIN account_user au ON au.accountID = op.id AND au.startDate < NOW() "
						+ "AND au.endDate > NOW() AND au.userID = " + permissions.getUserId());
			}
		}

		orderByDefault = "deadline, name";
	}

	@Deprecated
	private void addFilterToSQL() {
		ReportFilterNewContractor f = getFilter();

		if (filterOn(f.getStartsWith()))
			report.addFilter(new SelectFilter("startsWith", "cr.name LIKE '?%'", f.getStartsWith()));

		if (filterOn(f.getAccountName(), ReportFilterAccount.getDefaultName())) {
			String accountName = f.getAccountName().trim();
			report.addFilter(new SelectFilter("accountName", "cr.name LIKE '%?%'", accountName));
		}

		String locationList = Strings.implodeForDB(f.getLocation(), ",");
		if (filterOn(locationList)) {
			sql.addWhere("cr.countrySubdivision IN (" + locationList + ") OR cr.country IN (" + locationList + ")");
			sql.addOrderBy("CASE WHEN cr.country IN (" + locationList + ") THEN 1 ELSE 2 END, cr.country");
			sql.addOrderBy("CASE WHEN cr.countrySubdivision IN (" + locationList
					+ ") THEN 1 ELSE 2 END, cr.countrySubdivision");
			sql.addOrderBy("cr.country");
			sql.addOrderBy("cr.countrySubdivision");
			setFiltered(true);
		}

		if (filterOn(f.getOperator())) {
			String list = Strings.implode(f.getOperator(), ",");
			sql.addWhere("op.id IN (" + list + ")");
			setFiltered(true);
		}

		if (filterOn(f.getMarketingUsers())) {
			sql.addWhere("cr.lastContactedBy IN (" + Strings.implode(f.getMarketingUsers()) + ")");
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

		if (filterOn(f.getClosedOnDate1())) {
			report.addFilter(new SelectFilterDate("closedOnDate1", "cr.closedOnDate >= '?'", DateBean.format(
					f.getClosedOnDate1(), "M/d/yy")));
		}

		if (filterOn(f.getClosedOnDate2())) {
			report.addFilter(new SelectFilterDate("closedOnDate2", "cr.closedOnDate < '?'", DateBean.format(
					f.getClosedOnDate2(), "M/d/yy")));
		}

		if (filterOn(f.getCustomAPI()) && permissions.isAdmin())
			sql.addWhere(f.getCustomAPI());

		if (filterOn(f.getRequestStatus())) {
			sql.addWhere("cr.status = '" + f.getRequestStatus() + "'");
		}

		if (filterOn(f.getExcludeOperators())) {
			sql.addWhere("cr.requestedByID NOT IN (" + Strings.implode(f.getExcludeOperators()) + ")");
		}

		if (filterOn(f.getOperatorTags())) {
			StringBuilder where = new StringBuilder();

			for (int i = 0; i < f.getOperatorTags().length; i++) {
				if (i > 0)
					where.append(" OR ");

				where.append("(FIND_IN_SET(" + f.getOperatorTags()[i] + ", cr.operatorTags) > 0)");
			}

			sql.addWhere(where.toString());
		}
	}

	public ReportFilterNewContractor getFilter() {
		return filter;
	}

	@Deprecated
	private void addExcelColumns() {
		excelSheet.setData(data);

		excelSheet.addColumn(new ExcelColumn("name", getText("global.CompanyName")));
		excelSheet.addColumn(new ExcelColumn("Contact", getText("ContractorRegistrationRequest.contact")));
		excelSheet.addColumn(new ExcelColumn("Phone", getText("ContractorRegistrationRequest.phone")));
		excelSheet.addColumn(new ExcelColumn("Email", getText("ContractorRegistrationRequest.email")));
		excelSheet.addColumn(new ExcelColumn("TaxID", getText("ContractorRegistrationRequest.taxID")));
		excelSheet.addColumn(new ExcelColumn("Address", getText("ContractorRegistrationRequest.address")));
		excelSheet.addColumn(new ExcelColumn("City", getText("ContractorRegistrationRequest.city")));
		excelSheet.addColumn(new ExcelColumn("CountrySubdivision",
				getText("ContractorRegistrationRequest.countrySubdivision")));
		excelSheet.addColumn(new ExcelColumn("Zip", getText("ContractorRegistrationRequest.zip")));
		excelSheet.addColumn(new ExcelColumn("Country", getText("ContractorRegistrationRequest.country")));

		if (!permissions.isOperatorCorporate()) {
			excelSheet.addColumn(new ExcelColumn("RequestedByID", getText("ContractorRegistrationRequest.requestedBy"),
					ExcelCellType.Integer));
			excelSheet.addColumn(new ExcelColumn("RequestedUser",
					getText("ContractorRegistrationRequest.requestedByUser")));
			excelSheet.addColumn(new ExcelColumn("RequestedByUserOther",
					getText("ContractorRegistrationRequest.requestedByUserOther")));
		}

		excelSheet.addColumn(new ExcelColumn("deadline", getText("ContractorRegistrationRequest.deadline"),
				ExcelCellType.Date));

		if (permissions.isOperatorCorporate())
			excelSheet.addColumn(new ExcelColumn("ContactedBy",
					getText("ContractorRegistrationRequest.lastContactedBy")));
		else
			excelSheet.addColumn(new ExcelColumn("ContactedByID",
					getText("ContractorRegistrationRequest.lastContactedBy")));

		excelSheet.addColumn(new ExcelColumn("lastContactDate",
				getText("ContractorRegistrationRequest.lastContactedDate"), ExcelCellType.Date));
		excelSheet.addColumn(new ExcelColumn("contactCount", getText("ContractorRegistrationRequest.contactCount"),
				ExcelCellType.Integer));
		excelSheet.addColumn(new ExcelColumn("matchCount", getText("ContractorRegistrationRequest.matchCount"),
				ExcelCellType.Integer));
		excelSheet.addColumn(new ExcelColumn("conID", getText("ContractorRegistrationRequest.contractor"),
				ExcelCellType.Integer));
		excelSheet.addColumn(new ExcelColumn("contractorName", getText("global.ContractorName")));
		excelSheet.addColumn(new ExcelColumn("closedOnDate", getText("ReportNewRequestedContractor.ClosedOnDate"),
				ExcelCellType.Date));
		excelSheet.addColumn(new ExcelColumn("Notes", getText("global.Notes")));
		excelSheet.addColumn(new ExcelColumn("operatorTags", getText("RequestNewContractor.OperatorTags")));
	}

	@Deprecated
	public boolean isAmSales() {
		return auDAO.findByUserSalesAM(permissions.getUserId()).size() > 0;
	}

	@Deprecated
	private void setDefaultFilterDisplay() {
		getFilter().setShowOperator(permissions.isPicsEmployee());
		getFilter().setShowLocation(false);
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

		if (permissions.isPicsEmployee()) {
			getFilter().setShowMarketingUsers(true);
			getFilter().setShowViewAll(true);
			getFilter().setShowExcludeOperators(true);
			getFilter().setShowOperatorTags(true);
		}

		if (!permissions.hasGroup(User.GROUP_CSR)) {
			getFilter().setShowLocation(true);
		}
	}
}
