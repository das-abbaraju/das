package com.picsauditing.provisioning;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.employeeguard.daos.AccountEmployeeGuardDAO;
import com.picsauditing.employeeguard.entities.AccountEmployeeGuard;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.services.entity.ProfileEntityService;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.web.SessionInfoProviderFactory;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ProductSubscriptionServiceImpl implements ProductSubscriptionService {

	private Logger LOG = LoggerFactory.getLogger(ProductSubscriptionServiceImpl.class);

	@Autowired
	private AccountEmployeeGuardDAO accountEmployeeGuardDAO;
	@Autowired
	private ProfileEntityService profileEntityService;

	private CacheManager cacheManager;


	@Override
	public void addEmployeeGUARD(final int accountId) {
		try {
      removeCacheItem(accountId);

			accountEmployeeGuardDAO.save(new AccountEmployeeGuard(accountId));
		} catch (Exception e) {
			LOG.warn("Error adding accountId {} to EmployeeGUARD", accountId);
		}
	}

	@Override
	public void removeEmployeeGUARD(final int accountId) {
		try {
      removeCacheItem(accountId);

			AccountEmployeeGuard accountEmployeeGuard = accountEmployeeGuardDAO.find(accountId);
			if (accountEmployeeGuard != null) {
				accountEmployeeGuardDAO.remove(accountEmployeeGuard);
			}
		} catch (Exception e) {
			LOG.warn("Error removing accountId {} from EmployeeGUARD", accountId);
		}
	}

	@Override
	public boolean isEmployeeGUARDEmployeeUser(final int appUserId) {
		Profile profile = profileEntityService.findByAppUserId(appUserId);
		return profile != null;
	}

	@Override
	public boolean hasEmployeeGuard(final Permissions permissions) {
		final int accountId = permissions.getAccountId();
		Boolean status = findFromCache(accountId);
		if (status != null)
			return status;

    return anyUserTypeHasEmployeeGuard(accountId);

	}

  private boolean anyUserTypeHasEmployeeGuard(final int accountId){
    Boolean status=false;
    AccountEmployeeGuard accountEmployeeGuard = accountEmployeeGuardDAO.find(accountId);
    if (accountEmployeeGuard != null) {
      status=true;
    }

    cacheItem(accountId, status);
    return status;
  }

	private void cacheItem(final Integer key, final Boolean value) {
		Cache cache = cache();
		if (cache != null) {
			cache.put(new Element(key, value));
		} else {
			LOG.warn("Missing cache for product subscription service");
		}
	}

	private void removeCacheItem(final Integer key) {
		Cache cache = cache();
		if (cache != null) {
			cache.remove(key);
		} else {
			LOG.warn("Missing cache for product subscription service");
		}
	}

	private Boolean findFromCache(final int accountId) {
		Cache cache = cache();
		if (cache != null) {
			Element element = cache.get(accountId);
			if (element != null && element.getObjectValue() instanceof Boolean) {
				return (Boolean) element.getObjectValue();
			}
		}

		return null;
	}

  private Cache cache() {
    cacheManager = cacheManager();
    Cache cache = cacheManager.getCache(CACHE_NAME);
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
