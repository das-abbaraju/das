package com.picsauditing.i18n.service.cache;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import com.picsauditing.i18n.model.TinyTranslation;
import com.picsauditing.i18n.model.TranslationWrapper;
import net.sf.ehcache.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class TranslationCache {
    private Logger logger = LoggerFactory.getLogger(TranslationCache.class);

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
            logger.debug("Complete cache miss for {} in {}", key, requestedLocale);
            return null;
        } else {
            Table<String, String, TinyTranslation> requestedlocaleToTextToReturnedLocale = doGet(element);
            if (!requestedlocaleToTextToReturnedLocale.containsRow(requestedLocale)) {
                logger.debug("Requested local cache miss for {} in {}", key, requestedLocale);
                return null;
            } else {
                logger.debug("Cache hit for {} in {}", key, requestedLocale);
                return translationFrom(key, requestedLocale, requestedlocaleToTextToReturnedLocale);
            }
        }
    }

    public Table<String, String, TinyTranslation> get(String key) {
        Element element = cache.get(key);
        return doGet(element);
    }

    private Table<String, String, TinyTranslation> doGet(Element element) {
        Table<String, String, TinyTranslation> requestedlocaleToReturnedLocaleToText;
        if (element != null && element.getObjectValue() != null) {
            requestedlocaleToReturnedLocaleToText =  (Table<String, String, TinyTranslation>) element.getObjectValue();
        } else {
            requestedlocaleToReturnedLocaleToText = TreeBasedTable.create();
        }
        return requestedlocaleToReturnedLocaleToText;
    }

    public void put(TranslationWrapper translation) {
        Table<String, String, TinyTranslation> requestedlocaleToReturnedLocaleToText = doGet(cache.get(translation.getKey()));
        TinyTranslation tinyTranslation = new TinyTranslation();
        tinyTranslation.text = translation.getTranslation();
        tinyTranslation.addToUsedOnPages(translation.getUsedOnPages());
        requestedlocaleToReturnedLocaleToText.put(translation.getRequestedLocale(), translation.getLocale(), tinyTranslation);
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

    private TranslationWrapper translationFrom(String key, String requestedLocale, Table<String, String, TinyTranslation> requestedlocaleToReturnedLocaleToText) {
        Map<String,TinyTranslation> returnedLocaleToText = requestedlocaleToReturnedLocaleToText.row(requestedLocale);
        if (returnedLocaleToText != null && returnedLocaleToText.keySet() != null && returnedLocaleToText.keySet().iterator().hasNext()) {
            String returnedLocale = returnedLocaleToText.keySet().iterator().next();
            TinyTranslation translation = returnedLocaleToText.get(returnedLocale);
            Set<String> usedOnPages = new HashSet<>();
            usedOnPages.addAll(translation.usedOnPages);
            return new TranslationWrapper.Builder()
                    .key(key)
                    .locale(returnedLocale)
                    .requestedLocale(requestedLocale)
                    .translation(translation.text)
                    .usedOnPages(usedOnPages)
                    .retrievedByCache(true)
                    .build();
        } else {
            logger.error("there is no returnedLocale for {} in {}", key, requestedLocale);
            return null;
        }
    }

}
