package com.picsauditing.service.account;

import com.picsauditing.PicsTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.SlickEnhancedContractorOperatorDAO;
import com.picsauditing.jpa.entities.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.junit.Assert.*;

public class WaitingOnServiceTest extends PicsTest {
    @Mock
    private SlickEnhancedContractorOperatorDAO contractorOperatorDAO;
    @Mock
    ContractorAccount contractor;
    @Mock
    OperatorAccount operator;
    @Mock
    ContractorOperator co;
    @Mock
    ContractorAudit audit;
    @Mock
    AuditType auditType;

    private WaitingOnService testClass;

    private List<FlagCriteriaOperator> fcoList = new ArrayList<>();
    private List<ContractorAudit> auditList = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);

        testClass = new WaitingOnService();
        PicsTestUtil.forceSetPrivateField(testClass, "contractorOperatorDAO", contractorOperatorDAO);
        when(co.getContractorAccount()).thenReturn(contractor);
        when(co.getOperatorAccount()).thenReturn(operator);
        when(operator.getFlagAuditCriteriaInherited()).thenReturn(fcoList);
        when(contractor.getAudits()).thenReturn(auditList);

        setupDefaultContractor();
        setupDefaultOperator();
    }

    @After
    public void tearDown() throws Exception {
        fcoList.clear();
        auditList.clear();
    }

    @Test
    public void testCalculateWaitingOn_None() throws Exception {
        WaitingOn result = testClass.calculateWaitingOn(co);
        assertEquals(WaitingOn.None, result);
    }

    @Test
    public void testCalculateWaitingOn_NoSafetyRisk_Contractor() throws Exception {
        when(contractor.getSafetyRisk()).thenReturn(null);

        WaitingOn result = testClass.calculateWaitingOn(co);
        assertEquals(WaitingOn.Contractor, result);
    }

    @Test
    public void testCalculateWaitingOn_MaterialSupplier_Contractor() throws Exception {
        when(contractor.isMaterialSupplierOnly()).thenReturn(true);
        when(contractor.isMaterialSupplier()).thenReturn(true);
        when(contractor.getProductRisk()).thenReturn(null);

        WaitingOn result = testClass.calculateWaitingOn(co);
        assertEquals(WaitingOn.Contractor, result);
    }

    @Test
    public void testCalculateWaitingOn_Deactivated_Contractor() throws Exception {
        when(contractor.getStatus()).thenReturn(AccountStatus.Deactivated);

        WaitingOn result = testClass.calculateWaitingOn(co);
        assertEquals(WaitingOn.Contractor, result);
    }

    @Test
    public void testCalculateWaitingOn_PaymentOverdue_Contractor() throws Exception {
        when(contractor.isPaymentOverdue()).thenReturn(true);

        WaitingOn result = testClass.calculateWaitingOn(co);
        assertEquals(WaitingOn.Contractor, result);
    }

    @Test
    public void testCalculateWaitingOn_WorkStatusPending_Operator() throws Exception {
        when(operator.isAutoApproveRelationships()).thenReturn(false);
        when(contractorOperatorDAO.workStatusIsPending(co)).thenReturn(true);

        WaitingOn result = testClass.calculateWaitingOn(co);
        assertEquals(WaitingOn.Operator, result);
    }

    @Test
    public void testCalculateWaitingOn_WorkStatusRejected_None() throws Exception {
        when(operator.isAutoApproveRelationships()).thenReturn(false);
        when(contractorOperatorDAO.workStatusIsRejected(co)).thenReturn(true);

        WaitingOn result = testClass.calculateWaitingOn(co);
        assertEquals(WaitingOn.None, result);
    }

    @Test
    public void testCalculateWaitingOn_ExpiredAudit_None() throws Exception {
        setupOperatorCriteria(AuditStatus.Complete);
        setupDefaultAudit(AuditStatus.Pending);

        when(audit.isExpired()).thenReturn(true);

        WaitingOn result = testClass.calculateWaitingOn(co);
        assertEquals(WaitingOn.None, result);
    }

    @Test
    public void testCalculateWaitingOn_PendingContractorCanEdit_Contractor() throws Exception {
        setupOperatorCriteria(AuditStatus.Complete);
        setupDefaultAudit(AuditStatus.Pending);

        WaitingOn result = testClass.calculateWaitingOn(co);
        assertEquals(WaitingOn.Contractor, result);
    }

    @Test
    public void testCalculateWaitingOn_PendingOperatorEdit_Operator() throws Exception {
        setupOperatorCriteria(AuditStatus.Complete);
        setupDefaultAudit(AuditStatus.Pending);

        when(auditType.isCanContractorEdit()).thenReturn(false);
        when(auditType.getEditPermission()).thenReturn(OpPerms.ClientAuditEdit);

        WaitingOn result = testClass.calculateWaitingOn(co);
        assertEquals(WaitingOn.Operator, result);
    }

    @Test
    public void testCalculateWaitingOn_PendingPicsEdit_PICS() throws Exception {
        setupOperatorCriteria(AuditStatus.Complete);
        setupDefaultAudit(AuditStatus.Pending);

        when(auditType.isCanContractorEdit()).thenReturn(false);

        WaitingOn result = testClass.calculateWaitingOn(co);
        assertEquals(WaitingOn.PICS, result);
    }

    @Test
    public void testCalculateWaitingOn_NoEditPermissions_PICS() throws Exception {
        setupOperatorCriteria(AuditStatus.Complete);
        setupDefaultAudit(AuditStatus.Pending);

        when(auditType.isCanContractorEdit()).thenReturn(false);
        when(auditType.isImplementation()).thenReturn(false);

        WaitingOn result = testClass.calculateWaitingOn(co);
        assertEquals(WaitingOn.PICS, result);
    }

    @Test
    public void testCalculateWaitingOn_ImplementationAudit_Contractor() throws Exception {
        setupOperatorCriteria(AuditStatus.Complete);
        setupDefaultAudit(AuditStatus.Pending);

        when(auditType.isCanContractorEdit()).thenReturn(false);
        when(auditType.isImplementation()).thenReturn(true);

        WaitingOn result = testClass.calculateWaitingOn(co);
        assertEquals(WaitingOn.Contractor, result);
    }

    @Test
    public void testCalculateWaitingOn_ImplementationAudit_None() throws Exception {
        setupOperatorCriteria(AuditStatus.Complete);
        setupDefaultAudit(AuditStatus.Pending);

        when(auditType.isCanContractorEdit()).thenReturn(false);
        when(auditType.isImplementation()).thenReturn(true);
        when(audit.getScheduledDate()).thenReturn(new Date());

        WaitingOn result = testClass.calculateWaitingOn(co);
        assertEquals(WaitingOn.None, result);
    }

    @Test
    public void testCalculateWaitingOn_SubmittedAudit_PICS() throws Exception {
        setupOperatorCriteria(AuditStatus.Complete);
        setupDefaultAudit(AuditStatus.Submitted);

        WaitingOn result = testClass.calculateWaitingOn(co);
        assertEquals(WaitingOn.PICS, result);
    }

    @Test
    public void testCalculateWaitingOn_SubmittedManualAudit_Contractor() throws Exception {
        setupOperatorCriteria(AuditStatus.Complete);
        setupDefaultAudit(AuditStatus.Submitted);

        when(auditType.getId()).thenReturn(AuditType.MANUAL_AUDIT);

        WaitingOn result = testClass.calculateWaitingOn(co);
        assertEquals(WaitingOn.Contractor, result);
    }

    @Test
    public void testCalculateWaitingOn_SubmittedImplementationAudit_Contractor() throws Exception {
        setupOperatorCriteria(AuditStatus.Complete);
        setupDefaultAudit(AuditStatus.Submitted);

        when(auditType.getId()).thenReturn(AuditType.IMPLEMENTATION_AUDIT);

        WaitingOn result = testClass.calculateWaitingOn(co);
        assertEquals(WaitingOn.Contractor, result);
    }

    @Test
    public void testCalculateWaitingOn_CompletedPolicy_Operator() throws Exception {
        setupOperatorCriteria(AuditStatus.Approved);
        setupDefaultAudit(AuditStatus.Complete);

        WaitingOn result = testClass.calculateWaitingOn(co);
        assertEquals(WaitingOn.Operator, result);
    }

    @Test
    public void testCalculateWaitingOn_SubmittedImplementationAuditNotVerified_Contractor() throws Exception {
        setupOperatorCriteria(AuditStatus.Submitted);
        setupDefaultAudit(AuditStatus.Submitted);

        when(auditType.isImplementation()).thenReturn(true);

        WaitingOn result = testClass.calculateWaitingOn(co);
        assertEquals(WaitingOn.Contractor, result);
    }

    private void setupDefaultAudit(AuditStatus status) {
        when(auditType.getId()).thenReturn(500);
        when(auditType.isCanContractorEdit()).thenReturn(true);
        when(audit.getAuditType()).thenReturn(auditType);
        when(audit.isExpired()).thenReturn(false);

        // setup caos/caop
        ContractorAuditOperator cao = new ContractorAuditOperator();
        ContractorAuditOperatorPermission caop = new ContractorAuditOperatorPermission();
        cao.getCaoPermissions().add(caop);
        cao.setAudit(audit);
        cao.setVisible(true);
        cao.setPercentVerified(0);
        cao.setOperator(operator);
        caop.setOperator(operator);

        List<ContractorAuditOperator> caos = new ArrayList<>();
        caos.add(cao);
        when(audit.getOperators()).thenReturn(caos);

        when(audit.getOperators()).thenReturn(caos);

        auditList.add(audit);
        cao.changeStatus(status, null);
    }

    private void setupOperatorCriteria(AuditStatus status) {
        FlagCriteria fc = new FlagCriteria();
        fc.setId(1);
        fc.setAuditType(auditType);
        fc.setRequiredStatus(status);
        FlagCriteriaOperator fco = new FlagCriteriaOperator();
        fco.setId(1);
        fco.setOperator(operator);
        fco.setCriteria(fc);
        fco.setFlag(FlagColor.Red);

        fcoList.add(fco);
    }

    private void setupDefaultContractor() {
        when(contractor.isMaterialSupplierOnly()).thenReturn(false);
        when(contractor.getSafetyRisk()).thenReturn(LowMedHigh.High);
        when(contractor.getProductRisk()).thenReturn(LowMedHigh.High);
        when(contractor.getStatus()).thenReturn(AccountStatus.Active);
        when(contractor.getAccountLevel()).thenReturn(AccountLevel.Full);
        when(contractor.isPaymentOverdue()).thenReturn(false);
    }

    private void setupDefaultOperator() {
        when(operator.isAutoApproveRelationships()).thenReturn(true);
    }
}
