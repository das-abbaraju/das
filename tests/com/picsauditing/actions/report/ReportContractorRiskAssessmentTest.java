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
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.Permissions;
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
import com.picsauditing.search.Report;
import com.picsauditing.util.ReportFilterContractor;
import com.picsauditing.util.SpringUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ReportContractorRiskAssessment.class, ActionContext.class, ReportFilterContractor.class,
		SpringUtils.class })
@PowerMockIgnore({ "javax.xml.parsers.*", "ch.qos.logback.*", "org.slf4j.*", "org.apache.xerces.*" })
public class ReportContractorRiskAssessmentTest {
	private ContractorAccount contractorAccount;
	private ReportContractorRiskAssessment reportContractorRiskAssessment;

	private final String DEFAULT = "DEFAULT";

	@Mock
	private ActionContext actionContext;
	@Mock
	private EmailTemplate emailTemplate;
	@Mock
	private EmailTemplateDAO emailTemplateDAO;
	@Mock
	private EntityManager entityManager;
	@Mock
	private Permissions permissions;
	@Mock
	private Report report;
	@Mock
	private ReportFilterContractor reportFilterContractor;
	@Mock
	private UserDAO userDAO;

	@Before
	public void setUp() throws Exception {
		initializeMocks();

		reportContractorRiskAssessment = new ReportContractorRiskAssessment();

		initializeContractorAccount();
		setCustomPageVariables();
		setExpectedBehaviors();
		stubStaticFilterMethods();
	}

	@Test
	public void testAcceptWithNullType() throws Exception {
		reportContractorRiskAssessment.setType(null);
		Assert.assertEquals(Action.SUCCESS, reportContractorRiskAssessment.accept());

		Mockito.verify(entityManager, Mockito.never()).merge(Mockito.any());
		Mockito.verify(entityManager, Mockito.never()).persist(Mockito.any());

		Assert.assertTrue(reportContractorRiskAssessment.hasActionErrors());
	}

	@Test
	public void testAcceptWithSafetyType() throws Exception {
		ReportContractorRiskAssessment reportContractorRiskAssessmentSpy = Mockito.spy(reportContractorRiskAssessment);

		reportContractorRiskAssessmentSpy.setType(ReportContractorRiskAssessment.SAFETY);
		Assert.assertEquals(Action.SUCCESS, reportContractorRiskAssessmentSpy.accept());
		Assert.assertEquals(LowMedHigh.Low, contractorAccount.getSafetyRisk());
		Assert.assertNotNull(contractorAccount.getSafetyRiskVerified());

		// Save existing audit data and contractor account
		Mockito.verify(entityManager, Mockito.atLeastOnce()).merge(Mockito.any());
		Mockito.verify(entityManager).persist(Mockito.any(Note.class));

		Assert.assertFalse(reportContractorRiskAssessment.hasActionErrors());
	}

	@Test
	public void testAcceptWithProductType() throws Exception {
		ReportContractorRiskAssessment reportContractorRiskAssessmentSpy = Mockito.spy(reportContractorRiskAssessment);

		reportContractorRiskAssessmentSpy.setType(ReportContractorRiskAssessment.PRODUCT);
		Assert.assertEquals(Action.SUCCESS, reportContractorRiskAssessmentSpy.accept());
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
		Assert.assertEquals(Action.SUCCESS, reportContractorRiskAssessment.reject());

		Mockito.verify(entityManager, Mockito.never()).merge(Mockito.any());
		Mockito.verify(entityManager, Mockito.never()).persist(Mockito.any());

		Assert.assertTrue(reportContractorRiskAssessment.hasActionErrors());
	}

	@Test
	public void testRejectWithSafetyType() throws Exception {
		ReportContractorRiskAssessment reportContractorRiskAssessmentSpy = Mockito.spy(reportContractorRiskAssessment);

		reportContractorRiskAssessmentSpy.setType(ReportContractorRiskAssessment.SAFETY);
		Assert.assertEquals(Action.SUCCESS, reportContractorRiskAssessmentSpy.reject());
		Assert.assertEquals(LowMedHigh.Med, contractorAccount.getSafetyRisk());
		Assert.assertNotNull(contractorAccount.getSafetyRiskVerified());

		// Save existing audit data and contractor account
		Mockito.verify(entityManager, Mockito.atLeastOnce()).merge(Mockito.any());
		Mockito.verify(entityManager).persist(Mockito.any(Note.class));

		Assert.assertFalse(reportContractorRiskAssessment.hasActionErrors());
	}

	@Test
	public void testRejectWithProductType() throws Exception {
		ReportContractorRiskAssessment reportContractorRiskAssessmentSpy = Mockito.spy(reportContractorRiskAssessment);

		reportContractorRiskAssessmentSpy.setType(ReportContractorRiskAssessment.PRODUCT);
		Assert.assertEquals(Action.SUCCESS, reportContractorRiskAssessmentSpy.reject());

		Assert.assertEquals(LowMedHigh.High, contractorAccount.getProductRisk());
		Assert.assertNotNull(contractorAccount.getProductRiskVerified());

		// Save existing audit data and contractor account
		Mockito.verify(entityManager, Mockito.atLeastOnce()).merge(Mockito.any());
		Mockito.verify(entityManager).persist(Mockito.any(Note.class));

		Assert.assertFalse(reportContractorRiskAssessment.hasActionErrors());
	}

	private void initializeMocks() {
		MockitoAnnotations.initMocks(this);
		PowerMockito.mockStatic(ActionContext.class);
		PowerMockito.mockStatic(ReportFilterContractor.class);
		PowerMockito.mockStatic(SpringUtils.class);
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
		picsTestUtil.autowireEMInjectedDAOs(reportContractorRiskAssessment, entityManager);
	}

	private void setExpectedBehaviors() throws SQLException {
		Mockito.doNothing().when(contractorAccount).syncBalance();

		Mockito.when(ActionContext.getContext()).thenReturn(actionContext);
		Mockito.when(actionContext.getSession()).thenReturn(createSessionObject());
		Mockito.when(emailTemplateDAO.find(Mockito.anyInt())).thenReturn(emailTemplate);
		Mockito.when(permissions.hasPermission(OpPerms.RiskRank, OpType.View)).thenReturn(true);
		Mockito.when(report.getPage()).thenReturn(new ArrayList<BasicDynaBean>());

		Mockito.when(SpringUtils.getBean("EmailTemplateDAO")).thenReturn(emailTemplateDAO);
		Mockito.when(SpringUtils.getBean("UserDAO")).thenReturn(userDAO);
	}

	private Map<String, Object> createSessionObject() {
		Map<String, Object> sessions = new HashMap<String, Object>();
		sessions.put("filter" + ListType.Contractor, reportFilterContractor);
		return sessions;
	}

	private void stubStaticFilterMethods() {
		PowerMockito.stub(PowerMockito.method(ReportFilterContractor.class, "getDefaultName")).toReturn(DEFAULT);
		PowerMockito.stub(PowerMockito.method(ReportFilterContractor.class, "getDefaultCity")).toReturn(DEFAULT);
		PowerMockito.stub(PowerMockito.method(ReportFilterContractor.class, "getDefaultZip")).toReturn(DEFAULT);
		PowerMockito.stub(PowerMockito.method(ReportFilterContractor.class, "getDefaultAmount")).toReturn(DEFAULT);
		PowerMockito.stub(PowerMockito.method(ReportFilterContractor.class, "getDefaultTaxID")).toReturn(DEFAULT);
		PowerMockito.stub(PowerMockito.method(ReportFilterContractor.class, "getDefaultPerformedBy")).toReturn(DEFAULT);
		PowerMockito.stub(PowerMockito.method(ReportFilterContractor.class, "getDefaultSelectPerformedBy")).toReturn(
				DEFAULT);
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
