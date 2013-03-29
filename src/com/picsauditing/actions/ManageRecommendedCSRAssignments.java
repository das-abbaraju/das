package com.picsauditing.actions;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ReportDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.report.ReportJson;
import com.picsauditing.report.ReportValidationException;
import com.picsauditing.report.SqlBuilder;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.service.ReportSearchResults;
import com.picsauditing.util.Strings;

public class ManageRecommendedCSRAssignments extends PicsActionSupport {
	private static final long serialVersionUID = -1613254037742590324L;

    private final Logger logger = LoggerFactory.getLogger(ManageRecommendedCSRAssignments.class);


	@Autowired
	private ReportDAO reportDao;
	@Autowired
	private ContractorAccountDAO contractorAccountDAO;

	private final int RECOMMENDED_CSR_ASSIGNMENTS_REPORT_ID = 107;

	private Report report;
	private List<BasicDynaBean> queryResults;

	private String acceptRecommendations;
	private String rejectRecommendations;

	@RequiredPermission(value = OpPerms.ManageCsrAssignment)
	public String execute() throws Exception {
		runReport(RECOMMENDED_CSR_ASSIGNMENTS_REPORT_ID, permissions);
		return SUCCESS;
	}

	@SuppressWarnings("unchecked")
	private List<BasicDynaBean> runReport(int reportID, Permissions permissions) throws ReportValidationException,
			SQLException {
		report = reportDao.find(Report.class, reportID);
		SelectSQL sql = new SqlBuilder().initializeReportAndBuildSql(report, permissions);
		JSONObject json = new JSONObject();
		ReportSearchResults reportSearchResults = reportDao.runQuery(sql.toString());
		json.put(ReportJson.RESULTS_TOTAL, reportSearchResults.getTotalResultSize());
		queryResults = reportSearchResults.getResults();

		return queryResults;
	}

	public String save() throws IOException {
		if (Strings.isNotEmpty(acceptRecommendations)) {
			int numRowsAffected = contractorAccountDAO.acceptRecommendedCsrs(acceptRecommendations);
			logger.info(numRowsAffected + " changes accepted, ids: " + acceptRecommendations);
		}

		if (Strings.isNotEmpty(rejectRecommendations)) {
			int numRowsAffected = contractorAccountDAO.rejectRecommendedCsrs(rejectRecommendations);
			logger.info(numRowsAffected + " changes rejected, ids: " + rejectRecommendations);
		}

		return this.setUrlForRedirect("ManageRecommendedCSRAssignments.action");
	}

	public String startNewRun() {
		return SUCCESS;
	}

	public List<BasicDynaBean> getQueryResults() {
		return queryResults;
	}

	public void setQueryResults(List<BasicDynaBean> queryResults) {
		this.queryResults = queryResults;
	}

	public String getAcceptRecommendations() {
		return acceptRecommendations;
	}

	public void setAcceptRecommendations(String acceptRecommendations) {
		this.acceptRecommendations = acceptRecommendations;
	}

	public String getRejectRecommendations() {
		return rejectRecommendations;
	}

	public void setRejectRecommendations(String rejectRecommendations) {
		this.rejectRecommendations = rejectRecommendations;
	}

}
