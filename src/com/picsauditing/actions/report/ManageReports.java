package com.picsauditing.actions.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;

import com.picsauditing.service.ReportPreferencesService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ReportDAO;
import com.picsauditing.dao.ReportUserDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.report.RecordNotFoundException;
import com.picsauditing.report.ReportUtil;
import com.picsauditing.report.ReportValidationException;
import com.picsauditing.service.ManageReportsService;
import com.picsauditing.service.ReportService;
import com.picsauditing.strutsutil.AjaxUtils;
import com.picsauditing.util.Strings;
import com.picsauditing.util.pagination.Pagination;
import com.picsauditing.util.pagination.PaginationParameters;

@SuppressWarnings("serial")
public class ManageReports extends PicsActionSupport {

	@Autowired
	private ReportService reportService;
	@Autowired
	private ReportDAO reportDao;
	@Autowired
	private ReportUserDAO reportUserDao;
	@Autowired
	private ManageReportsService manageReportsService;
	@Autowired
	private ReportPreferencesService reportPreferencesService;

	private User toOwner;

	private List<ReportUser> reportUsers;
	private List<ReportUser> reportUserOverflow;
	private List<Report> reports;

	private Pagination<Report> pagination;

	// URL parameters
	private int reportId;
	private String searchTerm;
	private String sort;
	private String direction;

	private HttpServletRequest requestForTesting;

	public static final String LANDING_URL = "ManageReports!favoritesList.action";

	public static final String ALPHA_SORT = "alpha";
	public static final String DATE_ADDED_SORT = "dateAdded";
	public static final String LAST_VIEWED_SORT = "lastViewed";
	public static final String ASC = "ASC";
	public static final String DESC = "DESC";

	public static final int MAX_REPORTS_IN_MENU = 10;

	private static final Logger logger = LoggerFactory.getLogger(ManageReports.class);

	public String execute() {
		try {
			setUrlForRedirect(LANDING_URL);
		} catch (IOException ioe) {
			logger.error("Problem redirecting from default action in ManageReports.", ioe);
		}

		return REDIRECT;
	}

	public String favoritesList() {
		try {
			reportUsers = manageReportsService.buildFavorites(permissions.getUserId());

			if (CollectionUtils.isEmpty(reportUsers)) {
				reportUsers = new ArrayList<ReportUser>();
			}

			if (reportUsers.size() > MAX_REPORTS_IN_MENU) {
				reportUserOverflow = reportUsers.subList(MAX_REPORTS_IN_MENU,
						reportUsers.size());
				reportUsers = reportUsers.subList(0, MAX_REPORTS_IN_MENU);
			}
		} catch (Exception e) {
			logger.error("Unexpected exception in ManageReports!favoritesList.action", e);
		}

		if (AjaxUtils.isAjax(request())) {
			return "favoritesList";
		}

		return "favorites";
	}

	public String moveFavoriteUp() {
		try {
			ReportUser reportUser = reportPreferencesService.loadReportUser(permissions.getUserId(), reportId);

			manageReportsService.moveFavoriteUp(reportUser);
		} catch (NoResultException nre) {
			logger.warn("Unable to moveFavoriteUp. ReportUser not found for user id " + permissions.getUserId() + " and report id " + reportId, nre);
		} catch (Exception e) {
			logger.error("Unexpected exception in ReportApi.moveFavoriteUp(). ", e);
		}

		return redirectToPreviousView();
	}

	public String moveFavoriteDown() {
		try {
			ReportUser reportUser = reportPreferencesService.loadReportUser(permissions.getUserId(), reportId);

			manageReportsService.moveFavoriteDown(reportUser);
		} catch (NoResultException nre) {
			logger.warn("Unable to moveFavoriteDown. ReportUser not found for user id " + permissions.getUserId() + " and report id " + reportId, nre);
		} catch (Exception e) {
			logger.error("Unexpected exception in ReportApi.moveFavoriteDown(). ", e);
		}

		return redirectToPreviousView();
	}

	public String myReportsList() {
		try {
			reportUsers = reportPreferencesService.getAllReportUsers(sort, direction, permissions);
		} catch (IllegalArgumentException iae) {
			logger.warn("Illegal argument exception in ManageReports!myReportsList.action", iae);
		} catch (Exception e) {
			logger.error("Unexpected exception in ManageReports!myReportsList.action", e);
		}

		if (CollectionUtils.isEmpty(reportUsers)) {
			reportUsers = new ArrayList<ReportUser>();
		}

		if (AjaxUtils.isAjax(request())) {
			return "myReportsList";
		}

		return "myReports";
	}

	public String searchList() {
		reports = new ArrayList<Report>();
		try {
			reports = manageReportsService.getReportsForSearch(searchTerm, permissions, getPagination());
		} catch (Exception e) {
			logger.error("Unexpected exception in ManageReports!searchList.action", e);
			if (permissions.has(OpPerms.Debug)) {
				addActionError(e.getMessage());
			}
		}

		if (AjaxUtils.isAjax(request())) {
			return "searchList";
		}

		return "search";
	}

	public String transferOwnership() {
		try {
			Report report = reportService.loadReportFromDatabase(reportId);

			manageReportsService.transferOwnership(getUser(), toOwner, report, permissions);
		} catch (RecordNotFoundException rnfe) {
			logger.error("Report " + reportId + " not found. Cannot transfer ownership.", rnfe);
			return ERROR;
		} catch (ReportValidationException rve) {
			logger.error("Report " + reportId + " not valid. Cannot transfer ownership.", rve);
			return ERROR;
		} catch (Exception e) {
			logger.error("There was an exception with report " + reportId + ". Cannot transfer ownership.", e);
			return ERROR;
		}

		return NONE;
	}

	public String deleteReport() {
		try {
			Report report = reportService.loadReportFromDatabase(reportId);

			manageReportsService.deleteReport(getUser(), report, permissions);
		} catch (RecordNotFoundException rnfe) {
			// Report doesn't exist, so we don't need to do anything.
		} catch (ReportValidationException rve) {
			logger.error("Report " + reportId + " not valid. Cannot delete.", rve);
			return ERROR;
		} catch (Exception e) {
			logger.error("There was an exception with report " + reportId + ". Cannot delete.", e);
			return ERROR;
		}

		return NONE;
	}

	public String shareWithViewPermission() {
		try {
			Report report = reportService.loadReportFromDatabase(reportId);

			manageReportsService.shareWithViewPermission(getUser(), toOwner, report, permissions);
		} catch (RecordNotFoundException rnfe) {
			logger.error("Report " + reportId + " not found. Cannot share.", rnfe);
			return ERROR;
		} catch (ReportValidationException rve) {
			logger.error("Report " + reportId + " not valid. Cannot share.", rve);
			return ERROR;
		} catch (Exception e) {
			logger.error("There was an exception with report " + reportId + ". Cannot share.", e);
			return ERROR;
		}

		return NONE;
	}

	public String shareWithEditPermission() {
		try {
			Report report = reportService.loadReportFromDatabase(reportId);

			manageReportsService.shareWithEditPermission(getUser(), toOwner, report, permissions);
		} catch (RecordNotFoundException rnfe) {
			logger.error("Report " + reportId + " not found. Cannot share.", rnfe);
			return ERROR;
		} catch (ReportValidationException rve) {
			logger.error("Report " + reportId + " not valid. Cannot share.", rve);
			return ERROR;
		} catch (Exception e) {
			logger.error("There was an exception with report " + reportId + ". Cannot share.", e);
			return ERROR;
		}

		return NONE;
	}

	public String removeReportUser() {
		try {
			Report report = reportService.loadReportFromDatabase(reportId);
			manageReportsService.removeReportUser(getUser(), report,  permissions);
		} catch (NoResultException nre) {
			logger.error(nre.toString());
		} catch (Exception e) {
			logger.error("Uncaught exception in ManageReports.removeReportUser(). ", e);
		}

		return redirectToPreviousView();
	}

	public String favorite() {
		try {
			ReportUser reportUser = reportPreferencesService.loadOrCreateReportUser(permissions.getUserId(), reportId);
			reportPreferencesService.favoriteReport(reportUser);
		} catch (NoResultException nre) {
			logger.error(nre.toString());
		} catch (Exception e) {
			logger.error("Uncaught exception in ManageReports.favorite(). ", e);
		}

		return redirectToPreviousView();
	}

	public String unfavorite() {
		try {
			ReportUser reportUser = reportPreferencesService.loadOrCreateReportUser(permissions.getUserId(), reportId);
			reportPreferencesService.unfavoriteReport(reportUser);
		} catch (NoResultException nre) {
			logger.error(nre.toString());
		} catch (Exception e) {
			logger.error("Uncaught exception in ManageReports.unfavorite(). ", e);
		}

		return redirectToPreviousView();
	}

	/**
	 * Exclusively to export Columns for translations
	 *
	 * @return
	 * @throws Exception
	 */
	public String columnsToTranslate() throws Exception {
		List<Report> allReports = reportDao.findAll(Report.class);
		ReportUtil.findColumnsToTranslate(allReports);
		return SUCCESS;
	}

	private String redirectToPreviousView() {
		try {
			String referer = getRequest().getHeader("Referer");
			if (Strings.isEmpty(referer)) {
				referer = LANDING_URL;
			}

			setUrlForRedirect(referer);
		} catch (IOException e) {
			logger.warn("Problem setting URL for redirect in ManageReports.redirectToPreviousView()", e);
		} catch (Exception e) {
			logger.error("Unexpected problem in ManageReports.redirectToPreviousView()");
		}

		return REDIRECT;
	}

	private HttpServletRequest request() {
		if (requestForTesting != null) {
			return requestForTesting;
		}

		return getRequest();
	}

	public User getToOwner() {
		return toOwner;
	}

	public void setToOwner(User toOwner) {
		this.toOwner = toOwner;
	}

	public List<ReportUser> getReportUsers() {
		return reportUsers;
	}

	public void setReportUsers(List<ReportUser> reportUsers) {
		this.reportUsers = reportUsers;
	}

	public List<ReportUser> getReportUserOverflow() {
		return reportUserOverflow;
	}

	public List<Report> getReports() {
		return reports;
	}

	public void setReports(List<Report> reports) {
		this.reports = reports;
	}

	public ReportService getReportService() {
		return reportService;
	}

	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}

	public int getReportId() {
		return reportId;
	}

	public void setReportId(int reportId) {
		this.reportId = reportId;
	}

	public String getSearchTerm() {
		return searchTerm;
	}

	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public Pagination<Report> getPagination() {
		if (pagination == null) {
			pagination = new Pagination<Report>();
			pagination.setParameters(new PaginationParameters());
		}

		return pagination;
	}

	public void setPagination(Pagination<Report> pagination) {
		this.pagination = pagination;
	}

	public String getAlphaSortDirection() {
		if (!ALPHA_SORT.equals(sort) || DESC.equals(direction)) {
			return ASC;
		}

		return DESC;
	}

	public String getDateAddedSortDirection() {
		if (!DATE_ADDED_SORT.equals(sort) || ASC.equals(direction)) {
			return DESC;
		}

		return ASC;
	}

	public String getLastViewedSortDirection() {
		if (!LAST_VIEWED_SORT.equals(sort) || ASC.equals(direction)) {
			return DESC;
		}

		return ASC;
	}

	public String getAlphaSort() {
		return ALPHA_SORT;
	}

	public String getDateAddedSort() {
		return DATE_ADDED_SORT;
	}

	public String getLastViewedSort() {
		return LAST_VIEWED_SORT;
	}

	public String getMyReportsUrl() {
		return LANDING_URL;
	}
}
