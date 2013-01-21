package com.picsauditing.model.report;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.User;
import org.json.simple.JSONObject;

public class ReportContext {
	public final JSONObject payloadJson;
	public final int reportId;
	public final User user;
	public final Permissions permissions;
	public final boolean includeData;
	public final boolean includeReport;
	public final boolean includeColumns;
	public final boolean includeFilters;
	public int limit = 100;
	public int pageNumber = 1;


	public ReportContext(JSONObject payloadJson, int reportId, User user, Permissions permissions, boolean includeReport, boolean includeData, boolean includeColumns, boolean includeFilters, int limit, int pageNumber) {
		this.includeColumns = includeColumns;
		this.payloadJson = payloadJson;
		this.reportId = reportId;
		this.user = user;
		this.permissions = permissions;
		this.includeData = includeData;
		this.includeReport = includeReport;
		this.includeFilters = includeFilters;
		this.limit = limit;
		this.pageNumber = pageNumber;
	}
}
