package com.picsauditing.flagcalculator;

import com.picsauditing.flagcalculator.dao.FlagCalculatorDAO;
import com.picsauditing.flagcalculator.entities.*;
import com.picsauditing.flagcalculator.service.AuditService;
import com.picsauditing.flagcalculator.util.DateBean;
import org.apache.commons.lang.math.NumberUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = { "FlagDataCalculatorTest-context.xml" })
public class FlagDataCalculatorTest {

    private static final int ALBERTA_WCB_AUDIT_TYPE_ID = 145;
    private static final int OPERATOR_ID_FOR_CAOP = 11111;
    private static final int FAKE_CRITERIA_ID = 123;

    private FlagDataCalculator calculator;
    private FlagCriteriaContractor fcCon;
    private FlagCriteriaOperator fcOp;
    private FlagCriteria fc;
    List<FlagCriteriaContractor> conCrits;
    List<FlagCriteriaOperator> opCrits;
//    Map<AuditType, List<ContractorAuditOperator>> caoMap;
    private ContractorAccount contractor;
    private ContractorAudit ca;
    private OperatorAccount operator;
    private ContractorAuditOperator cao;
    private ContractorOperator co;
    private FlagCriteria lastYearCriteria;
    private FlagCriteria twoYearCriteria;
    private FlagCriteria threeYearCriteria;
    private FlagCriteria nullCriteria;
    private SimpleDateFormat dateAnswerFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Mock
    private FlagCalculatorDAO flagCalculatorDao;
    @Mock
    protected FlagCriteria multiCriteria;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        //super.setUp();

        contractor = EntityFactory.makeContractor();
        ca = EntityFactory.makeContractorAudit(1, contractor);
        operator = EntityFactory.makeOperator();
        cao = EntityFactory.makeContractorAuditOperator(ca);
        co = EntityFactory.addContractorOperator(contractor, operator);

        fc = new FlagCriteria();
        fc.setId(1);
        fc.setCategory(FlagCriteriaCategory.Safety);

        AuditQuestion question = EntityFactory.makeAuditQuestion();
        AuditService.getAuditType(question).setWorkFlow(EntityFactory.makeWorkflowWithSubmitted());

        fc.setQuestion(question);
        fc.setAuditType(EntityFactory.makeAuditType(1));
        fc.setComparison("=");
//        fc.setDataType(FlagCriteria.STRING);
        fc.setDefaultValue("Default");

        fcCon = new FlagCriteriaContractor();
        fcCon.setContractor(contractor);
        fcCon.setCriteria(fc);
        fcCon.setAnswer("Default");

        fcOp = new FlagCriteriaOperator();
        fcOp.setCriteria(fc);
        fcOp.setFlag(FlagColor.Green);

        conCrits = new ArrayList<>();
        conCrits.add(fcCon);

        opCrits = new ArrayList<>();
        opCrits.add(fcOp);

		/* Initialize the calculator */
        // calculator = new FlagDataCalculator(conCrits, flagCalculatorDao);
        calculator = new FlagDataCalculator(co.getId());
//        caoMap = null;

        lastYearCriteria = createFlagCriteria(1, MultiYearScope.LastYearOnly);
        twoYearCriteria = createFlagCriteria(2, MultiYearScope.TwoYearsAgo);
        threeYearCriteria = createFlagCriteria(3, MultiYearScope.ThreeYearsAgo);
        nullCriteria = createFlagCriteria(5, null);
    }

    @Test
    public void testIsFlaggableContractor_NoContractorCriteria() throws Exception {
        Map<FlagCriteria, FlagCriteriaContractor> contractorCriteria = new HashMap<>();
        Whitebox.setInternalState(calculator, "contractorCriteria", contractorCriteria);

        Boolean result = Whitebox.invokeMethod(calculator, "isFlaggableContractor");
        assertFalse(result);
    }

    @Test
    public void testIsFlaggableContractor_NoContractor() throws Exception {
        Map<FlagCriteria, FlagCriteriaContractor> contractorCriteria = makeContractorCriteria(null);

        Whitebox.setInternalState(calculator, "contractorCriteria", contractorCriteria);

        Boolean result = Whitebox.invokeMethod(calculator, "isFlaggableContractor");
        assertFalse(result);
    }

    @Test
    public void testIsFlaggableContractor_PendingContractor() throws Exception {
        Map<FlagCriteria, FlagCriteriaContractor> contractorCriteria = makeContractorCriteria(contractor);

        contractor.setStatus(AccountStatus.Pending);

        Whitebox.setInternalState(calculator, "contractorCriteria", contractorCriteria);

        Boolean result = Whitebox.invokeMethod(calculator, "isFlaggableContractor");
        assertFalse(result);
    }

    @Test
    public void testIsFlaggableContractor_DeactivatedContractor() throws Exception {
        Map<FlagCriteria, FlagCriteriaContractor> contractorCriteria = makeContractorCriteria(contractor);

        contractor.setStatus(AccountStatus.Deactivated);

        Whitebox.setInternalState(calculator, "contractorCriteria", contractorCriteria);

        Boolean result = Whitebox.invokeMethod(calculator, "isFlaggableContractor");
        assertFalse(result);
    }

    @Test
    public void testIsFlaggableContractor_ActiveContractor() throws Exception {
        Map<FlagCriteria, FlagCriteriaContractor> contractorCriteria = makeContractorCriteria(contractor);

        contractor.setStatus(AccountStatus.Active);

        Whitebox.setInternalState(calculator, "contractorCriteria", contractorCriteria);

        Boolean result = Whitebox.invokeMethod(calculator, "isFlaggableContractor");
        assertTrue(result);
    }

    private Map<FlagCriteria, FlagCriteriaContractor> makeContractorCriteria(ContractorAccount contractor) {
        Map<FlagCriteria, FlagCriteriaContractor> contractorCriteria = new HashMap<>();
        fc = createFlagCriteria(FAKE_CRITERIA_ID);
        fcCon = new FlagCriteriaContractor(contractor, fc, "answer");

        contractorCriteria.put(fc, fcCon);

        return contractorCriteria;
    }

    private FlagCriteria createFlagCriteria(int id) {
        FlagCriteria fc = new FlagCriteria();
        fc.setId(id);
        fc.setCategory(null);

        return fc;
    }

    @Test
    public void testAuditIsApplicableForThisOperator() throws Exception {
        Boolean applicable;

        calculator.setWorksForOperator(false);
        applicable = Whitebox.invokeMethod(calculator, "auditIsApplicableForThisOperator", fc, contractor);
        assertTrue(applicable);

        calculator.setWorksForOperator(true);
        fc.getQuestion().getCategory().setAuditType(ca.getAuditType());
        applicable = Whitebox.invokeMethod(calculator, "auditIsApplicableForThisOperator", fc, contractor);
        assertFalse(applicable);
    }

    @Test
    public void testAuditTypeHasMultiple() throws Exception {
        ContractorAccount contractor = EntityFactory.makeContractor();
        AuditType auditType = EntityFactory.makeAuditType();
        auditType.setHasMultiple(true);
        List<ContractorAudit> audits = makeContractorAudits(contractor, auditType);
        contractor.setAudits(audits);

        when(multiCriteria.getAuditType()).thenReturn(auditType);
        when(multiCriteria.getRequiredStatus()).thenReturn(AuditStatus.Resubmit);
        calculator.setOperator(audits.get(0).getOperators().get(0).getOperator());

        Boolean result;
        result = Whitebox.invokeMethod(calculator, "checkAuditStatus", multiCriteria, contractor);
        assertNotNull(result);
        assertTrue(result.booleanValue());

        audits.get(0).getOperators().get(0).setStatus(AuditStatus.Complete);
        result = Whitebox.invokeMethod(calculator, "checkAuditStatus", multiCriteria, contractor);
        assertNotNull(result);
        assertTrue(result.booleanValue());

        audits.get(1).getOperators().get(0).setStatus(AuditStatus.Complete);
        result = Whitebox.invokeMethod(calculator, "checkAuditStatus", multiCriteria, contractor);
        assertNotNull(result);
        assertFalse(result.booleanValue());
    }

    private List<ContractorAudit> makeContractorAudits(ContractorAccount contractor, AuditType auditType) {
        OperatorAccount operator = EntityFactory.makeOperator();

        List<ContractorAudit> audits = new ArrayList<>();
        ContractorAudit audit1 = createAudit(auditType, contractor);
        addCaoCaop(audit1, operator);
        audits.add(audit1);
        ContractorAudit audit2 = createAudit(auditType, contractor);
        addCaoCaop(audit2, operator);
        audits.add(audit2);
        return audits;
    }

    private ContractorAudit createAudit(AuditType auditType, ContractorAccount contractor) {
        ContractorAudit audit = EntityFactory.makeContractorAudit(auditType, contractor);
        contractor.getAudits().add(audit);

        return audit;
    }

    private void addCaoCaop(ContractorAudit audit, OperatorAccount operator) {
        ContractorAuditOperator cao = new ContractorAuditOperator();
        cao.setAudit(audit);
        cao.setOperator(operator);
        cao.setStatus(AuditStatus.Pending);

        ContractorAuditOperatorPermission caop = new ContractorAuditOperatorPermission();
//        caop.setCao(cao);
        caop.setOperator(operator);
        cao.getCaoPermissions().add(caop);

        audit.getOperators().add(cao);
    }

    @Test
    public void testClearFlags() throws Exception {
        calculator.setOperatorCriteria(opCrits);

        contractor.setStatus(AccountStatus.Pending);
        List<FlagData> list = calculator.calculate();
        assertTrue(list.size() == 0);

        contractor.setStatus(AccountStatus.Requested);
        list = calculator.calculate();
        assertTrue(list.size() == 0);

        contractor.setStatus(AccountStatus.Declined);
        list = calculator.calculate();
        assertTrue(list.size() == 0);
    }

    @Test
    public void testFlagDataOverrideAdjustment_noYearNoFdo() throws Exception {
        Map<FlagCriteria, List<FlagDataOverride>> overrides = new HashMap<>();
        FlagDataOverride override = null;

        createCorrespondingCriteriaLists();
        createContractorAnswers();

        ArrayList<FlagCriteria> criteriaList = new ArrayList<>();
        criteriaList.add(lastYearCriteria);
        criteriaList.add(twoYearCriteria);
        criteriaList.add(threeYearCriteria);
        when(flagCalculatorDao.findWhere(Matchers.anyString())).thenReturn(criteriaList);

        overrides.clear();
        Whitebox.setInternalState(calculator, "overrides", overrides);
        override = Whitebox.invokeMethod(calculator, "hasForceDataFlag", nullCriteria, operator);
        assertNull(override);
    }

    @Test
    @Ignore
    public void testFlagDataOverrideAdjustment_noYearFdo() throws Exception {
        Map<FlagCriteria, List<FlagDataOverride>> overrides = new HashMap<>();
        FlagDataOverride override = null;

        createCorrespondingCriteriaLists();
        createContractorAnswers();

        ArrayList<FlagCriteria> criteriaList = new ArrayList<>();
        criteriaList.add(lastYearCriteria);
        criteriaList.add(twoYearCriteria);
        criteriaList.add(threeYearCriteria);
        when(flagCalculatorDao.findWhere(Matchers.anyString())).thenReturn(criteriaList);

        overrides.clear();
        addFlagDataOverride(overrides, nullCriteria, "blah");
        calculator.setOverrides(overrides);
        override = Whitebox.invokeMethod(calculator, "hasForceDataFlag", nullCriteria, operator);
        assertNotNull(override);
    }

    @Test
    @Ignore
    public void testFlagDataOverrideAdjustment() throws Exception {
        Map<FlagCriteria, List<FlagDataOverride>> overrides = new HashMap<>();
        FlagDataOverride override = null;

        createCorrespondingCriteriaLists();
        createContractorAnswers();

        ArrayList<FlagCriteria> criteriaList = new ArrayList<>();
        criteriaList.add(lastYearCriteria);
        criteriaList.add(twoYearCriteria);
        criteriaList.add(threeYearCriteria);
        when(flagCalculatorDao.findWhere(Matchers.anyString())).thenReturn(criteriaList);

        // now do year criteria
        // no fdo
        overrides.clear();
        Whitebox.setInternalState(calculator, "overrides", overrides);
        override = Whitebox.invokeMethod(calculator, "hasForceDataFlag", lastYearCriteria, operator);
        assertNull(override);

        // override for current year, no adjustments - this is the normal case
        overrides.clear();
        addFlagDataOverride(overrides, lastYearCriteria, "2012");
        Whitebox.setInternalState(calculator, "overrides", overrides);
        override = Whitebox.invokeMethod(calculator, "hasForceDataFlag", lastYearCriteria, operator);
        assertNotNull(override);

        // new year adjustments
        // single fdo for 2011, push to 2 year
        overrides.clear();
        addFlagDataOverride(overrides, lastYearCriteria, "2011");
        Whitebox.setInternalState(calculator, "overrides", overrides);
        override = Whitebox.invokeMethod(calculator, "hasForceDataFlag", lastYearCriteria, operator);
        assertNull(override);
        assertNotNull(overrides.get(twoYearCriteria));
        assertNotNull(overrides.get(twoYearCriteria).get(0));

        // 2 fdo for 2011 last and 2010 two years; 2011 being retrieved and moved to two years
        overrides.clear();
        addFlagDataOverride(overrides, lastYearCriteria, "2011");
        addFlagDataOverride(overrides, twoYearCriteria, "2010");
        Whitebox.setInternalState(calculator, "overrides", overrides);
        override = Whitebox.invokeMethod(calculator, "hasForceDataFlag", twoYearCriteria, operator);
        assertNotNull(override);
        assertEquals(override.getCriteria(), twoYearCriteria);

        // 2 fdo for 2011 last and 2010 two years; 2010 and then 2011
        overrides.clear();
        addFlagDataOverride(overrides, lastYearCriteria, "2011");
        addFlagDataOverride(overrides, twoYearCriteria, "2010");
        Whitebox.setInternalState(calculator, "overrides", overrides);
        override = Whitebox.invokeMethod(calculator, "hasForceDataFlag", threeYearCriteria, operator);
        assertNotNull(override);
        assertEquals(override.getCriteria(), threeYearCriteria);
        override = Whitebox.invokeMethod(calculator, "hasForceDataFlag", twoYearCriteria, operator);
        assertNotNull(override);
        assertEquals(override.getCriteria().getMultiYearScope(), MultiYearScope.TwoYearsAgo);

        // three deleted in/out of order
        overrides.clear();
        addFlagDataOverride(overrides, lastYearCriteria, "2011");
        addFlagDataOverride(overrides, twoYearCriteria, "2010");
        addFlagDataOverride(overrides, threeYearCriteria, "2009");
        Whitebox.setInternalState(calculator, "overrides", overrides);
        override = Whitebox.invokeMethod(calculator, "hasForceDataFlag", twoYearCriteria, operator);
        assertNotNull(override);
        assertEquals(override.getCriteria(), twoYearCriteria);
    }

//    private MultiYearScope getScope(Map<FlagCriteria, List<FlagDataOverride>> overrides, FlagCriteria criteria) {
//        return overrides.get(criteria).get(0).getCriteria().getMultiYearScope();
//    }
//
    private void createContractorAnswers() {
        Map<FlagCriteria, FlagCriteriaContractor> contractorCriteria = new HashMap<FlagCriteria, FlagCriteriaContractor>();
        addFlagCriteriaContrtactor(contractorCriteria, lastYearCriteria, "2012");
        addFlagCriteriaContrtactor(contractorCriteria, twoYearCriteria, "2011");
        addFlagCriteriaContrtactor(contractorCriteria, threeYearCriteria, "2010");
        addFlagCriteriaContrtactor(contractorCriteria, nullCriteria, null);

        Whitebox.setInternalState(calculator, "contractorCriteria", contractorCriteria);
    }

    private void createCorrespondingCriteriaLists() {
        Map<Integer, List<Integer>> correspondingMultiYearCriteria = new HashMap<Integer, List<Integer>>();
        ArrayList<Integer> ids = new ArrayList<Integer>();
        ids.add(1);
        ids.add(2);
        ids.add(3);
        correspondingMultiYearCriteria.put(1, ids);
        correspondingMultiYearCriteria.put(2, ids);
        correspondingMultiYearCriteria.put(3, ids);
        Whitebox.setInternalState(calculator, "correspondingMultiYearCriteria", correspondingMultiYearCriteria);
    }

    private void addFlagDataOverride(
            Map<FlagCriteria, List<FlagDataOverride>> overrides,
            FlagCriteria criteria, String year) {
        ArrayList<FlagDataOverride> fdos = new ArrayList<FlagDataOverride>();
        FlagDataOverride fdo = new FlagDataOverride();
        fdo.setCriteria(criteria);
        fdo.setYear(year);
        fdo.setOperator(operator);
        Calendar date = Calendar.getInstance();
        date.add(Calendar.YEAR, 1);
        fdo.setForceEnd(date.getTime());

        fdos.add(fdo);
        overrides.put(criteria, fdos);
    }

    private void addFlagCriteriaContrtactor(Map<FlagCriteria, FlagCriteriaContractor> contractorCriteria, FlagCriteria criteria, String answer2) {
        FlagCriteriaContractor fcc = new FlagCriteriaContractor();
        fcc.setCriteria(criteria);
        fcc.setAnswer2(answer2);

        contractorCriteria.put(criteria, fcc);
    }

    private FlagCriteria createFlagCriteria(int id, MultiYearScope scope) {
        FlagCriteria fc = new FlagCriteria();
        fc.setId(id);
//        fc.setMultiYearScope(scope);
        fc.setCategory(null);

        return fc;
    }

    @Test
    public void testFlagCAO_noRequiredStatus() throws Exception {
        Boolean flagCAO = Whitebox.invokeMethod(calculator, "flagCAO", fc, cao);
        assertTrue("if the criteria has no required status, flagCAO should be true", flagCAO);
    }

    @Test
    public void testFlagCAO_CaoStatusAfterRequiredStatus() throws Exception {
        Boolean flagCAO;
        fc.setRequiredStatus(AuditStatus.Submitted);
        // cao.changeStatus is doing db interaction and permission testing which is not
        // relevant to this test.... so.... violate encapsulation.
        Whitebox.setInternalState(cao, "status", AuditStatus.Complete);

        fc.setRequiredStatusComparison(">");
        flagCAO = Whitebox.invokeMethod(calculator, "flagCAO", fc, cao);
        assertFalse("compare is '>', cao status is after criteria required status, flagCAO should be false", flagCAO);

        fc.setRequiredStatusComparison("=");
        flagCAO = Whitebox.invokeMethod(calculator, "flagCAO", fc, cao);
        assertTrue("compare is '=', cao status is after criteria required status, flagCAO should be true", flagCAO);

        fc.setRequiredStatusComparison("!=");
        flagCAO = Whitebox.invokeMethod(calculator, "flagCAO", fc, cao);
        assertFalse("compare is '!=', cao status is after criteria required status, flagCAO should be false", flagCAO);

        fc.setRequiredStatusComparison("<");
        flagCAO = Whitebox.invokeMethod(calculator, "flagCAO", fc, cao);
        assertTrue("compare is '<', cao status is after criteria required status, flagCAO should be true", flagCAO);

        fc.setRequiredStatusComparison("");
        flagCAO = Whitebox.invokeMethod(calculator, "flagCAO", fc, cao);
        assertTrue("compare is default (blank), cao status is after criteria required status, flagCAO should be true", flagCAO);

    }

    @Test
    public void testIsAuditVisibleToOperator_noCAOs() throws Exception {
        Boolean isAuditVisible = Whitebox.invokeMethod(calculator, "isAuditVisibleToOperator", ca, operator);
        assertFalse("with no operators, the audit should not be visible", isAuditVisible);
    }

    @Test
    public void testIsAuditVisibleToOperator_caoNotVisible() throws Exception {
        cao.setVisible(false);
        List<ContractorAuditOperator> operators = new ArrayList<ContractorAuditOperator>();
        operators.add(cao);
        ca.setOperators(operators);
        Boolean isAuditVisible = Whitebox.invokeMethod(calculator, "isAuditVisibleToOperator", ca, operator);
        assertFalse("if the cao is not visible, the audit should not be visible", isAuditVisible);
    }

//    @Test
//    public void testStatisticsValid() throws Exception {
//        ContractorAccount con = EntityFactory.makeContractor();
//        ContractorAudit audit = EntityFactory.makeAnnualUpdate(AuditType.ANNUALADDENDUM, con, "2012");
//        con.getAudits().add(audit);
//        Calendar date = Calendar.getInstance();
//        audit.setCreationDate(date.getTime());
//        date.add(Calendar.YEAR, 1);
//        audit.setExpiresDate(date.getTime());
//
//        OperatorAccount op1 = EntityFactory.makeOperator();
//        OperatorAccount op2 = EntityFactory.makeOperator();
//
//        ContractorAuditOperator cao = EntityFactory.addCao(audit, op1);
//        ContractorAuditOperatorPermission caop = new ContractorAuditOperatorPermission();
//        caop.setOperator(op1);
//        cao.getCaoPermissions().add(caop);
//
//        Boolean isValid = Whitebox.invokeMethod(calculator, "isStatisticValidForOperator", op1, con);
//        assertTrue("statistics are valid for operators on annual updates", isValid);
//
//        isValid = Whitebox.invokeMethod(calculator, "isStatisticValidForOperator", op2, con);
//        assertFalse("statistics are not valid for operators not on annual updates", isValid);
//    }
//
    @Test
    public void testIsAuditVisibleToOperator_caoNoPermissions() throws Exception {
        cao.setVisible(true);
        List<ContractorAuditOperator> operators = new ArrayList<ContractorAuditOperator>();
        operators.add(cao);
        ca.setOperators(operators);
        Boolean isAuditVisible = Whitebox.invokeMethod(calculator, "isAuditVisibleToOperator", ca, operator);
        assertFalse("if the cao has no permissions, the audit should not be visible", isAuditVisible);
    }

    @Test
    public void testIsAuditVisibleToOperator_caoWrongPermissions() throws Exception {
        ContractorAuditOperatorPermission caop = new ContractorAuditOperatorPermission();
        OperatorAccount anotherOp = EntityFactory.makeOperator();
        caop.setOperator(anotherOp);
        List<ContractorAuditOperatorPermission> caoPermissions = new ArrayList<ContractorAuditOperatorPermission>();
        caoPermissions.add(caop);
        cao.setCaoPermissions(caoPermissions);
        cao.setVisible(true);
        List<ContractorAuditOperator> operators = new ArrayList<ContractorAuditOperator>();
        operators.add(cao);
        ca.setOperators(operators);
        Boolean isAuditVisible = Whitebox.invokeMethod(calculator, "isAuditVisibleToOperator", ca, operator);
        assertFalse("if the cao has wrong permissions, the audit should not be visible", isAuditVisible);
    }

    @Test
    public void testIsAuditVisibleToOperator_caoOneWrongOneRightPermissions() throws Exception {
        List<ContractorAuditOperatorPermission> caoPermissions = new ArrayList<ContractorAuditOperatorPermission>();

        ContractorAuditOperatorPermission caop1 = new ContractorAuditOperatorPermission();
        caop1.setOperator(operator);
        caoPermissions.add(caop1);

        ContractorAuditOperatorPermission caop = new ContractorAuditOperatorPermission();
        OperatorAccount anotherOp = EntityFactory.makeOperator();
        caop.setOperator(anotherOp);
        caoPermissions.add(caop);

        cao.setCaoPermissions(caoPermissions);
        cao.setVisible(true);
        List<ContractorAuditOperator> operators = new ArrayList<ContractorAuditOperator>();
        operators.add(cao);
        ca.setOperators(operators);
        Boolean isAuditVisible = Whitebox.invokeMethod(calculator, "isAuditVisibleToOperator", ca, operator);
        assertTrue("if the cao has correct permissions, the audit should be visible", isAuditVisible);
    }

    @Test
    public void testGreen() {
        assertNull(getSingle()); // Green flags are ignored
    }

//    public void testDataTypes() {
//        fc.setDataType(FlagCriteria.NUMBER);
//        fc.setDefaultValue("3.5");
//        fcCon.setAnswer("3.5");
//        assertNull(getSingle()); // Green
//
//        fc.setDataType(FlagCriteria.BOOLEAN);
//        fc.setDefaultValue("true");
//        fcCon.setAnswer("true");
//        assertNull(getSingle()); // Green
//
//        fc.setDataType(FlagCriteria.STRING);
//        fc.setDefaultValue("String");
//        fcCon.setAnswer("String");
//        assertNull(getSingle()); // Green
//
//        fc.setDataType(FlagCriteria.DATE);
//        fc.setDefaultValue("2010-01-01");
//        fc.setComparison(">");
//        fcCon.setAnswer("2010-01-02");
//        assertNull(getSingle()); // Green
//    }
//
//    public void testMatch() {
//        // See if criteria IDs match
//        boolean caughtException = false;
//
//        try {
//            getSingle();
//        } catch (Exception e) {
//            caughtException = true;
//        }
//
//        assertFalse(caughtException);
//
//        FlagCriteria fcTemp = new FlagCriteria();
//        fcTemp.setId(5);
//        fcCon.setCriteria(fcTemp);
//
//        // Criterias don't match, return nothing
//        assertNull(getSingle());
//    }
//
//    @Test
//    public void testInsuranceCriteria() {
//        FlagDataCalculator calculator = setupInsuranceCriteria("10", "5");
//        Whitebox.setInternalState(calculator, "flagCalculatorDao", flagCalculatorDao);
//        Whitebox.setInternalState(calculator, "dao", dao);
//
//        List<FlagData> list = calculator.calculate();
//
//        assertTrue(list.size() == 1);
//        assertTrue(list.get(0).getFlag().equals(FlagColor.Red));
//    }
//
//    @Test
//    public void testInsuranceCriteria_rulesBasedInsuranceCriteria() {
//        FlagDataCalculator calculator = setupRulesBasedInsuranceCriteria("10", "5");
//        Whitebox.setInternalState(calculator, "flagCalculatorDao", flagCalculatorDao);
//        Whitebox.setInternalState(calculator, "dao", dao);
//
//        when(featureToggle.isFeatureEnabled(FeatureToggle.TOGGLE_RULES_BASED_INSURANCE_CRITERIA)).thenReturn(true);
//
//        List<FlagData> list = calculator.calculate();
//
//        assertTrue(list.size() == 1);
//        assertTrue(list.get(0).getFlag().equals(FlagColor.Red));
//    }
//
//    @Test
//    public void testInsuranceCriteria_rulesBasedInsuranceCriteriaGreen() {
//        FlagDataCalculator calculator = setupRulesBasedInsuranceCriteria("10", "10");
//        Whitebox.setInternalState(calculator, "flagCalculatorDao", flagCalculatorDao);
//        Whitebox.setInternalState(calculator, "dao", dao);
//
//        when(featureToggle.isFeatureEnabled(FeatureToggle.TOGGLE_RULES_BASED_INSURANCE_CRITERIA)).thenReturn(true);
//
//        List<FlagData> list = calculator.calculate();
//
//        assertTrue(list.size() == 1);
//        assertTrue(list.get(0).getFlag().equals(FlagColor.Green));
//    }
//
//    private FlagDataCalculator setupRulesBasedInsuranceCriteria(String criteriaLimit, String rbicLimit) {
//        FlagDataCalculator flagDataCalculator = setupInsuranceCriteria(criteriaLimit, rbicLimit);
//
//        List<InsuranceCriteriaContractorOperator> calculatedCriteria = Arrays.asList(
//                InsuranceCriteriaContractorOperator.builder()
//                        .operator(operator)
//                        .contractor(contractor)
//                        .criteria(fc)
//                        .limit(Integer.valueOf(criteriaLimit))
//                        .build()
//        );
//
//        contractor.setInsuranceCriteriaContractorOperators(calculatedCriteria);
//
//        return flagDataCalculator;
//    }
//
//
//    private FlagDataCalculator setupInsuranceCriteria(String criteriaLimit, String actualLimit) {
//        AuditType auditType = AuditType.builder().id(5).build();
//        FlagCriteria fc = EntityFactory.makeFlagCriteriaAuditQuestion(auditType);
//        fc.setInsurance(true);
//        fc.setCategory(FlagCriteriaCategory.InsuranceCriteria);
//        fc.setComparison("<");
//        fc.setDataType("number");
//        fc.setAllowCustomValue(true);
//        fc.setDefaultValue(criteriaLimit);
//        fc.setOptionCode(FlagCriteriaOptionCode.ExcessEachOccurrence);
//        fc.setRequiredStatus(AuditStatus.Submitted);
//
//        FlagCriteriaContractor fcc = EntityFactory.makeFlagCriteriaContractor(actualLimit);
//        fcc.setContractor(contractor);
//        fcc.setCriteria(fc);
//
//        ContractorAudit contractorAudit = ContractorAudit.builder().auditType(auditType).build();
//        addCaoAndCaopToAudit(contractorAudit, operator);
//        contractor.setAudits(Arrays.asList(contractorAudit));
//
//        FlagCriteriaOperator fco = EntityFactory.makeFlagCriteriaOperator(criteriaLimit);
//        fco.setOperator(operator);
//        fco.setCriteria(fc);
//
//        calculator = new FlagDataCalculator(fcc, fco);
//        calculator.setOperator(operator);
//
//        return calculator;
//    }
//
//    @Test
//    public void testFindRulesBasedInsuranceCriteriaLimit() throws Exception {
//        FlagCriteria criteria = FlagCriteria.builder().id(5).build();
//        FlagCriteriaOperator fco = setupFindRulesBasedInsuranceCriteriaLimit(criteria);
//
//        List<InsuranceCriteriaContractorOperator> calculatedCriteria = Arrays.asList(
//                InsuranceCriteriaContractorOperator.builder()
//                        .operator(operator)
//                        .contractor(contractor)
//                        .criteria(criteria)
//                        .limit(10)
//                        .build()
//        );
//
//        contractor.setInsuranceCriteriaContractorOperators(calculatedCriteria);
//
//        assertEquals("10",
//                Whitebox.invokeMethod(
//                        calculator,
//                        FlagDataCalculator.class,
//                        "findRulesBasedInsuranceCriteriaLimit",
//                        contractor,
//                        fco)
//        );
//    }
//
//    @Test
//    public void testFindRulesBasedInsuranceCriteriaLimit_CriteriaNotFound() throws Exception {
//        FlagCriteria criteria = FlagCriteria.builder().id(5).build();
//        FlagCriteriaOperator fco = setupFindRulesBasedInsuranceCriteriaLimit(criteria);
//
//        List<InsuranceCriteriaContractorOperator> calculatedCriteria = Arrays.asList(
//                InsuranceCriteriaContractorOperator.builder()
//                        .operator(operator)
//                        .contractor(contractor)
//                        .criteria(FlagCriteria.builder().build())
//                        .limit(10)
//                        .build()
//        );
//
//        contractor.setInsuranceCriteriaContractorOperators(calculatedCriteria);
//
//        assertEquals(null,
//                Whitebox.invokeMethod(
//                        calculator,
//                        FlagDataCalculator.class,
//                        "findRulesBasedInsuranceCriteriaLimit",
//                        contractor,
//                        fco)
//        );
//    }
//
//    private FlagCriteriaOperator setupFindRulesBasedInsuranceCriteriaLimit(FlagCriteria criteria) {
//        FlagCriteriaContractor fcc = FlagCriteriaContractor.builder().contractor(contractor).criteria(criteria).build();
//        FlagCriteriaOperator fco = FlagCriteriaOperator.builder().operator(operator).criteria(criteria).build();
//        calculator = new FlagDataCalculator(fcc, fco);
//        return fco;
//    }
//    // TODO get these tests to pass...need to get Bamboo running now so I'm skipping this for now
//	/*
//	public void testDifferentConAnswer() throws Exception {
//		fcCon.setAnswer("Custom");
//		assertEquals(FlagColor.Red, getSingle().getFlag());
//
//		fcCon.setAnswer("12345");
//		assertEquals(FlagColor.Red, getSingle().getFlag());
//	}
//
    @Test
	public void testCustomFlag() {
        setupStringTest();
        fcOp.setHurdle("Hurdle");
        fcOp.setFlag(FlagColor.Amber);
        fcCon.setAnswer(fc.getDefaultValue());
		assertEquals(FlagColor.Amber, getSingle().getFlagColor());
	}

    @Test
	public void testCustomValue() throws Exception {
        setupStringTest();
		fc.setAllowCustomValue(true);
        fcCon.setAnswer("Hurdle");
		fcOp.setHurdle("Hurdle");
		assertEquals(FlagColor.Red, getSingle().getFlagColor());
	}

//	public void testComparison() {
//		fc.setDataType(FlagCriteria.NUMBER);
//		fc.setComparison(">");
//		fc.setDefaultValue("2");
//		fcCon.setAnswer("3");
//		assertNull(getSingle()); // Green
//
//		fc.setComparison("<=");
//		assertEquals(FlagColor.Red, getSingle().getFlag());
//
//		fc.setAllowCustomValue(true);
//		fcOp.setHurdle("3");
//		assertNull(getSingle()); // Green
//
//		fc.setDataType(FlagCriteria.STRING);
//		fc.setComparison("=");
//		fc.setAllowCustomValue(false);
//		fc.setDefaultValue("Answer");
//		fcCon.setAnswer("Answer");
//		assertNull(getSingle()); // Green
//	}
//
    @Test
    public void testOsha_LwcrNaics() {
        setupOshaTest();
        fc.setOshaRateType(OshaRateType.LwcrNaics);
        fcCon.setAnswer("5.0");
        assertNotNull(getSingle());
        assertEquals(FlagColor.Red, getSingle().getFlagColor());
    }

    @Test
    public void testTag_ContractorNotTagged() {
        setupOshaTest();
        setupTag();
        fc.setOshaRateType(OshaRateType.TrirNaics);
        fcCon.setAnswer("5.0");
        assertNull(getSingle());
    }

    private void setupTag() {
        OperatorTag tag = new OperatorTag();
        tag.setId(1);
        fcOp.setTag(tag);
     }

    @Test
    public void testOsha_TrirNaics() {
        setupOshaTest();
        fc.setOshaRateType(OshaRateType.TrirNaics);
        fcCon.setAnswer("5.0");
        assertNotNull(getSingle());
        assertEquals(FlagColor.Red, getSingle().getFlagColor());
    }

    @Test
    public void testOsha_Dart() {
        setupOshaTest();
        fc.setOshaRateType(OshaRateType.DartNaics);
        fcCon.setAnswer("5.0");
        assertNotNull(getSingle());
        assertEquals(FlagColor.Red, getSingle().getFlagColor());
    }

    private void setupOshaTest() {
        fc.setDataType(FlagCriteria.NUMBER);
        fc.setComparison("=");
        fc.setDefaultValue("5.55");
        fc.setAuditType(null);
        fc.setRequiredStatus(null);
        fcOp.setFlag(FlagColor.Red);
        when(flagCalculatorDao.getDartIndustryAverage(any(Naics.class))).thenReturn(0f);
    }

    @Test
	public void testBoolean_True() {
        setupBooleanTest();
		fcCon.setAnswer("true");
        assertNotNull(getSingle());
        assertEquals(FlagColor.Red, getSingle().getFlagColor());
	}

    @Test
    public void testBoolean_False() {
        setupBooleanTest();
        fcCon.setAnswer("false");
        assertNotNull(getSingle());
        assertEquals(FlagColor.Green, getSingle().getFlagColor());
    }

    private void setupBooleanTest() {
        fc.setDataType(FlagCriteria.BOOLEAN);
        fc.setDefaultValue("true");
        fc.setAuditType(null);
        fc.setRequiredStatus(null);
        fcOp.setFlag(FlagColor.Red);
    }

    @Test
	public void testNumber_GreaterThan() {
        setupNumberTest();
		fc.setComparison(">");
		fcCon.setAnswer("6.78");
        assertNotNull(getSingle());
        assertEquals(FlagColor.Red, getSingle().getFlagColor());
	}

    @Test
    public void testNumber_GreaterThanEquals() {
        setupNumberTest();
        fc.setComparison(">=");
        fcCon.setAnswer("6.78");
        assertNotNull(getSingle());
        assertEquals(FlagColor.Red, getSingle().getFlagColor());

        fcCon.setAnswer("5.55");
        assertNotNull(getSingle());
        assertEquals(FlagColor.Red, getSingle().getFlagColor());
    }

    @Test
    public void testNumber_LessThan() {
        setupNumberTest();
        fc.setComparison("<");
        fcCon.setAnswer("4.56");
        assertNotNull(getSingle());
        assertEquals(FlagColor.Red, getSingle().getFlagColor());
    }

    @Test
    public void testNumber_LessThanEquals() {
        setupNumberTest();
        fc.setComparison("<=");
        fcCon.setAnswer("4.56");
        assertNotNull(getSingle());
        assertEquals(FlagColor.Red, getSingle().getFlagColor());

        fcCon.setAnswer("5.55");
        assertNotNull(getSingle());
        assertEquals(FlagColor.Red, getSingle().getFlagColor());
    }

    @Test
    public void testNumber_Equals() {
        setupNumberTest();
        fc.setComparison("=");
        fcCon.setAnswer("5.55");
        assertNotNull(getSingle());
        assertEquals(FlagColor.Red, getSingle().getFlagColor());
    }

    @Test
    public void testNumber_NotEquals() {
        setupNumberTest();
        fc.setComparison("!=");
        fcCon.setAnswer("4.55");
        assertNotNull(getSingle());
        assertEquals(FlagColor.Red, getSingle().getFlagColor());
    }

    private void setupNumberTest() {
        fc.setDataType(FlagCriteria.NUMBER);
        fc.setDefaultValue("5.55");
        fc.setAuditType(null);
        fc.setRequiredStatus(null);
        fcOp.setFlag(FlagColor.Red);
    }

    @Test
	public void testString_DefaultEquals() {
        setupStringTest();
        fcCon.setAnswer("New String");
		assertNotNull(getSingle());
        assertEquals(FlagColor.Red, getSingle().getFlagColor());
	}

    @Test
    public void testString_Contains() {
        setupStringTest();
        fc.setComparison("contains");
        fcCon.setAnswer("blah New String blah");
        assertNotNull(getSingle());
        assertEquals(FlagColor.Red, getSingle().getFlagColor());
    }

    @Test
    public void testString_NotEmpty() {
        setupStringTest();
        fc.setComparison("NOT EMPTY");
        fcCon.setAnswer("New String");
        assertNotNull(getSingle());
        assertEquals(FlagColor.Green, getSingle().getFlagColor()); // TODO The comparison looks wrong in flag data calculator
    }

    private void setupStringTest() {
        fc.setDefaultValue("New String");
        fc.setAuditType(null);
        fc.setRequiredStatus(null);
        fcOp.setFlag(FlagColor.Red);
    }

    @Test
	public void testDates_Default() {
        setupDatesTest();
        fcCon.setAnswer("2010-01-01");
		assertNotNull(getSingle());
	}

    @Test
    public void testDates_GreaterThan() {
        setupDatesTest();

        fc.setComparison(">");
		fcCon.setAnswer("2011-02-02");
		assertNotNull(getSingle());
        assertEquals(FlagColor.Red, getSingle().getFlagColor());
    }

    @Test
    public void testDates_LessThan() {
        setupDatesTest();

        fc.setComparison("<");
        fcCon.setAnswer("2009-12-12");
        assertNotNull(getSingle());
        assertEquals(FlagColor.Red, getSingle().getFlagColor());
    }

    private void setupDatesTest() {
        fc.setDataType(FlagCriteria.DATE);
        fc.setDefaultValue("2010-01-01");
        fc.setAuditType(null);
        fc.setRequiredStatus(null);
        fcOp.setFlag(FlagColor.Red);
    }

    //	public void testPolicy() {
//		// Create a map of <Integer, List<CAO>>
//		AuditType at = new AuditType();
//		at.setId(5);
//		at.setClassType(AuditTypeClass.Policy);
//		ContractorAudit ca = new ContractorAudit();
//		ca.setAuditType(at);
//		fc.setAuditType(at);
//		caoMap = new HashMap<AuditType, List<ContractorAuditOperator>>();
//		ContractorAuditOperator cao = new ContractorAuditOperator();
//		cao.changeStatus(AuditStatus.Pending, null);
//		cao.setAudit(ca);
//		List<ContractorAuditOperator> caoList = new ArrayList<ContractorAuditOperator>();
//		caoList.add(cao);
//		caoMap.put(at, caoList);
//
//		assertEquals(FlagColor.Red, getSingle().getFlagColor());
//
//		caoMap = new HashMap<AuditType, List<ContractorAuditOperator>>();
//		cao = new ContractorAuditOperator();
//		cao.changeStatus(AuditStatus.Approved, null);
//		cao.setAudit(ca);
//		caoList = new ArrayList<ContractorAuditOperator>();
//		caoList.add(cao);
//		caoMap.put(at, caoList);
//
//		assertNull(getSingle()); // Green
//	}
//	*/
//
    private FlagData getSingle() {
        conCrits.set(0, fcCon);
        opCrits.set(0, fcOp);

        // calculator.setCaoMap(caoMap);
        calculator.setOperator(fcOp.getOperator());
        calculator.setOperatorCriteria(opCrits);

        List<FlagData> data = calculator.calculate();

        if (data.size() > 0)
            return data.get(0);
        else
            return null;
    }

//    @Test
//    public void testIsInsuranceCriteria() throws Exception {
//        Boolean isInsuranceCriteria;
//        FlagData flagData = EntityFactory.makeFlagData();;
//        FlagCriteria criteria = EntityFactory.makeFlagCriteria();
//        AuditQuestion question = EntityFactory.makeAuditQuestion();
//        AuditType generalLiability = EntityFactory.makeAuditType(13);
//        AuditType pqf = EntityFactory.makeAuditType(1);
//        flagData.setCriteria(criteria);
//
//        // non insurance
//        criteria.setInsurance(false);
//        isInsuranceCriteria = Whitebox.invokeMethod(calculator, "isInsuranceCriteria", flagData, generalLiability);
//        assertFalse(isInsuranceCriteria);
//
//        // insurance
//        criteria.setInsurance(true);
//        isInsuranceCriteria = Whitebox.invokeMethod(calculator, "isInsuranceCriteria", flagData, generalLiability);
//        assertTrue(isInsuranceCriteria);
//    }
//
    @Test
    public void testIsFlagged_WCB_DoesNotHaveApplicableCAOP() throws Exception {
        ContractorAccount contractor = buildFakeContractorAccountWithWCBs(AuditStatus.NotApplicable);
        FlagCriteria flagCriteria = buildFakeFlagCriteria();
        FlagCriteriaOperator operatorFlagCriteria = buildFakeFlagCriteriaOperator(flagCriteria);
        FlagCriteriaContractor contractorFlagCriteria = buildFakeFlagCriteriaContractor(flagCriteria, contractor);

        OperatorAccount operator = EntityFactory.makeOperator();
        operator.setId(123);
        calculator.setOperator(operator);

        Boolean result = Whitebox.invokeMethod(calculator, "isFlagged", operatorFlagCriteria, contractorFlagCriteria);

        assertNull(result);
    }

    @Test
    public void testIsFlagged_WCB_GreenFlagged() throws Exception {
        ContractorAccount contractor = buildFakeContractorAccountWithWCBs(AuditStatus.Approved);
        FlagCriteria flagCriteria = buildFakeFlagCriteria();
        FlagCriteriaOperator operatorFlagCriteria = buildFakeFlagCriteriaOperator(flagCriteria);
        FlagCriteriaContractor contractorFlagCriteria = buildFakeFlagCriteriaContractor(flagCriteria, contractor);

        OperatorAccount operator = EntityFactory.makeOperator();
        operator.setId(OPERATOR_ID_FOR_CAOP);
        calculator.setOperator(operator);

        Boolean result = Whitebox.invokeMethod(calculator, "isFlagged", operatorFlagCriteria, contractorFlagCriteria);

        assertFalse(result);
    }

    @Test
    public void testIsFlagged_AuditCompleteAuditQuestionNoVerificationOrSubmittedWorkflow() throws Exception {
        AuditType auditType = EntityFactory.makeAuditType();
        ContractorAccount contractor = buildFakeContractorAccount(AuditStatus.Complete, auditType);

        FlagCriteria flagCriteria = buildFakeFlagCriteria();
        AuditQuestion question = EntityFactory.makeAuditQuestion();

        question.getCategory().setAuditType(auditType);

//        flagCriteria.setAuditType(null);
        flagCriteria.setQuestion(question);

        FlagCriteriaOperator operatorFlagCriteria = buildFakeFlagCriteriaOperator(flagCriteria);
        FlagCriteriaContractor contractorFlagCriteria = buildFakeFlagCriteriaContractor(flagCriteria, contractor);

        OperatorAccount operator = EntityFactory.makeOperator();
        operator.setId(OPERATOR_ID_FOR_CAOP);
        calculator.setOperator(operator);

        Boolean result = Whitebox.invokeMethod(calculator, "isFlagged", operatorFlagCriteria, contractorFlagCriteria);

        assertNull(result);
    }

    @Test
    public void testIsFlagged_AuditCompleteAuditQuestionNoVerificationButWithSubmittedWorkflow() throws Exception {
        ContractorAccount contractor = buildFakeContractorAccount(AuditStatus.Complete);

        FlagCriteria flagCriteria = buildFakeFlagCriteria();
        AuditQuestion question = EntityFactory.makeAuditQuestion();

        AuditType auditType = EntityFactory.makeAuditType();
        auditType.setWorkFlow(EntityFactory.makeWorkflowWithSubmitted());

        question.getCategory().setAuditType(auditType);

        flagCriteria.setAuditType(null);
        flagCriteria.setQuestion(question);

        FlagCriteriaOperator operatorFlagCriteria = buildFakeFlagCriteriaOperator(flagCriteria);
        FlagCriteriaContractor contractorFlagCriteria = buildFakeFlagCriteriaContractor(flagCriteria, contractor);

        OperatorAccount operator = EntityFactory.makeOperator();
        operator.setId(OPERATOR_ID_FOR_CAOP);
        calculator.setOperator(operator);

        Boolean result = Whitebox.invokeMethod(calculator, "isFlagged", operatorFlagCriteria, contractorFlagCriteria);

        assertNull(result);
    }

    @Test
    public void testIsFlagged_AuditApprovedAuditQuestionVerifiedWithSubmittedWorkflow() throws Exception {
        ContractorAccount contractor = buildFakeContractorAccount(AuditStatus.Approved);

        FlagCriteria flagCriteria = buildFakeFlagCriteria();
        AuditQuestion question = EntityFactory.makeAuditQuestion();

        AuditType auditType = contractor.getAudits().get(0).getAuditType();
//        auditType.setWorkFlow(EntityFactory.makeWorkflowWithSubmitted());

//        question.getCategory().setAuditType(auditType);

//        flagCriteria.setAuditType(null);
        flagCriteria.setQuestion(question);

        FlagCriteriaOperator operatorFlagCriteria = buildFakeFlagCriteriaOperator(flagCriteria);
        FlagCriteriaContractor contractorFlagCriteria = buildFakeFlagCriteriaContractor(flagCriteria, contractor);
//        contractorFlagCriteria.setVerified(true);

        OperatorAccount operator = EntityFactory.makeOperator();
        operator.setId(OPERATOR_ID_FOR_CAOP);
        calculator.setOperator(operator);

        Boolean result = Whitebox.invokeMethod(calculator, "isFlagged", operatorFlagCriteria, contractorFlagCriteria);

        assertNotNull(result);
    }

    @Test
    public void testIsFlagged_AuditPendingAuditQuestionNoVerificationOrSubmittedWorkflow() throws Exception {
        ContractorAccount contractor = buildFakeContractorAccount(AuditStatus.Pending);

        FlagCriteria flagCriteria = buildFakeFlagCriteria();
        AuditQuestion question = EntityFactory.makeAuditQuestion();
        question.getCategory().setAuditType(EntityFactory.makeAuditType(ALBERTA_WCB_AUDIT_TYPE_ID));

        flagCriteria.setAuditType(null);
        flagCriteria.setQuestion(question);

        FlagCriteriaOperator operatorFlagCriteria = buildFakeFlagCriteriaOperator(flagCriteria);
        FlagCriteriaContractor contractorFlagCriteria = buildFakeFlagCriteriaContractor(flagCriteria, contractor);

        OperatorAccount operator = EntityFactory.makeOperator();
        operator.setId(OPERATOR_ID_FOR_CAOP);
        calculator.setOperator(operator);

        Boolean result = Whitebox.invokeMethod(calculator, "isFlagged", operatorFlagCriteria, contractorFlagCriteria);

        assertNull(result);
    }

    @Test
    public void testIsFlagged_AuditPendingAuditQuestionNoVerificationButWithSubmittedWorkflow() throws Exception {
        operator = EntityFactory.makeOperator();
        operator.setId(OPERATOR_ID_FOR_CAOP);

        ContractorAccount contractor = buildFakeContractorAccount(AuditStatus.Pending);

        FlagCriteria flagCriteria = buildFakeFlagCriteria();
        AuditQuestion question = EntityFactory.makeAuditQuestion();

        AuditType auditType = EntityFactory.makeAuditType(ALBERTA_WCB_AUDIT_TYPE_ID);
        auditType.setWorkFlow(EntityFactory.makeWorkflowWithSubmitted());

        question.getCategory().setAuditType(auditType);

        flagCriteria.setAuditType(null);
        flagCriteria.setQuestion(question);

        FlagCriteriaOperator operatorFlagCriteria = buildFakeFlagCriteriaOperator(flagCriteria);
        FlagCriteriaContractor contractorFlagCriteria = buildFakeFlagCriteriaContractor(flagCriteria, contractor);

        calculator.setOperator(operator);

        Boolean result = Whitebox.invokeMethod(calculator, "isFlagged", operatorFlagCriteria, contractorFlagCriteria);

        assertNull(result);
    }

    @Test
    public void testIsFlagged_AuditPendingAuditQuestionVerifiedWithSubmittedWorkflow() throws Exception {
        ContractorAccount contractor = buildFakeContractorAccount(AuditStatus.Pending);

        FlagCriteria flagCriteria = buildFakeFlagCriteria();
        AuditQuestion question = EntityFactory.makeAuditQuestion();

        AuditType auditType = contractor.getAudits().get(0).getAuditType();
        auditType.setWorkFlow(EntityFactory.makeWorkflowWithSubmitted());

        question.getCategory().setAuditType(auditType);

        flagCriteria.setAuditType(null);
        flagCriteria.setQuestion(question);

        FlagCriteriaOperator operatorFlagCriteria = buildFakeFlagCriteriaOperator(flagCriteria);
        FlagCriteriaContractor contractorFlagCriteria = buildFakeFlagCriteriaContractor(flagCriteria, contractor);
//        contractorFlagCriteria.setVerified(true);

        OperatorAccount operator = EntityFactory.makeOperator();
        operator.setId(OPERATOR_ID_FOR_CAOP);
        calculator.setOperator(operator);

        Boolean result = Whitebox.invokeMethod(calculator, "isFlagged", operatorFlagCriteria, contractorFlagCriteria);

        assertNull(result);
    }

    @Test
    public void testIsFlagged_AuditCompleteFailsCPIQuestionCheck() throws Exception {
        ContractorAccount contractor = buildFakeContractorAccount(AuditStatus.Complete);

        FlagCriteria flagCriteria = EntityFactory.makeFlagCriteria();
        flagCriteria.setId(709);
        flagCriteria.setDataType("number");
        flagCriteria.setComparison("<");
        flagCriteria.setDefaultValue("70");
        flagCriteria.setRequiredStatus(AuditStatus.Complete);

        AuditQuestion question = EntityFactory.makeAuditQuestion();

//        AuditType auditType = EntityFactory.makeAuditType(ALBERTA_WCB_AUDIT_TYPE_ID);

//        question.getCategory().setAuditType(auditType);

//        flagCriteria.setAuditType(null);
        flagCriteria.setQuestion(question);

        FlagCriteriaOperator operatorFlagCriteria = buildFakeFlagCriteriaOperator(flagCriteria);
        FlagCriteriaContractor contractorFlagCriteria = buildFakeFlagCriteriaContractor(flagCriteria, contractor);
        contractorFlagCriteria.setAnswer("50");

        OperatorAccount operator = EntityFactory.makeOperator();
        operator.setId(OPERATOR_ID_FOR_CAOP);
        calculator.setOperator(operator);

        Boolean result = Whitebox.invokeMethod(calculator, "isFlagged", operatorFlagCriteria, contractorFlagCriteria);

        assertTrue(result);
    }

    @Test
    public void testIsFlagged_AuditCompleteSucceedsCPIQuestionCheck() throws Exception {
        ContractorAccount contractor = buildFakeContractorAccount(AuditStatus.Complete);

        FlagCriteria flagCriteria = EntityFactory.makeFlagCriteria();
        flagCriteria.setId(709);
        flagCriteria.setDataType("number");
        flagCriteria.setComparison("<");
        flagCriteria.setDefaultValue("70");
        flagCriteria.setRequiredStatus(AuditStatus.Complete);

        AuditQuestion question = EntityFactory.makeAuditQuestion();

//        AuditType auditType = EntityFactory.makeAuditType(ALBERTA_WCB_AUDIT_TYPE_ID);

//        question.getCategory().setAuditType(auditType);

//        flagCriteria.setAuditType(null);
        flagCriteria.setQuestion(question);

        FlagCriteriaOperator operatorFlagCriteria = buildFakeFlagCriteriaOperator(flagCriteria);
        FlagCriteriaContractor contractorFlagCriteria = buildFakeFlagCriteriaContractor(flagCriteria, contractor);
        contractorFlagCriteria.setAnswer("80");

        OperatorAccount operator = EntityFactory.makeOperator();
        operator.setId(OPERATOR_ID_FOR_CAOP);
        calculator.setOperator(operator);

        Boolean result = Whitebox.invokeMethod(calculator, "isFlagged", operatorFlagCriteria, contractorFlagCriteria);

        assertFalse(result);
    }

    // This test covers the issue: PICS-9623
    @Test
    public void testIsFlagged_AnnualUpdateQuestion() throws Exception {
        ContractorAudit annualUpdate2012 = EntityFactory.makeAnnualUpdate(11, contractor, "2012");
        mockContractorAuditOperator(annualUpdate2012, AuditStatus.Complete);
        addCaoAndCaopToAudit(annualUpdate2012, operator);

        ContractorAudit annualUpdate2011 = EntityFactory.makeAnnualUpdate(11, contractor, "2011");
        mockContractorAuditOperator(annualUpdate2011, AuditStatus.Pending);
        addCaoAndCaopToAudit(annualUpdate2011, operator);

        ContractorAudit annualUpdate2010 = EntityFactory.makeAnnualUpdate(11, contractor, "2010");
        mockContractorAuditOperator(annualUpdate2010, AuditStatus.Pending);
        addCaoAndCaopToAudit(annualUpdate2010, operator);


        List<ContractorAudit> annualUpdatesInSpecificOrder = new ArrayList<ContractorAudit>();
        annualUpdatesInSpecificOrder.add(0, annualUpdate2010);
        annualUpdatesInSpecificOrder.add(1, annualUpdate2011);
        annualUpdatesInSpecificOrder.add(2, annualUpdate2012);

        contractor.setAudits(annualUpdatesInSpecificOrder);


        lastYearCriteria.setQuestion(EntityFactory.makeAuditQuestion());
//        lastYearCriteria.getQuestion().getCategory().setAuditType(EntityFactory.makeAuditType(11));
        lastYearCriteria.setRequiredStatus(AuditStatus.Submitted);
//        lastYearCriteria.setMultiYearScope(MultiYearScope.LastYearOnly);
        lastYearCriteria.setDataType("string");

        FlagCriteriaOperator operatorFlagCriteria = buildFakeFlagCriteriaOperator(lastYearCriteria);
        FlagCriteriaContractor contractorFlagCriteria = buildFakeFlagCriteriaContractor(lastYearCriteria, contractor);

        calculator.setOperator(operator);
        Boolean result = Whitebox.invokeMethod(calculator, "isFlagged", operatorFlagCriteria, contractorFlagCriteria);
        assertTrue(result);
    }

    private void addCaoAndCaopToAudit(ContractorAudit audit, OperatorAccount operator) {
        ArrayList<ContractorAuditOperator> caos = new ArrayList<>();

        ContractorAuditOperatorPermission contractorAuditOperatorPermission = new ContractorAuditOperatorPermission();
        contractorAuditOperatorPermission.setOperator(operator);
        cao = new ContractorAuditOperator();
//        cao.setOperator(operator);
        cao.getCaoPermissions().add(contractorAuditOperatorPermission);
        cao.setStatus(AuditStatus.Approved);
        cao.setVisible(true);
        caos.add(cao);
        audit.setOperators(caos);
    }

    private void mockContractorAuditOperator(ContractorAudit contractorAudit, AuditStatus auditStatus) {
        ContractorAuditOperator contractorAuditOperator2012 = Mockito.mock(ContractorAuditOperator.class);

//        when(contractorAuditOperator2012.getAudit()).thenReturn(contractorAudit);
        when(contractorAuditOperator2012.getStatus()).thenReturn(auditStatus);
        when(contractorAuditOperator2012.isVisible()).thenReturn(true);
//        when(contractorAuditOperator2012.hasCaop(operator.getId())).thenReturn(true);

        contractorAudit.setOperators(Arrays.asList(contractorAuditOperator2012));
    }

    private ContractorAccount buildFakeContractorAccount(AuditStatus caoStatus) {
        return buildFakeContractorAccount(caoStatus, EntityFactory.makeAuditType(ALBERTA_WCB_AUDIT_TYPE_ID));
    }

    private ContractorAccount buildFakeContractorAccount(AuditStatus caoStatus, AuditType auditType) {
        ContractorAccount contractor = EntityFactory.makeContractor();
        contractor.setAccountLevel(AccountLevel.Full);
        ContractorAudit mockAudit = buildMockAudit(1000, yearForCurrentWCB(), caoStatus, auditType);
        List<ContractorAudit> audits = new ArrayList<>();
        audits.add(mockAudit);
        contractor.setAudits(audits);


        return contractor;
    }

    private ContractorAccount buildFakeContractorAccountWithWCBs(AuditStatus caoStatus) {
        int yearForCurrentWCB = yearForCurrentWCB();

        ContractorAccount contractor = EntityFactory.makeContractor();
        contractor.setAccountLevel(AccountLevel.Full);
        contractor.setAudits(buildMockAuditList(yearForCurrentWCB, caoStatus));

        return contractor;
    }

    private FlagCriteria buildFakeFlagCriteria() {
        FlagCriteria flagCriteria = EntityFactory.makeFlagCriteria();
        flagCriteria.setId(709);
//        flagCriteria.setDisplayOrder(999);
        flagCriteria.setDataType("boolean");
        flagCriteria.setAllowCustomValue(false);
        flagCriteria.setAuditType(EntityFactory.makeAuditType(ALBERTA_WCB_AUDIT_TYPE_ID));
        flagCriteria.setRequiredStatus(AuditStatus.Approved);

        return flagCriteria;
    }

    private FlagCriteriaContractor buildFakeFlagCriteriaContractor(FlagCriteria flagCriteria, ContractorAccount contractor) {
        FlagCriteriaContractor contractorFlagCriteria = EntityFactory.makeFlagCriteriaContractor("true");
        contractorFlagCriteria.setContractor(contractor);
        contractorFlagCriteria.setCriteria(flagCriteria);

        return contractorFlagCriteria;
    }

    private FlagCriteriaOperator buildFakeFlagCriteriaOperator(FlagCriteria flagCriteria) {
        FlagCriteriaOperator operatorFlagCriteria = EntityFactory.makeFlagCriteriaOperator(null);
        operatorFlagCriteria.setTag(null);
        operatorFlagCriteria.setCriteria(flagCriteria);
//        operatorFlagCriteria.setOperator(operator);

        return operatorFlagCriteria;
    }

    @Test
    public void testFindCaosForCurrentWCB() throws Exception {
        ContractorAccount contractor = Mockito.mock(ContractorAccount.class);

        int yearForCurrentWCB = yearForCurrentWCB();
        List<ContractorAudit> audits = buildMockAuditList(yearForCurrentWCB, AuditStatus.Approved);
        when(contractor.getAudits()).thenReturn(audits);

        List<ContractorAuditOperator> caos = Whitebox.invokeMethod(calculator, "findCaosForCurrentWCB", contractor,
                EntityFactory.makeAuditType(ALBERTA_WCB_AUDIT_TYPE_ID));

        assertNotNull(caos);
        assertFalse(caos.isEmpty());
        assertEquals(yearForCurrentWCB, caos.get(0).getId());
    }

    private int yearForCurrentWCB() {
        if (DateBean.isGracePeriodForWCB()) {
            return DateBean.getPreviousWCBYear();
        }

        return NumberUtils.toInt(DateBean.getWCBYear());
    }

    private List<ContractorAudit> buildMockAuditList(int yearForCurrentWCB, AuditStatus caoStatus) {
        List<ContractorAudit> audits = new ArrayList<ContractorAudit>();
        for (int year = yearForCurrentWCB; year <= yearForCurrentWCB + 1; year++) {
            audits.add(buildMockAudit(year + 1000, year, caoStatus));
        }

        return audits;
    }

//    /**
//     * Builds a mock ContractorAudit.
//     *
//     * @param id ContractorAudit's ID value
//     * @param auditForYear AuditFor field value for the ContractorAudit
//     * @return ContractorAudit with list that has one CAO with an ID that is the same as the
//     * auditForYear for the purpose of verifying the proper CAO was returned in tests.
//     */
    private ContractorAudit buildMockAudit(int id, int auditForYear, AuditStatus caoStatus) {
        return buildMockAudit(id, auditForYear, caoStatus, EntityFactory.makeAuditType(ALBERTA_WCB_AUDIT_TYPE_ID));
    }

    private ContractorAudit buildMockAudit(int id, int auditForYear, AuditStatus caoStatus, AuditType auditType) {
        ContractorAudit audit = Mockito.mock(ContractorAudit.class);
        when(audit.getId()).thenReturn(id);
        when(audit.getAuditFor()).thenReturn(Integer.toString(auditForYear));

        when(audit.getAuditType()).thenReturn(auditType);

        ContractorAuditOperator cao = Mockito.mock(ContractorAuditOperator.class);
        ContractorAuditOperatorPermission caop = Mockito.mock(ContractorAuditOperatorPermission.class);
        when(cao.getId()).thenReturn(auditForYear);
        when(cao.isVisible()).thenReturn(true);
        when(audit.getOperators()).thenReturn(Arrays.asList(cao));
//        when(cao.hasCaop(OPERATOR_ID_FOR_CAOP)).thenReturn(true);
        when(cao.getStatus()).thenReturn(caoStatus);
        when(cao.getOperator()).thenReturn(operator);
        when(caop.getOperator()).thenReturn(operator);
        operator.setId(OPERATOR_ID_FOR_CAOP);
        when(cao.getCaoPermissions()).thenReturn(Arrays.asList(caop));

        return audit;
    }
}
