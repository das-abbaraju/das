package com.picsauditing.actions;

import java.util.ArrayList;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;

@SuppressWarnings("serial")
public class CacheStatistics extends PicsActionSupport {
	private List<Ehcache> caches;
	private List<Object> elementList;
	private String cacheName;
	
	public List<Ehcache> getCaches() {
		if(caches == null) {
			caches = new ArrayList<Ehcache>();
			String[] cacheNames = CacheManager.getInstance().getCacheNames();
	
			for (String cacheName : cacheNames) {
				caches.add(CacheManager.getInstance().getEhcache(cacheName));
			}
		}
		
		return caches;
	}
	
	public int getTotalSize() {
		int sizeSum = 0;

		String[] cacheNames = CacheManager.getInstance().getCacheNames();
		for (String cacheName : cacheNames) {
			sizeSum += CacheManager.getInstance().getEhcache(cacheName).getSize();
		}
		
		return sizeSum;
	}
	
	public long getTotalMemoryUsage() {
		long memorySum = 0;

		String[] cacheNames = CacheManager.getInstance().getCacheNames();
		for (String cacheName : cacheNames) {
			memorySum += CacheManager.getInstance().getEhcache(cacheName).calculateInMemorySize();
		}
		
		return memorySum;
	}
	
	public String getElements() {
		return "CacheElementList";
	}

	@SuppressWarnings("unchecked")
	public List<Object> getElementList() {
		if(elementList == null){
			elementList = new ArrayList<Object>();
			
			Cache cache = CacheManager.getInstance().getCache(cacheName);
			if(cache != null)
				elementList.addAll(cache.getKeys());
		}

		return elementList;
	}

	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}

	public String getCacheName() {
		return cacheName;
	}
}
