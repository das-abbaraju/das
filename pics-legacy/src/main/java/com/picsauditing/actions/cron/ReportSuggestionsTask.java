package com.picsauditing.actions.cron;

import com.picsauditing.search.Database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReportSuggestionsTask implements CronTask {
    Database database = new Database();

    public String getDescription() {
        return "Connect each user with all their groups to eliminate the need to " +
                "recursively loop through users to know who is in a group. " +
                "Also, calculate suggestions for report favorites based on company, group, and usage.";
    }

    public List<String> getSteps() {
        List<String> list = new ArrayList<>();
        list.add("dw_calc_inherited_user_groups");
        list.add("dw_calc_report_suggestions");
        return list;
    }

    public CronTaskResult run() {
        CronTaskResult results = new CronTaskResult(true, "");
        try {
            database.executeUpdate("CALL dw_calc_inherited_user_groups();");
            database.executeUpdate("CALL dw_calc_report_suggestions();");
        } catch (SQLException e) {
            results.setSuccess(false);
            results.getLogger().append(e.getMessage());
            return results;
        }
        return results;
    }
}
