package com.picsauditing.dao;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.ReportPermissionAccount;

public class ReportPermissionAccountDAO extends PicsDAO {

	@Transactional(propagation = Propagation.NESTED)
	public ReportPermissionAccount findOne(int accountId, int reportId) throws NoResultException, NonUniqueResultException {
		String query = "t.account.id = " + accountId + " AND t.report.id = " + reportId;
		return findOne(ReportPermissionAccount.class, query);
	}

	@Transactional(propagation = Propagation.NESTED)
	public List<ReportPermissionAccount> findAll(int accountId) {
		String query = "t.account.id = " + accountId;
		return findWhere(ReportPermissionAccount.class, query);
	}
}