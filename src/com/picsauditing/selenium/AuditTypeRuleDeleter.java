package com.picsauditing.selenium;

import com.picsauditing.search.Database;
import com.picsauditing.util.Strings;

import java.sql.SQLException;
import java.util.List;

public class AuditTypeRuleDeleter extends Deleter {
    public void execute() throws SQLException {
        if (null == IDs || IDs.isEmpty()) return;

        Database db = new Database();

        {
            Delete t = new Delete("audit_type_rule");
            t.addJoin("WHERE opID IN (" + IDs + ")");
            t.delete(db);
        }
    }
}
