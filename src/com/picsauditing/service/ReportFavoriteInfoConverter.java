package com.picsauditing.service;

import com.picsauditing.jpa.entities.ReportUser;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReportFavoriteInfoConverter {

	public List<ReportFavoriteInfo> convert(List<ReportUser> reportUsers) {
		if (CollectionUtils.isEmpty(reportUsers)) {
			return Collections.emptyList();
		}

		List<ReportFavoriteInfo> reportFavoriteInfoList = new ArrayList<>();

		for (ReportUser reportUser : reportUsers) {
			reportFavoriteInfoList.add(convert(reportUser));
		}

		markUnmovableUnpinnedReports(reportFavoriteInfoList);

		return reportFavoriteInfoList;
	}

	public ReportFavoriteInfo convert(ReportUser reportUser) {
		ReportFavoriteInfo reportFavoriteInfo = new ReportFavoriteInfo();
		if (reportUser == null) {
			return reportFavoriteInfo;
		}

		reportFavoriteInfo.setId(reportUser.getReport().getId());
		reportFavoriteInfo.setName(reportUser.getReport().getName());
		reportFavoriteInfo.setFavorite(reportUser.isFavorite());
		reportFavoriteInfo.setCreatedBy(reportUser.getReport().getOwner());
		reportFavoriteInfo.setSortOrder(reportUser.getSortOrder());
		reportFavoriteInfo.setPinnedIndex(reportUser.getPinnedIndex());

		if (reportUser.isPinned()) {
			reportFavoriteInfo.setCanMoveUp(false);
			reportFavoriteInfo.setCanMoveDown(false);
		} else {
			reportFavoriteInfo.setCanMoveUp(true);
			reportFavoriteInfo.setCanMoveDown(true);
		}

		return reportFavoriteInfo;
	}

	private void markUnmovableUnpinnedReports(List<ReportFavoriteInfo> reportFavoriteInfoList) {
		// The highest unpinned report cannot move up
		for (ReportFavoriteInfo favorite : reportFavoriteInfoList) {
			if (!favorite.isPinned()) {
				favorite.setCanMoveUp(false);
				break;
			}
		}

		// The lowest unpinned report cannot move down
		for (int i = reportFavoriteInfoList.size() - 1; i >= 0; i -= 1) {
			ReportFavoriteInfo favorite = reportFavoriteInfoList.get(i);

			if (!favorite.isPinned()) {
				favorite.setCanMoveDown(false);
				break;
			}
		}
	}
}
