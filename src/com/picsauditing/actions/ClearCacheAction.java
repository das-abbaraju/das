package com.picsauditing.actions;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.access.Anonymous;
import com.picsauditing.auditBuilder.AuditCategoryRuleCache;
import com.picsauditing.auditBuilder.AuditTypeRuleCache;
import com.picsauditing.dao.AppPropertyDAO;

@SuppressWarnings("serial")
public class ClearCacheAction extends PicsActionSupport {
	public static String CLEAR_CACHE_PROPERTY = "PICS.clear_cache";

	@Autowired
	private AuditTypeRuleCache auditTypeRuleCache;
	@Autowired
	private AuditCategoryRuleCache auditCategoryRuleCache;
	@Autowired
	private AppPropertyDAO appPropertyDAO;

	private SimpleDateFormat databaseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private final Logger logger = LoggerFactory.getLogger(ClearCacheAction.class);
	@Anonymous
	@Override
	public String execute() throws Exception {
		String[] cacheNames = CacheManager.getInstance().getCacheNames();

		for (String cacheName : cacheNames) {
			logger.info(cacheName);
			addActionMessage("Cleared cache named " + cacheName);

			Cache cache = CacheManager.getInstance().getCache(cacheName);
			for (Object key : cache.getKeys()) {
				addActionMessage("Cleared object " + key);
				cache.remove(key);
			}
		}

		appPropertyDAO.setProperty(CLEAR_CACHE_PROPERTY, databaseFormat.format(new Date()));

		// The Python Cron monitors the status of the App Property "clear_cache"
		// and if it has been set, resets the cache via this Action Class on all
		// 3 servers
		auditTypeRuleCache.clear();
		auditCategoryRuleCache.clear();

		// Clear the translations from the cache
		I18nCache.getInstance().clear();

		// Clear the config environment settings
		PicsActionSupport.CONFIG = null;

		return SUCCESS;
	}

	@Anonymous
	public String monitor() {
		String property = appPropertyDAO.getProperty(CLEAR_CACHE_PROPERTY);
		Date lastClearDoneOnI18nCache = I18nCache.getLastCleared();
		Date lastClearCommandIssued = null;
		
		try {
			lastClearCommandIssued = databaseFormat.parse(property);
		} catch (Exception e) {
			lastClearCommandIssued = new Date();
		}

		if (lastClearDoneOnI18nCache == null || lastClearDoneOnI18nCache.before(lastClearCommandIssued)) {
			output = "CLEAR";
		} else {
			output = "OK";
		}

		return PLAIN_TEXT;
	}
}
