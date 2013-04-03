package com.picsauditing.service;

import static com.picsauditing.report.ReportJson.REPORT_FAVORITE;

import java.util.Date;

import javax.persistence.NoResultException;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.ReportDAO;
import com.picsauditing.dao.ReportUserDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;

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
