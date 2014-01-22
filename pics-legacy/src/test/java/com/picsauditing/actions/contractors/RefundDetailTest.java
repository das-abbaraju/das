package com.picsauditing.actions.contractors;

import com.opensymphony.xwork2.ActionSupport;
import com.picsauditing.PICS.BillingService;
import com.picsauditing.PicsActionTest;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.billing.BrainTree;
import com.picsauditing.braintree.CreditCard;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.dao.PaymentDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.model.billing.BillingNoteModel;
import com.picsauditing.util.SapAppPropertyUtil;
import com.picsauditing.util.Strings;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

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
