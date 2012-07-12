package com.picsauditing.actions.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
import com.picsauditing.report.access.ReportAccessor;
import com.picsauditing.report.access.ReportUtil;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ManageReports extends PicsActionSupport {

	// TODO make this an enum or something
	private static final String FAVORITE = "favorite";
	private static final String MY_REPORTS = "saved";
	private static final String ALL_REPORTS = "all";

	public static final String MY_REPORTS_URL = "ManageReports!viewMyReports.action";
	public static final String FAVORITE_REPORTS_URL = "ManageReports!viewFavoriteReports.action";

	@Autowired
	private ReportAccessor reportAccessor;

	private List<ReportUser> userReports = new ArrayList<ReportUser>();
	private String viewType;
	private int reportId;

	private static final Logger logger = LoggerFactory.getLogger(ManageReports.class);

	public String execute() {
		runQueryForCurrentView();
		return SUCCESS;
	}

	public String viewFavoriteReports() {
		viewType = FAVORITE;
		runQueryForCurrentView();
		return SUCCESS;
	}

	public String viewMyReports() {
		viewType = MY_REPORTS;
		runQueryForCurrentView();
		return SUCCESS;
	}

	public String viewAllReports() {
		viewType = ALL_REPORTS;
		runQueryForCurrentView();
		return SUCCESS;
	}

	public boolean viewingFavoriteReports() {
		return FAVORITE.equals(viewType);
	}

	public boolean viewingMyReports() {
		return MY_REPORTS.equals(viewType);
	}

	public boolean viewingAllReports() {
		return ALL_REPORTS.equals(viewType);
	}

	private void runQueryForCurrentView() {
		if (Strings.isEmpty(viewType))
			viewType = MY_REPORTS;

		try {
			int userId = permissions.getUserId();

			if (FAVORITE.equals(viewType)) {
				userReports = reportAccessor.findFavoriteUserReports(userId);
			} else if (MY_REPORTS.equals(viewType)) {
				userReports = reportAccessor.findAllUserReports(userId);
			} else if (ALL_REPORTS.equals(viewType)) {
				userReports = reportAccessor.findAllUserReports(userId);

				// TODO find solution for global reports
//				List<ReportUser> globalUserReports = reportAccessor.findGlobalUserReports(userId);
//				for (ReportUser userReport : globalUserReports) {
//					if (!ReportUtil.containsReportWithId(userReports, userReport.getId())) {
//						userReports.add(userReport);
//					}
//				}
			}
		} catch (Exception e) {
			// TODO add i18n to this
			addActionError("There was a problem finding your reports.");
			logger.error("Problem with runQueryForCurrentView() in ManageReports", e);

			if (userReports == null) {
				userReports = Collections.emptyList();
			}
		}
	}

	public String getPageDescription() {
		String pageDescription = "";

		if (FAVORITE.equals(viewType)) {
			// TODO add i18n to this
			pageDescription = "These reports will show in your Reports menu.";
		} else if (MY_REPORTS.equals(viewType)) {
			// TODO add i18n to this
			pageDescription = "All of your reports that you've saved, created, and that have been shared with you.";
		} else if (ALL_REPORTS.equals(viewType)) {
			// TODO add i18n to this
			pageDescription = "Search all your reports and find new ones based on your specific needs and what's popular.";
		}

		return pageDescription;
	}

	public String removeUserReport() throws Exception {
		try {
			reportAccessor.removeUserReport(permissions.getUserId(), reportId);
			// TODO add i18n to this
			addActionMessage("Your report has been removed.");
		} catch (NoResultException nre) {
			// TODO add i18n to this
			addActionMessage("The report you're trying to remove no longer exists.");
			logger.warn(nre.toString());
		} catch (Exception e) {
			logger.error(e.toString());
		}

		return redirectToFavoriteReports();
	}

	// TODO move to ReportAccessor
	public String deleteReport() throws IOException  {
		try {
			Report report = reportAccessor.findOneReport(reportId);
			if (ReportDynamicModel.canUserDelete(permissions.getUserId(), report)) {
				reportAccessor.deleteReport(report);
				// TODO add i18n to this
				addActionMessage("Your report has been deleted.");
			} else {
				// TODO add i18n to this
				addActionError("You do not have the necessary permissions to delete this report.");
			}
		} catch (NoResultException nre) {
			// TODO add i18n to this
			addActionError("The report you're trying to delete no longer exists.");
			logger.warn(nre.toString());
		} catch (Exception e) {
			logger.error(e.toString());
		}

		return redirectToFavoriteReports();
	}

	public String toggleFavorite() {
		try {
			reportAccessor.toggleReportUserFavorite(permissions.getUserId(), reportId);
		} catch (NoResultException nre) {
			// TODO add i18n to this
			addActionMessage("The report you're trying to favorite could not be found.");
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
