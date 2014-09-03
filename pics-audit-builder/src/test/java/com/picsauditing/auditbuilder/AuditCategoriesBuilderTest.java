package com.picsauditing.auditbuilder;

import com.picsauditing.EntityFactory;
import com.picsauditing.auditbuilder.entities.*;
import com.picsauditing.auditbuilder.service.DocumentUtilityService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.*;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.mockito.Mockito.when;


public class AuditCategoriesBuilderTest {
	ContractorAccount contractor;
	OperatorAccount operator;
	ContractorDocument audit;
	DocumentCategory cat1;
    DocumentCategory cat2;
    DocumentCategory cat3;

	@Mock
    AuditCategoryRuleCache2 ruleCache;

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
		ContractorDocumentOperator cao = EntityFactory.addCao(audit, operator);
		ContractorDocumentOperatorPermission caop = new ContractorDocumentOperatorPermission();
		caop.setOperator(operator);
		caop.setCao(cao);
		cao.getCaoPermissions().add(caop);
		cat1 = EntityFactory.makeAuditCategory(1);
		cat2 = EntityFactory.makeAuditCategory(2);
		cat2.setParent(cat1);
        cat3 = EntityFactory.makeAuditCategory(3);

		audit.getAuditType().getCategories().add(cat1);
		audit.getAuditType().getCategories().add(cat2);
        audit.getAuditType().getCategories().add(cat3);
	}

	private void setupCategoryRules() throws Exception {
		DocumentCategoryRule rule1 = new DocumentCategoryRule();
		rule1.setAuditType(audit.getAuditType());
		rule1.setOperatorAccount(operator);
		rule1.setDocumentCategory(cat1);
		rule1.setRootCategory(true);

		DocumentCategoryRule rule2 = new DocumentCategoryRule();
		rule2.setAuditType(audit.getAuditType());
		rule2.setOperatorAccount(operator);
		rule2.setDocumentCategory(cat2);
		rule2.setRootCategory(false);

		List<DocumentCategoryRule> catRules = new ArrayList<DocumentCategoryRule>();
		catRules.add(rule1);
		catRules.add(rule2);

		when(ruleCache.getRules(contractor, audit.getAuditType())).thenReturn(catRules);
	}

    @Test
    public void testCalculate_AuditCao() throws Exception {
        setupRules();
        AuditCategoriesBuilder categoryBuilder = new AuditCategoriesBuilder(ruleCache, contractor);
        Collection<OperatorAccount> operators = new ArrayList<OperatorAccount>(Arrays.asList(operator) );
        Set<DocumentCategory> categories = categoryBuilder.calculate(audit, operators);
        assertEquals(2, categories.size());
        for(DocumentCategory cat:categories) {
            assertFalse(cat.getId() == 3);
        }
    }

    @Test
    public void testIsCategoryApplicable_True() throws Exception {
        setupRules();
        AuditCategoriesBuilder categoryBuilder = new AuditCategoriesBuilder(ruleCache, contractor);
        Collection<OperatorAccount> operators = new ArrayList<OperatorAccount>(Arrays.asList(operator) );
        categoryBuilder.calculate(audit, operators);

        assertTrue(categoryBuilder.isCategoryApplicable(cat1, audit.getOperators().get(0)));
    }

    @Test
    public void testIsCategoryApplicable_False() throws Exception {
        setupRules();
        AuditCategoriesBuilder categoryBuilder = new AuditCategoriesBuilder(ruleCache, contractor);
        Collection<OperatorAccount> operators = new ArrayList<OperatorAccount>(Arrays.asList(operator) );
        categoryBuilder.calculate(audit, operators);

        OperatorAccount badOperator = EntityFactory.makeOperator();
        ContractorDocumentOperator cao = new ContractorDocumentOperator();
        ContractorDocumentOperatorPermission caop = new ContractorDocumentOperatorPermission();
        caop.setOperator(badOperator);
        caop.setCao(cao);
        cao.setOperator(badOperator);
        cao.getCaoPermissions().add(caop);

        assertFalse(categoryBuilder.isCategoryApplicable(cat1, cao));
    }

    private void setupRules() throws Exception {
        DocumentCategoryRule rule1 = new DocumentCategoryRule();
        rule1.setAuditType(audit.getAuditType());
        rule1.setOperatorAccount(operator);
        rule1.setDocumentCategory(cat1);
        rule1.setRootCategory(true);

        DocumentCategoryRule rule2 = new DocumentCategoryRule();
        rule2.setAuditType(audit.getAuditType());
        rule2.setOperatorAccount(operator);
        rule2.setDocumentCategory(cat2);
        rule2.setRootCategory(false);

        DocumentCategoryRule rule3 = new DocumentCategoryRule();
        rule3.setAuditType(audit.getAuditType());
        rule3.setOperatorAccount(EntityFactory.makeOperator());
        rule3.setDocumentCategory(cat3);
        rule3.setRootCategory(true);

        List<DocumentCategoryRule> catRules = new ArrayList<DocumentCategoryRule>();
        catRules.add(rule1);
        catRules.add(rule2);
        catRules.add(rule3);

        when(ruleCache.getRules(contractor, audit.getAuditType())).thenReturn(catRules);
    }

	@Test
	public void testFindMostRecentAudit() throws Exception {
		ContractorAccount testContractor = EntityFactory.makeContractor();
		testContractor.setAudits(new ArrayList<ContractorDocument>());
		
		Calendar oldAuditCreationDate = Calendar.getInstance();
		oldAuditCreationDate.set(Calendar.YEAR, 2001);
		
		Calendar newAuditCreationDate = Calendar.getInstance();
		newAuditCreationDate.set(Calendar.YEAR, 2002);
		
		ContractorDocument auditTypeOne = EntityFactory.makeContractorAudit(1, testContractor);
		auditTypeOne.setId(1);
		ContractorDocument auditTypeTwoOld = EntityFactory.makeContractorAudit(2, testContractor);
		auditTypeTwoOld.setCreationDate(oldAuditCreationDate.getTime());
		auditTypeTwoOld.setId(2);
		ContractorDocument auditTypeTwoNew = EntityFactory.makeContractorAudit(2, testContractor);
		auditTypeTwoNew.setCreationDate(newAuditCreationDate.getTime());
		auditTypeTwoNew.setId(3);
		
		testContractor.getAudits().add(auditTypeOne);
		testContractor.getAudits().add(auditTypeTwoOld);
		testContractor.getAudits().add(auditTypeTwoNew);
		
		
		AuditCategoriesBuilder categoryBuilder = new AuditCategoriesBuilder(null, testContractor);
	
		ContractorDocument result = Whitebox.invokeMethod(categoryBuilder, "findMostRecentAudit", 2);
		
		assertEquals(3, result.getId());
		
	}
	
	@Test
	public void testFindAnswer() throws Exception {
		ContractorAccount testContractor = EntityFactory.makeContractor();
		ContractorDocument audit = EntityFactory.makeContractorAudit(1, testContractor);
		audit.setData(new ArrayList<DocumentData>());
		
		DocumentQuestion questionOne = makeUpQuestion(1, audit, "pink");
		DocumentQuestion questionTwo = makeUpQuestion(2, audit, "purple");
		DocumentQuestion questionThree = makeUpQuestion(3, audit, "magenta");
		
		DocumentCatData documentCatData = EntityFactory.makeAuditCatData();
		
		documentCatData.getCategory().setQuestions(new ArrayList<DocumentQuestion>());
		documentCatData.getCategory().getQuestions().add(questionOne);
		documentCatData.getCategory().getQuestions().add(questionTwo);
		documentCatData.getCategory().getQuestions().add(questionThree);
		
		AuditCategoriesBuilder categoryBuilder = new AuditCategoriesBuilder(null, testContractor);
	
		assertEquals("pink", wrapPrivateMethodCall(categoryBuilder, audit, 1).getAnswer());
		assertEquals("purple", wrapPrivateMethodCall(categoryBuilder, audit, 2).getAnswer());
		assertEquals("magenta", wrapPrivateMethodCall(categoryBuilder, audit, 3).getAnswer());	
	}

	private DocumentQuestion makeUpQuestion(int id,ContractorDocument audit, String answer) {
		DocumentQuestion question = EntityFactory.makeAuditQuestion();
		question.setId(id);
		audit.getData().add(EntityFactory.makeAuditData(answer, question));
		return question;
	}
	
	private DocumentData wrapPrivateMethodCall(AuditCategoriesBuilder categoryBuilder, ContractorDocument audit, int currentQuestionId) throws Exception {
		return Whitebox.invokeMethod(categoryBuilder, "findAnswer", audit,currentQuestionId);
	}

	@Test
	public void testDependentAudit() throws Exception {
		setUp();

		ContractorDocument audit = makeAudit(300);
		ContractorDocument dependentAudit = makeAudit(301);
		contractor.getAudits().add(audit);
		contractor.getAudits().add(dependentAudit);

		DocumentCategory cat1 = EntityFactory.makeAuditCategory(1);
		DocumentCategory cat2 = EntityFactory.makeAuditCategory(2);
		audit.getAuditType().getCategories().add(cat1);
		audit.getAuditType().getCategories().add(cat2);

		DocumentCategoryRule rule1 = makeRule(audit.getAuditType(), cat1);
		DocumentCategoryRule rule2 = makeRule(audit.getAuditType(), cat2);
		rule2.setDependentAuditType(dependentAudit.getAuditType());
		rule2.setDependentDocumentStatus(DocumentStatus.Submitted);
		List<DocumentCategoryRule> catRules = new ArrayList<>();
		catRules.add(rule1);
		catRules.add(rule2);
		when(ruleCache.getRules(contractor, audit.getAuditType())).thenReturn(catRules);

		AuditCategoriesBuilder test = new AuditCategoriesBuilder(ruleCache, contractor);
		Set<DocumentCategory> categories;

		// dependent no there
		categories = test.calculate(audit);
		assertEquals(1, categories.size());

		// dependent there
		catRules.clear();
		catRules.add(rule1);
		catRules.add(rule2);
		DocumentUtilityService.changeStatus(dependentAudit.getOperators().get(0), DocumentStatus.Submitted);
		categories = test.calculate(audit);
		assertEquals(2, categories.size());
	}

	private DocumentCategoryRule makeRule(AuditType auditType, DocumentCategory category) {
		DocumentCategoryRule rule = new DocumentCategoryRule();
		rule.setDocumentCategory(category);
		rule.setAuditType(auditType);

		return rule;
	}

	private ContractorDocument makeAudit(int auditTypeId) {
		AuditType auditType = EntityFactory.makeAuditType(auditTypeId);

		ContractorDocument audit = EntityFactory.makeContractorAudit(auditTypeId, contractor);
		audit.setAuditType(auditType);

		ContractorDocumentOperator cao = EntityFactory.addCao(audit, operator);
		ContractorDocumentOperatorPermission caop = new ContractorDocumentOperatorPermission();
		caop.setOperator(operator);
		caop.setCao(cao);
		cao.getCaoPermissions().add(caop);

		DocumentUtilityService.changeStatus(cao, DocumentStatus.Pending);

		return audit;
	}
}
