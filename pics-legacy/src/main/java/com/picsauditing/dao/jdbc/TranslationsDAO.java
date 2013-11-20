package com.picsauditing.dao.jdbc;

import com.picsauditing.PICS.DBBean;
import com.picsauditing.dao.TranslationDAO;
import com.picsauditing.dao.mapper.LegacyTranslationMapper;
import com.picsauditing.i18n.model.TranslationWrapper;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.model.i18n.ContextTranslation;
import com.picsauditing.search.Database;
import com.picsauditing.search.QueryMapper;
import com.picsauditing.util.DatabaseUtil;
import com.picsauditing.util.Strings;
import org.apache.commons.beanutils.BasicDynaBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.CollectionUtils;

import java.sql.*;
import java.util.*;

public class TranslationsDAO implements TranslationDAO {
    private static final Logger logger = LoggerFactory.getLogger(TranslationsDAO.class);

    private static final String I18N_CACHE_QUERY = "SELECT k.msgKey, l.locale, l.msgValue, l.lastUsed FROM msg_key k join msg_locale l on l.keyID = k.id";

    private static final String SELECT_KEYIDS_BY_KEY = "SELECT id FROM msg_key WHERE msgKey IN (%s)";

    private static final String SELECT_KEYID_LOCALEID_BY_KEY_LOCALE = "SELECT k.id AS keyID, l.id AS localeID FROM msg_key k JOIN msg_locale l on l.keyID = k.id WHERE k.msgKey = :key AND l.locale = :locale";

    private static final String REMOVE_TRANSLATIONS_BY_KEY = "DELETE FROM msg_key WHERE msgKey IN (%s)";

    private static final String REMOVE_TRANSLATIONS_FROM_LOCALE_BY_KEYID = "DELETE FROM msg_locale WHERE keyID IN (%s)";

    private static final String INSERT_LAST_USED =
            "INSERT INTO translation_usage (keyID, localeID, pageName, environment, firstUsed, lastUsed, synchronizedBatch) " +
            "VALUES (:keyID, :localeID, :pageName, :environment, now(), now(), 'DIRECT INSERT') " +
            "ON DUPLICATE KEY UPDATE lastUsed = NOW(), synchronizedBatch = 'DIRECT INSERT', synchronizedDate = null";

    private static final String SAVE_TRANSLATION_KEY = "INSERT INTO msg_key (msgKey, createdBy, updatedBy, creationDate, updateDate, lastUsed) "
            + "VALUES ('%s', %s, %s, NOW(), NOW(), DATE(NOW())) ON DUPLICATE KEY UPDATE updateDate = NOW(), updatedBy = %s";
    private static final String SAVE_TRANSLATION_LOCALE = "INSERT INTO msg_locale (keyID, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, firstUsed, lastUsed) "
            + "VALUES (?, ?, ?, ?, ?, NOW(), NOW(), DATE(NOW()), DATE(NOW())) ON DUPLICATE KEY UPDATE msgValue = ?, updateDate = NOW(), updatedBy = ?";
    private static final String SELECT_ALL_JS_TRANSLATIONS = "select mk.msgKey as msgKey, ml.locale as locale, ml.msgValue as msgValue, mk.lastUsed as lastUsed " +
            "from msg_key mk join msg_locale ml on ml.keyID = mk.id where mk.js = 1";


    private static Database database = new Database();
    // for test injection
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<BasicDynaBean> getTranslationsForI18nCache() throws SQLException {
        Connection connection = null;
        try {
            connection = DBBean.getTranslationsConnection();
            return database.select(connection, I18N_CACHE_QUERY, false);
        } finally {
            DatabaseUtil.closeConnection(connection);
        }
    }

    @Override
    public void updateTranslationLastUsed(String key, String locale, String pageName, String environment) {
        try {
            NamedParameterJdbcTemplate jdbcTemplate = namedParameterJdbcTemplate();
            MapSqlParameterSource paramSource = new MapSqlParameterSource();
            paramSource.addValue("key", key);
            paramSource.addValue("locale", locale);
            Map ids = jdbcTemplate.queryForMap(SELECT_KEYID_LOCALEID_BY_KEY_LOCALE, paramSource);

            Map<String, Object> params = new HashMap<>();
            params.put("keyID", ids.get("keyID"));
            params.put("localeID", ids.get("localeID"));
            params.put("pageName", pageName);
            params.put("environment", environment);

            jdbcTemplate.update(INSERT_LAST_USED, params);
        } catch (Exception e) {
            logger.error("Error updating the last used by date for key {} in locale {}: {}", new Object[] {key, locale, e});
            throw new RuntimeException("Failed to reset lastUsed on msg_key because: " + e.getMessage());
        }
    }

    // for test injection
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate() throws SQLException {
        if (namedParameterJdbcTemplate == null) {
            return new NamedParameterJdbcTemplate(DBBean.getTranslationsDataSource());
        } else {
            return namedParameterJdbcTemplate;
        }
    }

    @Override
    public List<ContextTranslation> findAllForJS() {
        LegacyTranslationMapper rowMapper = new LegacyTranslationMapper();
        Connection connection = null;
        try {
            connection = DBBean.getTranslationsConnection();
            return database.select(connection, SELECT_ALL_JS_TRANSLATIONS, rowMapper);
        } catch (Exception e) {
            logger.error("Error finding JS translations: {}", e);
            throw new RuntimeException("Failed to reset lastUsed on msg_key because: " + e.getMessage());
        } finally {
            DatabaseUtil.closeConnection(connection);
        }
    }

    @Override
    public void removeTranslations(List<String> keys) throws Exception {
        Connection connection = null;
        try {
            connection = DBBean.getTranslationsConnection();
            SingleColumnRowMapper<Integer> rowMapper = new SingleColumnRowMapper<>();
            String query1 = String.format(SELECT_KEYIDS_BY_KEY, Strings.implodeForDB(keys));
            List<Integer> msgIDs = database.select(connection, query1, rowMapper);
            if (!CollectionUtils.isEmpty(msgIDs)) {
                String query2 = String.format(REMOVE_TRANSLATIONS_FROM_LOCALE_BY_KEYID, Strings.implodeForDB(msgIDs));
                database.execute(connection, query2);
                String query3 = String.format(REMOVE_TRANSLATIONS_BY_KEY, Strings.implodeForDB(keys));
                database.execute(connection, query3);
            }
        } finally {
            DatabaseUtil.closeConnection(connection);
        }
    }

    @Override
    public void saveTranslation(String key, String translation, List<String> requiredLanguages) throws Exception {
        if (CollectionUtils.isEmpty(requiredLanguages)) {
            return;
        }

        Connection connection = null;
        try {
            connection = DBBean.getTranslationsConnection();

            long id = database.executeInsert(connection,
                        String.format(SAVE_TRANSLATION_KEY,
                                Strings.escapeQuotesAndSlashes(key),
                                User.SYSTEM,
                                User.SYSTEM,
                                User.SYSTEM));

            List<TranslationWrapper> translations = new ArrayList<>();
            for (String language : requiredLanguages) {
                translations.add(new TranslationWrapper.Builder().keyID(new Long(id).intValue()).key(key).translation(translation).locale(language)
                        .createdBy(User.SYSTEM).updatedBy(User.SYSTEM).build());
            }

            database.executeBatch(connection, SAVE_TRANSLATION_LOCALE, translations, buildQueryMapperFoLocales());
        } finally {
            DatabaseUtil.closeConnection(connection);
        }
    }

    private QueryMapper<TranslationWrapper> buildQueryMapperFoLocales() {
        return new QueryMapper<TranslationWrapper>() {

            @Override
            public void mapObjectToPreparedStatement(TranslationWrapper translationWrapper,
                                                     PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setInt(1, translationWrapper.getKeyID());
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
