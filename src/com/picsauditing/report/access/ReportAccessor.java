package com.picsauditing.report.access;

import java.util.List;

import javax.persistence.NoResultException;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.ReportValidationException;
import com.picsauditing.dao.BasicDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.jpa.entities.User;

/**
 * This is the persistence layer. It is the only class that should contain a DAO.
 * It should also contain no business logic.
 */
public class ReportAccessor {

	@Autowired
	private BasicDAO basicDao;

	public Report findReportById(int id) throws NoResultException {
		return basicDao.findOne(Report.class, "t.id = " + id);
	}

	public void refresh(Report report) {
		basicDao.refresh(report);
	}

	public List<ReportUser> queryReportUser(int userId, int reportId) throws NoResultException {
		String query = "t.user.id = " + userId + " AND t.report.id = " + reportId;
		return basicDao.findWhere(ReportUser.class, query);
	}

	public void connectReportToUser(Report report, User user) {
		ReportUser entry = new ReportUser();

		entry.setAuditColumns(user);
		entry.setReport(report);
		// TODO shouldn't this just be user?
		entry.setUser(report.getCreatedBy());
		entry.setEditable(false);

		basicDao.save(entry);
	}

	public void grantEditPermission(Report report, User user) {
		setUserEditPermissions(report, user, true);
	}

	public void revokeEditPermission(Report report, User user) {
		setUserEditPermissions(report, user, false);
	}

	private void setUserEditPermissions(Report report, User user, boolean value) {
		List<ReportUser> reportUserList = queryReportUser(user.getId(), report.getId());

		if (CollectionUtils.isEmpty(reportUserList)) {
			// TODO log this
			return;
		} else if (reportUserList.size() > 1) {
			// TODO log this
			return;
		}

		ReportUser entry = reportUserList.get(0);
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
}
