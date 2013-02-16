package com.picsauditing.mail;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.ReportDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.report.ReportValidationException;
import com.picsauditing.report.SqlBuilder;
import com.picsauditing.search.SelectSQL;

public class EmailReportRunner {
	@Autowired
	private ReportDAO reportDao;

	final private String ACCOUND_ID_FIELDNAME = "AccountID";

	private Report report;
	private List<BasicDynaBean> queryResults;

	public List<BasicDynaBean> runReport(int reportID, Permissions permissions) throws ReportValidationException,
			SQLException {
		report = reportDao.find(Report.class, reportID);
		SelectSQL sql = new SqlBuilder().initializeReportAndBuildSql(report, permissions);
		JSONObject json = new JSONObject();
		queryResults = reportDao.runQuery(sql.toString(), json);
		return queryResults;
	}

	public EmailRequestDTO buildEmailRequest() {
		EmailRequestDTO request = new EmailRequestDTO();
		addAccountIDs(request);
		return request;
	}

	private void addAccountIDs(EmailRequestDTO request) {
		for (BasicDynaBean accountBean : queryResults) {
			request.contractorIDs.add((Integer) accountBean.get(ACCOUND_ID_FIELDNAME));
		}
	}

	public Report getReport() {
		return report;
	}

	public int getTotalRowsInReport() {
		return queryResults.size();
	}

}
