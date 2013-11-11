package com.picsauditing.service.i18n;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import com.picsauditing.model.i18n.TranslationWrapper;
import net.sf.ehcache.*;

import java.util.Map;

public class TranslationCache {
    private static final String CACHE_NAME = "i18n";

    private static Cache cache;

    static {
        // CacheManager.create returns the existing singleton if it already exists
        CacheManager manager = CacheManager.create();
        cache = manager.getCache(CACHE_NAME);
    }

    public TranslationWrapper get(String key, String requestedLocale) {
        Element element = cache.get(key);
        if (element == null) {
            return null;
        } else {
            Table<String, String, String> requestedlocaleToTextToReturnedLocale = get(key);
            if (!requestedlocaleToTextToReturnedLocale.containsRow(requestedLocale)) {
                return null;
            } else {
                return translationFrom(key, requestedLocale, requestedlocaleToTextToReturnedLocale);
            }
        }
    }

    public Table<String, String, String> get(String key) {
        Element element = cache.get(key);
        Table<String, String, String> requestedlocaleToReturnedLocaleToText = TreeBasedTable.create();
        if (element != null && element.getObjectValue() != null) {
            requestedlocaleToReturnedLocaleToText =  (Table<String, String, String>) element.getObjectValue();
        }
        return requestedlocaleToReturnedLocaleToText;
    }

    public void put(TranslationWrapper translation) {
        Table<String, String, String> requestedlocaleToReturnedLocaleToText = TreeBasedTable.create();
        requestedlocaleToReturnedLocaleToText.put(translation.getRequestedLocale(), translation.getLocale(), translation.getTranslation());
        Element element = new Element(translation.getKey(), requestedlocaleToReturnedLocaleToText);
        cache.put(element);
    }

    public void remove(String key) {
        cache.remove(key);
    }

    public void clear() {
        synchronized (this) {
            cache.removeAll();
        }
    }

    private TranslationWrapper translationFrom(String key, String requestedLocale, Table<String, String, String> requestedlocaleToReturnedLocaleToText) {
        Map<String,String> returnedLocaleToText = requestedlocaleToReturnedLocaleToText.row(requestedLocale);
        String returnedLocale = returnedLocaleToText.keySet().iterator().next();
        String translation = returnedLocaleToText.get(returnedLocale);
        return new TranslationWrapper.Builder()
                .key(key)
                .locale(returnedLocale)
                .requestedLocale(requestedLocale)
                .translation(translation)
                .build();
    }

}
