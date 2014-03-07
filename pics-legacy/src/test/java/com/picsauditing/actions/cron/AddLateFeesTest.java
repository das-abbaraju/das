package com.picsauditing.actions.cron;

import com.picsauditing.PICS.BillingService;
import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.dao.InvoiceItemDAO;
import com.picsauditing.jpa.entities.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AddLateFeesTest {

    private AddLateFees cron;
    @Mock
    private ContractorAccount mockContractorAccount;
    @Mock
    private Invoice mockInvoice;
    @Mock
    private InvoiceItem mockInvoiceItem;
    @Mock
    private InvoiceFee mockInvoiceFee;
    @Mock
    private InvoiceDAO invoiceDAO;
    @Mock
    private InvoiceItemDAO invoiceItemDAO;
    @Mock
    private InvoiceFeeDAO invoiceFeeDAO;
    @Mock
    private BillingService billingService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        cron = new AddLateFees();
        cron.invoiceDAO = invoiceDAO;
        cron.invoiceFeeDAO = invoiceFeeDAO;
        cron.invoiceItemDAO = invoiceItemDAO;
        cron.billingService = billingService;
    }

    @Test
    public void testCalculateLateFeeFor() {
        BigDecimal invoiceFeeAmount = new BigDecimal(100);
        BigDecimal expectedLateFee = new BigDecimal(AddLateFees.MINIMUM_LATE_FEE);
        when(mockInvoice.getTotalAmount()).thenReturn(invoiceFeeAmount);
        BigDecimal actualLateFee = cron.calculateLateFeeFor(mockInvoice);
        assertEquals(expectedLateFee, actualLateFee);
    }

    @Test
    public void createLateFeeInvoiceItem() {
        when(invoiceFeeDAO.findByNumberOfOperatorsAndClass(FeeClass.LateFee, 0)).thenReturn(mockInvoiceFee);
        when(mockInvoiceFee.getFeeClass()).thenReturn(FeeClass.LateFee);
        when(mockInvoice.getAccount()).thenReturn(mockContractorAccount);
        BigDecimal invoiceFeeAmount = new BigDecimal(100);
        when(mockInvoice.getTotalAmount()).thenReturn(invoiceFeeAmount);
        BigDecimal lateFee = cron.calculateLateFeeFor(mockInvoice);
        InvoiceItem lateFeeInvoiceItem = cron.createLateFeeInvoiceItem(mockInvoice, lateFee);
        assertEquals(FeeClass.LateFee, lateFeeInvoiceItem.getInvoiceFee().getFeeClass());
        assertEquals(lateFee, lateFeeInvoiceItem.getAmount());
        assertEquals(lateFee, lateFeeInvoiceItem.getOriginalAmount());
    }

    @Test
    public void testGenerateLateFeeInvoice() {
        when(invoiceFeeDAO.findByNumberOfOperatorsAndClass(FeeClass.LateFee, 20)).thenReturn(mockInvoiceFee);
        when(mockInvoiceFee.getFeeClass()).thenReturn(FeeClass.LateFee);
        when(mockInvoice.getAccount()).thenReturn(mockContractorAccount);
        Date invoiceDueDate = new Date();
        when(mockInvoice.getDueDate()).thenReturn(invoiceDueDate);
        BigDecimal invoiceFeeAmount = new BigDecimal(100);
        Currency contractorCurrency = Currency.CAD;
        when(mockContractorAccount.getCurrency()).thenReturn(contractorCurrency);
        when(mockContractorAccount.getPayingFacilities()).thenReturn(20);
        when(mockInvoice.getTotalAmount()).thenReturn(invoiceFeeAmount);
        BigDecimal lateFee = cron.calculateLateFeeFor(mockInvoice);
        InvoiceItem lateFeeInvoiceItem = cron.createLateFeeInvoiceItem(mockInvoice, lateFee);
        Invoice lateInvoice = cron.generateLateFeeInvoice(mockInvoice, lateFeeInvoiceItem);

        assertEquals(mockInvoice.getAccount(), lateInvoice.getAccount());
        assertEquals(lateFeeInvoiceItem, lateInvoice.getItems().get(0));
        assertEquals(lateFee, lateInvoice.getTotalAmount());
        assertEquals(contractorCurrency,lateInvoice.getCurrency());
        assertEquals(InvoiceType.LateFee, lateInvoice.getInvoiceType());
        assertEquals(20, lateInvoice.getPayingFacilities());
        assertEquals(FeeClass.LateFee, lateInvoice.getItems().get(0).getInvoiceFee().getFeeClass());
        assertEquals(invoiceDueDate.getDay(), lateInvoice.getDueDate().getDay());
        assertEquals(invoiceDueDate.getMonth(), lateInvoice.getDueDate().getMonth());
        assertEquals(invoiceDueDate.getYear(), lateInvoice.getDueDate().getYear());
    }

    @Test
    public void testSaveLateFeeInvoiceAndRelated() throws Exception {
        Invoice invoice = Whitebox.invokeMethod(cron, "saveLateFeeInvoiceAndRelated", mockInvoice, mockInvoiceItem, mockInvoice);
        verify(billingService).doFinalFinancialCalculationsBeforeSaving(mockInvoice);
        verify(billingService).verifyAndSaveInvoice(mockInvoice);
    }

}
