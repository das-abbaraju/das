package com.picsauditing.actions;

import com.picsauditing.PicsActionTest;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;


public class QBSyncEditTest extends PicsActionTest {

    @MockitoAnnotations.Mock
    private ContractorAccountDAO contractorAccountDAO;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testEditContractor() throws Exception {
        QBSyncEdit qbSyncEdit = new QBSyncEdit();
        ContractorAccount contractorAccount = mock(ContractorAccount.class);
        Whitebox.setInternalState(qbSyncEdit, "clearListID", true);
        Whitebox.setInternalState(qbSyncEdit, "contractorAccountDAO", contractorAccountDAO);

        Whitebox.invokeMethod(qbSyncEdit, "editContractor", contractorAccount);

        assertEquals( contractorAccount.getQbListCHID(),null);
        assertEquals( contractorAccount.getQbListCAID(),null);
        assertEquals( contractorAccount.getQbListEUID(),null);
        assertEquals( contractorAccount.getQbListID(),null);
        assertEquals( contractorAccount.getQbListUKID(),null);
        assertEquals( contractorAccount.getQbListPLID(),null);
    }
}
