package com.intuit.developer.adaptors;

import com.intuit.developer.QBSession;
import com.picsauditing.featuretoggle.Features;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Currency;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.quickbooks.qbxml.*;
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

    public static final String CUSTOMER_LIST_ID = "12345";
    InsertContractors insertContractors;

    @Rule
    public TogglzRule togglzRule = TogglzRule.allEnabled(Features.class);

    @Mock
    QBSession qbSession;

    @Mock
    QBXML qbxml;

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
    public void testSetQBListID_CHF() throws Exception {
        QBSession currentSession = mock(QBSession.class);
        CustomerRet customer = mock(CustomerRet.class);
        ContractorAccount contractorAccount = mock(ContractorAccount.class);

        when(currentSession.getCurrency()).thenReturn(Currency.CHF);
        when(customer.getListID()).thenReturn(CUSTOMER_LIST_ID);

        Whitebox.invokeMethod(insertContractors, "setQBListID", currentSession, customer, contractorAccount);

        verify(contractorAccount).setQbListCHFID(customer.getListID());
    }

    @Test
    public void testSetQBListID_USD() throws Exception {
        QBSession currentSession = mock(QBSession.class);
        CustomerRet customer = mock(CustomerRet.class);
        ContractorAccount contractorAccount = mock(ContractorAccount.class);

        when(currentSession.getCurrency()).thenReturn(Currency.USD);
        when(customer.getListID()).thenReturn(CUSTOMER_LIST_ID);

        Whitebox.invokeMethod(insertContractors, "setQBListID", currentSession, customer, contractorAccount);

        verify(contractorAccount).setQbListID(customer.getListID());
    }

    @Test
    public void testSetQBListID_CAD() throws Exception {
        QBSession currentSession = mock(QBSession.class);
        CustomerRet customer = mock(CustomerRet.class);
        ContractorAccount contractorAccount = mock(ContractorAccount.class);

        when(currentSession.getCurrency()).thenReturn(Currency.CAD);
        when(customer.getListID()).thenReturn(CUSTOMER_LIST_ID);

        Whitebox.invokeMethod(insertContractors, "setQBListID", currentSession, customer, contractorAccount);

        verify(contractorAccount).setQbListCAID(customer.getListID());
    }

    @Test
    public void testSetQBListID_GBP() throws Exception {
        QBSession currentSession = mock(QBSession.class);
        CustomerRet customer = mock(CustomerRet.class);
        ContractorAccount contractorAccount = mock(ContractorAccount.class);

        when(currentSession.getCurrency()).thenReturn(Currency.GBP);
        when(customer.getListID()).thenReturn(CUSTOMER_LIST_ID);

        Whitebox.invokeMethod(insertContractors, "setQBListID", currentSession, customer, contractorAccount);

        verify(contractorAccount).setQbListUKID(customer.getListID());
    }

    @Test
    public void testSetQBListID_EUR() throws Exception {
        QBSession currentSession = mock(QBSession.class);
        CustomerRet customer = mock(CustomerRet.class);
        ContractorAccount contractorAccount = mock(ContractorAccount.class);

        when(currentSession.getCurrency()).thenReturn(Currency.EUR);
        when(customer.getListID()).thenReturn(CUSTOMER_LIST_ID);

        Whitebox.invokeMethod(insertContractors, "setQBListID", currentSession, customer, contractorAccount);

        verify(contractorAccount).setQbListEUID(customer.getListID());
    }


    @Test
    public void testSetBillAddress_ToggleEnabled() throws Exception {
        togglzRule.enable(Features.QUICKBOOKS_EXCLUDE_CONTRACTOR_ADDRESS);
        ObjectFactory objectFactory = new ObjectFactory();
        ContractorAccount contractorAccount = new ContractorAccount();
        CustomerAdd customerAdd = mock(CustomerAdd.class);

        Whitebox.invokeMethod(insertContractors, "setBillAddress", objectFactory, contractorAccount, customerAdd);

        verify(customerAdd, never()).setBillAddress(any(BillAddress.class));
    }

    @Test
    public void testSetBillAddress_ToggleDisabled() throws Exception {
        togglzRule.disable(Features.QUICKBOOKS_EXCLUDE_CONTRACTOR_ADDRESS);
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
