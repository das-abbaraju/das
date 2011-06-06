package com.picsauditing.actions;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import com.picsauditing.auditBuilder.AuditCategoryRuleCache;
import com.picsauditing.auditBuilder.AuditTypeRuleCache;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.access.Anonymous;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.jpa.entities.AppProperty;

@SuppressWarnings("serial")
public class ClearCacheAction extends PicsActionSupport {

	AuditTypeRuleCache auditTypeRuleCache;
	AuditCategoryRuleCache auditCategoryRuleCache;
	AppPropertyDAO appPropertyDAO;

	public ClearCacheAction(AuditTypeRuleCache auditTypeRuleCache, AuditCategoryRuleCache auditCategoryRuleCache,
			AppPropertyDAO appPropertyDAO) {
		this.auditTypeRuleCache = auditTypeRuleCache;
		this.auditCategoryRuleCache = auditCategoryRuleCache;
		this.appPropertyDAO = appPropertyDAO;
	}

	@Anonymous
	@Override
	public String execute() throws Exception {
		String[] cacheNames = CacheManager.getInstance().getCacheNames();
		AppProperty appProp = appPropertyDAO.find("clear_cache");
		appProp.setValue("false");
		appPropertyDAO.save(appProp);

		for (String cacheName : cacheNames) {
			System.out.println(cacheName);
			addActionMessage("Cleared cache named " + cacheName);

			Cache cache = CacheManager.getInstance().getCache(cacheName);
			for (Object key : cache.getKeys()) {
				addActionMessage("Cleared object " + key);
				cache.remove(key);
			}
		}

		// The Python Cron monitors the status of the App Property "clear_cache"
		// and if it has been set, resets the cache via this Action Class on all
		// 3 servers
		auditTypeRuleCache.clear();
		auditCategoryRuleCache.clear();

		I18nCache.getInstance().clear();

		return SUCCESS;
	}
}
