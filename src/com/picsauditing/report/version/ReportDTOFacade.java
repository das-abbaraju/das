package com.picsauditing.report.version;

import org.json.simple.JSONObject;

import com.picsauditing.jpa.entities.Report;

public interface ReportDTOFacade {
	public JSONObject toJSON(Report report);

	public void fromJSON(JSONObject json, Report report);
}
