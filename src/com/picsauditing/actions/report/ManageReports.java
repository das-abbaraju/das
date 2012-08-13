package com.picsauditing.actions.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;

import org.apache.commons.beanutils.BasicDynaBean;
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
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ManageReports extends PicsActionSupport {

	private static final String FAVORITE_REPORTS = "favorites";
	private static final String MY_REPORTS = "myReports";
	private static final String ALL_REPORTS = "search";

	public static final String MY_REPORTS_URL = "ManageReports!myReportsList.action";
	public static final String FAVORITE_REPORTS_URL = "ManageReports!favoritesList.action";

	@Autowired
	private ReportModel reportModel;
	@Autowired
	private ReportDAO reportDao;

	private List<ReportUser> userReports = new ArrayList<ReportUser>();
	private int reportId;
	private String searchTerm;

	private static final Logger logger = LoggerFactory.getLogger(ManageReports.class);

	public String execute() {
		return myReportsList();
	}

	public String favoritesList() {
		try {
			userReports = reportDao.findFavoriteUserReports(permissions.getUserId());
		} catch (Exception e) {
			logger.error("Unexpected exception in ManageReports!favoritesList.action", e);
		}

		if (CollectionUtils.isEmpty(userReports)) {
			addActionMessage(getText("ManageReports.message.NoFavorites"));
			userReports = new ArrayList<ReportUser>();
		}

		return FAVORITE_REPORTS;
	}

	public String myReportsList() {
		try {
			userReports = reportDao.findAllUserReports(permissions.getUserId());
		} catch (Exception e) {
			logger.error("Unexpected exception in ManageReports!myReportsList.action", e);
		}

		if (CollectionUtils.isEmpty(userReports)) {
			addActionMessage(getText("ManageReports.message.NoUserReports"));
			userReports = new ArrayList<ReportUser>();
		}

		return MY_REPORTS;
	}

	public String searchList() {
		try {
			List<BasicDynaBean> results = reportModel.getReportsForSearch(searchTerm, permissions.getUserId());
			userReports = reportModel.populateUserReports(results);
		} catch (Exception e) {
			logger.error("Unexpected exception in ManageReports!searchList.action", e);
		}

		if (CollectionUtils.isEmpty(userReports)) {
			addActionMessage("No Reports found.");
			userReports = new ArrayList<ReportUser>();
		}

		return ALL_REPORTS;
	}

	// TODO make this an ajax call
	public String removeUserReport() {
		try {
			reportDao.removeUserReport(permissions.getUserId(), reportId);
			addActionMessage(getText("ManageReports.message.ReportRemoved"));
		} catch (NoResultException nre) {
			addActionMessage(getText("ManageReports.message.NoReportToRemove"));
			logger.warn(nre.toString());
		} catch (Exception e) {
			logger.error(e.toString());
		}

		return redirectToPreviousView();
	}

	// TODO make this an ajax call
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
			logger.warn(nre.toString());
		} catch (Exception e) {
			logger.error(e.toString());
		}

		return redirectToPreviousView();
	}

	public String toggleFavorite() {
		try {
			reportDao.toggleReportUserFavorite(permissions.getUserId(), reportId);
		} catch (NoResultException nre) {
			addActionMessage(getText("ManageReports.message.FavoriteNotFound"));
			logger.warn(nre.toString());
		} catch (Exception e) {
			logger.error(e.toString());
		}

		return redirectToPreviousView();
	}

	private String redirectToPreviousView() {
		try {
			String referer = getRequest().getHeader("Referer");
			if (Strings.isEmpty(referer)) {
				referer = MY_REPORTS_URL;
			}

			setUrlForRedirect(referer);
		} catch (IOException e) {
			logger.warn("Problem setting URL for redirect in ManageReports.redirectToPreviousView()", e);
		} catch (Exception e) {
			logger.error("Unexpected problem in 9ManageReports.redirectToPreviousView()");
		}

		return REDIRECT;
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

	public List<ReportUser> getUserReports() {
		return userReports;
	}

	public void setUserReports(List<ReportUser> userReports) {
		this.userReports = userReports;
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
}
