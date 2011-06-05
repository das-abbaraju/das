package com.picsauditing.auditBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.Test;

import com.picsauditing.EntityFactory;
import com.picsauditing.auditBuilder.AuditTypesBuilder.AuditTypeDetail;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.OperatorAccount;

public class AuditBuilderTest extends TestCase {
	@Test
	public void testBuilder() {
		ContractorAccount contractor = EntityFactory.makeContractor();
		ContractorAudit conAudit = EntityFactory.makeContractorAudit(1, contractor);
		AuditType pqf = conAudit.getAuditType();
		AuditCategory category1 = EntityFactory.addCategories(pqf, 101, "PQF Category 1");
		AuditCategory category2 = EntityFactory.addCategories(pqf, 102, "PQF Category 2");

		AuditTypeRuleCache auditRuleCache = new AuditTypeRuleCache();
		AuditTypesBuilder builder1 = new AuditTypesBuilder(auditRuleCache, contractor);
		Set<AuditTypeDetail> auditTypes = builder1.calculate();
		assertEquals(0, auditTypes.size());

		AuditCategoryRuleCache categoryRuleCache = new AuditCategoryRuleCache();
		List<AuditCategoryRule> rules = new ArrayList<AuditCategoryRule>();
		rules.add(new AuditCategoryRule());
		categoryRuleCache.initialize(rules);

		AuditCategoriesBuilder builder2 = new AuditCategoriesBuilder(categoryRuleCache, contractor);
		List<OperatorAccount> auditOperators = new ArrayList<OperatorAccount>();

		Set<AuditCategory> categories = builder2.calculate(conAudit, auditOperators);
		// We don't create categories when there's no operator
		assertEquals(0, categories.size());

		OperatorAccount operator1 = EntityFactory.makeOperator();
		auditOperators.add(operator1);
		EntityFactory.addContractorOperator(contractor, operator1);

		categories = builder2.calculate(conAudit, auditOperators);
		assertEquals(2, categories.size());

		{
			AuditCategoryRule rule = new AuditCategoryRule();
			rules.add(rule);
			rule.setInclude(false);
			rule.setAuditCategory(category2);

			categoryRuleCache.initialize(rules);
			categories = builder2.calculate(conAudit, auditOperators);
			assertEquals(1, categories.size());
		}
		{
			AuditCategoryRule rule = new AuditCategoryRule();
			rules.add(rule);
			rule.setInclude(true);
			rule.setAuditCategory(category2);
			rule.setOperatorAccount(operator1);

			categoryRuleCache.initialize(rules);
			categories = builder2.calculate(conAudit, auditOperators);
			assertEquals(2, categories.size());
		}
	}
}
