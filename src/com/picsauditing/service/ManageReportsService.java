package com.picsauditing.service;

import static com.picsauditing.report.ReportJson.REPORT_FAVORITE;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.ReportDAO;
import com.picsauditing.dao.ReportUserDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.report.ReportPaginationParameters;
import com.picsauditing.util.Strings;
import com.picsauditing.util.pagination.Pagination;

public class ManageReportsService {

	@Autowired
	private ReportDAO reportDao;
	@Autowired
	private ReportUserDAO reportUserDao;

	public ReportUser favoriteReport(ReportUser reportUser) {
		int userId = reportUser.getUser().getId();
		int nextSortIndex = getNextSortIndex(userId);

		reportUser.setSortOrder(nextSortIndex);
		reportUser.setFavorite(true);

		return reportUser;
	}

	private int getNextSortIndex(int userId) {
		int maxSortIndex = reportUserDao.findMaxSortIndex(userId);
		return maxSortIndex + 1;
	}

	public ReportUser unfavoriteReport(ReportUser reportUser) {
		reportUser.setSortOrder(0);
		reportUser.setFavorite(false);
		reportUser = (ReportUser) reportUserDao.save(reportUser);

		return reportUser;
	}

	public ReportUser moveFavoriteUp(ReportUser reportUser) throws Exception {
		return moveFavorite(reportUser, 1);
	}

	public ReportUser moveFavoriteDown(ReportUser reportUser) throws Exception {
		return moveFavorite(reportUser, -1);
	}

	private ReportUser moveFavorite(ReportUser reportUser, int magnitude) throws Exception {
		int userId = reportUser.getUser().getId();
		int numberOfFavorites = reportUserDao.getFavoriteCount(userId);
		int currentPosition = reportUser.getSortOrder();
		int newPosition = currentPosition + magnitude;

		if (moveIsUnnecessaryOrInvalid(currentPosition, newPosition, numberOfFavorites)) {
			return reportUser;
		}

		shiftFavoritesDisplacedByMove(userId, currentPosition, newPosition);

		reportUser.setSortOrder(newPosition);
		reportUserDao.save(reportUser);

		return reportUser;
	}

	private boolean moveIsUnnecessaryOrInvalid(int currentPosition, int newPosition, int numberOfFavorites) {
		if (currentPosition == newPosition) {
			return true;
		}

		if ((newPosition < 0) || (newPosition > numberOfFavorites)) {
			return true;
		}

		return false;
	}

	private void shiftFavoritesDisplacedByMove(int userId, int currentPosition, int newPosition) throws SQLException {
		reportUserDao.resetSortOrder(userId);

		int offsetAmount;
		int offsetRangeBegin;
		int offsetRangeEnd;

		if (currentPosition < newPosition) {
			// Moving up in list, displaced reports move down
			offsetAmount = -1;
			offsetRangeBegin = currentPosition + 1;
			offsetRangeEnd = newPosition;
		} else {
			// Moving down in list, displaced reports move up
			offsetAmount = 1;
			offsetRangeBegin = newPosition;
			offsetRangeEnd = currentPosition - 1;
		}

		reportUserDao.offsetSortOrderForRange(userId, offsetAmount, offsetRangeBegin, offsetRangeEnd);
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

	public List<Report> getReportsForSearch(String searchTerm, Permissions permissions, Pagination<Report> pagination) {
		List<Report> reports = new ArrayList<Report>();

		if (Strings.isEmpty(searchTerm)) {
			// By default, show the top ten most favorited reports sorted by
			// number of favorites
			List<ReportUser> reportUsers = reportUserDao.findTenMostFavoritedReports(permissions);
			for (ReportUser reportUser : reportUsers) {
				reports.add(reportUser.getReport());
			}
		} else {
			ReportPaginationParameters parameters = new ReportPaginationParameters(permissions, searchTerm);
			pagination.initialize(parameters, reportDao);
			reports = pagination.getResults();
		}

		return reports;
	}

}
