package com.picsauditing.dao;

import com.picsauditing.EntityFactory;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.notes.ActivityBean;
import com.picsauditing.jpa.entities.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class NoteDAOTest {
    private NoteDAO dao;

    private ContractorAccount contractor;

    @Mock
    private Permissions permissions;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        dao = new NoteDAO();
        contractor = EntityFactory.makeContractor();
    }

    @Test
    public void testAccumulateWorkflowChanges_Contractor() throws Exception {
        List<ActivityBean> beans = new ArrayList<>();
        NoteCategory[] filterCategory = new NoteCategory[1];
        filterCategory[0] = NoteCategory.Audits;

        OperatorAccount operator = EntityFactory.makeOperator();

        List<ContractorAuditOperatorWorkflow> workflows = new ArrayList<>();
        workflows.add(createWorkflow(100, operator, AuditStatus.Complete, AuditStatus.Pending));

        when(permissions.isContractor()).thenReturn(true);

        Whitebox.invokeMethod(dao, "accumulateWorkflowChanges", beans, workflows, filterCategory, permissions);
        assertEquals(1, beans.size());

    }

    @Test
    public void testAccumulateWorkflowChanges_Admin() throws Exception {
        List<ActivityBean> beans = new ArrayList<>();
        NoteCategory[] filterCategory = new NoteCategory[1];
        filterCategory[0] = NoteCategory.Audits;

        OperatorAccount operator = EntityFactory.makeOperator();

        List<ContractorAuditOperatorWorkflow> workflows = new ArrayList<>();
        workflows.add(createWorkflow(100, operator, AuditStatus.Complete, AuditStatus.Pending));

        when(permissions.isAdmin()).thenReturn(true);

        Whitebox.invokeMethod(dao, "accumulateWorkflowChanges", beans, workflows, filterCategory, permissions);
        assertEquals(1, beans.size());

    }

    @Test
    public void testAccumulateWorkflowChanges_NonApplicableOperator() throws Exception {
        List<ActivityBean> beans = new ArrayList<>();
        NoteCategory[] filterCategory = new NoteCategory[1];
        filterCategory[0] = NoteCategory.Audits;

        OperatorAccount operator = EntityFactory.makeOperator();

        List<ContractorAuditOperatorWorkflow> workflows = new ArrayList<>();
        workflows.add(createWorkflow(100, operator, AuditStatus.Complete, AuditStatus.Pending));

        Set<Integer> childrenIds = new HashSet<>();
        childrenIds.add(1000);

        when(permissions.getOperatorChildren()).thenReturn(childrenIds);

        Whitebox.invokeMethod(dao, "accumulateWorkflowChanges", beans, workflows, filterCategory, permissions);
        assertEquals(0, beans.size());

    }

    @Test
    public void testAccumulateWorkflowChanges_ApplicableOperator() throws Exception {
        List<ActivityBean> beans = new ArrayList<>();
        NoteCategory[] filterCategory = new NoteCategory[1];
        filterCategory[0] = NoteCategory.Audits;

        OperatorAccount operator = EntityFactory.makeOperator();

        List<ContractorAuditOperatorWorkflow> workflows = new ArrayList<>();
        workflows.add(createWorkflow(100, operator, AuditStatus.Complete, AuditStatus.Pending));

        Set<Integer> childrenIds = new HashSet<>();
        childrenIds.add(operator.getId());

        when(permissions.getOperatorChildren()).thenReturn(childrenIds);

        Whitebox.invokeMethod(dao, "accumulateWorkflowChanges", beans, workflows, filterCategory, permissions);
        assertEquals(1, beans.size());

    }

    private ContractorAuditOperatorWorkflow createWorkflow(int auditType, OperatorAccount operator, AuditStatus status, AuditStatus oldStatus) {
        ContractorAudit audit = EntityFactory.makeContractorAudit(auditType, contractor);
        audit.getAuditType().setClassType(AuditTypeClass.Audit);

        ContractorAuditOperator cao = EntityFactory.addCao(audit, operator);
        cao.setAudit(audit);
        cao.changeStatus(status, null);
        audit.getOperators().add(cao);

        ContractorAuditOperatorPermission caop = new ContractorAuditOperatorPermission();
        caop.setOperator(operator);
        caop.setCao(cao);
        cao.getCaoPermissions().add(caop);

        ContractorAuditOperatorWorkflow caow = new ContractorAuditOperatorWorkflow();
        caow.setCao(cao);
        caow.setStatus(status);
        caow.setPreviousStatus(oldStatus);
        cao.getCaoWorkflow().add(caow);

        return caow;
    }

}
