package com.picsauditing.service;

import static com.picsauditing.report.ReportJson.DEBUG_SQL;
import static com.picsauditing.report.ReportJson.LEVEL_COLUMNS;
import static com.picsauditing.report.ReportJson.LEVEL_DATA;
import static com.picsauditing.report.ReportJson.LEVEL_FILTERS;
import static com.picsauditing.report.ReportJson.LEVEL_REPORT;
import static com.picsauditing.report.ReportJson.LEVEL_RESULTS;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.ServletOutputStream;

import com.picsauditing.jpa.entities.*;
import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.access.Permissions;
import com.picsauditing.access.ReportPermissionException;
import com.picsauditing.dao.ReportDAO;
import com.picsauditing.report.PicsSqlException;
import com.picsauditing.report.RecordNotFoundException;
import com.picsauditing.report.ReportContext;
import com.picsauditing.report.ReportJson;
import com.picsauditing.report.ReportUtil;
import com.picsauditing.report.ReportValidationException;
import com.picsauditing.report.SqlBuilder;
import com.picsauditing.report.converter.JsonReportBuilder;
import com.picsauditing.report.converter.JsonReportElementsBuilder;
import com.picsauditing.report.converter.ReportBuilder;
import com.picsauditing.report.data.ReportDataConverter;
import com.picsauditing.report.data.ReportResults;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.SqlFunction;
import com.picsauditing.report.models.AbstractModel;
import com.picsauditing.report.models.ModelType;
import com.picsauditing.report.models.ReportModelFactory;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.JSONUtilities;
import com.picsauditing.util.excel.ExcelBuilder;

public class ReportService {

	@Autowired
	private ReportDAO reportDao;
	@Autowired
	private PermissionService permissionService;
	@Autowired
	private SqlBuilder sqlBuilder;
	@Autowired
	public ReportPreferencesService reportPreferencesService;

	private I18nCache i18nCache;
	private static final Logger logger = LoggerFactory.getLogger(ReportService.class);

	@SuppressWarnings("unchecked")
	public JSONObject buildJsonResponse(ReportContext reportContext) throws ReportValidationException,
			RecordNotFoundException, SQLException {
		Report report = createOrLoadReport(reportContext);
		SelectSQL sql = initializeReportAndBuildSql(reportContext, report);

		JSONObject responseJson = new JSONObject();

		if (reportContext.includeReport) {
			JSONObject reportJson = JsonReportBuilder.buildReportJson(report, reportContext.permissions);
			responseJson.put(LEVEL_REPORT, reportJson);
		}

		AbstractModel reportModel = ReportModelFactory.build(report.getModelType(), reportContext.permissions);
		if (reportContext.includeColumns) {
			JSONArray columnsJson = JsonReportElementsBuilder.buildColumns(reportModel, reportContext.permissions);
			responseJson.put(LEVEL_COLUMNS, columnsJson);
		}

		if (reportContext.includeFilters) {
			JSONArray filtersJson = JsonReportElementsBuilder.buildFilters(reportModel, reportContext.permissions);
			responseJson.put(LEVEL_FILTERS, filtersJson);
		}

		if (reportContext.includeData) {
			JSONObject dataJson = buildDataJson(report, reportContext, sql);
			responseJson.put(LEVEL_RESULTS, dataJson);
		}

		responseJson.put(ReportJson.EXT_JS_SUCCESS, true);

		return responseJson;
	}

	public SelectSQL initializeReportAndBuildSql(ReportContext reportContext, Report report)
			throws ReportValidationException {
		SelectSQL sql = sqlBuilder.initializeReportAndBuildSql(report, reportContext.permissions);
		logger.debug("Running report {0} with SQL: {1}", report.getId(), sql.toString());

		ReportUtil.addTranslatedLabelsToReport(report, reportContext.permissions.getLocale());
		return sql;
	}

	public Report createOrLoadReport(ReportContext reportContext) throws RecordNotFoundException,
			ReportValidationException {
		JSONObject reportJson = buildReportJsonFromPayload(reportContext.payloadJson);

		Report report;
		if (shouldLoadReportFromJson(reportJson, reportContext.includeData)) {
			report = buildReportFromJson(reportJson, reportContext.reportId);
		} else {
			report = loadReportFromDatabase(reportContext.reportId);
		}

		report.sortColumns();

		return report;
	}

	private JSONObject buildReportJsonFromPayload(JSONObject payloadJson) {
		JSONObject reportJson = new JSONObject();

		if (JSONUtilities.isNotEmpty(payloadJson)) {
			reportJson = (JSONObject) payloadJson.get(ReportJson.LEVEL_REPORT);
		}

		return reportJson;
	}

	public Report save(ReportContext reportContext) throws Exception {
		if (!permissionService.canUserEditReport(reportContext.permissions, reportContext.reportId)) {
			throw new Exception("User " + reportContext.permissions.getUserId() + " cannot edit report "
					+ reportContext.reportId);
		}

		JSONObject reportJson = buildReportJsonFromPayload(reportContext.payloadJson);
		Report report = createReportFromPayload(reportContext.reportId, reportJson);

		setReportOwnerIfNecessary(report, reportContext.user);

		validate(report);

		report.setAuditColumns(reportContext.permissions);
		reportDao.save(report);

		return report;
	}

	@Deprecated
	private void setReportOwnerIfNecessary(Report report, User user) {
		// FIXME: This is a temporary workaround to set the required ownerId of
		// a report for saving. Please delete
		// this method when we can verify that the ownerId is passed to the
		// frontend and is returned back to the backend
		// in the reportContext.payloadJson.
		if (report.getOwner() == null) {
			ReportUser reportUser = reportPreferencesService.loadReportUser(user.getId(), report.getId());
			report.setOwner(reportUser.getUser());
		}
	}

	public Report copy(ReportContext reportContext) throws Exception {
		int userId = reportContext.permissions.getUserId();
		Report oldReport = reportDao.findById(reportContext.reportId);

		if (!permissionService.canUserViewReport(reportContext.user, oldReport, reportContext.permissions)) {
			throw new Exception("User " + userId + " does not have permission to copy report " + reportContext.reportId);
		}

		JSONObject reportJson = buildReportJsonFromPayload(reportContext.payloadJson);
		Report newReport = createReportFromPayload(reportContext.reportId, reportJson);
		newReport.setOwner(reportContext.user);

		validate(newReport);

		prepareNewReportForDatabaseCopy(newReport, reportContext.permissions);

		newReport.setAuditColumns(reportContext.permissions);
		reportDao.save(newReport);

		ReportUser reportUser = reportPreferencesService.loadOrCreateReportUser(userId, newReport.getId());

		if (reportPreferencesService.shouldFavorite(reportJson)) {
			reportPreferencesService.favoriteReport(reportUser);
		}

		// This is a new report owned by the user, unconditionally give them
		// edit permission
		permissionService.grantUserEditPermission(userId, userId, newReport.getId());

		return newReport;
	}

	private void prepareNewReportForDatabaseCopy(Report newReport, Permissions permissions) {
		reportDao.detach(newReport);

		newReport.setAuditColumns(permissions);

		newReport.setId(0);

		for (Column column : newReport.getColumns()) {
			column.setReport(newReport);
			column.setId(0);
		}

		for (Filter filter : newReport.getFilters()) {
			filter.setReport(newReport);
			filter.setId(0);
		}

		for (Sort sort : newReport.getSorts()) {
			sort.setReport(newReport);
			sort.setId(0);
		}
	}

	protected Report createReportFromPayload(int reportId, JSONObject reportJson) throws IllegalArgumentException,
			ReportValidationException {
		Report report = buildReportFromJson(reportJson, reportId);

		report.sortColumns();

		return report;
	}

	public Report buildReportFromJson(JSONObject jsonReport, int reportId) throws ReportValidationException {
		Report report = ReportBuilder.fromJson(jsonReport);
		report.setId(reportId);
		return report;
	}

	void validate(Report report) throws ReportValidationException {
		if (report == null) {
			throw new ReportValidationException("Report object is null. (Possible security concern.)");
		}

		if (report.hasNoOwner()) {
			throw new ReportValidationException("Report " + report.getId() + " has no owner.");
		}

		if (report.hasNoModelType()) {
			throw new ReportValidationException("Report " + report.getId() + " is missing its base", report);
		}

		if (report.hasNoColumns()) {
			throw new ReportValidationException("Report contained no columns");
		}

		if (report.getOwner() == null) {
			throw new ReportValidationException("Report does not have an owner");
		}
	}

	private boolean shouldLoadReportFromJson(JSONObject reportJson, boolean includeData) {
		return JSONUtilities.isNotEmpty(reportJson) && includeData;
	}

	public Report loadReportFromDatabase(int reportId) throws RecordNotFoundException, ReportValidationException {
		Report report = reportDao.findById(reportId);

		if (report == null) {
			throw new RecordNotFoundException("Report " + reportId + " was not found in the database");
		}

		return report;
	}

	@SuppressWarnings("unchecked")
	private JSONObject buildDataJson(Report report, ReportContext reportContext, SelectSQL sql)
			throws ReportValidationException, PicsSqlException {
		JSONObject dataJson = new JSONObject();

		if (shouldIncludeSql(reportContext.permissions)) {
			String debugSQL = sql.toString().replace("\n", " ").replace("  ", " ");
			dataJson.put(DEBUG_SQL, debugSQL);
		}

		sql.setPageNumber(reportContext.limit, reportContext.pageNumber);
		List<BasicDynaBean> queryResults = runQuery(sql, dataJson);

		ReportResults reportResults = buildReportResults(report, reportContext, queryResults);

		dataJson.put(LEVEL_DATA, reportResults.toJson());

		return dataJson;
	}

	protected ReportResults buildReportResults(Report report, ReportContext reportContext,
			List<BasicDynaBean> queryResults) {
		ReportDataConverter converter = new ReportDataConverter(report.getColumns(), queryResults);

		converter.setLocale(reportContext.permissions.getLocale());
		converter.convertForExtJS(reportContext.user.getTimezone());
		ReportResults reportResults = converter.getReportResults();

		return reportResults;
	}

	public ReportResults prepareReportForPrinting(Report report, ReportContext reportContext,
			List<BasicDynaBean> queryResults) {
		ReportDataConverter converter = new ReportDataConverter(report.getColumns(), queryResults);
		converter.setLocale(reportContext.permissions.getLocale());
		converter.convertForPrinting();
		return converter.getReportResults();
	}

	public JSONObject buildReportResultsForChart(Report report, ReportContext reportContext)
			throws ReportValidationException, PicsSqlException {
        SelectSQL sql = initializeReportAndBuildSql(reportContext, report);
        List<BasicDynaBean> queryResults = runQuery(sql, new JSONObject());

        JSONObject responseJson = new JSONObject();
        // responseJson.put("style_type", chart.getChartOption().toString());

		ReportDataConverter converter = new ReportDataConverter(report.getColumns(), queryResults);
		converter.setLocale(reportContext.permissions.getLocale());
        responseJson.put("data", converter.convertForChart());

		return responseJson;
	}

    private boolean shouldIncludeSql(Permissions permissions) {
		return (permissions.isAdmin() || permissions.getAdminID() > 0);
	}

	@SuppressWarnings("unchecked")
	public List<BasicDynaBean> runQuery(SelectSQL sql, JSONObject json) throws PicsSqlException {
		ReportSearchResults reportSearchResults = null;

		try {
			reportSearchResults = reportDao.runQuery(sql.toString());
		} catch (SQLException se) {
			throw new PicsSqlException(se, sql.toString());
		}

		// TODO: Move this further up the stack
		json.put(ReportJson.RESULTS_TOTAL, reportSearchResults.getTotalResultSize());

		return reportSearchResults.getResults();
	}

	public ReportResults buildReportResultsForPrinting(ReportContext reportContext, Report report)
			throws ReportValidationException, PicsSqlException {
		SelectSQL sql = initializeReportAndBuildSql(reportContext, report);
		List<BasicDynaBean> queryResults = runQuery(sql, new JSONObject());
		ReportResults reportResults = prepareReportForPrinting(report, reportContext, queryResults);

		return reportResults;
	}

	public void downloadReport(Report report, ReportResults reportResults) throws IOException {
		HSSFWorkbook workbook = buildWorkbook(report, reportResults);
		writeFile(report.getName() + ".xls", workbook);
	}

	private HSSFWorkbook buildWorkbook(Report report, ReportResults reportResults) {
		ExcelBuilder builder = new ExcelBuilder();
		builder.addColumns(report.getColumns());
		builder.addSheet(report.getName(), reportResults);
		HSSFWorkbook workbook = builder.getWorkbook();

		return workbook;
	}

	private void writeFile(String filename, HSSFWorkbook workbook) throws IOException {
		ServletActionContext.getResponse().setContentType("application/vnd.ms-excel");
		ServletActionContext.getResponse().setHeader("Content-Disposition", "attachment; filename=" + filename);
		ServletOutputStream outstream = ServletActionContext.getResponse().getOutputStream();
		workbook.write(outstream);
		outstream.flush();
		ServletActionContext.getResponse().flushBuffer();
	}

	public JSONObject buildSqlFunctionsJson(ModelType modelType, String fieldId, Permissions permissions)
			throws Exception {
		AbstractModel model = ReportModelFactory.build(modelType, permissions);
		Field field = model.getAvailableFields().get(fieldId.toUpperCase());

		if (field == null) {
			throw new Exception("Unable to find field for field_id: " + fieldId.toUpperCase());
		}

		Set<SqlFunction> sqlFunctions = field.getType().getSqlFunctions();
		JSONObject sqlFunctionsJson = sqlFunctionsToJson(sqlFunctions, permissions.getLocale());

		return sqlFunctionsJson;
	}

	@SuppressWarnings("unchecked")
	private JSONObject sqlFunctionsToJson(Set<SqlFunction> sqlFunctions, Locale locale) {
		JSONObject sqlFunctionsJson = new JSONObject();
		JSONArray jsonArray = new JSONArray();

		for (SqlFunction sqlFunction : sqlFunctions) {
			JSONObject json = new JSONObject();

			json.put(ReportJson.SQL_FUNCTIONS_KEY, sqlFunction.name());
			String key = ReportUtil.REPORT_FUNCTION_KEY_PREFIX + sqlFunction.name();
			String translatedValue = getI18nCache().getText(key, locale);
			json.put(ReportJson.SQL_FUNCTIONS_VALUE, translatedValue);

			jsonArray.add(json);
		}

		sqlFunctionsJson.put(ReportJson.SQL_FUNCTIONS, jsonArray);

		return sqlFunctionsJson;
	}

	private I18nCache getI18nCache() {
		if (i18nCache == null) {
			return I18nCache.getInstance();
		}

		return i18nCache;
	}

	public void publicizeReport(User user, Report report) throws ReportPermissionException {
		if (!permissionService.canUserPublicizeReport(user, report)) {
			int userId = (user != null) ? user.getId() : -1;
			int reportId = (report != null) ? report.getId() : -1;
			throw new ReportPermissionException("User " + userId + " does not have permission to publicize report "
					+ reportId);
		}

		report.setPublic(true);
		report.setAuditColumns(user);

		reportDao.save(report);
	}

	public void unpublicizeReport(User user, Report report) throws ReportPermissionException {
		if (!permissionService.canUserPublicizeReport(user, report)) {
			int userId = (user != null) ? user.getId() : -1;
			int reportId = (report != null) ? report.getId() : -1;
			throw new ReportPermissionException("User " + userId + " does not have permission to unpublicize report "
					+ reportId);
		}

		report.setPublic(false);
		report.setAuditColumns(user);

		reportDao.save(report);
	}

	public JSONObject buildJsonReportInfo(int reportId) throws Exception {
		Report report = reportDao.findById(reportId);

		JSONObject infoJson = new JSONObject();
		infoJson.put("model", report.getModelType().toString());

		int shares = report.getReportPermissionUsers().size() + report.getReportPermissionAccounts().size();
		infoJson.put("shares", Integer.toString(shares));

		int favorites = 0;
		for (ReportUser user : report.getReportUsers()) {
			if (user.isFavorite())
				favorites++;
		}
		infoJson.put("favorites", Integer.toString(favorites));
		infoJson.put("updated", report.getUpdateDate().toString());
		infoJson.put("updated_by", report.getUpdatedBy().getName());
		infoJson.put("owner", report.getOwner().getName());

		return infoJson;
	}

}
