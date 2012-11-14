package com.picsauditing.dao;

import java.util.List;

import javax.persistence.NoResultException;

import com.picsauditing.jpa.entities.ReportPermissionAccount;

public class ReportPermissionAccountDAO extends PicsDAO {
	public ReportPermissionAccount findOne(int accountId, int reportId) throws NoResultException {
		String query = "t.account.id = " + accountId + " AND t.report.id = " + reportId;
		return findOne(ReportPermissionAccount.class, query);
	}

	public List<ReportPermissionAccount> findAll(int accountId) {
		String query = "t.account.id = " + accountId;
		return findWhere(ReportPermissionAccount.class, query);
	}

	public List<ReportPermissionAccount> findAllByReportId(int reportId) {
		String query = "t.report.id = " + reportId;
		return findWhere(ReportPermissionAccount.class, query);
	}
}