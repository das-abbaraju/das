package com.intuit.developer.adaptors;

import com.intuit.developer.QBSession;
import com.picsauditing.featuretoggle.Features;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.quickbooks.qbxml.BillAddress;
import com.picsauditing.quickbooks.qbxml.CustomerAdd;
import com.picsauditing.quickbooks.qbxml.ObjectFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.togglz.junit.TogglzRule;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class InsertContractorsTest {

    InsertContractors insertContractors;

    @Rule
    public TogglzRule togglzRule = TogglzRule.allEnabled(Features.class);

    @Mock
    QBSession qbSession;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        insertContractors = new InsertContractors();
    }

    @Test
    public void testGetQbXml() throws Exception {
        //TODO: FixMe
    }

    @Test
    public void testSetBillAddress_ToggleDisabled() throws Exception {
        togglzRule.disable(Features.QUICKBOOKS_INCLUDE_CONTRACTOR_ADDRESS);
        ObjectFactory objectFactory = new ObjectFactory();
        ContractorAccount contractorAccount = new ContractorAccount();
        CustomerAdd customerAdd = mock(CustomerAdd.class);

        Whitebox.invokeMethod(insertContractors, "setBillAddress", objectFactory, contractorAccount, customerAdd);

        verify(customerAdd, never()).setBillAddress(any(BillAddress.class));
    }

    @Test
    public void testSetBillAddress_ToggleEnabled() throws Exception {
        togglzRule.enable(Features.QUICKBOOKS_INCLUDE_CONTRACTOR_ADDRESS);
        ObjectFactory objectFactory = mock(ObjectFactory.class);
        ContractorAccount contractorAccount = mock(ContractorAccount.class);
        BillAddress billAddress = mock(BillAddress.class);
        User user = mock(User.class);
        CustomerAdd customerAdd = mock(CustomerAdd.class);

        when(contractorAccount.getPrimaryContact()).thenReturn(user);
        when(customerAdd.getBillAddress()).thenReturn(billAddress);

        Whitebox.invokeMethod(insertContractors, "setBillAddress", objectFactory, contractorAccount, customerAdd);

        verify(customerAdd, times(2)).setBillAddress(any(BillAddress.class));
    }


}