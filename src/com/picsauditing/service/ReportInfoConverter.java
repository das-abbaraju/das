package com.picsauditing.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.picsauditing.jpa.entities.ReportUser;
import org.apache.commons.collections.CollectionUtils;

public class ReportInfoConverter {

	public List<ReportInfo> convertReportUserToReportInfo(List<ReportUser> reportUsers) {
		if (CollectionUtils.isEmpty(reportUsers)) {
            return Collections.emptyList();
        }

        List<ReportInfo> reportInfoList = new ArrayList<ReportInfo>();
		for (ReportUser reportUser : reportUsers) {
			reportInfoList.add(convertReportUserToReportInfo(reportUser));
		}

		return reportInfoList;
	}

	public ReportInfo convertReportUserToReportInfo(ReportUser reportUser) {
		ReportInfo reportInfo = new ReportInfo();
		if (reportUser == null) {
			return reportInfo;
		}

		reportInfo.setCreatedBy(reportUser.getReport().getCreatedBy());
		reportInfo.setCreationDate(reportUser.getReport().getCreationDate());
		reportInfo.setDescription(reportUser.getReport().getDescription());
		reportInfo.setEditable(false); // TODO: look into a better way to set this value from permissions
		reportInfo.setFavorite(reportUser.isFavorite());
		reportInfo.setPrivate(reportUser.getReport().isPrivate());
		reportInfo.setId(reportUser.getReport().getId());
		reportInfo.setLastViewedDate(reportUser.getLastViewedDate());
		reportInfo.setName(reportUser.getReport().getName());
		reportInfo.setSortOrder(reportUser.getSortOrder());

		return reportInfo;
	}

}
