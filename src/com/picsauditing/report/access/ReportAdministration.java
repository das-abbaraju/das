package com.picsauditing.report.access;

import java.util.List;

import javax.persistence.NoResultException;

import com.picsauditing.access.ReportValidationException;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.jpa.entities.User;

public interface ReportAdministration {

	public Report findReportById(int id) throws NoResultException;

	public void refresh(Report report);

	public List<ReportUser> queryReportUser(int userId, int reportId);

	public void connectReportToUser(Report report, User user);

	public void grantEditPermission(Report report, User user);

	public void revokeEditPermission(Report report, User user);

	public void saveReport(Report report, User user) throws ReportValidationException;

	public void deleteReport(Report report) throws NoResultException;

}