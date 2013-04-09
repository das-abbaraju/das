package com.picsauditing.service;

import static com.picsauditing.report.ReportJson.REPORT_FAVORITE;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Permissions;
import com.picsauditing.actions.report.ManageReports;
import com.picsauditing.dao.ReportDAO;
import com.picsauditing.dao.ReportUserDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.util.Strings;

public class ReportPreferencesService {

	@Autowired
	private ReportUserDAO reportUserDao;
	@Autowired
	private ReportDAO reportDao;

	public ReportUser loadOrCreateReportUser(int userId, int reportId) {
		ReportUser reportUser;

		try {
			reportUser = loadReportUser(userId, reportId);
		} catch (NoResultException nre) {
			reportUser = createReportUser(userId, reportId);
		}

		return reportUser;
	}

	public void stampViewed(ReportUser reportUser, Permissions permissions) {
		reportUser.setAuditColumns(permissions);
		reportUser.setLastViewedDate(new Date());
		reportUser.setViewCount(reportUser.getViewCount()+1);
		reportUserDao.save(reportUser);
	}

	private ReportUser createReportUser(int userId, int reportId) {
		Report report = reportDao.findById(reportId);
		return createReportUser(userId, report);
	}

	private ReportUser createReportUser(int userId, Report report) {
		ReportUser reportUser = new ReportUser(userId, report);
		reportUserDao.save(reportUser);

		return reportUser;
	}

	public ReportUser loadReportUser(int userId, int reportId) {
		return reportUserDao.findOne(userId, reportId);
	}

	public List<ReportUser> getAllNonHiddenReportUsers(String sort, String direction, Permissions permissions) {
		return getAllReportUsers(sort, direction, permissions, false);
	}

	public List<ReportUser> getAllReportUsers(String sort, String direction, Permissions permissions, boolean includeHidden) throws IllegalArgumentException {
		List<ReportUser> reportUsers = new ArrayList<ReportUser>();

		if (Strings.isEmpty(sort)) {
			sort = ManageReports.ALPHA_SORT;
			direction = "ASC";
		}

		List<Report> reports = reportDao.findAllOrdered(permissions, sort, direction, includeHidden);

		for (Report report : reports) {
			ReportUser reportUser = report.getReportUser(permissions.getUserId());
			if (reportUser == null) {
				// todo: Why are creating a new ReportUser for each report???
				reportUser = createReportUser(permissions.getUserId(), report);
			}
			reportUsers.add(reportUser);
		}

		return reportUsers;
	}

	// todo: extract out the userId
	public boolean isUserFavoriteReport(Permissions permissions, int reportId) {
		try {
			ReportUser reportUser = reportUserDao.findOne(permissions.getUserId(), reportId);

			if (reportUser == null) {
				return false;
			}

			return reportUser.isFavorite();
		} catch (NoResultException nre) {

		}

		return false;
	}

	public ReportUser favoriteReport(ReportUser reportUser) {
		int userId = reportUser.getUser().getId();
		int nextSortIndex = getNextSortIndex(userId);

		reportUser.setSortOrder(nextSortIndex);
		reportUser.setFavorite(true);
		reportUser = (ReportUser) reportUserDao.save(reportUser);

		return reportUser;
	}

	public ReportUser unfavoriteReport(ReportUser reportUser) {
		reportUser.setSortOrder(0);
		reportUser.setFavorite(false);
		reportUser = (ReportUser) reportUserDao.save(reportUser);

		return reportUser;
	}


	public boolean shouldFavorite(JSONObject reportJson) {
		try {
			boolean favorite = (Boolean) reportJson.get(REPORT_FAVORITE);
			return favorite;
		} catch (Exception e) {
			// Just eat it!
		}

		return false;
	}

	private int getNextSortIndex(int userId) {
		int maxSortIndex = reportUserDao.findMaxSortIndex(userId);
		return maxSortIndex + 1;
	}
}
