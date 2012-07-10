package com.picsauditing.actions.audits;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.Permissions;
import com.picsauditing.auditBuilder.AuditCategoryRuleCache;
import com.picsauditing.auditBuilder.AuditPercentCalculator;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeRule;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.AnswerMap;

public class AuditDataSaveTest extends PicsTest {
	AuditDataSave auditDataSave;
	User user;

	@Mock
	private Permissions permissions;
	@Mock
	private AuditDataDAO auditDataDao;
	@Mock
	private AuditCategoryDataDAO catDataDao;
	@Mock
	private AuditDecisionTableDAO auditRuleDAO;
	@Mock
	private AuditQuestionDAO questionDao;

	@Mock
	AuditCategoryRuleCache categoryRuleCache = new AuditCategoryRuleCache();
	@Mock
	AuditPercentCalculator auditPercentCalculatior = new AuditPercentCalculator();

	private ContractorAccount contractor;
	private AuditData auditData;
	private ContractorAudit audit;
	private AnswerMap answerMap;
	private AuditCatData catData;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		super.setUp();
		MockitoAnnotations.initMocks(this);

		auditDataSave = new AuditDataSave();
		autowireEMInjectedDAOs(auditDataSave);

		// make some entities
		user = EntityFactory.makeUser();
		when(permissions.getUserId()).thenReturn(user.getId());
		when(permissions.getLocale()).thenReturn(new Locale("en"));
		PicsTestUtil.forceSetPrivateField(auditDataSave, "permissions",
				permissions);
		PicsTestUtil.forceSetPrivateField(auditDataSave, "user", user);

		contractor = EntityFactory.makeContractor();

		audit = EntityFactory.makeContractorAudit(1, contractor);

		auditData = EntityFactory.makeAuditData("Yes");
		auditData.setAudit(audit);
		auditData.getQuestion().setAuditCategoryRules(
				new ArrayList<AuditCategoryRule>());
		auditData.getQuestion().setAuditTypeRules(
				new ArrayList<AuditTypeRule>());
		auditData.getQuestion().setCategory(
				EntityFactory.addCategories(audit.getAuditType(), 104,
						"Test Category 104"));

		auditDataSave.setAuditData(auditData);
		auditDataSave.setContractor(contractor);

		catData = EntityFactory.makeAuditCatData();

		PicsTestUtil.forceSetPrivateField(auditDataSave,
				"auditCategoryRuleCache", categoryRuleCache);
		PicsTestUtil.forceSetPrivateField(auditDataSave,
				"auditPercentCalculator", auditPercentCalculatior);

		when(
				auditDataDao.findAnswers(anyInt(),
						(Collection<Integer>) Matchers.anyObject()))
				.thenReturn(answerMap);
		PicsTestUtil.forceSetPrivateField(auditDataSave, "auditDataDao",
				auditDataDao);
		PicsTestUtil.forceSetPrivateField(auditDataSave, "catDataDao",
				catDataDao);
		PicsTestUtil.forceSetPrivateField(auditDataSave, "auditRuleDAO",
				auditRuleDAO);

		PicsTestUtil.forceSetPrivateField(auditDataSave, "conAudit", audit);

		when(
				em.find(Matchers.argThat(equalTo(AuditQuestion.class)),
						Matchers.anyInt())).thenReturn(auditData.getQuestion());
		when(
				em.find(Matchers.argThat(equalTo(ContractorAudit.class)),
						anyInt())).thenReturn(audit);
		when(catDataDao.findAuditCatData(Matchers.anyInt(), Matchers.anyInt()))
				.thenReturn(catData);
		when(auditRuleDAO.findCategoryRulesByQuestion(Matchers.anyInt()))
				.thenReturn(new ArrayList<AuditCategoryRule>());
		doNothing().when(auditPercentCalculatior).updatePercentageCompleted(
				catData);
	}
	
	@Test 
	public void testNoIncidents() throws Exception {
		AuditType annualType = EntityFactory.makeAuditType(AuditType.ANNUALADDENDUM);
		audit.setAuditType(annualType);

		AuditQuestion hseIncidentsQuestion = EntityFactory.makeAuditQuestion();
		AuditData[] safetyData = null;
		
		PicsTestUtil.forceSetPrivateField(auditDataSave, "questionDao", questionDao);
		

		setUpIncidentQuestion(hseIncidentsQuestion, AuditDataSave.COHS_INCIDENT_QUESTION_ID);
		safetyData = new AuditData[AuditDataSave.COHS_INCIDENT_RELATED_QUESTION_IDS.length];
		setupSafetyDataAnswers(safetyData, AuditDataSave.COHS_INCIDENT_RELATED_QUESTION_IDS);
		when(questionDao.find(hseIncidentsQuestion.getId())).thenReturn(hseIncidentsQuestion);
		
		assertEquals("success", auditDataSave.execute());
		for (int i=0; i< AuditDataSave.COHS_INCIDENT_RELATED_QUESTION_IDS.length ; i++) {
			assertEquals("0", safetyData[i].getAnswer());
		}

		setUpIncidentQuestion(hseIncidentsQuestion, AuditDataSave.COHS_INCIDENT_QUESTION_ID);
		safetyData = new AuditData[AuditDataSave.COHS_INCIDENT_RELATED_QUESTION_IDS.length];
		setupSafetyDataAnswers(safetyData, AuditDataSave.COHS_INCIDENT_RELATED_QUESTION_IDS);
		when(questionDao.find(hseIncidentsQuestion.getId())).thenReturn(hseIncidentsQuestion);
		
		assertEquals("success", auditDataSave.execute());
		for (int i=0; i< AuditDataSave.COHS_INCIDENT_RELATED_QUESTION_IDS.length ; i++) {
			assertEquals("0", safetyData[i].getAnswer());
		}
		setUpIncidentQuestion(hseIncidentsQuestion, AuditDataSave.OSHA_INCIDENT_QUESTION_ID);
		safetyData = new AuditData[AuditDataSave.OSHA_INCIDENT_RELATED_QUESTION_IDS.length];
		setupSafetyDataAnswers(safetyData, AuditDataSave.OSHA_INCIDENT_RELATED_QUESTION_IDS);
		when(questionDao.find(hseIncidentsQuestion.getId())).thenReturn(hseIncidentsQuestion);
		
		assertEquals("success", auditDataSave.execute());
		for (int i=0; i< AuditDataSave.OSHA_INCIDENT_RELATED_QUESTION_IDS.length ; i++) {
			assertEquals("0", safetyData[i].getAnswer());
		}
}

	private void setUpIncidentQuestion(AuditQuestion coshIncidentsQuestion, int questionID) {
		coshIncidentsQuestion.setId(questionID);
		auditData.setAnswer("No");
		auditData.setQuestion(coshIncidentsQuestion);
		auditData.getQuestion().setAuditCategoryRules(
				new ArrayList<AuditCategoryRule>());
		auditData.getQuestion().setAuditTypeRules(
				new ArrayList<AuditTypeRule>());
		auditData.getQuestion().setCategory(
				EntityFactory.addCategories(audit.getAuditType(), 104,
						"Test Category 104"));
	}
	
	private void setupSafetyDataAnswers(AuditData[] safetyData, int[] questionIds) {
		for (int i=0; i< questionIds.length ; i++) {
			AuditData auditAnswer = EntityFactory.makeAuditData("6");
			auditAnswer.setId(questionIds[i]);
			safetyData[i] = auditAnswer;
			when(auditDataDao.findAnswerToQuestion(audit.getId(),
					safetyData[i].getId())).thenReturn(safetyData[i]);
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
		
		when(auditDataDao.find(anyInt())) .thenReturn(oldData);
		PicsTestUtil.forceSetPrivateField(auditDataSave, "button",
				"verify");
		assertEquals("success", auditDataSave.execute());
		assertEquals("Yes", oldData.getAnswer());
		assertNotNull(oldData.getDateVerified());
		assertEquals(user.getId(), oldData.getAuditor().getId());
	}

	@Test
	public void testTrimWhitespaceLeadingZerosAndAllCommas() throws Exception {
		assertEquals("10.10",
				AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas("10.10"));
		assertEquals("10.10",
				AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas("  10.10"));
		assertEquals("10.10",
				AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas("10.10  "));
		assertEquals("10.10",
				AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas("010.10"));
		assertEquals("10.10",
				AuditDataSave
						.trimWhitespaceLeadingZerosAndAllCommas("00010.10"));
		assertEquals("10.10",
				AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas(",010.10"));
		assertEquals("10.10",
				AuditDataSave
						.trimWhitespaceLeadingZerosAndAllCommas("0,010.10"));
		assertEquals("0",
				AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas("0"));
		assertEquals("0",
				AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas("00"));
		assertEquals("0",
				AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas("000"));
		assertEquals(".0",
				AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas("000.0"));
		assertEquals(".00",
				AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas("000.00"));
		assertEquals(".01",
				AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas("0.01"));
		assertEquals("1",
				AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas("01"));
		assertEquals("1",
				AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas("001"));
		assertEquals("1.01",
				AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas("  01.01"));
		assertEquals("1.01",
				AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas("\t01.01"));
		assertEquals("ABC",
				AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas("ABC"));
		assertEquals("ABC",
				AuditDataSave.trimWhitespaceLeadingZerosAndAllCommas("  ABC  "));
	}

	@Test
	public void testExecute_SimpleAnswer() throws Exception {
		assertEquals("success", auditDataSave.execute());
	}

	@Test
	public void testExecute_BadNumberAnswer() throws Exception {
		auditData.getQuestion().setQuestionType("Number");
		auditData.setAnswer("12-345"); // the '-' in the middle will cause a BigDecimal exception

		when(i18nCache.getText("Audit.message.InvalidFormat", Locale.ENGLISH, (Object[])null)).thenReturn("Unit Test String");
		assertEquals("success", auditDataSave.execute());
		assertEquals(true, auditDataSave.getActionErrors().size() > 0);
	}

	@Test
	public void testStructureNewDate() throws ParseException{
		Date expected = DateBean.parseDate("2001-02-03");
		Date actual;

		actual = AuditDataSave.restructureNewDate("2/3/2001");
		assertEquals(expected, actual);
		actual = AuditDataSave.restructureNewDate("2-3-01");
		assertEquals(expected, actual);
		actual = AuditDataSave.restructureNewDate("2/3/01");
		assertEquals(expected, actual);
		actual = AuditDataSave.restructureNewDate("02-03-2001");
		assertEquals(expected, actual);
		actual = AuditDataSave.restructureNewDate("2001/02/03");
		assertEquals(expected, actual);
		actual = AuditDataSave.restructureNewDate("2001/2/3");
		assertEquals(expected, actual);
		actual = AuditDataSave.restructureNewDate("02/03/2001");
		assertEquals(expected, actual);
		actual = AuditDataSave.restructureNewDate("02/03/01");
		assertEquals(expected, actual);
	}
	
}
