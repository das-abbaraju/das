package com.picsauditing.actions.report;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.util.business.DynamicReportUtil;

@SuppressWarnings("serial")
public class ManageReports extends PicsActionSupport {

	private static final String DELETE_REPORT = "delete";
	private static final String REMOVE_ASSOCIATION = "remove";
	private static final String SAVED = "saved";
	private static final String FAVORITE2 = "favorite";
	private static final String TEMPLATE = "template";

	private ReportUser reportUser;
	private Report report;
	private List<ReportUser> reportsByUser = new ArrayList<ReportUser>();

	private String filterType;
	private int reportId;
	private boolean favorite;
	private String deleteType;

	public String execute() throws Exception {
		loadPermissions();
		getCustomReport(filterType);

		return SUCCESS;
	}

	private void getCustomReport(String filterType) {
		String filterQuery = "createdBy=" + permissions.getUserId();

		// TODO: do we even need to have a template filter?
		if (TEMPLATE.equals(filterType)) {
			setReportsByUser(dao.findWhere(ReportUser.class, filterQuery, 2));
		} else if (FAVORITE2.equals(filterType)) {
			setReportsByUser(dao.findWhere(ReportUser.class,"favorite=1 and " + filterQuery));
		} else if (SAVED.equals(filterType)) {
			setReportsByUser(dao.findWhere(ReportUser.class, filterQuery));
		} else {
			setReportsByUser(dao.findWhere(ReportUser.class, filterQuery));
		}
	}

	public String deleteReport() throws Exception {
		if (DELETE_REPORT.equalsIgnoreCase(deleteType)) {
			String query = "t.report.id = " + reportId;
			Report report = dao.findOne(Report.class, query);

			if (DynamicReportUtil.userCanDelete(permissions.getUserId(), report)) {
				dao.remove(report);

				query += " AND t.user.id = " + permissions.getUserId();
				ReportUser reportUser = dao.findOne(ReportUser.class, query);
				dao.remove(reportUser);				

				getCustomReport(filterType);
				return SUCCESS;
			} else {
				addActionMessage("You do not have the necessary permissions to delete this report.");
				return SUCCESS;
			}
		}

		if (REMOVE_ASSOCIATION.equals(deleteType)) {
			String query = "t.report.id = " + reportId + " AND t.user.id = " + permissions.getUserId();
			ReportUser reportUser = dao.findOne(ReportUser.class, query);
			dao.remove(reportUser);
		}

		getCustomReport(filterType);

		return SUCCESS;
	}

	public String changeReportName() throws Exception {
		Report reportToUpdate = dao.find(Report.class, reportId);
		reportToUpdate.setName(report.getName());
		reportToUpdate.setDescription(report.getDescription());
		dao.save(reportToUpdate);

		getCustomReport(filterType);

		return SUCCESS;
	}

	public String changeFavorite() throws Exception {
		String userReportQuery = "t.user.id = " + permissions.getUserId() + " AND t.report.id = " + reportId;
		reportUser = dao.findOne(ReportUser.class, userReportQuery);
		reportUser.setFavorite(favorite);
		dao.save(reportUser);
		getCustomReport(filterType);

		return SUCCESS;
	}

	public String createReport() {
		ReportDynamic dr = new ReportDynamic();
		dr.setReport((Report)reportUser.getReport());
		dr.create();

		getCustomReport(filterType);

		return SUCCESS;
	}

	public ReportUser getReportUser() {
		return reportUser;
	}

	public void setReportUser(ReportUser reportUser) {
		this.reportUser = reportUser;
	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	public void setReportsByUser(List<ReportUser> reportsByUser) {
		this.reportsByUser = reportsByUser;
	}

	public List<ReportUser> getReportsByUser() {
		return reportsByUser;
	}

	public String getFilterType() {
		return filterType;
	}

	public void setFilterType(String reportType) {
		this.filterType = reportType;
	}

	public int getReportId() {
		return reportId;
	}

	public void setReportId(int reportId) {
		this.reportId = reportId;
	}

	public boolean isFavorite() {
		return favorite;
	}

	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}

	public String getDeleteType() {
		return deleteType;
	}

	public void setDeleteType(String deleteType) {
		this.deleteType = deleteType;
	}
}
