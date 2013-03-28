package com.picsauditing.actions.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;

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
import com.picsauditing.report.ReportUtil;
import com.picsauditing.service.ManageReportsService;
import com.picsauditing.service.ReportInfo;
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

	private List<ReportUser> reportUsers;
	private List<ReportUser> reportUserOverflow;
	private List<Report> reports;
	private List<ReportInfo> reportList;

	private Pagination<Report> pagination;

	// URL parameters
	private int reportId;
	private String searchTerm;
	private String sort;
	private String direction;

	private HttpServletRequest requestForTesting;

	public static final String LANDING_URL = "ManageReports!favorites.action";

	public static final String ALPHA_SORT = "alpha";
	public static final String DATE_ADDED_SORT = "dateAdded";
	public static final String LAST_VIEWED_SORT = "lastViewed";
	public static final String ASC = "ASC";
	public static final String DESC = "DESC";

	public static final int MAX_REPORTS_IN_MENU = 10;

	private static final Logger logger = LoggerFactory.getLogger(ManageReports.class);

	@Override
	public String execute() {
		try {
			setUrlForRedirect(LANDING_URL);
		} catch (IOException ioe) {
			logger.error("Problem redirecting from default action in ManageReports.", ioe);
		}

		return REDIRECT;
	}

	// TODO: Return a list of ReportInfo instead of reportUsers
	public String favorites() {
		try {
			reportUsers = reportUserDao.findAllFavorite(permissions.getUserId());

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

	// TODO: Return a list of ReportInfo instead of reportUsers
	public String myReports() {
		try {
			reportUsers = reportService.getAllReportUsers(sort, direction, permissions);
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

	public String search() {
		reports = new ArrayList<Report>();
		try {
			reports = manageReportsService.getReportsForSearch(searchTerm, permissions, getPagination());
		} catch (Exception e) {
			logger.error("Unexpected exception in ManageReports!search.action", e);
			if (permissions.has(OpPerms.Debug)) {
				addActionError(e.getMessage());
			}
		}

		if (AjaxUtils.isAjax(request())) {
			return "searchList";
		}

		return "search";
	}

	public String ownedBy() {
		reportList = new ArrayList<ReportInfo>();
		try {
			reportList = manageReportsService
					.getReportsForOwnedByUser(permissions);
		} catch (Exception e) {
			logger.error("Unexpected exception in ManageReports!ownedBy.action", e);
			if (permissions.has(OpPerms.Debug)) {
				addActionError(e.getMessage());
			}
		}

		if (AjaxUtils.isAjax(request())) {
			return "ownedByList";
		}

		return "ownedBy";
	}

	public String sharedWith() {
		reportList = new ArrayList<ReportInfo>();
		try {
			reportList = manageReportsService
					.getReportsForSharedWithUser(permissions);
		} catch (Exception e) {
			logger.error("Unexpected exception in ManageReports!sharedWith.action", e);
			if (permissions.has(OpPerms.Debug)) {
				addActionError(e.getMessage());
			}
		}

		if (AjaxUtils.isAjax(request())) {
			return "sharedWithList";
		}

		return "sharedWith";
	}

	public String moveFavoriteUp() {
		try {
			ReportUser reportUser = reportService.loadReportUser(permissions.getUserId(), reportId);

			manageReportsService.moveFavoriteUp(reportUser);
		} catch (NoResultException nre) {
			logger.warn("No result found in ReportApi.moveFavoriteUp()", nre);
		} catch (Exception e) {
			logger.error("Unexpected exception in ReportApi.moveFavoriteUp(). ", e);
		}

		return redirectToPreviousView();
	}

	public String moveFavoriteDown() {
		try {
			ReportUser reportUser = reportService.loadReportUser(permissions.getUserId(), reportId);

			manageReportsService.moveFavoriteDown(reportUser);
		} catch (NoResultException nre) {
			logger.warn("No result found in ReportApi.moveFavoriteDown()", nre);
		} catch (Exception e) {
			logger.error("Unexpected exception in ReportApi.moveFavoriteDown(). ", e);
		}

		return redirectToPreviousView();
	}

	/**
	 * This method has purposely been left empty because we wanted to disable
	 * this functionality.
	 *
	 * @return
	 */
	public String removeReportUser() {
		return redirectToPreviousView();
	}

	/**
	 * This method has purposely been left empty because we wanted to disable
	 * this functionality.
	 *
	 * @return
	 */
	public String removeReportPermissionUser() {
		return redirectToPreviousView();
	}

	/**
	 * This method has purposely been left empty because we wanted to disable
	 * this functionality.
	 *
	 * @return
	 */
	public String deleteReport() {
		return redirectToPreviousView();
	}

	public String favorite() {
		try {
			ReportUser reportUser = reportService.loadOrCreateReportUser(permissions.getUserId(), reportId);
			manageReportsService.favoriteReport(reportUser);
		} catch (NoResultException nre) {
			logger.error(nre.toString());
		} catch (Exception e) {
			logger.error("Uncaught exception in ManageReports.favorite(). ", e);
		}

		return redirectToPreviousView();
	}

	public String unfavorite() {
		try {
			ReportUser reportUser = reportService.loadOrCreateReportUser(permissions.getUserId(), reportId);
			manageReportsService.unfavoriteReport(reportUser);
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

	public List<ReportInfo> getReportList() {
		return reportList;
	}
}
