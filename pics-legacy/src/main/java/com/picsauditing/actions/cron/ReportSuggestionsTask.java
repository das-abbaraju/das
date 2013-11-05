package com.picsauditing.actions.cron;

import com.picsauditing.dao.ReportDAO;
import com.picsauditing.search.Database;

import java.sql.SQLException;

public class ReportSuggestionsTask extends CronTask {
    private static String NAME = "ReportSuggestions";
    private Database database;

    public ReportSuggestionsTask(Database database) {
        super(NAME);
        this.database = database;
    }

    protected void run() throws SQLException {
        database.executeUpdate("CALL dw_calc_inherited_user_groups();");
        database.executeUpdate("CALL dw_calc_report_suggestions();");
    }
}
