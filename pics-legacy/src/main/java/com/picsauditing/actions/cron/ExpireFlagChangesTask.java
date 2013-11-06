package com.picsauditing.actions.cron;

import com.picsauditing.search.Database;

import java.sql.SQLException;
import java.util.List;

public class ExpireFlagChangesTask implements CronTask {
    Database database = new Database();

    public String getDescription() {
        return "TODO";
    }

    public List<String> getSteps() {
        return null;
    }

    public CronTaskResult run() throws SQLException {
        CronTaskResult results = new CronTaskResult();
        String query = "UPDATE contractor_operator co ";
        query += "JOIN accounts a ON co.conID = a.id ";
        query += "JOIN contractor_info c ON a.id = c.id ";
        query += "SET baselineFlag = flag, ";
        query += "baselineFlagDetail = flagDetail, ";
        query += "baselineApproved = NOW(), ";
        query += "baselineApprover = 1 ";
        query += "WHERE flag != baselineFlag ";
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

        database.executeUpdate(query);

        results.setSuccess(true);
        return results;
    }
}
