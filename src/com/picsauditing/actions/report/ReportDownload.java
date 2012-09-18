package com.picsauditing.actions.report;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletOutputStream;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.ReportValidationException;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ReportDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.model.ReportModel;
import com.picsauditing.report.Column;
import com.picsauditing.report.SqlBuilder;
import com.picsauditing.report.access.ReportUtil;
import com.picsauditing.report.data.ReportDataConverter;
import com.picsauditing.report.data.ReportResults;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.excel.ExcelBuilder;
import com.picsauditing.util.excel.ExcelColumn;

/**
 * This is a controller. It should delegate business concerns and persistence
 * methods.
 */
@SuppressWarnings({ "serial" })
public class ReportDownload extends PicsActionSupport {
	private static final Logger logger = LoggerFactory.getLogger(ReportDownload.class);

	@Autowired
	private ReportDAO reportDao;
	private Report report;

	private ReportResults reportResults;

	public String execute() {
		try {
			getData();
			HSSFWorkbook workbook = buildWorkbook();
			writeFile(report.getName() + ".xls", workbook);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return BLANK;
	}

	private void getData() throws ReportValidationException, SQLException {
		ReportModel.validate(report);

		SelectSQL sql = new SqlBuilder().initializeSql(report.getModel(), report.getDefinition(), permissions);
		// TODO Print parameters
		logger.debug("Running report {0} with SQL: {1}", report.getId(), sql.toString());

		ReportUtil.addTranslatedLabelsToReportParameters(report.getDefinition(), permissions.getLocale());

		List<BasicDynaBean> queryResults = reportDao.runQuery(sql.toString(), json);

		ReportDataConverter converter = new ReportDataConverter(report.getDefinition().getColumns(), queryResults);
		converter.setLocale(permissions.getLocale());
		converter.convertForPrinting();
		reportResults = converter.getReportResults();
	}

	private HSSFWorkbook buildWorkbook() {
		logger.info("Building XLS File");
		ExcelBuilder builder = new ExcelBuilder();
		builder.addColumns(report.getDefinition().getColumns());
		builder.addSheet(report.getName(), reportResults);
		return builder.getWorkbook();
	}

	private void writeFile(String filename, HSSFWorkbook workbook) throws IOException {
		logger.info("Streaming XLS File to response");
		ServletActionContext.getResponse().setContentType("application/vnd.ms-excel");
		ServletActionContext.getResponse().setHeader("Content-Disposition", "attachment; filename=" + filename);
		ServletOutputStream outstream = ServletActionContext.getResponse().getOutputStream();
		workbook.write(outstream);
		outstream.flush();
		ServletActionContext.getResponse().flushBuffer();
	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

}
