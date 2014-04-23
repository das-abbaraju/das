package com.picsauditing.flagcalculator.etl;

import com.picsauditing.flagcalculator.EntityFactory;
import com.picsauditing.flagcalculator.PicsTestUtil;
import com.picsauditing.flagcalculator.dao.FlagEtlDAO;
import com.picsauditing.flagcalculator.entities.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.*;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

//@RunWith(PowerMockRunner.class)
//@PrepareForTest(ContractorFlagETL.class)
//@PowerMockIgnore({ "javax.xml.parsers.*", "ch.qos.logback.*", "org.slf4j.*", "org.apache.xerces.*" })
public class ContractorFlagETLTest {
	private ContractorFlagETL contractorFlagETL;

	@Mock
	private FlagEtlDAO flagEtlDAO;
	@Mock
	private FlagCriteria flagCriteria;
	@Mock
	private AuditQuestion auditQuestion;
	@Mock
	private AuditType auditType;
	@Mock
	private ContractorAccount contractor;
	@Mock
	private AuditCategory auditCategory;
	@Mock
	private AuditCategory auditCategory2;
	@Mock
	private HashMap<Integer, AuditData> answerMap;
	@Mock
	private ContractorAudit audit;
	@Mock
	private ContractorAudit audit1;
	@Mock
	private ContractorAudit audit2;
	@Mock
	private ContractorAudit audit3;
	@Mock
	private AuditData auditData;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		contractorFlagETL = new ContractorFlagETL(flagEtlDAO);
		PicsTestUtil.autowireDAOsFromDeclaredMocks(contractorFlagETL, this);
	}

    @Test
    public void testPerformFlaggingForNonEMR_NullOrEmptyAnswer() throws Exception {
        AuditQuestion question = EntityFactory.makeAuditQuestion();
        AuditData data = EntityFactory.makeAuditData("", question);

        when(flagCriteria.getQuestion()).thenReturn(question);
        when(answerMap.get(question.getId())).thenReturn(null);

        Set<FlagCriteriaContractor> changes;
        changes = Whitebox.invokeMethod(contractorFlagETL, "performFlaggingForNonEMR", contractor, answerMap, flagCriteria);
        assertEquals(0, changes.size());

        when(answerMap.get(question.getId())).thenReturn(data);
        changes = Whitebox.invokeMethod(contractorFlagETL, "performFlaggingForNonEMR", contractor, answerMap, flagCriteria);
        assertEquals(0, changes.size());
    }

    @Test
    public void testPerformFlaggingForNonEMR_VisibleQuestion_No() throws Exception {
        AuditQuestion question = EntityFactory.makeAuditQuestion();
        AuditData data = EntityFactory.makeAuditData("No", question);

        AuditQuestion parentQuestion = EntityFactory.makeAuditQuestion();
        AuditData parentData = EntityFactory.makeAuditData("No", parentQuestion);
        question.setVisibleQuestion(parentQuestion);
        question.setVisibleAnswer("Yes");

        when(answerMap.get(question.getId())).thenReturn(data);
        when(answerMap.get(parentQuestion.getId())).thenReturn(parentData);
        when(flagCriteria.getQuestion()).thenReturn(question);

        Set<FlagCriteriaContractor> changes;
        changes = Whitebox.invokeMethod(contractorFlagETL, "performFlaggingForNonEMR", contractor, answerMap, flagCriteria);
        assertEquals(0, changes.size());
    }

    @Test
    public void testPerformFlaggingForNonEMR_VisibleQuestion_Yes() throws Exception {
        AuditQuestion question = EntityFactory.makeAuditQuestion();
        AuditData data = EntityFactory.makeAuditData("No", question);

        AuditQuestion parentQuestion = EntityFactory.makeAuditQuestion();
        AuditData parentData = EntityFactory.makeAuditData("Yes", parentQuestion);
        question.setVisibleQuestion(parentQuestion);
        question.setVisibleAnswer("Yes");
        question.setQuestionType("text");

        when(answerMap.get(question.getId())).thenReturn(data);
        when(answerMap.get(parentQuestion.getId())).thenReturn(parentData);
        when(flagCriteria.getQuestion()).thenReturn(question);

        Set<FlagCriteriaContractor> changes;
        changes = Whitebox.invokeMethod(contractorFlagETL, "performFlaggingForNonEMR", contractor, answerMap, flagCriteria);
        assertEquals(1, changes.size());
    }

    @Test
	public void testCheckForMissingAnswer_NotFlaggableWhenMissing() throws Exception {
		when(flagCriteria.isFlaggableWhenMissing()).thenReturn(false);
		FlagCriteriaContractor result = Whitebox.invokeMethod(contractorFlagETL, "checkForMissingAnswer", flagCriteria,
				contractor);
		assertNull(result);
	}

	@Test
	public void testCheckForMissingAnswer_IsFlaggableWhenMissing() throws Exception {
		final int FLAG_CRITERIA_ID = 1;
		final int CONTRACTOR_ID = 2;
		when(flagCriteria.isFlaggableWhenMissing()).thenReturn(true);
		when(flagCriteria.getId()).thenReturn(FLAG_CRITERIA_ID);
		when(contractor.getId()).thenReturn(CONTRACTOR_ID);
		FlagCriteriaContractor result = Whitebox.invokeMethod(contractorFlagETL, "checkForMissingAnswer", flagCriteria,
				contractor);
		assertNotNull(result);
		assertNull(result.getAnswer2());
		assertNull(result.getAnswer());
		assertEquals(FLAG_CRITERIA_ID, result.getCriteria().getId());
		assertEquals(CONTRACTOR_ID, result.getContractor().getId());
	}

	private Set<FlagCriteria> mockFlagCriteriaQuestionsNotAnnualAddendumNoExcess(int numberToCreate) {
		Set<FlagCriteria> flagCriteriaSet = new HashSet<>();
		int questionId = 1;
		for (int i = 0; i < numberToCreate; i++, questionId++) {
			AuditType auditType = mock(AuditType.class);
			AuditQuestion auditQuestion = mock(AuditQuestion.class);
			when(auditQuestion.getId()).thenReturn(questionId);
            when(auditQuestion.getCategory()).thenReturn(auditCategory);
            when(auditCategory.getAuditType()).thenReturn(auditType);
			FlagCriteria flagCriteria = mock(FlagCriteria.class);
			when(flagCriteria.getQuestion()).thenReturn(auditQuestion);
			when(flagCriteria.getCategory()).thenReturn(FlagCriteriaCategory.Audits);
//			when(flagCriteria.getDescription()).thenReturn("test description");

			flagCriteriaSet.add(flagCriteria);
		}
		return flagCriteriaSet;
	}

	@Test
	public void testGetFlaggableAuditQuestionIds_AuditTypeIsNotAnnualAddendumNoExcess() throws Exception {
		Set<FlagCriteria> distinctFlagCriteria = mockFlagCriteriaQuestionsNotAnnualAddendumNoExcess(3);

		Set<Integer> criteriaQuestionSet = Whitebox.invokeMethod(contractorFlagETL, "getFlaggableAuditQuestionIds",
                distinctFlagCriteria);

		for (FlagCriteria flagCriteria : distinctFlagCriteria) {
			int questionId = flagCriteria.getQuestion().getId();
			assertTrue(criteriaQuestionSet.contains(questionId));
		}
	}

	@Test
	public void testGetFlaggableAuditQuestionIds_AuditTypeIsAnnualAddendum() throws Exception {
		when(flagCriteria.getQuestion()).thenReturn(auditQuestion);
		Set<FlagCriteria> distinctFlagCriteria = new HashSet<>();
		distinctFlagCriteria.add(flagCriteria);

		Set<Integer> criteriaQuestionSet = Whitebox.invokeMethod(contractorFlagETL, "getFlaggableAuditQuestionIds",
				distinctFlagCriteria);

		assertTrue(criteriaQuestionSet.isEmpty());
	}

	@Test
	public void testGetFlaggableAuditQuestionIds_QuestionHasNullAuditType() throws Exception {
		when(flagCriteria.getQuestion()).thenReturn(auditQuestion);
		Set<FlagCriteria> distinctFlagCriteria = new HashSet<>();
		distinctFlagCriteria.add(flagCriteria);

		Set<Integer> criteriaQuestionSet = Whitebox.invokeMethod(contractorFlagETL, "getFlaggableAuditQuestionIds",
				distinctFlagCriteria);

		assertTrue(criteriaQuestionSet.isEmpty());
	}

	@Test
	public void testGetFlaggableAuditQuestionIds_NoFlagCriteria() throws Exception {
		Set<FlagCriteria> distinctFlagCriteria = new HashSet<>();

		Set<Integer> criteriaQuestionSet = Whitebox.invokeMethod(contractorFlagETL, "getFlaggableAuditQuestionIds",
				distinctFlagCriteria);

		assertTrue(criteriaQuestionSet.isEmpty());
	}

	@Test
	public void testGetFlaggableAuditQuestionIds_CriteriaHasNullQuestion() throws Exception {
		Set<FlagCriteria> distinctFlagCriteria = new HashSet<>();
		distinctFlagCriteria.add(flagCriteria);

		Set<Integer> criteriaQuestionSet = Whitebox.invokeMethod(contractorFlagETL, "getFlaggableAuditQuestionIds",
				distinctFlagCriteria);

		assertTrue(criteriaQuestionSet.isEmpty());
	}

	@Test
	public void testRunAnnualUpdateFlaggingForCategoryOnMultiYearScope_Annual_Update_Audit() throws Exception {
		when(auditQuestion.getCategory()).thenReturn(auditCategory);
		when(auditType.getId()).thenReturn(AuditType.ANNUALADDENDUM);
        when(auditQuestion.getCategory()).thenReturn(auditCategory);
        when(auditCategory.getAuditType()).thenReturn(auditType);
		when(flagCriteria.getQuestion()).thenReturn(auditQuestion);

		Boolean result = Whitebox.invokeMethod(contractorFlagETL, "runAnnualUpdateFlaggingForCategoryOnMultiYearScope",
				flagCriteria);
		assertTrue(result);
	}

	@Test
	public void testRunAnnualUpdateFlaggingForCategoryOnMultiYearScope_Null_Category() throws Exception {
		when(auditQuestion.getCategory()).thenReturn(null);
		when(flagCriteria.getQuestion()).thenReturn(auditQuestion);

		Boolean result = Whitebox.invokeMethod(contractorFlagETL, "runAnnualUpdateFlaggingForCategoryOnMultiYearScope",
				flagCriteria);
		assertFalse(result);
	}

	@Test
	public void testRunAnnualUpdateFlaggingForCategoryOnMultiYearScope_Not_Annual_Update() throws Exception {
		when(auditQuestion.getCategory()).thenReturn(null);
//		when(auditType.isAnnualAddendum()).thenReturn(false);
//		when(auditQuestion.getAuditType()).thenReturn(auditType);
		when(flagCriteria.getQuestion()).thenReturn(auditQuestion);

		Boolean result = Whitebox.invokeMethod(contractorFlagETL, "runAnnualUpdateFlaggingForCategoryOnMultiYearScope",
				flagCriteria);
		assertFalse(result);
	}

	@Test
	public void testExecuteFlagCriteriaCalculation_No_Question_No_OSHA() throws Exception {
		when(flagCriteria.getAuditType()).thenReturn(auditType);
		when(flagCriteria.getOshaType()).thenReturn(null);
		when(flagCriteria.getQuestion()).thenReturn(null);

		Set<FlagCriteriaContractor> flagCriteriaContractor = Whitebox.invokeMethod(contractorFlagETL,
				"executeFlagCriteriaCalculation", flagCriteria, contractor, answerMap);
		assertEquals(1, flagCriteriaContractor.size());
	}

    // TODO: Need to find out the correct dependency for PowerMockito to actively use the spies
//	@Test
//	public void testExecuteFlagCriteriaCalculation_EMR_Question() throws Exception {
//		ContractorFlagETL spy = PowerMockito.spy(new ContractorFlagETL(flagEtlDAO));
//		PowerMockito.doReturn(new HashSet<FlagCriteriaContractor>()).when(spy, "calculateFlagCriteriaForEMR",
//				anyObject(), anyObject());
//
//		when(flagCriteria.getAuditType()).thenReturn(auditType);
//		when(flagCriteria.getOshaType()).thenReturn(null);
//		when(auditQuestion.getId()).thenReturn(AuditQuestion.EMR);
//		when(flagCriteria.getQuestion()).thenReturn(auditQuestion);
//
//		setCategoryApplicable(spy);
//
//		Whitebox.invokeMethod(spy, "executeFlagCriteriaCalculation", flagCriteria, contractor, answerMap);
//		PowerMockito.verifyPrivate(spy, times(1)).invoke("calculateFlagCriteriaForEMR", anyObject(), anyObject());
//	}
//
//	@Test
//	public void testExecuteFlagCriteriaCalculation_CITATIONS_Question_No_Flag_Criteria_Returned() throws Exception {
//		ContractorFlagETL spy = PowerMockito.spy(new ContractorFlagETL(flagEtlDAO));
//		PowerMockito.doReturn(null).when(spy, "generateFlaggableData", anyObject(), anyObject(), anyBoolean());
//
//		when(flagCriteria.getAuditType()).thenReturn(auditType);
//		when(flagCriteria.getOshaType()).thenReturn(null);
//		when(auditQuestion.getId()).thenReturn(AuditQuestion.CITATIONS);
//		when(flagCriteria.getQuestion()).thenReturn(auditQuestion);
//
//		setCategoryApplicable(spy);
//
//		Set<FlagCriteriaContractor> results = Whitebox.invokeMethod(spy, "executeFlagCriteriaCalculation",
//				flagCriteria, contractor, answerMap);
//		assertEquals(1, results.size());
//		PowerMockito.verifyPrivate(spy, times(1)).invoke("generateFlaggableData", anyObject(), anyObject(),
//				anyBoolean());
//	}
//
//	@Test
//	public void testExecuteFlagCriteriaCalculation_CITATIONS_Question_Flag_Criteria_Returned() throws Exception {
//		ContractorFlagETL spy = PowerMockito.spy(new ContractorFlagETL(flagEtlDAO));
//		PowerMockito.doReturn(new FlagCriteriaContractor()).when(spy, "generateFlaggableData", anyObject(),
//				anyObject(), anyBoolean());
//
//		when(flagCriteria.getAuditType()).thenReturn(auditType);
//		when(flagCriteria.getOshaType()).thenReturn(null);
//		when(auditQuestion.getId()).thenReturn(AuditQuestion.CITATIONS);
//		when(flagCriteria.getQuestion()).thenReturn(auditQuestion);
//
//		setCategoryApplicable(spy);
//
//		Set<FlagCriteriaContractor> results = Whitebox.invokeMethod(spy, "executeFlagCriteriaCalculation",
//				flagCriteria, contractor, answerMap);
//		assertEquals(2, results.size());
//		PowerMockito.verifyPrivate(spy, times(1)).invoke("generateFlaggableData", anyObject(), anyObject(),
//				anyBoolean());
//	}
//
//	@Test
//	public void testExecuteFlagCriteriaCalculation_Annual_Update_Multi_Year_No_Flag_Criteria_Returned()
//			throws Exception {
//		ContractorFlagETL spy = PowerMockito.spy(new ContractorFlagETL(flagEtlDAO));
//		PowerMockito.doReturn(true).when(spy, "runAnnualUpdateFlaggingForCategoryOnMultiYearScope", anyObject());
//		PowerMockito.doReturn(null).when(spy, "generateFlaggableData", anyObject(), anyObject(), anyBoolean());
//
//		when(flagCriteria.getAuditType()).thenReturn(auditType);
//		when(flagCriteria.getOshaType()).thenReturn(null);
//		when(auditQuestion.getId()).thenReturn(1);
//		when(flagCriteria.getQuestion()).thenReturn(auditQuestion);
//
//		setCategoryApplicable(spy);
//
//		Set<FlagCriteriaContractor> results = Whitebox.invokeMethod(spy, "executeFlagCriteriaCalculation",
//				flagCriteria, contractor, answerMap);
//		assertEquals(1, results.size());
//		PowerMockito.verifyPrivate(spy, times(1)).invoke("generateFlaggableData", anyObject(), anyObject(),
//				anyBoolean());
//		PowerMockito.verifyPrivate(spy, times(1)).invoke("runAnnualUpdateFlaggingForCategoryOnMultiYearScope",
//				anyObject());
//	}
//
//	@Test
//	public void testExecuteFlagCriteriaCalculation_Annual_Update_Multi_Year_Flag_Criteria_Returned() throws Exception {
//		ContractorFlagETL spy = PowerMockito.spy(new ContractorFlagETL(flagEtlDAO));
//		PowerMockito.doReturn(true).when(spy, "runAnnualUpdateFlaggingForCategoryOnMultiYearScope", anyObject());
//		PowerMockito.doReturn(new FlagCriteriaContractor()).when(spy, "generateFlaggableData", anyObject(),
//				anyObject(), anyBoolean());
//
//		when(flagCriteria.getAuditType()).thenReturn(auditType);
//		when(flagCriteria.getOshaType()).thenReturn(null);
//		when(auditQuestion.getId()).thenReturn(1);
//		when(flagCriteria.getQuestion()).thenReturn(auditQuestion);
//
//		setCategoryApplicable(spy);
//
//		Set<FlagCriteriaContractor> results = Whitebox.invokeMethod(spy, "executeFlagCriteriaCalculation",
//				flagCriteria, contractor, answerMap);
//		assertEquals(2, results.size());
//		PowerMockito.verifyPrivate(spy, times(1)).invoke("generateFlaggableData", anyObject(), anyObject(),
//				anyBoolean());
//		PowerMockito.verifyPrivate(spy, times(1)).invoke("runAnnualUpdateFlaggingForCategoryOnMultiYearScope",
//				anyObject());
//	}
//
//	@Test
//	public void testExecuteFlagCriteriaCalculation_Non_EMR_Criteria() throws Exception {
//		ContractorFlagETL spy = PowerMockito.spy(new ContractorFlagETL(flagEtlDAO));
//		PowerMockito.doReturn(new HashSet<FlagCriteriaContractor>()).when(spy, "performFlaggingForNonEMR", anyObject(),
//				anyMap(), anyObject());
//
//		when(flagCriteria.getAuditType()).thenReturn(null);
//		when(flagCriteria.getOshaType()).thenReturn(null);
//		when(auditQuestion.getId()).thenReturn(1);
//		when(flagCriteria.getQuestion()).thenReturn(auditQuestion);
////		when(auditQuestion.getAuditType()).thenReturn(auditType);
//
//		setCategoryApplicable(spy);
//
//		Set<FlagCriteriaContractor> results = Whitebox.invokeMethod(spy, "executeFlagCriteriaCalculation",
//				flagCriteria, contractor, answerMap);
//		assertTrue(results.isEmpty());
//		PowerMockito.verifyPrivate(spy, times(1))
//				.invoke("performFlaggingForNonEMR", anyObject(), anyMap(), anyObject());
//
//	}
//
//	@Test
//	public void testExecuteFlagCriteriaCalculation_OSHA_TYPE() throws Exception {
//		ContractorFlagETL spy = PowerMockito.spy(new ContractorFlagETL(flagEtlDAO));
//		PowerMockito.doNothing().when(spy, "performOshaFlagCalculations", anyObject(), anySet(), anyObject());
//
//		when(flagCriteria.getOshaType()).thenReturn(OshaType.OSHA);
//
//		Set<FlagCriteriaContractor> results = Whitebox.invokeMethod(spy, "executeFlagCriteriaCalculation",
//				flagCriteria, contractor, answerMap);
//		assertTrue(results.isEmpty());
//		PowerMockito.verifyPrivate(spy, times(1)).invoke("performOshaFlagCalculations", anyObject(), anySet(),
//				anyObject());
//	}
//
	@Test
	public void testGenerateFlaggableData_No_Annual_Updates() throws Exception {
		Map<MultiYearScope, ContractorAudit> completeAnnualUpdates = new HashMap<MultiYearScope, ContractorAudit>();
		completeAnnualUpdates.put(MultiYearScope.LastYearOnly, null);
//		doReturn(completeAnnualUpdates).when(contractor).getCompleteAnnualUpdates();
		when(flagCriteria.getMultiYearScope()).thenReturn(MultiYearScope.LastYearOnly);

		FlagCriteriaContractor result = Whitebox.invokeMethod(contractorFlagETL, "generateFlaggableData", flagCriteria,
				contractor, false);
		assertNull(result);
	}

//	@Test
//	public void testGenerateFlaggableData_No_ApplicableCategories() throws Exception {
//		ContractorFlagETL spy = PowerMockito.spy(new ContractorFlagETL(flagEtlDAO));
//		PowerMockito.doReturn(false).when(spy, "checkForApplicableCategory", anyObject(), anyObject(), anyBoolean());
//
//		Map<MultiYearScope, ContractorAudit> completeAnnualUpdates = new HashMap<MultiYearScope, ContractorAudit>();
//		completeAnnualUpdates.put(MultiYearScope.LastYearOnly, audit);
////		doReturn(completeAnnualUpdates).when(contractor).getCompleteAnnualUpdates();
//		when(flagCriteria.getMultiYearScope()).thenReturn(MultiYearScope.LastYearOnly);
//
//		FlagCriteriaContractor result = Whitebox.invokeMethod(spy, "generateFlaggableData", flagCriteria, contractor,
//				false);
//		assertNull(result);
//		PowerMockito.verifyPrivate(spy, times(1)).invoke("checkForApplicableCategory", anyObject(), anyObject(),
//				anyBoolean());
//	}
//
//	@Test
//	public void testGenerateFlaggableData() throws Exception {
//		ContractorFlagETL spy = PowerMockito.spy(new ContractorFlagETL(flagEtlDAO));
//		PowerMockito.doReturn(true).when(spy, "checkForApplicableCategory", anyObject(), anyObject(), anyBoolean());
//
//		Map<MultiYearScope, ContractorAudit> completeAnnualUpdates = new HashMap<MultiYearScope, ContractorAudit>();
//		completeAnnualUpdates.put(MultiYearScope.LastYearOnly, audit);
////		doReturn(completeAnnualUpdates).when(contractor).getCompleteAnnualUpdates();
//		when(flagCriteria.getMultiYearScope()).thenReturn(MultiYearScope.LastYearOnly);
//		when(auditQuestion.getId()).thenReturn(29);
//		when(flagCriteria.getQuestion()).thenReturn(auditQuestion);
//		when(auditData.getQuestion()).thenReturn(auditQuestion);
//		List<AuditData> auditDataList = Arrays.asList(auditData);
//		when(audit.getData()).thenReturn(auditDataList);
//		when(audit.getAuditFor()).thenReturn("2012");
//
//		FlagCriteriaContractor result = Whitebox.invokeMethod(spy, "generateFlaggableData", flagCriteria, contractor,
//				false);
//		assertNotNull(result);
//		assertEquals("", result.getAnswer());
//		assertEquals("for Year: 2012", result.getAnswer2());
//		PowerMockito.verifyPrivate(spy, times(1)).invoke("checkForApplicableCategory", anyObject(), anyObject(),
//				anyBoolean());
//	}

	@Test
	public void testCheckForApplicableCategory_Not_Apply_Category() throws Exception {
		Boolean result = Whitebox.invokeMethod(contractorFlagETL, "checkForApplicableCategory", flagCriteria, audit,
				false);
		assertFalse(result);
	}

	@Test
	public void testCheckForApplicableCategory_Null_Question_Category() throws Exception {
		when(auditQuestion.getCategory()).thenReturn(null);
		when(flagCriteria.getQuestion()).thenReturn(auditQuestion);

		Boolean result = Whitebox.invokeMethod(contractorFlagETL, "checkForApplicableCategory", flagCriteria, audit,
				true);
		assertFalse(result);
	}

	@Test
	public void testCheckForApplicableCategory_Category_Not_Applicable() throws Exception {
		when(auditQuestion.getCategory()).thenReturn(auditCategory);
		when(flagCriteria.getQuestion()).thenReturn(auditQuestion);
//		doReturn(false).when(audit).isCategoryApplicable(anyInt());

		Boolean result = Whitebox.invokeMethod(contractorFlagETL, "checkForApplicableCategory", flagCriteria, audit,
				true);
		assertFalse(result);
	}

	@Test
	public void testCheckForApplicableCategory() throws Exception {
		when(auditQuestion.getCategory()).thenReturn(auditCategory);
		when(flagCriteria.getQuestion()).thenReturn(auditQuestion);

        List<AuditCatData> auditCatDatas = new ArrayList<>();
        AuditCatData auditCatData = new AuditCatData();
        auditCatData.setApplies(true);
        auditCatData.setCategory(auditCategory);
        auditCatDatas.add(auditCatData);

        when(audit.getCategories()).thenReturn(auditCatDatas);

		Boolean result = Whitebox.invokeMethod(contractorFlagETL, "checkForApplicableCategory", flagCriteria, audit,
				true);
		assertTrue(result);
	}

	@Test
	public void testGetApplicableCategories_Null() throws Exception {
		Set<AuditCategory> results = Whitebox.invokeMethod(contractorFlagETL, "getApplicableCategories",
				(AuditQuestion) null, contractor);

		assertNotNull(results);
		assertTrue(results.isEmpty());

		results = Whitebox.invokeMethod(contractorFlagETL, "getApplicableCategories", auditQuestion,
				(ContractorAccount) null);

		assertNotNull(results);
		assertTrue(results.isEmpty());

        when(auditQuestion.getCategory()).thenReturn(auditCategory);
        when(auditCategory.getAuditType()).thenReturn(null);

		results = Whitebox.invokeMethod(contractorFlagETL, "getApplicableCategories", auditQuestion, contractor);

		assertNotNull(results);
		assertTrue(results.isEmpty());
	}

	@Test
	public void testGetApplicableCategories_NoMatch() throws Exception {
		AuditType auditTypeOnContractor = EntityFactory.makeAuditType();
		ContractorAudit audit = EntityFactory.makeContractorAudit(auditTypeOnContractor, contractor);

		AuditType auditTypeOnQuestion = EntityFactory.makeAuditType();

		List<ContractorAudit> audits = new ArrayList<ContractorAudit>();
		audits.add(audit);

//		when(auditQuestion.getAuditType()).thenReturn(auditTypeOnQuestion);
		when(contractor.getAudits()).thenReturn(audits);

		Set<AuditCategory> results = Whitebox.invokeMethod(contractorFlagETL, "getApplicableCategories",
				(AuditQuestion) null, contractor);

		assertNotNull(results);
		assertTrue(results.isEmpty());
	}

	@Test
	public void testGetApplicableCategories_Match() throws Exception {
		AuditType auditType = EntityFactory.makeAuditType();

		List<ContractorAudit> audits = new ArrayList<>();
		audits.add(audit);

        List<AuditCatData> auditCatDatas = new ArrayList<>();
        AuditCatData auditCatData = new AuditCatData();
        auditCatData.setApplies(true);
        auditCatData.setCategory(auditCategory);
        auditCatDatas.add(auditCatData);

        when(audit.getCategories()).thenReturn(auditCatDatas);

		when(audit.getAuditType()).thenReturn(auditType);
        when(auditQuestion.getCategory()).thenReturn(auditCategory);
        when(auditCategory.getAuditType()).thenReturn(auditType);
		when(contractor.getAudits()).thenReturn(audits);

		Set<AuditCategory> results = Whitebox.invokeMethod(contractorFlagETL, "getApplicableCategories", auditQuestion,
				contractor);

		assertNotNull(results);
		assertFalse(results.isEmpty());
	}

//	private void setCategoryApplicable(ContractorFlagETL spy) throws Exception {
//		Set<AuditCategory> categories = new HashSet<AuditCategory>();
//		categories.add(auditCategory);
//
//		when(auditQuestion.getCategory()).thenReturn(auditCategory);
//		PowerMockito.doReturn(categories).when(spy, "getApplicableCategories", auditQuestion, contractor);
//	}
//
	@Test
	public void testGetApplicableCategories_MultipleAudits() throws Exception {
		when(auditQuestion.getCategory()).thenReturn(auditCategory);
		when(auditCategory.getAuditType()).thenReturn(EntityFactory.makeAuditType(AuditType.ANNUALADDENDUM));
		when(auditCategory.getId()).thenReturn(2);
        when(auditCategory2.getId()).thenReturn(3);

		when(audit1.getAuditType()).thenReturn(EntityFactory.makeAuditType(AuditType.ANNUALADDENDUM));
		when(audit2.getAuditType()).thenReturn(EntityFactory.makeAuditType(AuditType.PQF));
		when(audit3.getAuditType()).thenReturn(EntityFactory.makeAuditType(AuditType.ANNUALADDENDUM));

		List<ContractorAudit> list = new ArrayList<>();
		list.add(audit1);
		list.add(audit2);
		list.add(audit3);
		when(contractor.getAudits()).thenReturn(list);

        List<AuditCatData> auditCatDatas = new ArrayList<>();
        AuditCatData auditCatData = new AuditCatData();
        auditCatData.setApplies(true);
        auditCatData.setCategory(auditCategory);
        auditCatDatas.add(auditCatData);

		when(audit1.getCategories()).thenReturn(auditCatDatas);
		when(audit2.getCategories()).thenReturn(auditCatDatas);

        List<AuditCatData> auditCatDatas2 = new ArrayList<>();
        AuditCatData auditCatData2 = new AuditCatData();
        auditCatData2.setApplies(true);
        auditCatData2.setCategory(auditCategory2);
        auditCatDatas2.add(auditCatData2);

        when(audit3.getCategories()).thenReturn(auditCatDatas2);

		Set<AuditCategory> applicableCats = Whitebox.invokeMethod(contractorFlagETL, "getApplicableCategories",
				auditQuestion, contractor);
		assertTrue(applicableCats.size() == 2);
	}
}