package com.picsauditing.report.access;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Permissions;
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

	private static final Logger logger = LoggerFactory.getLogger(ReportAccessor.class);

	public Report findOneReport(int id) throws NoResultException {
		return basicDao.findOne(Report.class, "t.id = " + id);
	}

	public ReportUser findOneUserReport(int userId, int reportId) throws NoResultException {
		String query = "t.user.id = " + userId + " AND t.report.id = " + reportId;
		List<ReportUser> result = basicDao.findWhere(ReportUser.class, query);

		if (CollectionUtils.isEmpty(result))
			throw new NoResultException();

		if (result.size() > 1)
			throw new NonUniqueResultException();

		return result.get(0);
	}

	public List<ReportUser> findAllUserReports(int userId) {
		String query = "t.user.id = " + userId;
		return basicDao.findWhere(ReportUser.class, query);
	}

	public List<ReportUser> findFavoriteUserReports(int userId) {
		String query = "t.user.id = " + userId + " AND is_favorite = 1";
		return basicDao.findWhere(ReportUser.class, query);
	}

	public void refresh(Report report) {
		basicDao.refresh(report);
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
		ReportUser entry = findOneUserReport(user.getId(), report.getId());
		entry.setEditable(value);

		basicDao.save(entry);
	}

	public void saveReport(Report report, User user) throws ReportValidationException {
		ReportUtil.validate(report);
		report.setAuditColumns(user);

		basicDao.save(report);
	}

	public void deleteReport(Report report) {
		List<ReportUser> reportUsers = basicDao.findWhere(ReportUser.class, "t.report.id = " + report.getId());
		for (ReportUser reportUser : reportUsers) {
			basicDao.remove(reportUser);
		}

		basicDao.remove(report);
	}

	public void removeUserReport(User user, Report report) throws Exception {
		removeUserReport(user.getId(), report.getId());
	}

	public void removeUserReport(int userId, int reportId) throws Exception {
		ReportUser entry = findOneUserReport(userId, reportId);
		basicDao.remove(entry);
	}

	public void toggleReportUserFavorite(int userId, int reportId) throws Exception {
		ReportUser reportUser = findOneUserReport(userId, reportId);
		reportUser.toggleFavorite();
		basicDao.save(reportUser);
	}

	public void giveUserDefaultReports(Permissions permissions) {
		// If a user logs in for the first time, they get the default set
		// If the user deletes their last report, they get the default set
		// TODO replace this hack with a customize recommendation default report set
		try {
			Report report11 = basicDao.findOne(Report.class, "id = 11");
			ReportUser reportUser11 = new ReportUser(permissions.getUserId(), report11);
			reportUser11.setAuditColumns(permissions);
			basicDao.save(reportUser11);

			Report report12 = basicDao.findOne(Report.class, "id = 12");
			ReportUser reportUser12 = new ReportUser(permissions.getUserId(), report12);
			reportUser12.setAuditColumns(permissions);
			basicDao.save(reportUser12);
		} catch (Exception e) {
			logger.error(e.toString());
		}
	}
}
