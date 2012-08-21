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

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ReportDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.model.ReportModel;
import com.picsauditing.report.access.ReportUtil;
import com.picsauditing.strutsutil.AjaxUtils;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ManageReports extends PicsActionSupport {

	public static final String MY_REPORTS_URL = "ManageReports!myReportsList.action";
	public static final String FAVORITE_REPORTS_URL = "ManageReports!favoritesList.action";

	public static final String ALPHA_SORT = "alpha";
	public static final String DATE_ADDED_SORT = "dateAdded";
	public static final String LAST_OPENED_SORT = "lastOpened";

	public static final int MAX_REPORTS_IN_MENU = 10;

	@Autowired
	private ReportModel reportModel;
	@Autowired
	private ReportDAO reportDao;

	private List<ReportUser> userReports;
	private List<ReportUser> userReportsOverflow;

	// URL parameters
	private int reportId;
	private String searchTerm;
	private String sort;

	private HttpServletRequest requestForTesting;

	private static final Logger logger = LoggerFactory.getLogger(ManageReports.class);

	public String execute() {
		return myReportsList();
	}

	public String favoritesList() {
		try {
			userReports = reportDao.findFavoriteUserReports(permissions.getUserId());

			if (CollectionUtils.isEmpty(userReports)) {
				addActionMessage(getText("ManageReports.message.NoFavorites"));
				userReports = new ArrayList<ReportUser>();
			}

			if (userReports.size() > MAX_REPORTS_IN_MENU) {
				userReportsOverflow = userReports.subList(MAX_REPORTS_IN_MENU, userReports.size());
				userReports = userReports.subList(0, MAX_REPORTS_IN_MENU);
			}
		} catch (Exception e) {
			logger.error("Unexpected exception in ManageReports!favoritesList.action", e);
		}

		return "favorites";
	}

	public String myReportsList() {
		try {
			userReports = reportModel.getUserReportsForMyReports(sort, permissions.getUserId());
		} catch (IllegalArgumentException iae) {
			logger.warn("Illegal argument exception in ManageReports!myReportsList.action", iae);
		} catch (Exception e) {
			logger.error("Unexpected exception in ManageReports!myReportsList.action", e);
		}

		if (CollectionUtils.isEmpty(userReports)) {
			addActionMessage(getText("ManageReports.message.NoUserReports"));
			userReports = new ArrayList<ReportUser>();
		}

		return "myReports";
	}

	public String searchList() {
		try {
			userReports = reportModel.getUserReportsForSearch(searchTerm, permissions.getUserId());
		} catch (Exception e) {
			logger.error("Unexpected exception in ManageReports!searchList.action", e);
		}

		if (CollectionUtils.isEmpty(userReports)) {
			addActionMessage("No Reports found.");
			userReports = new ArrayList<ReportUser>();
		}

		if (AjaxUtils.isAjax(request())) {
			return "searchList";
		}

		return "search";
	}

	public String removeUserReport() {
		try {
			reportDao.removeUserReport(permissions.getUserId(), reportId);
			addActionMessage(getText("ManageReports.message.ReportRemoved"));
		} catch (NoResultException nre) {
			addActionMessage(getText("ManageReports.message.NoReportToRemove"));
			logger.error(nre.toString());
		} catch (Exception e) {
			logger.error("Uncaught exception in removeUserReport(). ", e);
		}

		return redirectToPreviousView();
	}

	public String deleteReport() {
		try {
			Report report = reportDao.findOneReport(reportId);
			if (ReportModel.canUserDelete(permissions.getUserId(), report)) {
				reportDao.deleteReport(report);
				addActionMessage(getText("ManageReports.message.ReportDeleted"));
			} else {
				addActionError(getText("ManageReports.error.NoDeletePermissions"));
			}
		} catch (NoResultException nre) {
			addActionError(getText("ManageReports.error.NoReportToDelete"));
			logger.error(nre.toString());
		} catch (Exception e) {
			logger.error("Uncaught exception in deleteReport(). ", e);
		}

		return redirectToPreviousView();
	}

	public String favorite() {
		try {
			reportModel.favoriteReport(permissions.getUserId(), reportId);
		} catch (NoResultException nre) {
			addActionMessage(getText("ManageReports.message.FavoriteNotFound"));
			logger.error(nre.toString());
		} catch (Exception e) {
			logger.error("Uncaught exception in ManageReports.favorite(). ", e);
		}

		return redirectToPreviousView();
	}

	public String unfavorite() {
		try {
			reportModel.unfavoriteReport(permissions.getUserId(), reportId);
		} catch (NoResultException nre) {
			addActionMessage(getText("ManageReports.message.FavoriteNotFound"));
			logger.error(nre.toString());
		} catch (Exception e) {
			logger.error("Uncaught exception in ManageReports.favorite(). ", e);
		}

		return redirectToPreviousView();
	}

	public String moveUp() {
		try {
			reportModel.moveUserReportUpOne(permissions.getUserId(), reportId);
		} catch (NoResultException nre) {
			// Don't do anything
			logger.warn("No result found in ManageReports.moveUp()", nre);
		} catch (Exception e) {
			logger.error("Unexpected exception in ManageReports.moveUp(). ", e);
		}

		return redirectToPreviousView();
	}

	public String moveDown() {
		try {
			reportModel.moveUserReportDownOne(permissions.getUserId(), reportId);
		} catch (NoResultException nre) {
			// Don't do anything
			logger.warn("No result found in ManageReports.moveDown()", nre);
		} catch (Exception e) {
			logger.error("Unexpected exception in ManageReports.moveDown(). ", e);
		}

		return redirectToPreviousView();
	}

	public String columnsToTranslate() {
		try {
			List<Report> allReports = reportDao.findAllReports();
			// TODO: Get a button/link for debug only
			ReportUtil.findColumnsToTranslate(allReports);
		} catch (IOException ioe) {
			logger.warn("There was a problem finding columns to translate.", ioe);
		} catch (Exception e) {
			logger.error("Unexpected exeption finding columns to translate.", e);
		}

		return SUCCESS;
	}

	private String redirectToPreviousView() {
		try {
			String referer = getRequest().getHeader("Referer");
			if (Strings.isEmpty(referer)) {
				// TODO make this a full URL and add a test for it
				referer = MY_REPORTS_URL;
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
		if (requestForTesting != null)
			return requestForTesting;

		return getRequest();
	}

	public List<ReportUser> getUserReports() {
		return userReports;
	}

	public void setUserReports(List<ReportUser> userReports) {
		this.userReports = userReports;
	}

	public List<ReportUser> getUserReportsOverflow() {
		return userReportsOverflow;
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

	public String getAlphaSort() {
		return ALPHA_SORT;
	}

	public String getDateAddedSort() {
		return DATE_ADDED_SORT;
	}

	public String getLastOpenedSort() {
		return LAST_OPENED_SORT;
	}

	public String getMyReportsUrl() {
		return MY_REPORTS_URL;
	}
}
