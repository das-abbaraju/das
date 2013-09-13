package com.picsauditing.selenium;

import java.sql.SQLException;
import java.util.List;

import com.picsauditing.search.Database;
import com.picsauditing.util.Strings;

public class UserDeleter extends Deleter {
	public void execute() throws SQLException {
		if (null == IDs || IDs.isEmpty()) return;
		
		
		Database db = new Database();

		{
			Delete t = new Delete(null);
			t.addJoin("JOIN users u ON u.id = t.userID");
			t.addJoin("WHERE u.id IN (" + IDs + ")");
			
			String[] tables = { 
					"user_assignment", 
					"user_switch", 
					"useraccess", 
					"usergroup", 
					"widget_user" };
			
			for (String table : tables) {
				t.table = table;
				t.delete(db);
			}
		}
		{
			Delete t = new Delete("users");
			t.addJoin("WHERE id IN (" + IDs + ")");
			t.delete(db);
		}
	}
}
