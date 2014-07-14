package com.picsauditing.actions.contractors;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.contractors.risk.ServiceRiskCalculator;
import com.picsauditing.audits.AuditTypeRuleCache;
import com.picsauditing.dao.BasicDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.featuretoggle.Features;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.service.employeeGuard.EmployeeGuardRulesService;
import com.picsauditing.util.PermissionToViewContractor;
import edu.emory.mathcs.backport.java.util.Arrays;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.reflect.Whitebox;
import org.togglz.junit.TogglzRule;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RegistrationServiceEvaluationTest extends PicsTest {
	RegistrationServiceEvaluation serviceEvaluation;

	@Mock
	private Permissions permissions;
	@Mock
	protected ContractorAccountDAO contractorAccountDao;
	@Mock
	PermissionToViewContractor permissionToViewContractor;
	@Mock
	protected ContractorAuditDAO auditDao;
	@Mock
	protected BasicDAO dao;
    @Mock
    protected AuditTypeRuleCache auditTypeRuleCache;
    @Mock
    protected OperatorAccount operator;
    @Mock
    protected ServiceRiskCalculator serviceRiskCalculator;
    @Mock
    private EmployeeGuardRulesService employeeGuardRulesService;

    @Rule
    public TogglzRule togglzRule = TogglzRule.allEnabled(Features.class);

    private ContractorAccount contractor;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		MockitoAnnotations.initMocks(this);

		serviceEvaluation = new RegistrationServiceEvaluation();
		autowireEMInjectedDAOs(serviceEvaluation);

		contractor = EntityFactory.makeContractor();
		contractor.setAccountLevel(AccountLevel.Full);
		contractor.setStatus(AccountStatus.Pending);
		serviceEvaluation.setId(contractor.getId());

		when(permissionToViewContractor.check(Matchers.anyBoolean())).thenReturn(true);
		when(permissions.isContractor()).thenReturn(true);
		when(permissions.getAccountId()).thenReturn(contractor.getId());
		when(contractorAccountDao.find(Matchers.anyInt())).thenReturn(contractor);

		PicsTestUtil.forceSetPrivateField(serviceEvaluation, "permissions", permissions);
		PicsTestUtil.forceSetPrivateField(serviceEvaluation, "contractorAccountDao", contractorAccountDao);
		PicsTestUtil.forceSetPrivateField(serviceEvaluation, "permissionToViewContractor", permissionToViewContractor);
        PicsTestUtil.forceSetPrivateField(serviceEvaluation, "auditTypeRuleCache", auditTypeRuleCache);
        PicsTestUtil.forceSetPrivateField(serviceEvaluation, "serviceRiskCalculator", serviceRiskCalculator);
        PicsTestUtil.forceSetPrivateField(serviceEvaluation, "employeeGuardRulesService", employeeGuardRulesService);
	}

    @Test
    public void testParseSsipDates_LastSsipMemberAudit() throws Exception {
        Map<Integer, AuditData> ssipAnswers = createSsipAnswerMap();

        ssipAnswers.get(RegistrationServiceEvaluation.QUESTION_ID_SSIP_AUDIT_DATE).setAnswer("2015-11-01");
        Whitebox.setInternalState(serviceEvaluation, "ssipAnswerMap", ssipAnswers);

        Whitebox.invokeMethod(serviceEvaluation, "parseSsipDates");
        assertEquals(2015, serviceEvaluation.getYearOfLastSsipMemberAudit());
        assertEquals("11", serviceEvaluation.getMonthOfLastSsipMemberAudit());
        assertEquals("01", serviceEvaluation.getDayOfLastSsipMemberAudit());
    }

    @Test
    public void testParseSsipDates_SsipMembershipExpiration() throws Exception {
        Map<Integer, AuditData> ssipAnswers = createSsipAnswerMap();

        ssipAnswers.get(RegistrationServiceEvaluation.QUESTION_ID_SSIP_EXPIRATION_DATE).setAnswer("2015-01-31");
        Whitebox.setInternalState(serviceEvaluation, "ssipAnswerMap", ssipAnswers);

        Whitebox.invokeMethod(serviceEvaluation, "parseSsipDates");
        assertEquals(2015, serviceEvaluation.getYearOfSsipMembershipExpiration());
        assertEquals("01", serviceEvaluation.getMonthOfSsipMembershipExpiration());
        assertEquals("31", serviceEvaluation.getDayOfSsipMembershipExpiration());
    }

    @Test
    public void testDetermineReadyToProvideSsipDetails_Null() throws Exception {
        Map<Integer, AuditData> pqfAnswers = createPqfAnswerMap();
        Map<Integer, AuditData> ssipAnswers = createSsipAnswerMap();

        Whitebox.setInternalState(serviceEvaluation, "answerMap", pqfAnswers);
        Whitebox.setInternalState(serviceEvaluation, "ssipAnswerMap", ssipAnswers);

        Whitebox.invokeMethod(serviceEvaluation, "determineReadyToProvideSsipDetails");
        assertNull(serviceEvaluation.isReadyToProvideSsipDetails());
    }

    @Test
    public void testDetermineReadyToProvideSsipDetails_No() throws Exception {
        Map<Integer, AuditData> pqfAnswers = createPqfAnswerMap();
        Map<Integer, AuditData> ssipAnswers = createSsipAnswerMap();

        pqfAnswers.get(RegistrationServiceEvaluation.QUESTION_ID_REGISTERED_WITH_SSIP).setAnswer(YesNo.Yes.toString());

        Whitebox.setInternalState(serviceEvaluation, "answerMap", pqfAnswers);
        Whitebox.setInternalState(serviceEvaluation, "ssipAnswerMap", ssipAnswers);

        Whitebox.invokeMethod(serviceEvaluation, "determineReadyToProvideSsipDetails");
        assertEquals(YesNo.No.toString(), serviceEvaluation.isReadyToProvideSsipDetails());
    }

    @Test
    public void testDetermineReadyToProvideSsipDetails_Yes() throws Exception {
        Map<Integer, AuditData> pqfAnswers = createPqfAnswerMap();
        Map<Integer, AuditData> ssipAnswers = createSsipAnswerMap();

        ssipAnswers.get(RegistrationServiceEvaluation.QUESTION_ID_SSIP_AUDIT_DATE).setAnswer("2015-01-01");

        Whitebox.setInternalState(serviceEvaluation, "answerMap", pqfAnswers);
        Whitebox.setInternalState(serviceEvaluation, "ssipAnswerMap", ssipAnswers);

        Whitebox.invokeMethod(serviceEvaluation, "determineReadyToProvideSsipDetails");
        assertEquals(YesNo.Yes.toString(), serviceEvaluation.isReadyToProvideSsipDetails());
    }

    private Map<Integer, AuditData> createSsipAnswerMap() {
        Map<Integer, AuditData> map = new HashMap<>();
        AuditData data;

        data = EntityFactory.makeAuditData("", RegistrationServiceEvaluation.QUESTION_ID_SSIP_AUDIT_DATE);
        map.put(data.getQuestion().getId(), data);
        data = EntityFactory.makeAuditData("", RegistrationServiceEvaluation.QUESTION_ID_SSIP_EXPIRATION_DATE);
        map.put(data.getQuestion().getId(), data);
        data = EntityFactory.makeAuditData("", RegistrationServiceEvaluation.QUESTION_ID_SSIP_SCHEME);
        map.put(data.getQuestion().getId(), data);

        return map;
    }

    private Map<Integer, AuditData> createPqfAnswerMap() {
        Map<Integer, AuditData> map = new HashMap<>();
        AuditData data;

        data = EntityFactory.makeAuditData("", RegistrationServiceEvaluation.QUESTION_ID_REGISTERED_WITH_SSIP);
        map.put(data.getQuestion().getId(), data);

        return map;
    }

    @Test
    public void testNextStepListOnly() throws Exception {
        contractor.setMaterialSupplier(true);
        contractor.setOnsiteServices(false);
        contractor.setOffsiteServices(false);
        contractor.setTransportationServices(false);
        contractor.setSafetyRisk(LowMedHigh.Low);
        contractor.setProductRisk(LowMedHigh.Low);
        ContractorOperator conOp = EntityFactory.addContractorOperator(contractor, operator);

        when(operator.isAcceptsList()).thenReturn(true);
        when(operator.getStatus()).thenReturn(AccountStatus.Active);

        contractor.setAccountLevel(AccountLevel.Full);
        PicsTestUtil.forceSetPrivateField(serviceEvaluation, "contractor", contractor);
        Whitebox.invokeMethod(serviceEvaluation, "setAccountLevelByListOnlyEligibility");
        assertTrue(contractor.getAccountLevel().isListOnly());

        contractor.setAccountLevel(AccountLevel.ListOnly);
        PicsTestUtil.forceSetPrivateField(serviceEvaluation, "contractor", contractor);
        Whitebox.invokeMethod(serviceEvaluation, "setAccountLevelByListOnlyEligibility");
        assertTrue(contractor.getAccountLevel().isListOnly());

        contractor.setAccountLevel(AccountLevel.BidOnly);
        PicsTestUtil.forceSetPrivateField(serviceEvaluation, "contractor", contractor);
        Whitebox.invokeMethod(serviceEvaluation, "setAccountLevelByListOnlyEligibility");
        assertFalse(contractor.getAccountLevel().isListOnly());
    }

	@Test
	public void testNextStep_MaterialSupplier() throws Exception {
		contractor.setProductRisk(LowMedHigh.None);
		List<ContractorType> serviceTypes = new ArrayList<ContractorType>();
		serviceTypes.add(ContractorType.Supplier);
		contractor.setAccountTypes(serviceTypes);

		Map<Integer, AuditData> answerMap = new HashMap<Integer, AuditData>();
		answerMap.put(7679, EntityFactory.makeAuditData("High", 7679));

		PicsTestUtil.forceSetPrivateField(serviceEvaluation, "contractor", contractor);
		PicsTestUtil.forceSetPrivateField(serviceEvaluation, "answerMap", answerMap);

        doAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                ContractorAccount contractor = (ContractorAccount) args[0];
                contractor.setProductRisk(LowMedHigh.High);
                return "called with arguments: " + args;
            }
        }).when(serviceRiskCalculator).calculateContractorsRiskLevels(contractor, answerMap);

		Whitebox.invokeMethod(serviceEvaluation, "calculateRiskLevels");
		assertEquals("High", contractor.getProductRisk().toString());
	}

    @Test
    public void testNextStep_EmployeeGuardRulesEnabled() throws Exception {
        serviceEvaluation.setContractor(contractor);

        Whitebox.invokeMethod(serviceEvaluation, "runEmployeeGuardRules");

        verify(employeeGuardRulesService).runEmployeeGuardRules(contractor);
    }

    @Test
    public void testNextStep_EmployeeGuardRulesDisabled() throws Exception {
        togglzRule.disable(Features.USE_NEW_EMPLOYEE_GUARD_RULES);

        serviceEvaluation.setContractor(contractor);

        Whitebox.invokeMethod(serviceEvaluation, "runEmployeeGuardRules");

        verify(employeeGuardRulesService, never()).runEmployeeGuardRules(contractor);
    }

	@Test
	public void testValidateAnswers_MaterialSupplier() {
		contractor.setProductRisk(LowMedHigh.High);
		List<ContractorType> serviceTypes = new ArrayList<ContractorType>();
		serviceTypes.add(ContractorType.Supplier);
		contractor.setAccountTypes(serviceTypes);

		AuditCategory productCategory = new AuditCategory();
		productCategory.setId(1683);
		AuditCategory businessCategory = new AuditCategory();
		businessCategory.setId(1682);

		productCategory.getQuestions().add(createQuestion(7679));
		businessCategory.getQuestions().add(createQuestion(7660));
		businessCategory.getQuestions().add(createQuestion(7661));

		Map<Integer, AuditData> answerMap = new HashMap<Integer, AuditData>();
		answerMap.put(7679, EntityFactory.makeAuditData("High", 7679));
		answerMap.put(7660, EntityFactory.makeAuditData("Yes", 7660));
		answerMap.put(7661, EntityFactory.makeAuditData("Yes", 7661));

		when(dao.find(AuditCategory.class, 1683)).thenReturn(productCategory);
		when(dao.find(AuditCategory.class, 1682)).thenReturn(businessCategory);

		PicsTestUtil.forceSetPrivateField(serviceEvaluation, "answerMap", answerMap);
		PicsTestUtil.forceSetPrivateField(serviceEvaluation, "contractor", contractor);
		PicsTestUtil.forceSetPrivateField(serviceEvaluation, "dao", dao);

		boolean valid = serviceEvaluation.validatePqfAuditAnswers();
		assertTrue(valid);
	}

    @Test
    public void testShouldShowSsip() {
        AuditTypeRule excludeAll = AuditTypeRule.builder().exclude().build();

        setupShouldShowSsip(excludeAll);

        assertFalse(serviceEvaluation.shouldShowSsip());
    }

    @Test
    public void testShouldShowSsip_IncludeSsip() {
        AuditTypeRule excludeAll = AuditTypeRule.builder().exclude().build();
        AuditTypeRule includeSsip = AuditTypeRule.builder().include().auditType(AuditType.builder().id(AuditType.SSIP).build()).build();

        setupShouldShowSsip(excludeAll, includeSsip);

        assertTrue(serviceEvaluation.shouldShowSsip());
    }

    @Test
    public void testShouldShowSsip_ExcludeSssip() {
        AuditTypeRule excludeAll = AuditTypeRule.builder().exclude().build();
        AuditTypeRule excludeSsip = AuditTypeRule.builder().exclude().auditType(AuditType.builder().id(AuditType.SSIP).build()).build();

        setupShouldShowSsip(excludeAll, excludeSsip);

        assertFalse(serviceEvaluation.shouldShowSsip());
    }

    @Test
    public void testConTypesOk_NoOperatorSelectedTypes() {
        ArrayList<ContractorOperator> contractorOperators = new ArrayList<ContractorOperator>();
        ContractorOperator co = new ContractorOperator();
        co.setOperatorAccount(operator);
        contractorOperators.add(co);
        contractor.setOperators(contractorOperators);
        when(operator.isOperator()).thenReturn(true);
        when(operator.getStatus()).thenReturn(AccountStatus.Active);

        serviceEvaluation.setContractor(contractor);

        assertTrue(serviceEvaluation.conTypesOK());
    }

    private void setupShouldShowSsip(AuditTypeRule... rules) {
        PicsTestUtil.forceSetPrivateField(serviceEvaluation, "contractor", contractor);
        List<AuditTypeRule> listOfRules = Arrays.asList(rules);
        when(auditTypeRuleCache.getRules(contractor)).thenReturn(listOfRules);
    }

    private AuditQuestion createQuestion(int id) {
		AuditQuestion question = new AuditQuestion();
		question.setId(id);
		Calendar effective = Calendar.getInstance();
		effective.add(Calendar.YEAR, -10);
		question.setEffectiveDate(effective.getTime());
		Calendar expires = Calendar.getInstance();
		expires.add(Calendar.YEAR, 10);
		question.setExpirationDate(expires.getTime());

		return question;
	}
}
