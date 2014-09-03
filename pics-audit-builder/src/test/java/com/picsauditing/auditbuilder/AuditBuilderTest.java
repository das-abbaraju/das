package com.picsauditing.auditbuilder;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.auditbuilder.AuditTypesBuilder.AuditTypeDetail;
import com.picsauditing.auditbuilder.dao.*;
import com.picsauditing.auditbuilder.entities.*;
import com.picsauditing.auditbuilder.service.AccountService;
import com.picsauditing.auditbuilder.service.DocumentPeriodService;
import com.picsauditing.auditbuilder.util.DateBean;
import com.picsauditing.auditbuilder.util.Strings;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

public class AuditBuilderTest extends PicsTest {
	AuditBuilder2 auditBuilder;

	AuditTypeRuleCache2 typeRuleCache = new AuditTypeRuleCache2();
	List<DocumentTypeRule> typeRules = new ArrayList<>();
	AuditTypesBuilder typeBuilder;
    DocumentPeriodService auditPeriodService = new DocumentPeriodService();

	AuditCategoryRuleCache2 catRuleCache = new AuditCategoryRuleCache2();
	List<DocumentCategoryRule> catRules = new ArrayList<>();
	AuditCategoriesBuilder catBuilder;

	@Mock
    AuditPercentCalculator2 auditPercentCalculator;
	@Mock
    DocumentDataDAO auditDataDao;
	@Mock
    ContractorDocumentDAO conAuditDao;
    @Mock
    ContractorDocumentFileDAO contractorAuditFileDAO;
	@Mock
	private DocumentDecisionTableDAO auditDecisionTableDAO;
	@Mock
	private AppTranslationDAO appTranslationDAO;
    @Mock
    DocumentTypeDAO auditTypeDao;
    @Mock
    ContractorDocument audit;
    @Mock
    AuditType auditType;

	ContractorAccount contractor;
	OperatorAccount operator;

	/**
	 * Setup Contractors and Audit Types and Categories
	 */
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		auditBuilder = new AuditBuilder2();
		autowireEMInjectedDAOs(auditBuilder);
		PicsTestUtil.forceSetPrivateField(auditBuilder, "typeRuleCache", typeRuleCache);
		PicsTestUtil.forceSetPrivateField(auditBuilder, "categoryRuleCache", catRuleCache);
		PicsTestUtil.forceSetPrivateField(auditBuilder, "auditPercentCalculator", auditPercentCalculator);
        PicsTestUtil.forceSetPrivateField(auditBuilder, "auditPeriodService", auditPeriodService);
        PicsTestUtil.forceSetPrivateField(auditBuilder, "auditTypeDao", auditTypeDao);

		Whitebox.setInternalState(typeRuleCache, "auditDecisionTableDAO", auditDecisionTableDAO);
		Whitebox.setInternalState(catRuleCache, "auditDecisionTableDAO", auditDecisionTableDAO);
		Whitebox.setInternalState(auditBuilder, "appTranslationDAO", appTranslationDAO);
		Whitebox.setInternalState(auditBuilder, "conAuditDao", conAuditDao);
		Whitebox.setInternalState(auditBuilder, "contractorAuditFileDAO", contractorAuditFileDAO);

        AuditTypesBuilder typesBuilder = new AuditTypesBuilder();
        Whitebox.setInternalState(auditBuilder, "typesBuilder", typesBuilder);

        typeRules.clear();
		typeRuleCache.clear();
		catRules.clear();
		catRuleCache.clear();

		contractor = EntityFactory.makeContractor();
		operator = EntityFactory.makeOperator();
		AccountService.getOperatorAccounts(contractor).add(operator);
		EntityFactory.addContractorOperator(contractor, operator);
	}

	@Ignore
	public void testBuildAudits_WCB() throws Exception {
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
	public void testAuditTypeBuilderCategoryBuilder() throws Exception {
		// set up audit type
		AuditType pqfType = EntityFactory.makeAuditType(AuditType.PQF);
		EntityFactory.addCategories(pqfType, 101, "PQF Category 1");
		EntityFactory.addCategories(pqfType, 102, "PQF Category 2");

		// set up rules
		addTypeRules((new RuleParameters()).setAuditType(pqfType));
		for (DocumentCategory category : pqfType.getCategories()) {
			addCategoryRules((new RuleParameters()).setAuditType(pqfType).setDocumentCategory(category));
		}

		AuditTypesBuilder typeBuilder = new AuditTypesBuilder();
        typeBuilder.setRuleCache(typeRuleCache);
        typeBuilder.setContractor(contractor);

		AuditCategoriesBuilder categoryBuilder = new AuditCategoriesBuilder(catRuleCache, contractor);

		Set<AuditTypeDetail> auditTypes = typeBuilder.calculate();
		assertEquals(1, auditTypes.size());

		ContractorDocument conAudit = EntityFactory.makeContractorAudit(pqfType, contractor);
		Set<DocumentCategory> categories = categoryBuilder.calculate(conAudit, AccountService.getOperatorAccounts(contractor));
		assertEquals(2, categories.size());
	}

	@Test
	public void testBuildAudits_WelcomeCall() throws Exception {
		AuditType auditType = EntityFactory.makeAuditType(AuditType.WELCOME);
		WorkflowStep pendingStep = new WorkflowStep();
		pendingStep.setNewStatus(DocumentStatus.Pending);

		Workflow workflow = new Workflow();
		workflow.getSteps().add(pendingStep);
		auditType.setWorkFlow(workflow);

		addTypeRules((new RuleParameters()).setAuditTypeId(auditType.getId()));
		addCategoryRules(null);

		when(em.find(Matchers.argThat(equalTo(AuditType.class)), anyInt())).thenReturn(auditType);
		PicsTestUtil.forceSetPrivateField(auditBuilder, "conAuditDao", conAuditDao);
		when(conAuditDao.isNeedsWelcomeCall(anyInt())).thenReturn(true);
		when(conAuditDao.find(Matchers.argThat(equalTo(AuditType.class)), anyInt())).thenReturn(auditType);

		auditBuilder.buildAudits(contractor);
		assertEquals(1, contractor.getAudits().size());

        // test no duplicates
        auditBuilder.buildAudits(contractor);
        assertEquals(1, contractor.getAudits().size());
	}

	@Test
	public void testBuildAudits_AnnualUpdates() throws Exception {
		addTypeRules((new RuleParameters()).setAuditTypeId(AuditType.ANNUALADDENDUM));
		addCategoryRules(null);

		when(conAuditDao.find(Matchers.argThat(equalTo(AuditType.class)), anyInt())).thenReturn(
				EntityFactory.makeAuditType(AuditType.ANNUALADDENDUM));

		auditBuilder.buildAudits(contractor);
		assertEquals(3, contractor.getAudits().size());
	}

    @Test
    public void testBuildAudits_Monthly() throws Exception {
        AuditType auditType = EntityFactory.makeAuditType(1000);
        auditType.setPeriod(DocumentTypePeriod.Monthly);
        when(conAuditDao.find(Matchers.argThat(equalTo(AuditType.class)), anyInt())).thenReturn(
		        auditType);

        addTypeRules((new RuleParameters()).setAuditType(auditType));
        addCategoryRules(null);

        Calendar date = Calendar.getInstance();
        date.set(Calendar.YEAR, 2012);
        date.set(Calendar.DAY_OF_MONTH, 1);

        for (int month=0; month<12; month++) {
            date.set(Calendar.MONTH, month);
            auditBuilder.setToday(date.getTime());
            auditBuilder.buildAudits(contractor);
            assertEquals(month + 1, contractor.getAudits().size());
            String auditFor = contractor.getAudits().get(contractor.getAudits().size() - 1).getAuditFor();
            String[] parts = auditFor.split("-");
            if (month == 0) {
                assertEquals(12, Integer.parseInt(parts[1]));
            } else {
                assertEquals(month, Integer.parseInt(parts[1]));
            }
        }
    }

    @Test
    public void testBuildAudits_Quarterly() throws Exception {
        AuditType auditType = EntityFactory.makeAuditType(1000);
        auditType.setPeriod(DocumentTypePeriod.Quarterly);
        when(conAuditDao.find(Matchers.argThat(equalTo(AuditType.class)), anyInt())).thenReturn(
		        auditType);

        addTypeRules((new RuleParameters()).setAuditType(auditType));
        addCategoryRules(null);

        Calendar date = Calendar.getInstance();
        date.set(Calendar.YEAR, 2012);
        date.set(Calendar.DAY_OF_MONTH, 1);

        for (int quarter=1; quarter<=4; quarter++) {
            int month = (quarter - 1) * 3;
            date.set(Calendar.MONTH, month);
            auditBuilder.setToday(date.getTime());
            auditBuilder.buildAudits(contractor);
            assertEquals(quarter, contractor.getAudits().size());

            date.set(Calendar.MONTH, month + 1);
            auditBuilder.setToday(date.getTime());
            auditBuilder.buildAudits(contractor);
            assertEquals(quarter, contractor.getAudits().size());

            date.set(Calendar.MONTH, month + 2);
            auditBuilder.setToday(date.getTime());
            auditBuilder.buildAudits(contractor);
            assertEquals(quarter, contractor.getAudits().size());

            String auditFor = contractor.getAudits().get(contractor.getAudits().size() - 1).getAuditFor();
            String[] parts = auditFor.split(":");
            if (quarter == 1) {
                assertEquals(4, Integer.parseInt(parts[1]));
            } else {
                assertEquals(quarter - 1, Integer.parseInt(parts[1]));
            }
        }
    }

    @Test
    public void testBuildAudits_Yearly() throws Exception {
        AuditType auditType = EntityFactory.makeAuditType(1000);
        auditType.setPeriod(DocumentTypePeriod.Yearly);
        when(conAuditDao.find(Matchers.argThat(equalTo(AuditType.class)), anyInt())).thenReturn(
		        auditType);

        addTypeRules((new RuleParameters()).setAuditType(auditType));
        addCategoryRules(null);

        Calendar date = Calendar.getInstance();
        date.set(Calendar.MONTH, 0);
        date.set(Calendar.DAY_OF_MONTH, 1);

        for (int year=2012; year<2015; year++) {
            date.set(Calendar.YEAR, year);
            auditBuilder.setToday(date.getTime());
            auditBuilder.buildAudits(contractor);
            assertEquals(year - 2011, contractor.getAudits().size());
            String auditFor = contractor.getAudits().get(contractor.getAudits().size() - 1).getAuditFor();
            assertEquals(year - 1, Integer.parseInt(auditFor));
        }
    }

    @Test
    public void testAuditBuilderDate_NoDateSet() {
        Calendar cal = Calendar.getInstance();
        Calendar compareCal = Calendar.getInstance();
        AuditBuilder2 builder = new AuditBuilder2();
        Date date = builder.getToday();
        compareCal.setTime(date);
        assertEquals(cal.get(Calendar.YEAR), compareCal.get(Calendar.YEAR));
        assertEquals(cal.get(Calendar.MONTH), compareCal.get(Calendar.MONTH));
    }

    @Test
    public void testAuditBuilderDate_DateSet() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1);
        cal.add(Calendar.MONTH, -1);
        Calendar compareCal = Calendar.getInstance();
        AuditBuilder2 builder = new AuditBuilder2();
        builder.setToday(cal.getTime());
        Date date = builder.getToday();
        compareCal.setTime(date);
        assertEquals(cal.get(Calendar.YEAR), compareCal.get(Calendar.YEAR));
        assertEquals(cal.get(Calendar.MONTH), compareCal.get(Calendar.MONTH));
    }

    @Test
	public void testBuildAudits_ReviewCompetency() throws Exception {
		addTypeRules((new RuleParameters()).setAuditTypeId(AuditType.INTEGRITYMANAGEMENT));
		addCategoryRules(null);

		AuditType implementationType = EntityFactory.makeAuditType(AuditType.INTEGRITYMANAGEMENT);
		implementationType.setRenewable(false);

		when(em.find(Matchers.argThat(equalTo(AuditType.class)), anyInt())).thenReturn(implementationType);
		when(conAuditDao.find(Matchers.argThat(equalTo(AuditType.class)), anyInt())).thenReturn(implementationType);

		auditBuilder.buildAudits(contractor);
		assertEquals(1, contractor.getAudits().size());
	}

	@Test
	public void testBuildAudits_IsValidAudit_NullData() throws Exception {
		ContractorDocument corAudit = EntityFactory.makeContractorAudit(AuditType.COR, contractor);
		ContractorDocument pqfAudit = setupTestAudit();
		contractor.getAudits().add(pqfAudit);
		contractor.getAudits().add(corAudit);
		OperatorAccount operator = EntityFactory.makeOperator();
		EntityFactory.addCao(corAudit, operator);

		DocumentQuestion question = EntityFactory.makeAuditQuestion();
		question.setId(DocumentQuestion.COR);
		DocumentCatData documentCatData = EntityFactory.makeAuditCatData();
		documentCatData.setCategory(question.getCategory());
		documentCatData.setApplies(true);
		pqfAudit.getCategories().add(documentCatData);
		pqfAudit.setCreationDate(new Date());

		addTypeRules((new RuleParameters()).setAuditTypeId(AuditType.PQF));
		addCategoryRules(null);

		PicsTestUtil.forceSetPrivateField(auditBuilder, "auditDataDAO", auditDataDao);

		when(em.find(Matchers.argThat(equalTo(AuditType.class)), anyInt())).thenReturn(
				EntityFactory.makeAuditType(AuditType.PQF));

		auditBuilder.buildAudits(contractor);
		assertEquals(1, corAudit.getOperatorsVisible().size());
	}

	@Test
	public void testBuildAudits_IsValidAudit_Cor_Yes() throws Exception {
		ContractorDocument corAudit = EntityFactory.makeContractorAudit(AuditType.COR, contractor);
		ContractorDocument pqfAudit = setupTestAudit();
		contractor.getAudits().add(pqfAudit);
		contractor.getAudits().add(corAudit);
		OperatorAccount operator = EntityFactory.makeOperator();
		EntityFactory.addCao(corAudit, operator);

		DocumentData documentData = EntityFactory.makeAuditData("Yes");
		DocumentQuestion question = EntityFactory.makeAuditQuestion();

		DocumentCatData documentCatData = EntityFactory.makeAuditCatData();
		documentCatData.setCategory(question.getCategory());
		documentCatData.setApplies(false);

		question.setId(DocumentQuestion.COR);
		documentData.setQuestion(question);
		pqfAudit.getCategories().add(documentCatData);
		pqfAudit.setCreationDate(new Date());
		pqfAudit.getData().add(documentData);

		addTypeRules((new RuleParameters()).setAuditTypeId(AuditType.PQF));
		addCategoryRules(null);

		when(em.find(Matchers.argThat(equalTo(AuditType.class)), anyInt())).thenReturn(
				EntityFactory.makeAuditType(AuditType.PQF));

		auditBuilder.buildAudits(contractor);
		assertEquals(0, corAudit.getOperatorsVisible().size());
	}

	@Test
	public void testBuildAudits_IsValidAudit_Cor_No() throws Exception {
		ContractorDocument corAudit = EntityFactory.makeContractorAudit(AuditType.COR, contractor);
		ContractorDocument pqfAudit = setupTestAudit();
		contractor.getAudits().add(pqfAudit);
		contractor.getAudits().add(corAudit);
		OperatorAccount operator = EntityFactory.makeOperator();
		EntityFactory.addCao(corAudit, operator);

		DocumentData documentData = EntityFactory.makeAuditData("No");
		DocumentQuestion question = EntityFactory.makeAuditQuestion();
		DocumentCatData documentCatData = EntityFactory.makeAuditCatData();

		question.setId(DocumentQuestion.COR);
		documentData.setQuestion(question);
		documentCatData.setCategory(question.getCategory());
		documentCatData.setApplies(true);
		pqfAudit.getCategories().add(documentCatData);
		pqfAudit.setCreationDate(new Date());
		pqfAudit.getData().add(documentData);

		addTypeRules((new RuleParameters()).setAuditTypeId(AuditType.PQF));
		addCategoryRules(null);

		when(em.find(Matchers.argThat(equalTo(AuditType.class)), anyInt())).thenReturn(
				EntityFactory.makeAuditType(AuditType.PQF));

		auditBuilder.buildAudits(contractor);
		assertEquals(0, corAudit.getOperatorsVisible().size());
	}

	@Test
	public void testBuildAudits_IsValidAudit_Iec_Yes() throws Exception {
		ContractorDocument iecAudit = EntityFactory.makeContractorAudit(AuditType.IEC_AUDIT, contractor);
		ContractorDocument pqfAudit = setupTestAudit();
		contractor.getAudits().add(pqfAudit);
		contractor.getAudits().add(iecAudit);
		OperatorAccount operator = EntityFactory.makeOperator();
		EntityFactory.addCao(iecAudit, operator);

		DocumentData documentData = EntityFactory.makeAuditData("Yes");
		DocumentQuestion question = EntityFactory.makeAuditQuestion();
		DocumentCatData documentCatData = EntityFactory.makeAuditCatData();

		question.setId(DocumentQuestion.IEC);
		documentData.setQuestion(question);
		documentCatData.setCategory(question.getCategory());
		documentCatData.setApplies(false);
		pqfAudit.getCategories().add(documentCatData);
		pqfAudit.setCreationDate(new Date());
		pqfAudit.getData().add(documentData);

		addTypeRules((new RuleParameters()).setAuditTypeId(AuditType.PQF));
		addCategoryRules(null);

		when(em.find(Matchers.argThat(equalTo(AuditType.class)), anyInt())).thenReturn(
				EntityFactory.makeAuditType(AuditType.PQF));

		auditBuilder.buildAudits(contractor);
		assertEquals(0, iecAudit.getOperatorsVisible().size());
	}

	@Test
	public void testWelcomeCallValidity() throws Exception {
		ContractorDocument audit;
		Calendar creationDate;
		Boolean result;

		ContractorAccount con = EntityFactory.makeContractor();
		audit = EntityFactory.makeContractorAudit(AuditType.WELCOME, con);

		creationDate = Calendar.getInstance();
		audit.setCreationDate(creationDate.getTime());
		audit.setExpiresDate(null);
		result = Whitebox.invokeMethod(auditBuilder, "isValidAudit", audit);
		assertTrue(result);
		assertNull(audit.getExpiresDate());

		creationDate = Calendar.getInstance();
		creationDate.add(Calendar.MONTH, -2);
		audit.setCreationDate(creationDate.getTime());
		audit.setExpiresDate(null);
		result = Whitebox.invokeMethod(auditBuilder, "isValidAudit", audit);
		assertTrue(result);
		assertNotNull(audit.getExpiresDate());

	}

	@Test
	public void testBuildAudits_IsValidAudit_Iec__No() throws Exception {
		ContractorDocument iecAudit = EntityFactory.makeContractorAudit(AuditType.IEC_AUDIT, contractor);
		ContractorDocument pqfAudit = setupTestAudit();
		contractor.getAudits().add(pqfAudit);
		contractor.getAudits().add(iecAudit);
		OperatorAccount operator = EntityFactory.makeOperator();
		EntityFactory.addCao(iecAudit, operator);

		DocumentData documentData = EntityFactory.makeAuditData("No");
		DocumentQuestion question = EntityFactory.makeAuditQuestion();
		DocumentCatData documentCatData = EntityFactory.makeAuditCatData();

		question.setId(DocumentQuestion.IEC);
		documentData.setQuestion(question);
		documentCatData.setCategory(question.getCategory());
		documentCatData.setApplies(true);
		pqfAudit.getCategories().add(documentCatData);
		pqfAudit.setCreationDate(new Date());
		pqfAudit.getData().add(documentData);

		addTypeRules((new RuleParameters()).setAuditTypeId(AuditType.PQF));
		addCategoryRules(null);

		when(em.find(Matchers.argThat(equalTo(AuditType.class)), anyInt())).thenReturn(
				EntityFactory.makeAuditType(AuditType.PQF));

		auditBuilder.buildAudits(contractor);
		assertEquals(0, iecAudit.getOperatorsVisible().size());
	}

	@Test
	public void testCaoSplit() throws Exception {
		ContractorDocument conAudit = setupTestAudit();
		conAudit.getAuditType().setCanOperatorView(true);

		ContractorDocumentOperator cao1 = EntityFactory.addCao(conAudit, EntityFactory.makeOperator());
		// ContractorAuditOperator cao2 =
		// EntityFactory.makeContractorAuditOperator(conAudit,
		// AuditStatus.Pending);
		// cao2.setOperator(EntityFactory.makeOperator());
		cao1.setId(52);

		ContractorDocumentOperatorPermission caop1 = new ContractorDocumentOperatorPermission();
		ContractorDocumentOperatorPermission caop2 = new ContractorDocumentOperatorPermission();

		setupCaoCaop(cao1, caop1, DocumentStatus.Complete);
		setupCaoCaop(cao1, caop2, DocumentStatus.Complete);
		caop2.setOperator(EntityFactory.makeOperator());
		cao1.setStatus(DocumentStatus.Approved);

		Map<OperatorAccount, Set<OperatorAccount>> caoMap = new HashMap<OperatorAccount, Set<OperatorAccount>>();
		Set<OperatorAccount> caops = new HashSet<OperatorAccount>();
		caops.add(cao1.getOperator());
		caoMap.put(caop1.getOperator(), caops);
		caops = new HashSet<OperatorAccount>();
		caops.add(caop2.getOperator());
		caoMap.put(caop2.getOperator(), caops);

		Whitebox.invokeMethod(auditBuilder, "fillAuditOperators", conAudit, caoMap);
		assertEquals(2, conAudit.getOperators().size());
		for (ContractorDocumentOperator cao : conAudit.getOperators()) {
			assertEquals(DocumentStatus.Approved, cao.getStatus());
		}
	}

	@Test
	public void testNewOperatorPrevAllComplete() throws Exception {
		ContractorDocument conAudit = setupTestAudit();

		ContractorDocumentOperator cao1 = EntityFactory.addCao(conAudit, EntityFactory.makeOperator());
		ContractorDocumentOperator cao2 = EntityFactory.makeContractorAuditOperator(conAudit, DocumentStatus.Pending);
		cao2.setOperator(EntityFactory.makeOperator());

		ContractorDocumentOperatorPermission caop1 = new ContractorDocumentOperatorPermission();
		ContractorDocumentOperatorPermission caop2 = new ContractorDocumentOperatorPermission();

		setupCaoCaop(cao1, caop1, DocumentStatus.Complete);
		setupCaoCaop(cao2, caop2, DocumentStatus.Pending);

		Map<OperatorAccount, Set<OperatorAccount>> caoMap = new HashMap<>();
		Set<OperatorAccount> caops = new HashSet<>();
		caops.add(cao1.getOperator());
		caoMap.put(cao1.getOperator(), caops);
		caops = new HashSet<>();
		caops.add(cao2.getOperator());
		caoMap.put(cao2.getOperator(), caops);

		Whitebox.invokeMethod(auditBuilder, "fillAuditOperators", conAudit, caoMap);
		assertEquals(2, conAudit.getOperators().size());

		Set<DocumentCategory> categoriesNeeded = new HashSet<>();
		categoriesNeeded.addAll(conAudit.getAuditType().getCategories());
		Whitebox.invokeMethod(auditBuilder, "fillAuditCategories", conAudit, categoriesNeeded);
		for (DocumentCatData catData : conAudit.getCategories()) {
			assertTrue(catData.isApplies());
		}

	}

	@Test
	public void testMultipleCaoToOneCaoStatusMerge() throws Exception {
		ContractorDocument conAudit = setupTestAudit();
		conAudit.getAuditType().setCanOperatorView(true);
		conAudit.getAuditType().setId(AuditType.INTEGRITYMANAGEMENT);
		conAudit.getAuditType().setClassType(AuditTypeClass.PQF);

		ContractorDocumentOperator cao1 = EntityFactory.addCao(conAudit, EntityFactory.makeOperator());
		ContractorDocumentOperator cao2 = EntityFactory.addCao(conAudit, EntityFactory.makeOperator());
		cao1.setId(1);
		cao2.setId(2);

		ContractorDocumentOperatorPermission caop1 = new ContractorDocumentOperatorPermission();
		ContractorDocumentOperatorPermission caop2 = new ContractorDocumentOperatorPermission();

		setupCaoCaop(cao1, caop1, DocumentStatus.Complete);
		setupCaoCaop(cao2, caop2, DocumentStatus.Pending);

		Map<OperatorAccount, Set<OperatorAccount>> caoMap = new HashMap<OperatorAccount, Set<OperatorAccount>>();
		Set<OperatorAccount> caops = new HashSet<OperatorAccount>();
		caops.add(cao1.getOperator());
		caops.add(cao2.getOperator());
		caoMap.put(EntityFactory.makeOperator(), caops);

		Whitebox.invokeMethod(auditBuilder, "fillAuditOperators", conAudit, caoMap);
		assertEquals(3, conAudit.getOperators().size());
		for (ContractorDocumentOperator cao : conAudit.getOperators()) {
			if (cao.equals(cao1)) {
				assertFalse(cao.isVisible());
			} else if (cao.equals(cao2)) {
				assertFalse(cao.isVisible());
			} else {
				assertTrue(cao.isVisible());
				assertEquals(DocumentStatus.Complete, cao.getStatus());
			}
		}
	}

	private ContractorDocument setupTestAudit() {
		ContractorDocument audit = EntityFactory.makeContractorAudit(AuditType.PQF, contractor);
		WorkflowStep pendingStep = new WorkflowStep();
		pendingStep.setNewStatus(DocumentStatus.Pending);

		Workflow workflow = new Workflow();
		workflow.getSteps().add(pendingStep);
		audit.getAuditType().setWorkFlow(workflow);

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, -1);
		audit.setCreationDate(cal.getTime());

		DocumentCategory cat1 = EntityFactory.makeAuditCategory(1);
		DocumentCategory cat2 = EntityFactory.makeAuditCategory(2);

		audit.getAuditType().getCategories().add(cat1);
		audit.getAuditType().getCategories().add(cat2);

		DocumentCatData catData1 = EntityFactory.addCategories(audit, 1);
		DocumentCatData catData2 = EntityFactory.addCategories(audit, 2);

		catData1.setNumAnswered(catData1.getNumRequired());
		catData1.setApplies(true);
		catData2.setApplies(false);

		audit.getCategories().add(catData1);
		audit.getCategories().add(catData2);

		return audit;
	}

	private void setupCaoCaop(ContractorDocumentOperator cao, ContractorDocumentOperatorPermission caop, DocumentStatus status) {
		caop.setCao(cao);
		caop.setOperator(cao.getOperator());
		cao.getCaoPermissions().add(caop);
		cao.setStatus(status);
		if (status.isComplete()) {
			cao.setPercentComplete(100);
			cao.setPercentVerified(100);
		}
	}

	@Test(expected = RuntimeException.class)
	public void testFoundCurrentYearWCB_NullAuditFor() throws Exception {
		ContractorDocument audit = mock(ContractorDocument.class);
		when(audit.getAuditFor()).thenReturn(null);

		Boolean result = Whitebox.invokeMethod(auditBuilder, "foundCurrentYearWCB", audit);
	}

	@Test(expected = RuntimeException.class)
	public void testFoundCurrentYearWCB_BlankAuditFor() throws Exception {
		ContractorDocument audit = mock(ContractorDocument.class);
		when(audit.getAuditFor()).thenReturn(" ");

		Boolean result = Whitebox.invokeMethod(auditBuilder, "foundCurrentYearWCB", audit);
		assertTrue(result);
	}

	@Ignore
	public void testFoundCurrentYearWCB_CurrentYearAuditFor() throws Exception {
		ContractorDocument audit = mock(ContractorDocument.class);
		ContractorAccount contractor = mock(ContractorAccount.class);

		List<ContractorDocument> audits = buildMockAudits();
		when(contractor.getAudits()).thenReturn(audits);
		when(audit.getContractorAccount()).thenReturn(contractor);
		when(audit.getAuditFor()).thenReturn(DateBean.getWCBYear());
        AuditType auditType1 = new AuditType();
        auditType1.setId(145);
        when(audit.getAuditType()).thenReturn(auditType1);

		Boolean result = Whitebox.invokeMethod(auditBuilder, "foundCurrentYearWCB", audit);
		assertTrue(result);
	}

	@Ignore
	public void testFoundCurrentYearWCB_PreviousYearAuditFor() throws Exception {
		ContractorDocument audit = mock(ContractorDocument.class);
		ContractorAccount contractor = mock(ContractorAccount.class);

		when(contractor.getAudits()).thenReturn(null);
		when(audit.getContractorAccount()).thenReturn(contractor);
		when(audit.getAuditFor()).thenReturn(DateBean.getWCBYear());

		Boolean result = Whitebox.invokeMethod(auditBuilder, "foundCurrentYearWCB", audit);
		assertFalse(result);
	}

	@Ignore
	public void testFindAllWCBAuditYears_NoAudits() throws Exception {
		ContractorDocument audit = mock(ContractorDocument.class);
		ContractorAccount contractor = mock(ContractorAccount.class);
		when(contractor.getAudits()).thenReturn(null);

		List<String> result = Whitebox.invokeMethod(auditBuilder, "findAllWCBAuditYears", contractor, audit);
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	@Ignore
	public void testFindAllWCBAuditYears() throws Exception {
		ContractorDocument wcbAudit = mock(ContractorDocument.class);
		ContractorAccount contractor = mock(ContractorAccount.class);
		List<ContractorDocument> audits = buildMockAudits();
		when(contractor.getAudits()).thenReturn(audits);
        AuditType auditType1 = new AuditType();
        auditType1.setId(145);
        when(wcbAudit.getAuditType()).thenReturn(auditType1);

		List<String> result = Whitebox.invokeMethod(auditBuilder, "findAllWCBAuditYears", contractor, wcbAudit);
		assertNotNull(result);
		assertTrue(result.contains("2011"));
		assertTrue(result.contains(DateBean.getWCBYear()));
	}

	@Test
	public void testRemoveUnneededAudits_dontRemoveManuallyAddedAudits() throws Exception {
		Set<AuditType> requiredAuditTypes = new HashSet<>();
		ContractorDocument contractorDocument = setupMockContractorAudit(DocumentStatus.Pending, 0);
		when(contractorDocument.isManuallyAdded()).thenReturn(true);

		Whitebox.invokeMethod(auditBuilder, "removeUnneededAudits", contractor, requiredAuditTypes);

        verify(contractorAuditFileDAO, never()).removeAllByAuditID(anyInt());
		verify(conAuditDao, never()).remove(any(ContractorDocument.class));
	}

	@Test
	public void testRemoveUnneededAudits_dontRemoveRequiredAudits() throws Exception {
		HashSet requiredAuditTypes = mock(HashSet.class);
		ContractorDocument contractorDocument = setupMockContractorAudit(DocumentStatus.Pending, 0);
		when(requiredAuditTypes.contains(contractorDocument.getAuditType())).thenReturn(true);

		Whitebox.invokeMethod(auditBuilder, "removeUnneededAudits", contractor, requiredAuditTypes);

        verify(contractorAuditFileDAO, never()).removeAllByAuditID(anyInt());
        verify(conAuditDao, never()).remove(any(ContractorDocument.class));
	}

	@Test
	public void testRemoveUnneededAudits_dontRemovePqfs() throws Exception {
		Set<AuditType> requiredAuditTypes = new HashSet<>();
		ContractorDocument contractorDocument = setupMockContractorAudit(DocumentStatus.Pending, 0);
        AuditType auditType1 = new AuditType();
        auditType1.setId(AuditType.PQF);
        when(contractorDocument.getAuditType()).thenReturn(auditType1);

		Whitebox.invokeMethod(auditBuilder, "removeUnneededAudits", contractor, requiredAuditTypes);

        verify(contractorAuditFileDAO, never()).removeAllByAuditID(anyInt());
        verify(conAuditDao, never()).remove(any(ContractorDocument.class));
	}

	@Test
	public void testRemoveUnneededAudits_dontRemoveWcbs() throws Exception {
		Set<AuditType> requiredAuditTypes = new HashSet<>();
		ContractorDocument contractorDocument = setupMockContractorAudit(DocumentStatus.Pending, 0);
		when(contractorDocument.getAuditType().getId()).thenReturn(169);

		Whitebox.invokeMethod(auditBuilder, "removeUnneededAudits", contractor, requiredAuditTypes);

        verify(contractorAuditFileDAO, never()).removeAllByAuditID(anyInt());
        verify(conAuditDao, never()).remove(any(ContractorDocument.class));
	}

	@Test
	public void testRemoveUnneededAudits_dontRemoveScheduledAudits() throws Exception {
		Set<AuditType> requiredAuditTypes = new HashSet<>();
		ContractorDocument contractorDocument = setupMockContractorAudit(DocumentStatus.Pending, 0);
		when(contractorDocument.getScheduledDate()).thenReturn(new Date());

		Whitebox.invokeMethod(auditBuilder, "removeUnneededAudits", contractor, requiredAuditTypes);

        verify(contractorAuditFileDAO, never()).removeAllByAuditID(anyInt());
        verify(conAuditDao, never()).remove(any(ContractorDocument.class));
	}

	@Test
	public void testRemoveUnneededAudits_dontRemovePostPendingAudits() throws Exception {
		Set<AuditType> requiredAuditTypes = new HashSet<>();
		ContractorDocument contractorDocument = setupMockContractorAudit(DocumentStatus.Approved, 0);

		Whitebox.invokeMethod(auditBuilder, "removeUnneededAudits", contractor, requiredAuditTypes);

        verify(contractorAuditFileDAO, never()).removeAllByAuditID(anyInt());
        verify(conAuditDao, never()).remove(any(ContractorDocument.class));
	}

	@Test
	public void testRemoveUnneededAudits_dontRemovePartiallyCompleteAudits() throws Exception {
		Set<AuditType> requiredAuditTypes = new HashSet<>();
		ContractorDocument contractorDocument = setupMockContractorAudit(DocumentStatus.Pending, 10);

		Whitebox.invokeMethod(auditBuilder, "removeUnneededAudits", contractor, requiredAuditTypes);

        verify(contractorAuditFileDAO, never()).removeAllByAuditID(anyInt());
        verify(conAuditDao, never()).remove(any(ContractorDocument.class));
	}

	@Test
	public void testRemoveUnneededAudits_dontRemoveAuditsWithEmptyData() throws Exception {
		Set<AuditType> requiredAuditTypes = new HashSet<>();
		ContractorDocument contractorDocument = setupMockContractorAudit(DocumentStatus.Pending, 0);

		List<DocumentData> emptyDocumentData = new ArrayList<>();
		when(contractorDocument.getData()).thenReturn(emptyDocumentData);

		Whitebox.invokeMethod(auditBuilder, "removeUnneededAudits", contractor, requiredAuditTypes);

        verify(contractorAuditFileDAO, never()).removeAllByAuditID(anyInt());
        verify(conAuditDao, never()).remove(any(ContractorDocument.class));
	}

	@Test
	public void testRemoveUnneededAudits_dontRemovePreviousAudits() throws Exception {
		Set<AuditType> requiredAuditTypes = new HashSet<>();
		ContractorDocument contractorDocument = setupMockContractorAudit(DocumentStatus.Pending, 0);

		List<DocumentData> documentData = new ArrayList<>();
		documentData.add(new DocumentData());
		when(contractorDocument.getData()).thenReturn(documentData);

		List<ContractorDocument> subsequentAudits = new ArrayList<>();
		subsequentAudits.add(new ContractorDocument());
		when(conAuditDao.findSubsequentAudits(contractorDocument)).thenReturn(subsequentAudits);

		Whitebox.invokeMethod(auditBuilder, "removeUnneededAudits", contractor, requiredAuditTypes);

        verify(contractorAuditFileDAO, never()).removeAllByAuditID(anyInt());
        verify(conAuditDao, never()).remove(any(ContractorDocument.class));
	}

    @Test
    public void testIsAuditThatCanAdjustStatus() throws Exception {
        AuditType auditType = mock(AuditType.class);
        when(audit.getAuditType()).thenReturn(auditType);
        when(auditType.getClassType()).thenReturn(AuditTypeClass.Policy);
        boolean canAdjust = Whitebox.invokeMethod(auditBuilder, "isAuditThatCanAdjustStatus", audit);
        assertFalse(canAdjust);
    }

    private ContractorDocument setupMockContractorAudit(DocumentStatus documentStatus, int percentComplete) {
		List<ContractorDocument> contractorDocuments = new ArrayList<>();
		ContractorDocument contractorDocument = mock(ContractorDocument.class);
		contractorDocuments.add(contractorDocument);
		contractor.setAudits(contractorDocuments);

		AuditType auditType = mock(AuditType.class);
		when(contractorDocument.getAuditType()).thenReturn(auditType);

		List<ContractorDocumentOperator> operators = new ArrayList<>();
		ContractorDocumentOperator operator = mock(ContractorDocumentOperator.class);
		operators.add(operator);
		when(contractorDocument.getOperators()).thenReturn(operators);
		when(operator.getStatus()).thenReturn(documentStatus);
		when(operator.getPercentComplete()).thenReturn(percentComplete);

		return contractorDocument;
	}

	private List<ContractorDocument> buildMockAudits() {
		List<ContractorDocument> audits = new ArrayList<ContractorDocument>();
		audits.add(buildMockWCBAudit(145, "2011"));
		audits.add(buildMockWCBAudit(145, DateBean.getWCBYear()));

		return audits;
	}

	private ContractorDocument buildMockWCBAudit(int auditTypeId, String auditFor) {
		ContractorDocument audit = mock(ContractorDocument.class);
        AuditType auditType1 = new AuditType();
        auditType1.setId(auditTypeId);
        when(audit.getAuditType()).thenReturn(auditType1);
		when(audit.getAuditFor()).thenReturn(auditFor);

		return audit;
	}

	private void addTypeRules(RuleParameters params) throws Exception {
		DocumentTypeRule rule = new DocumentTypeRule();
		if (params != null) {
			fillAuditRule(params, rule);
			rule.setManuallyAdded(params.manuallyAdded);
			rule.setDependentDocumentStatus(params.dependentDocumentStatus);
			rule.setDependentAuditType(params.dependentAuditType);
		}

		typeRules.add(rule);
		Whitebox.invokeMethod(typeRuleCache, "initialize", typeRules);
	}

	private void addCategoryRules(RuleParameters params) throws Exception {
		DocumentCategoryRule rule = new DocumentCategoryRule();
		if (params != null) {
			fillAuditRule(params, rule);
			rule.setDocumentCategory(params.documentCategory);
			rule.setRootCategory(params.rootCategory);
		}

		catRules.add(rule);
		Whitebox.invokeMethod(catRuleCache, "initialize", catRules);
	}

	private void fillAuditRule(RuleParameters params, DocumentRule rule) {
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
		protected DocumentQuestion question;
		protected QuestionComparator questionComparator;
		protected String questionAnswer;
		protected Boolean soleProprietor;
		protected AccountLevel accountLevel;
		private AuditType dependentAuditType;
		private DocumentStatus dependentDocumentStatus;
		private boolean manuallyAdded = false;
		public DocumentCategory documentCategory;
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

		public RuleParameters setQuestion(DocumentQuestion question) {
			this.question = question;
			return this;
		}

		public RuleParameters setQuestionComparator(QuestionComparator questionComparator) {
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

		public RuleParameters setDocumentCategory(DocumentCategory documentCategory) {
			this.documentCategory = documentCategory;
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

		public RuleParameters setDependentDocumentStatus(DocumentStatus dependentDocumentStatus) {
			this.dependentDocumentStatus = dependentDocumentStatus;
			return this;
		}

		public RuleParameters setManuallyAdded(boolean manuallyAdded) {
			this.manuallyAdded = manuallyAdded;
			return this;
		}
	}
}
