package com.picsauditing.actions;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

public class ClearCacheAction extends PicsActionSupport {

	@Override
	public String execute() throws Exception {
		String[] cacheNames = CacheManager.getInstance().getCacheNames();
	
		for( String cacheName : cacheNames ) {
			System.out.println(cacheName);
			
			Cache cache = CacheManager.getInstance().getCache(cacheName);
			for( Object key : cache.getKeys() ) {
				cache.remove(key);
			}
		}
		
		return SUCCESS;
	}
}
