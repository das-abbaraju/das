package com.picsauditing.dao;

import com.picsauditing.model.i18n.ContextTranslation;
import org.apache.commons.beanutils.BasicDynaBean;

import java.sql.SQLException;
import java.util.List;

public interface TranslationDAO {
    List<BasicDynaBean> getTranslationsForI18nCache() throws SQLException;

    void updateTranslationLastUsed(String key);

    List<ContextTranslation> findAllForJS();

    void removeTranslations(List<String> keys) throws Exception;

    @SuppressWarnings("static-access")
    void saveTranslation(String key, String translation, List<String> requiredLanguages) throws Exception;
}
