package com.picsauditing.actions.report;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.actions.DataConversionRequestAccount;
import com.picsauditing.dao.AccountUserDAO;
import com.picsauditing.dao.ContractorRegistrationRequestDAO;
import com.picsauditing.jpa.entities.ContractorRegistrationRequestStatus;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.ReportFilterAccount;
import com.picsauditing.util.ReportFilterNewContractor;
import com.picsauditing.util.Strings;
import com.picsauditing.util.excel.ExcelCellType;
import com.picsauditing.util.excel.ExcelColumn;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletOutputStream;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class ReportRegistrationRequests extends ReportActionSupport {
	@Autowired
	private AccountUserDAO auDAO;
	@Autowired
	private ContractorRegistrationRequestDAO contractorRegistrationRequestDAO;
	@Autowired
	private FeatureToggle featureToggle;
	private SelectSQL sql;
	private ReportFilterNewContractor filter = new ReportFilterNewContractor();
	@Deprecated
	private SelectSQL legacy;

	public static SelectSQL buildAccountQuery() {
		SelectSQL sql = new SelectSQL("accounts a");
		sql.addJoin("JOIN contractor_info c ON c.id = a.id");
		sql.addJoin("JOIN generalcontractors gc ON gc.subID = c.id");
		sql.addJoin("JOIN accounts op ON op.id = gc.genID");
		sql.addJoin("LEFT JOIN users contact ON contact.id = a.contactID");
		sql.addJoin("LEFT JOIN users requestedUser ON requestedUser.id = gc.requestedByUserID");
		sql.addJoin("LEFT JOIN users pics ON pics.id = c.lastContactedByInsideSales");
		sql.addJoin("LEFT JOIN contractor_tag ct ON ct.conID = c.id");
		sql.addJoin("LEFT JOIN operator_tag ot ON ct.tagID = ot.id > 0");

		sql.addField("c.id");
		sql.addField("a.name");
		sql.addField("contact.name AS Contact");
		sql.addField("contact.phone AS Phone");
		sql.addField("contact.email AS Email");
		sql.addField("c.taxID AS TaxID");
		sql.addField("a.address AS Address");
		sql.addField("a.city AS City");
		sql.addField("a.countrySubdivision AS CountrySubdivision");
		sql.addField("a.zip AS Zip");
		sql.addField("a.country AS Country");
		sql.addField("op.name AS RequestedBy");
		sql.addField("op.id AS RequestedByID");
		sql.addField("requestedUser.id AS RequestedUserID");
		sql.addField("requestedUser.name AS RequestedUser");
		sql.addField("gc.requestedByUser AS RequestedByUserOther");
		sql.addField("gc.deadline");
		sql.addField("pics.name AS ContactedBy");
		sql.addField("pics.id AS ContactedByID");
		sql.addField("c.lastContactedByInsideSalesDate AS lastContactDate");
		sql.addField("(c.contactCountByEmail + c.contactCountByPhone) AS contactCount");
		sql.addField("'' AS matchCount");
		sql.addField("c.expiresOnDate AS closedOnDate");
		sql.addField("a.creationDate");
		sql.addField("a.id AS conID");
		sql.addField("a.name AS contractorName");
		sql.addField("'' AS Notes");
		sql.addField("GROUP_CONCAT(ot.tag SEPARATOR ', ') AS operatorTags");
		sql.addField("'ACC' AS systemType");
		sql.addField("CASE c.insideSalesPriority WHEN 1 THEN 'Low' WHEN 2 THEN 'Med' WHEN 3 THEN 'High' ELSE 'None' END AS priority");
		// Find requested or denied or pending but not active that have been
		// requested
		sql.addWhere("a.status = 'Requested' OR (a.status = 'Declined' AND a.reason IS NOT NULL) "
				+ "OR (a.status = 'Pending' AND a.id IN (SELECT subID FROM generalcontractors "
				+ "WHERE (requestedByUser IS NOT NULL OR requestedByUserID > 0)))");

		sql.addGroupBy("c.id, gc.id");

		return sql;
	}

	@RequiredPermission(value = OpPerms.RequestNewContractor)
	public String execute() throws Exception {
		if (permissions.isOperatorCorporate()
				&& featureToggle.isFeatureEnabled(FeatureToggle.TOGGLE_REQUESTNEWCONTRACTORACCOUNT)) {
			DataConversionRequestAccount justInTimeConversion = new DataConversionRequestAccount(dao, permissions);
			justInTimeConversion.upgrade();
		}

		setDefaultFilterDisplay();
		getFilter().setPermissions(permissions);

		buildQuery();

		List<SelectSQL> unions = new ArrayList<SelectSQL>();
		if (permissions.isPicsEmployee()) {
			getFilter().setShowInsideSalesPriority(true);
			unions.add(legacy);
		}

		run(sql, unions);

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

	public boolean isAccountManagerOrSalesRepresentative() {
		return auDAO.findByUserSalesAM(permissions.getUserId()).size() > 0;
	}

	private void buildQuery() {
		sql = buildAccountQuery();
		legacy = ReportNewRequestedContractor.buildLegacyQuery();

		addFilterToSQL();

		if (isAccountManagerOrSalesRepresentative() && !getFilter().isViewAll()) {
			sql.addJoin("JOIN account_user au ON au.accountID = c.requestedByID AND au.startDate < NOW() "
					+ "AND au.endDate > NOW() AND au.userID = " + permissions.getUserId());
			legacy.addJoin("JOIN account_user au ON au.accountID = cr.requestedByID AND au.startDate < NOW() "
					+ "AND au.endDate > NOW() AND au.userID = " + permissions.getUserId());
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

		legacy.addWhere("cr.conID NOT IN (SELECT id FROM accounts WHERE status IN ('Requested', 'Declined')) OR cr.conID IS NULL");

		orderByDefault = "deadline, name";
	}

	private void addFilterToSQL() {
		ReportFilterNewContractor f = getFilter();

		if (filterOn(f.getStartsWith())) {
			String startsWith = Strings.escapeQuotes(f.getStartsWith());
			sql.addWhere("a.name LIKE '" + startsWith + "%'");
			legacy.addWhere("cr.name LIKE '" + startsWith + "%'");

			setFiltered(true);
		}

		if (filterOn(f.getAccountName(), ReportFilterAccount.getDefaultName())) {
			String accountName = Strings.escapeQuotes(f.getAccountName().trim());

			sql.addWhere("a.name LIKE '%" + accountName + "%'");
			legacy.addWhere("cr.name LIKE '%" + accountName + "%'");

			setFiltered(true);
		}

		if (filterOn(f.getRequestStatus())) {
			legacy.addWhere("cr.status = '" + f.getRequestStatus().toString() + "'");

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
				sql.addWhere("a.status IN ('Declined') AND a.reason IS NOT NULL");
			}

			setFiltered(true);
		}

		String locationList = Strings.implodeForDB(f.getLocation(), ",");
		if (filterOn(locationList)) {
			sql.addWhere("a.countrySubdivision IN (" + locationList + ") OR a.country IN (" + locationList + ")");
			sql.addOrderBy("CASE WHEN a.country IN (" + locationList + ") THEN 1 ELSE 2 END");
			sql.addOrderBy("CASE WHEN a.countrySubdivision IN (" + locationList + ") THEN 1 ELSE 2 END");
			sql.addOrderBy("a.country");
			sql.addOrderBy("a.countrySubdivision");

			legacy.addWhere("cr.countrySubdivision IN (" + locationList + ") OR cr.country IN (" + locationList + ")");
			legacy.addOrderBy("CASE WHEN cr.country IN (" + locationList + ") THEN 1 ELSE 2 END");
			legacy.addOrderBy("CASE WHEN cr.countrySubdivision IN (" + locationList + ") THEN 1 ELSE 2 END");
			legacy.addOrderBy("cr.country");
			legacy.addOrderBy("cr.countrySubdivision");

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
			legacy.addWhere("cr.lastContactedByInsideSales IN (" + Strings.implode(f.getMarketingUsers()) + ")");

			setFiltered(true);
		}

		if (filterOn(f.getFollowUpDate())) {
			sql.addWhere("c.followupDate IS NULL OR c.followupDate < '"
					+ DateBean.format(f.getFollowUpDate(), "yyyy-MM-dd") + "'");
			legacy.addWhere("cr.holdDate IS NULL OR cr.holdDate < '"
					+ DateBean.format(f.getFollowUpDate(), "yyyy-MM-dd") + "'");

			setFiltered(true);
		}

		filterOnCreationDate(f);

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
			sql.addWhere(String.format("ot.id IN (%s)", Strings.implode(f.getOperatorTags())));
			legacy.addWhere(String.format("ot.id IN (%s)", Strings.implode(f.getOperatorTags())));

			setFiltered(true);
		}

		if (filterOn(f.getInsideSalesPriority())) {
			sql.addWhere(String.format("c.insideSalesPriority = %d", f.getInsideSalesPriority().ordinal()));
			// TODO Do we want to filter out old registration requests?
			// Old reg requests do not have priority
			legacy.addWhere("1<>1");

			setFiltered(true);
		}
	}

	private void filterOnCreationDate(ReportFilterNewContractor f) {
		if (filterOn(f.getCreationDate1())) {
			sql.addWhere(String.format("a.creationDate >= '%s'", DateBean.toDBFormat(f.getCreationDate1())));
			legacy.addWhere(String.format("cr.creationDate >= '%s'", DateBean.toDBFormat(f.getCreationDate1())));

			setFiltered(true);
		}

		if (filterOn(f.getCreationDate2())) {
			sql.addWhere(String.format("a.creationDate < '%s'", DateBean.toDBFormat(f.getCreationDate2())));
			legacy.addWhere(String.format("cr.creationDate < '%s'", DateBean.toDBFormat(f.getCreationDate1())));

			setFiltered(true);
		}

		if (filterOn(f.getClosedOnDate1())) {
			sql.addWhere(String.format("c.expiresOnDate >= '%s'", DateBean.toDBFormat(f.getClosedOnDate1())));
			legacy.addWhere(String.format("cr.closedOnDate >= '%s'", DateBean.toDBFormat(f.getCreationDate1())));

			setFiltered(true);
		}

		if (filterOn(f.getClosedOnDate2())) {
			sql.addWhere(String.format("c.expiresOnDate < '%s'", DateBean.toDBFormat(f.getClosedOnDate2())));
			legacy.addWhere(String.format("cr.closedOnDate < '%s'", DateBean.toDBFormat(f.getCreationDate1())));

			setFiltered(true);
		}
	}

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
			getFilter().setShowExcludeOperators(true);
			getFilter().setShowOperatorTags(true);

			if (isAccountManagerOrSalesRepresentative()) {
				getFilter().setShowViewAll(true);
			}
		}
	}
}
