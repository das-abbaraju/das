package com.picsauditing.auditBuilder;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.util.AnswerMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.slf4j.Logger;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.startsWith;
import static org.mockito.Mockito.*;

public class AuditPercentCalculatorTest {
	private AuditPercentCalculator calculator;
	private ContractorAccount contractor;
	private ContractorAudit audit;
	private AuditType auditType;

	AuditCategoryRuleCache catRuleCache = new AuditCategoryRuleCache();
	List<AuditCategoryRule> catRules = new ArrayList<AuditCategoryRule>();
	AuditCategoriesBuilder catBuilder;

	private AuditCatData acd1;
	private AuditCatData acd2;
	private AuditCatData acd3;
	private AuditCatData acd4;
	private List<AuditCatData> auditCatDataList = new ArrayList<AuditCatData>();
	private List<AuditCategory> auditCategoryList = new ArrayList<AuditCategory>();

	@Mock private Logger logger;
	@Mock private AuditDataDAO auditDataDAO;
	@Mock private AuditCatData catData;
	@Mock private AuditCategory category;
	@Mock private ContractorAudit contractorAudit;
    @Mock
    private AuditDecisionTableDAO auditDecisionTableDAO;

    @Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		calculator = new AuditPercentCalculator();

		Whitebox.setInternalState(calculator, "auditCategoryRuleCache", catRuleCache);
		Whitebox.setInternalState(calculator, "logger", logger);
        Whitebox.setInternalState(catRuleCache, "auditDecisionTableDAO", auditDecisionTableDAO);
	    Whitebox.setInternalState(calculator, "auditDataDAO", auditDataDAO);

		catRules.clear();
		catRuleCache.clear();

		catRules.add(new AuditCategoryRule());

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
	public void testChainedFunctions() throws Exception {
		ContractorAuditOperator cao = EntityFactory.addCao(audit, EntityFactory.makeOperator());
		cao.setStatus(AuditStatus.Pending);
		audit.getOperators().add(cao);

		AuditQuestion q1 = EntityFactory.makeAuditQuestion();
		AuditQuestion q2 = EntityFactory.makeAuditQuestion();
		AuditQuestion q3 = EntityFactory.makeAuditQuestion();
		q1.setId(1);
		q2.setId(2);
		q3.setId(3);
		q1.setUniqueCode("value");
		q2.setUniqueCode("value");
		q3.setUniqueCode("value");

		AuditCategory ac1 = EntityFactory.makeAuditCategory(1);
		AuditCategory ac2 = EntityFactory.makeAuditCategory(2);
		ac1.getQuestions().add(q1);
		ac1.getQuestions().add(q2);
		ac2.getQuestions().add(q3);
		audit.getAuditType().getCategories().add(ac1);
		audit.getAuditType().getCategories().add(ac2);
		q1.setCategory(ac1);
		q2.setCategory(ac1);
		q3.setCategory(ac2);

		AuditCatData acd1 = EntityFactory.makeAuditCatData();
		AuditCatData acd2 = EntityFactory.makeAuditCatData();
		acd1.setId(1);
		acd2.setId(2);
		acd1.setCategory(ac1);
		acd2.setCategory(ac2);
		audit.getCategories().add(acd1);
		audit.getCategories().add(acd2);
		acd1.setAudit(audit);
		acd2.setAudit(audit);

		AuditQuestionFunction aqf2 = new AuditQuestionFunction();
		AuditQuestionFunction aqf3 = new AuditQuestionFunction();
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

		AuditQuestionFunctionWatcher aqfw1 = new AuditQuestionFunctionWatcher();
		AuditQuestionFunctionWatcher aqfw2 = new AuditQuestionFunctionWatcher();
		aqfw1.setFunction(aqf2);
		aqfw2.setFunction(aqf3);
		aqf2.getWatchers().add(aqfw1);
		aqf3.getWatchers().add(aqfw2);
		aqfw1.setQuestion(q1);
		aqfw2.setQuestion(q2);
		q1.getFunctionWatchers().add(aqfw1);
		q2.getFunctionWatchers().add(aqfw2);

		AuditData ad1 = EntityFactory.makeAuditData("1");
		AuditData ad2 = EntityFactory.makeAuditData("0");
		AuditData ad3 = EntityFactory.makeAuditData("0");
		ad1.setQuestion(q1);
		ad2.setQuestion(q2);
		ad3.setQuestion(q3);
		List<AuditData> answers = new ArrayList<AuditData>();
		answers.add(ad1);
		answers.add(ad2);
		answers.add(ad3);
		AnswerMap map = new AnswerMap(answers);

		when(auditDataDAO.findAnswersByAuditAndQuestions(any(ContractorAudit.class), anyCollectionOf(Integer.class))).thenReturn(map);
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
		List<AuditQuestion> auditQuestions = new ArrayList<AuditQuestion>(); 
		List<AuditQuestionFunction> functions = new ArrayList<AuditQuestionFunction>();
		List<AuditQuestionFunctionWatcher> watchers = new ArrayList<AuditQuestionFunctionWatcher>();
		
		fullFunctionWatcherQuestionStubbing(auditQuestions, functions, watchers, true);
		
		Collection<Integer> ids = Whitebox.invokeMethod(calculator, "collectFunctionWatcherQuestionIdsFromAuditCatData", catData);
		
		assertTrue(ids.contains(2));
	}
	
	@Test
	public void testCollectFunctionWatcherQuestionIdsFromAuditCatData_FunctionWatcherQuestionNotValidForDateReturnsEmptyCollection() throws Exception {
		List<AuditQuestion> auditQuestions = new ArrayList<AuditQuestion>(); 
		List<AuditQuestionFunction> functions = new ArrayList<AuditQuestionFunction>();
		List<AuditQuestionFunctionWatcher> watchers = new ArrayList<AuditQuestionFunctionWatcher>();
		
		fullFunctionWatcherQuestionStubbing(auditQuestions, functions, watchers, false);
		
		Collection<Integer> ids = Whitebox.invokeMethod(calculator, "collectFunctionWatcherQuestionIdsFromAuditCatData", catData);
		
		assertTrue(ids.isEmpty());
	}

	private void fullFunctionWatcherQuestionStubbing(List<AuditQuestion> auditQuestions,
			List<AuditQuestionFunction> functions, List<AuditQuestionFunctionWatcher> watchers,
			boolean isDateValid) {
		AuditQuestion auditQuestion = mock(AuditQuestion.class);
		when(auditQuestion.getId()).thenReturn(1);
		auditQuestions.add(auditQuestion);

		AuditQuestionFunction auditQuestionFunction = new AuditQuestionFunction();
		
		functions.add(auditQuestionFunction);
		
		Date validDate = functionWatcherQuestionIdsBasicStubbing(auditQuestions);
		when(auditQuestion.isValidQuestion(validDate)).thenReturn(true);
		when(auditQuestion.getFunctions()).thenReturn(functions);
		
		AuditQuestion watcherAuditQuestion = mock(AuditQuestion.class);
		when(watcherAuditQuestion.getId()).thenReturn(2);
		when(watcherAuditQuestion.isValidQuestion(validDate)).thenReturn(isDateValid);
		
		AuditQuestionFunctionWatcher aqfw = new AuditQuestionFunctionWatcher();
		aqfw.setQuestion(watcherAuditQuestion);
		watchers.add(aqfw);
		auditQuestionFunction.setWatchers(watchers);
	}
	
	@Test
	public void testCollectFunctionWatcherQuestionIdsFromAuditCatData_NoWatchersReturnsEmptyCollection() throws Exception {
		List<AuditQuestion> auditQuestions = new ArrayList<AuditQuestion>(); 
		List<AuditQuestionFunction> functions = new ArrayList<AuditQuestionFunction>();
		
		AuditQuestion auditQuestion = mock(AuditQuestion.class);
		when(auditQuestion.getId()).thenReturn(1);
		auditQuestions.add(auditQuestion);

		AuditQuestionFunction auditQuestionFunction = new AuditQuestionFunction();
		functions.add(auditQuestionFunction);
		
		Date validDate = functionWatcherQuestionIdsBasicStubbing(auditQuestions);
		when(auditQuestion.isValidQuestion(validDate)).thenReturn(true);
		when(auditQuestion.getFunctions()).thenReturn(functions);
		
		Collection<Integer> ids = Whitebox.invokeMethod(calculator, "collectFunctionWatcherQuestionIdsFromAuditCatData", catData);
		
		assertTrue(ids.isEmpty());
	}
	
	@Test
	public void testCollectFunctionWatcherQuestionIdsFromAuditCatData_NoFunctionsReturnsEmptyCollection() throws Exception {
		List<AuditQuestion> auditQuestions = new ArrayList<AuditQuestion>(); 
		List<AuditQuestionFunction> functions = new ArrayList<AuditQuestionFunction>();
		
		AuditQuestion auditQuestion = mock(AuditQuestion.class);
		when(auditQuestion.getId()).thenReturn(1);
		auditQuestions.add(auditQuestion);

		Date validDate = functionWatcherQuestionIdsBasicStubbing(auditQuestions);
		when(auditQuestion.isValidQuestion(validDate)).thenReturn(true);
		when(auditQuestion.getFunctions()).thenReturn(functions);
		
		Collection<Integer> ids = Whitebox.invokeMethod(calculator, "collectFunctionWatcherQuestionIdsFromAuditCatData", catData);
		
		assertTrue(ids.isEmpty());
	}
	
	@Test
	public void testCollectFunctionWatcherQuestionIdsFromAuditCatData_NotValidQuestionReturnsEmptyCollection() throws Exception {
		List<AuditQuestion> auditQuestions = new ArrayList<AuditQuestion>(); 
		List<AuditQuestionFunction> functions = new ArrayList<AuditQuestionFunction>();
		
		AuditQuestion auditQuestion = mock(AuditQuestion.class);
		when(auditQuestion.getId()).thenReturn(1);
		auditQuestions.add(auditQuestion);

		AuditQuestionFunction auditQuestionFunction = new AuditQuestionFunction();
		functions.add(auditQuestionFunction);
		
		Date validDate = functionWatcherQuestionIdsBasicStubbing(auditQuestions);
		when(auditQuestion.isValidQuestion(validDate)).thenReturn(false);

		Collection<Integer> ids = Whitebox.invokeMethod(calculator, "collectFunctionWatcherQuestionIdsFromAuditCatData", catData);
		
		assertTrue(ids.isEmpty());
	}

	private Date functionWatcherQuestionIdsBasicStubbing(List<AuditQuestion> auditQuestions) {
		Date validDate = new Date();
		when(category.getQuestions()).thenReturn(auditQuestions);
		when(catData.getCategory()).thenReturn(category);
		when(catData.getAudit()).thenReturn(contractorAudit);
		when(contractorAudit.getValidDate()).thenReturn(validDate);
		return validDate;
	}
	
	@Test
	public void testCollectFunctionWatcherQuestionIdsFromAuditCatData_NoQuestionsReturnsEmptyCollection() throws Exception {
		List<AuditQuestion> auditQuestions = new ArrayList<AuditQuestion>(); 
		
		Date validDate = functionWatcherQuestionIdsBasicStubbing(auditQuestions);

		Collection<Integer> ids = Whitebox.invokeMethod(calculator, "collectFunctionWatcherQuestionIdsFromAuditCatData", catData);

		assertTrue(ids.isEmpty());
	}
	
	@Test
	public void testCollectQuestionIdsFromAuditCatData_NoRequiredNoVisible() throws Exception {
		List<AuditQuestion> auditQuestions = mockAuditQuestions(1, 5);
		when(category.getQuestions()).thenReturn(auditQuestions);
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
		List<AuditQuestion> auditQuestions = mockAuditQuestions(1, 5);
		List<AuditQuestion> requiredQuestions = mockAuditQuestions(6, 5);
		Iterator<AuditQuestion> requiredQuestionsIterator = requiredQuestions.iterator();
		for (AuditQuestion question : auditQuestions) {
			AuditQuestion requiredQuestion = requiredQuestionsIterator.next();
			when(question.getRequiredQuestion()).thenReturn(requiredQuestion);
		}
		when(category.getQuestions()).thenReturn(auditQuestions);
		when(catData.getCategory()).thenReturn(category);
		
		Set<Integer> questionIDs = Whitebox.invokeMethod(calculator, "collectQuestionIdsFromAuditCatData", catData);
		
		for (int count = 1; count <= 10; count++) {
			assertTrue(questionIDs.contains(count));
		}
		verify(logger, never()).warn(startsWith("Circular required questions detected"), anyInt());
	}

	@Test
	public void testCollectQuestionIdsFromAuditCatData_NoRequiredWithVisibleNotCircular() throws Exception {
		List<AuditQuestion> auditQuestions = mockAuditQuestions(1, 5);
		List<AuditQuestion> visibleQuestions = mockAuditQuestions(6, 5);
		Iterator<AuditQuestion> visibleQuestionsIterator = visibleQuestions.iterator();
		for (AuditQuestion question : auditQuestions) {
			AuditQuestion visibleQuestion = visibleQuestionsIterator.next();
			when(question.getVisibleQuestion()).thenReturn(visibleQuestion);
		}
		when(category.getQuestions()).thenReturn(auditQuestions);
		when(catData.getCategory()).thenReturn(category);
		
		Set<Integer> questionIDs = Whitebox.invokeMethod(calculator, "collectQuestionIdsFromAuditCatData", catData);
		
		for (int count = 1; count <= 10; count++) {
			assertTrue(questionIDs.contains(count));
		}
	}
	
	@Test
	public void testCollectQuestionIdsFromAuditCatData_WithRequiredWithCircularNoVisible() throws Exception {
		List<AuditQuestion> auditQuestions = new ArrayList<AuditQuestion>(); 
		AuditQuestion auditQuestion = mock(AuditQuestion.class);
		when(auditQuestion.getId()).thenReturn(1);
		auditQuestions.add(auditQuestion);
		
		AuditQuestion requiredQuestion = mock(AuditQuestion.class);
		when(requiredQuestion.getId()).thenReturn(2);
		when(auditQuestion.getRequiredQuestion()).thenReturn(requiredQuestion);
		
		AuditQuestion requiredQuestion2 = mock(AuditQuestion.class);
		when(requiredQuestion2.getId()).thenReturn(3);
		when(requiredQuestion.getRequiredQuestion()).thenReturn(requiredQuestion2);
		
		AuditQuestion circularRequiredQuestion = mock(AuditQuestion.class);
		when(circularRequiredQuestion.getId()).thenReturn(2);
		when(requiredQuestion2.getRequiredQuestion()).thenReturn(circularRequiredQuestion);
		
		when(category.getQuestions()).thenReturn(auditQuestions);
		when(catData.getCategory()).thenReturn(category);
		
		Set<Integer> questionIDs = Whitebox.invokeMethod(calculator, "collectQuestionIdsFromAuditCatData", catData);
		
		for (int count = 1; count <= 3; count++) {
			assertTrue(questionIDs.contains(count));
		}
		verify(logger).warn(startsWith("Circular required questions detected"), anyInt());
	}
	
	@Test
	public void testCollectQuestionIdsFromAuditCatData_WithVisibleWithCircularNoRequired() throws Exception {
		List<AuditQuestion> auditQuestions = new ArrayList<AuditQuestion>(); 
		AuditQuestion auditQuestion = mock(AuditQuestion.class);
		when(auditQuestion.getId()).thenReturn(1);
		auditQuestions.add(auditQuestion);
		
		AuditQuestion visibleQuestion = mock(AuditQuestion.class);
		when(visibleQuestion.getId()).thenReturn(2);
		when(auditQuestion.getVisibleQuestion()).thenReturn(visibleQuestion);
		
		AuditQuestion visibleQuestion2 = mock(AuditQuestion.class);
		when(visibleQuestion2.getId()).thenReturn(3);
		when(visibleQuestion.getVisibleQuestion()).thenReturn(visibleQuestion2);
		
		AuditQuestion circularVisibleQuestion = mock(AuditQuestion.class);
		when(circularVisibleQuestion.getId()).thenReturn(2);
		when(visibleQuestion2.getVisibleQuestion()).thenReturn(circularVisibleQuestion);
		
		when(category.getQuestions()).thenReturn(auditQuestions);
		when(catData.getCategory()).thenReturn(category);
		
		Set<Integer> questionIDs = Whitebox.invokeMethod(calculator, "collectQuestionIdsFromAuditCatData", catData);
		
		for (int count = 1; count <= 3; count++) {
			assertTrue(questionIDs.contains(count));
		}
		verify(logger, never()).warn(startsWith("Circular required questions detected"), anyInt());
		verify(logger).warn(startsWith("Circular visible questions detected"), anyInt());
	}
	
	private List<AuditQuestion> mockAuditQuestions(int startingId, int numberToCreate) {
		List<AuditQuestion> questions = new ArrayList<AuditQuestion>();
		for (int i = startingId; i < (startingId+numberToCreate); i++) {
			AuditQuestion auditQuestion = mock(AuditQuestion.class);
			when(auditQuestion.getId()).thenReturn(i);
			questions.add(auditQuestion);
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

		calculator.percentCalculateComplete(audit);
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
		calculator.percentCalculateComplete(audit);
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
		calculator.percentCalculateComplete(audit);
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
		calculator.percentCalculateComplete(audit);
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
		calculator.percentCalculateComplete(audit);
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
		calculator.percentCalculateComplete(audit);
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
		calculator.percentCalculateComplete(audit);
		assertEquals(75, audit.getScore());
	}

	private void createRelationships() {
		acd3.getCategory().setParent(acd2.getCategory());
		acd4.getCategory().setParent(acd2.getCategory());

		if (acd2.getCategory().getSubCategories() == null)
			acd2.getCategory().setSubCategories(new ArrayList<AuditCategory>());
		acd2.getCategory().getSubCategories().clear();
		acd2.getCategory().getSubCategories().add(acd3.getCategory());
		acd2.getCategory().getSubCategories().add(acd4.getCategory());

		auditCatDataList.clear();
		auditCatDataList.add(acd1);
		auditCatDataList.add(acd2);
		auditCatDataList.add(acd3);
		auditCatDataList.add(acd4);
		audit.setCategories(auditCatDataList);

		auditCategoryList.clear();
		auditCategoryList.add(acd1.getCategory());
		auditCategoryList.add(acd2.getCategory());
		auditCategoryList.add(acd3.getCategory());
		auditCategoryList.add(acd4.getCategory());
		auditType.setCategories(auditCategoryList);
	}

	private AuditCatData createWeightedAuditCatData(float score,
			float scorePossible, float scoreWeight) {
		AuditCatData acd = EntityFactory.makeAuditCatData();
		acd.setScore(score);
		acd.setScorePossible(scorePossible);
		acd.getCategory().setScoreWeight(scoreWeight);

		return acd;
	}

	@Test
	public void testUpdatePercentageCompleted_Verified_Count() {
		PicsTestUtil.forceSetPrivateField(calculator, "auditDataDAO",
				auditDataDAO);

		AuditCatData catData = EntityFactory.makeAuditCatData();
		catData.setAudit(audit);

		AuditQuestion q1 = EntityFactory.makeAuditQuestion();
		AuditQuestion q2 = EntityFactory.makeAuditQuestion();
		q1.setCategory(catData.getCategory());
		q2.setCategory(catData.getCategory());

		q1.setRequired(true);
		q2.setRequired(true);

		q1.setRequiredAnswer("Yes");
		q2.setRequiredAnswer("Yes");

		catData.getCategory().getQuestions().add(q1);
		catData.getCategory().getQuestions().add(q2);

		AuditData a1 = EntityFactory.makeAuditData("Yes");
		AuditData a2 = EntityFactory.makeAuditData("Yes");
		a1.setQuestion(q1);
		a2.setQuestion(q2);

		List<AuditData> answerList = new ArrayList<AuditData>();
		answerList.add(a1);
		answerList.add(a2);

		audit.setData(answerList);
		
		List<AuditData> pqfList = new ArrayList<AuditData>();
		
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
		AuditCatData catData = setupCircularTest(true);

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
		AuditCatData catData = setupCircularTest(false);

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

	private AuditCatData setupCircularTest(boolean doRequiredQuestions) {
		PicsTestUtil.forceSetPrivateField(calculator, "auditDataDAO",
				auditDataDAO);

		AuditCatData catData = EntityFactory.makeAuditCatData();
		catData.setAudit(audit);

		AuditQuestion q1 = EntityFactory.makeAuditQuestion();
		AuditQuestion q2 = EntityFactory.makeAuditQuestion();
		AuditQuestion q3 = EntityFactory.makeAuditQuestion();
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

		AuditData a1 = EntityFactory.makeAuditData("Yes");
		AuditData a2 = EntityFactory.makeAuditData("Yes");
		AuditData a3 = EntityFactory.makeAuditData("Yes");
		a1.setQuestion(q1);
		a2.setQuestion(q2);
		a3.setQuestion(q3);

		a1.setDateVerified(new Date());
		a2.setDateVerified(new Date());
		a3.setDateVerified(new Date());

		List<AuditData> answerList = new ArrayList<AuditData>();
		answerList.add(a1);
		answerList.add(a2);
		answerList.add(a3);

		audit.setData(answerList);

		when(
				auditDataDAO.findAnswersByAuditAndQuestions(
						Matchers.any(ContractorAudit.class),
						Matchers.anyCollectionOf(Integer.class))).thenReturn(
				new AnswerMap(answerList));

		return catData;

	}

	@Test
	public void testUpdatePercentageCompleted() {
	}
}
