package com.picsauditing.actions.audits;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.doNothing;
import org.mockito.MockitoAnnotations;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.access.Permissions;
import com.picsauditing.auditBuilder.AuditCategoryRuleCache;
import com.picsauditing.auditBuilder.AuditPercentCalculator;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
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
		MockitoAnnotations.initMocks(this);

		auditDataSave = new AuditDataSave();
		autowireEMInjectedDAOs(auditDataSave);

		// make some entities
		user = EntityFactory.makeUser();
		when(permissions.getUserId()).thenReturn(user.getId());
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
	public void testExecute_NumberAnswer() throws Exception {
		auditData.getQuestion().setQuestionType("Number");
		auditData.setAnswer("12345");
		assertEquals("success", auditDataSave.execute());
	}
}
