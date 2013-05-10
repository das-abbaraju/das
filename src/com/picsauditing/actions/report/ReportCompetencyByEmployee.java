package com.picsauditing.actions.report;

import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectFilter;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.PermissionQueryBuilderEmployee;
import com.picsauditing.util.ReportFilterAccount;
import com.picsauditing.util.ReportFilterEmployee;
import com.picsauditing.util.Strings;
import com.picsauditing.util.excel.ExcelCellType;
import com.picsauditing.util.excel.ExcelColumn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class ReportCompetencyByEmployee extends ReportEmployee {

    private final Logger logger = LoggerFactory.getLogger(ReportCompetencyByEmployee.class);

    public ReportCompetencyByEmployee() {
        orderByDefault = "Set1.name, Set1.lastName, Set1.firstName";
    }

    @Override
    protected void buildQuery() {
        super.buildQuery();

        sql = new SelectSQL("Set1");

        sql.addField("Set1.accountID");
        sql.addField("Set1.name");
        sql.addField("Set1.id AS employeeID");
        sql.addField("Set1.firstName");
        sql.addField("Set1.lastName");
        sql.addField("Set1.title");
        sql.addField("Set1.classification");
        sql.addField("Set1.hireDate");
        sql.addField("Set1.email");
        sql.addField("Set1.phone");
        sql.addField("Set1.twicExpiration");
        sql.addField("Set1.roles");
        sql.addField("Set1.skilled");
        sql.addField("Set1.required");
        sql.addField("Set1.percent");


        if (permissions.isOperator()) {
            String accountStatus = "'Active'";
            if (permissions.getAccountStatus().isDemo())
                accountStatus += ", 'Demo'";

            sql.addJoin("JOIN generalcontractors gc ON gc.subID = Set1.accountID AND gc.genID IN "
                    + "(SELECT f.opID FROM facilities f WHERE f.corporateID NOT IN ("
                    + Strings.implode(Account.PICS_CORPORATE) + ") AND f.corporateID IN ("
                    + Strings.implode(permissions.getCorporateParent()) + "))");
            sql.addJoin(String.format("JOIN accounts o ON o.id = gc.genID AND o.status IN (%s)", accountStatus));
            sql.addJoin(String.format(
                    "LEFT JOIN (SELECT subID FROM generalcontractors WHERE genID = %d) gcw ON gcw.subID = Set1.accountID",
                    permissions.getAccountId()));
            sql.addField("ISNULL(gcw.subID) notWorksFor");
        }

        if (permissions.isCorporate()) {
            PermissionQueryBuilderEmployee permQuery = new PermissionQueryBuilderEmployee(permissions);
            permQuery.setAccountAlias("Set1");
            sql.addWhere("1 " + permQuery.toString());
        }

        sql.addGroupBy("Set1.id");

        addFilterToSQL();
    }

    public String execute() throws Exception {
        getFilter().setShowSsn(false);
        getFilter().setShowOperators(true);

        buildQuery();
        String dropTempTable = "DROP TEMPORARY TABLE IF EXISTS Set1;";
        String createTempTable = "CREATE TEMPORARY TABLE IF NOT EXISTS Set1 \n" +
                "SELECT sub.id accountID, sub.name, sub.dbaName, sub.nameIndex, employee.id AS id, employee.active, employee.firstName, employee.lastName, employee.title, \n" +
                "CONCAT('EmployeeClassification.', employee.classification, '.description') classification, employee.hireDate, employee.email, \n" +
                "employee.phone, employee.twicExpiration, required.name roles, IFNULL(skilled.counts, 0) skilled, IFNULL(required.counts, 0) required, \n" +
                "ROUND((IFNULL(skilled.counts, 0) / IFNULL(required.counts, 1)) * 100) percent, sub.status, sub.requiresCompetencyReview \n" +
                "FROM employee \n" +
                "JOIN accounts sub ON sub.id = employee.accountID AND sub.status = 'Active' AND sub.requiresCompetencyReview = 1 \n" +
                "LEFT JOIN vwHSECompetenciesRequiredCount\trequired ON required.employeeID = employee.id \n" +
                "LEFT JOIN vwHSECompetenciesSkilledCount skilled ON skilled.employeeID = employee.id; \n";


        Database db = new Database();

        try {
            db.executeUpdate(createTempTable);
        } catch (SQLException se) {
            logger.error(se.getMessage());
        }

        run(sql);

        try {
            db.executeUpdate(dropTempTable);
        } catch (SQLException se) {
            logger.error(se.getMessage());
        }

        if (download || "download".equals(button))
            return download();

        return SUCCESS;
    }

    protected void addFilterToSQL() {
        ReportFilterEmployee f = getFilter();

        if (filterOn(f.getAccountName(), ReportFilterAccount.getDefaultName())) {
            String accountName = Strings.escapeQuotes(f.getAccountName().trim());
            report.addFilter(new SelectFilter("name", "Set1.nameIndex LIKE '%" + Strings.indexName(accountName)
                    + "%' OR Set1.name LIKE '%?%' OR Set1.dbaName LIKE '%" + Strings.escapeQuotes(accountName)
                    + "%' OR Set1.accountID = '" + Strings.escapeQuotes(accountName) + "'", accountName));
            f.setLimitEmployees(false);
        }

        if (filterOn(f.getFirstName())) {
            String firstName = Strings.escapeQuotes(f.getFirstName().trim());
            sql.addWhere("Set1.firstName LIKE '%" + firstName + "%'");
        }

        if (filterOn(f.getLastName())) {
            String lastName = Strings.escapeQuotes(f.getLastName().trim());
            sql.addWhere("Set1.lastName LIKE '%" + lastName + "%'");
        }

        if (filterOn(f.getEmail())) {
            String email = Strings.escapeQuotes(f.getEmail().trim());
            sql.addWhere("Set1.email LIKE '%" + email + "%'");
        }

        if (f.isLimitEmployees() && f.isShowLimitEmployees())
            sql.addWhere("Set1.accountID = " + permissions.getAccountId());

        if (filterOn(f.getOperators())) {
            List<Integer> allChildren = new ArrayList<Integer>();
            // combine all operators and their children into 1 list
            for (int corpOpID : f.getOperators()) {
                List<Integer> children = new ArrayList<Integer>();
                OperatorAccount op = (OperatorAccount) accountDAO.find(corpOpID);

                children.add(op.getId());
                for (Facility facil : op.getOperatorFacilities())
                    children.add(facil.getOperator().getId());

                if (f.isShowAnyOperator()) {
                    allChildren.addAll(children);
                } else {
                    sql.addWhere(String.format(
                            "Set1.id IN (SELECT es.employeeID FROM employee_site es WHERE es.opID IN (%s))",
                            Strings.implode(children)));
                }
            }

            if (f.isShowAnyOperator()) {
                sql.addWhere(String.format(
                        "Set1.id IN (SELECT es.employeeID FROM employee_site es WHERE es.opID IN (%s))",
                        Strings.implode(allChildren)));
            }
        }
    }

    @Override
    protected void addExcelColumns() {
        super.addExcelColumns();
        excelSheet.addColumn(new ExcelColumn("title", getText("Employee.title")));
        excelSheet.addColumn(new ExcelColumn("roles", getText(String.format("%s.label.JobRoles", getScope()))));
        excelSheet.addColumn(new ExcelColumn("skilled", getText(String.format(".label.Competency", getScope())),
                ExcelCellType.Integer));
        excelSheet.addColumn(new ExcelColumn("required", getText(String.format(".label.Required", getScope())),
                ExcelCellType.Integer));
        excelSheet.addColumn(new ExcelColumn("percent", getText(String.format(".label.Competency", getScope())) + " %",
                ExcelCellType.Integer));
    }
}
