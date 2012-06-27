package com.picsauditing.auditBuilder;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ScoreType;
import com.picsauditing.jpa.entities.Workflow;
import com.picsauditing.util.AnswerMap;

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

	@Mock
	private Logger logger;
	@Mock
	private AuditDataDAO auditDataDAO;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		calculator = new AuditPercentCalculator();

		PicsTestUtil.forceSetPrivateField(calculator, "auditCategoryRuleCache",
				catRuleCache);

		catRules.clear();
		catRuleCache.clear();

		catRules.add(new AuditCategoryRule());
		catRuleCache.initialize(catRules);

		// make standard entities
		contractor = EntityFactory.makeContractor();
		auditType = EntityFactory.makeAuditType(AuditType.PQF);
		Workflow workflow = new Workflow();
		workflow.setHasRequirements(false);
		auditType.setWorkFlow(workflow);
		audit = EntityFactory.makeContractorAudit(auditType, contractor);
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
		PicsTestUtil.forceSetPrivateField(calculator, "logger", logger);
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
