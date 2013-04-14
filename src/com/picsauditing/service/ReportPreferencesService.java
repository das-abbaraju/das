package com.picsauditing.service;

import static com.picsauditing.report.ReportJson.REPORT_FAVORITE;

import java.util.Date;
import java.util.List;

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
	@Autowired
	private ReportInfoProvider reportInfoProvider;

	public ReportUser loadReportUser(int userId, int reportId) {
		return reportUserDao.findOne(userId, reportId);
	}

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

	public List<ReportUser> getAllNonHiddenReportUsers(ReportSearch reportSearch) {
		return reportUserDao.findAllOrdered(reportSearch);
	}

	public boolean isUserFavoriteReport(int userId, int reportId) {
		try {
			ReportUser reportUser = reportUserDao.findOne(userId, reportId);

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

	void shiftFavoritesDisplacedByMove(int userId, int currentPosition, int newPosition) throws SQLException {
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

	public List<ReportInfo> buildFavorites(int userId) {
		List<ReportInfo> favorites = reportInfoProvider.findAllFavoriteReports(userId);
		if (sortOrderNeedsToBeReIndexed(favorites)) {
			favorites = reIndexSortOrder(favorites, userId);
		}

		return favorites;
	}

	private boolean sortOrderNeedsToBeReIndexed(List<ReportInfo> sortedFavorites) {
		ReportInfo firstReportUserInList = sortedFavorites.get(0);
		int highestSortOrder = firstReportUserInList.getSortOrder();

		if (highestSortOrder != sortedFavorites.size()) {
			return true;
		}

		if (hasDuplicateSortOrders(sortedFavorites)) {
			return true;
		}

		return false;
	}

	private List<ReportInfo> reIndexSortOrder(List<ReportInfo> favorites, int userId) {
		List<ReportInfo> reIndexedFavorites = new ArrayList<>();
		for (int i = 0; i < favorites.size(); i++) {
			ReportInfo favorite = favorites.get(i);
			int newSortOrder = favorites.size() - i;

			if (newSortOrder != favorite.getSortOrder()) {
				favorite.setSortOrder(newSortOrder);
				reportInfoProvider.updateSortOrder(favorite, userId);
			}

			reIndexedFavorites.add(favorite);
		}

		return reIndexedFavorites;
	}

	private boolean hasDuplicateSortOrders(List<ReportInfo> favorites) {
		Set<Integer> uniqueSortOrders = new HashSet<>();
		for (ReportInfo favoriteReport : favorites) {
			boolean addedSuccessfully = uniqueSortOrders.add(favoriteReport.getSortOrder());
			if (!addedSuccessfully) {
				return true;
			}
		}

		return false;
	}

}
