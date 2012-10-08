package com.picsauditing.actions.report;

import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ReportDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.model.report.ReportModel;
import com.picsauditing.report.SqlBuilder;
import com.picsauditing.report.access.ReportUtil;
import com.picsauditing.search.SelectSQL;

/**
 * This is a controller. It should delegate business concerns and persistence
 * methods.
 */
@SuppressWarnings({ "serial" })
public class ReportPrint extends PicsActionSupport {
	private static final Logger logger = LoggerFactory.getLogger(ReportPrint.class);

	@Autowired
	private ReportDAO reportDao;
	private Report report;

	private List<BasicDynaBean> data;

	public String execute() throws Exception {
		ReportModel.validate(report);
		
		SelectSQL sql = new SqlBuilder().initializeSql(report, permissions);
		sql.setLimit(report.getRowsPerPage());
		// TODO Print parameters
		logger.debug("Running report {0} with SQL: {1}", report.getId(), sql.toString());

		ReportUtil.addTranslatedLabelsToReportParameters(report.getDefinition(), permissions.getLocale());

		data = reportDao.runQuery(sql.toString(), json);

		return SUCCESS;
	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	public List<BasicDynaBean> getData() {
		return data;
	}
}
