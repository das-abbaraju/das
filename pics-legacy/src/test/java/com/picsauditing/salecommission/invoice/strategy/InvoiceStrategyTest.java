package com.picsauditing.salecommission.invoice.strategy;


import com.picsauditing.dao.BasicDAO;
import com.picsauditing.dao.InvoiceCommissionDAO;
import com.picsauditing.jpa.entities.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class InvoiceStrategyTest {

    public InvoiceStrategy invoiceStrategy = new InvoiceStrategy();
    public UpdateInvoiceStrategy updateInvoiceStrategy = new UpdateInvoiceStrategy();
    public VoidInvoiceCommissionStrategy voidInvoiceCommissionStrategy = new VoidInvoiceCommissionStrategy();

    @Mock
    InvoiceCommissionDAO invoiceCommissionDAO;
    @Mock
    Invoice invoice;
    @Mock
    private InvoiceCommission invoiceCommission;
    @Mock
    private PaymentCommission paymentCommission;
    @Mock
    private ContractorOperator contractorOperator1;
    @Mock
    private ContractorOperator contractorOperator2;

    private List<InvoiceCommission> invoiceCommissions;
    private List<PaymentCommission> paymentCommissions;
    private Map<ContractorOperator, Double> clientRevenueWeights;

    @Before
	public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Whitebox.setInternalState(invoiceStrategy, "invoiceCommissionDAO", invoiceCommissionDAO);

        Whitebox.setInternalState(updateInvoiceStrategy, "invoiceCommissionDAO", invoiceCommissionDAO);
        Whitebox.setInternalState(updateInvoiceStrategy, "invoiceStrategy", mock(InvoiceStrategy.class));

        Whitebox.setInternalState(voidInvoiceCommissionStrategy, "invoiceCommissionDAO", invoiceCommissionDAO);


        when(invoice.getId()).thenReturn(10);

        invoiceCommissions = new ArrayList<>();
        invoiceCommissions.add(invoiceCommission);
        paymentCommissions = new ArrayList<>();
        paymentCommissions.add(paymentCommission);
        clientRevenueWeights = new HashMap<>();
        clientRevenueWeights.put(contractorOperator1, .250);
        clientRevenueWeights.put(contractorOperator2, .500);
    }

    @Test
    public void testSaveStrategy_hasStrategyAlreadyProcessed() {
        invoiceStrategy.hasStrategyAlreadyProcessed(invoice);
        verify(invoiceCommissionDAO).findInvoiceCommissionsByInvoiceId(10);
    }

    @Test
    public void testSaveStrategy_generateInvoiceOperatorCommissions() throws Exception {
        Whitebox.invokeMethod(invoiceStrategy, "generateInvoiceOperatorCommissions", invoice, clientRevenueWeights);
        verify(invoiceCommissionDAO, times(2)).save(any(InvoiceOperatorCommission.class));
    }

    @Test
    public void testUpdateStrategy_processInvoiceCommission() {
        updateInvoiceStrategy.processInvoiceCommission(invoice);
        verify(invoiceCommissionDAO).deleteData(InvoiceCommission.class, "t.invoice.id = 10");
        verify(invoiceCommissionDAO).deleteData(InvoiceOperatorCommission.class, "t.invoice.id = 10");
    }

    @Test
    public void testVoidStrategy_hasStrategyAlreadyProcessed() {
        voidInvoiceCommissionStrategy.hasStrategyAlreadyProcessed(invoice);
        verify(invoiceCommissionDAO).findInvoiceCommissionNotVoid(10);
    }

    @Test
    public void testVoidStrategy_buildInvoiceCommissions() {
        voidInvoiceCommissionStrategy.buildInvoiceCommissions(invoice);
        verify(invoiceCommissionDAO).findInvoiceCommissionsByInvoiceId(10);
    }

    @Test
    public void testVoidStrategy_zeroOutInvoiceCommission() throws Exception {
        Whitebox.invokeMethod(voidInvoiceCommissionStrategy, "zeroOutInvoiceCommission", invoiceCommissions);
        verify(invoiceCommission).setPoints(BigDecimal.ZERO);
        verify(invoiceCommission).setRevenuePercent(BigDecimal.ZERO);
    }

    @Test
    public void testVoidStrategy_zeroOutPaymentCommissions() throws Exception {
        Whitebox.invokeMethod(voidInvoiceCommissionStrategy, "zeroOutPaymentCommissions", paymentCommissions);
        verify(paymentCommission).setActivationPoints(BigDecimal.ZERO);
        verify(paymentCommission).setPaymentAmount(BigDecimal.ZERO);
    }
}
