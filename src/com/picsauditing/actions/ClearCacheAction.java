package com.picsauditing.actions;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import com.picsauditing.PICS.AuditCategoryRuleCache;
import com.picsauditing.PICS.AuditTypeRuleCache;

public class ClearCacheAction extends PicsActionSupport {

	AuditTypeRuleCache auditTypeRuleCache;
	AuditCategoryRuleCache auditCategoryRuleCache;

	public ClearCacheAction(AuditTypeRuleCache auditTypeRuleCache, AuditCategoryRuleCache auditCategoryRuleCache) {
		this.auditTypeRuleCache = auditTypeRuleCache;
		this.auditCategoryRuleCache = auditCategoryRuleCache;
	}

	@Override
	public String execute() throws Exception {
		String[] cacheNames = CacheManager.getInstance().getCacheNames();

		for (String cacheName : cacheNames) {
			System.out.println(cacheName);
			addActionMessage("Cleared cache named " + cacheName);

			Cache cache = CacheManager.getInstance().getCache(cacheName);
			for (Object key : cache.getKeys()) {
				addActionMessage("Cleared object " + key);
				cache.remove(key);
			}
		}

		auditTypeRuleCache.clear();
		auditCategoryRuleCache.clear();

		return SUCCESS;
	}
}
