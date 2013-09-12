package com.picsauditing.selenium;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.picsauditing.search.Database;

class Delete {
	String table = "";
	List<String> joins = new ArrayList<String>();

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
		db.executeUpdate(toString());
	}

    public void deleteSingleTable(Database db) throws SQLException {
        db.executeUpdate(toSingleTableString());
    }

    private String toSingleTableString() {
        String sql = "DELETE FROM " + table + " ";

        for (String join : joins) {
            sql += "\n" + join;
        }

        return sql;
    }
}
