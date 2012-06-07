package com.picsauditing.util.business;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.picsauditing.dao.BasicDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.util.SpringUtils;

public class DynamicReportUtil {

	private static final BasicDAO basicDao = SpringUtils.getBean("BasicDAO");

	public static boolean userCanCopy(int userId, Report report) {
		List<ReportUser> reportUserList = basicDao.findWhere(ReportUser.class, "t.user.id = "
				+ userId + " AND t.report.id = " + report.getId());

		if (CollectionUtils.isEmpty(reportUserList))
			return false;

		return true;
	}

	public static boolean userCanEdit(int userId, Report report) {
		List<ReportUser> reportUserList = basicDao.findWhere(ReportUser.class, "t.user.id = "
				+ userId + " AND t.report.id = " + report.getId());

		if (CollectionUtils.isEmpty(reportUserList))
			return false;

		if (reportUserList.get(0).isCanEdit())
			return true;

		return false;
	}

	public static boolean userCanDelete(int userId, Report report) {
		if (report.getCreatedBy().getId() == userId)
			return true;

		return false;
	}
}
