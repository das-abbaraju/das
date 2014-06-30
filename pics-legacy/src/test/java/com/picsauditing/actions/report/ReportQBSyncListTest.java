package com.picsauditing.actions.report;

import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Currency;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ReportQBSyncListTest {
    private int id = 0;
    @Mock
    private ReportQBSyncList reportQBSyncList;
    @Mock
    private ContractorAccount mockContractor;
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSetQBData_CAD() throws Exception {
        when(mockContractor.getQbListCAID()).thenReturn(null);

        Whitebox.invokeMethod(reportQBSyncList, "setQBData", mockContractor, Currency.CAD);

        verify(mockContractor).setQbListCAID("NOLOAD" + id);
    }

    @Test
    public void testSetQBData_GBP() throws Exception {
        when(mockContractor.getQbListUKID()).thenReturn(null);

        Whitebox.invokeMethod(reportQBSyncList, "setQBData", mockContractor, Currency.GBP);

        verify(mockContractor).setQbListUKID("NOLOAD" + id);
    }

    @Test
    public void testSetQBData_EUR() throws Exception {
        when(mockContractor.getQbListEUID()).thenReturn(null);

        Whitebox.invokeMethod(reportQBSyncList, "setQBData", mockContractor, Currency.EUR);

        verify(mockContractor).setQbListEUID("NOLOAD" + id);
    }

    @Test
    public void testSetQBData_US() throws Exception {
        when(mockContractor.getQbListID()).thenReturn(null);

        Whitebox.invokeMethod(reportQBSyncList, "setQBData", mockContractor, Currency.USD);

        verify(mockContractor).setQbListID("NOLOAD" + id);
    }

    @Test
    public void testSetQBData_CHF() throws Exception {
        when(mockContractor.getQbListCHID()).thenReturn(null);

        Whitebox.invokeMethod(reportQBSyncList, "setQBData", mockContractor, Currency.CHF);

        verify(mockContractor).setQbListCHID("NOLOAD" + id);
    }

    @Test
    public void testSetQBData_PLN() throws Exception {
        when(mockContractor.getQbListPLID()).thenReturn(null);

        Whitebox.invokeMethod(reportQBSyncList, "setQBData", mockContractor, Currency.PLN);

        verify(mockContractor).setQbListPLID("NOLOAD" + id);
    }
}