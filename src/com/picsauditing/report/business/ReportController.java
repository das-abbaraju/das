package com.picsauditing.report.business;

import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.BasicDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.jpa.entities.User;

public class ReportController {

	@Autowired
	private BasicDAO basicDao;

	private static final String COPY = "copy";
	private static final String EDIT = "edit";
	private static final String DELETE = "delete";

	// was create() in ReportDynamic
	public Report copy(Report sourceReport, Permissions permissions) throws Exception {
		if (!userHasPermission(permissions.getUserId(), COPY, sourceReport))
			return null;

		Report newReport = copyReportWithoutPermissions(sourceReport);
		saveReport(newReport, permissions);
		connectReportToUser(sourceReport, new User(permissions.getUserId()), true);

		return sourceReport;
	}

	private Report copyReportWithoutPermissions(Report sourceReport) {
		Report newReport = new Report();
		newReport.setModelType(sourceReport.getModelType());
		newReport.setName(sourceReport.getName());
		newReport.setDescription(sourceReport.getDescription());
		newReport.setParameters(sourceReport.getParameters());
		newReport.setSharedWith(sourceReport.getSharedWith());

		return newReport;
	}

	private void connectReportToUser(Report report, User user) {
		connectReportToUser(report, user, false);
	}

	private void connectReportToUser(Report report, User user, boolean canEdit) {
		ReportUser userReport = new ReportUser();
		// TODO
		userReport.setAuditColumns(user);
		userReport.setReport(report);
		userReport.setUser(report.getCreatedBy());
		userReport.setCanEdit(canEdit);
		basicDao.save(userReport);
	}

	private boolean userHasPermission(int userId, String action, Report report) {
		// TODO remove bare SQL string
		List<ReportUser> reportUserList = basicDao.findWhere(ReportUser.class, "t.user.id = "
				+ userId + " AND t.report.id = " + report.getId());

		ReportUser reportUser = null;
		if (reportUserList.size() == 1) {
			reportUser = reportUserList.get(0);
		}

		if (action.equals(COPY) && canRead(reportUser))
			return true;
		if (action.equals(EDIT) && canEdit(reportUser))
			return true;
		if (action.equals(DELETE) && userOwnsReport(userId, report))
			return true;

		return false;
	}

	// This was ensureValidReport in ReportDynamic
	private void validate(Report report) throws Exception {
		if (report == null) {
			// TODO Add i18n to this
			throw new RuntimeException("Please provide a saved or ad hoc report to run");
		}

		if (report.getModelType() == null) {
			// TODO Add i18n to this
			throw new RuntimeException("The report is missing its base");
		}

		new JSONParser().parse(report.getParameters());
	}

	private void saveReport(Report report, Permissions permissions) throws Exception {
		validate(report);

		// TODO this should be like report.updateDatabaseInternalFields(User)
		report.setAuditColumns(permissions);
		basicDao.save(report);
	}

	private boolean userOwnsReport(int userId, Report report) {
		return userId == report.getCreatedBy().getId();
	}

	private boolean canRead(ReportUser reportUser) {
		return (reportUser != null);
	}

	private boolean canEdit(ReportUser reportUser) {
		return (reportUser != null && reportUser.isCanEdit());
	}

}
