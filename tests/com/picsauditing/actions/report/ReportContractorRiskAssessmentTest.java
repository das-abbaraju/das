package com.picsauditing.actions.report;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.reflect.Whitebox.setInternalState;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.commons.beanutils.BasicDynaBean;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picsauditing.PicsActionTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.EmailTemplateDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.AccountLevel;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.EmailTemplate;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.search.Report;

public class ReportContractorRiskAssessmentTest extends PicsActionTest {
	private ReportContractorRiskAssessment reportContractorRiskAssessment;

	@Mock
	private ContractorAccount contractorAccount;
	@Mock
	private EmailTemplate emailTemplate;
	@Mock
	private EmailTemplateDAO emailTemplateDAO;
	@Mock
	private EntityManager entityManager;
	@Mock
	private Report report;
	@Mock
	private UserDAO userDAO;
	@Mock
	private EmailSender emailSender;
	@Mock
	private EmailBuilder emailBuilder;

	@AfterClass
	public static void classTearDown() throws Exception {
		PicsActionTest.classTearDown();
		PicsTestUtil.resetSpringUtilsBeans();
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		reportContractorRiskAssessment = new ReportContractorRiskAssessment();
		super.setUp(reportContractorRiskAssessment);

		when(contractorAccount.getAccountLevel()).thenReturn(AccountLevel.Full);
		when(contractorAccount.getAudits()).thenReturn(generatePQFWithRiskAnswers());
		when(contractorAccount.getId()).thenReturn(1);

		setCustomPageVariables();
		setExpectedBehaviors();
	}

	@Test
	public void testAcceptWithSafetyType() throws Exception {
		when(contractorAccount.getSafetyRisk()).thenReturn(LowMedHigh.High);

		assertEquals(PicsActionSupport.REDIRECT, reportContractorRiskAssessment.accept());

		// Save existing audit data and contractor account
		verify(contractorAccount).setSafetyRisk(LowMedHigh.Low);
		verify(contractorAccount).setSafetyRiskVerified(any(Date.class));
		verify(entityManager, atLeastOnce()).merge(any());
		verify(entityManager).persist(any(Note.class));

		assertFalse(reportContractorRiskAssessment.hasActionErrors());
	}

	@Test
	public void testRejectWithSafetyType() throws Exception {
		when(contractorAccount.getSafetyRisk()).thenReturn(LowMedHigh.High);

		assertEquals(PicsActionSupport.REDIRECT, reportContractorRiskAssessment.reject());

		// Save existing audit data and contractor account
		verify(contractorAccount, never()).setSafetyRisk(any(LowMedHigh.class));
		verify(contractorAccount).setSafetyRiskVerified(any(Date.class));
		verify(entityManager, atLeastOnce()).merge(any());
		verify(entityManager).persist(any(Note.class));

		assertFalse(reportContractorRiskAssessment.hasActionErrors());
	}

	private void setCustomPageVariables() throws InstantiationException, IllegalAccessException {
		PicsTestUtil picsTestUtil = new PicsTestUtil();
		setInternalState(reportContractorRiskAssessment, "con", contractorAccount);
		setInternalState(reportContractorRiskAssessment, "report", report);
		setInternalState(reportContractorRiskAssessment, "permissions", permissions);
		setInternalState(reportContractorRiskAssessment, "emailSender", emailSender);
		setInternalState(reportContractorRiskAssessment, "emailBuilder", emailBuilder);
		picsTestUtil.autowireEMInjectedDAOs(reportContractorRiskAssessment, entityManager);
	}

	private void setExpectedBehaviors() throws SQLException {
		setupApplicationContext();

		doNothing().when(contractorAccount).syncBalance();

		when(emailTemplateDAO.find(anyInt())).thenReturn(emailTemplate);
		when(permissions.hasPermission(OpPerms.RiskRank, OpType.View)).thenReturn(true);
		when(report.getPage(false)).thenReturn(new ArrayList<BasicDynaBean>());
	}

	private void setupApplicationContext() {
		Map<String, Object> beans = new HashMap<>();
		beans.put("EmailTemplateDAO", emailTemplateDAO);
		beans.put("UserDAO", userDAO);
		PicsTestUtil.setSpringUtilsBeans(beans);
	}

	private List<ContractorAudit> generatePQFWithRiskAnswers() {
		List<ContractorAudit> contractorAudits = new ArrayList<ContractorAudit>();
		ContractorAudit contractorAudit = new ContractorAudit();
		contractorAudit.setAuditType(new AuditType(AuditType.PQF));

		createAndAddSafetyRiskQuestion(contractorAudit);
		contractorAudits.add(contractorAudit);

		return contractorAudits;
	}

	private void createAndAddSafetyRiskQuestion(ContractorAudit contractorAudit) {
		AuditData auditDataSafety = new AuditData();
		auditDataSafety.setId(1);
		auditDataSafety.setAnswer("Low");
		auditDataSafety.setQuestion(new AuditQuestion());
		auditDataSafety.getQuestion().setId(AuditQuestion.RISK_LEVEL_ASSESSMENT);
		contractorAudit.getData().add(auditDataSafety);
	}
}
