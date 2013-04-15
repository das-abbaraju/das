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
		reportFavoriteInfo.setCreatedBy(reportUser.getReport().getCreatedBy());
		reportFavoriteInfo.setSortOrder(reportUser.getSortOrder());
		reportFavoriteInfo.setPinnedIndex(reportUser.getPinnedIndex());

		return reportFavoriteInfo;
	}
}
