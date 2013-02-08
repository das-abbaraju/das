package com.picsauditing.report;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.servlet.ServletOutputStream;

import com.picsauditing.access.*;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.report.converter.JsonReportElementsBuilder;
import com.picsauditing.report.converter.ReportBuilder;
import com.picsauditing.report.converter.JsonReportBuilder;
import com.picsauditing.report.data.ReportDataConverter;
import com.picsauditing.report.data.ReportResults;
import com.picsauditing.report.models.AbstractModel;
import com.picsauditing.report.models.ModelFactory;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.service.PermissionService;
import com.picsauditing.util.JSONUtilities;
import com.picsauditing.util.excel.ExcelBuilder;
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

import com.picsauditing.actions.report.ManageReports;
import com.picsauditing.dao.ReportDAO;
import com.picsauditing.dao.ReportPermissionAccountDAO;
import com.picsauditing.dao.ReportPermissionUserDAO;
import com.picsauditing.dao.ReportUserDAO;
import com.picsauditing.report.converter.LegacyReportConverter;
import com.picsauditing.util.Strings;
import com.picsauditing.util.pagination.Pagination;

import static com.picsauditing.report.ReportJson.*;

public class ReportService {

	@Autowired
	private ReportDAO reportDao;
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

	// TODO remove this instance variable
	private ReportDataConverter converter;

	private static final Logger logger = LoggerFactory.getLogger(ReportService.class);
	private static final int MAX_REPORTS_IN_MENU = 10;

	public void setEditPermissions(Permissions permissions, int userId, int reportId, boolean editable)
			throws NoResultException, NonUniqueResultException, SQLException, Exception {
		ReportPermissionUser reportPermissionUser = connectReportPermissionUser(userId, reportId, editable);

		reportPermissionUserDao.save(reportPermissionUser);
	}

	public Report copy(ReportContext reportContext, boolean favorite) throws Exception {
		int userId = reportContext.permissions.getUserId();

		if (!permissionService.canUserViewAndCopyReport(reportContext.permissions, reportContext.reportId)) {
			throw new Exception("User " + userId + " does not have permission to copy report "
					+ reportContext.reportId);
		}

		Report newReport = createReportFromPayload(reportContext);

		validate(newReport);

		prepareNewReportForDatabaseCopy(newReport, reportContext.permissions);

		reportDao.save(newReport);

		if (favorite) {
			favoriteReport(userId, newReport.getId());
		}

		// This is a new report owned by the user, unconditionally give them edit permission
		loadOrCreateReportUser(userId, newReport.getId());
		connectReportPermissionUser(userId, newReport.getId(), true);

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

	public Report save(ReportContext reportContext) throws Exception {
		if (!permissionService.canUserEditReport(reportContext.permissions, reportContext.reportId)) {
			throw new Exception("User " + reportContext.permissions.getUserId() + " cannot edit report " + reportContext.reportId);
		}

		Report report = createReportFromPayload(reportContext);

		validate(report);

		reportDao.save(report);

		return report;
	}

	// TODO Remove this method after the next release
	@Deprecated
	private void legacyConvertParametersToReport(Report report) throws ReportValidationException {
		if (report == null) {
			throw new IllegalArgumentException("Report should not be null");
		}

		reportDao.remove(Column.class, "t.report.id = " + report.getId());
		report.getColumns().clear();
		reportDao.remove(Filter.class, "t.report.id = " + report.getId());
		report.getFilters().clear();
		reportDao.remove(Sort.class, "t.report.id = " + report.getId());
		report.getSorts().clear();

		legacyReportConverter.setReportPropertiesFromJsonParameters(report);
		reportDao.save(report);
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

	public List<Report> getReportsForSearch(String searchTerm, Permissions permissions, Pagination<Report> pagination) {
		List<Report> reports = new ArrayList<Report>();

		if (Strings.isEmpty(searchTerm)) {
			// By default, show the top ten most favorited reports sorted by
			// number of favorites
			List<ReportUser> reportUsers = reportUserDao.findTenMostFavoritedReports(permissions);
			for (ReportUser reportUser : reportUsers) {
				reports.add(reportUser.getReport());
			}
		} else {
			ReportPaginationParameters parameters = new ReportPaginationParameters(permissions, searchTerm);
			pagination.Initialize(parameters, reportDao);
			reports = pagination.getResults();
		}

		return reports;
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

	public List<ReportUser> getFavorites(int userId) {
		List<ReportUser> favorites;
		favorites = reportUserDao.findAllFavorite(userId);

		if (favorites.size() > MAX_REPORTS_IN_MENU) {
			List<ReportUser> reportUserOverflow = favorites.subList(MAX_REPORTS_IN_MENU, favorites.size());
			favorites = favorites.subList(0, MAX_REPORTS_IN_MENU);
		}

		return favorites;
	}

	public ReportUser favoriteReport(int userId, int reportId) throws NoResultException, NonUniqueResultException,
			SQLException {
		ReportUser reportUser = loadOrCreateReportUser(userId, reportId);
		favorite(reportUser);
		return (ReportUser) reportUserDao.save(reportUser);
	}

	private ReportUser favorite(ReportUser reportUser) throws SQLException {
		int userId = reportUser.getUser().getId() ;
		int nextSortIndex = getNextSortIndex(userId);
		reportUser.setSortOrder(nextSortIndex);
		reportUser.setFavorite(true);

		return reportUser;
	}

	public ReportUser unfavoriteReport(int userId, int reportId) {
		ReportUser unfavoritedReportUser = loadOrCreateReportUser(userId, reportId);

		unfavoritedReportUser.setSortOrder(0);
		unfavoritedReportUser.setFavorite(false);
		unfavoritedReportUser = (ReportUser) reportUserDao.save(unfavoritedReportUser);
		return unfavoritedReportUser;
	}

	public ReportUser moveFavoriteUp(int userId, int reportId) throws Exception {
		return moveFavorite(userId, reportId, 1);
	}

	public ReportUser moveFavoriteDown(int userId, int reportId) throws Exception {
		return moveFavorite(userId, reportId, -1);
	}

	public ReportUser moveFavorite(int userId, int reportId, int magnitude) throws Exception {

		ReportUser reportUser = loadReportUser(userId, reportId);
		int numberOfFavorites = reportUserDao.getFavoriteCount(userId);
		int currentPosition = reportUser.getSortOrder();
		int newPosition = currentPosition + magnitude;

		if (currentPosition == newPosition || newPosition < 0 || newPosition > numberOfFavorites) {
			return reportUser;
		}

		shiftFavoritesDisplacedByMove(userId, currentPosition, newPosition);

		reportUser.setSortOrder(newPosition);
		reportUser = (ReportUser) reportUserDao.save(reportUser);
		return reportUser;
	}

	private void shiftFavoritesDisplacedByMove(int userId, int currentPosition, int newPosition) throws SQLException {
		reportUserDao.resetSortOrder(userId);

		int offsetAmount;
		int offsetRangeBegin;
		int offsetRangeEnd;

		if (currentPosition < newPosition) {
			// Moving up in list, displaced reports move down
			offsetAmount = -1;
			offsetRangeBegin = currentPosition + 1;
			offsetRangeEnd = newPosition;
		} else {
			// Moving down in list, displaced reports move up
			offsetAmount = 1;
			offsetRangeBegin = newPosition;
			offsetRangeEnd = currentPosition - 1;
		}

		reportUserDao.offsetSortOrderForRange(userId, offsetAmount, offsetRangeBegin, offsetRangeEnd);
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

	private int getNextSortIndex(int userId) {
		int maxSortIndex = reportUserDao.findMaxSortIndex(userId);
		return maxSortIndex + 1;
	}

	public ReportPermissionUser connectReportPermissionUser(int userId, int reportId, boolean editable) {
		ReportPermissionUser reportPermissionUser;

		try {
			reportPermissionUser = reportPermissionUserDao.findOne(userId, reportId);
		} catch (NoResultException nre) {
			// Need to connect user to report first
			Report report = reportDao.find(Report.class, reportId);
			reportPermissionUser = new ReportPermissionUser(userId, report);
			reportPermissionUser.setAuditColumns(new User(userId));
		}

		reportPermissionUser.setEditable(editable);
		reportPermissionUserDao.save(reportPermissionUser);

		return reportPermissionUser;
	}

	public ReportPermissionAccount connectReportPermissionAccount(int accountId, int reportId, Permissions permissions) {
		ReportPermissionAccount reportPermissionAccount;

		try {
			reportPermissionAccount = reportPermissionAccountDao.findOne(accountId, reportId);
		} catch (NoResultException nre) {
			// Need to connect user to report first
			Report report = reportDao.find(Report.class, reportId);
			reportPermissionAccount = new ReportPermissionAccount(accountId, report);
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

	public Report createReportFromPayload(ReportContext reportContext) throws IllegalArgumentException, ReportValidationException {
		if (JSONUtilities.isEmpty(reportContext.payloadJson)) {
			throw new IllegalArgumentException();
		}

		JSONObject reportJson = getReportJsonFromPayload(reportContext.payloadJson);

		Report report = buildReportFromJson(reportJson, reportContext.reportId);

		return report;
	}

	public Report createOrLoadReport(ReportContext reportContext) throws RecordNotFoundException, ReportValidationException {
		JSONObject reportJson = getReportJsonFromPayload(reportContext.payloadJson);

		Report report;
		if (shouldLoadReportFromJson(reportJson, reportContext.includeData)) {
			report = buildReportFromJson(reportJson, reportContext.reportId);
		} else {
			report = loadReportFromDatabase(reportContext.reportId);

			if (report.hasNoColumnsFiltersOrSorts()) {
				legacyConvertParametersToReport(report);
			}
		}
		return report;
	}

	private JSONObject getReportJsonFromPayload(JSONObject payloadJson) {
		JSONObject reportJson = new JSONObject();

		if (JSONUtilities.isNotEmpty(payloadJson)) {
			reportJson = (JSONObject) payloadJson.get(ReportJson.LEVEL_REPORT);
		}

		return reportJson;
	}

	private boolean shouldLoadReportFromJson(JSONObject reportJson, boolean includeData) {
		return JSONUtilities.isNotEmpty(reportJson) && includeData;
	}

	private Report buildReportFromJson(JSONObject jsonReport, int reportId) throws ReportValidationException {
		Report report = ReportBuilder.fromJson(jsonReport);
		report.setId(reportId);
		return report;
	}

	private Report loadReportFromDatabase(int reportId) throws RecordNotFoundException, ReportValidationException {
		Report report = reportDao.findById(reportId);

		if (report == null) {
			throw new RecordNotFoundException("Report " + reportId + " was not found in the database");
		}

		return report;
	}

	@SuppressWarnings("unchecked")
	public JSONObject buildJsonResponse(ReportContext reportContext) throws ReportValidationException, RecordNotFoundException, SQLException {
		Report report = createOrLoadReport(reportContext);

		// FIXME this basically initializes a report as well as building SQL
		SelectSQL sql = new SqlBuilder().initializeSql(report, reportContext.permissions);
		logger.debug("Running report {0} with SQL: {1}", report.getId(), sql.toString());

		// TODO see if this can go before the initializeSql() call into the createReport() function
		ReportUtil.addTranslatedLabelsToReportParameters(report, reportContext.permissions.getLocale());

		JSONObject responseJson = new JSONObject();

		if (reportContext.includeReport) {
			JSONObject reportJson = JsonReportBuilder.buildReportJson(report, reportContext.user.getId());
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

	protected ReportResults buildReportResults(Report report, ReportContext reportContext,
			List<BasicDynaBean> queryResults) {
		converter = new ReportDataConverter(report.getColumns(), queryResults);
		converter.setLocale(reportContext.permissions.getLocale());
		converter.convertForExtJS();
		return converter.getReportResults();
	}

	private boolean shouldIncludeSql(Permissions permissions) {
		return (permissions.isAdmin() || permissions.getAdminID() > 0);
	}

	protected List<BasicDynaBean> runQuery(SelectSQL sql, JSONObject json) throws PicsSqlException {
		List<BasicDynaBean> queryResults = null;

		try {
			queryResults = reportDao.runQuery(sql.toString(), json);
		} catch (SQLException se) {
			throw new PicsSqlException(se, sql.toString());
		}

		return queryResults;
	}

	public void convertForPrinting() {
		converter.convertForPrinting();
	}

	public void downloadReport(Report report) throws IOException {
		convertForPrinting();
		HSSFWorkbook workbook = buildWorkbook(report);
		writeFile(report.getName() + ".xls", workbook);
	}

	public List<ReportUser> getSortedFavoriteReports(int userId) {
		return reportUserDao.findAllFavorite(userId);
	}

	private HSSFWorkbook buildWorkbook(Report report) {
		logger.info("Building XLS File");
		ExcelBuilder builder = new ExcelBuilder();
		builder.addColumns(report.getColumns());
		builder.addSheet(report.getName(), converter.getReportResults());
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

}
