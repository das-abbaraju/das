package com.picsauditing.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.picsauditing.jpa.entities.User;
import com.picsauditing.service.ReportInfo;

public class ReportInfoMapper implements RowMapper<ReportInfo> {

	@Override
	public ReportInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
		ReportInfo reportInfo = new ReportInfo();
		reportInfo.setId(rs.getInt(1));
		reportInfo.setName(rs.getString(2));
		reportInfo.setDescription(rs.getString(3));
		reportInfo.setCreationDate(rs.getDate(4));
		reportInfo.setFavorite(rs.getBoolean(5));
		reportInfo.setEditable(rs.getBoolean(6));
		reportInfo.setLastViewedDate(rs.getDate(7));

		reportInfo.setCreatedBy(mapUser(rs, rowNum));

		return reportInfo;
	}

	private User mapUser(ResultSet rs, int rowNum) throws SQLException {
		UserMapper userMapper = new UserMapper();
		return userMapper.mapRow(rs, rowNum);
	}

}
