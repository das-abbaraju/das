package com.picsauditing.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.picsauditing.jpa.entities.FeeClass;
import com.picsauditing.salecommission.invoice.strategy.CommissionAudit;

public class CommissionAuditRowMapper implements RowMapper<CommissionAudit> {

	@Override
	public CommissionAudit mapRow(ResultSet rs, int rowNum) throws SQLException {
		CommissionAudit commissionAudit = new CommissionAudit();
		commissionAudit.setInvoiceId(rs.getInt(1));
		commissionAudit.setClientSiteId(rs.getInt(2));
		commissionAudit.setFeeClass(FeeClass.valueOf(FeeClass.class, rs.getString(3)));

		return commissionAudit;
	}

}