package com.picsauditing.dao.mapper;

import com.picsauditing.persistence.model.UserContactInfo;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountContactMapper implements RowMapper<UserContactInfo> {
    private static final String USER_ID_FIELD = "id";
    private static final String USER_NAME_FIELD = "name";
    private static final String USER_EMAIL_FIELD = "email";
    private static final String USER_PHONE_FIELD = "phone";
    private static final String USER_FAX_FIELD = "fax";
    private static final String USERNAME_FIELD = "username";

    @Override
    public UserContactInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new UserContactInfo(
                rs.getInt(USER_ID_FIELD),
                rs.getString(USERNAME_FIELD),
                rs.getString(USER_NAME_FIELD),
                rs.getString(USER_EMAIL_FIELD),
                rs.getString(USER_PHONE_FIELD),
                rs.getString(USER_FAX_FIELD)
                );
    }
}