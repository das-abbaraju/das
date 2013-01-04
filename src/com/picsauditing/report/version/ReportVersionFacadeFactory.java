package com.picsauditing.report.version;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.picsauditing.jpa.entities.Report;
import com.picsauditing.report.version.previous.ReportDTOFacadeImpl;

public class ReportVersionFacadeFactory {

	public static void parseReportParameters(Report report) {
		if (StringUtils.isEmpty(report.getParameters())) {
			return;
		}
		JSONObject json = (JSONObject) JSONValue.parse(report.getParameters());
		applyJsonToReport(json, report);
	}

	public static Report createReport(JSONObject json) {
		Report report = new Report();
		String jsonString = json.toString();
		if (StringUtils.isEmpty(jsonString)) {
			return report;
		}
		report.setParameters(jsonString);
		applyJsonToReport(json, report);
		return report;
	}

	private static void applyJsonToReport(JSONObject json, Report report) {
		ReportDTOFacadeImpl facade = new ReportDTOFacadeImpl();
		facade.fromJSON(json, report);
	}

}
