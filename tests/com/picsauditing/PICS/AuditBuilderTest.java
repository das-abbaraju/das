package com.picsauditing.PICS;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import com.picsauditing.EntityFactory;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeRule;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.OperatorAccount;

public class AuditBuilderTest extends TestCase {

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

		catRules.add(createCategoryRule(true, pqf, pqf.getCategories().get(0), null, null));
		auditBuilder.getRequiredCategories(pqf, catRules);
		assertEquals(1, auditBuilder.getAuditTypes(pqf).categories.size());
		assertEquals(1, auditBuilder.getAuditTypes(pqf).governingBodies.size());

		catRules.add(createCategoryRule(true, null, null, null, null));
		catRules.add(createCategoryRule(false, pqf, null, null, null));
		auditBuilder.getRequiredCategories(pqf, catRules);
		assertEquals(1, auditBuilder.getAuditTypes(pqf).categories.size());

		catRules.add(createCategoryRule(true, pqf, pqf.getCategories().get(2), null, bpCarson));
		auditBuilder.getRequiredCategories(pqf, catRules);
		assertEquals(2, auditBuilder.getAuditTypes(pqf).categories.size());
		assertEquals(2, auditBuilder.getAuditTypes(pqf).governingBodies.size());

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
