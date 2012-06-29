// TODO: COLLAPSE THIS FILE INTO DYNAMIC REPORT ACTION CONTROLLER
// DO IT

package com.picsauditing.actions.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.report.access.ReportAccess;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ManageReports extends PicsActionSupport {

	private static final String SAVED = "saved";
	private static final String FAVORITE = "favorite";

	private List<ReportUser> userReports = new ArrayList<ReportUser>();

	private String viewType;
	private int reportId;

	private static final Logger logger = LoggerFactory.getLogger(ManageReports.class);

	public String execute() throws Exception {
		loadPermissions();
		runQueryForCurrentView();

		try {
			Map<String, Object> session = ActionContext.getContext().getSession();
			String errorMessage = (String) session.get("errorMessage");
			if (!Strings.isEmpty(errorMessage)) {
				addActionError(errorMessage);
				session.put("errorMessage", "");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return SUCCESS;
	}

	private void runQueryForCurrentView() {
		if (Strings.isEmpty(viewType))
			viewType = SAVED;

		String filterQuery = "userID = " + permissions.getUserId();

		if (FAVORITE.equals(viewType)) {
			filterQuery += " AND is_favorite = 1";
		} else if (SAVED.equals(viewType)) {
		}

		try {
			userReports = dao.findWhere(ReportUser.class, filterQuery);
		} catch (Exception e) {
			userReports = null;
			addActionMessage("There was a problem finding your reports.");
		}

		if (userReports == null) {
			userReports = Collections.emptyList();
			return;
		}

		if (userHasNoFavoriteReports()) {
			addActionMessage("You have not favorited any reports.");
		}

		if (userHasNoSavedReports()) {
			giveUserDefaultReports();
		}
	}

	public String removeReportUserAssociation() throws Exception {
		String query = "t.report.id = " + reportId + " AND t.user.id = " + permissions.getUserId();

		try {
			ReportUser reportUser = dao.findOne(ReportUser.class, query);

			dao.remove(reportUser);
		} catch (NoResultException nre) {
			addActionMessage("The report you're trying to remove no longer exists.");
		} catch (Exception e) {
			// An empty catch block is bad, but displaying an exception to the user is worse
		}

		runQueryForCurrentView();

		return SUCCESS;
	}

	public String deleteReport() throws IOException  {
		try {
			Report report = ReportAccess.findReportById(reportId);
			if (ReportAccess.canUserDelete(permissions.getUserId(), report)) {
				ReportAccess.deleteReport(report);
				addActionMessage("Your report has been deleted.");
			} else {
				addActionError("You do not have the necessary permissions to delete this report.");
			}
		} catch (NoResultException nre) {
			addActionError("The report you're trying to delete no longer exists.");
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		setUrlForRedirect("ManageReports.action");

		return REDIRECT;
	}

	public String changeReportName() {
		try {
//			Report report = dao.find(Report.class, reportId);
//			report.setName(report.getName());
//			report.setDescription(report.getDescription());
//			dao.save(report);
		} catch (Exception e) {
			// An empty catch block is bad, but displaying an exception to the user is worse
		}

		runQueryForCurrentView();

		return SUCCESS;
	}

	public String toggleFavorite() {
		String query = "t.user.id = " + permissions.getUserId() + " AND t.report.id = " + reportId;

		try {
			ReportUser reportUser = dao.findOne(ReportUser.class, query);
			reportUser.setFavorite(!reportUser.isFavorite());

			dao.save(reportUser);
		} catch (NoResultException nre) {
			addActionMessage("The report you're trying to favorite could not be found.");
		}

		runQueryForCurrentView();

		return SUCCESS;
	}

	private void giveUserDefaultReports() {
		// If a user logs in for the first time, they get the default set
		// If the user deletes their last report, they get the default set
		// TODO replace this hack with a customize recommendation default report set
		try {
			Report report11 = dao.findOne(Report.class, "id = 11");
			ReportUser reportUser11 = new ReportUser(permissions.getUserId(), report11);
			reportUser11.setAuditColumns(permissions);
			dao.save(reportUser11);

			Report report12 = dao.findOne(Report.class, "id = 12");
			ReportUser reportUser12 = new ReportUser(permissions.getUserId(), report12);
			reportUser12.setAuditColumns(permissions);
			dao.save(reportUser12);
		} catch (Exception e) {
			e.printStackTrace();
		}

		runQueryForCurrentView();
	}

	private boolean userHasNoSavedReports() {
		return SAVED.equals(viewType) && userReports.isEmpty();
	}

	private boolean userHasNoFavoriteReports() {
		return FAVORITE.equals(viewType) && userReports.isEmpty();
	}

	public String copyReport() {
		// TODO just call ReportController.copy() or similar

		runQueryForCurrentView();

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
