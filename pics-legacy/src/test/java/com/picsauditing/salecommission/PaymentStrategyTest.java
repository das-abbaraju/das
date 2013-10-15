package com.picsauditing.salecommission;

import com.picsauditing.PICS.data.DataObservable;
import com.picsauditing.PICS.data.InvoiceDataEvent;
import com.picsauditing.PICS.data.PaymentDataEvent;
import com.picsauditing.PICS.data.PaymentDataEvent.PaymentEventType;
import com.picsauditing.dao.InvoiceCommissionDAO;
import com.picsauditing.dao.PaymentCommissionDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.salecommission.payment.strategy.PaymentRemoveStrategy;
import com.picsauditing.salecommission.payment.strategy.PaymentStrategy;
import com.picsauditing.toggle.FeatureToggle;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class PaymentStrategyTest {

	private PaymentStrategy paymentStrategy;

    private PaymentRemoveStrategy paymentRemoveStrategy;

    @Mock
    private InvoiceCommissionDAO invoiceCommissionDAO;
    @Mock
    private PaymentCommissionDAO paymentCommissionDAO;
    @Mock
    private PaymentAppliedToInvoice paymentApplied;
    @Mock
    private Payment payment;
    @Mock
    private Invoice invoice;
    @Mock
    private InvoiceCommission invoiceCommission;

    private List<InvoiceCommission> invoiceCommissions;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		paymentStrategy = new PaymentStrategy();
        paymentRemoveStrategy = new PaymentRemoveStrategy();

		Whitebox.setInternalState(paymentStrategy, "invoiceCommissionDAO", invoiceCommissionDAO);
		Whitebox.setInternalState(paymentStrategy, "paymentCommissionDAO", paymentCommissionDAO);
        Whitebox.setInternalState(paymentRemoveStrategy, "invoiceCommissionDAO", invoiceCommissionDAO);
        Whitebox.setInternalState(paymentRemoveStrategy, "paymentCommissionDAO", paymentCommissionDAO);

        when(paymentApplied.getPayment()).thenReturn(payment);
        when(paymentApplied.getInvoice()).thenReturn(invoice);
        when(payment.getId()).thenReturn(2);
        when(invoice.getId()).thenReturn(1);

        invoiceCommissions = new ArrayList<InvoiceCommission>();
        invoiceCommissions.add(invoiceCommission);
    }

	@Test
	public void testProcessPaymentCommission_PaymentAlreadyProcessed() {
        ArrayList<PaymentCommission> paymentCommissions = new ArrayList<PaymentCommission>();
        paymentCommissions.add(new PaymentCommission());

        when(paymentCommissionDAO.findByPaymentIdInvoiceId(2, 1)).thenReturn(paymentCommissions);
		paymentStrategy.processPaymentCommission(paymentApplied);

        verify(invoiceCommissionDAO, never()).findByInvoiceId(1);
	}

    @Test
    public void testProcessPaymentCommission_RemoveAlreadyProcessed() {
        when(paymentCommissionDAO.findByPaymentIdInvoiceId(2,1)).thenReturn(new ArrayList<PaymentCommission>());
        paymentRemoveStrategy.processPaymentCommission(paymentApplied);

        verify(invoiceCommissionDAO, never()).findByInvoiceId(1);
    }

    @Test
    public void testProcessPaymentCommission_PaymentProcessed() {
        when(paymentCommissionDAO.findByPaymentIdInvoiceId(2,1)).thenReturn(new ArrayList<PaymentCommission>());
        when(invoiceCommissionDAO.findByInvoiceId(1)).thenReturn(invoiceCommissions);
        when(invoiceCommission.getPoints()).thenReturn(BigDecimal.ONE);
        when(invoiceCommission.getInvoice()).thenReturn(invoice);
        when(invoiceCommission.getRevenuePercent()).thenReturn(BigDecimal.TEN);
        when(invoice.getTotalAmount()).thenReturn(BigDecimal.TEN);
        when(invoice.getCommissionableAmount()).thenReturn(BigDecimal.TEN);
        paymentStrategy.processPaymentCommission(paymentApplied);

        verify(paymentCommissionDAO, times(1)).save(any(PaymentCommission.class));
    }

    @Test
    public void testProcessPaymentCommission_RemoveProcessed() {
        ArrayList<PaymentCommission> paymentCommissions = new ArrayList<PaymentCommission>();
        paymentCommissions.add(new PaymentCommission());

        when(paymentCommissionDAO.findByPaymentIdInvoiceId(2, 1)).thenReturn(paymentCommissions);
        when(invoiceCommissionDAO.findByInvoiceId(1)).thenReturn(invoiceCommissions);
        when(invoiceCommission.getPoints()).thenReturn(BigDecimal.ONE);
        when(invoiceCommission.getInvoice()).thenReturn(invoice);
        when(invoiceCommission.getRevenuePercent()).thenReturn(BigDecimal.TEN);
        when(invoice.getTotalAmount()).thenReturn(BigDecimal.TEN);
        when(invoice.getCommissionableAmount()).thenReturn(BigDecimal.TEN);
        paymentRemoveStrategy.processPaymentCommission(paymentApplied);

        verify(paymentCommissionDAO, times(1)).save(any(PaymentCommission.class));
    }
}
