package com.picsauditing.PICS;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PicsTestUtil;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.FlagCriteriaContractor;
import com.picsauditing.jpa.entities.TranslatableString;


public class ContractorFlagETLTest {
	private ContractorFlagETL contractorFlagETL;
	private PicsTestUtil picsTestUtil = new PicsTestUtil();
	
	@Mock private EntityManager em;
	@Mock private FlagCriteria flagCriteria;
	@Mock private AuditQuestion auditQuestion;
	@Mock private AuditType auditType;
	@Mock private ContractorAccount contractor;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		contractorFlagETL = new ContractorFlagETL();
		picsTestUtil.autowireEMInjectedDAOs(contractorFlagETL, em);
	}

	@Test
	public void testCheckForMissingAnswer_NotFlaggableWhenMissing() throws Exception {
		when(flagCriteria.isFlaggableWhenMissing()).thenReturn(false);
		FlagCriteriaContractor result = 
				Whitebox.invokeMethod(contractorFlagETL, "checkForMissingAnswer", flagCriteria, contractor);
		assertNull(result);
	}

	@Test
	public void testCheckForMissingAnswer_IsFlaggableWhenMissing() throws Exception {
		final int FLAG_CRITERIA_ID = 1;
		final int CONTRACTOR_ID = 2;
		when(flagCriteria.isFlaggableWhenMissing()).thenReturn(true);
		when(flagCriteria.getId()).thenReturn(FLAG_CRITERIA_ID);
		when(contractor.getId()).thenReturn(CONTRACTOR_ID);
		FlagCriteriaContractor result = 
				Whitebox.invokeMethod(contractorFlagETL, "checkForMissingAnswer", flagCriteria, contractor);
		assertNotNull(result);
		assertNull(result.getAnswer2());
		assertNull(result.getAnswer());
		assertEquals(FLAG_CRITERIA_ID, result.getCriteria().getId());
		assertEquals(CONTRACTOR_ID, result.getContractor().getId());
	}

	private Set<FlagCriteria> mockFlagCriteriaQuestionsNotAnnualAddendumNoExcess(int numberToCreate) {
		TranslatableString translatableString = mock(TranslatableString.class);
		when(translatableString.toString()).thenReturn("test description");
		Set<FlagCriteria> flagCriteriaSet = new HashSet<FlagCriteria>();
		int questionId = 1;
		for(int i = 0; i < numberToCreate; i++, questionId++) {
			AuditType auditType = mock(AuditType.class);
			when(auditType.isAnnualAddendum()).thenReturn(Boolean.FALSE);
			AuditQuestion auditQuestion = mock(AuditQuestion.class);
			when(auditQuestion.getId()).thenReturn(questionId);
			when(auditQuestion.getAuditType()).thenReturn(auditType);
			FlagCriteria flagCriteria = mock(FlagCriteria.class);
			when(flagCriteria.getQuestion()).thenReturn(auditQuestion);
			when(flagCriteria.getCategory()).thenReturn("test category");
			when(flagCriteria.getDescription()).thenReturn(translatableString);
			when(flagCriteria.includeExcess()).thenReturn(null);
			
			flagCriteriaSet.add(flagCriteria);
		}
		return flagCriteriaSet;
	}
	
	@Test
	public void testGetFlaggableAuditQuestionIds_AuditTypeIsNotAnnualAddendumNoExcess() throws Exception {
		Set<FlagCriteria> distinctFlagCriteria = mockFlagCriteriaQuestionsNotAnnualAddendumNoExcess(3);
		
		Set<Integer> criteriaQuestionSet = Whitebox.invokeMethod(contractorFlagETL, "getFlaggableAuditQuestionIds", distinctFlagCriteria);
		
		for (FlagCriteria flagCriteria: distinctFlagCriteria) {
			int questionId = flagCriteria.getQuestion().getId();
			assertTrue(criteriaQuestionSet.contains(questionId));
		}
	}

	@Test
	public void testGetFlaggableAuditQuestionIds_AuditTypeIsAnnualAddendum() throws Exception {
		when(auditType.isAnnualAddendum()).thenReturn(Boolean.TRUE);
		when(flagCriteria.getQuestion()).thenReturn(auditQuestion);
		when(auditQuestion.getAuditType()).thenReturn(auditType);
		Set<FlagCriteria> distinctFlagCriteria = new HashSet<FlagCriteria>();
		distinctFlagCriteria.add(flagCriteria);
		
		Set<Integer> criteriaQuestionSet = Whitebox.invokeMethod(contractorFlagETL, "getFlaggableAuditQuestionIds", distinctFlagCriteria);
		
		assertTrue(criteriaQuestionSet.isEmpty());
	}

	@Test
	public void testGetFlaggableAuditQuestionIds_QuestionHasNullAuditType() throws Exception {
		when(flagCriteria.getQuestion()).thenReturn(auditQuestion);
		when(auditQuestion.getAuditType()).thenReturn(null);
		Set<FlagCriteria> distinctFlagCriteria = new HashSet<FlagCriteria>();
		distinctFlagCriteria.add(flagCriteria);
		
		Set<Integer> criteriaQuestionSet = Whitebox.invokeMethod(contractorFlagETL, "getFlaggableAuditQuestionIds", distinctFlagCriteria);
		
		assertTrue(criteriaQuestionSet.isEmpty());
	}
	
	@Test
	public void testGetFlaggableAuditQuestionIds_NoFlagCriteria() throws Exception {
		Set<FlagCriteria> distinctFlagCriteria = new HashSet<FlagCriteria>();
		
		Set<Integer> criteriaQuestionSet = Whitebox.invokeMethod(contractorFlagETL, "getFlaggableAuditQuestionIds", distinctFlagCriteria);
		
		assertTrue(criteriaQuestionSet.isEmpty());
	}
	
	@Test
	public void testGetFlaggableAuditQuestionIds_CriteriaHasNullQuestion() throws Exception {
		Set<FlagCriteria> distinctFlagCriteria = new HashSet<FlagCriteria>();
		distinctFlagCriteria.add(flagCriteria);
		
		Set<Integer> criteriaQuestionSet = Whitebox.invokeMethod(contractorFlagETL, "getFlaggableAuditQuestionIds", distinctFlagCriteria);
		
		assertTrue(criteriaQuestionSet.isEmpty());
	}
}
