package com.picsauditing.actions.audits;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.access.Permissions;
import com.picsauditing.auditBuilder.AuditCategoryRuleCache;
import com.picsauditing.auditBuilder.AuditPercentCalculator;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeRule;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.search.Database;
import com.picsauditing.util.AnswerMap;

public class AuditDataSaveTest {
	private AuditDataSave auditDataSave;
	private User user;

	@Mock private Permissions permissions;
	@Mock private ContractorAuditDAO auditDao;
	@Mock private ContractorAccountDAO contractorAccountDao;
	@Mock private AuditDataDAO auditDataDao;
	@Mock private AuditCategoryDataDAO catDataDao;
	@Mock private AuditDecisionTableDAO auditRuleDAO;
	@Mock private AuditQuestionDAO questionDao;
	@Mock private AuditCategoryRuleCache categoryRuleCache;
	@Mock private AuditPercentCalculator auditPercentCalculatior;
	@Mock private I18nCache i18nCache;
	@Mock private Database databaseForTesting;
	
	private ContractorAccount contractor;
	private AuditData auditData;
	private ContractorAudit audit;
	private AnswerMap answerMap;
	private AuditCatData catData;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", databaseForTesting);
		
		auditDataSave = new AuditDataSave();
		PicsTestUtil.autowireDAOsFromDeclaredMocks(auditDataSave, this);

		// make some entities
		user = EntityFactory.makeUser();
		when(permissions.getUserId()).thenReturn(user.getId());
		when(permissions.getLocale()).thenReturn(new Locale("en"));
		PicsTestUtil.forceSetPrivateField(auditDataSave, "permissions", permissions);
		PicsTestUtil.forceSetPrivateField(auditDataSave, "user", user);

		contractor = EntityFactory.makeContractor();

		audit = EntityFactory.makeContractorAudit(1, contractor);

		auditData = EntityFactory.makeAuditData("Yes");
		auditData.setAudit(audit);
		auditData.getQuestion().setAuditCategoryRules(new ArrayList<AuditCategoryRule>());
		auditData.getQuestion().setAuditTypeRules(new ArrayList<AuditTypeRule>());
		auditData.getQuestion()
				.setCategory(EntityFactory.addCategories(audit.getAuditType(), 104, "Test Category 104"));

		auditDataSave.setAuditData(auditData);
		auditDataSave.setContractor(contractor);

		catData = EntityFactory.makeAuditCatData();

		PicsTestUtil.forceSetPrivateField(auditDataSave, "auditCategoryRuleCache", categoryRuleCache);
		PicsTestUtil.forceSetPrivateField(auditDataSave, "auditPercentCalculator", auditPercentCalculatior);
		PicsTestUtil.forceSetPrivateField(auditDataSave, "conAudit", audit);
		PicsTestUtil.forceSetPrivateField(auditDataSave, "i18nCache", i18nCache);
		
		when(auditDataDao.findAnswers(anyInt(), (Collection<Integer>) Matchers.anyObject())).thenReturn(answerMap);
		when(auditDao.find(anyInt())).thenReturn(audit);
		when(questionDao.find(anyInt())).thenReturn(auditData.getQuestion());
		when(catDataDao.findAuditCatData(Matchers.anyInt(), Matchers.anyInt())).thenReturn(catData);
		when(auditRuleDAO.findCategoryRulesByQuestion(Matchers.anyInt()))
				.thenReturn(new ArrayList<AuditCategoryRule>());
		doNothing().when(auditPercentCalculatior).updatePercentageCompleted(catData);
	}

	@Test
	public void testCheckUniqueCode() throws Exception {
		Date date;
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");

		auditData.setAnswer("01/31/2025");

		setupCheckUniqueCode("exipireMonths12");
		auditDataSave.checkUniqueCode(audit);
		assertNotNull(audit.getExpiresDate());
		date = audit.getExpiresDate();
		assertEquals("01/31/2026", format.format(date));
	}

	private void setupCheckUniqueCode(String code) {
		audit.setExpiresDate(null);
		audit.setEffectiveDate(null);
		auditData.getQuestion().setUniqueCode(code);
	}

	@Test
	public void testExecute_NoIncidents_HSE_COHS_INCIDENT_QUESTION() throws Exception {
		AuditType annualType = EntityFactory.makeAuditType(AuditType.ANNUALADDENDUM);
		audit.setAuditType(annualType);

		AuditQuestion hseIncidentsQuestion = EntityFactory.makeAuditQuestion();
		AuditData[] safetyData = null;

		setUpIncidentQuestion(hseIncidentsQuestion, AuditDataSave.COHS_INCIDENT_QUESTION_ID);
		safetyData = new AuditData[AuditDataSave.COHS_INCIDENT_RELATED_QUESTION_IDS.length];
		setupSafetyDataAnswers(safetyData, AuditDataSave.COHS_INCIDENT_RELATED_QUESTION_IDS);
		when(questionDao.find(hseIncidentsQuestion.getId())).thenReturn(hseIncidentsQuestion);

		String strutsAction = auditDataSave.execute();
		
		assertEquals("success", strutsAction);
		for (int i = 0; i < AuditDataSave.COHS_INCIDENT_RELATED_QUESTION_IDS.length; i++) {
			assertEquals("0", safetyData[i].getAnswer());
		}
	}
	
	@Test
	public void testExecute_NoIncidents_OSHA_INCIDENT_QUESTION() throws Exception {
		AuditQuestion hseIncidentsQuestion = EntityFactory.makeAuditQuestion();
		AuditData[] safetyData = null;

		setUpIncidentQuestion(hseIncidentsQuestion, AuditDataSave.OSHA_INCIDENT_QUESTION_ID);
		safetyData = new AuditData[AuditDataSave.OSHA_INCIDENT_RELATED_QUESTION_IDS.length];
		setupSafetyDataAnswers(safetyData, AuditDataSave.OSHA_INCIDENT_RELATED_QUESTION_IDS);
		when(questionDao.find(hseIncidentsQuestion.getId())).thenReturn(hseIncidentsQuestion);

		assertEquals("success", auditDataSave.execute());
		for (int i = 0; i < AuditDataSave.OSHA_INCIDENT_RELATED_QUESTION_IDS.length; i++) {
			assertEquals("0", safetyData[i].getAnswer());
		}
	}

	private void setUpIncidentQuestion(AuditQuestion coshIncidentsQuestion, int questionID) {
		coshIncidentsQuestion.setId(questionID);
		auditData.setAnswer("No");
		auditData.setQuestion(coshIncidentsQuestion);
		auditData.getQuestion().setAuditCategoryRules(new ArrayList<AuditCategoryRule>());
		auditData.getQuestion().setAuditTypeRules(new ArrayList<AuditTypeRule>());
		auditData.getQuestion()
				.setCategory(EntityFactory.addCategories(audit.getAuditType(), 104, "Test Category 104"));
	}

	private void setupSafetyDataAnswers(AuditData[] safetyData, int[] questionIds) {
		for (int i = 0; i < questionIds.length; i++) {
			AuditData auditAnswer = EntityFactory.makeAuditData("6");
			auditAnswer.setId(questionIds[i]);
			safetyData[i] = auditAnswer;
			when(auditDataDao.findAnswerToQuestion(audit.getId(), safetyData[i].getId())).thenReturn(safetyData[i]);
		}
	}

	@Test
	public void testExecute_Verify() throws Exception {
		AuditData oldData = EntityFactory.makeAuditData("No");
		oldData.setId(auditData.getId());

		AuditType annual = EntityFactory.makeAuditType(AuditType.ANNUALADDENDUM);
		audit.setAuditType(annual);
		oldData.setAudit(auditData.getAudit());
		oldData.setQuestion(auditData.getQuestion());

		when(auditDataDao.find(anyInt())).thenReturn(oldData);
		PicsTestUtil.forceSetPrivateField(auditDataSave, "button", "verify");
		assertEquals("success", auditDataSave.execute());
		assertEquals("Yes", oldData.getAnswer());
		assertNotNull(oldData.getDateVerified());
		assertEquals(user.getId(), oldData.getAuditor().getId());
	}

	@Test
	public void testTrimWhitespaceLeadingZerosAndAllCommas() throws Exception {
		assertEquals("10.10", AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas("10.10"));
		assertEquals("10.10", AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas("  10.10"));
		assertEquals("10.10", AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas("10.10  "));
		assertEquals("10.10", AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas("010.10"));
		assertEquals("10.10", AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas("00010.10"));
		assertEquals("10.10", AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas(",010.10"));
		assertEquals("10.10", AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas("0,010.10"));
		assertEquals("0", AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas("0"));
		assertEquals("0", AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas("00"));
		assertEquals("0", AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas("000"));
		assertEquals(".0", AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas("000.0"));
		assertEquals(".00", AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas("000.00"));
		assertEquals(".01", AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas("0.01"));
		assertEquals("1", AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas("01"));
		assertEquals("1", AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas("001"));
		assertEquals("1.01", AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas("  01.01"));
		assertEquals("1.01", AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas("\t01.01"));
		assertEquals("ABC", AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas("ABC"));
		assertEquals("ABC", AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas("  ABC  "));
	}

	@Test
	public void testExecute_SimpleAnswer() throws Exception {
		assertEquals("success", auditDataSave.execute());
	}

	@Test
	public void testExecute_BadNumberAnswer() throws Exception {
		auditData.getQuestion().setQuestionType("Number");
		auditData.setAnswer("12-345"); // the '-' in the middle will cause a
										// BigDecimal exception

		when(i18nCache.getText("Audit.message.InvalidFormat", Locale.ENGLISH, (Object[]) null)).thenReturn("Unit Test String");
		assertEquals("success", auditDataSave.execute());
		assertEquals(true, auditDataSave.getActionErrors().size() > 0);
	}

	@Test
	public void testExecute_SetAnswerToDateOrRecordError_BadDateRecordsErrorReturnsFalse() throws Exception {

		Boolean returnValue = Whitebox.invokeMethod(auditDataSave, "setAnswerToDateOrRecordError", auditData, "BAD_DATE");
		
		assertFalse(returnValue);
		verify(i18nCache).hasKey("AuditData.error.InvalidDate", Locale.ENGLISH);
	}

	@Test
	public void testExecute_SetAnswerToDateOrRecordError_DateTooFarInFutureRecordsErrorReturnsFalse() throws Exception {

		// this seems silly, but it is the behavior of the CUT
		Boolean returnValue = Whitebox.invokeMethod(auditDataSave, "setAnswerToDateOrRecordError", auditData, "10000-01-01");
		
		assertFalse(returnValue);
		verify(i18nCache).hasKey("AuditData.error.DateOutOfRange", Locale.ENGLISH);
	}

	@Test
	public void testExecute_SetAnswerToDateOrRecordError_DateGoodSetAnswer() throws Exception {
		String testFormat = "dd-MM-yyyy";
		String dbFormat = "yyyy-MM-dd";
		SimpleDateFormat format = new SimpleDateFormat(testFormat);
		Date now = new Date();
		String answer = format.format(now); 
		format.applyPattern(dbFormat);
		String expected = format.format(now);

		// this seems silly, but it is the behavior of the CUT
		Boolean returnValue = Whitebox.invokeMethod(auditDataSave, "setAnswerToDateOrRecordError", auditData, answer);
		
		assertTrue(returnValue);
		assertThat(expected, is(equalTo(auditData.getAnswer())));
	}

}
