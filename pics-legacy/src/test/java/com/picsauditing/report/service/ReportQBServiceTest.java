package com.picsauditing.report.service;

import com.intuit.developer.adaptors.*;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.dao.PaymentDAO;
import com.picsauditing.jpa.entities.Currency;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Mockito.verify;

public class ReportQBServiceTest {

    private ReportQBService reportQBService;

    @Mock
    ContractorAccountDAO contractorAccountDAO;
    @Mock
    InvoiceDAO invoiceDAO;
    @Mock
    PaymentDAO paymentDAO;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        reportQBService = new ReportQBService();
        Whitebox.setInternalState(reportQBService, "contractorAccountDAO", contractorAccountDAO);
        Whitebox.setInternalState(reportQBService, "invoiceDAO", invoiceDAO);
        Whitebox.setInternalState(reportQBService, "paymentDAO", paymentDAO);
    }

    @Test
    public void testGetContractorsToInsert() throws Exception {
        Currency currency = Currency.USD;
        String whereClause = InsertContractors.getWhereClause(currency);

        reportQBService.getContractorsToInsert(currency);

        verify(contractorAccountDAO).findWhere(whereClause);
    }

    @Test
    public void testGetInvoicesToInsert() throws Exception {
        Currency currency = Currency.USD;
        String whereClause = InsertInvoices.getWhereClause(currency);

        reportQBService.getInvoicesToInsert(currency);

        verify(invoiceDAO).findWhere(whereClause, ReportQBService.ROW_LIMIT);
    }

    @Test
    public void testGetPaymentsToInsert() throws Exception {
        Currency currency = Currency.USD;
        String whereClause = InsertPayments.getWhereClause(currency);

        reportQBService.getPaymentsToInsert(currency);

        verify(paymentDAO).findWhere(whereClause, ReportQBService.ROW_LIMIT);
    }

    @Test
    public void testGetContractorsForUpdate() throws Exception {
        Currency currency = Currency.USD;
        String whereClause = GetContractorsForUpdate.getWhereClause(currency);

        reportQBService.getContractorsForUpdate(currency);

        verify(contractorAccountDAO).findWhere(whereClause);
    }

    @Test
    public void testGetInvoicesForUpdate() throws Exception {
        Currency currency = Currency.USD;
        String whereClause = GetInvoicesForUpdate.getWhereClause(currency);

        reportQBService.getInvoicesForUpdate(currency);

        verify(invoiceDAO).findWhere(whereClause, ReportQBService.ROW_LIMIT);
    }

    @Test
    public void testGetPaymentsForUpdate() throws Exception {
        Currency currency = Currency.USD;
        String whereClause = GetPaymentsForUpdate.getWhereClause(currency);

        reportQBService.getPaymentsForUpdate(currency);

        verify(paymentDAO).findWhere(whereClause, ReportQBService.ROW_LIMIT);
    }
}
