package com.picsauditing.provisioning;

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

	final static String CACHE_NAME = "product_subscription";

	@Autowired
	private AccountEmployeeGuardDAO accountEmployeeGuardDAO;
	@Autowired
	private ProfileEntityService profileEntityService;

	private CacheManager cacheManager;

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
		accountEmployeeGuardDAO.save(new AccountEmployeeGuard(accountId));
	}

	@Override
	public void removeEmployeeGUARD(final int accountId) {
		AccountEmployeeGuard accountEmployeeGuard = accountEmployeeGuardDAO.find(accountId);
		accountEmployeeGuardDAO.remove(accountEmployeeGuard);
	}

	@Override
	public boolean isEmployeeGUARDEmployeeUser(final int appUserId) {
		Profile profile = profileEntityService.findByAppUserId(appUserId);
		return profile != null;
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
		CacheManager cacheManager = cacheManager();
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
