package com.picsauditing.actions.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;

import com.picsauditing.access.ReportPermissionException;
import com.picsauditing.service.*;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.UserService;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ReportDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.report.RecordNotFoundException;
import com.picsauditing.report.ReportUtil;
import com.picsauditing.report.ReportValidationException;
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
	private ManageReportsService manageReportsService;
	@Autowired
	private ReportPreferencesService reportPreferencesService;
	@Autowired
	private PermissionService permissionService;
	@Autowired
	private UserService userService;
	@Autowired
	private ReportFavoriteInfoConverter reportFavoriteInfoConverter;

	// TODO remove this after we have separate pojos for all views
	private List<ReportInfo> reportList;

	private List<ReportFavoriteInfo> reportFavoriteList;
	private List<ReportFavoriteInfo> reportFavoriteListOverflow;

	private List<ReportPermissionInfo> userAccessList;
	private List<ReportPermissionInfo> groupAccessList;

	private Pagination<ReportInfo> pagination;

	private boolean isCurrentUserOwner;

	// URL parameters
	private int reportId;
	private String searchTerm;
	private String sort;
	private String direction;
	protected int shareId;
	protected boolean editable;

	private int pinnedIndex;

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

	public String favorites() {
		try {
			List<ReportUser> favorites = reportPreferencesService.buildFavorites(permissions.getUserId());
			reportFavoriteList = reportFavoriteInfoConverter.convert(favorites);

			if (CollectionUtils.isEmpty(reportFavoriteList)) {
				reportFavoriteList = Collections.emptyList();
			}

			if (reportFavoriteList.size() > MAX_REPORTS_IN_MENU) {
				reportFavoriteListOverflow = reportFavoriteList.subList(MAX_REPORTS_IN_MENU, reportFavoriteList.size());
				reportFavoriteList = reportFavoriteList.subList(0, MAX_REPORTS_IN_MENU);
			}
		} catch (Exception e) {
			logger.error("Unexpected exception in ManageReports!favorites.action", e);
		}

		return determineViewName("favoritesList", "favorites");
	}

	public String ownedBy() {
		reportList = new ArrayList<>();

		try {

			reportList = manageReportsService.getReportsForOwnedByUser(buildReportSearch());
		} catch (NoResultException nre) {
			logger.warn("Unable to moveFavoriteDown. ReportUser not found for user id " + permissions.getUserId()
					+ " and report id " + reportId, nre);
		} catch (Exception e) {
			logAndShowUserInDebugMode("Unexpected exception in ManageReports!ownedBy.action", e);
		}

		return determineViewName("ownedByList", "ownedBy");
	}

	public String sharedWith() {
		reportList = Collections.emptyList();
		try {
			reportList = manageReportsService.getReportsForSharedWithUser(buildReportSearch());
		} catch (Exception e) {
			logAndShowUserInDebugMode("Unexpected exception in ManageReports!sharedWith.action", e);
		}

		return determineViewName("sharedWithList", "sharedWith");
	}

	// TODO: Fix this method to call David A's new method
	public String search() {
		reportList = Collections.emptyList();

		try {
			reportList = manageReportsService.getReportsForSearch(searchTerm, permissions, getPagination());
		} catch (IllegalArgumentException iae) {
			logger.warn("Illegal argument exception in ManageReports!myReportsList.action", iae);
		} catch (Exception e) {
			logAndShowUserInDebugMode("Unexpected exception in ManageReports!search.action", e);
		}

		return determineViewName("searchList", "search");
	}

	public String access() {
		userAccessList = Collections.emptyList();
		groupAccessList = Collections.emptyList();

		try {
			Report report = reportService.loadReportFromDatabase(reportId);
			User user = userService.loadUser(permissions.getUserId());

			if ( !(permissionService.canUserShareReport(user, report)
					|| permissionService.isReportDevelopmentGroup(permissions)) ) {
				throw new ReportPermissionException("User " + permissions.getUserId() + " cannot view access rights for report " + reportId + " because they are not the owner.");
			}

			isCurrentUserOwner = permissionService.isOwner(user, report);

			userAccessList = manageReportsService.buildUserAccessList(report);
			groupAccessList = manageReportsService.buildGroupAndAccountAccessList(report, permissions.getLocale());
		} catch (Exception e) {
			logAndShowUserInDebugMode("Unexpected exception in ManageReports!access.action", e);
		}

		return determineViewName("accessList", "access");
	}

	private String determineViewName(String ajaxViewname, String jspViewName) {
		if (AjaxUtils.isAjax(request())) {
			return ajaxViewname;
		}

		return jspViewName;
	}

	public String moveFavoriteUp() {
		try {
			ReportUser reportUser = reportPreferencesService.loadReportUser(permissions.getUserId(), reportId);

			reportPreferencesService.moveUnpinnedFavoriteUp(reportUser);
		} catch (NoResultException nre) {
			logger.warn("No result found in ReportApi.moveFavoriteUp()", nre);
		} catch (Exception e) {
			logger.error("Unexpected exception in ReportApi.moveFavoriteUp(). ", e);
		}

		return redirectOrReturnNoneForAjaxRequest();
	}

	private String redirectOrReturnNoneForAjaxRequest() {
		if (AjaxUtils.isAjax(request())) {
			return NONE;
		}

		return redirectToPreviousView();
	}

	public String moveFavoriteDown() {
		try {
			ReportUser reportUser = reportPreferencesService.loadReportUser(permissions.getUserId(), reportId);

			reportPreferencesService.moveUnpinnedFavoriteDown(reportUser);
		} catch (NoResultException nre) {
			logger.warn("No result found in ReportApi.moveFavoriteDown()", nre);
		} catch (Exception e) {
			logger.error("Unexpected exception in ReportApi.moveFavoriteDown(). ", e);
		}

		return redirectOrReturnNoneForAjaxRequest();
	}

	public String transferOwnership() {
		try {
			Report report = reportService.loadReportFromDatabase(reportId);
			User newOwner = userService.loadUser(shareId);

			manageReportsService.transferOwnership(getUser(), newOwner, report, permissions);
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

		return redirectOrReturnNoneForAjaxRequest();
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

		return redirectOrReturnNoneForAjaxRequest();
	}

	public String shareWithUserViewPermission() {
		try {
			Report report = reportService.loadReportFromDatabase(reportId);
			User toUser = userService.loadUser(shareId);

			manageReportsService.shareReportWithUserOrGroup(getUser(), toUser, report, permissions, false);
		} catch (Exception e) {
			logger.error("There was an exception with report " + reportId + ". Cannot share.", e);
			return ERROR;
		}

		return redirectOrReturnNoneForAjaxRequest();
	}

	public String shareWithUserEditPermission() {
		try {
			Report report = reportService.loadReportFromDatabase(reportId);
			User toUser = userService.loadUser(shareId);

			manageReportsService.shareReportWithUserOrGroup(getUser(), toUser, report, permissions, true);
		} catch (Exception e) {
			logger.error("There was an exception with report " + reportId + ". Cannot share.", e);
			return ERROR;
		}

		return redirectOrReturnNoneForAjaxRequest();
	}

	public String shareWithGroupViewPermission() {
		try {
			Report report = reportService.loadReportFromDatabase(reportId);
			User toGroup = userService.loadUser(shareId);

			manageReportsService.shareReportWithUserOrGroup(getUser(), toGroup, report, permissions, false);
		} catch (Exception e) {
			logger.error("There was an exception with report " + reportId + ". Cannot share.", e);
			return ERROR;
		}

		return redirectOrReturnNoneForAjaxRequest();
	}

	public String shareWithGroupEditPermission() {
		try {
			Report report = reportService.loadReportFromDatabase(reportId);
			User toGroup = userService.loadUser(shareId);

			manageReportsService.shareReportWithUserOrGroup(getUser(), toGroup, report, permissions, true);
		} catch (Exception e) {
			logger.error("There was an exception with report " + reportId + ". Cannot share.", e);
			return ERROR;
		}

		return redirectOrReturnNoneForAjaxRequest();
	}

	public String shareWithAccountViewPermission() {
		try {
			Report report = reportService.loadReportFromDatabase(reportId);
			Account toAccount = dao.find(Account.class, shareId);

			manageReportsService.shareReportWithAccount(getUser(), toAccount, report, permissions);
		} catch (Exception e) {
			logger.error("There was an exception with report " + reportId + ". Cannot share.", e);
			return ERROR;
		}

		return redirectOrReturnNoneForAjaxRequest();
	}

	public String unshareUser() {
		try {
			Report report = reportService.loadReportFromDatabase(reportId);
			User toUser = userService.loadUser(shareId);

			manageReportsService.unshareUser(getUser(), toUser, report, permissions);
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

		return redirectOrReturnNoneForAjaxRequest();
	}

	public String unshareGroup() {
		try {
			Report report = reportService.loadReportFromDatabase(reportId);
			User group = userService.loadUser(shareId);

			manageReportsService.unshareGroup(getUser(), group, report, permissions);
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

		return redirectOrReturnNoneForAjaxRequest();
	}

	public String unshareAccount() {
		try {
			Report report = reportService.loadReportFromDatabase(reportId);
			Account toAccount = dao.find(Account.class, shareId);

			manageReportsService.unshareAccount(getUser(), toAccount, report, permissions);
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

		return redirectOrReturnNoneForAjaxRequest();
	}

	// Questionable
	public String removeReport() {
		try {
			Report report = reportService.loadReportFromDatabase(reportId);
			manageReportsService.removeReportUser(getUser(), report, permissions);
		} catch (NoResultException nre) {
			logger.error(nre.toString());
		} catch (Exception e) {
			logger.error("Unexpected exception in ManageReports.removeReport(). ", e);
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
			logger.error("Unexpected exception in ManageReports.favorite(). ", e);
		}

		return redirectOrReturnNoneForAjaxRequest();
	}

	public String unfavorite() {
		try {
			ReportUser reportUser = reportPreferencesService.loadOrCreateReportUser(permissions.getUserId(), reportId);
			reportPreferencesService.unfavoriteReport(reportUser);
		} catch (NoResultException nre) {
			logger.error(nre.toString());
		} catch (Exception e) {
			logger.error("Unexpected exception in ManageReports.unfavorite(). ", e);
		}

		return redirectOrReturnNoneForAjaxRequest();
	}

	public String privatize() {
		try {
			User user = userService.loadUser(permissions.getUserId());
			Report report = reportService.loadReportFromDatabase(reportId);

			reportService.privatizeReport(user, report);
		} catch (NoResultException nre) {
			logger.error(nre.toString());
		} catch (Exception e) {
			logger.error("Unexpected exception in ManageReports.privatize(). ", e);
		}

		return redirectOrReturnNoneForAjaxRequest();
	}

	public String unprivatize() {
		try {
			User user = userService.loadUser(permissions.getUserId());
			Report report = reportService.loadReportFromDatabase(reportId);

			reportService.unprivatizeReport(user, report);
		} catch (NoResultException nre) {
			logger.error(nre.toString());
		} catch (Exception e) {
			logger.error("Unexpected exception in ManageReports.unprivatize(). ", e);
		}

		return redirectOrReturnNoneForAjaxRequest();
	}

	public String pinFavorite() {
		try {
			Report report = reportService.loadReportFromDatabase(reportId);

			reportPreferencesService.pinFavorite(getUser(), report, pinnedIndex);
		} catch (Exception e) {
			logger.error("Unexpected exception in ManageReports.pin(). ", e);
		}

		return redirectOrReturnNoneForAjaxRequest();
	}

	public String unpinFavorite() {
		try {
			Report report = reportService.loadReportFromDatabase(reportId);

			reportPreferencesService.unpinFavorite(getUser(), report);
		} catch (Exception e) {
			logger.error("Unexpected exception in ManageReports.unpin(). ", e);
		}

		return redirectOrReturnNoneForAjaxRequest();
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

	private void logAndShowUserInDebugMode(String errorMessage, Exception e) {
		logger.error(errorMessage, e);
		if (permissions.has(OpPerms.Debug)) {
			addActionError(e.getMessage());
		}
	}

	private ReportSearch buildReportSearch() {
		return new ReportSearch.Builder().permissions(permissions).sortType(sort)
				.sortDirection(direction).build();
	}

	public List<ReportFavoriteInfo> getReportFavoriteListOverflow() {
		return reportFavoriteListOverflow;
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

	public void setPinnedIndex(int pinnedIndex) {
		this.pinnedIndex = pinnedIndex;
	}

	public void setShareId(int shareId) {
		this.shareId = shareId;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public Pagination<ReportInfo> getPagination() {
		if (pagination == null) {
			pagination = new Pagination<ReportInfo>();
			pagination.setParameters(new PaginationParameters());
		}

		return pagination;
	}

	public void setPagination(Pagination<ReportInfo> pagination) {
		this.pagination = pagination;
	}

	// TODO remove this
	public List<ReportInfo> getReportList() {
		return reportList;
	}

	public List<ReportFavoriteInfo> getReportFavoriteList() {
		return reportFavoriteList;
	}

	public List<ReportPermissionInfo> getUserAccessList() {
		return userAccessList;
	}

	public List<ReportPermissionInfo> getGroupAccessList() {
		return groupAccessList;
	}

	public boolean isCurrentUserOwner() {
		return isCurrentUserOwner;
	}
}
