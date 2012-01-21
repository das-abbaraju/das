package com.picsauditing.report.bases;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.BasicDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.report.QueryField;
import com.picsauditing.report.SimpleReportField;
import com.picsauditing.search.SelectSQL;

public class QueryBase {
	protected SelectSQL sql = new SelectSQL();
	private Map<String, QueryField> availableFields = new HashMap<String, QueryField>();
	private Map<String, String> joins = new HashMap<String, String>();
	private Permissions permissions;
	protected String defaultSort = null;
	private int allRows = 0;
	private List<SimpleReportField> columns;
	private BasicDAO dao;
	private Report report;

}
