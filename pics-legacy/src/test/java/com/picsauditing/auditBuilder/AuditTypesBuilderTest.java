package com.picsauditing.auditBuilder;

import com.picsauditing.EntityFactory;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.jpa.entities.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class AuditTypesBuilderTest {

	private AuditTypesBuilder auditTypesBuilder;
	List<AuditTypeRule> auditTypeRules = new ArrayList<>();

	@Mock
	AuditTypeRuleCache auditTypeRuleCache;
	@Mock
	ContractorAccount contractor;
	@Mock
	OperatorAccount operator;
	@Mock
	AuditDataDAO auditDataDAO;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		auditTypesBuilder = new AuditTypesBuilder(auditTypeRuleCache, contractor);

		Whitebox.setInternalState(auditTypesBuilder, "auditDataDAO", auditDataDAO);

		Set<ContractorType> contractorTypes = new HashSet<>();
		contractorTypes.add(ContractorType.Onsite);
		Whitebox.setInternalState(auditTypesBuilder, "contractorTypes", contractorTypes);

		when(auditTypeRuleCache.getRules(contractor)).thenReturn(auditTypeRules);

		List<OperatorAccount> contractorOperatorAccounts = setupOperatorAccountsWithActiveOperator();
		when(contractor.getOperatorAccounts()).thenReturn(contractorOperatorAccounts);
	}

    @Test
    public void testBuildQuestionAnswersMap_NoAnswer() throws Exception {
        AuditTypeRule otherTypeRule = createAuditTypeRuleForTypeAndCategory(200, 102, "Test Cat 1");
        AuditQuestion otherQuestion = createQuestion(otherTypeRule, 100);


        AuditTypeRule manualTypeRule = createAuditTypeRuleForTypeAndCategory(AuditType.MANUAL_AUDIT, 101, "Test Cat 1");
        manualTypeRule.setQuestion(otherQuestion);
        manualTypeRule.setQuestionAnswer("Yes");

        List<AuditData> auditDataList = new ArrayList<>();

        when(auditDataDAO.findAnswersByContractorAndQuestion(contractor, otherQuestion)).thenReturn(auditDataList);

        List<AuditTypeRule> rules = new ArrayList<>();
        rules.add(manualTypeRule);
        rules.add(otherTypeRule);

        Map<Integer, List<AuditData>> map;
        map = Whitebox.invokeMethod(auditTypesBuilder, "buildQuestionAnswersMap", rules);
        assertEquals(0, map.get(otherQuestion.getId()).size());
    }

    @Test
    public void testBuildQuestionAnswersMap_NonApplicableCategory() throws Exception {
        AuditTypeRule otherTypeRule = createAuditTypeRuleForTypeAndCategory(200, 102, "Test Cat 1");
        AuditQuestion otherQuestion = createQuestion(otherTypeRule, 100);

        ContractorAudit audit = EntityFactory.makeContractorAudit(otherTypeRule.getAuditType(), contractor);
        List<ContractorAudit> audits = new ArrayList<>();
        audits.add(audit);
        when(contractor.getAudits()).thenReturn(audits);

        AuditTypeRule manualTypeRule = createAuditTypeRuleForTypeAndCategory(AuditType.MANUAL_AUDIT, 101, "Test Cat 1");
        manualTypeRule.setQuestion(otherQuestion);
        manualTypeRule.setQuestionAnswer("Yes");

        List<AuditData> auditDataList = new ArrayList<>();
        AuditData auditData = new AuditData();
        auditData.setQuestion(otherQuestion);
        auditData.setAnswer("No");
        auditData.setAudit(audit);
        auditDataList.add(auditData);

        AuditCatData auditCatData = new AuditCatData();
        auditCatData.setAudit(audit);
        auditCatData.setCategory(otherQuestion.getCategory());
        auditCatData.setApplies(false);
        audit.getCategories().add(auditCatData);

        when(auditDataDAO.findAnswersByContractorAndQuestion(contractor, otherQuestion)).thenReturn(auditDataList);

        List<AuditTypeRule> rules = new ArrayList<>();
        rules.add(manualTypeRule);
        rules.add(otherTypeRule);

        Map<Integer, List<AuditData>> map;
        map = Whitebox.invokeMethod(auditTypesBuilder, "buildQuestionAnswersMap", rules);
        assertEquals(0, map.get(otherQuestion.getId()).size());
    }

    @Test
    public void testBuildQuestionAnswersMap_ApplicableCategory() throws Exception {
        AuditTypeRule otherTypeRule = createAuditTypeRuleForTypeAndCategory(200, 102, "Test Cat 1");
        AuditQuestion otherQuestion = createQuestion(otherTypeRule, 100);

        ContractorAudit audit = EntityFactory.makeContractorAudit(otherTypeRule.getAuditType(), contractor);
        List<ContractorAudit> audits = new ArrayList<>();
        audits.add(audit);
        when(contractor.getAudits()).thenReturn(audits);

        AuditTypeRule manualTypeRule = createAuditTypeRuleForTypeAndCategory(AuditType.MANUAL_AUDIT, 101, "Test Cat 1");
        manualTypeRule.setQuestion(otherQuestion);
        manualTypeRule.setQuestionAnswer("Yes");

        List<AuditData> auditDataList = new ArrayList<>();
        AuditData auditData = new AuditData();
        auditData.setQuestion(otherQuestion);
        auditData.setAnswer("No");
        auditData.setAudit(audit);
        auditDataList.add(auditData);

        AuditCatData auditCatData = new AuditCatData();
        auditCatData.setAudit(audit);
        auditCatData.setCategory(otherQuestion.getCategory());
        auditCatData.setApplies(true);
        audit.getCategories().add(auditCatData);

        when(auditDataDAO.findAnswersByContractorAndQuestion(contractor, otherQuestion)).thenReturn(auditDataList);

        List<AuditTypeRule> rules = new ArrayList<>();
        rules.add(manualTypeRule);
        rules.add(otherTypeRule);

        Map<Integer, List<AuditData>> map;
        map = Whitebox.invokeMethod(auditTypesBuilder, "buildQuestionAnswersMap", rules);
        assertEquals(1, map.get(otherQuestion.getId()).size());
    }

    private AuditQuestion createQuestion(AuditTypeRule typeRule, int categoryId) {
        AuditQuestion question = EntityFactory.makeAuditQuestion();
        for (AuditCategory category:typeRule.getAuditType().getCategories()) {
            if (category.getId() == categoryId) {
                question.setCategory(category);

            }
        }
        return question;
    }

    @Test
    public void testRulePruningForDependentAuditTypes() throws Exception {
        AuditTypeRule auditTypeRule = createAuditTypeRuleForTypeAndCategory(AuditType.MANUAL_AUDIT, 101, "Test Cat 1");
        auditTypeRule.setDependentAuditType(EntityFactory.makeAuditType(200));
        auditTypeRule.setDependentAuditStatus(AuditStatus.Complete);

        auditTypeRules.add(auditTypeRule);

        Set<AuditTypesBuilder.AuditTypeDetail> auditTypeDetails = auditTypesBuilder.calculate();
        assertEquals(0, auditTypeDetails.size());
    }

	@Test
	public void testCalculate_whenWelcomeRuleIsPresentAndOperatorIsActive_addAuditTypeDetail() throws Exception {
		AuditTypeRule auditTypeRule = createAuditTypeRuleForTypeAndCategory(AuditType.WELCOME, 101, "Welcome Category 1");
		auditTypeRules.add(auditTypeRule);

		Set<AuditTypesBuilder.AuditTypeDetail> auditTypeDetails = auditTypesBuilder.calculate();

		assertEquals(1, auditTypeDetails.size());
		AuditTypesBuilder.AuditTypeDetail auditTypeDetail = (AuditTypesBuilder.AuditTypeDetail) auditTypeDetails.toArray()[0];
		assertEquals(auditTypeDetail.rule, auditTypeRule);
		assertEquals(1, auditTypeDetail.operators.size());
		assertEquals(auditTypeDetail.operators.toArray()[0], operator);

	}

	@Test
	public void testCalculate_whenRuleIndicatesYearToCheck_andMatchingAnswerExists_addAuditTypeDetail() throws Exception {
		AuditQuestion question = createAuditQuestion(10);
		AuditTypeRule auditTypeRule = createAuditTypeRuleForTypeAndCategory(100, 200, "Test Category");
		setupRuleCriteria(auditTypeRule, question, QuestionComparator.GreaterThan, "1", PastAuditYear.ThreeYearsAgo);
		auditTypeRules.add(auditTypeRule);

		String[][] answerForAuditYearArray = {
				new String[]{"1", currentYearMinus(4)},
				new String[]{"3", currentYearMinus(2)},
				new String[]{"2", currentYearMinus(3)},
				new String[]{"4", currentYearMinus(1)},
				new String[]{"5", currentYearMinus(0)}
		};

		List<AuditData> answers = buildAnswersForQuestion(question, answerForAuditYearArray);
		when(auditDataDAO.findAnswersByContractorAndQuestion(contractor, question)).thenReturn(answers);

		Set<AuditTypesBuilder.AuditTypeDetail> auditTypeDetails = auditTypesBuilder.calculate();

		assertEquals(1, auditTypeDetails.size());
		AuditTypesBuilder.AuditTypeDetail auditTypeDetail = (AuditTypesBuilder.AuditTypeDetail) auditTypeDetails.toArray()[0];
		assertEquals(auditTypeDetail.rule, auditTypeRule);
	}

    @Test
    public void testCalculate_whenRuleIndicatesYearToCheck_andMatchingAnswerExists_dependentOnVisibleQuestions() throws Exception {
        AuditQuestion visibleQuestion = createAuditQuestion(20);
        AuditQuestion question = createAuditQuestion(10);
        question.setVisibleQuestion(visibleQuestion);
        question.setVisibleAnswer("Yes");

        AuditData visibleAnswer = new AuditData();
        visibleAnswer.setAnswer("Yes");
        AuditTypeRule auditTypeRule = createAuditTypeRuleForTypeAndCategory(100, 200, "Test Category");
        setupRuleCriteria(auditTypeRule, question, QuestionComparator.GreaterThan, "1", PastAuditYear.ThreeYearsAgo);
        auditTypeRules.add(auditTypeRule);

        String[][] answerForAuditYearArray = {
                new String[]{"1", currentYearMinus(4)},
                new String[]{"3", currentYearMinus(2)},
                new String[]{"2", currentYearMinus(3)},
                new String[]{"4", currentYearMinus(1)},
                new String[]{"5", currentYearMinus(0)}
        };

        List<AuditData> answers = buildAnswersForQuestion(question, answerForAuditYearArray);
        when(auditDataDAO.findAnswersByContractorAndQuestion(contractor, question)).thenReturn(answers);
        when(auditDataDAO.findAnswerToQuestion(0, 20)).thenReturn(visibleAnswer);

        Set<AuditTypesBuilder.AuditTypeDetail> auditTypeDetails = auditTypesBuilder.calculate();

        assertEquals(1, auditTypeDetails.size());
        AuditTypesBuilder.AuditTypeDetail auditTypeDetail = (AuditTypesBuilder.AuditTypeDetail) auditTypeDetails.toArray()[0];
        assertEquals(auditTypeDetail.rule, auditTypeRule);

        // make it invisible
        visibleAnswer.setAnswer("No");
        auditTypeDetails = auditTypesBuilder.calculate();
        assertEquals(0, auditTypeDetails.size());

    }

    @Test
	public void testChooseAnswerToEvaluate_whenRuleDoesNotApplyToASpecificYear_ChooseTheFirstAnswer() throws Exception {
		AuditQuestion question = createAuditQuestion(10);
		AuditTypeRule auditTypeRule = createAuditTypeRuleForTypeAndCategory(100, 200, "Test Category");
		setupRuleCriteria(auditTypeRule, question, QuestionComparator.GreaterThan, "1", PastAuditYear.Any);
		auditTypeRules.add(auditTypeRule);

		String[][] answerForAuditYearArray = {
				new String[]{"1", currentYearMinus(4)}, // first one found wins
				new String[]{"3", currentYearMinus(2)},
				new String[]{"2", currentYearMinus(3)},
				new String[]{"4", currentYearMinus(1)},
				new String[]{"5", currentYearMinus(0)}
		};

		List<AuditData> answers = buildAnswersForQuestion(question, answerForAuditYearArray);

		AuditData auditData = auditTypesBuilder.chooseAnswerToEvaluate(auditTypeRule, answers);

		assertEquals("1", auditData.getAnswer());
		assertEquals(currentYearMinus(4), auditData.getAudit().getAuditFor());
	}

	@Test
	public void testChooseAnswerToEvaluate_whenRuleAppliesToASpecificYear_ChooseTheAnswerForThatYear() throws Exception {
		AuditQuestion question = createAuditQuestion(10);
		AuditTypeRule auditTypeRule = createAuditTypeRuleForTypeAndCategory(100, 200, "Test Category");
		setupRuleCriteria(auditTypeRule, question, QuestionComparator.GreaterThan, "1", PastAuditYear.TwoYearsAgo);
		auditTypeRules.add(auditTypeRule);

		String[][] answerForAuditYearArray = {
				new String[]{"1", currentYearMinus(4)},
				new String[]{"3", currentYearMinus(2)}, // winner
				new String[]{"2", currentYearMinus(3)},
				new String[]{"4", currentYearMinus(1)},
				new String[]{"5", currentYearMinus(0)}
		};

		List<AuditData> answers = buildAnswersForQuestion(question, answerForAuditYearArray);

		AuditData auditData = auditTypesBuilder.chooseAnswerToEvaluate(auditTypeRule, answers);

		assertEquals("3", auditData.getAnswer());
		assertEquals(currentYearMinus(2), auditData.getAudit().getAuditFor());
	}

	@Test
	public void testChooseAnswerToEvaluate_whenRuleAppliesToASpecificYear_ChooseTheAnswerForThatYearOrNull() throws Exception {
		AuditQuestion question = createAuditQuestion(10);
		AuditTypeRule auditTypeRule = createAuditTypeRuleForTypeAndCategory(100, 200, "Test Category");
		setupRuleCriteria(auditTypeRule, question, QuestionComparator.GreaterThan, "1", PastAuditYear.TwoYearsAgo);
		auditTypeRules.add(auditTypeRule);

		String[][] answerForAuditYearArray = {
				new String[]{"1", currentYearMinus(4)},
				new String[]{"3", currentYearMinus(3)},
				new String[]{"2", currentYearMinus(3)},
				new String[]{"4", currentYearMinus(1)},
				new String[]{"5", currentYearMinus(0)}
		};

		List<AuditData> answers = buildAnswersForQuestion(question, answerForAuditYearArray);

		AuditData auditData = auditTypesBuilder.chooseAnswerToEvaluate(auditTypeRule, answers);

		assertEquals(null, auditData);
	}

    @Test
    public void testChooseAnswerToEvaluate_whenNoSpecificYearNoAnswer() throws Exception {
        AuditQuestion question = createAuditQuestion(10);
        AuditTypeRule auditTypeRule = createAuditTypeRuleForTypeAndCategory(100, 200, "Test Category");
        setupRuleCriteria(auditTypeRule, question, QuestionComparator.GreaterThan, "1", PastAuditYear.Any);
        auditTypeRules.add(auditTypeRule);

        List<AuditData> answers = new ArrayList<>();

        AuditData auditData = auditTypesBuilder.chooseAnswerToEvaluate(auditTypeRule, answers);

        assertEquals(null, auditData);
    }

	private String currentYearMinus(int x) {
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		return String.valueOf(currentYear - x);
	}

	private void setupRuleCriteria(AuditTypeRule auditTypeRule, AuditQuestion question, QuestionComparator comparator, String value, PastAuditYear yearToCheck) {
		auditTypeRule.setQuestion(question);
		auditTypeRule.setQuestionComparator(comparator);
		auditTypeRule.setQuestionAnswer(value);
		auditTypeRule.setYearToCheck(yearToCheck);
	}

	private List<AuditData> buildAnswersForQuestion(AuditQuestion question, String[][] answerForAuditYearArray) {
		List<AuditData> answers = new ArrayList<>();
		for (String[] answerAndYear : answerForAuditYearArray) {

			AuditData auditData = new AuditData();
			auditData.setQuestion(question);
			auditData.setAnswer(answerAndYear[0]);

			ContractorAudit audit = new ContractorAudit();
			audit.setAuditFor(answerAndYear[1]);
			auditData.setAudit(audit);

			answers.add(auditData);
		}

		return answers;
	}

	private AuditQuestion createAuditQuestion(int questionId) {
		AuditQuestion question = new AuditQuestion();
		question.setId(questionId);
		question.setEffectiveDate(DateBean.parseDate("2000-01-01"));
		question.setExpirationDate(DateBean.parseDate("4000-01-01"));
		question.setQuestionType("Number");
		return question;
	}

	private AuditTypeRule createAuditTypeRuleForTypeAndCategory(int auditTypeId, int categoryId, String categoryName) {
		AuditType auditType = EntityFactory.makeAuditType(auditTypeId);
		AuditCategory auditCategory = createAuditCategory(categoryId, categoryName);
		addAuditCategoryToAuditType(auditCategory, auditType);
		AuditTypeRule auditTypeRule = createAuditTypeRuleForAuditType(auditType);

		return auditTypeRule;
	}

	private AuditCategory createAuditCategory(int categoryId, String categoryName) {
		AuditCategory auditCategory = new AuditCategory();
		auditCategory.setId(categoryId);
		auditCategory.setName(categoryName);

		return auditCategory;
	}

	public AuditType addAuditCategoryToAuditType(AuditCategory auditCategory, AuditType auditType) {
		auditCategory.setAuditType(auditType);
		auditType.getCategories().add(auditCategory);
		auditCategory.setNumber(auditType.getCategories().get(auditType.getCategories().size() - 1).getNumber() + 1);

		return auditType;
	}

	private AuditTypeRule createAuditTypeRuleForAuditType(AuditType auditType) {
		AuditTypeRule auditTypeRule = new AuditTypeRule();
		auditTypeRule.setAuditType(auditType);
		auditTypeRule.setInclude(true);

		return auditTypeRule;
	}

	private List<OperatorAccount> setupOperatorAccountsWithActiveOperator() {
		when(operator.isOperator()).thenReturn(true);
		when(operator.getStatus()).thenReturn(AccountStatus.Active);
		List<OperatorAccount> operatorAccounts = new ArrayList<>();
		operatorAccounts.add(operator);
		return operatorAccounts;
	}

}
