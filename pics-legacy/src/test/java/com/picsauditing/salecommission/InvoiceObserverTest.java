package com.picsauditing.salecommission;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PICS.data.DataObservable;
import com.picsauditing.PICS.data.InvoiceDataEvent;
import com.picsauditing.PICS.data.InvoiceDataEvent.InvoiceEventType;
import com.picsauditing.PICS.data.PaymentDataEvent;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.salecommission.invoice.strategy.InvoiceStrategy;
import com.picsauditing.salecommission.invoice.strategy.UpdateInvoiceStrategy;
import com.picsauditing.salecommission.invoice.strategy.VoidInvoiceCommissionStrategy;
import com.picsauditing.toggle.FeatureToggle;

public class InvoiceObserverTest {

	private InvoiceObserver invoiceObserver;

	@Mock
	private FeatureToggle featureToggle;
	@Mock
	private InvoiceDataEvent invoiceDataEvent;
	@Mock
	private PaymentDataEvent paymentDataEvent;
	@Mock
	private InvoiceStrategy invoiceStrategy;
	@Mock
	private VoidInvoiceCommissionStrategy voidInvoiceCommissionStrategy;
	@Mock
	private UpdateInvoiceStrategy updateInvoiceStrategy;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		invoiceObserver = new InvoiceObserver();

		Whitebox.setInternalState(invoiceObserver, "featureToggle", featureToggle);
		Whitebox.setInternalState(invoiceObserver, "invoiceStrategy", invoiceStrategy);
		Whitebox.setInternalState(invoiceObserver, "voidInvoiceCommissionStrategy", voidInvoiceCommissionStrategy);
		Whitebox.setInternalState(invoiceObserver, "updateInvoiceStrategy", updateInvoiceStrategy);
	}

	@Test
	public void testUpdate_FeatureNotEnabled() {
		invoiceObserver.update(new DataObservable(), invoiceDataEvent);

		verify(invoiceDataEvent, never()).getData();
	}

	@Test
	public void testUpdate_NotInvoiceDataEvent() {
		invoiceObserver.update(new DataObservable(), paymentDataEvent);

		verify(paymentDataEvent, never()).getData();
	}

	@Test
	public void testUpdate_CatchAnyExceptionThatHappensDuringProcessing() {
		setupFeatureToggleToEnabled();
		when(invoiceDataEvent.getData()).thenThrow(new NullPointerException());

		try {
			invoiceObserver.update(new DataObservable(), invoiceDataEvent);
		} catch (Exception e) {
			fail("We should always catch the exception.");
		}
	}

	@Test
	public void testUpdate_ExecuteStrategyForNew() {
		setupDataEvent(InvoiceEventType.NEW);

		invoiceObserver.update(new DataObservable(), invoiceDataEvent);

		verify(invoiceStrategy, times(1)).processInvoiceCommission(any(Invoice.class));
	}

	@Test
	public void testUpdate_ExecuteStrategyForVoid() {
		setupDataEvent(InvoiceEventType.VOID);

		invoiceObserver.update(new DataObservable(), invoiceDataEvent);

		verify(voidInvoiceCommissionStrategy, times(1)).processInvoiceCommission(any(Invoice.class));
	}

	@Test
	public void testUpdate_ExecuteStrategyForUpdate() {
		setupDataEvent(InvoiceEventType.UPDATE);

		invoiceObserver.update(new DataObservable(), invoiceDataEvent);

		verify(updateInvoiceStrategy, times(1)).processInvoiceCommission(any(Invoice.class));
	}

	@Test
	public void testUpdate_NoStrategyExecutedBecauseTypeIsNull() {
		setupDataEvent(null);

		invoiceObserver.update(new DataObservable(), invoiceDataEvent);

		verify(invoiceStrategy, never()).processInvoiceCommission(any(Invoice.class));
		verify(voidInvoiceCommissionStrategy, never()).processInvoiceCommission(any(Invoice.class));
		verify(updateInvoiceStrategy, never()).processInvoiceCommission(any(Invoice.class));
	}

	private void setupDataEvent(InvoiceEventType invoiceEventType) {
		setupFeatureToggleToEnabled();
		when(invoiceDataEvent.getEventType()).thenReturn(invoiceEventType);
		when(invoiceDataEvent.getData()).thenReturn(new Invoice());
	}

	private void setupFeatureToggleToEnabled() {
		when(featureToggle.isFeatureEnabled(FeatureToggle.TOGGLE_INVOICE_COMMISSION)).thenReturn(true);
	}

}
