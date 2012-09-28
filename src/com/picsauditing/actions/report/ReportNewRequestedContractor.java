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
import com.picsauditing.jpa.entities.ContractorRegistrationRequestStatus;
import com.picsauditing.jpa.entities.User;
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
	@Autowired
	protected ContractorRegistrationRequestDAO contractorRegistrationRequestDAO;

	protected SelectSQL sql;
	protected ReportFilterNewContractor filter = new ReportFilterNewContractor();

	@RequiredPermission(value = OpPerms.RequestNewContractor)
	public String execute() throws Exception {
		setDefaultFilterDisplay();
		getFilter().setPermissions(permissions);

		buildQuery();

		if (report.getUnionSql().isEmpty()) {
			run(sql);
		} else {
			run(sql, report.getUnionSql());
		}

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

	public ReportFilterNewContractor getFilter() {
		return filter;
	}

	public void setFilter(ReportFilterNewContractor filter) {
		this.filter = filter;
	}

	public boolean isAmSales() {
		return auDAO.findByUserSalesAM(permissions.getUserId()).size() > 0;
	}

	protected void buildQuery() {
		sql = buildNewQuery();
		SelectSQL legacy = buildLegacyQuery();

		addFilterToSQL(legacy);

		report.getUnionSql().add(legacy);

		if (permissions.isPicsEmployee()) {
			if (permissions.hasGroup(User.GROUP_CSR) && !getFilter().isViewAll()) {
				sql.addJoin("JOIN user_assignment ua ON ua.country = a.country AND ua.userID = "
						+ permissions.getUserId() + " AND (a.countrySubdivision = ua.countrySubdivision OR a.zip "
						+ "BETWEEN ua.postal_start AND ua.postal_end)");

				legacy.addJoin("JOIN user_assignment ua ON ua.country = cr.country AND ua.userID = "
						+ permissions.getUserId() + " AND (cr.countrySubdivision = ua.countrySubdivision OR cr.zip "
						+ "BETWEEN ua.postal_start AND ua.postal_end)");
			}

			if (isAmSales() && !getFilter().isViewAll()) {
				sql.addJoin("JOIN account_user au ON au.accountID = a.requestedByID AND au.startDate < NOW() "
						+ "AND au.endDate > NOW() AND au.userID = " + permissions.getUserId());

				legacy.addJoin("JOIN account_user au ON au.accountID = cr.requestedByID AND au.startDate < NOW() "
						+ "AND au.endDate > NOW() AND au.userID = " + permissions.getUserId());
			}
		}

		if (permissions.isOperatorCorporate()) {
			if (permissions.isCorporate()) {
				getFilter().setShowOperator(true);
				sql.addWhere("gc.genID IN (" + Strings.implode(permissions.getOperatorChildren()) + ","
						+ permissions.getAccountId() + ")");

				legacy.addWhere("cr.requestedByID IN (" + Strings.implode(permissions.getOperatorChildren()) + ","
						+ permissions.getAccountId() + ")");
			} else {
				sql.addWhere("gc.genID = " + permissions.getAccountId());
				legacy.addWhere("cr.requestedByID = " + permissions.getAccountId());
			}
		}

		orderByDefault = "deadline, name";
	}

	protected void addFilterToSQL(SelectSQL legacy) {
		ReportFilterNewContractor f = getFilter();

		if (filterOn(f.getStartsWith())) {
			sql.addWhere("a.name LIKE '" + f.getStartsWith() + "%'");
			legacy.addWhere("cr.name LIKE '" + f.getStartsWith() + "%'");

			setFiltered(true);
		}

		if (filterOn(f.getAccountName(), ReportFilterAccount.getDefaultName())) {
			String accountName = f.getAccountName().trim();

			sql.addWhere("a.name LIKE '%" + accountName + "%'");
			legacy.addWhere("cr.name LIKE '%" + accountName + "%'");

			setFiltered(true);
		}

		if (filterOn(f.getRequestStatus())) {
			legacy.addWhere("cr.status = '" + f.getRequestStatus() + "'");

			if (ContractorRegistrationRequestStatus.Active.toString().equals(getFilter().getRequestStatus())) {
				sql.addWhere("a.status IN ('Requested', 'Pending') AND c.followupDate IS NULL");
			} else if (ContractorRegistrationRequestStatus.Hold.toString().equals(getFilter().getRequestStatus())) {
				sql.addWhere("a.status IN ('Requested', 'Pending') AND c.followupDate IS NOT NULL");
			} else if (ContractorRegistrationRequestStatus.ClosedSuccessful.toString().equals(
					getFilter().getRequestStatus())) {
				sql.addWhere("a.status IN ('Active') AND c.contactCountByPhone = 0");
			} else if (ContractorRegistrationRequestStatus.ClosedContactedSuccessful.toString().equals(
					getFilter().getRequestStatus())) {
				sql.addWhere("a.status IN ('Active') AND c.contactCountByPhone > 0");
			} else {
				sql.addWhere("a.status IN ('Deactivated') AND a.reason IS NOT NULL");
			}
		}

		String locationList = Strings.implodeForDB(f.getLocation(), ",");
		if (filterOn(locationList)) {
			sql.addWhere("a.countrySubdivision IN (" + locationList + ") OR a.country IN (" + locationList + ")");
			sql.addOrderBy("CASE WHEN country IN (" + locationList + ") THEN 1 ELSE 2 END");
			sql.addOrderBy("CASE WHEN countrySubdivision IN (" + locationList + ") THEN 1 ELSE 2 END");
			sql.addOrderBy("country");
			sql.addOrderBy("countrySubdivision");

			legacy.addWhere("cr.countrySubdivision IN (" + locationList + ") OR cr.country IN (" + locationList + ")");

			setFiltered(true);
		}

		if (filterOn(f.getOperator())) {
			String list = Strings.implode(f.getOperator(), ",");

			sql.addWhere("gc.genID IN (" + list + ")");
			legacy.addWhere("cr.requestedByID IN (" + list + ")");

			setFiltered(true);
		}

		if (filterOn(f.getMarketingUsers())) {
			sql.addWhere("c.lastContactedByInsideSales IN (" + Strings.implode(f.getMarketingUsers()) + ")");
			legacy.addWhere("cr.lastContactedBy IN (" + Strings.implode(f.getMarketingUsers()) + ")");

			setFiltered(true);
		}

		if (filterOn(f.getFollowUpDate())) {
			sql.addWhere("c.followupDate IS NULL OR c.followupDate < '"
					+ DateBean.format(f.getFollowUpDate(), "yyyy-MM-dd") + "'");
			legacy.addWhere("cr.deadline IS NULL OR cr.deadline < '"
					+ DateBean.format(f.getFollowUpDate(), "yyyy-MM-dd") + "'");

			setFiltered(true);
		}

		if (filterOn(f.getCreationDate1())) {
			sql.addWhere(String.format("a.creationDate >= '%s'", DateBean.toDBFormat(f.getCreationDate1())));
			legacy.addWhere(String.format("cr.creationDate >= '%s'", DateBean.toDBFormat(f.getCreationDate1())));
			setFiltered(true);
		}

		if (filterOn(f.getCreationDate2())) {
			sql.addWhere(String.format("a.creationDate < '%s'", DateBean.toDBFormat(f.getCreationDate2())));
			legacy.addWhere(String.format("cr.creationDate < '%s'", DateBean.toDBFormat(f.getCreationDate2())));
			setFiltered(true);
		}

		if (filterOn(f.getClosedOnDate1())) {
			sql.addWhere(String.format("c.expiresOnDate >= '%s'", DateBean.toDBFormat(f.getClosedOnDate1())));
			legacy.addWhere(String.format("cr.closedOnDate >= '%s'", DateBean.toDBFormat(f.getClosedOnDate1())));
			setFiltered(true);
		}

		if (filterOn(f.getClosedOnDate2())) {
			sql.addWhere(String.format("c.expiresOnDate < '%s'", DateBean.toDBFormat(f.getClosedOnDate2())));
			legacy.addWhere(String.format("cr.closedOnDate < '%s'", DateBean.toDBFormat(f.getClosedOnDate2())));
			setFiltered(true);
		}

		if (filterOn(f.getCustomAPI()) && permissions.isAdmin()) {
			sql.addWhere(f.getCustomAPI());

			legacy.addWhere(f.getCustomAPI());

			setFiltered(true);
		}

		if (filterOn(f.getExcludeOperators())) {
			sql.addWhere("gc.genID NOT IN (" + Strings.implode(f.getExcludeOperators()) + ")");

			legacy.addWhere("cr.requestedByID NOT IN (" + Strings.implode(f.getExcludeOperators()) + ")");

			setFiltered(true);
		}

		if (filterOn(f.getOperatorTags())) {
			StringBuilder where = new StringBuilder();

			for (int i = 0; i < f.getOperatorTags().length; i++) {
				if (i > 0)
					where.append(" OR ");

				where.append("(FIND_IN_SET(" + f.getOperatorTags()[i] + ", operatorTags) > 0)");
			}

			sql.addWhere(where.toString());

			legacy.addWhere(where.toString());
		}
	}

	protected void addExcelColumns() {
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
		excelSheet.addColumn(new ExcelColumn("operatorTags", getText("RequestNewContractor.OperatorTags")));
	}

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

	public static SelectSQL buildLegacyQuery() {
		SelectSQL sql_legacy = new SelectSQL("contractor_registration_request cr");
		sql_legacy.addJoin("JOIN accounts op ON op.id = cr.requestedByID");
		sql_legacy.addJoin("LEFT JOIN users u ON u.id = cr.requestedByUserID");
		sql_legacy.addJoin("LEFT JOIN users uc ON uc.id = cr.lastContactedBy");
		sql_legacy.addJoin("LEFT JOIN accounts con ON con.id = cr.conID");
		sql_legacy.addJoin("LEFT JOIN operator_tag ot ON FIND_IN_SET(ot.id, cr.operatorTags) > 0");

		sql_legacy.addField("cr.id");
		sql_legacy.addField("cr.name");
		sql_legacy.addField("cr.contact AS Contact");
		sql_legacy.addField("cr.phone AS Phone");
		sql_legacy.addField("cr.email AS Email");
		sql_legacy.addField("cr.taxID AS TaxID");
		sql_legacy.addField("cr.address AS Address");
		sql_legacy.addField("cr.city AS City");
		sql_legacy.addField("cr.countrySubdivision AS CountrySubdivision");
		sql_legacy.addField("cr.zip AS Zip");
		sql_legacy.addField("cr.country AS Country");
		sql_legacy.addField("op.name AS RequestedBy");
		sql_legacy.addField("op.id AS RequestedByID");
		sql_legacy.addField("u.name AS RequestedUser");
		sql_legacy.addField("u.id AS RequestedUserID");
		sql_legacy.addField("cr.requestedByUser AS RequestedByUserOther");
		sql_legacy.addField("cr.deadline");
		sql_legacy.addField("uc.name AS ContactedBy");
		sql_legacy.addField("uc.id AS ContactedByID");
		sql_legacy.addField("cr.lastContactDate");
		sql_legacy.addField("(cr.contactCountByEmail + cr.contactCountByPhone) AS contactCount");
		sql_legacy.addField("cr.closedOnDate");
		sql_legacy.addField("cr.creationDate");
		sql_legacy.addField("con.id AS conID");
		sql_legacy.addField("con.name AS contractorName");
		sql_legacy.addField("GROUP_CONCAT(ot.tag SEPARATOR ', ') AS operatorTags");
		sql_legacy.addField("'CRR' AS systemType");

		sql_legacy.addWhere("(con.status NOT IN ('Requested', 'Deactivated') OR con.id IS NULL)");

		sql_legacy.addGroupBy("cr.id");

		return sql_legacy;
	}

	public static SelectSQL buildNewQuery() {
		SelectSQL sql_new = new SelectSQL("accounts a");
		sql_new.addJoin("JOIN contractor_info c ON c.id = a.id");
		sql_new.addJoin("JOIN generalcontractors gc ON gc.subID = c.id");
		sql_new.addJoin("JOIN accounts op ON op.id = gc.genID");
		sql_new.addJoin("LEFT JOIN users contact ON contact.id = a.contactID");
		sql_new.addJoin("LEFT JOIN users requestedUser ON requestedUser.id = gc.requestedByUserID");
		sql_new.addJoin("LEFT JOIN users pics ON pics.id = c.lastContactedByInsideSales");
		sql_new.addJoin("LEFT JOIN contractor_tag ct ON ct.conID = c.id");
		sql_new.addJoin("LEFT JOIN operator_tag ot ON ct.tagID = ot.id > 0");

		sql_new.addField("c.id");
		sql_new.addField("a.name");
		sql_new.addField("contact.name AS Contact");
		sql_new.addField("contact.phone AS Phone");
		sql_new.addField("contact.email AS Email");
		sql_new.addField("c.taxID AS TaxID");
		sql_new.addField("a.address AS Address");
		sql_new.addField("a.city AS City");
		sql_new.addField("a.countrySubdivision AS CountrySubdivision");
		sql_new.addField("a.zip AS Zip");
		sql_new.addField("a.country AS Country");
		sql_new.addField("op.name AS RequestedBy");
		sql_new.addField("op.id AS RequestedByID");
		sql_new.addField("requestedUser.id AS RequestedUserID");
		sql_new.addField("requestedUser.name AS RequestedUser");
		sql_new.addField("gc.requestedByUser AS RequestedByUserOther");
		sql_new.addField("gc.deadline");
		sql_new.addField("pics.name AS ContactedBy");
		sql_new.addField("pics.id AS ContactedByID");
		sql_new.addField("c.lastContactedByInsideSalesDate AS lastContactDate");
		sql_new.addField("(c.contactCountByEmail + c.contactCountByPhone) AS contactCount");
		sql_new.addField("c.expiresOnDate AS closedOnDate");
		sql_new.addField("a.creationDate");
		sql_new.addField("a.id AS conID");
		sql_new.addField("a.name AS contractorName");
		sql_new.addField("GROUP_CONCAT(ot.tag SEPARATOR ', ') AS operatorTags");
		sql_new.addField("'ACC' AS systemType");

		sql_new.addWhere("a.status = 'Requested' OR (a.status = 'Deactivated' AND a.reason IS NOT NULL)");

		sql_new.addGroupBy("c.id, gc.id");

		return sql_new;
	}
}
