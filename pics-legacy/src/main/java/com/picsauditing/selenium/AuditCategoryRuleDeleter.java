package com.picsauditing.selenium;

import com.picsauditing.search.Database;

import java.sql.SQLException;

public class AuditCategoryRuleDeleter extends Deleter {
    public void execute() throws SQLException {
        if (null == IDs || IDs.isEmpty()) return;

        Database db = new Database();

        {
            Delete t = new Delete("audit_category_rule");
            t.addJoin("WHERE opID IN (" + IDs + ")");
            t.delete(db);
        }
    }
}
