package com.picsauditing.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.picsauditing.PICS.DBBean;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.DatabaseUtil;
import com.picsauditing.util.SpringUtils;
import org.apache.commons.beanutils.BasicDynaBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.dao.mapper.ContextTranslationMapper;
import com.picsauditing.model.i18n.ContextTranslation;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;


public class AppTranslationDAO extends PicsDAO {

	private static final String I18N_CACHE_QUERY = "SELECT msgKey, locale, msgValue, lastUsed FROM app_translation";

	private final Database database;

	private static final Logger logger = LoggerFactory.getLogger(AppTranslationDAO.class);

	public List<BasicDynaBean> getTranslationsForI18nCache() throws SQLException {
		return database.select(I18N_CACHE_QUERY, false);
	}

	public AppTranslationDAO(Database database) {
		this.database = database;
	}

	@Deprecated
	public List<BasicDynaBean> findTranslationsForJSOldStyle(Set<String> locales) throws SQLException {
		String sql = buildQuery(locales);
		return database.select(sql.toString(), false);
	}

	private String buildQuery(Set<String> locales) {
		SelectSQL sql = new SelectSQL("app_translation");
		sql.addField("msgKey");
		sql.addField("msgValue");
		sql.addWhere("msgKey LIKE 'JS.%'");
		sql.addWhere("locale IN (" + Strings.implodeForDB(locales) + ")");

		// Order in this way fr_CA_Suncor, fr_CA, fr, en
		sql.addOrderBy("CASE locale WHEN 'EN' THEN 1 ELSE 0 END, locale DESC");

		return sql.toString();
	}

	public List<ContextTranslation> findAllForJS() {
		try {
//				return loadTranslationsUsingStoredProc();
				return loadTranslationsFromTableDirectly();
		} catch (Exception e) {
			logger.error("Error while retrieving all the translations for JS.", e);
		}

		return Collections.emptyList();
	}

	private List<ContextTranslation> loadTranslationsUsingStoredProc() throws SQLException {
		return database.select(buildStoredProcedureCall(), new ContextTranslationMapper());
	}

	private List<ContextTranslation> loadTranslationsFromTableDirectly() throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		ContextTranslationMapper rowMapper = new ContextTranslationMapper();

		List<ContextTranslation> results = new ArrayList<ContextTranslation>();
		try {
			connection = DBBean.getDBConnection();
			Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			String sql = "select * from app_translation where js = 1";
			resultSet = statement.executeQuery(sql);

			int row = 0;
			while (resultSet.next()) {
				results.add(rowMapper.mapRowFromAppTranslation(resultSet));
				row++;
			}
		} finally {
			DatabaseUtil.closeResultSet(resultSet);
			DatabaseUtil.closeStatement(preparedStatement);
			DatabaseUtil.closeConnection(connection);
		}

		return results;
	}

	private String buildStoredProcedureCall() {
		return "call	gfpItem_Context_Locale " +
				"(" +
				"	@Item_id 	:= null" +
				",	@Item_tp	:= null" +
				",	@Context_id	:= NULL" +
				",	@Context_tp	:= null" +
				",	@Locale_cd	:= null" +
				",	@ItemEntry_tp	:= null" +
				",	@ItemEntry_tx 	:= null" +
				",	@Item_nm	:= NULL" +
				",	@Item_cd	:= null" +
				",	@Context_nm	:= null" +
				",	@Context_cd	:= null" +
				",	@Language_cd	:= null" +
				",	@Country_cd	:= null" +
				",	@Status_nm	:= null" +
				",	@Item_tx	:= null" +
				",	@EFF_dm		:= null" +
				",	@USE_dm		:= null" +
				",	@Key_cd		:= null" +
				");";
	}

}
