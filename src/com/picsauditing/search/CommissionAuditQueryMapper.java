package com.picsauditing.search;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.picsauditing.salecommission.invoice.strategy.CommissionAudit;

public class CommissionAuditQueryMapper implements QueryMapper<CommissionAudit> {

	@Override
	public void mapObjectToPreparedStatement(CommissionAudit commissionAudit, PreparedStatement preparedStatement) throws SQLException {
		if (commissionAudit == null || preparedStatement == null) {
			return;
		}
		
		preparedStatement.setInt(1, commissionAudit.getInvoiceId());
		preparedStatement.setInt(2, commissionAudit.getClientSiteId());
		preparedStatement.setString(3, commissionAudit.getFeeClass().name());
	}

}
