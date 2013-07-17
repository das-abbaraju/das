package com.picsauditing.actions.audits;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import com.picsauditing.jpa.entities.*;
import com.picsauditing.models.audits.AuditEditModel;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.PicsTranslationTest;
import com.picsauditing.access.Permissions;
import com.picsauditing.auditBuilder.AuditCategoryRuleCache;
import com.picsauditing.auditBuilder.AuditPercentCalculator;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.util.AnswerMap;
import com.picsauditing.util.PicsDateFormat;

@SuppressWarnings("deprecation")
public class AuditDataSaveTest extends PicsTranslationTest {

	private AuditDataSave auditDataSave;
	private User user;

	@Mock
	private Permissions permissions;
	@Mock
	private ContractorAuditDAO auditDao;
	@Mock
	private ContractorAccountDAO contractorAccountDao;
	@Mock
	private AuditDataDAO auditDataDao;
	@Mock
	private AuditCategoryDataDAO catDataDao;
	@Mock
	private AuditDecisionTableDAO auditRuleDAO;
	@Mock
	private AuditQuestionDAO questionDao;
	@Mock
	private AuditCategoryRuleCache categoryRuleCache;
	@Mock
	private AuditPercentCalculator auditPercentCalculatior;

	private ContractorAccount contractor;
	private AuditData auditData;
	private ContractorAudit audit;
    private AuditEditModel auditEditModel;

	private AnswerMap answerMap;
	private AuditCatData catData;

	@AfterClass
	public static void classTearDown() {
		PicsTranslationTest.tearDownTranslationService();
		PicsTestUtil.resetSpringUtilsBeans();
	}

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		super.resetTranslationService();

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

        auditEditModel = new AuditEditModel();

		PicsTestUtil.forceSetPrivateField(auditDataSave, "auditCategoryRuleCache", categoryRuleCache);
		PicsTestUtil.forceSetPrivateField(auditDataSave, "auditPercentCalculator", auditPercentCalculatior);
		PicsTestUtil.forceSetPrivateField(auditDataSave, "conAudit", audit);
        PicsTestUtil.forceSetPrivateField(auditDataSave, "auditEditModel", auditEditModel);


		when(auditDataDao.findAnswers(anyInt(), (Collection<Integer>) Matchers.anyObject())).thenReturn(answerMap);
		when(auditDao.find(anyInt())).thenReturn(audit);
		when(questionDao.find(anyInt())).thenReturn(auditData.getQuestion());
		when(catDataDao.findAuditCatData(Matchers.anyInt(), Matchers.anyInt())).thenReturn(catData);
		when(auditRuleDAO.findCategoryRulesByQuestion(Matchers.anyInt()))
				.thenReturn(new ArrayList<AuditCategoryRule>());
		doNothing().when(auditPercentCalculatior).updatePercentageCompleted(catData);
	}

	@Test
	public void testCheckUniqueCode_ExpiresMonth() throws Exception {
		Date date;
		SimpleDateFormat format = new SimpleDateFormat(PicsDateFormat.Iso);

		auditData.setAnswer("2025-01-31");

		setupCheckUniqueCode("exipireMonths12");
		Whitebox.invokeMethod(auditDataSave, "checkUniqueCode", audit);
		assertNotNull(audit.getExpiresDate());
		date = audit.getExpiresDate();
		assertEquals("2026-01-31", format.format(date));
	}

	@Ignore
	public void testESignatureVerify() throws Exception {
		AuditData auditData = EntityFactory.makeAuditData(null);
		auditData.setComment(null);
		AuditData databaseData = EntityFactory.makeAuditData("John Smith / Supervisor");
		databaseData.setId(auditData.getId());
		AuditQuestion question = EntityFactory.makeAuditQuestion();
		question.setQuestionType("ESignature");
		auditData.setQuestion(question);
		databaseData.setQuestion(question);

		PicsTestUtil.forceSetPrivateField(auditDataSave, "mode", "Verify");
		Boolean result = Whitebox.invokeMethod(auditDataSave, "answerFormatValid", auditData, databaseData);
		assertTrue(result);
		assertTrue(auditData.getAnswer().equals("John Smith / Supervisor"));
	}

	@Test
	public void testESignatureReset() throws Exception {
		AuditData auditData = EntityFactory.makeAuditData(null);
		auditData.setComment(null);
		AuditQuestion question = EntityFactory.makeAuditQuestion();
		question.setQuestionType("ESignature");
		auditData.setQuestion(question);

		Boolean result = Whitebox.invokeMethod(auditDataSave, "answerFormatValid", auditData, auditData);
		assertTrue(result);
		assertTrue(auditData.getComment().equals(""));
		assertTrue(auditData.getAnswer().equals(""));

	}

	@Test
	public void testCheckUniqueCode_PolicyEffectiveDate() throws Exception {
		Date date;
		SimpleDateFormat format = new SimpleDateFormat(PicsDateFormat.Iso);

		auditData.setAnswer("2025-01-31");

		setupCheckUniqueCode("policyEffectiveDate");
		Whitebox.invokeMethod(auditDataSave, "checkUniqueCode", audit);
		assertNotNull(audit.getEffectiveDate());
		date = audit.getEffectiveDate();
		assertEquals("2025-01-31", format.format(date));
	}

	@Test
	public void testUniqueCode_ExpirationDate() throws Exception {
		Date date;
		SimpleDateFormat format = new SimpleDateFormat(PicsDateFormat.Iso);

		auditData.setAnswer("2005-01-31");

		setupCheckUniqueCode("expirationDate");
		Whitebox.invokeMethod(auditDataSave, "checkUniqueCode", audit);
		assertNotNull(audit.getExpiresDate());
		date = audit.getExpiresDate();
		assertEquals("2005-01-31", format.format(date));

	}

	@Test
	public void testUniqueCode_EffectiveDate() throws Exception {
		Date date;
		SimpleDateFormat format = new SimpleDateFormat(PicsDateFormat.Iso);

		Calendar effectiveDate = Calendar.getInstance();
		effectiveDate.add(Calendar.YEAR, 10);
		auditData.setAnswer(format.format(effectiveDate.getTime()));

		setupCheckUniqueCode("effectiveDate");
		Whitebox.invokeMethod(auditDataSave, "checkUniqueCode", audit);
		assertNull(audit.getEffectiveDate());
	}

	private void setupCheckUniqueCode(String code) {
		audit.setCreationDate(new Date());
		audit.setExpiresDate(null);
		audit.setEffectiveDate(null);
		auditData.getQuestion().setUniqueCode(code);
	}

	@Test
	public void testExecute_Verify() throws Exception {
		AuditData oldData = EntityFactory.makeAuditData("No");
		oldData.setId(auditData.getId());

		AuditType annual = EntityFactory.makeAuditType(AuditType.ANNUALADDENDUM);
		audit.setAuditType(annual);
		oldData.setAudit(auditData.getAudit());
		oldData.setQuestion(auditData.getQuestion());

		when(questionDao.find(auditData.getQuestion().getId())).thenReturn(auditData.getQuestion());

        when(permissions.seesAllContractors()).thenReturn(true);

		when(auditDataDao.find(anyInt())).thenReturn(oldData);
		PicsTestUtil.forceSetPrivateField(auditDataSave, "button", "verify");
		assertEquals("success", auditDataSave.execute());
		assertEquals("Yes", oldData.getAnswer());
		assertNotNull(oldData.getDateVerified());
		assertEquals(user.getId(), oldData.getAuditor().getId());
	}

	@Test
	public void testExecute_ContractorEditingSiteSpecificWhileStatusIsComplete() throws Exception {
		AuditData oldData = EntityFactory.makeAuditData("No");
		oldData.setId(auditData.getId());

		AuditType siteSpecific = EntityFactory.makeAuditType();
        siteSpecific.setClassType(AuditTypeClass.PQF);
        siteSpecific.setCanContractorEdit(true);
		audit.setAuditType(siteSpecific);

        audit.setOperators(
                Arrays.asList(
                        ContractorAuditOperator.builder()
                                .status(AuditStatus.Complete)
                                .visible()
                                .build()
                )
        );

		oldData.setAudit(auditData.getAudit());
		oldData.setQuestion(auditData.getQuestion());

		when(questionDao.find(auditData.getQuestion().getId())).thenReturn(auditData.getQuestion());
        when(permissions.isContractor()).thenReturn(true);
		when(auditDataDao.find(anyInt())).thenReturn(oldData);

		assertEquals("success", auditDataSave.execute());
		assertEquals("No", oldData.getAnswer());
	}
    @Test
    public void testExecute_ContractorEditingSiteSpecificWhileStatusIsPending() throws Exception {
        AuditData oldData = EntityFactory.makeAuditData("No");
        oldData.setId(auditData.getId());

        AuditType siteSpecific = EntityFactory.makeAuditType();
        siteSpecific.setClassType(AuditTypeClass.PQF);
        siteSpecific.setCanContractorEdit(true);
        audit.setAuditType(siteSpecific);

        audit.setOperators(
                Arrays.asList(
                        ContractorAuditOperator.builder()
                                .status(AuditStatus.Pending)
                                .visible()
                                .build()
                )
        );

        oldData.setAudit(auditData.getAudit());
        oldData.setQuestion(auditData.getQuestion());

        when(questionDao.find(auditData.getQuestion().getId())).thenReturn(auditData.getQuestion());
        when(permissions.isContractor()).thenReturn(true);
        when(auditDataDao.find(anyInt())).thenReturn(oldData);

        assertEquals("success", auditDataSave.execute());
        assertEquals("Yes", oldData.getAnswer());
    }

    @Test
	public void testDates() throws Exception {
		Boolean result = null;
		AuditQuestion question = EntityFactory.makeAuditQuestion();
		AuditData data = new AuditData();
		data.setQuestion(question);

		// null answer
		result = Whitebox.invokeMethod(auditDataSave, "isDateValid", data);
		assertTrue(result);

		// empty answer
		data.setAnswer("");
		result = Whitebox.invokeMethod(auditDataSave, "isDateValid", data);
		assertTrue(result);

		// valid policy date answer
		question.setUniqueCode("policyEffectiveDate");
		data.setAnswer("2001-01-01");
		result = Whitebox.invokeMethod(auditDataSave, "isDateValid", data);
		assertTrue(result);

		// invalid policy date answer
		question.setUniqueCode("policyEffectiveDate");
		data.setAnswer("1999-12-31");
		result = Whitebox.invokeMethod(auditDataSave, "isDateValid", data);
		assertFalse(result);
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

	// @Test
	public void testExecute_SimpleAnswer() throws Exception {
		assertEquals("success", auditDataSave.execute());
	}

	@Test
	public void testExecute_BadNumberAnswer() throws Exception {
		auditData.getQuestion().setQuestionType("Number");
		auditData.setAnswer("12-345"); // the '-' in the middle will cause a
										// BigDecimal exception

		when(translationService.getText("Audit.message.InvalidFormat", Locale.ENGLISH, (Object[]) null)).thenReturn(
				"Unit Test String");
		assertEquals("success", auditDataSave.execute());
		assertEquals(true, auditDataSave.getActionErrors().size() > 0);
	}

	// Test case to cover this issue: PICS-8585.
	@Test
	public void testExecute_NewMoneyAnswer() throws Exception {
		PicsTestUtil.setSpringUtilsBeans(new HashMap<String, Object>());
		AuditQuestion questionInTheDatabase = auditData.getQuestion();
		questionInTheDatabase.setQuestionType("Money");

		// This is to simulate not having a fully hydrated audit question
		// because
		// it hasn't been pulled up from the database yet.
		AuditQuestion questionInstantiatedFromPostParameters = new AuditQuestion();
		questionInstantiatedFromPostParameters.setId(questionInTheDatabase.getId());
		auditData.setQuestion(questionInstantiatedFromPostParameters);
		auditData.setId(0);
		auditData.setAnswer("2000000");

		when(questionDao.find(questionInTheDatabase.getId())).thenReturn(questionInTheDatabase);
		when(auditDataDao.find(auditData.getId())).thenReturn(auditData);

		assertEquals("success", auditDataSave.execute());
		assertEquals(0, auditDataSave.getActionErrors().size());
		assertEquals("2,000,000", auditData.getAnswer());
	}

	@Test
	public void testExecute_SetAnswerToDateOrRecordError_BadDateRecordsErrorReturnsFalse() throws Exception {

		Boolean returnValue = Whitebox.invokeMethod(auditDataSave, "setAnswerToDateOrRecordError", auditData,
				"BAD_DATE");

		assertFalse(returnValue);
		verify(translationService).hasKey("AuditData.error.InvalidDate", Locale.ENGLISH);
	}

	@Test
	public void testExecute_SetAnswerToDateOrRecordError_DateTooFarInFutureRecordsErrorReturnsFalse() throws Exception {

		// this seems silly, but it is the behavior of the CUT
		Boolean returnValue = Whitebox.invokeMethod(auditDataSave, "setAnswerToDateOrRecordError", auditData,
				"10000-01-01");

		assertFalse(returnValue);
		verify(translationService).hasKey("AuditData.error.DateOutOfRange", Locale.ENGLISH);
	}

	@Test
	public void testExecute_SetAnswerToDateOrRecordError_DateGoodSetAnswer() throws Exception {
		SimpleDateFormat americanFormat = new SimpleDateFormat(PicsDateFormat.American);
		Date now = new Date();
		String answer = americanFormat.format(now);
		americanFormat.applyPattern(PicsDateFormat.Iso);
		String expected = americanFormat.format(now);

		// this seems silly, but it is the behavior of the CUT
		Boolean returnValue = Whitebox.invokeMethod(auditDataSave, "setAnswerToDateOrRecordError", auditData, answer);

		assertTrue(returnValue);
		assertThat(expected, is(equalTo(auditData.getAnswer())));
	}

	@Test
	public void testIsInvalidNegativeNumber() throws Exception {
		AuditQuestion question = EntityFactory.makeAuditQuestion();
		question.setId(AuditQuestion.EMR);
		Boolean returnValue = Whitebox.invokeMethod(auditDataSave, "isInvalidNegativeNumber", new BigDecimal(-1),
				question);
		assertTrue(returnValue);

		returnValue = Whitebox.invokeMethod(auditDataSave, "isInvalidNegativeNumber", new BigDecimal(1), question);
		assertFalse(returnValue);

		question.setId(1);
		returnValue = Whitebox.invokeMethod(auditDataSave, "isInvalidNegativeNumber", new BigDecimal(-1), question);
		assertFalse(returnValue);
	}

	@Test
	public void testIsInvalidPercent() throws Exception {
		AuditQuestion question = EntityFactory.makeAuditQuestion();
		question.setQuestionType("Percent");
		Boolean returnValue = Whitebox.invokeMethod(auditDataSave, "isInvalidPercent", new BigDecimal(-1), question);
		assertTrue(returnValue);

		returnValue = Whitebox.invokeMethod(auditDataSave, "isInvalidPercent", new BigDecimal(0), question);
		assertFalse(returnValue);

		returnValue = Whitebox.invokeMethod(auditDataSave, "isInvalidPercent", new BigDecimal(0.1), question);
		assertFalse(returnValue);

		returnValue = Whitebox.invokeMethod(auditDataSave, "isInvalidPercent", new BigDecimal(100), question);
		assertFalse(returnValue);

		returnValue = Whitebox.invokeMethod(auditDataSave, "isInvalidPercent", new BigDecimal(100.01), question);
		assertTrue(returnValue);
	}

	@Test
	public void testEmptyTagit() throws Exception {
		AuditQuestion question = EntityFactory.makeAuditQuestion();
		question.setQuestionType("Tagit");
		AuditData data = EntityFactory.makeAuditData("[]", question);

		Whitebox.invokeMethod(auditDataSave, "answerFormatValid", data, data);
		assertTrue("".equals(data.getAnswer()));

		data.setAnswer("[test, 1, 2, 3]");
		Whitebox.invokeMethod(auditDataSave, "answerFormatValid", data, data);
		assertTrue("[test, 1, 2, 3]".equals(data.getAnswer()));
	}
}
