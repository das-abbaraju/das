package com.picsauditing.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.picsauditing.jpa.entities.User;
import com.picsauditing.service.ReportInfo;

public class ReportInfoMapper implements RowMapper<ReportInfo> {

	public static final String NUMBER_OF_TIMES_FAVORITED = "numberOfTimesFavorited";
	public static final String LAST_VIEWED_DATE_FIELD = "lastViewedDate";
	public static final String EDITABLE_FIELD = "editable";
	public static final String PRIVATE_FIELD = "private";
	public static final String FAVORITE_FIELD = "favorite";
	public static final String CREATION_DATE_FIELD = "creationDate";
	public static final String DESCRIPTION_FIELD = "description";
	public static final String NAME_FIELD = "name";
	public static final String ID_FIELD = "id";

	@Override
	public ReportInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
		ReportInfo reportInfo = new ReportInfo();
		reportInfo.setId(rs.getInt(ID_FIELD));
		reportInfo.setName(rs.getString(NAME_FIELD));
		reportInfo.setDescription(rs.getString(DESCRIPTION_FIELD));
		reportInfo.setCreationDate(rs.getDate(CREATION_DATE_FIELD));
		reportInfo.setFavorite(rs.getBoolean(FAVORITE_FIELD));
		reportInfo.setPrivate(rs.getBoolean(PRIVATE_FIELD));
		reportInfo.setEditable(rs.getBoolean(EDITABLE_FIELD));
		reportInfo.setLastViewedDate(rs.getDate(LAST_VIEWED_DATE_FIELD));
		reportInfo.setNumberOfTimesFavorited(rs.getInt(NUMBER_OF_TIMES_FAVORITED));

		reportInfo.setCreatedBy(mapUser(rs, rowNum));

		return reportInfo;
	}

	private User mapUser(ResultSet rs, int rowNum) throws SQLException {
		UserMapper userMapper = new UserMapper();
		return userMapper.mapRow(rs, rowNum);
	}

}
