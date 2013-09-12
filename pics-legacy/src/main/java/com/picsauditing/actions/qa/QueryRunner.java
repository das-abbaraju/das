package com.picsauditing.actions.qa;

import java.sql.SQLException;

import com.picsauditing.search.SelectSQL;

public interface QueryRunner {
	TabularModel run() throws SQLException;

	void setSelectSQL(SelectSQL query);

	void setSelectSQL(String query);

	void setTabularModelForData(TabularModel data);
}
