package com.picsauditing.flagcalculator.service;

import com.picsauditing.flagcalculator.entities.*;
import org.junit.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class FlagServiceTest {
    private static final String CRITERIA_CONTRACTOR_ANSWER1 = "Answer One";
    private static final String CRITERIA_CONTRACTOR_ANSWER2 = "Answer Two";

    private Date lastMonth = org.joda.time.DateTime.now().minusMonths(1).toDate();
    private Date nextMonth = org.joda.time.DateTime.now().plusMonths(1).toDate();

    private List<FlagCriteriaOperator> flagCriteriaList;
    private List<ContractorAudit> contractorAudits;
    private List<Facility> facilities;
    private List<ContractorOperator> operators;

    @Mock
    private FlagDataOverride flagDataOverride;
    @Mock
    private OperatorAccount operatorAccount;
    @Mock
    private OperatorAccount inheritFlagCriteria;
    @Mock
    private FlagCriteriaOperator flagCriteriaOperator;
    @Mock
    private FlagCriteria flagCriteria;
    @Mock
    private AuditType auditType;
    @Mock
    private AuditQuestion question;
    @Mock
    private AuditCategory category;
    @Mock
    private FlagCriteriaContractor toUpdate;
    @Mock
    private FlagCriteriaContractor fromUpdate;
    @Mock
    private FlagData dataToUpdate;
    @Mock
    private FlagData dataFromUpdate;
    @Mock
    private ContractorAccount contractorAccount;
    @Mock
    private ContractorAudit contractorAudit;
    @Mock
    private ContractorOperator contractorOperator;
    @Mock
    private ContractorOperator contractorOperator2;
    @Mock
    private Facility facility;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        flagCriteriaList = new ArrayList<>();
        flagCriteriaList.add(flagCriteriaOperator);

        contractorAudits = new ArrayList<>();
        contractorAudits.add(contractorAudit);

        facilities = new ArrayList<>();

        operators = new ArrayList<>();
    }

    @Test
    public void testIsInForce_NullForceEndIsFalse() throws Exception {
        when(flagDataOverride.getForceEnd()).thenReturn(null);
        assertFalse(FlagService.isInForce(flagDataOverride));
    }

    @Test
    public void testIsInForce_ForceEndIsBeforeNowIsFalse() throws Exception {
        when(flagDataOverride.getForceEnd()).thenReturn(lastMonth);
        assertFalse(FlagService.isInForce(flagDataOverride));
    }

    @Test
    public void testIsInForce_ForceEndIsAfterNowIsTrue() throws Exception {
        when(flagDataOverride.getForceEnd()).thenReturn(nextMonth);
        assertTrue(FlagService.isInForce(flagDataOverride));
    }

    @Test
    public void testGetFlagCriteriaInherited_WhenInsuranceGetInheritedFlagCriteriaCriteria() throws Exception {
        when(operatorAccount.getInheritFlagCriteria()).thenReturn(inheritFlagCriteria);
        boolean insurance = true;

        FlagService.getFlagCriteriaInherited(operatorAccount, insurance);

        verify(inheritFlagCriteria).getFlagCriteria();
    }

    @Test
    public void testGetFlagCriteriaInherited_NotInsurance_EmptyListWhenInheritedFlagCriteriaIsNull() throws Exception {
        when(operatorAccount.getInheritFlagCriteria()).thenReturn(null);
        boolean insurance = false;

        List<FlagCriteriaOperator> criteria = FlagService.getFlagCriteriaInherited(operatorAccount, insurance);

        assertTrue(criteria.isEmpty());
    }

    @Test
    public void testGetFlagCriteriaInherited_NotInsurance_GetInheritedFlagCriteriaCriteria() throws Exception {
        when(operatorAccount.getInheritFlagCriteria()).thenReturn(inheritFlagCriteria);
        boolean insurance = false;

        FlagService.getFlagCriteriaInherited(operatorAccount, insurance);

        verify(inheritFlagCriteria).getFlagCriteria();
    }

    @Ignore
    @Test
    public void testGetFlagCriteriaInherited_GetsBothAuditCriteriaAndFlagQuestionCriteria() throws Exception {

    }

    @Test
    public void testGetFlagAuditCriteriaInherited_NullInheritedFlagCriteria_ReturnsEmptyList() throws Exception {
        when(operatorAccount.getInheritFlagCriteria()).thenReturn(null);

        List<FlagCriteriaOperator> criteria = FlagService.getFlagAuditCriteriaInherited(operatorAccount);

        assertTrue(criteria.isEmpty());
    }

    @Test
    public void testGetFlagAuditCriteriaInherited_NullInheritedFlagCriteriaAuditType_ReturnsEmptyList() throws Exception {
        when(operatorAccount.getInheritFlagCriteria()).thenReturn(inheritFlagCriteria);
        when(inheritFlagCriteria.getFlagCriteria()).thenReturn(flagCriteriaList);
        when(flagCriteriaOperator.getCriteria()).thenReturn(flagCriteria);
        when(flagCriteria.getAuditType()).thenReturn(null);

        List<FlagCriteriaOperator> criteria = FlagService.getFlagAuditCriteriaInherited(operatorAccount);

        assertTrue(criteria.isEmpty());
    }

    @Test
    public void testGetFlagAuditCriteriaInherited_AuditTypeIsPolicy_ReturnsEmptyList() throws Exception {
        when(operatorAccount.getInheritFlagCriteria()).thenReturn(inheritFlagCriteria);
        when(inheritFlagCriteria.getFlagCriteria()).thenReturn(flagCriteriaList);
        when(flagCriteriaOperator.getCriteria()).thenReturn(flagCriteria);
        when(flagCriteria.getAuditType()).thenReturn(auditType);
        when(auditType.getClassType()).thenReturn(AuditTypeClass.Policy);

        List<FlagCriteriaOperator> criteria = FlagService.getFlagAuditCriteriaInherited(operatorAccount);

        assertTrue(criteria.isEmpty());
    }

    @Test
    public void testGetFlagAuditCriteriaInherited_AuditTypeIsPolicyAndOperatorCannotSeeInsurance_ReturnsEmptyList() throws Exception {
        when(operatorAccount.getInheritFlagCriteria()).thenReturn(inheritFlagCriteria);
        when(inheritFlagCriteria.getFlagCriteria()).thenReturn(flagCriteriaList);
        when(flagCriteriaOperator.getCriteria()).thenReturn(flagCriteria);
        when(flagCriteria.getAuditType()).thenReturn(auditType);
        when(auditType.getClassType()).thenReturn(AuditTypeClass.Policy);
        when(operatorAccount.getCanSeeInsurance()).thenReturn(YesNo.No);

        List<FlagCriteriaOperator> criteria = FlagService.getFlagAuditCriteriaInherited(operatorAccount);

        assertTrue(criteria.isEmpty());
    }

    @Test
    public void testGetFlagAuditCriteriaInherited_AuditTypeIsNotPolicy_ReturnsCriteriaList() throws Exception {
        when(operatorAccount.getInheritFlagCriteria()).thenReturn(inheritFlagCriteria);
        when(inheritFlagCriteria.getFlagCriteria()).thenReturn(flagCriteriaList);
        when(flagCriteriaOperator.getCriteria()).thenReturn(flagCriteria);
        when(flagCriteria.getAuditType()).thenReturn(auditType);
        when(auditType.getClassType()).thenReturn(AuditTypeClass.Audit);
        when(operatorAccount.getCanSeeInsurance()).thenReturn(YesNo.No);

        List<FlagCriteriaOperator> criteria = FlagService.getFlagAuditCriteriaInherited(operatorAccount);
        assertTrue(flagCriteriaOperator.equals(criteria.get(0)));

        when(operatorAccount.getCanSeeInsurance()).thenReturn(YesNo.Yes);
        criteria = FlagService.getFlagAuditCriteriaInherited(operatorAccount);
        assertTrue(flagCriteriaOperator.equals(criteria.get(0)));
    }

    @Test
    public void testGetFlagQuestionCriteriaInherited_NullInheritedFlagCriteria_ReturnsEmptyList() throws Exception {
        when(operatorAccount.getInheritFlagCriteria()).thenReturn(null);

        List<FlagCriteriaOperator> criteria = FlagService.getFlagQuestionCriteriaInherited(operatorAccount);

        assertTrue(criteria.isEmpty());
    }

    @Test
    public void testGetFlagQuestionCriteriaInherited_NullQuestionAndNullOshaType_ReturnsEmptyList() throws Exception {
        when(operatorAccount.getInheritFlagCriteria()).thenReturn(inheritFlagCriteria);
        when(inheritFlagCriteria.getFlagCriteria()).thenReturn(flagCriteriaList);
        when(flagCriteriaOperator.getCriteria()).thenReturn(flagCriteria);
        when(flagCriteria.getQuestion()).thenReturn(null);
        when(flagCriteria.getOshaType()).thenReturn(null);
        // when(flagCriteria.getQuestion()).thenReturn(question);

        List<FlagCriteriaOperator> criteria = FlagService.getFlagQuestionCriteriaInherited(operatorAccount);

        assertTrue(criteria.isEmpty());
    }

    @Test
    public void testGetFlagQuestionCriteriaInherited_NotCurrentQuestionAndNullOshaType_ReturnsEmptyList() throws Exception {
        when(operatorAccount.getInheritFlagCriteria()).thenReturn(inheritFlagCriteria);
        when(inheritFlagCriteria.getFlagCriteria()).thenReturn(flagCriteriaList);
        when(flagCriteriaOperator.getCriteria()).thenReturn(flagCriteria);
        when(flagCriteria.getQuestion()).thenReturn(question);
        when(question.getExpirationDate()).thenReturn(lastMonth);
        when(flagCriteria.getOshaType()).thenReturn(null);

        List<FlagCriteriaOperator> criteria = FlagService.getFlagQuestionCriteriaInherited(operatorAccount);

        assertTrue(criteria.isEmpty());
    }

    @Test
    public void testGetFlagQuestionCriteriaInherited_CurrentQuestionPolicyAndCannotSeeInsuranceAndNullOshaType_ReturnsEmptyList() throws Exception {
        when(operatorAccount.getInheritFlagCriteria()).thenReturn(inheritFlagCriteria);
        when(inheritFlagCriteria.getFlagCriteria()).thenReturn(flagCriteriaList);
        when(flagCriteriaOperator.getCriteria()).thenReturn(flagCriteria);
        when(flagCriteria.getQuestion()).thenReturn(question);
        when(question.getExpirationDate()).thenReturn(nextMonth);
        when(question.getCategory()).thenReturn(category);
        when(category.getAuditType()).thenReturn(auditType);

        when(auditType.getClassType()).thenReturn(AuditTypeClass.Policy);
        when(operatorAccount.getCanSeeInsurance()).thenReturn(YesNo.No);

        when(flagCriteria.getOshaType()).thenReturn(null);

        List<FlagCriteriaOperator> criteria = FlagService.getFlagQuestionCriteriaInherited(operatorAccount);

        assertTrue(criteria.isEmpty());
    }

    @Test
    public void testGetFlagQuestionCriteriaInherited_CurrentQuestionNotPolicyAndCannotSeeInsuranceAndNullOshaType_Returns() throws Exception {
        when(operatorAccount.getInheritFlagCriteria()).thenReturn(inheritFlagCriteria);
        when(inheritFlagCriteria.getFlagCriteria()).thenReturn(flagCriteriaList);
        when(flagCriteriaOperator.getCriteria()).thenReturn(flagCriteria);
        when(flagCriteria.getQuestion()).thenReturn(question);
        when(question.getExpirationDate()).thenReturn(nextMonth);
        when(question.getCategory()).thenReturn(category);
        when(category.getAuditType()).thenReturn(auditType);

        when(auditType.getClassType()).thenReturn(AuditTypeClass.Audit);
        when(operatorAccount.getCanSeeInsurance()).thenReturn(YesNo.No);

        when(flagCriteria.getOshaType()).thenReturn(null);

        List<FlagCriteriaOperator> criteria = FlagService.getFlagQuestionCriteriaInherited(operatorAccount);

        assertTrue(flagCriteriaOperator.equals(criteria.get(0)));
    }


    @Test
    public void testGetFlagQuestionCriteriaInherited_NullQuestionAndNoEqualOshaType_ReturnsEmptyList() throws Exception {
        when(operatorAccount.getInheritFlagCriteria()).thenReturn(inheritFlagCriteria);
        when(inheritFlagCriteria.getFlagCriteria()).thenReturn(flagCriteriaList);
        when(flagCriteriaOperator.getCriteria()).thenReturn(flagCriteria);
        when(flagCriteria.getQuestion()).thenReturn(null);
        when(flagCriteria.getOshaType()).thenReturn(OshaType.AUSTRALIA);
        when(operatorAccount.getOshaType()).thenReturn(OshaType.GREECE);

        List<FlagCriteriaOperator> criteria = FlagService.getFlagQuestionCriteriaInherited(operatorAccount);

        assertTrue(criteria.isEmpty());
    }

    @Test
    public void testGetFlagQuestionCriteriaInherited_EqualOshaType_Returns() throws Exception {
        when(operatorAccount.getInheritFlagCriteria()).thenReturn(inheritFlagCriteria);
        when(inheritFlagCriteria.getFlagCriteria()).thenReturn(flagCriteriaList);
        when(flagCriteriaOperator.getCriteria()).thenReturn(flagCriteria);
        when(flagCriteria.getQuestion()).thenReturn(null);
        when(flagCriteria.getOshaType()).thenReturn(OshaType.AUSTRALIA);
        when(operatorAccount.getOshaType()).thenReturn(OshaType.AUSTRALIA);

        List<FlagCriteriaOperator> criteria = FlagService.getFlagQuestionCriteriaInherited(operatorAccount);

        assertTrue(flagCriteriaOperator.equals(criteria.get(0)));
    }

    @Test
    public void testIncludeExcess_NotInsurance_ReturnsNull() throws Exception {
        when(flagCriteria.isInsurance()).thenReturn(false);

        assertNull(FlagService.includeExcess(flagCriteria));
    }

    @Test
    public void testIncludeExcess_InsuranceWithNullOptionCode_ReturnsNull() throws Exception {
        when(flagCriteria.isInsurance()).thenReturn(true);
        when(flagCriteria.getOptionCode()).thenReturn(null);

        assertNull(FlagService.includeExcess(flagCriteria));
    }

    @Test
    public void testIncludeExcess_ExceessAggregateOptionCode_ReturnsExcessAggregateQuestionID() throws Exception {
        when(flagCriteria.isInsurance()).thenReturn(true);
        when(flagCriteria.getOptionCode()).thenReturn(FlagCriteriaOptionCode.ExcessAggregate);

        assertTrue(FlagService.includeExcess(flagCriteria) == AuditQuestion.EXCESS_AGGREGATE);
    }

    @Test
    public void testIncludeExcess_ExceessEachOccurrenceOptionCode_ReturnsExcessEachOccurrenceID() throws Exception {
        when(flagCriteria.isInsurance()).thenReturn(true);
        when(flagCriteria.getOptionCode()).thenReturn(FlagCriteriaOptionCode.ExcessEachOccurrence);

        assertTrue(FlagService.includeExcess(flagCriteria) == AuditQuestion.EXCESS_EACH);
    }

    @Test
    public void testIncludeExcess_NotExcessOptionCode_ReturnsNull() throws Exception {
        when(flagCriteria.isInsurance()).thenReturn(true);
        when(flagCriteria.getOptionCode()).thenReturn(FlagCriteriaOptionCode.None);

        assertNull(FlagService.includeExcess(flagCriteria));
    }

    @Test
    public void testUpdateFlagCriteriaContractor_AnswersSame_DoesNotUpdate() {
        when(toUpdate.getAnswer()).thenReturn(CRITERIA_CONTRACTOR_ANSWER1);
        when(fromUpdate.getAnswer()).thenReturn(CRITERIA_CONTRACTOR_ANSWER1);

        FlagService.updateFlagCriteriaContractor(toUpdate, fromUpdate);

        verify(toUpdate, never()).setAnswer(anyString());
    }

    @Test
    public void testUpdateFlagCriteriaContractor_AnswersDifferent_DoesUpdate() {
        when(toUpdate.getAnswer()).thenReturn(CRITERIA_CONTRACTOR_ANSWER1);
        when(fromUpdate.getAnswer()).thenReturn(CRITERIA_CONTRACTOR_ANSWER2);

        FlagService.updateFlagCriteriaContractor(toUpdate, fromUpdate);

        verify(toUpdate).setAnswer(CRITERIA_CONTRACTOR_ANSWER2);
        verify(toUpdate).setAuditColumns(any(User.class));
    }

    @Test
    public void testUpdateFlagCriteriaContractor_Answers2Same_DoesNotUpdate() {
        when(toUpdate.getAnswer2()).thenReturn(CRITERIA_CONTRACTOR_ANSWER1);
        when(fromUpdate.getAnswer2()).thenReturn(CRITERIA_CONTRACTOR_ANSWER1);

        FlagService.updateFlagCriteriaContractor(toUpdate, fromUpdate);

        verify(toUpdate, never()).setAnswer2(anyString());
    }

    @Test
    public void testUpdateFlagCriteriaContractor_Answers2Different_DoesUpdate() {
        when(toUpdate.getAnswer2()).thenReturn(CRITERIA_CONTRACTOR_ANSWER1);
        when(fromUpdate.getAnswer2()).thenReturn(CRITERIA_CONTRACTOR_ANSWER2);

        FlagService.updateFlagCriteriaContractor(toUpdate, fromUpdate);

        verify(toUpdate).setAnswer2(CRITERIA_CONTRACTOR_ANSWER2);
        verify(toUpdate).setAuditColumns(any(User.class));
    }

    @Test
    public void testUpdateFlagCriteriaContractor_IsVerifiedSame_DoesNotUpdate() {
        when(toUpdate.isVerified()).thenReturn(true);
        when(fromUpdate.isVerified()).thenReturn(true);

        FlagService.updateFlagCriteriaContractor(toUpdate, fromUpdate);

        verify(toUpdate, never()).setVerified(anyBoolean());
    }

    @Test
    public void testUpdateFlagCriteriaContractor_IsVerifiedDifferent_DoesUpdate() {
        when(toUpdate.isVerified()).thenReturn(false);
        when(fromUpdate.isVerified()).thenReturn(true);

        FlagService.updateFlagCriteriaContractor(toUpdate, fromUpdate);

        verify(toUpdate).setVerified(true);
        verify(toUpdate).setAuditColumns(any(User.class));
    }

    @Test
    public void testUpdateFlagData_MustBeEqual() throws Exception {
        // Note: you cannot mock or verify equals
        FlagService.updateFlagData(dataToUpdate, dataFromUpdate);

        verify(dataToUpdate, never()).getFlag();
    }

    @Test
    public void testUpdateFlagData_SameFlagColorDoesNotUpdate() throws Exception {
        when(dataToUpdate.getFlag()).thenReturn(FlagColor.Green).thenReturn(FlagColor.Green);

        FlagService.updateFlagData(dataToUpdate, dataToUpdate);

        verify(dataToUpdate, never()).setFlag(any(FlagColor.class));
    }

    @Test
    public void testUpdateFlagData_DifferentFlagColorDoesUpdate() throws Exception {
        when(dataToUpdate.getFlag()).thenReturn(FlagColor.Red).thenReturn(FlagColor.Green);

        FlagService.updateFlagData(dataToUpdate, dataToUpdate);

        verify(dataToUpdate).setFlag(FlagColor.Green);
        verify(dataToUpdate).setAuditColumns(any(User.class));
    }

    /*
     I do not at this time see a way to do any meanigful testing for this method as FlagService is static, uses AuditService and
     instantiates new concrete OshaAudits
    */
    @Ignore
    @Test
    public void testGetOshaOrganizer_() throws Exception {
        OshaOrganizer oshaOrganizer = FlagService.getOshaOrganizer(contractorAccount);
    }

    /*
     I do not at this time see a way to do any meanigful testing for this method as FlagService is static, uses AuditService and
     instantiates new concrete OshaAudits
    */
    @Ignore
    @Test
    public void testgetOshaAudits_() throws Exception {

    }

    @Test
    public void testGetForceOverallFlag_ContractorOperatorInEffect_ReturnsContractorOperator() throws Exception {
        when(contractorOperator.getForceFlag()).thenReturn(FlagColor.Green);
        when(contractorOperator.getForceEnd()).thenReturn(nextMonth);

        ContractorOperator result = FlagService.getForceOverallFlag(contractorOperator);

        assertEquals(contractorOperator, result);
    }

    @Test
    public void testGetForceOverallFlag_ContractorOperatorNotInEffect_ReturnsFirstForcedFlagFromFacilities() throws Exception {
        when(contractorOperator.getForceFlag()).thenReturn(null);

        facilities.add(facility);

        when(contractorOperator.getOperatorAccount()).thenReturn(operatorAccount);
        when(operatorAccount.getCorporateFacilities()).thenReturn(facilities);
        when(contractorOperator.getContractorAccount()).thenReturn(contractorAccount);
        when(contractorAccount.getOperators()).thenReturn(operators);
        when(contractorOperator2.getOperatorAccount()).thenReturn(operatorAccount);
        operators.add(contractorOperator2);
        when(contractorOperator2.getForceFlag()).thenReturn(FlagColor.Green);
        when(contractorOperator2.getForceEnd()).thenReturn(nextMonth);

        when(facility.getCorporate()).thenReturn(operatorAccount);

        ContractorOperator result = FlagService.getForceOverallFlag(contractorOperator);

        assertEquals(contractorOperator2, result);
    }

    @Test
    public void testIsForcedFlag_NullForceFlag_IsFalse() throws Exception {
        when(contractorOperator.getForceFlag()).thenReturn(null);
        assertFalse(FlagService.isForcedFlag(contractorOperator));
    }

    @Test
    public void testIsForcedFlag_NullForceEnd_IsFalse() throws Exception {
        when(contractorOperator.getForceFlag()).thenReturn(FlagColor.Green);
        when(contractorOperator.getForceEnd()).thenReturn(null);

        assertFalse(FlagService.isForcedFlag(contractorOperator));
    }

    @Test
    public void testIsForcedFlag_NoLongerInEffect_IsFalse() throws Exception {
        when(contractorOperator.getForceFlag()).thenReturn(FlagColor.Green);
        when(contractorOperator.getForceEnd()).thenReturn(lastMonth);

        assertFalse(FlagService.isForcedFlag(contractorOperator));
    }

    @Test
    public void testIsForcedFlag_InEffect_IsTrue() throws Exception {
        when(contractorOperator.getForceFlag()).thenReturn(FlagColor.Green);
        when(contractorOperator.getForceEnd()).thenReturn(nextMonth);

        assertTrue(FlagService.isForcedFlag(contractorOperator));
    }

}