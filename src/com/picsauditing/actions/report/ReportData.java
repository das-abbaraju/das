package com.picsauditing.actions.report;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;
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
import com.picsauditing.report.data.ReportDataConverter;
import com.picsauditing.search.SelectSQL;

@SuppressWarnings({ "unchecked", "serial" })
public class ReportData extends PicsActionSupport {
	@Autowired
	private ReportDAO reportDao;

	protected Report report;
	private int pageNumber = 1;

	private static final Logger logger = LoggerFactory.getLogger(ReportData.class);

	private String debugSQL = "";

	public String execute() {
		SelectSQL sql = null;
		try {
			ReportModel.validate(report);
			sql = new SqlBuilder().initializeSql(report, permissions);
			sql.setPageNumber(report.getRowsPerPage(), pageNumber);
			getData(sql.toString());
		} catch (ReportValidationException error) {
			writeJsonError(error.getMessage());
		} catch (Exception error) {
			logger.error("Report:" + report.getId() + " " + error.getMessage() + " SQL: " + debugSQL);
			if (permissions.has(OpPerms.Debug) || permissions.getAdminID() > 0) {
				writeJsonError(error);
				if (sql != null) {
					json.put("sql", sql.toString());
				}
			} else {
				writeJsonError("Invalid Query");
			}
		}
		return JSON;
	}

	private void getData(String sql) throws ReportValidationException, SQLException {
		debugSQL = sql;

		if (!ReportUtil.hasColumns(report)) {
			// Should this really happen? Maybe we should catch this during
			// Report validation
			writeJsonError("Report contained no columns");
			return;
		}
		List<BasicDynaBean> queryResults = reportDao.runQuery(sql, json);

		ReportDataConverter converter = new ReportDataConverter(report.getDefinition().getColumns(), queryResults);
		converter.setLocale(permissions.getLocale());
		converter.convertForExtJS();

		json.put("data", converter.getReportResults().toJson());
		json.put("success", true);
	}

	private void writeJsonError(Exception e) {
		String message = e.getMessage();
		if (message == null) {
			message = e.toString();
		}

		json.put("sql", debugSQL);

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
