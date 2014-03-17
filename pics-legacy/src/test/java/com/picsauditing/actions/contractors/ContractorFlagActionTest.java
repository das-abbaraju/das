package com.picsauditing.actions.contractors;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTest;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.OperatorAccount;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import static org.mockito.Mockito.*;

public class ContractorFlagActionTest extends PicsTest {

    @Mock
    private Permissions permissions;
    @Mock
    private NoteDAO noteDAO;
    @Mock
    private ContractorOperatorDAO contractorOperatorDao;

    ContractorFlagAction contractorFlag;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        contractorFlag = new ContractorFlagAction();

        Whitebox.setInternalState(contractorFlag, "permissions", permissions);
        Whitebox.setInternalState(contractorFlag, "noteDAO", noteDAO);
        Whitebox.setInternalState(contractorFlag, "contractorOperatorDao", contractorOperatorDao);
    }

    @Test
    public void testCompleteAction_OperatorNote_UsesOperatorAccountID() throws Exception {
        ContractorAccount contractor = EntityFactory.makeContractor();
        OperatorAccount operator = EntityFactory.makeOperator();
        OperatorAccount topAccount = EntityFactory.makeOperator();
        ContractorOperator conOp = EntityFactory.addContractorOperator(contractor, operator);

        when(permissions.getAccountId()).thenReturn(operator.getId());
        when(permissions.getTopAccountID()).thenReturn(topAccount.getId());

        Whitebox.setInternalState(contractorFlag, "co", conOp);

        Whitebox.invokeMethod(contractorFlag, "completeAction", "Test note");
        verify(permissions).getAccountId();
        verify(permissions, never()).getTopAccountID();
    }
}
