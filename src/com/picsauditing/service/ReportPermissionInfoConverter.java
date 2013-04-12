package com.picsauditing.service;

import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ReportPermissionUser;
import com.picsauditing.jpa.entities.User;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReportPermissionInfoConverter {

	@Autowired
	private PermissionService permissionService;

	public List<ReportPermissionInfo> convertToReportPermissionInfo(List<ReportPermissionUser> reportPermissionUsers) {
		if (CollectionUtils.isEmpty(reportPermissionUsers)) {
			return Collections.emptyList();
		}

		List<ReportPermissionInfo> reportPermissionInfoList = new ArrayList<>();

		for (ReportPermissionUser reportPermissionUser : reportPermissionUsers) {
			reportPermissionInfoList.add(convertToReportPermissionInfo(reportPermissionUser));
		}

		return reportPermissionInfoList;
	}

	private ReportPermissionInfo convertToReportPermissionInfo(ReportPermissionUser reportPermissionUser) {
		ReportPermissionInfo reportPermissionInfo = new ReportPermissionInfo();

		if (reportPermissionUser == null) {
			return reportPermissionInfo;
		}

		User user = reportPermissionUser.getUser();
		Account account = user.getAccount();

		reportPermissionInfo.setId(user.getId());
		reportPermissionInfo.setUserName(user.getName());
		reportPermissionInfo.setOwner(permissionService.isOwner(user, reportPermissionUser.getReport()));
		reportPermissionInfo.setEditable(reportPermissionUser.isEditable());
		reportPermissionInfo.setAccountName(account.getName());
		reportPermissionInfo.setLocation(account.getCity() + ", " + account.getCountrySubdivision());

		return reportPermissionInfo;
	}

}
