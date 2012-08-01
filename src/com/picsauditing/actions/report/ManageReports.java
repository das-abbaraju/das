package com.picsauditing.actions.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.model.ReportDynamicModel;
import com.picsauditing.provider.ReportProvider;
import com.picsauditing.report.access.ReportUtil;

@SuppressWarnings("serial")
public class ManageReports extends PicsActionSupport {

	private static final String FAVORITE_REPORTS = "favorites";
	private static final String MY_REPORTS = "myReports";
	private static final String ALL_REPORTS = "search";

	public static final String MY_REPORTS_URL = "ManageReports!myReports.action";
	public static final String FAVORITE_REPORTS_URL = "ManageReports!favorites.action";

	@Autowired
	private ReportProvider reportProvider;

	private List<ReportUser> userReports = new ArrayList<ReportUser>();
	// TODO remove viewType after making the toggleFavorite an ajax call
	private String viewType;
	private int reportId;

	private static final Logger logger = LoggerFactory.getLogger(ManageReports.class);

	public String execute() {
		return myReports();
	}

	public String favorites() {
		viewType = FAVORITE_REPORTS;

		try {
			userReports = reportProvider.findFavoriteUserReports(permissions.getUserId());
		} catch (Exception e) {
			logger.error("Unexpected exception in ManageReports!favorites.action", e);
		}

		if (CollectionUtils.isEmpty(userReports)) {
			addActionMessage(getText("ManageReports.message.NoFavorites"));
			userReports = new ArrayList<ReportUser>();
		}

		return FAVORITE_REPORTS;
	}

	public String myReports() {
		viewType = MY_REPORTS;

		try {
			userReports = reportProvider.findAllUserReports(permissions.getUserId());
		} catch (Exception e) {
			logger.error("Unexpected exception in ManageReports!myReports.action", e);
		}

		if (CollectionUtils.isEmpty(userReports)) {
			addActionMessage(getText("ManageReports.message.NoUserReports"));
			userReports = new ArrayList<ReportUser>();
		}

		return MY_REPORTS;
	}

	public String search() {
		viewType = ALL_REPORTS;

		try {
			int userId = permissions.getUserId();

			userReports = reportProvider.findAllUserReports(userId);

			List<Report> publicReports = reportProvider.findPublicReports();
			for (Report report : publicReports) {
				if (ReportUtil.containsReportWithId(userReports, report.getId()))
					continue;

				userReports.add(new ReportUser(userId, report));
			}
		} catch (Exception e) {
			logger.error("Unexpected exception in ManageReports!search.action", e);
		}

		if (CollectionUtils.isEmpty(userReports)) {
			logger.error("There are no reports in the system. This should never happen.");
			userReports = new ArrayList<ReportUser>();
		}

		return ALL_REPORTS;
	}

	// TODO move to ReportDynamic.java
	public String removeUserReport() {
		try {
			reportProvider.removeUserReport(permissions.getUserId(), reportId);
			addActionMessage(getText("ManageReports.message.ReportRemoved"));
		} catch (NoResultException nre) {
			addActionMessage(getText("ManageReports.message.NoReportToRemove"));
			logger.warn(nre.toString());
		} catch (Exception e) {
			logger.error(e.toString());
		}

		return redirectToPreviousView();
	}

	// TODO move to ReportDynamic.java
	public String deleteReport() {
		try {
			Report report = reportProvider.findOneReport(reportId);
			if (ReportDynamicModel.canUserDelete(permissions.getUserId(), report)) {
				reportProvider.deleteReport(report);
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
			reportProvider.toggleReportUserFavorite(permissions.getUserId(), reportId);
		} catch (NoResultException nre) {
			addActionMessage(getText("ManageReports.message.FavoriteNotFound"));
			logger.warn(nre.toString());
		} catch (IOException ioe) {
			logger.warn(ioe.toString());
		} catch (Exception e) {
			logger.error(e.toString());
		}

		return redirectToPreviousView();
	}

	private String redirectToPreviousView() {
		try {
			if (FAVORITE_REPORTS.equals(viewType)) {
				setUrlForRedirect(FAVORITE_REPORTS_URL);
			} else {
				setUrlForRedirect(MY_REPORTS_URL);
			}
		} catch (IOException ioe) {
			logger.error(ioe.toString());
		}

		return REDIRECT;
	}

	public String columnsToTranslate() {
		try {
			List<Report> allReports = reportProvider.findAllReports();
			// TODO: Get a button/link for debug only
			ReportUtil.findColumnsToTranslate(allReports);
		} catch (IOException ioe) {
			logger.warn("There was a problem finding columns to translate.", ioe);
		} catch (Exception e) {
			logger.error("Unexpected exeption finding columns to translate.", e);
		}

		return SUCCESS;
	}

	public void setUserReports(List<ReportUser> userReports) {
		this.userReports = userReports;
	}

	public List<ReportUser> getUserReports() {
		return userReports;
	}

	public String getViewType() {
		return viewType;
	}

	public void setViewType(String viewType) {
		this.viewType = viewType;
	}

	public int getReportId() {
		return reportId;
	}

	public void setReportId(int reportId) {
		this.reportId = reportId;
	}
}
