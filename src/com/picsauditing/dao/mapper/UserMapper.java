package com.picsauditing.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.picsauditing.jpa.entities.User;

public class UserMapper implements RowMapper<User> {

	public static final String USER_NAME_FIELD = "users.name";
	public static final String USER_ID_FIELD = "users.id";

	@Override
	public User mapRow(ResultSet rs, int rowNum) throws SQLException {
		User user = new User();
		user.setId(rs.getInt(USER_ID_FIELD));
		user.setName(rs.getString(USER_NAME_FIELD));

		return user;
	}

}
