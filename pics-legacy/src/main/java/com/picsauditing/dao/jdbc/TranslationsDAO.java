package com.picsauditing.dao.jdbc;

import com.picsauditing.PICS.DBBean;
import com.picsauditing.dao.TranslationDAO;
import com.picsauditing.dao.mapper.LegacyTranslationMapper;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.model.i18n.ContextTranslation;
import com.picsauditing.model.i18n.TranslationWrapper;
import com.picsauditing.search.Database;
import com.picsauditing.search.QueryMapper;
import com.picsauditing.util.DatabaseUtil;
import com.picsauditing.util.Strings;
import org.apache.commons.beanutils.BasicDynaBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.util.CollectionUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TranslationsDAO implements TranslationDAO {
    private static final Logger logger = LoggerFactory.getLogger(TranslationsDAO.class);

    private static final String I18N_CACHE_QUERY = "SELECT k.msgKey, l.locale, l.msgValue, l.lastUsed FROM msg_key k join msg_locale l on l.keyID = k.id";
    private static final String REMOVE_TRANSLATIONS_BY_KEY = "DELETE FROM msg_key WHERE msgKey IN (%s)";
    private static final String SELECT_KEYIDS_BY_KEY = "SELECT id FROM msg_key WHERE msgKey IN (%s)";
    private static final String REMOVE_TRANSLATIONS_FROM_LOCALE_BY_KEYID = "DELETE FROM msg_locale WHERE keyID IN (%s)";
    private static final String UPDATE_LAST_USED_DATE = "UPDATE msg_key SET lastUsed = NOW() WHERE msgKey = '%s'";
    private static final String SAVE_TRANSLATION_KEY = "INSERT INTO msg_key (msgKey, createdBy, updatedBy, creationDate, updateDate, lastUsed) "
            + "VALUES ('%s', %s, %s, NOW(), NOW(), DATE(NOW())) ON DUPLICATE KEY UPDATE updateDate = NOW(), updatedBy = %s";
    private static final String SAVE_TRANSLATION_LOCALE = "INSERT INTO msg_locale (keyID, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, firstUsed, lastUsed) "
            + "VALUES (?, ?, ?, ?, ?, NOW(), NOW(), DATE(NOW()), DATE(NOW())) ON DUPLICATE KEY UPDATE msgValue = ?, updateDate = NOW(), updatedBy = ?";
    private static final String SELECT_ALL_JS_TRANSLATIONS = "select mk.msgKey as msgKey, ml.locale as locale, ml.msgValue as msgValue, mk.lastUsed as lastUsed " +
            "from msg_key mk join msg_locale ml on ml.keyID = mk.id where mk.js = 1";


    private static Database database = new Database();

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
    public void updateTranslationLastUsed(String key) {
        Connection connection = null;
        try {
            connection = DBBean.getTranslationsConnection();
            database.execute(connection, String.format(UPDATE_LAST_USED_DATE, Strings.escapeQuotesAndSlashes(key)));
        } catch (Exception e) {
            logger.error("Error updating the last used by date for key {}: {}", key, e);
            throw new RuntimeException("Failed to reset lastUsed on msg_key because: " + e.getMessage());
        } finally {
            DatabaseUtil.closeConnection(connection);
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
