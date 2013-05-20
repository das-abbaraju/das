package com.picsauditing.auditBuilder;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.*;

import com.opensymphony.xwork2.interceptor.annotations.Before;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.jpa.entities.*;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.EntityFactory;


public class AuditCategoriesBuilderTest {
	ContractorAccount contractor;
	OperatorAccount operator;
	ContractorAudit audit;
	AuditCategory cat1;
	AuditCategory cat2;

	@Mock
	AuditCategoryRuleCache ruleCache;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		contractor = EntityFactory.makeContractor();
		operator = EntityFactory.makeOperator();
		setupAudit();
		setupCategoryRules();
	}

	private void setupAudit() {
		audit = EntityFactory.makeContractorAudit(200, contractor);
		ContractorAuditOperator cao = EntityFactory.addCao(audit, operator);
		ContractorAuditOperatorPermission caop = new ContractorAuditOperatorPermission();
		caop.setOperator(operator);
		caop.setCao(cao);
		cao.getCaoPermissions().add(caop);
		cat1 = EntityFactory.makeAuditCategory(1);
		cat2 = EntityFactory.makeAuditCategory(2);
		cat2.setParent(cat1);

		audit.getAuditType().getCategories().add(cat1);
		audit.getAuditType().getCategories().add(cat2);
	}

	private void setupCategoryRules() throws Exception {
		AuditCategoryRule rule1 = new AuditCategoryRule();
		rule1.setAuditType(audit.getAuditType());
		rule1.setOperatorAccount(operator);
		rule1.setAuditCategory(cat1);
		rule1.setRootCategory(true);

		AuditCategoryRule rule2 = new AuditCategoryRule();
		rule2.setAuditType(audit.getAuditType());
		rule2.setOperatorAccount(operator);
		rule2.setAuditCategory(cat2);
		rule2.setRootCategory(false);

		List<AuditCategoryRule> catRules = new ArrayList<AuditCategoryRule>();
		catRules.add(rule1);
		catRules.add(rule2);

		when(ruleCache.getRules(contractor, audit.getAuditType())).thenReturn(catRules);
	}

	@Test
	public void testExpiredCategories() throws Exception {
		setUp();
		AuditCategoriesBuilder categoryBuilder = new AuditCategoriesBuilder(ruleCache, contractor);

		// none expired
		assertEquals(2, categoryBuilder.calculate(audit).size());

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		Date expiredDate = cal.getTime();
		cal.add(Calendar.DATE, -1);
		Date effectiveDate = cal.getTime();

		cat2.setEffectiveDate(effectiveDate);
		cat2.setExpirationDate(expiredDate);
		// one expired
		assertEquals(1, categoryBuilder.calculate(audit).size());
	}

	@Test
	public void testFindMostRecentAudit() throws Exception {
		ContractorAccount testContractor = EntityFactory.makeContractor();
		testContractor.setAudits(new ArrayList<ContractorAudit>());
		
		Calendar oldAuditCreationDate = Calendar.getInstance();
		oldAuditCreationDate.set(Calendar.YEAR, 2001);
		
		Calendar newAuditCreationDate = Calendar.getInstance();
		newAuditCreationDate.set(Calendar.YEAR, 2002);
		
		ContractorAudit auditTypeOne = EntityFactory.makeContractorAudit(1, testContractor);
		auditTypeOne.setId(1);
		ContractorAudit auditTypeTwoOld = EntityFactory.makeContractorAudit(2, testContractor);
		auditTypeTwoOld.setCreationDate(oldAuditCreationDate.getTime());
		auditTypeTwoOld.setId(2);
		ContractorAudit auditTypeTwoNew = EntityFactory.makeContractorAudit(2, testContractor);
		auditTypeTwoNew.setCreationDate(newAuditCreationDate.getTime());
		auditTypeTwoNew.setId(3);
		
		testContractor.getAudits().add(auditTypeOne);
		testContractor.getAudits().add(auditTypeTwoOld);
		testContractor.getAudits().add(auditTypeTwoNew);
		
		
		AuditCategoriesBuilder categoryBuilder = new AuditCategoriesBuilder(null, testContractor);
	
		ContractorAudit result = Whitebox.invokeMethod(categoryBuilder, "findMostRecentAudit", 2);
		
		assertEquals(3, result.getId());
		
	}
	
	@Test
	public void testFindAnswer() throws Exception {
		ContractorAccount testContractor = EntityFactory.makeContractor();
		ContractorAudit audit = EntityFactory.makeContractorAudit(1, testContractor);
		audit.setData(new ArrayList<AuditData>());
		
		AuditQuestion questionOne = makeUpQuestion(1, audit, "pink");
		AuditQuestion questionTwo = makeUpQuestion(2, audit, "purple");
		AuditQuestion questionThree = makeUpQuestion(3, audit, "magenta");
		
		AuditCatData auditCatData = EntityFactory.makeAuditCatData();
		
		auditCatData.getCategory().setQuestions(new ArrayList<AuditQuestion>());
		auditCatData.getCategory().getQuestions().add(questionOne);
		auditCatData.getCategory().getQuestions().add(questionTwo);
		auditCatData.getCategory().getQuestions().add(questionThree);
		
		AuditCategoriesBuilder categoryBuilder = new AuditCategoriesBuilder(null, testContractor);
	
		assertEquals("pink", wrapPrivateMethodCall(categoryBuilder, audit, 1).getAnswer());
		assertEquals("purple", wrapPrivateMethodCall(categoryBuilder, audit, 2).getAnswer());
		assertEquals("magenta", wrapPrivateMethodCall(categoryBuilder, audit, 3).getAnswer());	
	}

	private AuditQuestion makeUpQuestion(int id,ContractorAudit audit, String answer) {
		AuditQuestion question = EntityFactory.makeAuditQuestion();
		question.setId(id);
		audit.getData().add(EntityFactory.makeAuditData(answer, question));
		return question;
	}
	
	private AuditData wrapPrivateMethodCall(AuditCategoriesBuilder categoryBuilder, ContractorAudit audit, int currentQuestionId) throws Exception { 
		return Whitebox.invokeMethod(categoryBuilder, "findAnswer", audit,currentQuestionId);
	}
}
