package com.picsauditing.actions;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RequiredPermission;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;

@SuppressWarnings("serial")
public class CacheStatistics extends PicsActionSupport {
    private static final String CACHE_MANAGER_NAME = "TranslateCacheManager";
    private List<Ehcache> caches;
    private List<Object> elementList;
    private String cacheName;
    private List<String> selectedCacheNames = new ArrayList<>();

    @Override
    @RequiredPermission(value = OpPerms.DevelopmentEnvironment)
    public String execute() throws Exception {
        return SUCCESS;
    }

    public List<String> getCacheNames() {
        List<String> allCacheNames = new ArrayList<>();
        for (String cacheName : CacheManager.getInstance().getCacheNames()) {
            allCacheNames.add(cacheName);
        }
        CacheManager manager = CacheManager.getCacheManager(CACHE_MANAGER_NAME);
        if (manager != null) {
            for (String cacheName : manager.getCacheNames()) {
                allCacheNames.add(cacheName);
            }
        }
        return allCacheNames;
    }

    private String[] cacheNames;

    public List<String> getSelectedCacheNames() {
        return selectedCacheNames;
    }

    public void setSelectedCacheNames(List<String> selectedCacheNames) {
        this.selectedCacheNames = selectedCacheNames;
    }

    public List<Ehcache> getCaches() {
		if(caches == null) {
			caches = new ArrayList<Ehcache>();
			String[] cacheNames = CacheManager.getInstance().getCacheNames();
	
			for (String cacheName : selectedCacheNames) {
                Ehcache cache = CacheManager.getInstance().getEhcache(cacheName);
                if (cache != null) {
                    caches.add(cache);
                } else {
                    caches.add(CacheManager.getCacheManager(CACHE_MANAGER_NAME).getCache(cacheName));
                }
            }
		}
		
		return caches;
	}
	
	public int getTotalSizeOfSelectedCaches() {
		int sizeSum = 0;

		String[] cacheNames = CacheManager.getInstance().getCacheNames();
        for (String cacheName : selectedCacheNames) {
            Ehcache cache = CacheManager.getInstance().getEhcache(cacheName);
            if (cache == null) {
                cache = CacheManager.getCacheManager(CACHE_MANAGER_NAME).getCache(cacheName);
            }
			sizeSum += cache.getSize();
		}
		return sizeSum;
	}
	
	public long getTotalMemoryUsageOfSelectedCaches() {
		long memorySum = 0;

		String[] cacheNames = CacheManager.getInstance().getCacheNames();
		for (String cacheName : selectedCacheNames) {
            Ehcache cache = CacheManager.getInstance().getEhcache(cacheName);
            if (cache == null) {
                cache = CacheManager.getCacheManager(CACHE_MANAGER_NAME).getCache(cacheName);
            }
			memorySum += cache.calculateInMemorySize();
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
