package com.picsauditing.dao;

import java.sql.SQLException;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.collections.CollectionUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.ReportValidationException;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.model.ReportDynamicModel;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;

public class ReportDAO extends PicsDAO {

	// TODO temp solution for save not working. I have no clue about this
	@Autowired
	private BasicDAO basicDao;

	public Report findOneReport(int id) throws NoResultException {
		return findOne(Report.class, "t.id = " + id);
	}

	public List<Report> findPublicReports() {
		String query = "private = 0";
		List<Report> publicReports = findWhere(Report.class, query);

		return publicReports;
	}

	public List<Report> findAllReports() {
		return findAll(Report.class);
	}

	public ReportUser findOneUserReport(int userId, int reportId) throws NoResultException, NonUniqueResultException {
		String query = "t.user.id = " + userId + " AND t.report.id = " + reportId;
		List<ReportUser> result = findWhere(ReportUser.class, query);

		if (CollectionUtils.isEmpty(result))
			throw new NoResultException("No result found for userId = " + userId + " and reportId = " + reportId);

		if (result.size() > 1)
			throw new NonUniqueResultException("Multiple results found for userId = " + userId + " and reportId = " + reportId);

		return result.get(0);
	}

	public List<ReportUser> findFavoriteUserReports(int userId) {
		String query = "t.user.id = " + userId + " AND is_favorite = 1";
		return findWhere(ReportUser.class, query);
	}

	public List<ReportUser> findEditableUserReports(int userId) {
		String query = "t.user.id = " + userId + " AND is_editable = 1";
		return findWhere(ReportUser.class, query);
	}

	public List<ReportUser> findAllUserReports(int userId) {
		String query = "t.user.id = " + userId;
		return findWhere(ReportUser.class, query);
	}

	public void refreshReport(Report report) {
		refresh(report);
	}

	public void saveReport(Report report, User user) throws ReportValidationException {
		ReportDynamicModel.validate(report);
		report.setAuditColumns(user);

		basicDao.save(report);
	}

	public void deleteReport(Report report) {
		List<ReportUser> userReports = findWhere(ReportUser.class, "t.report.id = " + report.getId());
		for (ReportUser userReport : userReports) {
			remove(userReport);
		}

		remove(report);
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

	private void setUserEditPermissions(Report report, User user, boolean value) throws NoResultException, NonUniqueResultException {
		ReportUser userReport = findOneUserReport(user.getId(), report.getId());
		userReport.setEditable(value);

		basicDao.save(userReport);
	}

	public void removeUserReport(User user, Report report) throws NoResultException, NonUniqueResultException {
		removeUserReport(user.getId(), report.getId());
	}

	public void removeUserReport(int userId, int reportId) throws NoResultException, NonUniqueResultException {
		ReportUser userReport = findOneUserReport(userId, reportId);
		remove(userReport);
	}

	public void toggleReportUserFavorite(int userId, int reportId) throws NoResultException, NonUniqueResultException {
		ReportUser reportUser = findOneUserReport(userId, reportId);
		reportUser.toggleFavorite();
		basicDao.save(reportUser);
	}

	public void favoriteReport(int userId, int reportId) throws NoResultException, NonUniqueResultException {
		ReportUser reportUser = findOneUserReport(userId, reportId);
		reportUser.setFavorite(true);
		basicDao.save(reportUser);
	}

	public void unfavoriteReport(int userId, int reportId) throws NoResultException, NonUniqueResultException {
		ReportUser reportUser = findOneUserReport(userId, reportId);
		reportUser.setFavorite(false);
		basicDao.save(reportUser);
	}

	@SuppressWarnings("unchecked")
	public List<BasicDynaBean> runQuery(SelectSQL sql, JSONObject json) throws SQLException {
		Database db = new Database();
		List<BasicDynaBean> rows = db.select(sql.toString(), true);
		json.put("total", db.getAllRows());

		return rows;
	}
}
