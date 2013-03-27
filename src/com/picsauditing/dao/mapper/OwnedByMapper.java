package com.picsauditing.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.picsauditing.service.ReportInfo;

public class OwnedByMapper implements RowMapper<ReportInfo> {

	@Override
	public ReportInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
		ReportInfo reportInfo = new ReportInfo();
		reportInfo.setId(rs.getInt(1));
		reportInfo.setName(rs.getString(2));
		reportInfo.setDescription(rs.getString(3));
		reportInfo.setCreationDate(rs.getDate(4));

		return reportInfo;
	}

}
