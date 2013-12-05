package com.picsauditing.selenium;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.picsauditing.search.Database;

class Delete {
    String table = "";
    List<String> joins = new ArrayList<String>();
    public boolean disableForeignKeyCheck = false;

    private static final String DISABLE_FOREIGN_KEY_CHECK = "SET FOREIGN_KEY_CHECKS=0";
    private static final String ENABLE_FOREIGN_KEY_CHECK = "SET FOREIGN_KEY_CHECKS=1";

    public Delete(String table) {
        this.table = table;
    }

    public Delete addJoin(String join) {
        joins.add(join);
        return this;
    }

    public String toString() {
        String sql = "DELETE t FROM " + table + " t ";

        for (String join : joins) {
            sql += "\n" + join;
        }

        return sql;
    }

    public void delete(Database db) throws SQLException {
        if (disableForeignKeyCheck) {
            db.execute(DISABLE_FOREIGN_KEY_CHECK);
        }

        db.executeUpdate(toString());

        if (disableForeignKeyCheck) {
            db.execute(ENABLE_FOREIGN_KEY_CHECK);
        }
    }

    public void deleteSingleTable(Database db) throws SQLException {
        if (disableForeignKeyCheck) {
            db.execute(DISABLE_FOREIGN_KEY_CHECK);
        }

        db.executeUpdate(toSingleTableString());

        if (disableForeignKeyCheck) {
            db.execute(ENABLE_FOREIGN_KEY_CHECK);
        }
    }

    private String toSingleTableString() {
        String sql = "DELETE FROM " + table + " ";

        for (String join : joins) {
            sql += "\n" + join;
        }

        return sql;
    }
}
