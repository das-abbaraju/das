package com.picsauditing.model.report;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.servlet.ServletOutputStream;

import com.picsauditing.access.*;
import com.picsauditing.actions.report.PicsSqlException;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.report.ReportJson;
import com.picsauditing.report.SqlBuilder;
import com.picsauditing.report.access.ReportUtil;
import com.picsauditing.report.converter.AvailableFieldsToExtJSConverter;
import com.picsauditing.report.converter.ExtJSToReportConverter;
import com.picsauditing.report.converter.ReportToExtJSConverter;
import com.picsauditing.report.data.ReportDataConverter;
import com.picsauditing.report.data.ReportResults;
import com.picsauditing.report.models.AbstractModel;
import com.picsauditing.report.models.ModelFactory;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.JSONUtilities;
import com.picsauditing.util.excel.ExcelBuilder;
import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.ServletActionContext;
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
import com.picsauditing.report.ReportPaginationParameters;
import com.picsauditing.report.converter.LegacyReportConverter;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.Strings;
import com.picsauditing.util.pagination.Pagination;

import static com.picsauditing.report.ReportJson.*;

public class ReportService {

	private static final int REPORT_DEVELOPER_GROUP = 77375;

	@Autowired
	private ReportDAO reportDao;
	@Autowired
	private ReportUserDAO reportUserDao;
	@Autowired
	private ReportPermissionUserDAO reportPermissionUserDao;
	@Autowired
	private ReportPermissionAccountDAO reportPermissionAccountDao;

	@SuppressWarnings("deprecation")
	@Autowired
	private LegacyReportConverter legacyReportConverter;
	@Autowired
	private FeatureToggle featureToggle;

	private static final Logger logger = LoggerFactory.getLogger(ReportService.class);
	private ReportDataConverter converter;

	public boolean canUserViewAndCopy(Permissions permissions, int reportId) {

		try {
			reportPermissionUserDao.findOneByPermissions(permissions, reportId);
		} catch (NoResultException nre) {
			try {
				reportPermissionAccountDao.findOne(permissions.getAccountId(), reportId);
			} catch (NoResultException nr) {
				return isReportDevelopmentGroup(permissions);
			}
		}

		return true;
	}

	public boolean canUserEdit(Permissions permissions, Report report) {
		boolean editable = false;

		try {
			editable = reportPermissionUserDao.findOneByPermissions(permissions, report.getId()).isEditable();
		} catch (NoResultException nre) {
			logger.error("No results found for {} and reportId = {}", permissions.toString(), report.getId());
		}

		return editable || isReportDevelopmentGroup(permissions);
	}

	private boolean isReportDevelopmentGroup(Permissions permissions) {
		try {
			int userID = permissions.getUserId();

			if (permissions.getAdminID() > 0) {
				userID = permissions.getAdminID();
			}

			String where = "group.id = " + REPORT_DEVELOPER_GROUP + " AND user.id = " + userID;

			reportDao.findOne(UserGroup.class, where);
		} catch (NoResultException nre) {
			return false;
		}

		return true;
	}

	public void setEditPermissions(Permissions permissions, int id, int reportId, boolean editable)
			throws NoResultException, NonUniqueResultException, SQLException, Exception {
		ReportPermissionUser reportPermissionUser = connectReportPermissionUser(permissions, id, reportId, editable);

		reportPermissionUserDao.save(reportPermissionUser);
	}

	public Report copy(Report sourceReport, Permissions permissions, boolean favorite) throws NoResultException,
			NonUniqueResultException, SQLException, Exception {
		if (!canUserViewAndCopy(permissions, sourceReport.getId())) {
			throw new NoRightsException("User " + permissions.getUserId() + " does not have permission to copy report "
					+ sourceReport.getId());
		}

		boolean editable = true;

		Report newReport = copyReportWithoutPermissions(sourceReport);

		// TODO the front end is passing new report data in the current report,
		// so we need to change sourceReport to it's old state.
		// Is this is the desired behavior?
		reportDao.refresh(sourceReport);

		validate(newReport);
		// setReportParameters(newReport);
		newReport.setAuditColumns(permissions);
		reportDao.save(newReport);

		if (favorite) {
			favoriteReport(permissions.getUserId(), newReport.getId());
		}

		// This is a new report owned by the user, unconditionally give them
		// edit permission
		connectReportUser(permissions.getUserId(), newReport.getId());
		connectReportPermissionUser(permissions, permissions.getUserId(), newReport.getId(), editable);

		return newReport;
	}

	public void edit(Report report, Permissions permissions) throws Exception {
		if (!canUserEdit(permissions, report)) {
			throw new NoRightsException("User " + permissions.getUserId() + " cannot edit report " + report.getId());
		}

		// TODO Consider adding a "save" column to the ReportElement class to
		// store Delete/Update/Insert flags
		// Allow updating rather than full delete/insert instead
		removeReportElements(report);

		legacyConvertParametersToReport(report);
		setReportParameters(report);
		saveReportElements(report);
	}

	public void saveReportElements(Report report) {
		reportDao.save(report.getColumns());
		reportDao.save(report.getFilters());
		reportDao.save(report.getSorts());
		reportDao.save(report);
	}

	public void removeReportElements(Report report) {
		removeAllReportElements(report.getId(), Column.class);
		report.getColumns().clear();
		removeAllReportElements(report.getId(), Filter.class);
		report.getFilters().clear();
		removeAllReportElements(report.getId(), Sort.class);
		report.getSorts().clear();
	}

	private <E extends ReportElement> void removeAllReportElements(int reportId, Class<E> type) {
		reportDao.remove(type, "t.report.id = " + reportId);
	}

	// TODO Remove this method after the next release
	@SuppressWarnings("deprecation")
	public void legacyConvertParametersToReport(Report report) throws ReportValidationException {
		if (report == null) {
			throw new IllegalArgumentException("Report should not be null");
		}

		if (!isBackwardsCompatibilityOn()) {
			return;
		}

		legacyReportConverter.convertParametersToEntities(report);
	}

	// TODO: Remove this method after the next release
	@SuppressWarnings("deprecation")
	private void setReportParameters(Report report) {
		if (!isBackwardsCompatibilityOn()) {
			return;
		}

		JSONObject json = legacyReportConverter.toJSON(report);
		report.setParameters(json.toString());
	}

	private boolean isBackwardsCompatibilityOn() {
		return featureToggle.isFeatureEnabled(FeatureToggle.TOGGLE_DR_STORAGE_BACKWARDS_COMPATIBILITY);
	}

	private Report copyReportWithoutPermissions(Report sourceReport) {
		Report newReport = new Report();

		newReport.setModelType(sourceReport.getModelType());
		newReport.setName(sourceReport.getName());
		newReport.setDescription(sourceReport.getDescription());
		newReport.setParameters(sourceReport.getParameters());

		return newReport;
	}

	/**
	 * Rudimentary validation of a report object. Currently, this only means
	 * that the model type is set and that the report-spec is parsable as valid
	 * JSON.
	 */
	public void validate(Report report) throws ReportValidationException {
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

	@SuppressWarnings("deprecation")
	public List<ReportUser> getReportUsersForMyReports(String sort, String direction, Permissions permissions)
			throws IllegalArgumentException {
		List<ReportUser> reportUsers = new ArrayList<ReportUser>();

		if (Strings.isEmpty(sort)) {
			sort = ManageReports.ALPHA_SORT;
			direction = "ASC";
		}

		List<Report> reports = reportDao.findAllOrdered(permissions, sort, direction);

		for (Report report : reports) {
			ReportUser reportUser = report.getReportUser(permissions.getUserId());
			if (reportUser == null) {
				reportUser = new ReportUser();

				User user = new User();
				user.setId(permissions.getUserId());
				reportUser.setUser(user);
				reportUser.setReport(report);

				reportUser.setFavorite(false);
				reportUserDao.save(reportUser);
			}
			reportUsers.add(reportUser);
		}

		return reportUsers;
	}

	public ReportUser loadOrCreateReportUser(User user, Report report) {
		ReportUser reportUser = new ReportUser();

		try {
			reportUser = reportUserDao.findOne(user.getId(), report.getId());
		} catch (NoResultException nre) {
			reportUser.setUser(user);
			reportUser.setReport(report);
		}

		reportUser.setLastViewedDate(new Date());
		reportUser.setViewCount(reportUser.getViewCount() + 1);
		return reportUser;
	}

	public void favoriteReport(int userId, int reportId) throws NoResultException, NonUniqueResultException,
			SQLException, Exception {
		ReportUser reportUser = connectReportUser(userId, reportId);

		reportUserDao.cascadeFavoriteReportSorting(userId, 1, 1, Integer.MAX_VALUE);

		reportUser.setSortOrder(1);
		reportUser.setFavorite(true);
		reportUserDao.save(reportUser);
	}

	public void unfavoriteReport(int userId, int reportId) throws NoResultException, NonUniqueResultException,
			SQLException, Exception {
		ReportUser unfavoritedReportUser = connectReportUser(userId, reportId);
		int removedSortIndex = unfavoritedReportUser.getSortOrder();

		reportUserDao.cascadeFavoriteReportSorting(userId, -1, removedSortIndex + 1, Integer.MAX_VALUE);

		unfavoritedReportUser.setSortOrder(0);
		unfavoritedReportUser.setFavorite(false);
		reportUserDao.save(unfavoritedReportUser);
	}

	public void moveReportUser(int userId, int reportId, int magnitude) throws Exception {
		reportUserDao.resetSortOrder(userId);

		ReportUser reportUser = reportUserDao.findOne(userId, reportId);
		int numberOfFavorites = reportUserDao.getFavoriteCount(userId);
		int currentPosition = reportUser.getSortOrder();
		int newPosition = currentPosition + magnitude;

		if (currentPosition == newPosition || newPosition < 0 || newPosition > numberOfFavorites) {
			return;
		}

		int offsetPosition;
		int topPositionToMove;
		int bottomPositionToMove;

		if (currentPosition < newPosition) {
			// Moving down in list, other reports move up
			offsetPosition = -1;
			topPositionToMove = currentPosition + 1;
			bottomPositionToMove = newPosition;
		} else {
			// Moving up in list, other reports move down
			offsetPosition = 1;
			topPositionToMove = newPosition;
			bottomPositionToMove = currentPosition - 1;
		}

		reportUserDao.cascadeFavoriteReportSorting(userId, offsetPosition, topPositionToMove, bottomPositionToMove);

		reportUser.setSortOrder(newPosition);
		reportUserDao.save(reportUser);
	}

	public ReportUser connectReportUser(int userId, int reportId) {
		ReportUser reportUser;

		try {
			reportUser = reportUserDao.findOne(userId, reportId);
		} catch (NoResultException nre) {
			// Need to connect user to report first
			Report report = reportDao.find(Report.class, reportId);
			reportUser = new ReportUser(userId, report);
			reportUser.setAuditColumns(new User(userId));
			reportUser.setFavorite(false);
			reportUserDao.save(reportUser);
		}

		return reportUser;
	}

	/**
	 * Create permissions to access the report permissions.
	 *
	 * @param permissions
	 *            Permissions object from request
	 * @param userId
	 *            Could be either the User ID or Group ID to share with
	 * @param reportId
	 * @param editable
	 * @return
	 */
	public ReportPermissionUser connectReportPermissionUser(Permissions permissions, int userId, int reportId,
			boolean editable) {
		ReportPermissionUser reportPermissionUser;

		try {
			reportPermissionUser = reportPermissionUserDao.findOne(userId, reportId);
		} catch (NoResultException nre) {
			// Need to connect user to report first
			Report report = reportDao.find(Report.class, reportId);
			reportPermissionUser = new ReportPermissionUser(userId, report);
			reportPermissionUser.setAuditColumns(new User(permissions.getUserId()));
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

	public void disconnectReportPermissionUser(int id, int reportId) {
		try {
			reportPermissionUserDao.revokePermissions(id, reportId);
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

	public void removeAndCascade(Report report) {
		List<ReportPermissionUser> reportPermissionUsers = reportPermissionUserDao.findAllByReportId(report.getId());
		for (ReportPermissionUser reportPermissionUser : reportPermissionUsers) {
			reportPermissionUserDao.remove(reportPermissionUser);
		}

		List<ReportPermissionAccount> reportPermissionAccounts = reportPermissionAccountDao.findAllByReportId(report
				.getId());
		for (ReportPermissionAccount reportPermissionAccount : reportPermissionAccounts) {
			reportPermissionAccountDao.remove(reportPermissionAccount);
		}
	}

	public Report createReport(ReportContext reportContext) throws RecordNotFoundException, ReportValidationException {
		JSONObject reportJson = null;

		if (JSONUtilities.isNotEmpty(reportContext.payloadJson)) {
			reportJson = getReportJsonFromPayload(reportContext.payloadJson);
		}

		Report report = null;
		if (shouldLoadReportFromJson(reportJson, reportContext.includeData)) {
			report = buildReportFromJson(reportJson, reportContext.reportId);
		} else {
			report = loadReportFromDatabase(reportContext.reportId);
		}

		validate(report);
		ReportUser reportUser = loadOrCreateReportUser(reportContext.user, report);
		reportUserDao.save(reportUser);

		// FIXME: This is a problem that will cause a Ninja save that the refresh above
		//        was fixing
//		if (StringUtils.isNotEmpty(reportParameters)) {
//			report.setParameters(reportParameters);
//		}

		// FIXME this basically initializes a report as well as building SQL
		buildSqlForReport(reportContext.permissions, report);

		ReportUtil.addTranslatedLabelsToReportParameters(report, reportContext.permissions.getLocale());

		return report;
	}

	private JSONObject getReportJsonFromPayload(JSONObject payloadJson) {
		JSONObject reportJson = (JSONObject) payloadJson.get(ReportJson.LEVEL_REPORT);
		return reportJson;
	}

	private boolean shouldLoadReportFromJson(JSONObject reportJson, boolean includeData) {
		return JSONUtilities.isNotEmpty(reportJson) && includeData;
	}

	private Report buildReportFromJson(JSONObject jsonReport, int reportId) {
		Report report = ExtJSToReportConverter.convertToReport(jsonReport);
		report.setId(reportId);
		return report;
	}

	private Report loadReportFromDatabase(int reportId) throws RecordNotFoundException, ReportValidationException {
		Report report = reportDao.find(Report.class, reportId);

		if (report == null) {
			throw new RecordNotFoundException("Report " + reportId + " was not found in the database");
		}

		legacyConvertParametersToReport(report);

		return report;
	}

	private SelectSQL buildSqlForReport(Permissions permissions, Report report) throws ReportValidationException {
		SelectSQL sql = new SqlBuilder().initializeSql(report, permissions);
		logger.debug("Running report {0} with SQL: {1}", report.getId(), sql.toString());
		return sql;
	}

	public JSONObject buildJsonResponse(ReportContext reportContext) throws ReportValidationException, RecordNotFoundException, SQLException {
		Report report = createReport(reportContext);

		JSONObject json = new JSONObject();

		if (reportContext.includeReport) {
			json.put(LEVEL_REPORT, ReportToExtJSConverter.toJSON(report));
		}

		AbstractModel reportModel = ModelFactory.build(report.getModelType(), reportContext.permissions);
		if (reportContext.includeColumns) {
			json.put(LEVEL_COLUMNS, AvailableFieldsToExtJSConverter.getColumns(reportModel, reportContext.permissions));
		}

		if (reportContext.includeFilters) {
			json.put(LEVEL_FILTERS, AvailableFieldsToExtJSConverter.getFilters(reportModel, reportContext.permissions));
		}

		if (reportContext.includeData) {
			json.put(LEVEL_RESULTS, getData(reportContext, report, json));
		}

		json.put(ReportJson.EXT_JS_SUCCESS, true);

		return json;
	}

	private JSONObject getData(ReportContext reportContext, Report report, JSONObject json) throws ReportValidationException, PicsSqlException {
		JSONObject jsonObject = new JSONObject();
		SelectSQL sql = buildSqlForReport(reportContext.permissions, report);
		if (shouldIncludeSql(reportContext.permissions)) {
			String debugSQL = sql.toString().replace("\n", " ").replace("  ", " ");
			jsonObject.put(ReportUtil.SQL, debugSQL);
		}

		sql.setPageNumber(reportContext.limit, reportContext.pageNumber);
		runQuery(sql, json, reportContext.permissions, report);

		// TODO have this take something or return something
		converter.convertForExtJS();
		ReportResults reportResults = converter.getReportResults();
		jsonObject.put(LEVEL_DATA, reportResults.toJson());
		jsonObject.put(RESULTS_TOTAL, json.get(RESULTS_TOTAL));
		json.remove(RESULTS_TOTAL);

		return jsonObject;
	}



	private boolean shouldIncludeSql(Permissions permissions) {
		return (permissions.isAdmin() || permissions.getAdminID() > 0);
	}

	protected void runQuery(SelectSQL sql, JSONObject json, Permissions permissions, Report report) throws PicsSqlException {
		List<BasicDynaBean> queryResults = null;
		try {
			queryResults = reportDao.runQuery(sql.toString(), json);
		} catch (SQLException se) {
			throw new PicsSqlException(se, sql.toString());
		}
		converter = new ReportDataConverter(report.getColumns(), queryResults);
		converter.setLocale(permissions.getLocale());
	}

	private void handleErrorForData(Exception error, Report report, SelectSQL sql, Permissions permissions) throws Exception {
		logger.error("Report:" + report.getId() + " " + error.getMessage() + " SQL: " + sql);

		if (permissions.has(OpPerms.Debug) || permissions.getAdminID() > 0) {
			throw new Exception(error + " debug SQL: " + sql.toString());
		} else {
			throw new Exception("Invalid Query");
		}
	}

	public void convertForPrinting() {
		converter.convertForPrinting();
	}

	public void downloadReport(Report report) throws IOException {
		convertForPrinting();
		HSSFWorkbook workbook = buildWorkbook(report);
		writeFile(report.getName() + ".xls", workbook);
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
