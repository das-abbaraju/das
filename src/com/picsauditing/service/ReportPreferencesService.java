package com.picsauditing.service;

import static com.picsauditing.report.ReportJson.REPORT_FAVORITE;

import java.sql.SQLException;
import java.util.*;

import javax.persistence.NoResultException;

import com.picsauditing.jpa.entities.User;
import org.apache.commons.collections.CollectionUtils;
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
	private ReportInfoConverter reportInfoConverter;

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
		reportUser.setFavorite(true);

		List<ReportUser> pinnedFavorites = reportUserDao.findPinnedFavorites(userId);
		List<ReportUser> unpinnedFavorites = reportUserDao.findUnpinnedFavorites(userId);

		// Favoriting a report really means putting it at the top of the unpinned reports
		unpinnedFavorites.add(0, reportUser);

		List<ReportUser> favorites = mergePinnedAndUnpinnedFavorites(pinnedFavorites, unpinnedFavorites);

		reIndexSortOrder(favorites);

		return reportUser;
	}

	public ReportUser unfavoriteReport(ReportUser reportUser) {
		reportUser.setSortOrder(0);
		reportUser.setFavorite(false);
		reportUser.setPinnedIndex(ReportUser.UNPINNED_INDEX);
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

	public ReportUser moveUnpinnedFavoriteUp(ReportUser movingFavorite) throws Exception {
		if (movingFavorite.isPinned()) {
			return movingFavorite;
		}

		List<ReportUser> replacedFavoriteList = reportUserDao.findUnpinnedWithNextHighestSortOrder(movingFavorite);

		if (CollectionUtils.isNotEmpty(replacedFavoriteList)) {
			ReportUser replacedFavorite = replacedFavoriteList.get(0);

			int originalSortOrder = movingFavorite.getSortOrder();
			movingFavorite.setSortOrder(replacedFavorite.getSortOrder());
			replacedFavorite.setSortOrder(originalSortOrder);

			reportUserDao.save(replacedFavorite);
			reportUserDao.save(movingFavorite);
		}

		return movingFavorite;
	}

	public ReportUser moveUnpinnedFavoriteDown(ReportUser movingFavorite) throws Exception {
		if (movingFavorite.isPinned()) {
			return movingFavorite;
		}

		List<ReportUser> replacedFavoriteList = reportUserDao.findUnpinnedWithNextLowestSortOrder(movingFavorite);

		if (CollectionUtils.isNotEmpty(replacedFavoriteList)) {
			ReportUser replacedFavorite = replacedFavoriteList.get(0);

			int originalSortOrder = movingFavorite.getSortOrder();
			movingFavorite.setSortOrder(replacedFavorite.getSortOrder());
			replacedFavorite.setSortOrder(originalSortOrder);

			reportUserDao.save(replacedFavorite);
			reportUserDao.save(movingFavorite);
		}

		return movingFavorite;
	}

	public List<ReportInfo> buildFavorites(int userId) {
		List<ReportUser> pinnedFavorites = reportUserDao.findPinnedFavorites(userId);
		List<ReportUser> unpinnedFavorites = reportUserDao.findUnpinnedFavorites(userId);

		List<ReportUser> favorites = mergePinnedAndUnpinnedFavorites(pinnedFavorites, unpinnedFavorites);

		if (sortOrderNeedsToBeReIndexed(favorites)) {
			favorites = reIndexSortOrder(favorites);
		}

		List<ReportInfo> favoritesDTO = reportInfoConverter.convertReportUserToReportInfo(favorites);

		return favoritesDTO;
	}

	private List<ReportUser> mergePinnedAndUnpinnedFavorites(List<ReportUser> pinnedFavorites, List<ReportUser> unpinnedFavorites) {
		List<ReportUser> mergedFavorites = new ArrayList<>();
		int totalFavorites = pinnedFavorites.size() + unpinnedFavorites.size();

		for (int i = 0; i < totalFavorites; i += 1) {
			// See if we've run out of either list
			if (CollectionUtils.isEmpty(pinnedFavorites)) {
				mergedFavorites.addAll(unpinnedFavorites);
				break;
			} else if (CollectionUtils.isEmpty(unpinnedFavorites)) {
				mergedFavorites.addAll(pinnedFavorites);
				break;
			}

			if (i == pinnedFavorites.get(0).getPinnedIndex()) {
				mergedFavorites.add(pinnedFavorites.get(0));
				pinnedFavorites.remove(0);
			} else {
				mergedFavorites.add(unpinnedFavorites.get(0));
				unpinnedFavorites.remove(0);
			}
		}

		return mergedFavorites;
	}

	private boolean sortOrderNeedsToBeReIndexed(List<ReportUser> sortedFavorites) {
		ReportUser firstReportUserInList = sortedFavorites.get(0);
		int highestSortOrder = firstReportUserInList.getSortOrder();

		if (highestSortOrder != sortedFavorites.size()) {
			return true;
		}

		if (hasDuplicateSortOrders(sortedFavorites)) {
			return true;
		}

		return false;
	}

	public List<ReportUser> reIndexSortOrder(List<ReportUser> favorites) {
		int expectedSortOrder = favorites.size();

		for (ReportUser favorite : favorites) {
			if (favorite.getSortOrder() != expectedSortOrder) {
				favorite.setSortOrder(expectedSortOrder);
				reportUserDao.save(favorite);
			}

			expectedSortOrder -= 1;
		}

		return favorites;
	}

	private boolean hasDuplicateSortOrders(List<ReportUser> favorites) {
		Set<Integer> uniqueSortOrders = new HashSet<>();

		for (ReportUser favoriteReport : favorites) {
			boolean addedSuccessfully = uniqueSortOrders.add(favoriteReport.getSortOrder());

			if (!addedSuccessfully) {
				return true;
			}
		}

		return false;
	}

	public void pinFavorite(User user, Report report, int pinnedIndex) throws Exception {
		if (!isUserFavoriteReport(user.getId(), report.getId())) {
			throw new Exception("Report " + report.getId() + " could not be pinned by user " + user.getId() + " because it is a not a favorite.");
		}

		// Make sure there isn't already a favorite pinned at pinnedIndex
		for (ReportUser reportUser : reportUserDao.findAllFavorite(user.getId())) {
			if (reportUser.getPinnedIndex() == pinnedIndex) {
				throw new Exception("User " + user.getId() + " already has a favorite pinned at index " + pinnedIndex);
			}
		}

		ReportUser reportPreferences = loadReportUser(user.getId(), report.getId());
		reportPreferences.setPinnedIndex(pinnedIndex);
		reportUserDao.save(reportPreferences);
	}

	public void unpinFavorite(User user, Report report) throws Exception {
		if (!isUserFavoriteReport(user.getId(), report.getId())) {
			throw new Exception("Report " + report.getId() + " could not be unpinned by user " + user.getId() + " because it is a not a favorite.");
		}

		ReportUser reportPreferences = loadReportUser(user.getId(), report.getId());
		reportPreferences.setPinnedIndex(ReportUser.UNPINNED_INDEX);
		reportUserDao.save(reportPreferences);
	}

}
