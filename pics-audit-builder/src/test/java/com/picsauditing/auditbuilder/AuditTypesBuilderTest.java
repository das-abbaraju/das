package com.picsauditing.auditbuilder;

import com.picsauditing.EntityFactory;
import com.picsauditing.auditbuilder.dao.DocumentDataDAO;
import com.picsauditing.auditbuilder.entities.*;
import com.picsauditing.auditbuilder.util.DateBean;
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
	List<DocumentTypeRule> documentTypeRules = new ArrayList<>();

	@Mock
    AuditTypeRuleCache2 auditTypeRuleCache;
	@Mock
    ContractorAccount contractor;
	@Mock
    OperatorAccount operator;
	@Mock
    DocumentDataDAO auditDataDAO;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		auditTypesBuilder = new AuditTypesBuilder();
        auditTypesBuilder.setRuleCache(auditTypeRuleCache);
        auditTypesBuilder.setContractor(contractor);

		Whitebox.setInternalState(auditTypesBuilder, "auditDataDAO", auditDataDAO);

		Set<ContractorType> contractorTypes = new HashSet<>();
		contractorTypes.add(ContractorType.Onsite);
		Whitebox.setInternalState(auditTypesBuilder, "contractorTypes", contractorTypes);

		when(auditTypeRuleCache.getRules(contractor)).thenReturn(documentTypeRules);

		List<ContractorOperator> contractorOperatorAccounts = setupContractorOperatorWithActiveOperator();
		when(contractor.getOperators()).thenReturn(contractorOperatorAccounts);
	}

    @Test
    public void testBuildQuestionAnswersMap_NoAnswer() throws Exception {
        DocumentTypeRule otherTypeRule = createAuditTypeRuleForTypeAndCategory(200, 102, "Test Cat 1");
        DocumentQuestion otherQuestion = createQuestion(otherTypeRule, 100);


        DocumentTypeRule manualTypeRule = createAuditTypeRuleForTypeAndCategory(AuditType.MANUAL_AUDIT, 101, "Test Cat 1");
        manualTypeRule.setQuestion(otherQuestion);
        manualTypeRule.setQuestionAnswer("Yes");

        List<DocumentData> documentDataList = new ArrayList<>();

        when(auditDataDAO.findAnswersByContractorAndQuestion(contractor, otherQuestion)).thenReturn(documentDataList);

        List<DocumentTypeRule> rules = new ArrayList<>();
        rules.add(manualTypeRule);
        rules.add(otherTypeRule);

        Map<Integer, List<DocumentData>> map;
        map = Whitebox.invokeMethod(auditTypesBuilder, "buildQuestionAnswersMap", rules);
        assertEquals(0, map.get(otherQuestion.getId()).size());
    }

    @Test
    public void testBuildQuestionAnswersMap_NonApplicableCategory() throws Exception {
        DocumentTypeRule otherTypeRule = createAuditTypeRuleForTypeAndCategory(200, 102, "Test Cat 1");
        DocumentQuestion otherQuestion = createQuestion(otherTypeRule, 100);

        ContractorDocument audit = EntityFactory.makeContractorAudit(otherTypeRule.getAuditType(), contractor);
        List<ContractorDocument> audits = new ArrayList<>();
        audits.add(audit);
        when(contractor.getAudits()).thenReturn(audits);

        DocumentTypeRule manualTypeRule = createAuditTypeRuleForTypeAndCategory(AuditType.MANUAL_AUDIT, 101, "Test Cat 1");
        manualTypeRule.setQuestion(otherQuestion);
        manualTypeRule.setQuestionAnswer("Yes");

        List<DocumentData> documentDataList = new ArrayList<>();
        DocumentData documentData = new DocumentData();
        documentData.setQuestion(otherQuestion);
        documentData.setAnswer("No");
        documentData.setAudit(audit);
        documentDataList.add(documentData);

        DocumentCatData documentCatData = new DocumentCatData();
        documentCatData.setAudit(audit);
        documentCatData.setCategory(otherQuestion.getCategory());
        documentCatData.setApplies(false);
        audit.getCategories().add(documentCatData);

        when(auditDataDAO.findAnswersByContractorAndQuestion(contractor, otherQuestion)).thenReturn(documentDataList);

        List<DocumentTypeRule> rules = new ArrayList<>();
        rules.add(manualTypeRule);
        rules.add(otherTypeRule);

        Map<Integer, List<DocumentData>> map;
        map = Whitebox.invokeMethod(auditTypesBuilder, "buildQuestionAnswersMap", rules);
        assertEquals(0, map.get(otherQuestion.getId()).size());
    }

    @Test
    public void testBuildQuestionAnswersMap_ApplicableCategory() throws Exception {
        DocumentTypeRule otherTypeRule = createAuditTypeRuleForTypeAndCategory(200, 102, "Test Cat 1");
        DocumentQuestion otherQuestion = createQuestion(otherTypeRule, 100);

        ContractorDocument audit = EntityFactory.makeContractorAudit(otherTypeRule.getAuditType(), contractor);
        List<ContractorDocument> audits = new ArrayList<>();
        audits.add(audit);
        when(contractor.getAudits()).thenReturn(audits);

        DocumentTypeRule manualTypeRule = createAuditTypeRuleForTypeAndCategory(AuditType.MANUAL_AUDIT, 101, "Test Cat 1");
        manualTypeRule.setQuestion(otherQuestion);
        manualTypeRule.setQuestionAnswer("Yes");

        List<DocumentData> documentDataList = new ArrayList<>();
        DocumentData documentData = new DocumentData();
        documentData.setQuestion(otherQuestion);
        documentData.setAnswer("No");
        documentData.setAudit(audit);
        documentDataList.add(documentData);

        DocumentCatData documentCatData = new DocumentCatData();
        documentCatData.setAudit(audit);
        documentCatData.setCategory(otherQuestion.getCategory());
        documentCatData.setApplies(true);
        audit.getCategories().add(documentCatData);

        when(auditDataDAO.findAnswersByContractorAndQuestion(contractor, otherQuestion)).thenReturn(documentDataList);

        List<DocumentTypeRule> rules = new ArrayList<>();
        rules.add(manualTypeRule);
        rules.add(otherTypeRule);

        Map<Integer, List<DocumentData>> map;
        map = Whitebox.invokeMethod(auditTypesBuilder, "buildQuestionAnswersMap", rules);
        assertEquals(1, map.get(otherQuestion.getId()).size());
    }

    private DocumentQuestion createQuestion(DocumentTypeRule typeRule, int categoryId) {
        DocumentQuestion question = EntityFactory.makeAuditQuestion();
        for (DocumentCategory category:typeRule.getAuditType().getCategories()) {
            if (category.getId() == categoryId) {
                question.setCategory(category);

            }
        }
        return question;
    }

    @Test
    public void testRulePruningForDependentAuditTypes() throws Exception {
        DocumentTypeRule documentTypeRule = createAuditTypeRuleForTypeAndCategory(AuditType.MANUAL_AUDIT, 101, "Test Cat 1");
        documentTypeRule.setDependentAuditType(EntityFactory.makeAuditType(200));
        documentTypeRule.setDependentDocumentStatus(DocumentStatus.Complete);

        documentTypeRules.add(documentTypeRule);

        Set<AuditTypesBuilder.AuditTypeDetail> auditTypeDetails = auditTypesBuilder.calculate();
        assertEquals(0, auditTypeDetails.size());
    }

	@Test
	public void testCalculate_whenWelcomeRuleIsPresentAndOperatorIsActive_addAuditTypeDetail() throws Exception {
		DocumentTypeRule documentTypeRule = createAuditTypeRuleForTypeAndCategory(AuditType.WELCOME, 101, "Welcome Category 1");
		documentTypeRules.add(documentTypeRule);

		Set<AuditTypesBuilder.AuditTypeDetail> auditTypeDetails = auditTypesBuilder.calculate();

		assertEquals(1, auditTypeDetails.size());
		AuditTypesBuilder.AuditTypeDetail auditTypeDetail = (AuditTypesBuilder.AuditTypeDetail) auditTypeDetails.toArray()[0];
		assertEquals(auditTypeDetail.rule, documentTypeRule);
		assertEquals(1, auditTypeDetail.operators.size());
		assertEquals(auditTypeDetail.operators.toArray()[0], operator);

	}

	@Test
	public void testCalculate_whenRuleIndicatesYearToCheck_andMatchingAnswerExists_addAuditTypeDetail() throws Exception {
		DocumentQuestion question = createAuditQuestion(10);
		DocumentTypeRule documentTypeRule = createAuditTypeRuleForTypeAndCategory(100, 200, "Test Category");
		setupRuleCriteria(documentTypeRule, question, QuestionComparator.GreaterThan, "1", PastDocumentYear.ThreeYearsAgo);
		documentTypeRules.add(documentTypeRule);

		String[][] answerForAuditYearArray = {
				new String[]{"1", currentYearMinus(4)},
				new String[]{"3", currentYearMinus(2)},
				new String[]{"2", currentYearMinus(3)},
				new String[]{"4", currentYearMinus(1)},
				new String[]{"5", currentYearMinus(0)}
		};

		List<DocumentData> answers = buildAnswersForQuestion(question, answerForAuditYearArray);
		when(auditDataDAO.findAnswersByContractorAndQuestion(contractor, question)).thenReturn(answers);

		Set<AuditTypesBuilder.AuditTypeDetail> auditTypeDetails = auditTypesBuilder.calculate();

		assertEquals(1, auditTypeDetails.size());
		AuditTypesBuilder.AuditTypeDetail auditTypeDetail = (AuditTypesBuilder.AuditTypeDetail) auditTypeDetails.toArray()[0];
		assertEquals(auditTypeDetail.rule, documentTypeRule);
	}

    @Test
    public void testCalculate_whenRuleIndicatesYearToCheck_andMatchingAnswerExists_dependentOnVisibleQuestions() throws Exception {
        DocumentQuestion visibleQuestion = createAuditQuestion(20);
        DocumentQuestion question = createAuditQuestion(10);
        question.setVisibleQuestion(visibleQuestion);
        question.setVisibleAnswer("Yes");

        DocumentData visibleAnswer = new DocumentData();
        visibleAnswer.setAnswer("Yes");
        DocumentTypeRule documentTypeRule = createAuditTypeRuleForTypeAndCategory(100, 200, "Test Category");
        setupRuleCriteria(documentTypeRule, question, QuestionComparator.GreaterThan, "1", PastDocumentYear.ThreeYearsAgo);
        documentTypeRules.add(documentTypeRule);

        String[][] answerForAuditYearArray = {
                new String[]{"1", currentYearMinus(4)},
                new String[]{"3", currentYearMinus(2)},
                new String[]{"2", currentYearMinus(3)},
                new String[]{"4", currentYearMinus(1)},
                new String[]{"5", currentYearMinus(0)}
        };

        List<DocumentData> answers = buildAnswersForQuestion(question, answerForAuditYearArray);
        when(auditDataDAO.findAnswersByContractorAndQuestion(contractor, question)).thenReturn(answers);
        when(auditDataDAO.findAnswerToQuestion(0, 20)).thenReturn(visibleAnswer);

        Set<AuditTypesBuilder.AuditTypeDetail> auditTypeDetails = auditTypesBuilder.calculate();

        assertEquals(1, auditTypeDetails.size());
        AuditTypesBuilder.AuditTypeDetail auditTypeDetail = (AuditTypesBuilder.AuditTypeDetail) auditTypeDetails.toArray()[0];
        assertEquals(auditTypeDetail.rule, documentTypeRule);

        // make it invisible
        visibleAnswer.setAnswer("No");
        auditTypeDetails = auditTypesBuilder.calculate();
        assertEquals(0, auditTypeDetails.size());

    }

    @Test
	public void testChooseAnswerToEvaluate_whenRuleDoesNotApplyToASpecificYear_ChooseTheFirstAnswer() throws Exception {
		DocumentQuestion question = createAuditQuestion(10);
		DocumentTypeRule documentTypeRule = createAuditTypeRuleForTypeAndCategory(100, 200, "Test Category");
		setupRuleCriteria(documentTypeRule, question, QuestionComparator.GreaterThan, "1", PastDocumentYear.Any);
		documentTypeRules.add(documentTypeRule);

		String[][] answerForAuditYearArray = {
				new String[]{"1", currentYearMinus(4)}, // first one found wins
				new String[]{"3", currentYearMinus(2)},
				new String[]{"2", currentYearMinus(3)},
				new String[]{"4", currentYearMinus(1)},
				new String[]{"5", currentYearMinus(0)}
		};

		List<DocumentData> answers = buildAnswersForQuestion(question, answerForAuditYearArray);

		DocumentData documentData = auditTypesBuilder.chooseAnswerToEvaluate(documentTypeRule, answers);

		assertEquals("1", documentData.getAnswer());
		assertEquals(currentYearMinus(4), documentData.getAudit().getAuditFor());
	}

	@Test
	public void testChooseAnswerToEvaluate_whenRuleAppliesToASpecificYear_ChooseTheAnswerForThatYear() throws Exception {
		DocumentQuestion question = createAuditQuestion(10);
		DocumentTypeRule documentTypeRule = createAuditTypeRuleForTypeAndCategory(100, 200, "Test Category");
		setupRuleCriteria(documentTypeRule, question, QuestionComparator.GreaterThan, "1", PastDocumentYear.TwoYearsAgo);
		documentTypeRules.add(documentTypeRule);

		String[][] answerForAuditYearArray = {
				new String[]{"1", currentYearMinus(4)},
				new String[]{"3", currentYearMinus(2)}, // winner
				new String[]{"2", currentYearMinus(3)},
				new String[]{"4", currentYearMinus(1)},
				new String[]{"5", currentYearMinus(0)}
		};

		List<DocumentData> answers = buildAnswersForQuestion(question, answerForAuditYearArray);

		DocumentData documentData = auditTypesBuilder.chooseAnswerToEvaluate(documentTypeRule, answers);

		assertEquals("3", documentData.getAnswer());
		assertEquals(currentYearMinus(2), documentData.getAudit().getAuditFor());
	}

	@Test
	public void testChooseAnswerToEvaluate_whenRuleAppliesToASpecificYear_ChooseTheAnswerForThatYearOrNull() throws Exception {
		DocumentQuestion question = createAuditQuestion(10);
		DocumentTypeRule documentTypeRule = createAuditTypeRuleForTypeAndCategory(100, 200, "Test Category");
		setupRuleCriteria(documentTypeRule, question, QuestionComparator.GreaterThan, "1", PastDocumentYear.TwoYearsAgo);
		documentTypeRules.add(documentTypeRule);

		String[][] answerForAuditYearArray = {
				new String[]{"1", currentYearMinus(4)},
				new String[]{"3", currentYearMinus(3)},
				new String[]{"2", currentYearMinus(3)},
				new String[]{"4", currentYearMinus(1)},
				new String[]{"5", currentYearMinus(0)}
		};

		List<DocumentData> answers = buildAnswersForQuestion(question, answerForAuditYearArray);

		DocumentData documentData = auditTypesBuilder.chooseAnswerToEvaluate(documentTypeRule, answers);

		assertEquals(null, documentData);
	}

    @Test
    public void testChooseAnswerToEvaluate_whenNoSpecificYearNoAnswer() throws Exception {
        DocumentQuestion question = createAuditQuestion(10);
        DocumentTypeRule documentTypeRule = createAuditTypeRuleForTypeAndCategory(100, 200, "Test Category");
        setupRuleCriteria(documentTypeRule, question, QuestionComparator.GreaterThan, "1", PastDocumentYear.Any);
        documentTypeRules.add(documentTypeRule);

        List<DocumentData> answers = new ArrayList<>();

        DocumentData documentData = auditTypesBuilder.chooseAnswerToEvaluate(documentTypeRule, answers);

        assertEquals(null, documentData);
    }

	private String currentYearMinus(int x) {
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		return String.valueOf(currentYear - x);
	}

	private void setupRuleCriteria(DocumentTypeRule documentTypeRule, DocumentQuestion question, QuestionComparator comparator, String value, PastDocumentYear yearToCheck) {
		documentTypeRule.setQuestion(question);
		documentTypeRule.setQuestionComparator(comparator);
		documentTypeRule.setQuestionAnswer(value);
		documentTypeRule.setYearToCheck(yearToCheck);
	}

	private List<DocumentData> buildAnswersForQuestion(DocumentQuestion question, String[][] answerForAuditYearArray) {
		List<DocumentData> answers = new ArrayList<>();
		for (String[] answerAndYear : answerForAuditYearArray) {

			DocumentData documentData = new DocumentData();
			documentData.setQuestion(question);
			documentData.setAnswer(answerAndYear[0]);

			ContractorDocument audit = new ContractorDocument();
			audit.setAuditFor(answerAndYear[1]);
			documentData.setAudit(audit);

			answers.add(documentData);
		}

		return answers;
	}

	private DocumentQuestion createAuditQuestion(int questionId) {
		DocumentQuestion question = new DocumentQuestion();
		question.setId(questionId);
		question.setEffectiveDate(DateBean.parseDate("2000-01-01"));
		question.setExpirationDate(DateBean.parseDate("4000-01-01"));
		question.setQuestionType("Number");
		return question;
	}

	private DocumentTypeRule createAuditTypeRuleForTypeAndCategory(int auditTypeId, int categoryId, String categoryName) {
		AuditType auditType = EntityFactory.makeAuditType(auditTypeId);
		DocumentCategory documentCategory = createAuditCategory(categoryId, categoryName);
		addAuditCategoryToAuditType(documentCategory, auditType);
		DocumentTypeRule documentTypeRule = createAuditTypeRuleForAuditType(auditType);

		return documentTypeRule;
	}

	private DocumentCategory createAuditCategory(int categoryId, String categoryName) {
		DocumentCategory documentCategory = new DocumentCategory();
		documentCategory.setId(categoryId);

		return documentCategory;
	}

	public AuditType addAuditCategoryToAuditType(DocumentCategory documentCategory, AuditType auditType) {
		documentCategory.setAuditType(auditType);
		auditType.getCategories().add(documentCategory);

		return auditType;
	}

	private DocumentTypeRule createAuditTypeRuleForAuditType(AuditType auditType) {
		DocumentTypeRule documentTypeRule = new DocumentTypeRule();
		documentTypeRule.setAuditType(auditType);
		documentTypeRule.setInclude(true);

		return documentTypeRule;
	}

	private List<ContractorOperator> setupContractorOperatorWithActiveOperator() {
		when(operator.getType()).thenReturn("Operator");
		when(operator.getStatus()).thenReturn(AccountStatus.Active);
		List<ContractorOperator> contractorOperators = new ArrayList<>();
        ContractorOperator contractorOperator = new ContractorOperator();
        contractorOperator.setOperatorAccount(operator);
		contractorOperators.add(contractorOperator);
		return contractorOperators;
	}

}
