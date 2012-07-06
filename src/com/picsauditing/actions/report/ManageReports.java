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
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ManageReports extends PicsActionSupport {

	private static final String SAVED = "saved";
	private static final String FAVORITE = "favorite";

	@Autowired
	private ReportAccessor reportAccessor;

	private List<ReportUser> userReports = new ArrayList<ReportUser>();

	private String viewType;
	private int reportId;

	private static final Logger logger = LoggerFactory.getLogger(ManageReports.class);

	public String execute() throws Exception {
		runQueryForCurrentView();
		return SUCCESS;
	}

	public String viewAllReports() throws Exception {
		viewType = SAVED;
		runQueryForCurrentView();
		return SUCCESS;
	}

	public String viewFavoriteReports() throws Exception {
		viewType = FAVORITE;
		runQueryForCurrentView();
		return SUCCESS;
	}

	public String getPageDescription() {
		String pageDescription = "";

		if (SAVED.equals(viewType)) {
			// TODO add i18n to this
			pageDescription = "Edit and manage all of your reports.";
		} else if (FAVORITE.equals(viewType)) {
			// TODO add i18n to this
			pageDescription = "These reports will show in your Reports menu dropdown.";
		}

		return pageDescription;
	}

	public boolean viewingAllReports() {
		return SAVED.equals(viewType);
	}

	public boolean viewingFavoriteReports() {
		return FAVORITE.equals(viewType);
	}

	private void runQueryForCurrentView() {
		if (Strings.isEmpty(viewType))
			viewType = SAVED;

		try {
			if (FAVORITE.equals(viewType)) {
				userReports = reportAccessor.findFavoriteUserReports(permissions.getUserId());

				if (CollectionUtils.isEmpty(userReports)) {
					// TODO add i18n to this
					addActionMessage("You have not favorited any reports.");
				}
			} else if (SAVED.equals(viewType)) {
				userReports = reportAccessor.findAllUserReports(permissions.getUserId());

				if (CollectionUtils.isEmpty(userReports)) {
					reportAccessor.giveUserDefaultReports(permissions);
					userReports = reportAccessor.findAllUserReports(permissions.getUserId());
				}
			}
		} catch (Exception e) {
			userReports = Collections.emptyList();
			// TODO add i18n to this
			addActionMessage("There was a problem finding your reports.");
		}
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

		return redirectToMyReports();
	}

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

		return redirectToMyReports();
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

		return redirectToMyReports();
	}

	private String redirectToMyReports() {
		try {
			setUrlForRedirect("ManageMyReports.action");
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
