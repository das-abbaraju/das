package com.picsauditing.report.access;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.persistence.NoResultException;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.ReportValidationException;
import com.picsauditing.dao.BasicDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.SpringUtils;

public class ReportAccess {
	
	private static final BasicDAO basicDao = SpringUtils.getBean("BasicDAO");
	
	private static final List<Integer> baseReports =
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

		return (CollectionUtils.isEmpty(reportUserList));
	}
	
	public static boolean canUserEdit(int userId, Report report) {
		ReportUser reportUser = basicDao.findOne(ReportUser.class, "t.user.id = "
				+ userId + " AND t.report.id = " + report.getId());

		return (reportUser != null) ? reportUser.isCanEdit() : false; 
	}

	public static boolean canUserDelete(int userId, Report report) {
		return (report.getCreatedBy().getId() == userId);
	}
	
	public static void connectReportToUser(Report report, User user) {
		ReportUser entry = new ReportUser();
		entry.setAuditColumns(user);
		entry.setReport(report);
		entry.setUser(report.getCreatedBy());
		entry.setCanEdit(false);
		basicDao.save(entry);
	}
	
	public static void grantPermissionToEdit(Report report, User user) {
		editReportEditPermissions(report, user, true);
	}
	
	public static void revokePermissionToEdit(Report report, User user) {
		editReportEditPermissions(report, user, false);
	}
	
	private static void editReportEditPermissions(Report report, User user, boolean value) {
		List<ReportUser> entries = basicDao.findWhere(ReportUser.class, "t.user.id = " + user.getId() + " AND t.report.id = " + report.getId());
		
		if (CollectionUtils.isEmpty(entries) || entries.size() > 1) return;
		
		ReportUser entry = entries.get(0);
		entry.setCanEdit(value);
		basicDao.save(entry);
	}
	
	public static void saveReport(Report report, User user) throws ReportValidationException {
		DynamicReportUtil.validate(report);
		report.setAuditColumns(user);
		basicDao.save(report);
	}
	
	//TODO: Make certain report cascading happens.
	public static void deleteReport(Report report) throws NoResultException {
		List<ReportUser> reportUsers = basicDao.findWhere(ReportUser.class, "t.reportID = " + report.getId());
		for (ReportUser reportUser : reportUsers) {
			basicDao.remove(reportUser);
		}
		basicDao.remove(report);
	}
	
	public static Report findReportById(int Id) throws NoResultException {
		return basicDao.findOne(Report.class, "t.id = " + Id);
	}
	
	
}
