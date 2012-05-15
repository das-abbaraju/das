package com.picsauditing.auditBuilder;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.auditBuilder.AuditTypesBuilder.AuditTypeDetail;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeRule;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.OperatorAccount;

public class AuditBuilderTest extends PicsTest {
	ContractorAccount contractor1;
	ContractorAudit conAudit1PQF;
	AuditType pqf;
	AuditCategory pqfCategory2;

	AuditTypeRuleCache typeRuleCache = new AuditTypeRuleCache();
	List<AuditTypeRule> typeRules = new ArrayList<AuditTypeRule>();
	AuditTypesBuilder typeBuilder;
	Set<AuditTypeDetail> auditTypes;

	AuditCategoryRuleCache catRuleCache = new AuditCategoryRuleCache();
	List<AuditCategoryRule> catRules = new ArrayList<AuditCategoryRule>();
	AuditCategoriesBuilder catBuilder;
	Set<AuditCategory> categories;
	Map<OperatorAccount, Set<OperatorAccount>> caos;
	
	AuditBuilder auditBuilder = new AuditBuilder();
	AuditTypeRuleCache abTypeRuleCache = new AuditTypeRuleCache();
	AuditCategoryRuleCache abCatRuleCache = new AuditCategoryRuleCache();
	@Mock AuditPercentCalculator abAuditPercentCalculatior = new AuditPercentCalculator();

	/**
	 * Setup Contractors and Audit Types and Categories
	 */
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		autowireEMInjectedDAOs(auditBuilder);
//		PicsTestUtil.forceSetPrivateField(auditBuilder, "typeRuleCache", abTypeRuleCache);
//		PicsTestUtil.forceSetPrivateField(auditBuilder, "categoryRuleCache", abCatRuleCache);
//		PicsTestUtil.forceSetPrivateField(auditBuilder, "auditPercentCalculator", abAuditPercentCalculatior);
		
		contractor1 = EntityFactory.makeContractor();
		conAudit1PQF = EntityFactory.makeContractorAudit(1, contractor1);
		pqf = conAudit1PQF.getAuditType();
		EntityFactory.addCategories(pqf, 101, "PQF Category 1");
		pqfCategory2 = EntityFactory.addCategories(pqf, 102, "PQF Category 2");

		typeBuilder = new AuditTypesBuilder(typeRuleCache, contractor1);
		catBuilder = new AuditCategoriesBuilder(catRuleCache, contractor1);
	}

	@Test
	public void testBuilder() {
		{
			// Include the PQF for Everyone
			AuditTypeRule rule = new AuditTypeRule();
			rule.setAuditType(pqf);
			typeRules.add(rule);

			// Include All categories by default
			catRules.add(new AuditCategoryRule());
		}
		typeRuleCache.initialize(typeRules);
		catRuleCache.initialize(catRules);

		// Contractor has no operators, so should needs zero audits and categories
		auditTypes = typeBuilder.calculate();
		assertEquals(0, auditTypes.size());
		categories = catBuilder.calculate(conAudit1PQF, contractor1.getOperatorAccounts());
		assertEquals(0, categories.size());

		OperatorAccount operator1 = EntityFactory.makeOperator();
		EntityFactory.addContractorOperator(contractor1, operator1);

		// Now we should have 1 audit and 2 categories for the single operator
		auditTypes = typeBuilder.calculate();
		assertEquals(1, auditTypes.size());
		categories = catBuilder.calculate(conAudit1PQF, contractor1.getOperatorAccounts());
		assertEquals(2, categories.size());
		caos = catBuilder.getCaos();
		assertEquals(1, caos.size());

		{
			AuditCategoryRule rule = new AuditCategoryRule();
			catRules.add(rule);
			rule.setInclude(false);
			rule.setAuditCategory(pqfCategory2);

			catRuleCache.initialize(catRules);
			categories = catBuilder.calculate(conAudit1PQF, contractor1.getOperatorAccounts());
			assertEquals(1, categories.size());
		}
		{
			AuditCategoryRule rule = new AuditCategoryRule();
			catRules.add(rule);
			rule.setInclude(true);
			rule.setAuditCategory(pqfCategory2);
			rule.setOperatorAccount(operator1);

			catRuleCache.initialize(catRules);
			categories = catBuilder.calculate(conAudit1PQF, contractor1.getOperatorAccounts());
			assertEquals(2, categories.size());
		}
	}

	@Test
	public void testAuditTypes() {
		AuditTypeRule rule;

		// clear out old rules
		typeRules.clear();
		typeRuleCache.clear();
		
		// create rules
		rule = new AuditTypeRule();
		rule.setAuditType(EntityFactory.makeAuditType(50));
		typeRules.add(rule);
		
		// initialize cache
		typeRuleCache.initialize(typeRules);
		
		// initialize contractor
		EntityFactory.addContractorOperator(contractor1, EntityFactory.makeOperator());

		
		// typeBuilder = new AuditTypesBuilder(typeRuleCache, contractor1);
		// Now we should have 1 audit and 2 categories for the single operator
		auditTypes = typeBuilder.calculate();

		assertEquals(1, auditTypes.size()); // should get

	}
	
	@Test
	public void testBuildAudits() {
		// clear out old rules
		typeRules.clear();
		catRules.clear();

		addTypeRules(AuditType.INTEGRITYMANAGEMENT, null);

		// Include All categories by default
		catRules.add(new AuditCategoryRule());
		
		abTypeRuleCache.initialize(typeRules);
		abCatRuleCache.initialize(catRules);
		PicsTestUtil.forceSetPrivateField(auditBuilder, "typeRuleCache", abTypeRuleCache);
		PicsTestUtil.forceSetPrivateField(auditBuilder, "categoryRuleCache", abCatRuleCache);
		PicsTestUtil.forceSetPrivateField(auditBuilder, "auditPercentCalculator", abAuditPercentCalculatior);
		
		ContractorAccount contractor = EntityFactory.makeContractor();
		OperatorAccount operator = EntityFactory.makeOperator();
		EntityFactory.addContractorOperator(contractor, operator);

//		when(auditBuilder.).thenReturn(user.getId());
		auditBuilder.buildAudits(contractor);
	}
	
	private void addTypeRules(int auditTypeID, OperatorAccount operator) {
		AuditTypeRule rule = new AuditTypeRule();
		AuditType auditType = EntityFactory.makeAuditType(auditTypeID);
		rule.setAuditType(auditType);
		if (operator != null)
			rule.setOperatorAccount(operator);
		typeRules.add(rule);
	}
	
	private void addCategoryRules() {
		
	}
}
