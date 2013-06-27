package com.picsauditing.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BasicDynaBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.picsauditing.dao.mapper.ContextTranslationMapper;
import com.picsauditing.dao.mapper.LegacyTranslationMapper;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.model.i18n.ContextTranslation;
import com.picsauditing.model.i18n.TranslationWrapper;
import com.picsauditing.search.Database;
import com.picsauditing.search.QueryMapper;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;

public class AppTranslationDAO extends PicsDAO {

	private static final String I18N_CACHE_QUERY = "SELECT msgKey, locale, msgValue, lastUsed FROM app_translation";
	private static final String REMOVE_TRANSLATIONS_BY_KEY = "DELETE FROM app_translation WHERE msgKey IN (%s)";
	private static final String SAVE_TRANSLATIONS = "INSERT INTO app_translation (msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, lastUsed) "
			+ "VALUES (?, ?, ?, ?, ?, NOW(), NOW(), DATE(NOW())) ON DUPLICATE KEY UPDATE msgValue = ?, updateDate = NOW(), updatedBy = ?";

	private static Database database = new Database();

	private static final Logger logger = LoggerFactory.getLogger(AppTranslationDAO.class);

	public List<BasicDynaBean> getTranslationsForI18nCache() throws SQLException {
		return database.select(I18N_CACHE_QUERY, false);
	}

	@Deprecated
	public List<BasicDynaBean> findTranslationsForJSOldStyle(Set<String> locales) throws SQLException {
		String sql = buildQuery(locales);
		return database.select(sql.toString(), false);
	}

	public void updateTranslationLastUsed(String key) {
		try {
			String sql = "UPDATE app_translation SET lastUsed = NOW() WHERE msgKey = '" + Strings.escapeQuotes(key)
					+ "'";
			database.execute(sql);
		} catch (Exception e) {
			logger.error("Error updating the last used by date for key {}.", key, e);
			throw new RuntimeException("Failed to reset lastUsed on app_translation because: " + e.getMessage());
		}
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
			// return loadTranslationsUsingStoredProc();q
			return loadTranslationsFromTableDirectly();
		} catch (Exception e) {
			logger.error("Error while retrieving all the translations for JS.", e);
		}

		return Collections.emptyList();
	}

	public void removeTranslations(List<String> keys) throws Exception {
		String query = String.format(REMOVE_TRANSLATIONS_BY_KEY, Strings.implodeForDB(keys));
		database.execute(query);
	}

	@SuppressWarnings("unused")
	private List<ContextTranslation> loadTranslationsUsingStoredProc() throws SQLException {
		return database.select(buildStoredProcedureCall(), new ContextTranslationMapper());
	}

	private List<ContextTranslation> loadTranslationsFromTableDirectly() throws SQLException {
		String sql = "select * from app_translation where js = 1";
		LegacyTranslationMapper rowMapper = new LegacyTranslationMapper();
		return database.select(sql, rowMapper);
	}

	private String buildStoredProcedureCall() {
		return "call	gfpItem_Context_Locale " + "(" + "	@Item_id 	:= null" + ",	@Item_tp	:= null"
				+ ",	@Context_id	:= NULL" + ",	@Context_tp	:= null" + ",	@Locale_cd	:= null"
				+ ",	@ItemEntry_tp	:= null" + ",	@ItemEntry_tx 	:= null" + ",	@Item_nm	:= NULL" + ",	@Item_cd	:= null"
				+ ",	@Context_nm	:= null" + ",	@Context_cd	:= null" + ",	@Language_cd	:= null"
				+ ",	@Country_cd	:= null" + ",	@Status_nm	:= null" + ",	@Item_tx	:= null" + ",	@EFF_dm		:= null"
				+ ",	@USE_dm		:= null" + ",	@Key_cd		:= null" + ");";
	}

	@SuppressWarnings("static-access")
	public void saveTranslation(String key, String translation, List<String> requiredLanguages) throws Exception {
		if (CollectionUtils.isEmpty(requiredLanguages)) {
			return;
		}

		List<TranslationWrapper> translations = new ArrayList<>();
		for (String language : requiredLanguages) {
			translations.add(new TranslationWrapper.Builder().key(key).translation(translation).locale(language)
					.createdBy(User.SYSTEM).updatedBy(User.SYSTEM).build());
		}

		database.executeBatch(SAVE_TRANSLATIONS, translations, buildQueryMapper());
	}

	private QueryMapper<TranslationWrapper> buildQueryMapper() {
		return new QueryMapper<TranslationWrapper>() {

			@Override
			public void mapObjectToPreparedStatement(TranslationWrapper translationWrapper,
					PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setString(1, translationWrapper.getKey());
				preparedStatement.setString(2, translationWrapper.getLocale());
				preparedStatement.setString(3, translationWrapper.getTranslation());
				preparedStatement.setInt(4, translationWrapper.getCreatedBy());
				preparedStatement.setInt(5, translationWrapper.getUpdatedBy());
				preparedStatement.setString(6, translationWrapper.getTranslation());
				preparedStatement.setInt(7, translationWrapper.getUpdatedBy());
			}
		};
	}
}
