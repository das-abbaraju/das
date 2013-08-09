package com.picsauditing.selenium;

import java.sql.SQLException;
import java.util.List;

import com.picsauditing.search.Database;
import com.picsauditing.util.Strings;

public class EmployeeDeleter extends Deleter {
	public void execute() throws SQLException {
		if (null == IDs || IDs.isEmpty()) return;

		Database db = new Database();

		{

			Delete t = new Delete(null);
			t.addJoin("JOIN employee e ON t.employeeID = e.id");
			t.addJoin("WHERE e.id IN (" + IDs + ")");

			String[] tables = { 
					"employee_site", 
					"employee_role", 
					"employee_qualification", 
					"employee_competency",
					"employee_assessment_authorization", };
			
			for (String table : tables) {
				t.table = table;
				t.delete(db);
			}
		}
		{
			Delete t = new Delete("employee");
			t.addJoin("WHERE t.id IN (" + IDs + ")");
			t.delete(db);
		}

	}

}
