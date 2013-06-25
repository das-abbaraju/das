package com.picsauditing.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.picsauditing.jpa.entities.AppProperty;

public class AppPropertyRowMapper implements RowMapper<AppProperty> {

	@Override
	public AppProperty mapRow(ResultSet rs, int rowNum) throws SQLException {
		AppProperty appProperty = new AppProperty();
		appProperty.setProperty(rs.getString("property"));
		appProperty.setValue(rs.getString("value"));
		appProperty.setTicklerDate(rs.getDate("ticklerDate"));
		return appProperty;
	}

}
