package com.picsauditing.service;

import static com.picsauditing.report.ReportJson.DEBUG_SQL;
import static com.picsauditing.report.ReportJson.LEVEL_COLUMNS;
import static com.picsauditing.report.ReportJson.LEVEL_DATA;
import static com.picsauditing.report.ReportJson.LEVEL_FILTERS;
import static com.picsauditing.report.ReportJson.LEVEL_REPORT;
import static com.picsauditing.report.ReportJson.LEVEL_RESULTS;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.persistence.NoResultException;
import javax.servlet.ServletOutputStream;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.access.Permissions;
import com.picsauditing.access.ReportPermissionException;
import com.picsauditing.actions.report.ManageReports;
import com.picsauditing.dao.ReportDAO;
import com.picsauditing.dao.ReportElementDAO;
import com.picsauditing.dao.ReportPermissionAccountDAO;
import com.picsauditing.dao.ReportPermissionUserDAO;
import com.picsauditing.dao.ReportUserDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.Column;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportPermissionAccount;
import com.picsauditing.jpa.entities.ReportPermissionUser;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.jpa.entities.Sort;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.report.PicsSqlException;
import com.picsauditing.report.RecordNotFoundException;
import com.picsauditing.report.ReportContext;
import com.picsauditing.report.ReportJson;
import com.picsauditing.report.ReportUtil;
import com.picsauditing.report.ReportValidationException;
import com.picsauditing.report.SqlBuilder;
import com.picsauditing.report.converter.JsonReportBuilder;
import com.picsauditing.report.converter.JsonReportElementsBuilder;
import com.picsauditing.report.converter.LegacyReportConverter;
import com.picsauditing.report.converter.ReportBuilder;
import com.picsauditing.report.data.ReportDataConverter;
import com.picsauditing.report.data.ReportResults;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.SqlFunction;
import com.picsauditing.report.models.AbstractModel;
import com.picsauditing.report.models.ModelFactory;
import com.picsauditing.report.models.ModelType;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.JSONUtilities;
import com.picsauditing.util.Strings;
import com.picsauditing.util.excel.ExcelBuilder;

public class ReportService {

	@Autowired
	private ReportDAO reportDao;
	@Autowired
	private ReportElementDAO reportElementDAO;
	@Autowired
	private ReportUserDAO reportUserDao;
	@Autowired
	private ReportPermissionUserDAO reportPermissionUserDao;
	@Autowired
	private ReportPermissionAccountDAO reportPermissionAccountDao;
	@Autowired
	private PermissionService permissionService;
	@Autowired
	private LegacyReportConverter legacyReportConverter;
	@Autowired
	private SqlBuilder sqlBuilder;
	@Autowired
	public ManageReportsService manageReportsService;

	private I18nCache i18nCache;

	private static final Logger logger = LoggerFactory.getLogger(ReportService.class);

	@SuppressWarnings("unchecked")
	public JSONObject buildJsonResponse(ReportContext reportContext) throws ReportValidationException, RecordNotFoundException, SQLException {
		Report report = createOrLoadReport(reportContext);
		SelectSQL sql = initializeReportAndBuildSql(reportContext, report);

		JSONObject responseJson = new JSONObject();

		if (reportContext.includeReport) {
			JSONObject reportJson = JsonReportBuilder.buildReportJson(report, reportContext.permissions);
			responseJson.put(LEVEL_REPORT, reportJson);
		}

		AbstractModel reportModel = ModelFactory.build(report.getModelType(), reportContext.permissions);
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

	public SelectSQL initializeReportAndBuildSql(ReportContext reportContext, Report report) throws ReportValidationException {
		SelectSQL sql = sqlBuilder.initializeReportAndBuildSql(report, reportContext.permissions);
		logger.debug("Running report {0} with SQL: {1}", report.getId(), sql.toString());

		ReportUtil.addTranslatedLabelsToReportParameters(report, reportContext.permissions.getLocale());
		return sql;
	}

	public Report createOrLoadReport(ReportContext reportContext) throws RecordNotFoundException, ReportValidationException {
		JSONObject reportJson = buildReportJsonFromPayload(reportContext.payloadJson);

		Report report;
		if (shouldLoadReportFromJson(reportJson, reportContext.includeData)) {
			report = buildReportFromJson(reportJson, reportContext.reportId);
		} else {
			report = loadReportFromDatabase(reportContext.reportId);

			// todo: Remove me when all reports are converted to new layout on stable
			if (report.hasNoColumnsFiltersOrSorts()) {
				legacyConvertParametersToReport(report);
			}
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
			throw new Exception("User " + reportContext.permissions.getUserId() + " cannot edit report " + reportContext.reportId);
		}

		JSONObject reportJson = buildReportJsonFromPayload(reportContext.payloadJson);
		Report report = createReportFromPayload(reportContext.reportId, reportJson);

		validate(report);

		reportDao.save(report);

		return report;
	}

	public Report copy(ReportContext reportContext) throws Exception {
		int userId = reportContext.permissions.getUserId();

		if (!permissionService.canUserViewReport(reportContext.permissions, reportContext.reportId)) {
			throw new Exception("User " + userId + " does not have permission to copy report "
					+ reportContext.reportId);
		}

		JSONObject reportJson = buildReportJsonFromPayload(reportContext.payloadJson);
		Report newReport = createReportFromPayload(reportContext.reportId, reportJson);

		validate(newReport);

		prepareNewReportForDatabaseCopy(newReport, reportContext.permissions);

		reportDao.save(newReport);

		if (manageReportsService.shouldFavorite(reportJson)) {
			ReportUser reportUser = loadOrCreateReportUser(userId, newReport.getId());
			manageReportsService.favoriteReport(reportUser);
			reportUserDao.save(reportUser);
		}

		// This is a new report owned by the user, unconditionally give them edit permission
		loadOrCreateReportUser(userId, newReport.getId());
		connectReportPermissionUser(userId, newReport.getId(), true, userId);

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

	protected Report createReportFromPayload(int reportId, JSONObject reportJson) throws IllegalArgumentException, ReportValidationException {
		Report report = buildReportFromJson(reportJson, reportId);

		report.sortColumns();

		return report;
	}

	private Report buildReportFromJson(JSONObject jsonReport, int reportId) throws ReportValidationException {
		Report report = ReportBuilder.fromJson(jsonReport);
		report.setId(reportId);
		return report;
	}

	void validate(Report report) throws ReportValidationException {
		if (report == null) {
			throw new ReportValidationException("Report object is null. (Possible security concern.)");
		}

		if (report.hasNoModelType()) {
			throw new ReportValidationException("Report " + report.getId() + " is missing its base", report);
		}

		if (report.hasNoColumns()) {
			throw new ReportValidationException("Report contained no columns");
		}

		if (report.hasParameters()) {
			try {
				JSONParser parser = new JSONParser();
				parser.parse(report.getParameters());
			} catch (ParseException e) {
				throw new ReportValidationException(e, report);
			}
		}
	}

	// TODO Remove this method after the next release
	@Deprecated
	private void legacyConvertParametersToReport(Report report) throws ReportValidationException {
		if (report == null) {
			throw new IllegalArgumentException("Report should not be null");
		}

		reportElementDAO.remove(Column.class, "t.report.id = " + report.getId());
		report.getColumns().clear();
		reportElementDAO.remove(Filter.class, "t.report.id = " + report.getId());
		report.getFilters().clear();
		reportElementDAO.remove(Sort.class, "t.report.id = " + report.getId());
		report.getSorts().clear();

		legacyReportConverter.setReportPropertiesFromJsonParameters(report);
		reportDao.save(report);
	}

	public List<ReportUser> getAllReportUsers(String sort, String direction, Permissions permissions) throws IllegalArgumentException {
		List<ReportUser> reportUsers = new ArrayList<ReportUser>();

		if (Strings.isEmpty(sort)) {
			sort = ManageReports.ALPHA_SORT;
			direction = "ASC";
		}

		List<Report> reports = reportDao.findAllOrdered(permissions, sort, direction);

		for (Report report : reports) {
			ReportUser reportUser = report.getReportUser(permissions.getUserId());
			if (reportUser == null) {
				// todo: Why are creating a new ReportUser for each report???
				reportUser = createReportUserForReport(permissions.getUserId(), report);
			}
			reportUsers.add(reportUser);
		}

		return reportUsers;
	}

	// FIXME mostly duplicated by createReportUser
	private ReportUser createReportUserForReport(int userId, Report report) {
		ReportUser reportUser = new ReportUser();

		User user = new User();
		user.setId(userId);
		reportUser.setUser(user);
		reportUser.setReport(report);
		reportUser.setFavorite(false);
		reportUserDao.save(reportUser);

		return reportUser;
	}

	public ReportUser loadOrCreateReportUser(int userId, int reportId) {
		ReportUser reportUser;

		try {
			reportUser = loadReportUser(userId, reportId);
		} catch (NoResultException nre) {
			reportUser = createReportUser(userId, reportId);
		}

		return reportUser;
	}

	// FIXME mostly duplicated by createReportUserForReport
	private ReportUser createReportUser(int userId, int reportId) {
		ReportUser reportUser;// Need to connect user to report first
		Report report = reportDao.find(Report.class, reportId);
		reportUser = new ReportUser(userId, report);
		reportUser.setAuditColumns(new User(userId));
		reportUser.setFavorite(false);
		reportUserDao.save(reportUser);
		return reportUser;
	}

	public ReportUser loadReportUser(int userId, int reportId) {
		return reportUserDao.findOne(userId, reportId);
	}

	public ReportPermissionUser shareReportWithUser(int shareToUserId, int reportId, Permissions permissions,
			boolean editable) throws ReportPermissionException {
		if (!permissionService.canUserEditReport(permissions, reportId)) {
			// TODO translate this
			throw new ReportPermissionException("You cannot share a report that you cannot edit.");
		}

		return connectReportPermissionUser(shareToUserId, reportId, editable, permissions.getUserId());
	}

	private ReportPermissionUser connectReportPermissionUser(int shareToUserId, int reportId, boolean editable, int shareFromUserId) {
		ReportPermissionUser reportPermissionUser;

		try {
			reportPermissionUser = reportPermissionUserDao.findOne(shareToUserId, reportId);
		} catch (NoResultException nre) {
			Report report = reportDao.findById(reportId);
			// TODO use a different DAO
			User shareToUser = reportDao.find(User.class, shareToUserId);
			reportPermissionUser = new ReportPermissionUser(shareToUser, report);
			reportPermissionUser.setAuditColumns(new User(shareFromUserId));

			if (!shareToUser.isGroup()) {
				loadOrCreateReportUser(shareToUserId, reportId);
			}
		}

		reportPermissionUser.setEditable(editable);
		reportPermissionUserDao.save(reportPermissionUser);

		return reportPermissionUser;
	}

	public ReportPermissionAccount shareReportWithAccount(int accountId, int reportId, Permissions permissions) throws ReportPermissionException {
		if (!permissionService.canUserEditReport(permissions, reportId)) {
			// TODO translate this
			throw new ReportPermissionException("You cannot share a report that you cannot edit.");
		}

		return connectReportPermissionAccount(accountId, reportId, permissions);
	}

	private ReportPermissionAccount connectReportPermissionAccount(int accountId, int reportId,
			Permissions permissions) {
		ReportPermissionAccount reportPermissionAccount;

		try {
			reportPermissionAccount = reportPermissionAccountDao.findOne(accountId, reportId);
		} catch (NoResultException nre) {
			Report report = reportDao.findById(reportId);
			// TODO use a different DAO
			Account account = reportDao.find(Account.class, accountId);
			reportPermissionAccount = new ReportPermissionAccount(account, report);
			reportPermissionAccount.setAuditColumns(new User(permissions.getUserId()));
		}

		reportPermissionAccountDao.save(reportPermissionAccount);

		return reportPermissionAccount;
	}

	public void disconnectReportPermissionUser(int userId, int reportId) {
		try {
			reportPermissionUserDao.revokePermissions(userId, reportId);
		} catch (NoResultException nre) {

		}
	}

	public void disconnectReportPermissionAccount(int accountId, int reportId) {
		ReportPermissionAccount reportPermissionAccount;

		try {
			reportPermissionAccount = reportPermissionAccountDao.findOne(accountId, reportId);
			reportPermissionAccountDao.remove(reportPermissionAccount);
		} catch (NoResultException nre) {

		}
	}

	private boolean shouldLoadReportFromJson(JSONObject reportJson, boolean includeData) {
		return JSONUtilities.isNotEmpty(reportJson) && includeData;
	}

	private Report loadReportFromDatabase(int reportId) throws RecordNotFoundException, ReportValidationException {
		Report report = reportDao.findById(reportId);

		if (report == null) {
			throw new RecordNotFoundException("Report " + reportId + " was not found in the database");
		}

		return report;
	}

	@SuppressWarnings("unchecked")
	private JSONObject buildDataJson(Report report, ReportContext reportContext, SelectSQL sql) throws ReportValidationException, PicsSqlException {
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

	protected ReportResults buildReportResults(Report report, ReportContext reportContext, List<BasicDynaBean> queryResults) {
		ReportDataConverter converter = new ReportDataConverter(report.getColumns(), queryResults);

		converter.setLocale(reportContext.permissions.getLocale());
		converter.convertForExtJS();
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

	public List<ReportUser> getSortedFavoriteReports(int userId) {
		return reportUserDao.findAllFavorite(userId);
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

	public JSONObject buildSqlFunctionsJson(ModelType modelType, String fieldId, Permissions permissions) throws Exception {
		AbstractModel model = ModelFactory.build(modelType, permissions);
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

	public boolean isUserFavoriteReport(Permissions permissions, int reportId) {
		try {
			ReportUser reportUser = reportUserDao.findOne(permissions.getUserId(), reportId);

			if (reportUser == null) {
				return false;
			}

			return reportUser.isFavorite();
		} catch (NoResultException nre) {

		}

		return false;
	}

	private I18nCache getI18nCache() {
		if (i18nCache == null) {
			return I18nCache.getInstance();
		}

		return i18nCache;
	}
}
