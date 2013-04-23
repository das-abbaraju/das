package com.picsauditing.dao.mapper;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.picsauditing.salecommission.invoice.strategy.CommissionAudit;
import com.picsauditing.search.QueryMapper;

public class CommissionAuditQueryMapper implements QueryMapper<CommissionAudit> {

	@Override
	public void mapObjectToPreparedStatement(CommissionAudit commissionAudit, PreparedStatement preparedStatement) throws SQLException {
		if (commissionAudit == null || preparedStatement == null) {
			throw new IllegalArgumentException("CommissionAudit and PreparedStatement cannot be null.");
		}
		
		preparedStatement.setInt(1, commissionAudit.getInvoiceId());
		preparedStatement.setInt(2, commissionAudit.getClientSiteId());
		preparedStatement.setString(3, commissionAudit.getFeeClass().name());
	}

}
