package com.picsauditing.auditbuilder;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.auditbuilder.dao.DocumentDataDAO;
import com.picsauditing.auditbuilder.dao.DocumentDecisionTableDAO;
import com.picsauditing.auditbuilder.entities.*;
import com.picsauditing.auditbuilder.util.AnswerMap;
import com.picsauditing.auditbuilder.util.DateBean;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.slf4j.Logger;

import java.util.*;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.startsWith;
import static org.mockito.Mockito.*;

public class DocumentPercentCalculatorTest {
	private DocumentPercentCalculator calculator;
	private ContractorAccount contractor;
	private ContractorDocument audit;
	private AuditType auditType;

	DocumentCategoryRuleCache catRuleCache = new DocumentCategoryRuleCache();
	List<DocumentCategoryRule> catRules = new ArrayList<>();
	DocumentCategoriesBuilder catBuilder;

	private DocumentCatData acd1;
	private DocumentCatData acd2;
	private DocumentCatData acd3;
	private DocumentCatData acd4;
	private List<DocumentCatData> documentCatDataList = new ArrayList<>();
	private List<DocumentCategory> documentCategoryList = new ArrayList<>();

	@Mock private Logger logger;
	@Mock private DocumentDataDAO auditDataDAO;
	@Mock private DocumentCatData catData;
	@Mock private DocumentCategory category;
	@Mock private AuditType mockAuditType;
	@Mock private ContractorDocument contractorDocument;
	@Mock private ContractorDocumentOperator contractorDocumentOperator;
    @Mock
    private DocumentDecisionTableDAO auditDecisionTableDAO;

    @Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		calculator = new DocumentPercentCalculator();

		Whitebox.setInternalState(calculator, "auditCategoryRuleCache", catRuleCache);
		Whitebox.setInternalState(calculator, "logger", logger);
        Whitebox.setInternalState(catRuleCache, "auditDecisionTableDAO", auditDecisionTableDAO);
	    Whitebox.setInternalState(calculator, "auditDataDAO", auditDataDAO);

		catRules.clear();
		catRuleCache.clear();

		catRules.add(new DocumentCategoryRule());

        Whitebox.invokeMethod(catRuleCache, "initialize", catRules);

		// make standard entities
		contractor = EntityFactory.makeContractor();
		auditType = EntityFactory.makeAuditType(AuditType.PQF);
		Workflow workflow = new Workflow();
		workflow.setHasRequirements(false);
		auditType.setWorkFlow(workflow);
		audit = EntityFactory.makeContractorAudit(auditType, contractor);
	}

    @Test
    public void testShouldAdjustAuditScore_NonVisibleCao() throws Exception {
        ContractorDocumentOperator cao = mock(ContractorDocumentOperator.class);
        ContractorDocument audit = mock(ContractorDocument.class);

        when(cao.isVisible()).thenReturn(false);

        Boolean result = Whitebox.invokeMethod(calculator, "shouldAdjustAuditScore", audit, cao);
        assertFalse(result);
    }

    @Test
    public void testShouldAdjustAuditScore_NonScoring() throws Exception {
        ContractorDocumentOperator cao = mock(ContractorDocumentOperator.class);
        ContractorDocument audit = mock(ContractorDocument.class);
        AuditType auditType = mock(AuditType.class);

        when(cao.isVisible()).thenReturn(true);
        when(audit.getAuditType()).thenReturn(auditType);
        when(auditType.getScoreType()).thenReturn(null);

        Boolean result = Whitebox.invokeMethod(calculator, "shouldAdjustAuditScore", audit, cao);
        assertFalse(result);
    }

    @Test
    public void testShouldAdjustAuditScore_Scoring() throws Exception {
        ContractorDocumentOperator cao = mock(ContractorDocumentOperator.class);
        ContractorDocument audit = mock(ContractorDocument.class);
        AuditType auditType = mock(AuditType.class);

        when(cao.isVisible()).thenReturn(true);
        when(audit.getAuditType()).thenReturn(auditType);
        when(auditType.getScoreType()).thenReturn(ScoreType.Percent);

        Boolean result = Whitebox.invokeMethod(calculator, "shouldAdjustAuditScore", audit, cao);
        assertTrue(result);
    }

    @Test
    public void testIsVisibleToRecalculate_NoAnswerYet() throws Exception {
        DocumentData data = null;
        List<DocumentData> responses = new ArrayList<>();
        AnswerMap map = new AnswerMap(responses);

        Boolean visible = Whitebox.invokeMethod(calculator, "isVisibleToRecalculate", data, map);
        assertTrue(visible);
    }

    @Test
    public void testIsVisibleToRecalculate_AnsweredNoVisibleRequirements() throws Exception {
        DocumentQuestion question = EntityFactory.makeAuditQuestion();
        DocumentData data = EntityFactory.makeAuditData("Yes", question);
        List<DocumentData> responses = new ArrayList<>();
        AnswerMap map = new AnswerMap(responses);

        Boolean visible = Whitebox.invokeMethod(calculator, "isVisibleToRecalculate", data, map);
        assertTrue(visible);
    }

    @Test
    public void testIsVisibleToRecalculate_AnsweredVisibleRequirements_Yes() throws Exception {
        DocumentQuestion question = EntityFactory.makeAuditQuestion();
        DocumentQuestion visQuestion = EntityFactory.makeAuditQuestion();
        question.setVisibleQuestion(visQuestion);
        question.setVisibleAnswer("Yes");
        DocumentData data = EntityFactory.makeAuditData("Yes", question);
        DocumentData visData = EntityFactory.makeAuditData("Yes", visQuestion);

        List<DocumentData> responses = new ArrayList<>();
        responses.add(visData);
        AnswerMap map = new AnswerMap(responses);


        Boolean visible = Whitebox.invokeMethod(calculator, "isVisibleToRecalculate", data, map);
        assertTrue(visible);
    }

    @Test
    public void testIsVisibleToRecalculate_AnsweredVisibleRequirements_No() throws Exception {
        DocumentQuestion question = EntityFactory.makeAuditQuestion();
        DocumentQuestion visQuestion = EntityFactory.makeAuditQuestion();
        question.setVisibleQuestion(visQuestion);
        question.setVisibleAnswer("Yes");
        DocumentData data = EntityFactory.makeAuditData("Yes", question);
        DocumentData visData = EntityFactory.makeAuditData("No", visQuestion);

        List<DocumentData> responses = new ArrayList<>();
        responses.add(visData);
        AnswerMap map = new AnswerMap(responses);


        Boolean visible = Whitebox.invokeMethod(calculator, "isVisibleToRecalculate", data, map);
        assertFalse(visible);
    }

    @Test
	public void testChainedFunctions() throws Exception {
		ContractorDocumentOperator cao = EntityFactory.addCao(audit, EntityFactory.makeOperator());
		cao.setStatus(DocumentStatus.Pending);
		audit.getOperators().add(cao);

		DocumentQuestion q1 = EntityFactory.makeAuditQuestion();
		DocumentQuestion q2 = EntityFactory.makeAuditQuestion();
		DocumentQuestion q3 = EntityFactory.makeAuditQuestion();
		q1.setId(1);
		q2.setId(2);
		q3.setId(3);
        q1.setEffectiveDate(DateBean.addMonths(new Date(), -1));
        q2.setEffectiveDate(DateBean.addMonths(new Date(), -1));
        q3.setEffectiveDate(DateBean.addMonths(new Date(), -1));
        q1.setExpirationDate(DateBean.addMonths(new Date(), 1));
        q2.setExpirationDate(DateBean.addMonths(new Date(), 1));
        q3.setExpirationDate(DateBean.addMonths(new Date(), 1));

		DocumentCategory ac1 = EntityFactory.makeAuditCategory(1);
		DocumentCategory ac2 = EntityFactory.makeAuditCategory(2);
		ac1.getQuestions().add(q1);
		ac1.getQuestions().add(q2);
		ac2.getQuestions().add(q3);
		audit.getAuditType().getCategories().add(ac1);
		audit.getAuditType().getCategories().add(ac2);
		q1.setCategory(ac1);
		q2.setCategory(ac1);
		q3.setCategory(ac2);

		DocumentCatData acd1 = EntityFactory.makeAuditCatData();
		DocumentCatData acd2 = EntityFactory.makeAuditCatData();
		acd1.setId(1);
		acd2.setId(2);
		acd1.setCategory(ac1);
		acd2.setCategory(ac2);
		audit.getCategories().add(acd1);
		audit.getCategories().add(acd2);
		acd1.setAudit(audit);
		acd2.setAudit(audit);

		DocumentQuestionFunction aqf2 = new DocumentQuestionFunction();
		DocumentQuestionFunction aqf3 = new DocumentQuestionFunction();
		aqf2.setType(QuestionFunctionType.Calculation);
		aqf3.setType(QuestionFunctionType.Calculation);
		aqf2.setFunction(QuestionFunction.DOUBLE);
		aqf3.setFunction(QuestionFunction.DOUBLE);
		aqf2.setOverwrite(true);
		aqf3.setOverwrite(true);
		aqf2.setQuestion(q2);
		aqf3.setQuestion(q3);
		q2.getFunctions().add(aqf2);
		q3.getFunctions().add(aqf3);

		DocumentQuestionFunctionWatcher aqfw1 = new DocumentQuestionFunctionWatcher();
		DocumentQuestionFunctionWatcher aqfw2 = new DocumentQuestionFunctionWatcher();
		aqfw1.setFunction(aqf2);
		aqfw2.setFunction(aqf3);
		aqf2.getWatchers().add(aqfw1);
		aqf3.getWatchers().add(aqfw2);
		aqfw1.setQuestion(q1);
		aqfw2.setQuestion(q2);
		q1.getFunctionWatchers().add(aqfw1);
		q2.getFunctionWatchers().add(aqfw2);

		DocumentData ad1 = EntityFactory.makeAuditData("1");
		DocumentData ad2 = EntityFactory.makeAuditData("0");
		DocumentData ad3 = EntityFactory.makeAuditData("0");
		ad1.setQuestion(q1);
		ad2.setQuestion(q2);
		ad3.setQuestion(q3);
		List<DocumentData> answers = new ArrayList<DocumentData>();
		answers.add(ad1);
		answers.add(ad2);
		answers.add(ad3);
		AnswerMap map = new AnswerMap(answers);

		when(auditDataDAO.findAnswersByAuditAndQuestions(any(ContractorDocument.class), anyCollectionOf(Integer.class))).thenReturn(map);
		when(auditDataDAO.findAnswerByAuditQuestion(audit.getId(), q1.getId())).thenReturn(ad1);
		when(auditDataDAO.findAnswerByAuditQuestion(audit.getId(), q2.getId())).thenReturn(ad2);
		when(auditDataDAO.findAnswerByAuditQuestion(audit.getId(), q3.getId())).thenReturn(ad3);

		calculator.updatePercentageCompleted(acd1);
		assertTrue(ad1.getAnswer().equals("1"));
		assertTrue(ad2.getAnswer().equals("2"));
		assertTrue(ad3.getAnswer().equals("4"));
	}

	@Test
	public void testCollectFunctionWatcherQuestionIdsFromAuditCatData_AllPopulatedAllValidPutsFunctionWatcherQuestionIdInReturn() throws Exception {
		List<DocumentQuestion> documentQuestions = new ArrayList<>();
		List<DocumentQuestionFunction> functions = new ArrayList<>();
		List<DocumentQuestionFunctionWatcher> watchers = new ArrayList<>();

        functionWatcherQuestionIdsBasicStubbing(documentQuestions);
		fullFunctionWatcherQuestionStubbing(documentQuestions, functions, watchers, true);
		
		Collection<Integer> ids = Whitebox.invokeMethod(calculator, "collectFunctionWatcherQuestionIdsFromAuditCatData", catData);
		
		assertTrue(ids.contains(2));
	}
	
	@Test
	public void testCollectFunctionWatcherQuestionIdsFromAuditCatData_FunctionWatcherQuestionNotValidForDateReturnsEmptyCollection() throws Exception {
		List<DocumentQuestion> documentQuestions = new ArrayList<>();
		List<DocumentQuestionFunction> functions = new ArrayList<>();
		List<DocumentQuestionFunctionWatcher> watchers = new ArrayList<>();

        functionWatcherQuestionIdsBasicStubbing(documentQuestions);
		fullFunctionWatcherQuestionStubbing(documentQuestions, functions, watchers, false);
		
		Collection<Integer> ids = Whitebox.invokeMethod(calculator, "collectFunctionWatcherQuestionIdsFromAuditCatData", catData);
		
		assertTrue(ids.isEmpty());
	}

	private void fullFunctionWatcherQuestionStubbing(List<DocumentQuestion> documentQuestions,
			List<DocumentQuestionFunction> functions, List<DocumentQuestionFunctionWatcher> watchers,
			boolean isDateValid) {
		DocumentQuestion documentQuestion = mock(DocumentQuestion.class);
		when(documentQuestion.getId()).thenReturn(1);
        when(documentQuestion.getEffectiveDate()).thenReturn(DateBean.addMonths(new Date(),-1));
        when(documentQuestion.getExpirationDate()).thenReturn(DateBean.addMonths(new Date(),1));
		documentQuestions.add(documentQuestion);

		DocumentQuestionFunction documentQuestionFunction = new DocumentQuestionFunction();
		
		functions.add(documentQuestionFunction);
		
		when(documentQuestion.getFunctions()).thenReturn(functions);
		
		DocumentQuestion watcherDocumentQuestion = mock(DocumentQuestion.class);
		when(watcherDocumentQuestion.getId()).thenReturn(2);
        when(watcherDocumentQuestion.getEffectiveDate()).thenReturn(DateBean.addMonths(new Date(),-1));
        if (isDateValid) {
            when(watcherDocumentQuestion.getExpirationDate()).thenReturn(DateBean.addMonths(new Date(),1));
        }
        else {
            when(watcherDocumentQuestion.getExpirationDate()).thenReturn(DateBean.addMonths(new Date(),-1));
        }

		DocumentQuestionFunctionWatcher aqfw = new DocumentQuestionFunctionWatcher();
		aqfw.setQuestion(watcherDocumentQuestion);
		watchers.add(aqfw);
		documentQuestionFunction.setWatchers(watchers);
	}
	
	@Test
	public void testCollectFunctionWatcherQuestionIdsFromAuditCatData_NoWatchersReturnsEmptyCollection() throws Exception {
		List<DocumentQuestion> documentQuestions = new ArrayList<>();
		List<DocumentQuestionFunction> functions = new ArrayList<>();
		
		DocumentQuestion documentQuestion = mock(DocumentQuestion.class);
		when(documentQuestion.getId()).thenReturn(1);
        when(documentQuestion.getEffectiveDate()).thenReturn(DateBean.addMonths(new Date(),-1));
        when(documentQuestion.getExpirationDate()).thenReturn(DateBean.addMonths(new Date(),1));
        documentQuestions.add(documentQuestion);

		DocumentQuestionFunction documentQuestionFunction = new DocumentQuestionFunction();
		functions.add(documentQuestionFunction);

        functionWatcherQuestionIdsBasicStubbing(documentQuestions);
		functionWatcherQuestionIdsBasicStubbing(documentQuestions);
		when(documentQuestion.getFunctions()).thenReturn(functions);
		
		Collection<Integer> ids = Whitebox.invokeMethod(calculator, "collectFunctionWatcherQuestionIdsFromAuditCatData", catData);
		
		assertTrue(ids.isEmpty());
	}
	
	@Test
	public void testCollectFunctionWatcherQuestionIdsFromAuditCatData_NoFunctionsReturnsEmptyCollection() throws Exception {
		List<DocumentQuestion> documentQuestions = new ArrayList<>();
		List<DocumentQuestionFunction> functions = new ArrayList<>();
		
		DocumentQuestion documentQuestion = mock(DocumentQuestion.class);
		when(documentQuestion.getId()).thenReturn(1);
		when(documentQuestion.getEffectiveDate()).thenReturn(DateBean.addMonths(new Date(),-1));
		when(documentQuestion.getExpirationDate()).thenReturn(DateBean.addMonths(new Date(),1));
		documentQuestions.add(documentQuestion);

        functionWatcherQuestionIdsBasicStubbing(documentQuestions);
		Date validDate = functionWatcherQuestionIdsBasicStubbing(documentQuestions);
		when(documentQuestion.getFunctions()).thenReturn(functions);
		
		Collection<Integer> ids = Whitebox.invokeMethod(calculator, "collectFunctionWatcherQuestionIdsFromAuditCatData", catData);
		
		assertTrue(ids.isEmpty());
	}
	
	@Test
	public void testCollectFunctionWatcherQuestionIdsFromAuditCatData_NotValidQuestionReturnsEmptyCollection() throws Exception {
		List<DocumentQuestion> documentQuestions = new ArrayList<>();
		List<DocumentQuestionFunction> functions = new ArrayList<>();
		
		DocumentQuestion documentQuestion = mock(DocumentQuestion.class);
		when(documentQuestion.getId()).thenReturn(1);
        when(documentQuestion.getEffectiveDate()).thenReturn(DateBean.addMonths(new Date(),-1));
        when(documentQuestion.getExpirationDate()).thenReturn(DateBean.addMonths(new Date(),1));
        documentQuestions.add(documentQuestion);

		DocumentQuestionFunction documentQuestionFunction = new DocumentQuestionFunction();
		functions.add(documentQuestionFunction);

        functionWatcherQuestionIdsBasicStubbing(documentQuestions);
		Date validDate = functionWatcherQuestionIdsBasicStubbing(documentQuestions);

		Collection<Integer> ids = Whitebox.invokeMethod(calculator, "collectFunctionWatcherQuestionIdsFromAuditCatData", catData);
		
		assertTrue(ids.isEmpty());
	}

	private Date functionWatcherQuestionIdsBasicStubbing(List<DocumentQuestion> documentQuestions) {
		Date validDate = new Date();
		when(category.getQuestions()).thenReturn(documentQuestions);
		when(catData.getCategory()).thenReturn(category);
		when(catData.getAudit()).thenReturn(contractorDocument);
        when(contractorDocument.getAuditType()).thenReturn(auditType);
		return validDate;
	}
	
	@Test
	public void testCollectFunctionWatcherQuestionIdsFromAuditCatData_NoQuestionsReturnsEmptyCollection() throws Exception {
		List<DocumentQuestion> documentQuestions = new ArrayList<DocumentQuestion>();

        functionWatcherQuestionIdsBasicStubbing(documentQuestions);
		functionWatcherQuestionIdsBasicStubbing(documentQuestions);

		Collection<Integer> ids = Whitebox.invokeMethod(calculator, "collectFunctionWatcherQuestionIdsFromAuditCatData", catData);

		assertTrue(ids.isEmpty());
	}
	
	@Test
	public void testCollectQuestionIdsFromAuditCatData_NoRequiredNoVisible() throws Exception {
		List<DocumentQuestion> documentQuestions = mockAuditQuestions(1, 5);
		when(category.getQuestions()).thenReturn(documentQuestions);
		when(catData.getCategory()).thenReturn(category);
		
		Set<Integer> questionIDs = Whitebox.invokeMethod(calculator, "collectQuestionIdsFromAuditCatData", catData);
		
		for (int count = 1; count < questionIDs.size(); count++) {
			assertTrue(questionIDs.contains(count));
		}
		verify(logger, never()).warn(startsWith("Circular required questions detected"), anyInt());
		verify(logger, never()).warn(startsWith("Circular visible questions detected"), anyInt());
	}

	@Test
	public void testCollectQuestionIdsFromAuditCatData_WithRequiredNotCircularNoVisible() throws Exception {
		List<DocumentQuestion> documentQuestions = mockAuditQuestions(1, 5);
		List<DocumentQuestion> requiredQuestions = mockAuditQuestions(6, 5);
		Iterator<DocumentQuestion> requiredQuestionsIterator = requiredQuestions.iterator();
		for (DocumentQuestion question : documentQuestions) {
			DocumentQuestion requiredQuestion = requiredQuestionsIterator.next();
			when(question.getRequiredQuestion()).thenReturn(requiredQuestion);
		}
		when(category.getQuestions()).thenReturn(documentQuestions);
		when(catData.getCategory()).thenReturn(category);
		
		Set<Integer> questionIDs = Whitebox.invokeMethod(calculator, "collectQuestionIdsFromAuditCatData", catData);
		
		for (int count = 1; count <= 10; count++) {
			assertTrue(questionIDs.contains(count));
		}
		verify(logger, never()).warn(startsWith("Circular required questions detected"), anyInt());
	}

	@Test
	public void testCollectQuestionIdsFromAuditCatData_NoRequiredWithVisibleNotCircular() throws Exception {
		List<DocumentQuestion> documentQuestions = mockAuditQuestions(1, 5);
		List<DocumentQuestion> visibleQuestions = mockAuditQuestions(6, 5);
		Iterator<DocumentQuestion> visibleQuestionsIterator = visibleQuestions.iterator();
		for (DocumentQuestion question : documentQuestions) {
			DocumentQuestion visibleQuestion = visibleQuestionsIterator.next();
			when(question.getVisibleQuestion()).thenReturn(visibleQuestion);
		}
		when(category.getQuestions()).thenReturn(documentQuestions);
		when(catData.getCategory()).thenReturn(category);
		
		Set<Integer> questionIDs = Whitebox.invokeMethod(calculator, "collectQuestionIdsFromAuditCatData", catData);
		
		for (int count = 1; count <= 10; count++) {
			assertTrue(questionIDs.contains(count));
		}
	}
	
	@Test
	public void testCollectQuestionIdsFromAuditCatData_WithRequiredWithCircularNoVisible() throws Exception {
		List<DocumentQuestion> documentQuestions = new ArrayList<DocumentQuestion>();
		DocumentQuestion documentQuestion = mock(DocumentQuestion.class);
		when(documentQuestion.getId()).thenReturn(1);
        when(documentQuestion.getEffectiveDate()).thenReturn(DateBean.addMonths(new Date(),-1));
        when(documentQuestion.getExpirationDate()).thenReturn(DateBean.addMonths(new Date(),1));
        documentQuestions.add(documentQuestion);
		
		DocumentQuestion requiredQuestion = mock(DocumentQuestion.class);
		when(requiredQuestion.getId()).thenReturn(2);
		when(documentQuestion.getRequiredQuestion()).thenReturn(requiredQuestion);
		
		DocumentQuestion requiredQuestion2 = mock(DocumentQuestion.class);
		when(requiredQuestion2.getId()).thenReturn(3);
		when(requiredQuestion.getRequiredQuestion()).thenReturn(requiredQuestion2);
		
		DocumentQuestion circularRequiredQuestion = mock(DocumentQuestion.class);
		when(circularRequiredQuestion.getId()).thenReturn(2);
		when(requiredQuestion2.getRequiredQuestion()).thenReturn(circularRequiredQuestion);
		
		when(category.getQuestions()).thenReturn(documentQuestions);
		when(catData.getCategory()).thenReturn(category);
		
		Set<Integer> questionIDs = Whitebox.invokeMethod(calculator, "collectQuestionIdsFromAuditCatData", catData);
		
		for (int count = 1; count <= 3; count++) {
			assertTrue(questionIDs.contains(count));
		}
		verify(logger).warn(startsWith("Circular required questions detected"), anyInt());
	}
	
	@Test
	public void testCollectQuestionIdsFromAuditCatData_WithVisibleWithCircularNoRequired() throws Exception {
		List<DocumentQuestion> documentQuestions = new ArrayList<DocumentQuestion>();
		DocumentQuestion documentQuestion = mock(DocumentQuestion.class);
		when(documentQuestion.getId()).thenReturn(1);
        when(documentQuestion.getEffectiveDate()).thenReturn(DateBean.addMonths(new Date(),-1));
        when(documentQuestion.getExpirationDate()).thenReturn(DateBean.addMonths(new Date(),1));
        documentQuestions.add(documentQuestion);
		
		DocumentQuestion visibleQuestion = mock(DocumentQuestion.class);
		when(visibleQuestion.getId()).thenReturn(2);
		when(documentQuestion.getVisibleQuestion()).thenReturn(visibleQuestion);
		
		DocumentQuestion visibleQuestion2 = mock(DocumentQuestion.class);
		when(visibleQuestion2.getId()).thenReturn(3);
		when(visibleQuestion.getVisibleQuestion()).thenReturn(visibleQuestion2);
		
		DocumentQuestion circularVisibleQuestion = mock(DocumentQuestion.class);
		when(circularVisibleQuestion.getId()).thenReturn(2);
		when(visibleQuestion2.getVisibleQuestion()).thenReturn(circularVisibleQuestion);
		
		when(category.getQuestions()).thenReturn(documentQuestions);
		when(catData.getCategory()).thenReturn(category);
		
		Set<Integer> questionIDs = Whitebox.invokeMethod(calculator, "collectQuestionIdsFromAuditCatData", catData);
		
		for (int count = 1; count <= 3; count++) {
			assertTrue(questionIDs.contains(count));
		}
		verify(logger, never()).warn(startsWith("Circular required questions detected"), anyInt());
		verify(logger).warn(startsWith("Circular visible questions detected"), anyInt());
	}
	
	private List<DocumentQuestion> mockAuditQuestions(int startingId, int numberToCreate) {
		List<DocumentQuestion> questions = new ArrayList<DocumentQuestion>();
		for (int i = startingId; i < (startingId+numberToCreate); i++) {
			DocumentQuestion documentQuestion = mock(DocumentQuestion.class);
			when(documentQuestion.getId()).thenReturn(i);
            when(documentQuestion.getEffectiveDate()).thenReturn(DateBean.addMonths(new Date(),-1));
            when(documentQuestion.getExpirationDate()).thenReturn(DateBean.addMonths(new Date(),1));
            questions.add(documentQuestion);
		}
		return questions;
	}
	
	@Test
	public void testPercentCalculateComplete_WeightedScore_All_Answer_100()
			throws Exception {
		auditType.setScoreType(ScoreType.Weighted);

		acd1 = createWeightedAuditCatData(10f, 10f, 50f);
		acd2 = createWeightedAuditCatData(0f, 0f, 50f);
		acd3 = createWeightedAuditCatData(10f, 10f, 1f);
		acd4 = createWeightedAuditCatData(10f, 10f, 1f);
		createRelationships();

		calculator.percentCalculateComplete(audit, false);
		assertEquals(100, audit.getScore());
	}

	@Test
	public void testPercentCalculateComplete_WeightedScore_All_NA_100()
			throws Exception {
		auditType.setScoreType(ScoreType.Weighted);

		acd1 = createWeightedAuditCatData(0f, 0f, 50f);
		acd2 = createWeightedAuditCatData(0f, 0f, 50f);
		acd3 = createWeightedAuditCatData(0f, 0f, 1f);
		acd4 = createWeightedAuditCatData(0f, 0f, 1f);

		createRelationships();
		calculator.percentCalculateComplete(audit, false);
		assertEquals(100, audit.getScore());
	}

	@Test
	public void testPercentCalculateComplete_WeightedScore_First_100_Second_NA_100()
			throws Exception {
		auditType.setScoreType(ScoreType.Weighted);

		acd1 = createWeightedAuditCatData(10f, 10f, 50f);
		acd2 = createWeightedAuditCatData(0f, 0f, 50f);
		acd3 = createWeightedAuditCatData(0f, 0f, 1f);
		acd4 = createWeightedAuditCatData(0f, 0f, 1f);

		createRelationships();
		calculator.percentCalculateComplete(audit, false);
		assertEquals(100, audit.getScore());
	}

	@Test
	public void testPercentCalculateComplete_WeightedScore_First_NA_Second_100_100()
			throws Exception {
		auditType.setScoreType(ScoreType.Weighted);

		acd1 = createWeightedAuditCatData(0f, 0f, 50f);
		acd2 = createWeightedAuditCatData(0f, 0f, 50f);
		acd3 = createWeightedAuditCatData(10f, 10f, 1f);
		acd4 = createWeightedAuditCatData(10f, 10f, 1f);

		createRelationships();
		calculator.percentCalculateComplete(audit, false);
		assertEquals(100, audit.getScore());
	}

	@Test
	public void testPercentCalculateComplete_WeightedScore_First_100_Second_Mix_100()
			throws Exception {
		auditType.setScoreType(ScoreType.Weighted);

		acd1 = createWeightedAuditCatData(10f, 10f, 50f);
		acd2 = createWeightedAuditCatData(0f, 0f, 50f);
		acd3 = createWeightedAuditCatData(10f, 10f, 1f);
		acd4 = createWeightedAuditCatData(0f, 0f, 1f);

		createRelationships();
		calculator.percentCalculateComplete(audit, false);
		assertEquals(100, audit.getScore());
	}

	@Test
	public void testPercentCalculateComplete_WeightedScore_First_75()
			throws Exception {
		auditType.setScoreType(ScoreType.Weighted);

		acd1 = createWeightedAuditCatData(5f, 10f, 50f);
		acd2 = createWeightedAuditCatData(0f, 0f, 50f);
		acd3 = createWeightedAuditCatData(10f, 10f, 1f);
		acd4 = createWeightedAuditCatData(10f, 10f, 1f);

		createRelationships();
		calculator.percentCalculateComplete(audit, false);
		assertEquals(75, audit.getScore());
	}

	@Test
	public void testPercentCalculateComplete_WeightedScore_Second_75()
			throws Exception {
		auditType.setScoreType(ScoreType.Weighted);

		acd1 = createWeightedAuditCatData(10f, 10f, 50f);
		acd2 = createWeightedAuditCatData(0f, 0f, 50f);
		acd3 = createWeightedAuditCatData(5f, 10f, 1f);
		acd4 = createWeightedAuditCatData(5f, 10f, 1f);

		createRelationships();
		calculator.percentCalculateComplete(audit, false);
		assertEquals(75, audit.getScore());
	}

	private void createRelationships() {
		acd3.getCategory().setParent(acd2.getCategory());
		acd4.getCategory().setParent(acd2.getCategory());

		if (acd2.getCategory().getSubCategories() == null)
			acd2.getCategory().setSubCategories(new ArrayList<DocumentCategory>());
		acd2.getCategory().getSubCategories().clear();
		acd2.getCategory().getSubCategories().add(acd3.getCategory());
		acd2.getCategory().getSubCategories().add(acd4.getCategory());

		documentCatDataList.clear();
		documentCatDataList.add(acd1);
		documentCatDataList.add(acd2);
		documentCatDataList.add(acd3);
		documentCatDataList.add(acd4);
		audit.setCategories(documentCatDataList);

		documentCategoryList.clear();
		documentCategoryList.add(acd1.getCategory());
		documentCategoryList.add(acd2.getCategory());
		documentCategoryList.add(acd3.getCategory());
		documentCategoryList.add(acd4.getCategory());
		auditType.setCategories(documentCategoryList);
	}

	private DocumentCatData createWeightedAuditCatData(float score,
			float scorePossible, float scoreWeight) {
		DocumentCatData acd = EntityFactory.makeAuditCatData();
		acd.setScore(score);
		acd.setScorePossible(scorePossible);
		acd.getCategory().setScoreWeight(scoreWeight);

		return acd;
	}

	@Test
	public void testUpdatePercentageCompleted_Verified_Count() {
		PicsTestUtil.forceSetPrivateField(calculator, "auditDataDAO",
				auditDataDAO);

		DocumentCatData catData = EntityFactory.makeAuditCatData();
		catData.setAudit(audit);

		DocumentQuestion q1 = EntityFactory.makeAuditQuestion();
		DocumentQuestion q2 = EntityFactory.makeAuditQuestion();
		q1.setCategory(catData.getCategory());
		q2.setCategory(catData.getCategory());

		q1.setRequired(true);
		q2.setRequired(true);

		q1.setRequiredAnswer("Yes");
		q2.setRequiredAnswer("Yes");

		catData.getCategory().getQuestions().add(q1);
		catData.getCategory().getQuestions().add(q2);

		DocumentData a1 = EntityFactory.makeAuditData("Yes");
		DocumentData a2 = EntityFactory.makeAuditData("Yes");
		a1.setQuestion(q1);
		a2.setQuestion(q2);

		List<DocumentData> answerList = new ArrayList<DocumentData>();
		answerList.add(a1);
		answerList.add(a2);

		audit.setData(answerList);
		
		List<DocumentData> pqfList = new ArrayList<DocumentData>();
		
		when(auditDataDAO.findCustomPQFVerifications(Matchers.anyInt())).thenReturn(pqfList);

		// no pqf questions need to be verified so verified count should be 2
		// each required question that does not need to be verified, increments verified count
		calculator.updatePercentageCompleted(catData);
		assertEquals(2, catData.getNumVerified());
		
		// 1 needs verification, none verified
		pqfList.add(a1);
		calculator.updatePercentageCompleted(catData);
		assertEquals(1, catData.getNumVerified());

		// 2 needs verification, none verified
		pqfList.add(a2);
		calculator.updatePercentageCompleted(catData);
		assertEquals(0, catData.getNumVerified());
		
		// both need verification, 1 verified
		a1.setDateVerified(new Date());
		calculator.updatePercentageCompleted(catData);
		assertEquals(1, catData.getNumVerified());
}

	@Test
	public void testUpdatePercentageCompleted_CircularRequiredQuestions() {
		DocumentCatData catData = setupCircularTest(true);

		calculator.updatePercentageCompleted(catData);
		verify(logger, times(2 * 3)).warn(Matchers.anyString(), Matchers.any()); // instances
																					// of
																					// call
																					// *
																					// #
																					// of
																					// questions
																					// in
																					// loop
																					// *
																					// 2
																					// for
																					// answers
	}

	@Test
	public void testUpdatePercentageCompleted_CircularVisibleQuestions() {
		DocumentCatData catData = setupCircularTest(false);

		calculator.updatePercentageCompleted(catData);
		verify(logger, times(2 * 3)).warn(Matchers.anyString(), Matchers.any()); // instances
																					// of
																					// call
																					// *
																					// #
																					// of
																					// questions
																					// in
																					// loop
	}

	private DocumentCatData setupCircularTest(boolean doRequiredQuestions) {
		PicsTestUtil.forceSetPrivateField(calculator, "auditDataDAO",
				auditDataDAO);

		DocumentCatData catData = EntityFactory.makeAuditCatData();
		catData.setAudit(audit);

		DocumentQuestion q1 = EntityFactory.makeAuditQuestion();
		DocumentQuestion q2 = EntityFactory.makeAuditQuestion();
		DocumentQuestion q3 = EntityFactory.makeAuditQuestion();
		q1.setCategory(catData.getCategory());
		q2.setCategory(catData.getCategory());
		q3.setCategory(catData.getCategory());

		if (doRequiredQuestions) {
			q1.setRequiredQuestion(q2);
			q2.setRequiredQuestion(q3);
			q3.setRequiredQuestion(q1);
		} else {
			q1.setVisibleQuestion(q2);
			q2.setVisibleQuestion(q3);
			q3.setVisibleQuestion(q1);
		}

		q1.setRequired(true);
		q2.setRequired(true);
		q3.setRequired(true);

		q1.setRequiredAnswer("Yes");
		q2.setRequiredAnswer("Yes");
		q3.setRequiredAnswer("Yes");

		catData.getCategory().getQuestions().add(q1);
		catData.getCategory().getQuestions().add(q2);
		catData.getCategory().getQuestions().add(q3);

		DocumentData a1 = EntityFactory.makeAuditData("Yes");
		DocumentData a2 = EntityFactory.makeAuditData("Yes");
		DocumentData a3 = EntityFactory.makeAuditData("Yes");
		a1.setQuestion(q1);
		a2.setQuestion(q2);
		a3.setQuestion(q3);

		a1.setDateVerified(new Date());
		a2.setDateVerified(new Date());
		a3.setDateVerified(new Date());

		List<DocumentData> answerList = new ArrayList<DocumentData>();
		answerList.add(a1);
		answerList.add(a2);
		answerList.add(a3);

		audit.setData(answerList);

		when(
				auditDataDAO.findAnswersByAuditAndQuestions(
						Matchers.any(ContractorDocument.class),
						Matchers.anyCollectionOf(Integer.class))).thenReturn(
				new AnswerMap(answerList));

		return catData;

	}

    @Test
    public void testPolicyNoRequiredQuestionsIsNotPushedTo100Percent() throws Exception {
        List<ContractorDocumentOperator> caos = new ArrayList<>();
        caos.add(contractorDocumentOperator);
        List<DocumentCatData> documentCatDatas = new ArrayList<>();
        documentCatDatas.add(catData);

        setupMocksForZeroQuestionsRequired();
        when(contractorDocument.getOperators()).thenReturn(caos);
        when(mockAuditType.getClassType()).thenReturn(AuditTypeClass.Policy);
        when(contractorDocument.getCategories()).thenReturn(documentCatDatas);
        when(catData.getCategory()).thenReturn(category);

        calculator.percentCalculateComplete(contractorDocument, false);

        verify(contractorDocumentOperator).setPercentComplete(0);
        verify(contractorDocumentOperator).setPercentVerified(0);
    }

    @Test
    public void testNonPolicyNoRequiredQuestionsIsNotPushedTo100Percent() throws Exception {
        List<ContractorDocumentOperator> caos = new ArrayList<>();
        caos.add(contractorDocumentOperator);
        List<DocumentCatData> documentCatDatas = new ArrayList<>();
        documentCatDatas.add(catData);

        setupMocksForZeroQuestionsRequired();
        when(contractorDocument.getOperators()).thenReturn(caos);
        when(mockAuditType.getClassType()).thenReturn(AuditTypeClass.Audit);
        when(contractorDocument.getCategories()).thenReturn(documentCatDatas);
        when(catData.getCategory()).thenReturn(category);

        calculator.percentCalculateComplete(contractorDocument, false);

        verify(contractorDocumentOperator).setPercentComplete(100);
        verify(contractorDocumentOperator).setPercentVerified(100);
    }

    private void setupMocksForZeroQuestionsRequired() {
        when(contractorDocument.getContractorAccount()).thenReturn(contractor);
        when(contractorDocument.getAuditType()).thenReturn(mockAuditType);
        when(contractorDocumentOperator.getStatus()).thenReturn(DocumentStatus.Pending);
        when(contractorDocumentOperator.getPercentComplete()).thenReturn(100);
        when(catData.isOverride()).thenReturn(true);
        when(catData.isApplies()).thenReturn(true);
        when(catData.getNumRequired()).thenReturn(0);
        when(catData.getRequiredCompleted()).thenReturn(0);
        when(catData.getNumVerified()).thenReturn(0);
        when(catData.getNumAnswered()).thenReturn(0);
    }

    @Test
    public void testPercentCalculateComplete_PolicySubmittedOrAfter()
            throws Exception {
        List<ContractorDocumentOperator> caos = new ArrayList<>();
        caos.add(contractorDocumentOperator);
        List<DocumentCatData> documentCatDatas = new ArrayList<>();
        documentCatDatas.add(catData);

        when(contractorDocument.getContractorAccount()).thenReturn(contractor);
        when(contractorDocument.getOperators()).thenReturn(caos);
        when(contractorDocument.getAuditType()).thenReturn(mockAuditType);
        when(mockAuditType.getClassType()).thenReturn(AuditTypeClass.Policy);
        when(contractorDocumentOperator.getStatus()).thenReturn(DocumentStatus.Submitted);
        when(contractorDocumentOperator.getPercentComplete()).thenReturn(100);
        when(contractorDocument.getCategories()).thenReturn(documentCatDatas);
        when(catData.getCategory()).thenReturn(category);
        when(catData.isOverride()).thenReturn(true);
        when(catData.isApplies()).thenReturn(true);
        when(catData.getNumRequired()).thenReturn(100);
        when(catData.getRequiredCompleted()).thenReturn(50);
        when(catData.getNumVerified()).thenReturn(50);
        when(catData.getNumAnswered()).thenReturn(50);
        when(catData.getCategory()).thenReturn(category);

        calculator.percentCalculateComplete(contractorDocument, false);

        verify(contractorDocumentOperator).setPercentComplete(100);
        verify(contractorDocumentOperator).setPercentVerified(50);
    }
}