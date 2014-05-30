package com.picsauditing.report.service;

import com.intuit.developer.adaptors.InsertContractors;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.Currency;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;

import static org.mockito.Mockito.verify;

public class ReportQBServiceTest {
    private ReportQBService reportQBService;

    @Mock
    ContractorAccountDAO contractorAccountDAO;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        reportQBService = new ReportQBService();
        Whitebox.setInternalState(reportQBService, "contractorAccountDAO", contractorAccountDAO);
    }

    @Test
    public void testGetContractorsToInsert() throws Exception {
        String qbListID = "qbListID";
        Currency currency = Currency.USD;
        String whereClause = InsertContractors.getWhereClause(currency);

        reportQBService.getContractorsToInsert(Currency.USD);

        verify(contractorAccountDAO).findWhere(whereClause);
    }

    @Test
    public void testGetInvoicesToInsert() throws Exception {

    }

    @Test
    public void testGetPaymentsToInsert() throws Exception {

    }

    @Test
    public void testGetContractorsForUpdate() throws Exception {

    }

    @Test
    public void testGetInvoicesForUpdate() throws Exception {

    }

    @Test
    public void testGetPaymentsForUpdate() throws Exception {

    }
}
