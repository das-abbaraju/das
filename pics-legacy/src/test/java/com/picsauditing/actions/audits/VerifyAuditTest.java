package com.picsauditing.actions.audits;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.jpa.entities.*;
import com.picsauditing.service.AuditDataService;
import com.picsauditing.util.AnswerMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.PicsTranslationTest;
import com.picsauditing.dao.AuditDataDAO;

public class VerifyAuditTest extends PicsTranslationTest {
	private VerifyAudit verifyAudit;

	private ContractorAccount contractor;
	private ContractorAudit conAudit;

	@Mock
	private AuditDataDAO auditDataDao;
    @Mock
    private AuditDataService auditDataService;
    @Mock
    private OshaAudit oshaAudit;

    @Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);


		verifyAudit = new VerifyAudit();
		PicsTestUtil.autowireDAOsFromDeclaredMocks(verifyAudit, this);

		contractor = EntityFactory.makeContractor();
		conAudit = EntityFactory.makeContractorAudit(AuditType.PQF, contractor);

		PicsTestUtil.forceSetPrivateField(verifyAudit, "conAudit", conAudit);
        PicsTestUtil.forceSetPrivateField(verifyAudit, "oshaAudit", oshaAudit);
        PicsTestUtil.forceSetPrivateField(verifyAudit, "auditDataService", auditDataService);
	}

    @Test
    public void testGetVisibleOShaQuestions_NoOshaLog() {
        AuditData keptOshaLogs = createAnswer("No", 123, null);
        AuditData oshaIncidents = createAnswer("No", 124, keptOshaLogs.getQuestion());
        AuditData hours = createAnswer("2000", 125, null);
        AuditData fatalities = createAnswer("0", 126, oshaIncidents.getQuestion());

        List<AuditData> answersToVerify = new ArrayList<>();
        answersToVerify.add(hours);
        answersToVerify.add(fatalities);

        List<AuditData> answers = new ArrayList<AuditData>();
        answers.add(hours);
        answers.add(fatalities);
        answers.add(oshaIncidents);
        answers.add(keptOshaLogs);
        AnswerMap answerMap = new AnswerMap(answers);

        when(oshaAudit.getQuestionsToVerify(OshaType.OSHA)).thenReturn(answersToVerify);
        when(auditDataService.loadAnswerMap(any(AuditData.class))).thenReturn(answerMap);
        assertEquals(1, verifyAudit.getVisibleOshaQuestions(OshaType.OSHA).size());
    }

    @Test
    public void testGetVisibleOShaQuestions_NoOshaRecordableIncidents() {
        AuditData keptOshaLogs = createAnswer("Yes", 123, null);
        AuditData oshaIncidents = createAnswer("No", 124, keptOshaLogs.getQuestion());
        AuditData hours = createAnswer("2000", 125, null);
        AuditData fatalities = createAnswer("0", 126, oshaIncidents.getQuestion());

        List<AuditData> answersToVerify = new ArrayList<>();
        answersToVerify.add(hours);
        answersToVerify.add(fatalities);

        List<AuditData> answers = new ArrayList<AuditData>();
        answers.add(hours);
        answers.add(fatalities);
        answers.add(oshaIncidents);
        answers.add(keptOshaLogs);
        AnswerMap answerMap = new AnswerMap(answers);

        when(oshaAudit.getQuestionsToVerify(OshaType.OSHA)).thenReturn(answersToVerify);
        when(auditDataService.loadAnswerMap(any(AuditData.class))).thenReturn(answerMap);
        assertEquals(1, verifyAudit.getVisibleOshaQuestions(OshaType.OSHA).size());
    }

    @Test
    public void testGetVisibleOShaQuestions_WithOshaReordableIncidents() {
        AuditData keptOshaLogs = createAnswer("Yes", 123, null);
        AuditData oshaIncidents = createAnswer("Yes", 124, keptOshaLogs.getQuestion());
        AuditData hours = createAnswer("2000", 125, null);
        AuditData fatalities = createAnswer("1", 126, oshaIncidents.getQuestion());

        List<AuditData> answersToVerify = new ArrayList<>();
        answersToVerify.add(hours);
        answersToVerify.add(fatalities);

        List<AuditData> answers = new ArrayList<AuditData>();
        answers.add(hours);
        answers.add(fatalities);
        answers.add(oshaIncidents);
        answers.add(keptOshaLogs);
        AnswerMap answerMap = new AnswerMap(answers);

        when(oshaAudit.getQuestionsToVerify(OshaType.OSHA)).thenReturn(answersToVerify);
        when(auditDataService.loadAnswerMap(any(AuditData.class))).thenReturn(answerMap);
        assertEquals(2, verifyAudit.getVisibleOshaQuestions(OshaType.OSHA).size());
    }

    private AuditData createAnswer(String answer, int questionId, AuditQuestion visibleQuestion) {
        AuditQuestion question = new AuditQuestion();
        question.setId(questionId);
        question.setVisibleQuestion(visibleQuestion);
        question.setVisibleAnswer("Yes");

        AuditData data = new AuditData();
        data.setAnswer(answer);
        data.setQuestion(question);

        return data;
    }

    @Test
	public void testGetPqfQuestions_QuestionsToValidate() {
		ArrayList<AuditData> list = new ArrayList<AuditData>();
		AuditData auditData = EntityFactory.makeAuditData("Yes");
		AuditCatData auditCatData = EntityFactory.makeAuditCatData();

		auditCatData.getCategory().setId(auditData.getQuestion().getCategory().getId());
		conAudit.getCategories().add(auditCatData);

		when(auditDataDao.findCustomPQFVerifications(Matchers.anyInt())).thenReturn(list);

		assertEquals(0, verifyAudit.getPqfQuestions().size()); // no categories

		list.add(auditData);
		PicsTestUtil.forceSetPrivateField(verifyAudit, "pqfQuestions", null);
		assertEquals(1, verifyAudit.getPqfQuestions().size()); // one applicable

		auditCatData.setApplies(false);
		PicsTestUtil.forceSetPrivateField(verifyAudit, "pqfQuestions", null);
		assertEquals(0, verifyAudit.getPqfQuestions().size()); // no applicable
	}

	@Test
	public void testShowOsha_Osha() {
		ContractorAccount con = EntityFactory.makeContractor();

		ContractorAudit annual = EntityFactory.makeAnnualUpdate(11, con, "2010");
		PicsTestUtil.forceSetPrivateField(verifyAudit, "conAudit", annual);

		assertTrue(verifyAudit.showOsha(OshaType.OSHA));
        AuditData oshaKept = EntityFactory.makeAuditData("Yes", 2064);
		annual.getData().add(oshaKept);
		assertTrue(verifyAudit.showOsha(OshaType.OSHA));
		;
	}

    @Test
    public void testShowOsha_Cohs() {
        ContractorAccount con = EntityFactory.makeContractor();

        ContractorAudit annual = EntityFactory.makeAnnualUpdate(11, con, "2010");
        PicsTestUtil.forceSetPrivateField(verifyAudit, "conAudit", annual);

        assertFalse(verifyAudit.showOsha(OshaType.COHS));
        AuditData oshaKept = EntityFactory.makeAuditData("Yes", AuditQuestion.COHS_KEPT_ID);
        annual.getData().add(oshaKept);
        assertTrue(verifyAudit.showOsha(OshaType.COHS));
        ;
    }

}
