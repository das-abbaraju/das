package com.picsauditing.actions.cron;

import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import org.apache.commons.beanutils.BasicDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExpireFlagChangesTask implements CronTask {
    Database database = new Database();

    public String getDescription() {
        return "Auto approve baseline flags that changed more than 2 weeks ago";
    }

    public List<String> getSteps() {
        List<String> result = new ArrayList<>();
        try {
            String query = getSelectStatement();
            List<BasicDynaBean> queryResult = database.selectReadOnly(query, false);
            result.add("Auto approving " + queryResult.get(0).get("total").toString() + " flag changes");
        } catch (Exception e) {
            result.add(e.getMessage());
        }
        return result;
    }

    public CronTaskResult run() {
        CronTaskResult results = new CronTaskResult();
        try {
            String query = getUpdateStatement();
            int changeCount = database.executeUpdate(query);
            results.getLogger().append("Updated " + changeCount + " records");
            results.setSuccess(true);
        } catch (SQLException e) {
            results.setSuccess(false);
            results.getLogger().append(e.getMessage());
        }

        return results;
    }

    private String getSelectStatement() {
        SelectSQL sql = new SelectSQL("contractor_operator co");
        sql.addField("COUNT(*) total");
        sql.addJoin("JOIN accounts a ON co.conID = a.id");
        sql.addJoin("JOIN contractor_info c ON a.id = c.id");
        return sql.toString() + getWhereClause();
    }

    private String getUpdateStatement() {
        String query = "UPDATE contractor_operator co ";
        query += "JOIN accounts a ON co.conID = a.id ";
        query += "JOIN contractor_info c ON a.id = c.id ";
        query += "SET baselineFlag = flag, ";
        query += "baselineFlagDetail = flagDetail, ";
        query += "baselineApproved = NOW(), ";
        query += "baselineApprover = 1 ";
        query += getWhereClause();
        return query;
    }

    private String getWhereClause() {
        String query = " WHERE flag != baselineFlag ";
        // Ignore Flag Changes that are two weeks old or longer
        query += "AND (flagLastUpdated <= DATE_SUB(NOW(), INTERVAL 14 DAY) ";
        // Automatically approve a. Audited - Unspecified Facility
        // and a. PQF Only - Unspecified Facility
        query += "OR opID IN (10403,2723) ";
        // Ignore Flag Changes for newly created contractors
        query += "OR a.creationDate >= DATE_SUB(NOW(), INTERVAL 2 WEEK) ";
        // Ignore Flag Changes for recently added contractors
        query += "OR co.creationDate >= DATE_SUB(NOW(), INTERVAL 2 WEEK) ";
        // Ignore Clear Flag Changes
        query += "OR flag = 'Clear' OR baselineFlag = 'Clear'";
        // Removed Forced Overall Flags
        query += "OR (forceFlag IS NOT NULL AND NOW() < forceEnd))";
        return query;
    }
}
