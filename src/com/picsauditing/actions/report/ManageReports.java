package com.picsauditing.actions.report;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.util.business.DynamicReportUtil;

@SuppressWarnings("serial")
public class ManageReports extends PicsActionSupport {

	private static final String SAVED = "saved";
	private static final String FAVORITE = "favorite";

	private List<ReportUser> userReports = new ArrayList<ReportUser>();

	private String viewType;
	private int reportId;

	public String execute() throws Exception {
		loadPermissions();
		runQueryForCurrentView();

		return SUCCESS;
	}

	private void runQueryForCurrentView() {
		String filterQuery = "userID = " + permissions.getUserId();

		if (FAVORITE.equals(viewType)) {
			filterQuery += " AND is_favorite = 1";
		} else if (SAVED.equals(viewType)) {
		}

		setUserReports(dao.findWhere(ReportUser.class, filterQuery));
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

	public String deleteReport()  {
		String query = "t.id = " + reportId;

		try {
			Report report = dao.findOne(Report.class, query);

			if (!DynamicReportUtil.canUserDelete(permissions.getUserId(), report)) {
				addActionMessage("You do not have the necessary permissions to delete this report.");
				// TODO this should not return success
				return SUCCESS;
			}

			// Deleting the report from the report table cascades to the report_user table,
			// so we don't have to manually delete the entry from the report_user table.
			dao.remove(report);
		} catch (NoResultException nre) {
			addActionMessage("The report you're trying to delete no longer exists.");
		} catch (Exception e) {
			// An empty catch block is bad, but displaying an exception to the user is worse
		}

		runQueryForCurrentView();

		return SUCCESS;
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
		// TODO give the user reports 11 and 12
		// If a user logs in for the first time, they get the default set
		// If the user deletes their last report, they get the default set
	}

	public String copyReport() {
		// TODO just call ReportController.copy() or similar

		runQueryForCurrentView();

		return SUCCESS;
	}

	public void setUserReports(List<ReportUser> userReports) {
		if (userReports.isEmpty()) {
			giveUserDefaultReports();
		} else {
			this.userReports = userReports;
		}
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
