package com.picsauditing.provisioning;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.employeeguard.daos.AccountEmployeeGuardDAO;
import com.picsauditing.employeeguard.entities.AccountEmployeeGuard;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.services.entity.ProfileEntityService;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.OperatorAccount;
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

	@Autowired
	private OperatorAccountDAO operatorAccountDAO;
	@Autowired
	private ContractorAccountDAO contractorAccountDAO;

	@Override
	public boolean hasEmployeeGUARD(final Account account) {
		if (account.isContractor()) {
			return contractorHasEmployeeGUARD((ContractorAccount) account);
		}

		if (account.isOperatorCorporate()) {
			return operatorHasEmployeeGUARD((OperatorAccount) account);
		}

		return false;
	}

	@Override
	public boolean hasEmployeeGUARD(final int accountId) {
		return isEmployeeGUARDEnabled(accountId);
	}

	private boolean contractorHasEmployeeGUARD(ContractorAccount contractor) {
		return contractorHasEmployeeGUARDOperator(contractor);
	}

	public boolean operatorHasEmployeeGUARD(final OperatorAccount operator) {
		return isEmployeeGUARDEnabled(operator.getId());
	}

	private boolean contractorHasEmployeeGUARDOperator(ContractorAccount contractor) {
		return isEmployeeGUARDEnabled(contractor.getId());
		// FIXME Find out what is needed for a contractor
//		for (OperatorAccount operator : contractor.getOperatorAccounts()) {
//			if (operatorHasEmployeeGUARD(operator)) {
//				return true;
//			}
//		}

//		return false;
	}

	@Override
	public void addEmployeeGUARD(final int accountId) {
		try {
			accountEmployeeGuardDAO.save(new AccountEmployeeGuard(accountId));
		} catch (Exception e) {
			LOG.warn("Error adding accountId {} to EmployeeGUARD", accountId);
		}
	}

	@Override
	public void removeEmployeeGUARD(final int accountId) {
		try {
			AccountEmployeeGuard accountEmployeeGuard = accountEmployeeGuardDAO.find(accountId);
			if (accountEmployeeGuard != null) {
				accountEmployeeGuardDAO.remove(accountEmployeeGuard);
			}
		} catch (Exception e) {
			LOG.warn("Error removing accountId {} from EmployeeGUARD", accountId);
		}
	}

	private boolean isEmployeeGUARDEnabled(final int accountId) {
		Cache cache = cache();
		if (cache != null) {
			Element element = cache.get(accountId);
			if (element != null) {
				return false;
			}
		}

		AccountEmployeeGuard accountEmployeeGuard = accountEmployeeGuardDAO.find(accountId);
		if (accountEmployeeGuard == null) {
			cacheItem(accountId);
		}

		return accountEmployeeGuard != null;
	}

	private void cacheItem(final int accountId) {
		Cache cache = cache();
		if (cache != null) {
			cache.put(new Element(accountId, accountId));
		} else {
			LOG.info("Missing cache for product subscription service");
		}
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

	@Override
	public boolean isEmployeeGUARDEmployeeUser(final int appUserId) {
		Profile profile = profileEntityService.findByAppUserId(appUserId);
		return profile != null;
	}

	@Override
	public boolean hasEmployeeGuardLegacy(final Permissions permissions) {
		final int accountId = permissions.getAccountId();
		Boolean status = findFromCacheLegacy(accountId);
		if (status != null)
			return status;

		if (permissions.isOperatorCorporate()) {
			return operatorCorporateHasEmployeeGuardLegacy(accountId);
		}

		return contractorHasEmployeeGuardLegacy(accountId);
	}

	@Override
	public void employeeGuardAcquiredLegacy(int accountId) {
		removeCacheItemLegacy(accountId);
	}

	@Override
	public void employeeGuardRemovedLegacy(int accountId) {
		//-- Currently we dirty the cache only.
		removeCacheItemLegacy(accountId);
	}

	private boolean contractorHasEmployeeGuardLegacy(final int accountId) {
		ContractorAccount contractorAccount = contractorAccountDAO.find(accountId);
		Boolean status = (contractorAccount != null && contractorAccount.isHasEmployeeGuard());
		cacheItemLegacy(accountId, status);
		return status;
	}

	private boolean operatorCorporateHasEmployeeGuardLegacy(final int accountId) {
		OperatorAccount operatorAccount = operatorAccountDAO.find(accountId);
		Boolean status = operatorAccount != null && operatorAccount.isRequiresEmployeeGuard();
		cacheItemLegacy(accountId, status);
		return status;
	}

	private void cacheItemLegacy(final Integer key, final Boolean value) {
		Cache cache = cache();
		if (cache != null) {
			cache.put(new Element(key, value));
		} else {
			LOG.warn("Missing cache for product subscription service");
		}
	}

	private void removeCacheItemLegacy(final Integer key) {
		Cache cache = cache();
		if (cache != null) {
			cache.remove(key);
		} else {
			LOG.warn("Missing cache for product subscription service");
		}
	}

	private Boolean findFromCacheLegacy(final int accountId) {
		Cache cache = cache();
		if (cache != null) {
			Element element = cache.get(accountId);
			if (element != null && element.getObjectValue() instanceof Boolean) {
				return (Boolean) element.getObjectValue();
			}
		}

		return null;
	}

}
