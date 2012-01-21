package com.picsauditing.report.bases;

import com.picsauditing.report.FilterType;
import com.picsauditing.search.SelectSQL;

public class QueryAccount extends QueryBase {
	public QueryAccount() {
		sql = new SelectSQL();
		sql.setFromTable("accounts a");

		defaultSort = "a.nameIndex";

	}

}
