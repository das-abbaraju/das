package com.picsauditing.service;

import com.picsauditing.jpa.entities.*;
import com.picsauditing.report.ReportUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ReportPermissionInfoConverter {

	public static final String TYPE_USER = "User";
	public static final String ACCESS_TYPE_USER = "user";

	@Autowired
	private PermissionService permissionService;

	public List<ReportPermissionInfo> convertUsersToReportPermissionInfo(List<ReportPermissionUser> reportPermissionUsers) {
		if (CollectionUtils.isEmpty(reportPermissionUsers)) {
			return new ArrayList<>();
		}

		List<ReportPermissionInfo> reportPermissionInfoList = new ArrayList<>();

		for (ReportPermissionUser reportPermissionUser : reportPermissionUsers) {
			reportPermissionInfoList.add(convertUserToReportPermissionInfo(reportPermissionUser));
		}

		return reportPermissionInfoList;
	}

	private ReportPermissionInfo convertUserToReportPermissionInfo(ReportPermissionUser reportPermissionUser) {
		ReportPermissionInfo reportPermissionInfo = new ReportPermissionInfo();

		if (reportPermissionUser == null) {
			return reportPermissionInfo;
		}

		User user = reportPermissionUser.getUser();
		Account account = user.getAccount();

		reportPermissionInfo.setId(user.getId());
		reportPermissionInfo.setName(user.getName());
		reportPermissionInfo.setOwner(permissionService.isOwner(user, reportPermissionUser.getReport()));
		reportPermissionInfo.setEditable(reportPermissionUser.isEditable());
		reportPermissionInfo.setLocation(account.getName());
		reportPermissionInfo.setType(user.isGroup() ? "Group" : TYPE_USER);
		reportPermissionInfo.setAccessType(user.isGroup() ? "group" : ACCESS_TYPE_USER);

		return reportPermissionInfo;
	}

	public List<ReportPermissionInfo> convertAccountsToReportPermissionInfo(List<ReportPermissionAccount> reportPermissionAccounts, Locale locale) {
		if (CollectionUtils.isEmpty(reportPermissionAccounts)) {
			return new ArrayList<>();
		}

		List<ReportPermissionInfo> reportPermissionInfoList = new ArrayList<>();

		for (ReportPermissionAccount reportPermissionAccount : reportPermissionAccounts) {
			reportPermissionInfoList.add(convertAccountToReportPermissionInfo(reportPermissionAccount, locale));
		}

		return reportPermissionInfoList;
	}

	private ReportPermissionInfo convertAccountToReportPermissionInfo(ReportPermissionAccount reportPermissionAccount, Locale locale) {
		ReportPermissionInfo reportPermissionInfo = new ReportPermissionInfo();

		if (reportPermissionAccount == null) {
			return reportPermissionInfo;
		}

		Account account = reportPermissionAccount.getAccount();

		reportPermissionInfo.setId(account.getId());
		reportPermissionInfo.setName(account.getName());
		reportPermissionInfo.setOwner(false);
		reportPermissionInfo.setEditable(reportPermissionAccount.isEditable());
		reportPermissionInfo.setLocation(translateLocation(account.getCity(), account.getCountrySubdivision(), locale));
		reportPermissionInfo.setType(account.getType());
		reportPermissionInfo.setAccessType("account");

		return reportPermissionInfo;
	}

	private String translateLocation(String city, CountrySubdivision countrySubdivision, Locale locale) {
		String countrySubdivisionString = (countrySubdivision == null) ? "" : countrySubdivision.toString();

		return ReportUtil.buildLocationString(city, countrySubdivisionString, locale);
	}

}
