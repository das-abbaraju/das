package com.picsauditing.actions.contractors;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.BillingService;
import com.picsauditing.PICS.FeeService;
import com.picsauditing.PICS.PaymentProcessor;
import com.picsauditing.PICS.data.DataEvent;
import com.picsauditing.PICS.data.DataObservable;
import com.picsauditing.PICS.data.InvoiceDataEvent;
import com.picsauditing.PICS.data.InvoiceDataEvent.InvoiceEventType;
import com.picsauditing.PICS.data.PaymentDataEvent;
import com.picsauditing.PICS.data.PaymentDataEvent.PaymentEventType;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.billing.BrainTree;
import com.picsauditing.braintree.CreditCard;
import com.picsauditing.braintree.exception.NoBrainTreeServiceResponseException;
import com.picsauditing.dao.*;
import com.picsauditing.service.i18n.TranslationService;
import com.picsauditing.service.i18n.TranslationServiceFactory;
import com.picsauditing.util.SapAppPropertyUtil;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.mail.EventSubscriptionBuilder;
import com.picsauditing.model.account.AccountStatusChanges;
import com.picsauditing.model.billing.AccountingSystemSynchronization;
import com.picsauditing.model.billing.BillingNoteModel;
import com.picsauditing.model.billing.CommissionDetail;
import com.picsauditing.model.billing.InvoiceModel;
import com.picsauditing.util.EmailAddressUtils;
import com.picsauditing.util.Strings;
import com.picsauditing.util.log.PicsLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Transient;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@SuppressWarnings("serial")
public class RefundDetail extends ContractorActionSupport implements Preparable {

	@Autowired
	private InvoiceDAO invoiceDAO;
	@Autowired
	private BillingService billingService;

	private SapAppPropertyUtil sapAppPropertyUtil;

	private InvoiceCreditMemo creditMemo;
    private PaymentMethod paymentMethod;
    private String transactionNumber;
    private String transactionID;
    private String bankName;

	@Override
	public void prepare() {
		if (sapAppPropertyUtil == null) {
			sapAppPropertyUtil = SapAppPropertyUtil.factory();
		}
		int transactionId = getParameter("creditmemo.id");
		if (transactionId > 0) {
			Transaction transaction = invoiceDAO.find(Transaction.class,transactionId);
			if (transaction != null) {
                account = transaction.getAccount();
				id = account.getId();
				contractor = (ContractorAccount) account;
                creditMemo = (InvoiceCreditMemo) transaction;
			}
		}
	}

	@SuppressWarnings("deprecation")
	public String execute() throws Exception {
        return SUCCESS;
	}

    public String save() throws IOException {

        if (paymentMethod == null) {
            addActionError("Please select a refund method.");
        }

        String urlForRedirect = "InvoiceDetail.action?invoice.id=" + creditMemo.getId();

        BigDecimal balance = BigDecimal.ZERO.add(contractor.getBalance());

        if (balance.doubleValue() < 0) {
            BigDecimal amountApplied = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_UP);
            BigDecimal creditLeft = creditMemo.getCreditLeft();

            if (creditLeft.doubleValue() < 0) {
                addActionError("This credit memo's linked refunds total a higher value than the credit memo's value itself");
            } else if (creditLeft.doubleValue() == 0) {
                addActionError("This credit memo has been fully refunded");
            }
            else {
                if (balance.abs().doubleValue() <= creditLeft.doubleValue()) {
                    amountApplied = amountApplied.add(balance);
                }
                else {
                    amountApplied = amountApplied.add(creditLeft);
                }

                RefundAppliedToCreditMemo refundApplied = createRefundForCreditMemo(amountApplied);

                invoiceDAO.save(refundApplied);
                billingService.syncBalance(contractor);
                contractorAccountDao.save(contractor);
                addActionMessage("Refund created for " + amountApplied.abs().doubleValue() + " " + creditMemo.getCurrency());
            }
        }
        else {
            addActionMessage("No Refund Needed");
        }

        return this.setUrlForRedirect(urlForRedirect);
    }

    private RefundAppliedToCreditMemo createRefundForCreditMemo(BigDecimal amount) {
        RefundAppliedToCreditMemo refundApplied = RefundAppliedToCreditMemo.from(creditMemo);

        refundApplied.setAmount(amount.abs());
        refundApplied.setAuditColumns(permissions);

        Refund refund = new Refund();
        refund.setTotalAmount(amount.abs());
        refund.setAmountApplied(amount.abs());
        refund.setAuditColumns(permissions);
        refund.setPaymentMethod(paymentMethod);
		switch (paymentMethod) {
			case CreditCard:
				refund.setCcNumber(transactionNumber);
				refund.setTransactionID(transactionID);
				break;
			case Check:
				refund.setCheckNumber(transactionNumber);
                refund.setNotes(bankName);
				break;
			default:
				break;
		}

        refund.setStatus(TransactionStatus.Paid);
        refund.setAccount(contractor);

        refundApplied.setRefund(refund);

        if (sapAppPropertyUtil.isSAPBusinessUnitSetSyncTrueEnabledForObject(creditMemo)) {
            refundApplied.getRefund().setSapSync(true);
        }

        return refundApplied;
    }

    public List<PaymentMethod> getRefundMethods() {
        List<PaymentMethod> refundMethods = new ArrayList<PaymentMethod>();
        refundMethods.add(PaymentMethod.Check);
        refundMethods.add(PaymentMethod.CreditCard);
        refundMethods.add(PaymentMethod.EFT);
        return refundMethods;
    }

    public Map<String, String> getBankNames() {
        Map<String, String> bankNames = new HashMap<String, String>();
        bankNames.put("BOW","Bank of the West");
        bankNames.put("CB","Citibank");
        bankNames.put("HSBC","HSBC Bank");
        return Collections.unmodifiableMap(bankNames);
    }

    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public InvoiceCreditMemo getCreditMemo() {
		return creditMemo;
	}

	public void setCreditMemo(InvoiceCreditMemo creditMemo) {
		this.creditMemo = creditMemo;
	}

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionNumber() {
        return transactionNumber;
    }

    public void setTransactionNumber(String transactionNumber) {
        this.transactionNumber = transactionNumber;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}
