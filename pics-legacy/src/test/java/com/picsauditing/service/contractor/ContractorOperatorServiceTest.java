package com.picsauditing.service.contractor;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.jpa.entities.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class ContractorOperatorServiceTest extends PicsTest {
    ContractorOperatorService service;

    @Mock
    protected ContractorOperatorDAO contractorOperatorDAO;
    @Mock
    private ContractorAccount contractor;
    @Mock
    private OperatorAccount operator;
    @Mock
    private OperatorAccount parentOperator;

    private List<ContractorOperator> conOps;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        conOps = new ArrayList<>();

        service = new ContractorOperatorService();
        PicsTestUtil.forceSetPrivateField(service, "contractorOperatorDAO", contractorOperatorDAO);
        when(contractor.getId()).thenReturn(1);
        when(operator.getId()).thenReturn(2);
        when(parentOperator.getId()).thenReturn(3);when(operator.getParent()).thenReturn(parentOperator);
        when(contractorOperatorDAO.findWhere(anyString())).thenReturn(conOps);
    }

    @Test
    public void testCascadeWorkStatusToParent_AlreadyApproved() throws Exception {
        ContractorOperator conOp = makeContractorOperator(contractor, operator, ApprovalStatus.Y);
        ContractorOperator conParentOp = makeContractorOperator(contractor, parentOperator, ApprovalStatus.Y);

        when(contractorOperatorDAO.find(contractor.getId(), parentOperator.getId())).thenReturn(conParentOp);

        service.cascadeWorkStatusToParent(conOp);
        assertTrue(ApprovalStatus.Y.equals(conParentOp.getWorkStatus()));
    }

    @Test
    public void testCascadeWorkStatusToParent_ApprovedGreaterThanCorp() throws Exception {
        ContractorOperator conOp = makeContractorOperator(contractor, operator, ApprovalStatus.Y);
        ContractorOperator conParentOp = makeContractorOperator(contractor, parentOperator, ApprovalStatus.P);

        when(contractorOperatorDAO.find(contractor.getId(), parentOperator.getId())).thenReturn(conParentOp);

        service.cascadeWorkStatusToParent(conOp);
        assertTrue(ApprovalStatus.Y.equals(conParentOp.getWorkStatus()));
    }

    @Test
    public void testCascadeWorkStatusToParent_Uniform() throws Exception {
        ContractorOperator conOp = makeContractorOperator(contractor, operator, ApprovalStatus.P);
        ContractorOperator conParentOp = makeContractorOperator(contractor, parentOperator, ApprovalStatus.Y);

        when(contractorOperatorDAO.find(contractor.getId(), parentOperator.getId())).thenReturn(conParentOp);
        when(operator.getApprovesRelationships()).thenReturn(YesNo.Yes);
        conOps.add(makeContractorOperator(contractor, operator, ApprovalStatus.P));
        conOps.add(makeContractorOperator(contractor, operator, ApprovalStatus.P));
        conOps.add(makeContractorOperator(contractor, operator, ApprovalStatus.P));

        service.cascadeWorkStatusToParent(conOp);
        assertTrue(ApprovalStatus.P.equals(conParentOp.getWorkStatus()));
    }

    @Test
    public void testCascadeWorkStatusToParent_NonUniform() throws Exception {
        ContractorOperator conOp = makeContractorOperator(contractor, operator, ApprovalStatus.P);
        ContractorOperator conParentOp = makeContractorOperator(contractor, parentOperator, ApprovalStatus.Y);

        when(contractorOperatorDAO.find(contractor.getId(), parentOperator.getId())).thenReturn(conParentOp);
        when(operator.getApprovesRelationships()).thenReturn(YesNo.Yes);
        conOps.add(makeContractorOperator(contractor, operator, ApprovalStatus.Y));
        conOps.add(makeContractorOperator(contractor, operator, ApprovalStatus.Y));
        conOps.add(makeContractorOperator(contractor, operator, ApprovalStatus.P));

        service.cascadeWorkStatusToParent(conOp);
        assertTrue(ApprovalStatus.Y.equals(conParentOp.getWorkStatus()));
    }

    private ContractorOperator makeContractorOperator(ContractorAccount contractor, OperatorAccount operator, ApprovalStatus status) {
        ContractorOperator conOp = new ContractorOperator();
        conOp.setContractorAccount(contractor);
        conOp.setOperatorAccount(operator);
        conOp.setWorkStatus(status);

        return conOp;
    }

    @Test
    public void testAreAllContractorRelationshipsUniform_DoesNotApprovesRelations() throws Exception {
        when(operator.getApprovesRelationships()).thenReturn(YesNo.No);
        conOps.add(makeContractorOperator(contractor, operator, ApprovalStatus.P));
        conOps.add(makeContractorOperator(contractor, operator, ApprovalStatus.P));
        conOps.add(makeContractorOperator(contractor, operator, ApprovalStatus.P));

        assertTrue(service.areAllContractorRelationshipsUniform(operator));
    }

    @Test
    public void testAreAllContractorRelationshipsUniform_Uniform() throws Exception {
        when(operator.getApprovesRelationships()).thenReturn(YesNo.Yes);
        conOps.add(makeContractorOperator(contractor, operator, ApprovalStatus.P));
        conOps.add(makeContractorOperator(contractor, operator, ApprovalStatus.P));
        conOps.add(makeContractorOperator(contractor, operator, ApprovalStatus.P));

        assertTrue(service.areAllContractorRelationshipsUniform(operator));
    }


    @Test
    public void testAreAllContractorRelationshipsUniform_NonUniform() throws Exception {
        when(operator.getApprovesRelationships()).thenReturn(YesNo.Yes);
        conOps.add(makeContractorOperator(contractor, operator, ApprovalStatus.Y));
        conOps.add(makeContractorOperator(contractor, operator, ApprovalStatus.P));
        conOps.add(makeContractorOperator(contractor, operator, ApprovalStatus.P));

        assertFalse(service.areAllContractorRelationshipsUniform(operator));
    }
}
