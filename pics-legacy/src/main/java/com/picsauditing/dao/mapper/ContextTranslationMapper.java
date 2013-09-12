package com.picsauditing.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

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
		translation.setLastUsed(rs.getDate("USE_dm"));
		return translation;
	}

}
