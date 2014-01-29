package com.picsauditing.actions.contractors;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.BillingService;
import com.picsauditing.billing.BrainTree;
import com.picsauditing.dao.*;
import com.picsauditing.util.SapAppPropertyUtil;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.model.billing.AccountingSystemSynchronization;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@SuppressWarnings("serial")
public class RefundDetail extends ContractorActionSupport implements Preparable {

	@Autowired
	private InvoiceDAO invoiceDAO;
	@Autowired
	private BillingService billingService;
    @Autowired
    private BrainTree paymentService;

	private SapAppPropertyUtil sapAppPropertyUtil;

    private String urlForRedirect;
	private InvoiceCreditMemo creditMemo;
    private PaymentMethod paymentMethod;
    private String transactionNumber;
    private String transactionID;
    private String bankName;

	@Override
	public void prepare() {
		if (sapAppPropertyUtil == null) {
			sapAppPropertyUtil = SapAppPropertyUtil.factory();
            AccountingSystemSynchronization.setSapAppPropertyUtil(sapAppPropertyUtil);
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
        RefundAppliedToCreditMemo refundApplied = generateRefund();

        if (refundApplied == null) {
            return ERROR;
        }

        return saveRefund(refundApplied);
    }

    private String saveRefund(RefundAppliedToCreditMemo refundApplied) throws IOException {
        invoiceDAO.save(refundApplied);
        billingService.syncBalance(contractor);
        contractorAccountDao.save(contractor);
        BigDecimal absolute = refundApplied.getRefund().getAmountApplied();
        addActionMessage("Refund created for " + absolute.toString() + " " + creditMemo.getCurrency());

        return this.setUrlForRedirect(urlForRedirect);
    }

    private RefundAppliedToCreditMemo generateRefund() {
        if (paymentMethod == null) {
            addActionError("Please select a refund method.");
            return null;
        }

        urlForRedirect = "InvoiceDetail.action?invoice.id=" + creditMemo.getId();

        BigDecimal amountApplied = calculateAmountApplied();
        if (amountApplied == null) {
            return null;
        }

        RefundAppliedToCreditMemo refundApplied = createRefundForCreditMemo(amountApplied);
        return refundApplied;
    }

    private BigDecimal calculateAmountApplied() {
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
                    return amountApplied.add(balance);
                }
                else {
                    return amountApplied.add(creditLeft);
                }
            }
        }
        else {
            addActionMessage("No Refund Needed");
        }

        return null;
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
