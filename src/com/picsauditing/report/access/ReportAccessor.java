package com.picsauditing.report.access;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.persistence.NoResultException;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.ReportValidationException;
import com.picsauditing.dao.BasicDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.jpa.entities.User;

public class ReportAccessor implements ReportAdministration {

	@Autowired
	private BasicDAO basicDao;

	private static final List<Integer> baseReports =
			Collections.unmodifiableList(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12));

	public boolean canUserViewAndCopy(int userId, Report report) {
		if (report == null)
			return false;

		return canUserViewAndCopy(userId, report.getId());
	}

	public boolean canUserViewAndCopy(int userId, int reportId) {
		if (baseReports.contains(reportId))
			return true;

		String query = "t.user.id = " + userId + " AND t.report.id = " + reportId;
		List<ReportUser> reportUserList = basicDao.findWhere(ReportUser.class, query);

		if (!CollectionUtils.isEmpty(reportUserList))
			return true;

		return false;
	}

	public boolean canUserEdit(int userId, Report report) {
		String query = "t.user.id = " + userId + " AND t.report.id = " + report.getId();
		ReportUser reportUser = basicDao.findOne(ReportUser.class, query);

		if (reportUser == null)
			return false;

		if (reportUser.canEditReport())
			return true;

		return false;
	}

	public boolean canUserDelete(int userId, Report report) {
		if (report.getCreatedBy().getId() == userId)
			return true;

		return false;
	}

	public void connectReportToUser(Report report, User user) {
		ReportUser entry = new ReportUser();

		entry.setAuditColumns(user);
		entry.setReport(report);
		entry.setUser(report.getCreatedBy());
		entry.setEditable(false);

		basicDao.save(entry);
	}

	public void grantPermissionToEdit(Report report, User user) {
		setReportEditPermissions(report, user, true);
	}

	public void revokePermissionToEdit(Report report, User user) {
		setReportEditPermissions(report, user, false);
	}

	private void setReportEditPermissions(Report report, User user, boolean value) {
		String query = "t.user.id = " + user.getId() + " AND t.report.id = " + report.getId();
		List<ReportUser> entries = basicDao.findWhere(ReportUser.class, query);

		if (CollectionUtils.isEmpty(entries) || entries.size() > 1)
			return;

		ReportUser entry = entries.get(0);
		entry.setEditable(value);

		basicDao.save(entry);
	}

	public void saveReport(Report report, User user) throws ReportValidationException {
		DynamicReportUtil.validate(report);
		report.setAuditColumns(user);

		basicDao.save(report);
	}

	public void deleteReport(Report report) throws NoResultException {
		List<ReportUser> reportUsers = basicDao.findWhere(ReportUser.class, "t.report.id = " + report.getId());
		for (ReportUser reportUser : reportUsers) {
			basicDao.remove(reportUser);
		}

		basicDao.remove(report);
	}

	public Report findReportById(int id) throws NoResultException {
		return basicDao.findOne(Report.class, "t.id = " + id);
	}
}
