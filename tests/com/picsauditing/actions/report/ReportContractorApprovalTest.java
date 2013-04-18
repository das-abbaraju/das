package com.picsauditing.actions.report;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsActionTest;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.jpa.entities.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class ReportContractorApprovalTest extends PicsActionTest {
    private ReportContractorApproval reportContractorApproval;

    @Mock
    private ContractorOperatorDAO contractorOperatorDAO;

    @Before
    public void setupTest() throws Exception {
        reportContractorApproval = new ReportContractorApproval();
        MockitoAnnotations.initMocks(this);
        Whitebox.setInternalState(reportContractorApproval, "contractorOperatorDAO", contractorOperatorDAO);
        super.setUp(reportContractorApproval);
    }

    @Test
    public void testApproveContractor_SingleOperatorPendingToApproved() throws Exception {
        ContractorAccount contractor = EntityFactory.makeContractor();
        contractor.setOperators(new ArrayList<ContractorOperator>());

        when(permissions.isOperator()).thenReturn(true);

        OperatorAccount operator = EntityFactory.makeOperator();
        operator.setType(Account.OPERATOR_ACCOUNT_TYPE);

        ContractorOperator contractorOperator = new ContractorOperator();
        contractorOperator.setContractorAccount(contractor);
        contractorOperator.setOperatorAccount(operator);
        contractorOperator.setWorkStatus(ApprovalStatus.P);

        contractor.getOperators().add(contractorOperator);

        reportContractorApproval.approveContractor(contractor, operator.getId(), ApprovalStatus.Y);

        assertEquals(ApprovalStatus.Y, contractorOperator.getWorkStatus());
    }

    @Test
    public void testApproveContractor_ChildAndCorporateOperatorPendingToApproved() throws Exception {
        ContractorAccount contractor = EntityFactory.makeContractor();
        contractor.setOperators(new ArrayList<ContractorOperator>());

        when(permissions.isOperator()).thenReturn(true);

        OperatorAccount childOperator = EntityFactory.makeOperator();
        childOperator.setType(Account.OPERATOR_ACCOUNT_TYPE);

        ContractorOperator childOperatorContractor = new ContractorOperator();
        childOperatorContractor.setContractorAccount(contractor);
        childOperatorContractor.setOperatorAccount(childOperator);
        childOperatorContractor.setWorkStatus(ApprovalStatus.P);

        contractor.getOperators().add(childOperatorContractor);

        OperatorAccount parentOperator = EntityFactory.makeOperator();
        parentOperator.setType(Account.CORPORATE_ACCOUNT_TYPE);

        ContractorOperator parentOperatorContractor = new ContractorOperator();
        parentOperatorContractor.setContractorAccount(contractor);
        parentOperatorContractor.setOperatorAccount(parentOperator);
        parentOperatorContractor.setWorkStatus(ApprovalStatus.P);

        childOperator.setParent(parentOperator);

        contractor.getOperators().add(parentOperatorContractor);

        reportContractorApproval.approveContractor(contractor, childOperator.getId(), ApprovalStatus.Y);

        assertEquals(ApprovalStatus.Y, childOperatorContractor.getWorkStatus());
        assertEquals(ApprovalStatus.Y, parentOperatorContractor.getWorkStatus());
    }

    @Test
    public void testApproveContractor_CorporateWithTwoChildAccounts() throws Exception {
        ContractorAccount contractor = EntityFactory.makeContractor();
        contractor.setOperators(new ArrayList<ContractorOperator>());

        when(permissions.isOperator()).thenReturn(false);

        OperatorAccount childOperatorOne = EntityFactory.makeOperator();
        childOperatorOne.setType(Account.OPERATOR_ACCOUNT_TYPE);

        ContractorOperator childOperatorApproved = new ContractorOperator();
        childOperatorApproved.setContractorAccount(contractor);
        childOperatorApproved.setOperatorAccount(childOperatorOne);
        childOperatorApproved.setWorkStatus(ApprovalStatus.Y);

        contractor.getOperators().add(childOperatorApproved);

        OperatorAccount childOperatorTwo = EntityFactory.makeOperator();
        childOperatorTwo.setType(Account.OPERATOR_ACCOUNT_TYPE);

        ContractorOperator childOperatorPending = new ContractorOperator();
        childOperatorPending.setContractorAccount(contractor);
        childOperatorPending.setOperatorAccount(childOperatorTwo);
        childOperatorPending.setWorkStatus(ApprovalStatus.P);

        contractor.getOperators().add(childOperatorPending);

        OperatorAccount parentOperator = EntityFactory.makeOperator();
        parentOperator.setType(Account.CORPORATE_ACCOUNT_TYPE);

        ContractorOperator parentOperatorPending = new ContractorOperator();
        parentOperatorPending.setContractorAccount(contractor);
        parentOperatorPending.setOperatorAccount(parentOperator);
        parentOperatorPending.setWorkStatus(ApprovalStatus.P);

        contractor.getOperators().add(parentOperatorPending);

        childOperatorOne.setParent(parentOperator);
        childOperatorTwo.setParent(parentOperator);

        parentOperator.setChildOperators(new ArrayList<OperatorAccount>());
        parentOperator.getChildOperators().add(childOperatorOne);
        parentOperator.getChildOperators().add(childOperatorTwo);

        reportContractorApproval.approveContractor(contractor, parentOperator.getId(), ApprovalStatus.Y);

        assertEquals(ApprovalStatus.Y, childOperatorApproved.getWorkStatus());
        assertEquals(ApprovalStatus.YF, childOperatorPending.getWorkStatus());
        assertEquals(ApprovalStatus.YF, parentOperatorPending.getWorkStatus());
    }
}
