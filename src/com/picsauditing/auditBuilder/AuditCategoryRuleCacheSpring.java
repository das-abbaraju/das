package com.picsauditing.auditBuilder;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;

/**
 * TODO: Experimental class to allow for spring loading the AuditCategoryRuleCache
 */
public class AuditCategoryRuleCacheSpring {

	@Autowired
	private AuditDecisionTableDAO auditDecisionTableDAO;

	private AuditCategoryRuleCache cache;

	public List<AuditCategoryRule> getRules(ContractorAccount contractor, AuditType auditType) {
		if (cache == null) {
			cache = new AuditCategoryRuleCache();
		}
		return cache.getRules(contractor, auditType);
	}

	public void clear() {
		cache.clear();
	}
}
