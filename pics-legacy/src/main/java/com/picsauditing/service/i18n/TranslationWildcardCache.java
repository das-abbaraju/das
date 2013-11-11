package com.picsauditing.service.i18n;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
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
            Table<String, String, String> localeToKeyToValue = (Table<String, String, String>)element.getObjectValue();
            Map<String, String> keyToTranslation = Collections.unmodifiableMap(localeToKeyToValue.row(requestedLocale));
            if (keyToTranslation != null) {
                for (String msgKey : keyToTranslation.keySet()) {
                    translations.add(new TranslationWrapper.Builder().key(msgKey).locale(requestedLocale).translation(keyToTranslation.get(msgKey)).build());
                }
            }
        }
        return translations;
    }

    public void put(String wildcardKey, String requestedLocale, List<TranslationWrapper> translations) {
        Table<String, String, String> localeToKeyToValue;
        Element element = wildcardCache.get(wildcardKey);
        if (element != null) {
            localeToKeyToValue = (Table<String, String, String>)element.getObjectValue();
        } else {
            localeToKeyToValue = TreeBasedTable.create();
        }

        if (translations == null || translations.isEmpty()) {
            // TODO: is this the right thing to do?
            localeToKeyToValue.put(requestedLocale, "", "");
        } else {
            for (TranslationWrapper translation : translations) {
                localeToKeyToValue.put(requestedLocale, translation.getKey(), translation.getTranslation());
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
