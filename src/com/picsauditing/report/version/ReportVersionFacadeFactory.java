package com.picsauditing.report.version;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.picsauditing.jpa.entities.Report;
import com.picsauditing.util.AppVersion;

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
		ReportDTOFacade facade = getFacade(json);
		facade.fromJSON(json, report);
	}

	public static ReportDTOFacade getFacade(JSONObject json) {
		return getFacade(getVersion(json));
	}

	private static AppVersion getVersion(JSONObject json) {
		Object version = json.get("version");
		if (version == null)
			return new AppVersion();
		return new AppVersion(version.toString());
	}

	private static ReportDTOFacade getFacade(AppVersion version) {
		if (version.greaterThanOrEqualTo(6, 33))
			return new com.picsauditing.report.version.latest.ReportDTOFacadeImpl();

		return new com.picsauditing.report.version.previous.ReportDTOFacadeImpl();
		// throw new
		// RuntimeException("Could not find valid ReportVersionFacade for version "
		// + version.getVersion());
	}

}
