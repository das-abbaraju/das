package com.picsauditing.util.business;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.picsauditing.dao.BasicDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.report.Column;
import com.picsauditing.util.SpringUtils;

public class DynamicReportUtil {

	private static final BasicDAO basicDao = SpringUtils.getBean("BasicDAO");

	public static final List<Integer> baseReports =
			Collections.unmodifiableList(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12));

	public static boolean canUserViewAndCopy(int userId, Report report) {
		if (report == null)
			return false;

		return canUserViewAndCopy(userId, report.getId());
	}

	public static boolean canUserViewAndCopy(int userId, int reportId) {
		if (baseReports.contains(reportId))
			return true;

		List<ReportUser> reportUserList = basicDao.findWhere(ReportUser.class, "t.user.id = "
				+ userId + " AND t.report.id = " + reportId);

		if (CollectionUtils.isEmpty(reportUserList))
			return false;

		return true;
	}

	public static boolean canUserEdit(int userId, Report report) {
		List<ReportUser> reportUserList = basicDao.findWhere(ReportUser.class, "t.user.id = "
				+ userId + " AND t.report.id = " + report.getId());

		if (CollectionUtils.isEmpty(reportUserList))
			return false;

		if (reportUserList.get(0).isCanEdit())
			return true;

		return false;
	}

	public static boolean canUserDelete(int userId, Report report) {
		if (report.getCreatedBy().getId() == userId)
			return true;

		return false;
	}

	public static Column getColumnFromFieldName(String fieldName, List<Column> columns) {
		if (fieldName == null)
			return null;

		for (Column column : columns) {
			if (column.getFieldName().equals(fieldName))
				return column;
		}

		return null;
	}
}
