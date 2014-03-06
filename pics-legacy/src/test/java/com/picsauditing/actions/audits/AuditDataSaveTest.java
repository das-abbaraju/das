package com.picsauditing.actions.audits;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.PicsTranslationTest;
import com.picsauditing.access.Permissions;
import com.picsauditing.auditBuilder.AuditCategoryRuleCache;
import com.picsauditing.auditBuilder.AuditPercentCalculator;
import com.picsauditing.dao.*;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.models.audits.AuditEditModel;
import com.picsauditing.service.AuditDataService;
import com.picsauditing.service.ContractorAuditService;
import com.picsauditing.util.AnswerMap;
import com.picsauditing.util.PicsDateFormat;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

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
	@Mock
	private AuditDataService auditDataService;
	@Mock
	private ContractorAuditService contractorAuditService;
    @Mock
    private AuditData mockAuditData;

	private ContractorAccount contractor;
	private AuditData auditData;
	private ContractorAudit audit;
    private AuditEditModel auditEditModel;

	private AnswerMap answerMap;
	private AuditCatData catData;

	@AfterClass
	public static void classTearDown() {
		PicsTestUtil.resetSpringUtilsBeans();
	}

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);


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
        PicsTestUtil.forceSetPrivateField(auditDataSave, "auditDataService", auditDataService);
        PicsTestUtil.forceSetPrivateField(auditDataSave, "contractorAuditService", contractorAuditService);

		when(auditDataDao.findAnswers(anyInt(), (Collection<Integer>) Matchers.anyObject())).thenReturn(answerMap);
		when(contractorAuditService.findContractorAudit(anyInt())).thenReturn(audit);
		when(questionDao.find(anyInt())).thenReturn(auditData.getQuestion());
		when(auditDataService.findAuditQuestion(anyInt())).thenReturn(auditData.getQuestion());
		when(catDataDao.findAuditCatData(Matchers.anyInt(), Matchers.anyInt())).thenReturn(catData);
		when(auditRuleDAO.findCategoryRulesByQuestion(Matchers.anyInt()))
				.thenReturn(new ArrayList<AuditCategoryRule>());
		doNothing().when(auditPercentCalculatior).updatePercentageCompleted(catData);
	}

    @Test
    public void testGetRollbackStatus_AdminChangingPolicySubmitted() throws Exception {
        when(permissions.isAdmin()).thenReturn(true);
        AuditType auditType = EntityFactory.makeAuditType();
        auditType.setClassType(AuditTypeClass.Policy);
        audit.setAuditType(auditType);

        audit.setOperators(
                Arrays.asList(
                        ContractorAuditOperator.builder()
                                .status(AuditStatus.Submitted)
                                .visible()
                                .build()
                )
        );

        assertEquals(AuditStatus.Resubmitted, Whitebox.invokeMethod(auditDataSave, "getRollbackStatus", audit, audit.getOperators().get(0)));
    }

    @Test
    public void testGetRollbackStatus_AdminChangingPolicyComplete() throws Exception {
        when(permissions.isAdmin()).thenReturn(true);
        AuditType auditType = EntityFactory.makeAuditType();
        auditType.setClassType(AuditTypeClass.Policy);
        audit.setAuditType(auditType);

        audit.setOperators(
                Arrays.asList(
                        ContractorAuditOperator.builder()
                                .status(AuditStatus.Approved)
                                .visible()
                                .build()
                )
        );

        assertEquals(AuditStatus.Resubmitted, Whitebox.invokeMethod(auditDataSave, "getRollbackStatus", audit, audit.getOperators().get(0)));
    }

    @Test
    public void testGetRollbackStatus_AdminChangingPolicyApproved() throws Exception {
        when(permissions.isAdmin()).thenReturn(true);
        AuditType auditType = EntityFactory.makeAuditType();
        auditType.setClassType(AuditTypeClass.Policy);
        audit.setAuditType(auditType);

        audit.setOperators(
                Arrays.asList(
                        ContractorAuditOperator.builder()
                                .status(AuditStatus.Approved)
                                .visible()
                                .build()
                )
        );

        assertEquals(AuditStatus.Resubmitted, Whitebox.invokeMethod(auditDataSave, "getRollbackStatus", audit, audit.getOperators().get(0)));
    }

    @Test
    public void testGetRollbackStatus_PolicySubmitted() throws Exception {
        when(permissions.isAdmin()).thenReturn(false);
        AuditType auditType = EntityFactory.makeAuditType();
        auditType.setClassType(AuditTypeClass.Policy);
        audit.setAuditType(auditType);

        audit.setOperators(
                Arrays.asList(
                        ContractorAuditOperator.builder()
                                .status(AuditStatus.Submitted)
                                .visible()
                                .build()
                )
        );

        assertEquals(AuditStatus.Incomplete, Whitebox.invokeMethod(auditDataSave, "getRollbackStatus", audit, audit.getOperators().get(0)));
    }

    @Test
    public void testGetRollbackStatus_PolicyComplete() throws Exception {
        when(permissions.isAdmin()).thenReturn(true);
        AuditType auditType = EntityFactory.makeAuditType();
        auditType.setClassType(AuditTypeClass.Policy);
        audit.setAuditType(auditType);

        audit.setOperators(
                Arrays.asList(
                        ContractorAuditOperator.builder()
                                .status(AuditStatus.Complete)
                                .visible()
                                .build()
                )
        );

        assertEquals(AuditStatus.Resubmitted, Whitebox.invokeMethod(auditDataSave, "getRollbackStatus", audit, audit.getOperators().get(0)));
    }


    @Test
    public void testGetRollbackStatus_PolicyApproved() throws Exception {
        when(permissions.isAdmin()).thenReturn(true);
        AuditType auditType = EntityFactory.makeAuditType();
        auditType.setClassType(AuditTypeClass.Policy);
        audit.setAuditType(auditType);

        audit.setOperators(
                Arrays.asList(
                        ContractorAuditOperator.builder()
                                .status(AuditStatus.Approved)
                                .visible()
                                .build()
                )
        );

        assertEquals(AuditStatus.Resubmitted, Whitebox.invokeMethod(auditDataSave, "getRollbackStatus", audit, audit.getOperators().get(0)));
    }

    @Test
	public void testESignatureVerify() throws Exception {
		AuditData auditData = EntityFactory.makeAuditData("John Smith / Supervisor");
		auditData.setComment("123.123.123.123");
		AuditData databaseData = EntityFactory.makeAuditData("John Smith / Supervisor");
		databaseData.setId(auditData.getId());
        databaseData.setComment("123.145.167.189");
		AuditQuestion question = EntityFactory.makeAuditQuestion();
		question.setQuestionType("ESignature");
		auditData.setQuestion(question);
		databaseData.setQuestion(question);

		PicsTestUtil.forceSetPrivateField(auditDataSave, "mode", "Verify");
        PicsTestUtil.forceSetPrivateField(auditDataSave, "verifyButton", true);
		Boolean result = Whitebox.invokeMethod(auditDataSave, "processAndValidateBasedOnQuestionType", auditData, databaseData);
		assertTrue(result);
		assertTrue(auditData.getAnswer().equals("John Smith / Supervisor"));
        assertTrue(auditData.getComment().equals("123.145.167.189"));
	}

	@Test
	public void testESignatureReset() throws Exception {
		AuditData auditData = EntityFactory.makeAuditData(null);
		auditData.setComment(null);
		AuditQuestion question = EntityFactory.makeAuditQuestion();
		question.setQuestionType("ESignature");
		auditData.setQuestion(question);

		Boolean result = Whitebox.invokeMethod(auditDataSave, "processAndValidateBasedOnQuestionType", auditData, auditData);
		assertTrue(result);
		assertTrue(auditData.getComment().equals(""));
		assertTrue(auditData.getAnswer().equals(""));

	}

	@Test
	public void testExecute_Verify() throws Exception {
		AuditData oldData = EntityFactory.makeAuditData("No");
		oldData.setId(auditData.getId());

		AuditType annual = EntityFactory.makeAuditType(AuditType.ANNUALADDENDUM);
		audit.setAuditType(annual);
		oldData.setAudit(auditData.getAudit());
		oldData.setQuestion(auditData.getQuestion());

		when(auditDataService.findAuditQuestion(auditData.getQuestion().getId())).thenReturn(auditData.getQuestion());
		when(permissions.seesAllContractors()).thenReturn(true);
		when(auditDataDao.find(anyInt())).thenReturn(oldData);
		when(auditDataService.findAuditData(anyInt())).thenReturn(oldData);
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

		when(auditDataService.findAuditQuestion(auditData.getQuestion().getId())).thenReturn(auditData.getQuestion());
		when(permissions.isContractor()).thenReturn(true);
		when(auditDataDao.find(anyInt())).thenReturn(oldData);
		when(auditDataService.findAuditData(anyInt())).thenReturn(oldData);

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
        when(auditDataService.findAuditData(anyInt())).thenReturn(oldData);

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

	@Test
	public void testExecute_SimpleAnswer() throws Exception {
        when(auditDataDao.find(anyInt())).thenReturn(auditData);
		assertEquals("success", auditDataSave.execute());
	}

    @Test
    public void testExecute_SimpleAnswerResubmit() throws Exception {
        auditData.setId(0);
        when(auditDataService.findAuditDataByAuditAndQuestion(auditData)).thenReturn(mockAuditData);
        when(mockAuditData.getQuestion()).thenReturn(auditData.getQuestion());
        when(mockAuditData.getAudit()).thenReturn(auditData.getAudit());
        when(auditDataDao.find(anyInt())).thenReturn(auditData);
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

    @Test
    public void testExecute_EmptyStringNumberAnswer() throws Exception {
        auditData.getQuestion().setQuestionType("Number");
        auditData.setAnswer(""); // empty answers are always good

        Boolean result = Whitebox.invokeMethod(auditDataSave, "processAndValidateNumeric", auditData, null, "Number");
        assertTrue(result);

        auditData.setAnswer(null); // null answers are always good
        result = Whitebox.invokeMethod(auditDataSave, "processAndValidateNumeric", auditData, null, "Number");
        assertTrue(result);
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

		when(auditDataService.findAuditQuestion(questionInTheDatabase.getId())).thenReturn(questionInTheDatabase);
		when(auditDataDao.find(auditData.getId())).thenReturn(auditData);
		when(auditDataService.findAuditData(auditData.getId())).thenReturn(auditData);

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

		Whitebox.invokeMethod(auditDataSave, "processAndValidateBasedOnQuestionType", data, data);
		assertTrue("".equals(data.getAnswer()));

		data.setAnswer("[test, 1, 2, 3]");
		Whitebox.invokeMethod(auditDataSave, "processAndValidateBasedOnQuestionType", data, data);
		assertTrue("[test, 1, 2, 3]".equals(data.getAnswer()));
	}
}
