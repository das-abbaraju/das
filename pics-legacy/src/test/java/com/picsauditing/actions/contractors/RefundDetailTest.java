package com.picsauditing.actions.contractors;

import com.picsauditing.PICS.BillingService;
import com.picsauditing.PicsActionTest;
import com.picsauditing.billing.BrainTree;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.InvoiceCreditMemo;
import com.picsauditing.jpa.entities.PaymentMethod;
import com.picsauditing.jpa.entities.RefundAppliedToCreditMemo;
import com.picsauditing.util.SapAppPropertyUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class RefundDetailTest extends PicsActionTest {
	RefundDetail refundDetail;

    @Mock
    private ContractorAccountDAO contractorAccountDao;
    @Mock
    private InvoiceDAO invoiceDAO;
    @Mock
    private BillingService billingService;
    @Mock
    private BrainTree paymentService;
    @Mock
    private SapAppPropertyUtil sapAppPropertyUtil;
    @Mock
    private InvoiceCreditMemo invoiceCreditMemo;
    @Mock
    private ContractorAccount contractorAccount;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
        refundDetail = new RefundDetail();
		super.setUp(refundDetail);

		Whitebox.setInternalState(refundDetail, "invoiceDAO", invoiceDAO);
		Whitebox.setInternalState(refundDetail, "contractorAccountDao", contractorAccountDao);
		Whitebox.setInternalState(refundDetail, "billingService", billingService);
		Whitebox.setInternalState(refundDetail, "paymentService", paymentService);
        Whitebox.setInternalState(refundDetail, "sapAppPropertyUtil", sapAppPropertyUtil);
        refundDetail.setContractor(contractorAccount);
        refundDetail.setCreditMemo(invoiceCreditMemo);
        refundDetail.setPaymentMethod(PaymentMethod.CreditCard);
        refundDetail.setTransactionNumber("12345");
        refundDetail.setTransactionID("12345");
	}

    @Test
	public void testCreateRefundForCreditMemo() throws Exception {
        RefundAppliedToCreditMemo refundAppliedToCreditMemo = Whitebox.invokeMethod(refundDetail, "createRefundForCreditMemo", BigDecimal.TEN.negate());
        assertEquals(BigDecimal.TEN,refundAppliedToCreditMemo.getAmount());
        assertEquals(BigDecimal.TEN,refundAppliedToCreditMemo.getRefund().getAmountApplied());
	}
}
