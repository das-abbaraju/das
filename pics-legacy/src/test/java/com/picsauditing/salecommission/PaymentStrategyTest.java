package com.picsauditing.salecommission;

import com.picsauditing.PICS.data.PaymentDataEvent;
import com.picsauditing.dao.InvoiceCommissionDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.salecommission.payment.strategy.PaymentStrategy;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class PaymentStrategyTest {

	private PaymentStrategy paymentStrategy;

    @Mock
    private InvoiceCommissionDAO invoiceCommissionDAO;
    @Mock
    private PaymentAppliedToInvoice paymentApplied;
    @Mock
    private Payment payment;
    @Mock
    private Invoice invoice;
    @Mock
    private InvoiceCommission invoiceCommission;
    @Mock
    private InvoiceOperatorCommission invoiceOperatorCommission;

    private List<InvoiceCommission> invoiceCommissions;
    private List<InvoiceOperatorCommission> invoiceOperatorCommissions;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

        paymentStrategy = new PaymentStrategy();

		Whitebox.setInternalState(paymentStrategy, "invoiceCommissionDAO", invoiceCommissionDAO);

        when(paymentApplied.getPayment()).thenReturn(payment);
        when(paymentApplied.getInvoice()).thenReturn(invoice);
        when(payment.getId()).thenReturn(2);
        when(invoice.getId()).thenReturn(1);

        invoiceCommissions = new ArrayList<>();
        invoiceCommissions.add(invoiceCommission);
        invoiceOperatorCommissions = new ArrayList<>();
        invoiceOperatorCommissions.add(invoiceOperatorCommission);
    }

    @Test
    public void testProcessPaymentCommission_PaymentProcessed() {
        when(invoiceCommissionDAO.findInvoiceCommissionsByInvoiceId(1)).thenReturn(invoiceCommissions);
        when(invoiceCommission.getPoints()).thenReturn(BigDecimal.ONE);
        when(invoiceCommission.getInvoice()).thenReturn(invoice);
        when(invoiceCommission.getRevenuePercent()).thenReturn(BigDecimal.TEN);
        when(invoice.getTotalAmount()).thenReturn(BigDecimal.TEN);
        when(invoice.getCommissionableAmount()).thenReturn(BigDecimal.TEN);
        paymentStrategy.processPaymentCommission(paymentApplied, PaymentDataEvent.PaymentEventType.PAYMENT);

        verify(invoiceCommissionDAO, times(1)).save(any(PaymentCommission.class));
    }

    @Test
    public void testProcessPaymentCommission_RemoveProcessed() {
        when(invoiceCommissionDAO.findInvoiceCommissionsByInvoiceId(1)).thenReturn(invoiceCommissions);
        when(invoiceCommission.getPoints()).thenReturn(BigDecimal.ONE);
        when(invoiceCommission.getInvoice()).thenReturn(invoice);
        when(invoiceCommission.getRevenuePercent()).thenReturn(BigDecimal.TEN);
        when(invoice.getTotalAmount()).thenReturn(BigDecimal.TEN);
        when(invoice.getCommissionableAmount()).thenReturn(BigDecimal.TEN);
        paymentStrategy.processPaymentCommission(paymentApplied, PaymentDataEvent.PaymentEventType.REMOVE);

        verify(invoiceCommissionDAO, times(1)).save(any(PaymentCommission.class));
    }

    @Test
    public void testProcessPaymentOperatorCommission_PaymentProcessed() {
        when(invoiceCommissionDAO.findInvoiceOperatorCommissionsByInvoiceId(1)).thenReturn(invoiceOperatorCommissions);
        when(invoiceOperatorCommission.getInvoice()).thenReturn(invoice);
        when(invoiceOperatorCommission.getRevenuePercent()).thenReturn(BigDecimal.TEN);
        when(invoice.getTotalAmount()).thenReturn(BigDecimal.TEN);
        when(invoice.getCommissionableAmount()).thenReturn(BigDecimal.TEN);
        paymentStrategy.processPaymentCommission(paymentApplied, PaymentDataEvent.PaymentEventType.SAVE);

        verify(invoiceCommissionDAO, times(1)).save(any(PaymentOperatorCommission.class));
    }

    @Test
    public void testProcessPaymentOperatorCommission_RemoveProcessed() {
        when(invoiceCommissionDAO.findInvoiceOperatorCommissionsByInvoiceId(1)).thenReturn(invoiceOperatorCommissions);
        when(invoiceOperatorCommission.getInvoice()).thenReturn(invoice);
        when(invoiceOperatorCommission.getRevenuePercent()).thenReturn(BigDecimal.TEN);
        when(invoice.getTotalAmount()).thenReturn(BigDecimal.TEN);
        when(invoice.getCommissionableAmount()).thenReturn(BigDecimal.TEN);
        paymentStrategy.processPaymentCommission(paymentApplied, PaymentDataEvent.PaymentEventType.REFUND);

        verify(invoiceCommissionDAO, times(1)).save(any(PaymentOperatorCommission.class));
    }

    @Test
    public void testCalculateActivationPoints_zeroPoints() throws Exception {
        BigDecimal value = Whitebox.invokeMethod(paymentStrategy, "calculatePoints", invoice, BigDecimal.ZERO, paymentApplied);
        assertEquals(BigDecimal.ZERO, value);
    }

    @Test
    public void testCalculateActivationPoints_tenPoints() throws Exception {
        Whitebox.setInternalState(paymentStrategy,"eventType", PaymentDataEvent.PaymentEventType.SAVE);
        when(invoice.getTotalAmount()).thenReturn(BigDecimal.TEN);
        when(paymentApplied.getAmount()).thenReturn(BigDecimal.TEN);
        BigDecimal value = Whitebox.invokeMethod(paymentStrategy, "calculatePoints", invoice, BigDecimal.TEN, paymentApplied);
        BigDecimal expected = BigDecimal.valueOf(BigDecimal.TEN.doubleValue() / BigDecimal.TEN.doubleValue()).multiply(BigDecimal.TEN);
        assertEquals(expected, value);
    }

    @Test
    public void testCalculateActivationPoints_negTenPoints() throws Exception {
        Whitebox.setInternalState(paymentStrategy,"eventType", PaymentDataEvent.PaymentEventType.REMOVE);
        when(invoice.getTotalAmount()).thenReturn(BigDecimal.TEN);
        when(paymentApplied.getAmount()).thenReturn(BigDecimal.TEN);
        BigDecimal value = Whitebox.invokeMethod(paymentStrategy, "calculatePoints", invoice, BigDecimal.TEN, paymentApplied);
        BigDecimal expected = BigDecimal.valueOf(BigDecimal.TEN.doubleValue() / BigDecimal.TEN.doubleValue()).multiply(BigDecimal.TEN).negate();
        assertEquals(expected, value);
    }

    @Test
    public void testCalculateRevenueAmount_zero() throws Exception {
        BigDecimal value = Whitebox.invokeMethod(paymentStrategy, "calculateRevenueAmount", invoice, BigDecimal.ZERO, paymentApplied);
        assertEquals(BigDecimal.ZERO, value);
    }

    @Test
    public void testCalculateRevenueAmount_100() throws Exception {
        Whitebox.setInternalState(paymentStrategy,"eventType", PaymentDataEvent.PaymentEventType.SAVE);
        when(invoiceCommission.getPoints()).thenReturn(BigDecimal.TEN);
        when(invoiceCommission.getInvoice()).thenReturn(invoice);
        when(invoice.getTotalAmount()).thenReturn(BigDecimal.TEN);
        when(invoice.getCommissionableAmount()).thenReturn(BigDecimal.TEN);
        when(paymentApplied.getAmount()).thenReturn(BigDecimal.TEN);
        BigDecimal value = Whitebox.invokeMethod(paymentStrategy, "calculateRevenueAmount", invoice, BigDecimal.TEN, paymentApplied);
        BigDecimal expected = BigDecimal.valueOf(BigDecimal.TEN.doubleValue() / BigDecimal.TEN.doubleValue()).multiply(BigDecimal.TEN).multiply(BigDecimal.TEN);
        assertEquals(expected, value);
    }

    @Test
    public void testCalculateRevenueAmount_neg100() throws Exception {
        Whitebox.setInternalState(paymentStrategy,"eventType", PaymentDataEvent.PaymentEventType.REMOVE);
        when(invoiceCommission.getPoints()).thenReturn(BigDecimal.TEN);
        when(invoiceCommission.getInvoice()).thenReturn(invoice);
        when(invoice.getTotalAmount()).thenReturn(BigDecimal.TEN);
        when(invoice.getCommissionableAmount()).thenReturn(BigDecimal.TEN);
        when(paymentApplied.getAmount()).thenReturn(BigDecimal.TEN);
        BigDecimal value = Whitebox.invokeMethod(paymentStrategy, "calculateRevenueAmount", invoice, BigDecimal.TEN, paymentApplied);
        BigDecimal expected = BigDecimal.valueOf(BigDecimal.TEN.doubleValue() / BigDecimal.TEN.doubleValue()).multiply(BigDecimal.TEN).multiply(BigDecimal.TEN).negate();
        assertEquals(expected, value);
    }
}
