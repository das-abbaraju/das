package com.picsauditing.dao.mapper;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import com.picsauditing.util.Strings;
import org.springframework.jdbc.core.RowMapper;

import com.picsauditing.model.i18n.ContextTranslation;

public class ContextTranslationMapper implements RowMapper<ContextTranslation> {

	public static final String MSGKEY_DELIMITER = ".";

	@Override
	public ContextTranslation mapRow(ResultSet rs, int rowNum) throws SQLException {
		ContextTranslation translation = new ContextTranslation();
		translation.setActionName(rs.getString("Context_tp"));
		translation.setMethodName(rs.getString("Context_nm"));
		translation.setFrontEndControlName(rs.getString("Item_tp"));
		translation.setKey(rs.getString("Item_nm"));
		translation.setLocale(rs.getString("Locale_cd"));
		translation.setTranslation(rs.getString("ItemEntry_tx"));
		translation.setLastUsed(rs.getDate("USE_dm"));
		return translation;
	}

	public ContextTranslation mapRowFromAppTranslation(ResultSet rs) throws SQLException {
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
