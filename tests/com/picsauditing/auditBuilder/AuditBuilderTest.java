package com.picsauditing.auditBuilder;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.auditBuilder.AuditTypesBuilder.AuditTypeDetail;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.jpa.entities.AccountLevel;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditRule;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeRule;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorAuditOperatorPermission;
import com.picsauditing.jpa.entities.ContractorType;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.jpa.entities.QuestionComparator;
import com.picsauditing.jpa.entities.Trade;
import com.picsauditing.util.Strings;

public class AuditBuilderTest extends PicsTest {
	AuditBuilder auditBuilder;

	AuditTypeRuleCache typeRuleCache = new AuditTypeRuleCache();
	List<AuditTypeRule> typeRules = new ArrayList<AuditTypeRule>();
	AuditTypesBuilder typeBuilder;

	AuditCategoryRuleCache catRuleCache = new AuditCategoryRuleCache();
	List<AuditCategoryRule> catRules = new ArrayList<AuditCategoryRule>();
	AuditCategoriesBuilder catBuilder;

	@Mock
	AuditPercentCalculator auditPercentCalculator;
	@Mock
	AuditDataDAO auditDataDao;

	ContractorAccount contractor;
	OperatorAccount operator;

	/**
	 * Setup Contractors and Audit Types and Categories
	 */
	@Before
	public void setUp() throws Exception {
//		MockitoAnnotations.initMocks(this);

		auditBuilder = new AuditBuilder();
		autowireEMInjectedDAOs(auditBuilder);
		PicsTestUtil.forceSetPrivateField(auditBuilder, "typeRuleCache",
				typeRuleCache);
		PicsTestUtil.forceSetPrivateField(auditBuilder, "categoryRuleCache",
				catRuleCache);
		PicsTestUtil.forceSetPrivateField(auditBuilder,
				"auditPercentCalculator", auditPercentCalculator);

		typeRules.clear();
		typeRuleCache.clear();
		catRules.clear();
		catRuleCache.clear();

		contractor = EntityFactory.makeContractor();
		operator = EntityFactory.makeOperator();
		contractor.getOperatorAccounts().add(operator);
		EntityFactory.addContractorOperator(contractor, operator);
	}
	
	@Ignore
	public void testBuildAudits_WCB() {
		addTypeRules((new RuleParameters()).setAuditTypeId(145));
		addCategoryRules(null);

		when(em.find(Matchers.argThat(equalTo(AuditType.class)), anyInt()))
				.thenReturn(EntityFactory.makeAuditType(145));

		auditBuilder.buildAudits(contractor);
		assertEquals(1, contractor.getAudits().size());
		assertTrue(!Strings.isEmpty(contractor.getAudits().get(0).getAuditFor()));

		// see that it doesn't replicate again
		auditBuilder.buildAudits(contractor);
		assertEquals(1, contractor.getAudits().size());
	}
	
	@Test
	public void testAdjustCaoStatus() {
		ContractorAudit annualAudit = EntityFactory.makeAnnualUpdate(AuditType.ANNUALADDENDUM, contractor, "2011");
		EntityFactory.addCategories(annualAudit.getAuditType(), 101, "Annual Category 1");
		
	    OperatorAccount nueOperator = EntityFactory.makeOperator();
	    OperatorAccount oldOperator = EntityFactory.makeOperator();
	    OperatorAccount childOperator = EntityFactory.makeOperator();
	    
	    EntityFactory.addCao(annualAudit, oldOperator);
	    ContractorAuditOperator cao =annualAudit.getOperators().get(0);
	    cao.setStatus(AuditStatus.Complete);
	    ContractorAuditOperatorPermission caop = new ContractorAuditOperatorPermission();
	    caop.setCao(cao);
	    caop.setOperator(childOperator);
	    cao.getCaoPermissions().add(caop);
	    
	    contractor.getAudits().add(annualAudit);
	    
		addTypeRules((new RuleParameters())
				.setAuditTypeId(AuditType.ANNUALADDENDUM));
		for (AuditCategory category : annualAudit.getAuditType().getCategories()) {
			addCategoryRules((new RuleParameters()).setAuditType(annualAudit.getAuditType())
					.setAuditCategory(category).setOperatorAccount(nueOperator));
		}

		when(em.find(Matchers.argThat(equalTo(AuditType.class)), anyInt()))
				.thenReturn(
						EntityFactory.makeAuditType(AuditType.ANNUALADDENDUM));

		auditBuilder.buildAudits(contractor);
		assertEquals(3, contractor.getAudits().size());
	}
		
	@Test
	public void testAuditTypeBuilderCategoryBuildere() {
		// set up audit type
		AuditType pqfType = EntityFactory.makeAuditType(AuditType.PQF);
		EntityFactory.addCategories(pqfType, 101, "PQF Category 1");
		EntityFactory.addCategories(pqfType, 102, "PQF Category 2");

		// set up rules
		addTypeRules((new RuleParameters()).setAuditType(pqfType));
		for (AuditCategory category : pqfType.getCategories()) {
			addCategoryRules((new RuleParameters()).setAuditType(pqfType)
					.setAuditCategory(category));
		}

		AuditTypesBuilder typeBuilder = new AuditTypesBuilder(typeRuleCache,
				contractor);
		AuditCategoriesBuilder categoryBuilder = new AuditCategoriesBuilder(
				catRuleCache, contractor);

		Set<AuditTypeDetail> auditTypes = typeBuilder.calculate();
		assertEquals(1, auditTypes.size());

		ContractorAudit conAudit = EntityFactory.makeContractorAudit(pqfType,
				contractor);
		Set<AuditCategory> categories = categoryBuilder.calculate(conAudit,
				contractor.getOperatorAccounts());
		assertEquals(2, categories.size());
	}

	@Test
	public void testBuildAudits_AnnualUpdates() {
		addTypeRules((new RuleParameters())
				.setAuditTypeId(AuditType.ANNUALADDENDUM));
		addCategoryRules(null);

		when(em.find(Matchers.argThat(equalTo(AuditType.class)), anyInt()))
				.thenReturn(
						EntityFactory.makeAuditType(AuditType.ANNUALADDENDUM));

		auditBuilder.buildAudits(contractor);
		assertEquals(3, contractor.getAudits().size());
	}

	@Test
	public void testBuildAudits_ReviewCompetency() {
		addTypeRules((new RuleParameters())
				.setAuditTypeId(AuditType.INTEGRITYMANAGEMENT));
		addCategoryRules(null);

		when(em.find(Matchers.argThat(equalTo(AuditType.class)), anyInt()))
				.thenReturn(
						EntityFactory
								.makeAuditType(AuditType.INTEGRITYMANAGEMENT));

		auditBuilder.buildAudits(contractor);
		assertEquals(1, contractor.getAudits().size());
	}

	@Test
	public void testBuildAudits_IsValidAudit_NullData() {
		ContractorAudit corAudit = EntityFactory.makeContractorAudit(
				AuditType.COR, contractor);
		ContractorAudit pqfAudit = EntityFactory.makeContractorAudit(
				AuditType.PQF, contractor);
		contractor.getAudits().add(pqfAudit);
		contractor.getAudits().add(corAudit);
		OperatorAccount operator = EntityFactory.makeOperator();
		EntityFactory.addCao(corAudit, operator);

		AuditQuestion question = EntityFactory.makeAuditQuestion();
		question.setId(AuditQuestion.COR);
		AuditCatData auditCatData = EntityFactory.makeAuditCatData();
		auditCatData.setCategory(question.getCategory());
		auditCatData.setApplies(true);
		pqfAudit.getCategories().add(auditCatData);
		pqfAudit.setCreationDate(new Date());

		addTypeRules((new RuleParameters()).setAuditTypeId(AuditType.PQF));
		addCategoryRules(null);

		PicsTestUtil.forceSetPrivateField(auditBuilder,
				"auditDataDAO", auditDataDao);
		
		when(em.find(Matchers.argThat(equalTo(AuditType.class)), anyInt()))
				.thenReturn(EntityFactory.makeAuditType(AuditType.PQF));

		auditBuilder.buildAudits(contractor);
		assertEquals(1, corAudit.getOperatorsVisible().size());
	}

	@Test
	public void testBuildAudits_IsValidAudit_Cor_Yes() {
		ContractorAudit corAudit = EntityFactory.makeContractorAudit(
				AuditType.COR, contractor);
		ContractorAudit pqfAudit = EntityFactory.makeContractorAudit(
				AuditType.PQF, contractor);
		contractor.getAudits().add(pqfAudit);
		contractor.getAudits().add(corAudit);
		OperatorAccount operator = EntityFactory.makeOperator();
		EntityFactory.addCao(corAudit, operator);

		AuditData auditData = EntityFactory.makeAuditData("Yes");
		AuditQuestion question = EntityFactory.makeAuditQuestion();
		
		AuditCatData auditCatData = EntityFactory.makeAuditCatData();
		auditCatData.setCategory(question.getCategory());
		auditCatData.setApplies(false);
		
		question.setId(AuditQuestion.COR);
		auditData.setQuestion(question);
		pqfAudit.getCategories().add(auditCatData);
		pqfAudit.setCreationDate(new Date());
		pqfAudit.getData().add(auditData);

		addTypeRules((new RuleParameters()).setAuditTypeId(AuditType.PQF));
		addCategoryRules(null);

		when(em.find(Matchers.argThat(equalTo(AuditType.class)), anyInt()))
				.thenReturn(EntityFactory.makeAuditType(AuditType.PQF));

		auditBuilder.buildAudits(contractor);
		assertEquals(0, corAudit.getOperatorsVisible().size());
	}

	@Test
	public void testBuildAudits_IsValidAudit_Cor_No() {
		ContractorAudit corAudit = EntityFactory.makeContractorAudit(
				AuditType.COR, contractor);
		ContractorAudit pqfAudit = EntityFactory.makeContractorAudit(
				AuditType.PQF, contractor);
		contractor.getAudits().add(pqfAudit);
		contractor.getAudits().add(corAudit);
		OperatorAccount operator = EntityFactory.makeOperator();
		EntityFactory.addCao(corAudit, operator);

		AuditData auditData = EntityFactory.makeAuditData("No");
		AuditQuestion question = EntityFactory.makeAuditQuestion();
		AuditCatData auditCatData = EntityFactory.makeAuditCatData();

		question.setId(AuditQuestion.COR);
		auditData.setQuestion(question);
		auditCatData.setCategory(question.getCategory());
		auditCatData.setApplies(true);
		pqfAudit.getCategories().add(auditCatData);
		pqfAudit.setCreationDate(new Date());
		pqfAudit.getData().add(auditData);

		addTypeRules((new RuleParameters()).setAuditTypeId(AuditType.PQF));
		addCategoryRules(null);

		when(em.find(Matchers.argThat(equalTo(AuditType.class)), anyInt()))
				.thenReturn(EntityFactory.makeAuditType(AuditType.PQF));

		auditBuilder.buildAudits(contractor);
		assertEquals(0, corAudit.getOperatorsVisible().size());
	}

	@Test
	public void testBuildAudits_IsValidAudit_Iec_Yes() {
		ContractorAudit iecAudit = EntityFactory.makeContractorAudit(
				AuditType.IEC_AUDIT, contractor);
		ContractorAudit pqfAudit = EntityFactory.makeContractorAudit(
				AuditType.PQF, contractor);
		contractor.getAudits().add(pqfAudit);
		contractor.getAudits().add(iecAudit);
		OperatorAccount operator = EntityFactory.makeOperator();
		EntityFactory.addCao(iecAudit, operator);

		AuditData auditData = EntityFactory.makeAuditData("Yes");
		AuditQuestion question = EntityFactory.makeAuditQuestion();
		AuditCatData auditCatData = EntityFactory.makeAuditCatData();

		question.setId(AuditQuestion.IEC);
		auditData.setQuestion(question);
		auditCatData.setCategory(question.getCategory());
		auditCatData.setApplies(false);
		pqfAudit.getCategories().add(auditCatData);
		pqfAudit.setCreationDate(new Date());
		pqfAudit.getData().add(auditData);

		addTypeRules((new RuleParameters()).setAuditTypeId(AuditType.PQF));
		addCategoryRules(null);

		when(em.find(Matchers.argThat(equalTo(AuditType.class)), anyInt()))
				.thenReturn(EntityFactory.makeAuditType(AuditType.PQF));

		auditBuilder.buildAudits(contractor);
		assertEquals(0, iecAudit.getOperatorsVisible().size());
	}

	@Test
	public void testBuildAudits_IsValidAudit_Iec__No() {
		ContractorAudit iecAudit = EntityFactory.makeContractorAudit(
				AuditType.IEC_AUDIT, contractor);
		ContractorAudit pqfAudit = EntityFactory.makeContractorAudit(
				AuditType.PQF, contractor);
		contractor.getAudits().add(pqfAudit);
		contractor.getAudits().add(iecAudit);
		OperatorAccount operator = EntityFactory.makeOperator();
		EntityFactory.addCao(iecAudit, operator);

		AuditData auditData = EntityFactory.makeAuditData("No");
		AuditQuestion question = EntityFactory.makeAuditQuestion();
		AuditCatData auditCatData = EntityFactory.makeAuditCatData();

		question.setId(AuditQuestion.IEC);
		auditData.setQuestion(question);
		auditCatData.setCategory(question.getCategory());
		auditCatData.setApplies(true);
		pqfAudit.getCategories().add(auditCatData);
		pqfAudit.setCreationDate(new Date());
		pqfAudit.getData().add(auditData);

		addTypeRules((new RuleParameters()).setAuditTypeId(AuditType.PQF));
		addCategoryRules(null);

		when(em.find(Matchers.argThat(equalTo(AuditType.class)), anyInt()))
				.thenReturn(EntityFactory.makeAuditType(AuditType.PQF));

		auditBuilder.buildAudits(contractor);
		assertEquals(0, iecAudit.getOperatorsVisible().size());
	}
	
	@Test
	public void testFoundCurrentYearWCB_NullAuditFor() throws Exception {
		ContractorAudit audit = Mockito.mock(ContractorAudit.class);
		when(audit.getAuditFor()).thenReturn(null);
		
		Boolean result = Whitebox.invokeMethod(auditBuilder, "foundCurrentYearWCB", audit);
		assertTrue(result);
	}
	
	@Test
	public void testFoundCurrentYearWCB_BlankAuditFor() throws Exception {
		ContractorAudit audit = Mockito.mock(ContractorAudit.class);
		when(audit.getAuditFor()).thenReturn(" ");
		
		Boolean result = Whitebox.invokeMethod(auditBuilder, "foundCurrentYearWCB", audit);
		assertTrue(result);
	}
	
	@Ignore
	public void testFoundCurrentYearWCB_CurrentYearAuditFor() throws Exception {
		ContractorAudit audit = Mockito.mock(ContractorAudit.class);
		ContractorAccount contractor = Mockito.mock(ContractorAccount.class);
		
		List<ContractorAudit> audits = buildMockAudits();
		when(contractor.getAudits()).thenReturn(audits);
		when(audit.getContractorAccount()).thenReturn(contractor);
		when(audit.getAuditFor()).thenReturn(DateBean.getWCBYear());
		when(audit.getAuditType()).thenReturn(new AuditType(145));
		
		Boolean result = Whitebox.invokeMethod(auditBuilder, "foundCurrentYearWCB", audit);
		assertTrue(result);
	}
	
	@Ignore
	public void testFoundCurrentYearWCB_PreviousYearAuditFor() throws Exception {
		ContractorAudit audit = Mockito.mock(ContractorAudit.class);
		ContractorAccount contractor = Mockito.mock(ContractorAccount.class);
		
		when(contractor.getAudits()).thenReturn(null);
		when(audit.getContractorAccount()).thenReturn(contractor);
		when(audit.getAuditFor()).thenReturn(DateBean.getWCBYear());
		
		Boolean result = Whitebox.invokeMethod(auditBuilder, "foundCurrentYearWCB", audit);
		assertFalse(result);
	}
	
	@Ignore
	public void testFindAllWCBAuditYears_NoAudits() throws Exception {
		ContractorAudit audit = Mockito.mock(ContractorAudit.class);
		ContractorAccount contractor = Mockito.mock(ContractorAccount.class);
		when(contractor.getAudits()).thenReturn(null);
		
		List<String> result = Whitebox.invokeMethod(auditBuilder, "findAllWCBAuditYears", contractor, audit);
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}
	
	@Ignore
	public void testFindAllWCBAuditYears() throws Exception {
		ContractorAudit wcbAudit = Mockito.mock(ContractorAudit.class);
		ContractorAccount contractor = Mockito.mock(ContractorAccount.class);		
		List<ContractorAudit> audits = buildMockAudits();
		when(contractor.getAudits()).thenReturn(audits);
		when(wcbAudit.getAuditType()).thenReturn(new AuditType(145));
		
		List<String> result = Whitebox.invokeMethod(auditBuilder, "findAllWCBAuditYears", contractor, wcbAudit);
		assertNotNull(result);
		assertTrue(result.contains("2011"));
		assertTrue(result.contains(DateBean.getWCBYear()));
	}
	
	private List<ContractorAudit> buildMockAudits() {
		List<ContractorAudit> audits = new ArrayList<ContractorAudit>();		
		audits.add(buildMockWCBAudit(145, "2011"));
		audits.add(buildMockWCBAudit(145, DateBean.getWCBYear()));
		
		return audits;
	}
	
	private ContractorAudit buildMockWCBAudit(int auditTypeId, String auditFor) {
		ContractorAudit audit = Mockito.mock(ContractorAudit.class);
		when(audit.getAuditType()).thenReturn(new AuditType(auditTypeId));
		when(audit.getAuditFor()).thenReturn(auditFor);
		
		return audit;
	}

	private void addTypeRules(RuleParameters params) {
		AuditTypeRule rule = new AuditTypeRule();
		if (params != null) {
			fillAuditRule(params, rule);
			rule.setManuallyAdded(params.manuallyAdded);
			rule.setDependentAuditStatus(params.dependentAuditStatus);
			rule.setDependentAuditType(params.dependentAuditType);
		}

		typeRules.add(rule);
		typeRuleCache.initialize(typeRules);
	}

	private void addCategoryRules(RuleParameters params) {
		AuditCategoryRule rule = new AuditCategoryRule();
		if (params != null) {
			fillAuditRule(params, rule);
			rule.setAuditCategory(params.auditCategory);
			rule.setRootCategory(params.rootCategory);
		}

		catRules.add(rule);
		catRuleCache.initialize(catRules);
	}

	private void fillAuditRule(RuleParameters params, AuditRule rule) {
		rule.setPriority(params.priority);
		rule.setInclude(params.include);
		if (params.auditTypeId != 0) {
			rule.setAuditType(EntityFactory.makeAuditType(params.auditTypeId));
		} else {
			rule.setAuditType(params.auditType);
		}
		rule.setSafetyRisk(params.safetyRisk);
		rule.setProductRisk(params.productRisk);
		rule.setOperatorAccount(params.operatorAccount);
		rule.setContractorType(params.contractorType);
		rule.setTag(params.tag);
		rule.setTrade(params.trade);
		rule.setQuestion(params.question);
		rule.setQuestionAnswer(params.questionAnswer);
		rule.setQuestionComparator(params.questionComparator);
		rule.setSoleProprietor(params.soleProprietor);
		rule.setAccountLevel(params.accountLevel);
	}

	public class RuleParameters {
		protected int priority = 0;
		protected boolean include = true;
		public int auditTypeId = 0;
		public AuditType auditType;
		public LowMedHigh safetyRisk;
		public LowMedHigh productRisk;
		public OperatorAccount operatorAccount;
		protected ContractorType contractorType;
		protected OperatorTag tag;
		protected Trade trade;
		protected AuditQuestion question;
		protected QuestionComparator questionComparator;
		protected String questionAnswer;
		protected Boolean soleProprietor;
		protected AccountLevel accountLevel;
		private AuditType dependentAuditType;
		private AuditStatus dependentAuditStatus;
		private boolean manuallyAdded = false;
		public AuditCategory auditCategory;
		public Boolean rootCategory;

		public void setPriority(int priority) {
			this.priority = priority;
		}

		public RuleParameters setInclude(boolean include) {
			this.include = include;
			return this;
		}

		public RuleParameters setAuditTypeId(int auditTypeId) {
			this.auditTypeId = auditTypeId;
			return this;
		}

		public RuleParameters setAuditType(AuditType auditType) {
			this.auditType = auditType;
			return this;
		}

		public RuleParameters setSafetyRisk(LowMedHigh safetyRisk) {
			this.safetyRisk = safetyRisk;
			return this;
		}

		public RuleParameters setProductRisk(LowMedHigh productRisk) {
			this.productRisk = productRisk;
			return this;
		}

		public RuleParameters setOperatorAccount(OperatorAccount operatorAccount) {
			this.operatorAccount = operatorAccount;
			return this;
		}

		public RuleParameters setContractorType(ContractorType contractorType) {
			this.contractorType = contractorType;
			return this;
		}

		public RuleParameters setTag(OperatorTag tag) {
			this.tag = tag;
			return this;
		}

		public RuleParameters setTrade(Trade trade) {
			this.trade = trade;
			return this;
		}

		public RuleParameters setQuestion(AuditQuestion question) {
			this.question = question;
			return this;
		}

		public RuleParameters setQuestionComparator(
				QuestionComparator questionComparator) {
			this.questionComparator = questionComparator;
			return this;
		}

		public RuleParameters setQuestionAnswer(String questionAnswer) {
			this.questionAnswer = questionAnswer;
			return this;
		}

		public RuleParameters setSoleProprietor(Boolean soleProprietor) {
			this.soleProprietor = soleProprietor;
			return this;
		}

		public RuleParameters setAccountLevel(AccountLevel accountLevel) {
			this.accountLevel = accountLevel;
			return this;
		}

		public RuleParameters setAuditCategory(AuditCategory auditCategory) {
			this.auditCategory = auditCategory;
			return this;
		}

		public RuleParameters setRootCategory(Boolean rootCategory) {
			this.rootCategory = rootCategory;
			return this;
		}

		public RuleParameters setDependentAuditType(AuditType dependentAuditType) {
			this.dependentAuditType = dependentAuditType;
			return this;
		}

		public RuleParameters setDependentAuditStatus(
				AuditStatus dependentAuditStatus) {
			this.dependentAuditStatus = dependentAuditStatus;
			return this;
		}

		public RuleParameters setManuallyAdded(boolean manuallyAdded) {
			this.manuallyAdded = manuallyAdded;
			return this;
		}
	}
}
