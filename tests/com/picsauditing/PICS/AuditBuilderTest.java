package com.picsauditing.PICS;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.EntityFactory;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeRule;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.OperatorAccount;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class AuditBuilderTest {
	@Autowired
	ContractorAccountDAO contractoraccountDAO;
	@Autowired
	AuditDecisionTableDAO auditDecisionTableDAO;
	@Autowired
	AuditCategoryRuleCache auditCategoryRuleCache;
	@Autowired
	AuditTypeDAO auditTypeDAO;

	@SuppressWarnings("serial")
	@Test
	public void testRules() throws Exception {
		int[] cons = { 3, 11384, 81 };
		int[] audits = { 1, 11 };
		for (int con : cons) {
			for (int audit : audits) {
				ContractorAccount contractor = contractoraccountDAO.find(con);
				final AuditType auditType = auditTypeDAO.find(audit);
				List<AuditCategoryRule> rules = auditDecisionTableDAO.getApplicableCategoryRules(contractor, auditType);

				long time = System.currentTimeMillis();
				List<AuditCategoryRule> cache = auditCategoryRuleCache.getApplicableCategoryRules(contractor, new HashSet<AuditType>() {
					{
						add(auditType);
					}
				});
				System.out.printf("Execution for contractor %d with AuditType %d took %dms\n", con, audit,
						System.currentTimeMillis() - time);

				/*
				 * System.out.println("DAO RULES\n-------------------------");
				 * for (AuditCategoryRule auditCategoryRule : rules) {
				 * System.out.print(auditCategoryRule.getId() + ","); }
				 * System.out
				 * .println("\nCACHE RULES\n-------------------------"); for
				 * (AuditCategoryRule auditCategoryRule : cache) {
				 * System.out.print(auditCategoryRule.getId() + ","); }
				 */

				assertEquals("The rules lists are different sizes", rules.size(), cache.size());

				for (AuditCategoryRule rule : rules) {
					assertTrue("There are rules missing in the cache list", cache.contains(rule));
				}

				int priority = Integer.MAX_VALUE;
				for (AuditCategoryRule rule : cache) {
					assertTrue("The priorities are not in order", rule.getPriority() <= priority);
					priority = rule.getPriority();
				}
			}
		}
	}

	public void testRequiredAuditTypes() {
		Map<AuditType, AuditBuilder.AuditTypeDetail> results = null;

		Set<OperatorAccount> operators = new HashSet<OperatorAccount>();
		OperatorAccount bpCherryPoint = EntityFactory.makeOperator();
		operators.add(bpCherryPoint);
		OperatorAccount bpCarson = EntityFactory.makeOperator();
		operators.add(bpCarson);

		List<AuditTypeRule> rules = new ArrayList<AuditTypeRule>();
		AuditType pqf = EntityFactory.makeAuditType(1);
		EntityFactory.addCategories(pqf, 1, "General Information");
		EntityFactory.addCategories(pqf, 2, "High Risk Only");
		EntityFactory.addCategories(pqf, 3, "California Licenses"); // only for
		// carson
		rules.add(createAuditTypeRule(true, pqf, null, null));

		AuditBuilder auditBuilder = new AuditBuilder();

		results = auditBuilder.calculateRequiredAuditTypes(rules, operators);
		assertEquals(1, results.size());
		assertEquals(2, results.get(pqf).operators.size());

		AuditType desktop = EntityFactory.makeAuditType(2);
		rules.add(createAuditTypeRule(false, desktop, null, null));
		rules.add(createAuditTypeRule(true, desktop, null, bpCherryPoint));
		rules.add(createAuditTypeRule(false, desktop, LowMedHigh.High, bpCherryPoint));
		rules.add(createAuditTypeRule(true, desktop, LowMedHigh.High, bpCarson));

		results = auditBuilder.calculateRequiredAuditTypes(rules, operators);
		assertEquals(2, results.size());
		assertEquals(1, results.get(desktop).operators.size());

		List<AuditCategoryRule> catRules = new ArrayList<AuditCategoryRule>();

		/*
		 * catRules.add(createCategoryRule(true, pqf,
		 * pqf.getCategories().get(0), null, null)); auditBuilder.getDetail(pqf,
		 * catRules, null); assertEquals(1,
		 * auditBuilder.getAuditTypes(pqf).categories.size()); assertEquals(1,
		 * auditBuilder.getAuditTypes(pqf).governingBodies.size());
		 * 
		 * catRules.add(createCategoryRule(true, null, null, null, null));
		 * catRules.add(createCategoryRule(false, pqf, null, null, null));
		 * auditBuilder.getDetail(pqf, catRules); assertEquals(1,
		 * auditBuilder.getAuditTypes(pqf).categories.size());
		 * 
		 * catRules.add(createCategoryRule(true, pqf,
		 * pqf.getCategories().get(2), null, bpCarson));
		 * auditBuilder.getDetail(pqf, catRules); assertEquals(2,
		 * auditBuilder.getAuditTypes(pqf).categories.size()); assertEquals(2,
		 * auditBuilder.getAuditTypes(pqf).governingBodies.size());
		 */
	}

	private AuditTypeRule createAuditTypeRule(boolean include, AuditType auditType, LowMedHigh risk,
			OperatorAccount operator) {
		AuditTypeRule rule = new AuditTypeRule();
		rule.setInclude(include);
		rule.setAuditType(auditType);
		rule.setRisk(risk);
		rule.setOperatorAccount(operator);

		rule.calculatePriority();
		return rule;
	}

	private AuditCategoryRule createCategoryRule(boolean include, AuditType auditType, AuditCategory category,
			LowMedHigh risk, OperatorAccount operator) {
		AuditCategoryRule rule = new AuditCategoryRule();
		rule.setAuditCategory(category);
		rule.setInclude(include);
		rule.setAuditType(auditType);
		rule.setRisk(risk);
		rule.setOperatorAccount(operator);

		rule.calculatePriority();
		return rule;
	}

}
