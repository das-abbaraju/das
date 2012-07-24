package com.picsauditing.provider;

import java.sql.SQLException;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.collections.CollectionUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.ReportValidationException;
import com.picsauditing.dao.BasicDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.model.ReportDynamicModel;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;

/**
 * This is the persistence layer. It is the only class that should contain a DAO.
 * It should also contain no business logic.
 */
public class ReportProvider {

	@Autowired
	private BasicDAO basicDao;

	public Report findOneReport(int id) throws NoResultException {
		return basicDao.findOne(Report.class, "t.id = " + id);
	}

	public ReportUser findOneUserReport(int userId, int reportId) throws NoResultException, NonUniqueResultException {
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

	public List<ReportUser> findEditableUserReports(int userId) {
		String query = "t.user.id = " + userId + " AND is_editable = 1";
		return basicDao.findWhere(ReportUser.class, query);
	}

	public void refresh(Report report) {
		basicDao.refresh(report);
	}

	public void connectReportToUser(Report report, User user) {
		ReportUser userReport = new ReportUser();

		userReport.setAuditColumns(user);
		userReport.setReport(report);
		userReport.setUser(user);
		userReport.setEditable(false);

		basicDao.save(userReport);
	}

	public void connectReportToUser(Report report, int userId) {
		ReportUser userReport = new ReportUser(userId, report);
		userReport.setAuditColumns(new User(userId));

		basicDao.save(userReport);
	}

	public void connectReportToUserEditable(Report report, int userId) {
		ReportUser userReport = new ReportUser(userId, report);
		userReport.setAuditColumns(new User(userId));
		userReport.setEditable(true);

		basicDao.save(userReport);
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
		ReportDynamicModel.validate(report);
		report.setAuditColumns(user);

		basicDao.save(report);
	}

	public void deleteReport(Report report) {
		List<ReportUser> userReports = basicDao.findWhere(ReportUser.class, "t.report.id = " + report.getId());
		for (ReportUser userReport : userReports) {
			basicDao.remove(userReport);
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

	@SuppressWarnings("unchecked")
	public static List<BasicDynaBean> runQuery(SelectSQL sql, JSONObject json) throws SQLException {
		Database db = new Database();
		List<BasicDynaBean> rows = db.select(sql.toString(), true);
		json.put("total", db.getAllRows());

		return rows;
	}

	public List<Report> findPublicReports() {
		String query = "private = 0";
		List<Report> publicReports = basicDao.findWhere(Report.class, query);

		return publicReports;
	}

	public boolean isReportPublic(int reportId) {
		try {
			Report report = findOneReport(reportId);
			if (report != null && report.isPublic()) {
				return true;
			}
		} catch (NoResultException nre) {
			// If the report doesn't exist, it's not public
		}

		return false;
	}
}
