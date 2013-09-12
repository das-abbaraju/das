package com.picsauditing.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.picsauditing.model.i18n.ContextTranslation;
import com.picsauditing.util.Strings;

public class LegacyTranslationMapper implements RowMapper<ContextTranslation> {

	public static final String MSGKEY_DELIMITER = "\\.";

	@Override
	public ContextTranslation mapRow(ResultSet rs, int rowNum) throws SQLException {
		ContextTranslation translation = new ContextTranslation();

		setPropertiesBasedOnMsgKey(rs, translation);

		translation.setLocale(rs.getString("locale"));
		translation.setTranslation(rs.getString("msgValue"));
		translation.setLastUsed(rs.getDate("lastUsed"));
		return translation;

	}

	private void setPropertiesBasedOnMsgKey(ResultSet rs, ContextTranslation translation) throws SQLException {
		String msgKey = rs.getString("msgKey");
		if (msgKey == null) {
			translation.setActionName(Strings.EMPTY_STRING);
			translation.setMethodName(Strings.EMPTY_STRING);
			translation.setFrontEndControlName(Strings.EMPTY_STRING);
			translation.setKey(Strings.EMPTY_STRING);
			return;
		}

		String[] msgKeyArray = msgKey.split(MSGKEY_DELIMITER);
		translation.setActionName(getActionName(msgKeyArray));
		translation.setMethodName(getMethodName(msgKeyArray));
		translation.setFrontEndControlName(getFrontEndControlName(msgKeyArray));
		translation.setKey(getKey(msgKeyArray));
	}

	private String getActionName(String[] msgKeyArray) {
		return msgKeyArray[0];
	}

	private String getMethodName(String[] msgKeyArray) {
		return msgKeyArray[1];
	}

	private String getFrontEndControlName(String[] msgKeyArray) {
		return msgKeyArray[2];
	}

	private String getKey(String[] msgKeyArray) {
		StringBuilder builder = new StringBuilder();
		for (int i = 3; i < msgKeyArray.length; i++) {
			builder.append(msgKeyArray[i]);
		}
		return builder.toString();
	}

}
