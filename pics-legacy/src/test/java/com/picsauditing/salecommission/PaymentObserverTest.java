package com.picsauditing.salecommission;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.PaymentApplied;
import com.picsauditing.jpa.entities.PaymentAppliedToInvoice;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PICS.data.DataObservable;
import com.picsauditing.PICS.data.InvoiceDataEvent;
import com.picsauditing.PICS.data.PaymentDataEvent;
import com.picsauditing.PICS.data.PaymentDataEvent.PaymentEventType;
import com.picsauditing.jpa.entities.Payment;
import com.picsauditing.salecommission.payment.strategy.PaymentRemoveStrategy;
import com.picsauditing.salecommission.payment.strategy.PaymentStrategy;
import com.picsauditing.toggle.FeatureToggle;

public class PaymentObserverTest {

	private PaymentObserver paymentObserver;

	@Mock
	private FeatureToggle featureToggle;
	@Mock
	private InvoiceDataEvent invoiceDataEvent;
	@Mock
	private PaymentDataEvent paymentDataEvent;
	@Mock
	private PaymentStrategy paymentStrategy;
	@Mock
	private PaymentRemoveStrategy paymentRemoveStrategy;
    @Mock
    PaymentAppliedToInvoice paymentApplied;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		paymentObserver = new PaymentObserver();

		Whitebox.setInternalState(paymentObserver, "featureToggle", featureToggle);
		Whitebox.setInternalState(paymentObserver, "paymentStrategy", paymentStrategy);
		Whitebox.setInternalState(paymentObserver, "paymentRemoveStrategy", paymentRemoveStrategy);
	}

	@Test
	public void testUpdate_FeatureNotEnabled() {
		paymentObserver.update(new DataObservable(), paymentDataEvent);

		verify(paymentDataEvent, never()).getData();
	}

	@Test
	public void testUpdate_NotPaymentDataEvent() {
		paymentObserver.update(new DataObservable(), paymentDataEvent);

		verify(paymentDataEvent, never()).getData();
	}

	@Test
	public void testUpdate_CatchAnyExceptionThatHappensDuringProcessing() {
		setupFeatureToggleToEnabled();
		when(invoiceDataEvent.getData()).thenThrow(new NullPointerException());

		try {
			paymentObserver.update(new DataObservable(), invoiceDataEvent);
		} catch (Exception e) {
			fail("We should always catch the exception.");
		}
	}

	@Test
	public void testUpdate_ExecuteStrategyForPayment() {
		setupDataEvent(PaymentEventType.PAYMENT);

		paymentObserver.update(new DataObservable(), paymentDataEvent);

		verify(paymentStrategy, times(1)).processPaymentCommission(any(PaymentApplied.class));
	}

	@Test
	public void testUpdate_ExecuteStrategyForSave() {
		setupDataEvent(PaymentEventType.SAVE);

		paymentObserver.update(new DataObservable(), paymentDataEvent);

		verify(paymentStrategy, times(1)).processPaymentCommission(any(PaymentApplied.class));
	}

	@Test
	public void testUpdate_ExecuteStrategyForRemove() {
		setupDataEvent(PaymentEventType.REMOVE);

		paymentObserver.update(new DataObservable(), paymentDataEvent);

		verify(paymentRemoveStrategy, times(1)).processPaymentCommission(any(PaymentApplied.class));
	}

	@Test
	public void testUpdate_ExecuteStrategyForRefund() {
		setupDataEvent(PaymentEventType.REFUND);

		paymentObserver.update(new DataObservable(), paymentDataEvent);

		verify(paymentRemoveStrategy, times(1)).processPaymentCommission(any(PaymentApplied.class));
	}

	@Test
	public void testUpdate_NoStrategyExecutedBecauseTypeIsNull() {
		setupDataEvent(null);

		paymentObserver.update(new DataObservable(), paymentDataEvent);

		verify(paymentStrategy, never()).processPaymentCommission(any(PaymentApplied.class));
		verify(paymentRemoveStrategy, never()).processPaymentCommission(any(PaymentApplied.class));
	}

	private void setupDataEvent(PaymentEventType paymentEventType) {
		setupFeatureToggleToEnabled();
		when(paymentDataEvent.getPaymentEventType()).thenReturn(paymentEventType);
        when(paymentDataEvent.getData()).thenReturn(paymentApplied);
        when(paymentApplied.getPayment()).thenReturn(new Payment());
        when(paymentApplied.getInvoice()).thenReturn(new Invoice());
	}

	private void setupFeatureToggleToEnabled() {
		when(featureToggle.isFeatureEnabled(FeatureToggle.TOGGLE_INVOICE_COMMISSION)).thenReturn(true);
	}

}
