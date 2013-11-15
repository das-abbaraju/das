package com.picsauditing.service.i18n;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import com.picsauditing.model.i18n.TinyTranslation;
import com.picsauditing.model.i18n.TranslationWrapper;
import net.sf.ehcache.*;

import java.util.*;

public class TranslationWildcardCache {
    private static final String WILDCARD_CACHE_NAME = "i18n-wildcards";
    private static Cache wildcardCache;

    static {
        // CacheManager.create returns the existing singleton if it already exists
        CacheManager manager = CacheManager.create();
        wildcardCache = manager.getCache(WILDCARD_CACHE_NAME);
    }

    public List<TranslationWrapper> get(String key, String requestedLocale) {
        List<TranslationWrapper> translations = new ArrayList<>();
        Element element = wildcardCache.get(key);
        if (element != null) {
            Table<String, String, TinyTranslation> localeToKeyToValue = (Table<String, String, TinyTranslation>)element.getObjectValue();
            Map<String, TinyTranslation> keyToTranslation = Collections.unmodifiableMap(localeToKeyToValue.row(requestedLocale));
            if (keyToTranslation != null) {
                for (String msgKey : keyToTranslation.keySet()) {
                    TinyTranslation translation = keyToTranslation.get(msgKey);
                    Set<String> usedOnPages = new HashSet<>();
                    usedOnPages.addAll(translation.usedOnPages);
                    translations.add(new TranslationWrapper.Builder()
                            .key(msgKey)
                            .requestedLocale(requestedLocale)
                            .locale(requestedLocale)
                            .translation(translation.text)
                            .usedOnPages(usedOnPages)
                            .retrievedByCache(true)
                            .build());
                }
            }
        }
        return translations;
    }

    public void put(String wildcardKey, String requestedLocale, List<TranslationWrapper> translations) {
        Table<String, String, TinyTranslation> localeToKeyToValue;
        Element element = wildcardCache.get(wildcardKey);
        if (element != null) {
            localeToKeyToValue = (Table<String, String, TinyTranslation>)element.getObjectValue();
        } else {
            localeToKeyToValue = TreeBasedTable.create();
        }

        if (translations != null && !translations.isEmpty()) {
            for (TranslationWrapper translation : translations) {
                TinyTranslation tinyTranslation = new TinyTranslation();
                tinyTranslation.text = translation.getTranslation();
                tinyTranslation.addToUsedOnPages(translation.getUsedOnPages());
                localeToKeyToValue.put(requestedLocale, translation.getKey(), tinyTranslation);
            }
        }
        wildcardCache.put(new Element(wildcardKey, localeToKeyToValue));
    }

    public void clear() {
        synchronized (this) {
            wildcardCache.removeAll();
        }
    }

}
