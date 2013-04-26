package com.picsauditing.dao.mapper;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import org.springframework.jdbc.core.RowMapper;

import com.picsauditing.model.i18n.ContextTranslation;

public class ContextTranslationMapper implements RowMapper<ContextTranslation> {

	@Override
	public ContextTranslation mapRow(ResultSet rs, int rowNum) throws SQLException {
		ContextTranslation translation = new ContextTranslation();
		translation.setActionName(rs.getString("Context_tp"));
		translation.setMethodName(rs.getString("Context_nm"));
		translation.setFrontEndControlName(rs.getString("Item_tp"));
		translation.setKey(rs.getString("Item_nm"));
		translation.setLocale(rs.getString("Locale_cd"));
		translation.setTranslation(rs.getString("ItemEntry_tx"));
		translation.setLastUsed(new java.util.Date());
		return translation;
	}

	private java.util.Date convertToJavaDate(Date sqlDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(sqlDate.getTime());
		return calendar.getTime();
	}

}
