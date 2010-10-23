package com.picsauditing.actions.report;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.ServletActionContext;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AccountUserDAO;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.dao.StateDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.State;
import com.picsauditing.search.SelectFilter;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.ReportFilterAccount;
import com.picsauditing.util.ReportFilterNewContractor;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;
import com.picsauditing.util.excel.ExcelCellType;
import com.picsauditing.util.excel.ExcelColumn;

@SuppressWarnings("serial")
public class ReportNewRequestedContractor extends ReportActionSupport {
	protected ReportFilterNewContractor filter = new ReportFilterNewContractor();
	protected StateDAO stateDAO;
	protected CountryDAO countryDAO;
	protected UserDAO userDAO;

	public ReportNewRequestedContractor(StateDAO stateDAO, CountryDAO countryDAO, UserDAO userDAO) {
		this.stateDAO = stateDAO;
		this.countryDAO = countryDAO;
		this.userDAO = userDAO;
	}

	protected SelectSQL sql = new SelectSQL();

	public SelectSQL getSql() {
		return sql;
	}

	public void setSql(SelectSQL sql) {
		this.sql = sql;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		tryPermissions(OpPerms.RequestNewContractor);
		
		filter.setShowOperator(false);
		filter.setShowTrade(false);
		filter.setShowLicensedIn(false);
		filter.setShowWorksIn(false);
		filter.setShowOfficeIn(false);
		filter.setShowTaxID(false);
		filter.setShowRiskLevel(false);
		filter.setShowRegistrationDate(false);
		filter.setShowIndustry(false);
		filter.setShowAddress(false);
		filter.setPermissions(permissions);

		if (permissions.isOperator()) {
			sql.addWhere("op.id = " + permissions.getAccountId());
		} else if (permissions.isCorporate()) {
			sql.addWhere("op.id IN (SELECT opID FROM facilities WHERE corporateID = " + permissions.getAccountId()
					+ ")");
		} else if (!filter.isViewAll()) {
			List<State> states = stateDAO.findByCSR(permissions.getUserId());
			List<Country> countries = countryDAO.findByCSR(permissions.getUserId());
			String where = "";
			if (states.size() > 0 || countries.size() > 0) {
				filter.setShowOperator(true);
				if (states.size() > 0) {
					List<String> state = new ArrayList<String>();
					for (State s : states) {
						state.add(s.getIsoCode());
					}
					where = "(cr.state IN (" + Strings.implodeForDB(state, ",")+")";
					if(countries.size() > 0)
						where += " OR "; 
				}
				if (countries.size() > 0) {
					List<String> country = new ArrayList<String>();
					for (Country c : countries) {
						country.add(c.getIsoCode());
					}
					where += "cr.country IN (" + Strings.implodeForDB(country, ",")+")";
				}
				where += ")";
				sql.addWhere(where);
				
				if (Strings.isEmpty(filter.getHandledBy()))
					sql.addWhere("cr.handledBy = 'PICS'");
			} else { // Account Managers and Sales Reps
				filter.setShowConAuditor(true);
				filter.setShowState(true);
				filter.setShowCountry(true);
				
				if (isAMSales()) {
					sql.addWhere("cr.requestedByID IN (SELECT DISTINCT accountID FROM account_user " +
							"WHERE userID = " + permissions.getUserId() + ")");
				}
			}
		}
		
		sql.setFromTable("contractor_registration_request cr");
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
		sql.addField("con.id AS conID");
		sql.addField("con.name AS contractorName");
		sql.addField("cr.notes AS Notes");
		
		sql.addOrderBy("cr.deadline, cr.name");
		addFilterToSQL();
		
		this.run(sql);
		
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

	private void addFilterToSQL() {
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
			sql.addJoin("LEFT JOIN ref_state rs ON rs.isoCode = cr.state");
			sql.addJoin("LEFT JOIN ref_country rc ON rc.isoCode = cr.country");
			sql.addField("rs.csrID as stateCSR");
			sql.addField("rc.csrID as countryCSR");
			String list = Strings.implode(f.getConAuditorId(), ",");
			sql.addWhere("rs.csrID IN (" + list + ") OR rc.csrID in (" + list + ")");
			setFiltered(true);
		}
		
		if (filterOn(f.getFollowUpDate())) {
			sql.addWhere("cr.deadline IS NULL OR cr.deadline < '"
					+ DateBean.format(f.getFollowUpDate(), "yyyy-MM-dd") + "'");
			setFiltered(true);
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
	
	public boolean isAMSales() {
		AccountUserDAO auDAO = (AccountUserDAO) SpringUtils.getBean("AccountUserDAO");
		return auDAO.findByUserSalesAM(permissions.getUserId()).size() > 0;
	}
}
