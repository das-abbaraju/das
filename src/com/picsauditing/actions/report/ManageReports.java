package com.picsauditing.actions.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.NoResultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.model.ReportDynamicModel;
import com.picsauditing.provider.ReportProvider;
import com.picsauditing.report.access.ReportUtil;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ManageReports extends PicsActionSupport {

	// TODO make this an enum or something
	private static final String FAVORITE = "favorite";
	private static final String MY_REPORTS = "saved";
	private static final String ALL_REPORTS = "all";

	public static final String MY_REPORTS_URL = "ManageReports!myReports.action";
	public static final String FAVORITE_REPORTS_URL = "ManageReports!favorites.action";

	@Autowired
	private ReportProvider reportProvider;

	private List<ReportUser> userReports = new ArrayList<ReportUser>();
	private String viewType;
	private int reportId;

	private static final Logger logger = LoggerFactory.getLogger(ManageReports.class);

	public String execute() {
		viewType = MY_REPORTS;
		runQueryForCurrentView();

		return "myReports";
	}

	public String favorites() {
		viewType = FAVORITE;
		runQueryForCurrentView();

		return "favorites";
	}

	public String myReports() {
		viewType = MY_REPORTS;
		runQueryForCurrentView();

		return "myReports";
	}

	public String search() {
		viewType = ALL_REPORTS;
		runQueryForCurrentView();

		return "search";
	}

	private void runQueryForCurrentView() {
		if (Strings.isEmpty(viewType))
			viewType = MY_REPORTS;

		try {
			int userId = permissions.getUserId();

			if (FAVORITE.equals(viewType)) {
				userReports = reportProvider.findFavoriteUserReports(userId);
			} else if (MY_REPORTS.equals(viewType)) {
				userReports = reportProvider.findAllUserReports(userId);
			} else if (ALL_REPORTS.equals(viewType)) {
				userReports = reportProvider.findAllUserReports(userId);

				List<Report> publicReports = reportProvider.findPublicReports();
				for (Report report : publicReports) {
					if (!ReportUtil.containsReportWithId(userReports, report.getId())) {
						userReports.add(new ReportUser(permissions.getUserId(), report));
					}
				}
			}
		} catch (Exception e) {
			addActionError(getText("ManageReports.error.problemFindingReports"));
			logger.error("Problem with runQueryForCurrentView() in ManageReports", e);

			if (userReports == null) {
				userReports = Collections.emptyList();
			}
		}
	}

	public String removeUserReport() throws Exception {
		try {
			reportProvider.removeUserReport(permissions.getUserId(), reportId);
			addActionMessage(getText("ManageReports.message.ReportRemoved"));
		} catch (NoResultException nre) {
			addActionMessage(getText("ManageReports.message.NoReportToRemove"));
			logger.warn(nre.toString());
		} catch (Exception e) {
			logger.error(e.toString());
		}

		return redirectToFavoriteReports();
	}

	public String deleteReport() throws IOException {
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

		return redirectToFavoriteReports();
	}

	public String toggleFavorite() {
		try {
			reportProvider.toggleReportUserFavorite(permissions.getUserId(), reportId);
		} catch (NoResultException nre) {
			addActionMessage(getText("ManageReports.message.FavoriteNotFound"));
			logger.warn(nre.toString());
		} catch (Exception e) {
			logger.error(e.toString());
		}

		return redirectToFavoriteReports();
	}

	private String redirectToFavoriteReports() {
		try {
			setUrlForRedirect(FAVORITE_REPORTS_URL);
		} catch (IOException ioe) {
			logger.error(ioe.toString());
		}

		return REDIRECT;
	}

	public String columnsToTranslate() throws Exception {
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
