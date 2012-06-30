package com.picsauditing.report.access;

import javax.persistence.NoResultException;

import com.picsauditing.access.ReportValidationException;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.User;

public interface ReportAdministration {

	public boolean canUserViewAndCopy(int userId, Report report);

	public boolean canUserViewAndCopy(int userId, int reportId);

	public boolean canUserEdit(int userId, Report report);

	public boolean canUserDelete(int userId, Report report);

	public void connectReportToUser(Report report, User user);

	public void grantPermissionToEdit(Report report, User user);

	public void revokePermissionToEdit(Report report, User user);

	public void saveReport(Report report, User user) throws ReportValidationException;

	public void deleteReport(Report report) throws NoResultException;

	public Report findReportById(int id) throws NoResultException;

}