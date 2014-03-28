package com.picsauditing.dao.mapper;

import com.picsauditing.model.contractor.AccountContact;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountContactMapper implements RowMapper<AccountContact> {
    private static final String USER_ID_FIELD = "id";
    private static final String USER_NAME_FIELD = "name";
    private static final String USER_EMAIL_FIELD = "email";
    private static final String USER_PHONE_FIELD = "phone";
    private static final String USER_FAX_FIELD = "fax";

    @Override
    public AccountContact mapRow(ResultSet rs, int rowNum) throws SQLException {
        AccountContact accountContact = new AccountContact();
        accountContact.setId(rs.getInt(USER_ID_FIELD));
        accountContact.setName(rs.getString(USER_NAME_FIELD));
        accountContact.setEmail(rs.getString(USER_EMAIL_FIELD));
        accountContact.setPhone(rs.getString(USER_PHONE_FIELD));
        accountContact.setFax(rs.getString(USER_FAX_FIELD));
        return accountContact;
    }
}