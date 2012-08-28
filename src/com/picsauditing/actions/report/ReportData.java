package com.picsauditing.actions.report;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BasicDynaBean;
import org.json.simple.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.ReportValidationException;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ReportDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.model.ReportModel;
import com.picsauditing.report.SqlBuilder;
import com.picsauditing.report.access.ReportUtil;
import com.picsauditing.report.fields.Field;
import com.picsauditing.search.SelectSQL;

@SuppressWarnings({ "unchecked", "serial" })
public class ReportData extends PicsActionSupport {
	@Autowired
	private ReportDAO reportDao;

	private Report report;
	private int pageNumber = 1;

	private static final Logger logger = LoggerFactory.getLogger(ReportData.class);

	public String execute() {
		try {
			getData();
		} catch (ReportValidationException error) {
			writeJsonError(getText(error.getMessage()));
		} catch (Exception error) {
			logger.error("Report:" + report.getId() + " " + error.getMessage());
			if (permissions.has(OpPerms.Debug)) {
				writeJsonError(error);
			} else {
				writeJsonError("Invalid Query");
			}
		}
		return JSON;
	}

	private void getData() throws ReportValidationException, SQLException {
		ReportModel.validate(report);

		SelectSQL sql = new SqlBuilder().initializeSql(report.getModel(), report.getDefinition(), permissions);
		sql.setPageNumber(report.getRowsPerPage(), pageNumber);

		Map<String, Field> availableFields = ReportModel.buildAvailableFields(report.getTable(), permissions);

		if (ReportUtil.hasColumns(report)) {
			List<BasicDynaBean> queryResults = reportDao.runQuery(sql, json);

			JSONArray queryResultsAsJson = ReportUtil.convertQueryResultsToJson(queryResults, availableFields,
					permissions, permissions.getLocale());
			json.put("data", queryResultsAsJson);
			json.put("success", true);
		}
	}

	private void writeJsonError(Exception e) {
		String message = e.getMessage();

		if (message == null) {
			message = e.toString();
		}

		writeJsonError(message);
	}

	private void writeJsonError(String message) {
		json.put("success", false);
		json.put("message", message);
	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	public void setPage(int page) {
		this.pageNumber = page;
	}
}