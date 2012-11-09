package com.picsauditing.actions.report;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.commons.beanutils.BasicDynaBean;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.springframework.context.ApplicationContext;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsActionTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.EmailTemplateDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.EmailTemplate;
import com.picsauditing.jpa.entities.ListType;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.search.Report;
import com.picsauditing.util.ReportFilterContractor;
import com.picsauditing.util.SpringUtils;

public class ReportContractorRiskAssessmentTest extends PicsActionTest {
	private ReportContractorRiskAssessment reportContractorRiskAssessment;

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
	private ReportFilterContractor reportFilterContractor;
	@Mock
	private UserDAO userDAO;
	@Mock
	private ApplicationContext applicationContext;
	@Mock
	private EmailSender emailSender;
	@Mock
	private EmailBuilder emailBuilder;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		reportContractorRiskAssessment = new ReportContractorRiskAssessment();
		super.setUp(reportContractorRiskAssessment);

		initializeContractorAccount();
		setCustomPageVariables();
		setExpectedBehaviors();
	}

	@Test
	public void testAcceptWithNullType() throws Exception {
		reportContractorRiskAssessment.setType(null);
		Assert.assertEquals(PicsActionSupport.REDIRECT, reportContractorRiskAssessment.accept());

		Mockito.verify(entityManager, Mockito.never()).merge(Mockito.any());
		Mockito.verify(entityManager, Mockito.never()).persist(Mockito.any());

		Assert.assertTrue(reportContractorRiskAssessment.hasActionErrors());
	}

	@Test
	public void testAcceptWithSafetyType() throws Exception {
		reportContractorRiskAssessment.setType(ReportContractorRiskAssessment.SAFETY);
		Assert.assertEquals(PicsActionSupport.REDIRECT, reportContractorRiskAssessment.accept());
		Assert.assertEquals(LowMedHigh.Low, contractorAccount.getSafetyRisk());
		Assert.assertNotNull(contractorAccount.getSafetyRiskVerified());

		// Save existing audit data and contractor account
		Mockito.verify(entityManager, Mockito.atLeastOnce()).merge(Mockito.any());
		Mockito.verify(entityManager).persist(Mockito.any(Note.class));

		Assert.assertFalse(reportContractorRiskAssessment.hasActionErrors());
	}

	@Test
	public void testAcceptWithProductType() throws Exception {
		reportContractorRiskAssessment.setType(ReportContractorRiskAssessment.PRODUCT);
		Assert.assertEquals(PicsActionSupport.REDIRECT, reportContractorRiskAssessment.accept());
		// Even though the first product risk question was low, the highest
		// risks from the two product risk questions was medium
		Assert.assertEquals(LowMedHigh.Med, contractorAccount.getProductRisk());
		Assert.assertNotNull(contractorAccount.getProductRiskVerified());

		// Save existing audit data and contractor account
		Mockito.verify(entityManager, Mockito.atLeastOnce()).merge(Mockito.any());
		Mockito.verify(entityManager).persist(Mockito.any(Note.class));

		Assert.assertFalse(reportContractorRiskAssessment.hasActionErrors());
	}

	@Test
	public void testRejectWithNullType() throws Exception {
		reportContractorRiskAssessment.setType(null);
		Assert.assertEquals(PicsActionSupport.REDIRECT, reportContractorRiskAssessment.reject());

		Mockito.verify(entityManager, Mockito.never()).merge(Mockito.any());
		Mockito.verify(entityManager, Mockito.never()).persist(Mockito.any());

		Assert.assertTrue(reportContractorRiskAssessment.hasActionErrors());
	}

	@Test
	public void testRejectWithSafetyType() throws Exception {
		reportContractorRiskAssessment.setType(ReportContractorRiskAssessment.SAFETY);
		Assert.assertEquals(PicsActionSupport.REDIRECT, reportContractorRiskAssessment.reject());
		Assert.assertEquals(LowMedHigh.Med, contractorAccount.getSafetyRisk());
		Assert.assertNotNull(contractorAccount.getSafetyRiskVerified());

		// Save existing audit data and contractor account
		Mockito.verify(entityManager, Mockito.atLeastOnce()).merge(Mockito.any());
		Mockito.verify(entityManager).persist(Mockito.any(Note.class));

		Assert.assertFalse(reportContractorRiskAssessment.hasActionErrors());
	}

	@Test
	public void testRejectWithProductType() throws Exception {
		reportContractorRiskAssessment.setType(ReportContractorRiskAssessment.PRODUCT);
		Assert.assertEquals(PicsActionSupport.REDIRECT, reportContractorRiskAssessment.reject());

		Assert.assertEquals(LowMedHigh.High, contractorAccount.getProductRisk());
		Assert.assertNotNull(contractorAccount.getProductRiskVerified());

		// Save existing audit data and contractor account
		Mockito.verify(entityManager, Mockito.atLeastOnce()).merge(Mockito.any());
		Mockito.verify(entityManager).persist(Mockito.any(Note.class));

		Assert.assertFalse(reportContractorRiskAssessment.hasActionErrors());
	}

	private void initializeContractorAccount() {
		contractorAccount = Mockito.spy(EntityFactory.makeContractor());
		contractorAccount.setAudits(generatePQFWithRiskAnswers());
		contractorAccount.setProductRisk(LowMedHigh.High);
	}

	private void setCustomPageVariables() throws InstantiationException, IllegalAccessException {
		PicsTestUtil picsTestUtil = new PicsTestUtil();
		Whitebox.setInternalState(reportContractorRiskAssessment, "con", contractorAccount);
		Whitebox.setInternalState(reportContractorRiskAssessment, "report", report);
		Whitebox.setInternalState(reportContractorRiskAssessment, "permissions", permissions);
		Whitebox.setInternalState(reportContractorRiskAssessment, "emailSender", emailSender);
		Whitebox.setInternalState(reportContractorRiskAssessment, "emailBuilder", emailBuilder);
		picsTestUtil.autowireEMInjectedDAOs(reportContractorRiskAssessment, entityManager);
	}

	private void setExpectedBehaviors() throws SQLException {
		Whitebox.setInternalState(SpringUtils.class, "applicationContext", applicationContext);
		
		Mockito.doNothing().when(contractorAccount).syncBalance();

		Mockito.when(emailTemplateDAO.find(Mockito.anyInt())).thenReturn(emailTemplate);
		Mockito.when(permissions.hasPermission(OpPerms.RiskRank, OpType.View)).thenReturn(true);
		Mockito.when(report.getPage(false)).thenReturn(new ArrayList<BasicDynaBean>());

		Mockito.when(applicationContext.getBean("EmailTemplateDAO")).thenReturn(emailTemplateDAO);
		Mockito.when(applicationContext.getBean("UserDAO")).thenReturn(userDAO);
	}

	private Map<String, Object> createSessionObject() {
		Map<String, Object> sessions = new HashMap<String, Object>();
		sessions.put("filter" + ListType.Contractor, reportFilterContractor);
		return sessions;
	}

	private List<ContractorAudit> generatePQFWithRiskAnswers() {
		List<ContractorAudit> contractorAudits = new ArrayList<ContractorAudit>();
		ContractorAudit contractorAudit = new ContractorAudit();
		contractorAudit.setAuditType(new AuditType(AuditType.PQF));

		createAndAddSafetyRiskQuestion(contractorAudit);
		createAndAddProductRiskQuestion(contractorAudit);
		createAndAddProductRiskQuestion2(contractorAudit);

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

	private void createAndAddProductRiskQuestion(ContractorAudit contractorAudit) {
		AuditData auditDataProduct = new AuditData();
		auditDataProduct.setId(2);
		auditDataProduct.setAnswer("Low");
		auditDataProduct.setQuestion(new AuditQuestion());
		auditDataProduct.getQuestion().setId(AuditQuestion.PRODUCT_SAFETY_CRITICAL_ASSESSMENT);
		contractorAudit.getData().add(auditDataProduct);
	}

	private void createAndAddProductRiskQuestion2(ContractorAudit contractorAudit) {
		AuditData auditDataProduct2 = new AuditData();
		auditDataProduct2.setId(3);
		auditDataProduct2.setAnswer("Med");
		auditDataProduct2.setQuestion(new AuditQuestion());
		auditDataProduct2.getQuestion().setId(AuditQuestion.PRODUCT_CRITICAL_ASSESSMENT);
		contractorAudit.getData().add(auditDataProduct2);
	}
}
