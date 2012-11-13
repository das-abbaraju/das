package com.picsauditing.actions.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
import com.picsauditing.jpa.entities.ReportPermissionUser;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.model.report.ReportModel;
import com.picsauditing.report.access.ReportUtil;
import com.picsauditing.strutsutil.AjaxUtils;
import com.picsauditing.util.Strings;
import com.picsauditing.util.pagination.Pagination;
import com.picsauditing.util.pagination.PaginationParameters;

@SuppressWarnings("serial")
public class ManageReports extends PicsActionSupport {

	public static final String LANDING_URL = "ManageReports!favoritesList.action";

	public static final String ALPHA_SORT = "alpha";
	public static final String DATE_ADDED_SORT = "dateAdded";
	public static final String LAST_VIEWED_SORT = "lastViewed";
	public static final String ASC = "ASC";
	public static final String DESC = "DESC";

	public static final int MAX_REPORTS_IN_MENU = 10;

	@Autowired
	private ReportModel reportModel;
	@Autowired
	private ReportDAO reportDao;
	@Autowired
	private ReportUserDAO reportUserDao;
	

	private List<ReportUser> reportUserFavorites;
	private List<ReportUser> reportUsersFavoritesOverflow;
	private List<ReportPermissionUser> reportPermissionUsers;
	private List<Report> reports;
	
	private Pagination<Report> pagination;

	// URL parameters
	private int reportId;
	private String searchTerm;
	private String sort;
	private String direction;

	private HttpServletRequest requestForTesting;

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
			reportUserFavorites = reportUserDao.findAllFavorite(permissions.getUserId());

			if (CollectionUtils.isEmpty(reportUserFavorites)) {
				reportUserFavorites = Collections.emptyList();
			}

			if (reportUserFavorites.size() > MAX_REPORTS_IN_MENU) {
				reportUsersFavoritesOverflow = reportUserFavorites.subList(MAX_REPORTS_IN_MENU, reportUserFavorites.size());
				reportUserFavorites = reportUserFavorites.subList(0, MAX_REPORTS_IN_MENU);
			}
		} catch (Exception e) {
			logger.error("Unexpected exception in ManageReports!favoritesList.action", e);
		}

		if (AjaxUtils.isAjax(request())) {
			return "favoritesList";
		}

		return "favorites";
	}

	public String myReportsList() {
		try {
			reportPermissionUsers = reportModel.getReportPermissionUsersForMyReports(sort, direction, permissions);
		} catch (IllegalArgumentException iae) {
			logger.warn("Illegal argument exception in ManageReports!myReportsList.action", iae);
		} catch (Exception e) {
			logger.error("Unexpected exception in ManageReports!myReportsList.action", e);
		}

		if (CollectionUtils.isEmpty(reportPermissionUsers)) {
			reportPermissionUsers = Collections.emptyList();
		}

		if (AjaxUtils.isAjax(request())) {
			return "myReportsList";
		}

		return "myReports";
	}

	public String searchList() {
		reports = new ArrayList<Report>();
		try {
			reports = reportModel.getReportsForSearch(searchTerm, permissions, getPagination());
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
			reportModel.favoriteReport(permissions.getUserId(), reportId);
		} catch (NoResultException nre) {
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
			logger.error(nre.toString());
		} catch (Exception e) {
			logger.error("Uncaught exception in ManageReports.unfavorite(). ", e);
		}

		return redirectToPreviousView();
	}

	public String moveUp() {
		int positionChange = -1;
		try {
			reportModel.moveReportUser(permissions.getUserId(), reportId, positionChange);
		} catch (NoResultException nre) {
			logger.warn("No result found in ManageReports.moveUp()", nre);
		} catch (Exception e) {
			logger.error("Unexpected exception in ManageReports.moveUp(). ", e);
		}

		return redirectToPreviousView();
	}

	public String moveDown() {
		int positionChange = 1;
		try {
			reportModel.moveReportUser(permissions.getUserId(), reportId, positionChange);
		} catch (NoResultException nre) {
			logger.warn("No result found in ManageReports.moveDown()", nre);
		} catch (Exception e) {
			logger.error("Unexpected exception in ManageReports.moveDown(). ", e);
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
		if (requestForTesting != null)
			return requestForTesting;

		return getRequest();
	}

	public List<ReportUser> getReportUserFavorites() {
		return reportUserFavorites;
	}

	public void setReportUserFavorites(List<ReportUser> reportUserFavorites) {
		this.reportUserFavorites = reportUserFavorites;
	}

	public List<ReportUser> getReportUsersFavoritesOverflow() {
		return reportUsersFavoritesOverflow;
	}

	public List<ReportPermissionUser> getReportPermissionUsers() {
		return reportPermissionUsers;
	}

	public void setReportPermissionUsers(List<ReportPermissionUser> reportPermissionUsers) {
		this.reportPermissionUsers = reportPermissionUsers;
	}

	public List<Report> getReports() {
		return reports;
	}

	public void setReports(List<Report> reports) {
		this.reports = reports;
	}

	public ReportModel getReportModel() {
		return reportModel;
	}

	public void setReportModel(ReportModel reportModel) {
		this.reportModel = reportModel;
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
		if (!ALPHA_SORT.equals(sort) || DESC.equals(direction))
			return ASC;

		return DESC;
	}

	public String getDateAddedSortDirection() {
		if (!DATE_ADDED_SORT.equals(sort) || ASC.equals(direction))
			return DESC;

		return ASC;
	}

	public String getLastViewedSortDirection() {
		if (!LAST_VIEWED_SORT.equals(sort) || ASC.equals(direction))
			return DESC;

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
