package com.picsauditing.dao.jdbc;

import com.picsauditing.dao.mapper.AppPropertyRowMapper;
import com.picsauditing.dao.mapper.GenericQueryMapper;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.model.general.AppPropertyProvider;
import com.picsauditing.search.Database;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class JdbcAppPropertyProvider implements AppPropertyProvider {
    private static final Logger logger = LoggerFactory.getLogger(JdbcAppPropertyProvider.class);
    private static final String FIND_APP_PROPERTY_BY_NAME = "SELECT * FROM app_properties WHERE property = ?";
    private static final String cacheName = "app_properties";
    // to inject for test
    private CacheManager cacheManager;

    public JdbcAppPropertyProvider() {
    }

    public String getPropertyString(String appPropertyName) {
        String property = propertyFromCache(appPropertyName);
        if (property == null) {
            property = findAppPropertyFromDatabase(appPropertyName);
            if (property != null) {
                cacheProperty(appPropertyName, property);
            }
        }
        return property;
    }

    private String findAppPropertyFromDatabase(String appPropertyName) {
        try {
            List<AppProperty> appProperties
                    = Database.select(FIND_APP_PROPERTY_BY_NAME,
                                      appPropertyName,
                                      new GenericQueryMapper<String>(),
                                      new AppPropertyRowMapper());
            if (appProperties != null && appProperties.size() > 0) {
                return appProperties.get(0).getValue();
            }
        } catch (Exception e) {
            logger.error("Error finding appProperty {}: {}", appPropertyName, e);
        }
        return null;
    }

    private void cacheProperty(String appPropertyName, String propertyValue) {
        Cache cache = cache();
        if (cache != null) {
            cache.put(new Element(appPropertyName, propertyValue));
        } else {
            logger.error("cache configuration issue - no cache named " + cacheName);
        }
    }

    private String propertyFromCache(String appPropertyName) {
        Cache cache = cache();
        if (cache != null) {
            Element element = cache.get(appPropertyName);
            if (element != null) {
                Object object = element.getObjectValue();
                if (object instanceof String) {
                    return (String) object;
                }
            }
        }
        return null;
    }

    private Cache cache() {
        Cache cache = null;
        CacheManager cacheManager = cacheManager();
        if (cacheManager != null) {
            cache = cacheManager.getCache(cacheName);
        }
        return cache;
    }

    private CacheManager cacheManager() {
        if (cacheManager == null) {
            return CacheManager.getInstance();
        } else {
            return cacheManager;
        }
    }

}