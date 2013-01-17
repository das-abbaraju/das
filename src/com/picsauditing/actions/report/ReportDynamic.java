package com.picsauditing.actions.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.model.report.ReportModel;

@SuppressWarnings({ "unchecked", "serial" })
public class ReportDynamic extends PicsActionSupport {
	
	@Autowired
	private ReportModel reportModel;

	private Report report;
	private boolean favorite;

	private static final Logger logger = LoggerFactory.getLogger(ReportDynamic.class);

	public String copy() {
		return save(true);
	}

	public String save() {
		return save(false);
	}

	private String save(boolean copy) {
		try {
			if (copy)
				report = reportModel.copy(report, permissions, favorite);
			else
				reportModel.edit(report, permissions);

			json.put("success", true);
			json.put("reportID", report.getId());
		} catch (NoRightsException nre) {
			json.put("success", false);
			json.put("error", nre.getMessage());
		} catch (Exception e) {
			if (report == null)
				logger.error("Report was not able to load from DB");
			else
				logger.error("An error occurred saving report id = {} for user {}", report.getId(), permissions.getUserId());
			writeJsonError(e);
		}

		return JSON;
	}

	private void writeJsonError(Exception e) {
		json.put("success", false);
		json.put("message", e.toString());
	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}
}
