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
import java.math.BigDecimal;
import java.util.*;

@SuppressWarnings("serial")
public class InvoiceDetail extends ContractorActionSupport implements Preparable {

    private static final String PAY_BUTTON = "pay";
	private static final String CANCEL_BUTTON = "cancel";
	private static final String SAVE_BUTTON = "save";
	private static final String CHANGE_TO_BUTTON = "changeto";
	private static final String EMAIL_BUTTON = "email";
    private static final String BAD_DEBT_BUTTON = "baddebt";
	private static final String RETURN_ITEMS_BUTTON = "returnitems";

	@Autowired
	private InvoiceDAO invoiceDAO;
	@Autowired
	private InvoiceItemDAO invoiceItemDAO;
	@Autowired
	private InvoiceFeeDAO invoiceFeeDAO;
	@Autowired
	private PaymentDAO paymentDAO;
	@Autowired
	private NoteDAO noteDAO;
	@Autowired
	private BillingService billingService;
	@Autowired
	private AppPropertyDAO appPropertyDAO;
	@Autowired
	private BrainTree paymentService;
	@Autowired
	private EmailSender emailSender;
	@Autowired
	private DataObservable salesCommissionDataObservable;
	@Autowired
	private InvoiceModel invoiceModel;
	@Autowired
	private BillingNoteModel billingNoteModel;
    @Autowired
    private FeeService feeService;

	private SapAppPropertyUtil sapAppPropertyUtil;

	private boolean edit = false;
	private String message = null;
	private int newFeeId;
	private Invoice invoice = null;
	private InvoiceCreditMemo creditMemo = null;
	private Transaction transaction;
	private List<InvoiceFee> feeList = null;
	private String country;

	private static final Logger logger = LoggerFactory.getLogger(ContractorActionSupport.class);

	@Override
	public void prepare() {
		if (sapAppPropertyUtil == null) {
			sapAppPropertyUtil = SapAppPropertyUtil.factory();
		}
		int transactionId = getParameter("invoice.id");
		if (transactionId > 0) {
			transaction = invoiceDAO.find(Transaction.class,transactionId);
			if (transaction != null) {
                account = transaction.getAccount();
				id = account.getId();
				contractor = (ContractorAccount) account;
                country = account.getCountry().toString();

				if (isTransactionIsInvoice()) {
					invoice = (Invoice) transaction;
				} else if (isTransactionIsCreditMemo()) {
					creditMemo = (InvoiceCreditMemo) transaction;
				} else addActionError("ID "+transactionId+" does not return anything");
			}
		}
	}

	@SuppressWarnings("deprecation")
	public String execute() throws Exception {

        if (!isTransactionIsInvoice() && !isTransactionIsCreditMemo()) {
            addActionError(getText("InvoiceDetail.error.CantFindInvoice"));
            return BLANK;
        }

        if (userViewIsDenied()) throw new NoRightsException(getText("InvoiceDetail.error.CantViewInvoice"));

		if (isTransactionIsInvoice()) {
			invoice.updateAmountApplied();
		}

		if (button != null) {
            return processedCommand();
		} else {
			if (isTransactionIsInvoice()) {
				updateTotals();
				billingService.saveInvoice(invoice);
				feeService.calculateContractorInvoiceFees(contractor);
				billingService.syncBalance(contractor);
				contractor.setAuditColumns(permissions);
				contractorAccountDao.save(contractor);
			}
            return SUCCESS;
        }
	}

    private String processedCommand() throws Exception {
		String urlForRedirect = "InvoiceDetail.action?invoice.id=" + transaction.getId() + "&edit=" + edit;
		if (isTransactionIsCreditMemo() && !button.equals(EMAIL_BUTTON)) {
			return this.setUrlForRedirect(urlForRedirect);
		}

		switch (button) {
			case SAVE_BUTTON:  save();
				break;
			case CHANGE_TO_BUTTON:  change();
				break;
			case EMAIL_BUTTON:  email();
				break;
			case CANCEL_BUTTON:  cancel();
				break;
			case BAD_DEBT_BUTTON: badDebt();
				break;
			case PAY_BUTTON:
				Payment payment = PaymentProcessor.PayOffInvoice(invoice, getUser(), PaymentMethod.CreditCard);
				try {
					process(payment);
				} catch (NoBrainTreeServiceResponseException re) {
					handleBrainTreeError(payment, re);
					return SUCCESS;
				} catch (Exception e) {
					handleGenericCreditCardError(e);
					return SUCCESS;
				}
		}

		if (isTransactionIsInvoice()) {
			billingService.saveInvoice(invoice);
		}
        if (!Strings.isEmpty(message)) {
            addActionMessage(message);
        }

        if (isTransactionIsInvoice() && SAVE_BUTTON.equals(button) && !invoice.getStatus().isPaid()) {
            notifyDataChange(new InvoiceDataEvent(invoice, InvoiceEventType.UPDATE));
        }

        return this.setUrlForRedirect(urlForRedirect);
    }

    private void badDebt() {
		if (isTransactionIsCreditMemo() || !isSapEnabledForBizUnit()) return;
        if (isTransactionIsInvoice() && !invoice.getPayments().isEmpty()) return;
        AccountStatus status = contractor.getStatus();
        if (status.isActivePendingRequested() || status.isDeclined()) {
            addActionError(getText("InvoiceDetail.BadDebtUnavailable"));
            return;
        }

        Payment payment = PaymentProcessor.PayOffInvoice(invoice, getUser(), PaymentMethod.BadDebtCreditMemo);
        PaymentProcessor.ApplyPaymentToInvoice(payment, invoice, billingNoteModel.findUserForPaymentNote(permissions), payment.getTotalAmount());
        payment.setStatus(TransactionStatus.BadDebt);
        payment.setAuditColumns(getUser());
        AccountingSystemSynchronization.setToSynchronize(payment);
        paymentDAO.save(payment);
        invoice.setStatus(TransactionStatus.Paid);
        invoiceDAO.save(invoice);
//        contractor.syncBalance();
        contractor.setBalance(BigDecimal.ZERO);
        contractorAccountDao.save(contractor);

        notifyDataChange(new PaymentDataEvent(payment, PaymentEventType.REMOVE));

        addNote("Invoice marked as 'Bad Debt'.", billingNoteModel.findUserForPaymentNote(permissions));
    }

    private void handleGenericCreditCardError(Exception e) {
        addNote("Credit Card invoice failed: " + e.getMessage(), billingNoteModel.findUserForPaymentNote(permissions));
        this.addActionError(getText("InvoiceDetail.error.FailedCreditCard") + e.getMessage());
    }

    private void handleBrainTreeError(Payment payment, NoBrainTreeServiceResponseException re) {
		if (isTransactionIsCreditMemo()) return;
        addNote("Credit Card service connection error: " + re.getMessage(), billingNoteModel.findUserForPaymentNote(permissions));

        EmailBuilder emailBuilder = new EmailBuilder();
        emailBuilder.setTemplate(EmailTemplate.BRAIN_TREE_ERROR_EMAIL_TEMPLATE);
        emailBuilder.setFromAddress(EmailAddressUtils.PICS_IT_TEAM_EMAIL);
        emailBuilder.setToAddresses(EmailAddressUtils.getBillingEmail(contractor.getCurrency()));
        emailBuilder.setPermissions(permissions);
        emailBuilder.addToken("permissions", permissions);
        emailBuilder.addToken("contractor", contractor);
        emailBuilder.addToken("billingusers", contractor.getUsersByRole(OpPerms.ContractorBilling));
        emailBuilder.addToken("invoice", invoice);

        EmailQueue emailQueue;
        try {
            emailQueue = emailBuilder.build();
            emailQueue.setVeryHighPriority();
			emailQueue.setSubjectViewableById(Account.PicsID);
			emailQueue.setBodyViewableById(Account.PicsID);
            emailSender.send(emailQueue);
        } catch (Exception e) {
            PicsLogger
                    .log("Cannot send email error message or determine credit processing status for contractor "
							+ contractor.getName()
							+ " ("
							+ contractor.getId()
							+ ") for invoice "
							+ invoice.getId());
        }
        addActionError(getTextParameterized("InvoiceDetail.error.ContactBilling",
                getText("PicsBillingPhone")));

        // Assuming Unpaid status per Aaron so that he can
        // refund or void manually.
        payment.setStatus(TransactionStatus.Unpaid);
        paymentDAO.save(payment);

        notifyDataChange(new PaymentDataEvent(payment, PaymentEventType.REMOVE));
    }

    private void process(Payment payment) throws Exception {
		if (isTransactionIsCreditMemo()) return;
        if (!(invoice.getTotalAmount().compareTo(BigDecimal.ZERO) > 0) || !contractor.isCcValid()) return;

        paymentService.processPayment(payment, invoice);

        CreditCard creditCard = paymentService.getCreditCard(contractor);
        payment.setCcNumber(creditCard.getCardNumber());

        // Only if the invoice succeeds
        PaymentProcessor.ApplyPaymentToInvoice(payment, invoice, billingNoteModel.findUserForPaymentNote(permissions),
                payment.getTotalAmount());
        AccountingSystemSynchronization.setToSynchronize(payment);

        paymentDAO.save(payment);
        billingService.performInvoiceStatusChangeActions(invoice, TransactionStatus.Paid);
        invoice.updateAmountApplied();

        // Activate the contractor
        billingService.activateContractor(contractor, invoice);
        contractorAccountDao.save(contractor);

        notifyDataChange(new PaymentDataEvent(payment, PaymentEventType.SAVE));

        addNote("Credit Card invoice completed and emailed the receipt for "
				+ invoice.getTotalAmount() + Strings.SINGLE_SPACE
				+ invoice.getCurrency().getDisplay(), billingNoteModel.findUserForPaymentNote(permissions));

        // Send a receipt to the contractor
        try {
            EventSubscriptionBuilder.contractorInvoiceEvent(contractor, invoice);
        } catch (Exception theyJustDontGetAnEmail) {
        }
    }

    private void cancel() {
		if (!isVoidEnabled()) return;
        Iterator<PaymentAppliedToInvoice> paIterator = invoice.getPayments().iterator();
        if (paIterator.hasNext()) {
            PaymentAppliedToInvoice paymentAppliedToInvoice = paIterator.next();
            paymentDAO.removePaymentInvoice(paymentAppliedToInvoice, getUser());
        }
        invoice.setStatus(TransactionStatus.Void);
		invoice.setSapSync(false);
        billingService.performInvoiceStatusChangeActions(invoice, TransactionStatus.Void);
        invoice.setAuditColumns(permissions);
        AccountingSystemSynchronization.setToSynchronize(invoice);
        invoice.setNotes("Cancelled Invoice");

        // Automatically deactivating account based on expired
        // membership
        BillingStatus status = contractor.getBillingStatus();
        if (!contractor.getStatus().equals(AccountStatus.Deactivated)
                && (status.isRenewalOverdue() || status.isReactivation())) {
            if (contractor.getAccountLevel().isBidOnly()) {
                contractor.setReason(AccountStatusChanges.BID_ONLY_ACCOUNT_REASON);
            }
        }

        feeService.calculateContractorInvoiceFees(contractor);
        billingService.syncBalance(contractor);
        contractor.incrementRecalculation(10);
        contractor.setAuditColumns(permissions);
        contractorAccountDao.save(contractor);

        message = getText("InvoiceDetail.message.CanceledInvoice");
        notifyDataChange(new InvoiceDataEvent(invoice, InvoiceEventType.VOID));

        String noteText = "Cancelled Invoice " + invoice.getId() + " for "
                + contractor.getCountry().getCurrency().getSymbol() + invoice.getTotalAmount().toString();
        addNote(noteText, billingNoteModel.findUserForPaymentNote(permissions));
    }

    private void email() {
        try {
			if (isTransactionIsCreditMemo())  {        // TODO: John B?  Help?
				addActionMessage("Email not yet supported for credit memos");
				return;
			}
            EmailQueue email = EventSubscriptionBuilder.contractorInvoiceEvent(contractor, invoice);
            String note = Strings.EMPTY_STRING;
            if (transaction.getStatus().isPaid()) {
                note += "Payment Receipt for Invoice";
            } else {
                note += "Invoice";
            }

            note += " emailed to " + email.getToAddresses();
            if (!Strings.isEmpty(email.getCcAddresses())) {
                note += " and cc'd " + email.getCcAddresses();
            }
            addNote(note, billingNoteModel.findUserForPaymentNote(permissions));
            message = getText("InvoiceDetail.message.SentEmail");

        } catch (Exception e) {
            logger.error("Unable to send InvoiceDetail email due to exception: {}", e.getMessage());
            addActionError(getText("InvoiceDetail.message.EmailFail"));
        }
    }

    private void change() throws Exception {
		if (!isEditEnabled()) return;
        List<String> removedItemNames = new ArrayList<String>();
        List<String> createdItemNames = new ArrayList<String>();

        Date membershipExpiration = null;

        // Removing all Membership items
        Iterator<InvoiceItem> iterator = invoice.getItems().iterator();
        while (iterator.hasNext()) {
            InvoiceItem item = iterator.next();
            if (item.getInvoiceFee().isMembership() || item.getInvoiceFee().isImportFee()) {
                if (item.getPaymentExpires() != null) {
                    membershipExpiration = item.getPaymentExpires();
                }
                removedItemNames.add(item.getInvoiceFee().getFee().toString());
                iterator.remove();
                invoiceItemDAO.remove(item);
            }
        }

        // Re-adding Membership Items
        for (FeeClass feeClass : contractor.getFees().keySet()) {
            if ((feeClass.isMembership() || feeClass.equals(FeeClass.ImportFee))
                    && !contractor.getFees().get(feeClass).getNewLevel().isFree()) {
                InvoiceItem newInvoiceItem = new InvoiceItem();
                newInvoiceItem.setInvoiceFee(contractor.getFees().get(feeClass).getNewLevel());
                newInvoiceItem.setAmount(contractor.getFees().get(feeClass).getNewAmount());
                newInvoiceItem.setAuditColumns(new User(User.SYSTEM));
                newInvoiceItem.setPaymentExpires(membershipExpiration);
                newInvoiceItem.setAuditColumns(permissions);

                newInvoiceItem.setInvoice(invoice);
                invoice.getItems().add(newInvoiceItem);

                createdItemNames.add(newInvoiceItem.getInvoiceFee().getFee().toString());
            }
        }

        AccountingSystemSynchronization.setToSynchronize(invoice);
        invoice.updateTotalAmount();
        invoice.updateAmountApplied();
        billingService.saveInvoice(invoice);

        addNote("Changed Membership Level", "Changed invoice from " + Strings.implode(removedItemNames, ", ")
				+ " to " + Strings.implode(createdItemNames, ", "), billingNoteModel.findUserForPaymentNote(permissions));
        message = getText("InvoiceDetail.message.ChangedLevel");

        invoice.setNotes(invoiceModel.getSortedClientSiteList(contractor));
        billingService.syncBalance(contractor);

        notifyDataChange(new InvoiceDataEvent(invoice, InvoiceEventType.UPDATE));
    }

    private void save() {
		if (isTransactionIsCreditMemo()) return;
        edit = false;
        if (newFeeId > 0) {
            addInvoiceItem(newFeeId);
            newFeeId = 0;
            edit = true;
        } else {
            message = getText("InvoiceDetail.message.SavedInvoice");
        }
        updateTotals();
        AccountingSystemSynchronization.setToSynchronize(invoice);
    }

    private boolean userViewIsDenied() throws NoRightsException {
        return (!permissions.hasPermission(OpPerms.AllContractors) && permissions.getAccountId() != transaction.getAccount().getId());
    }


    private void updateTotals() {
		if (isTransactionIsCreditMemo()) return;
		if (!invoice.getStatus().isPaid()) {
			invoice.setTotalAmount(BigDecimal.ZERO);
            invoice.setCommissionableAmount(BigDecimal.ZERO);

			for (InvoiceItem item : invoice.getItems()) {
				invoice.setTotalAmount(invoice.getTotalAmount().add(item.getAmount()));

                if (item.getInvoiceFee().isCommissionEligible()) {
                    invoice.setCommissionableAmount(invoice.getCommissionableAmount().add(item.getAmount()));
                }
			}
		}
	}

	private void addNote(String subject, User u) {
		Note note = new Note(transaction.getAccount(), u, subject);
		note.setNoteCategory(NoteCategory.Billing);
		note.setCanContractorView(true);
		note.setViewableById(Account.PicsID);
		noteDAO.save(note);
	}

	private void addNote(String subject, String body, User u) {
		Note note = new Note(transaction.getAccount(), u, subject);
		note.setBody(body);
		note.setNoteCategory(NoteCategory.Billing);
		note.setCanContractorView(true);
		note.setViewableById(Account.PicsID);
		noteDAO.save(note);
	}

	private void addInvoiceItem(int feeId) {
		if (isTransactionIsCreditMemo()) return;
		InvoiceItem newItem = new InvoiceItem();
		InvoiceFee newFee = invoiceFeeDAO.find(feeId);
		newItem.setInvoiceFee(newFee);
		newItem.setAmount(FeeService.getRegionalAmountOverride((ContractorAccount) invoice.getAccount(), newFee));
		newItem.setInvoice(invoice);
		newItem.setAuditColumns(permissions);

		invoice.getItems().add(newItem);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public boolean isEdit() {
		return edit;
	}

	public void setEdit(boolean edit) {
		this.edit = edit;
	}

	public List<InvoiceFee> getFeeList() {
		if (feeList == null) {
			feeList = invoiceModel.getFeeList();
		}

		return feeList;
	}

	public int getNewFeeId() {
		return newFeeId;
	}

	public void setNewFeeId(int newFeeId) {
		this.newFeeId = newFeeId;
	}

	public boolean isShowHeader() {
		return true;
	}

	public String getCcNumber() {
		String ccNumber = "";
		try {
			CreditCard creditCard = paymentService.getCreditCard(contractor);
			ccNumber = creditCard.getCardNumber();
		} catch (Exception e) {
		}
		return ccNumber;
	}

	public User getBillingUser() {
		return contractor.getUsersByRole(OpPerms.ContractorBilling).get(0);
	}

	public boolean isHasInvoiceMembershipChanged() {
		if (isTransactionIsCreditMemo()) return false;
		for (InvoiceItem item : invoice.getItems()) {
			for (FeeClass feeClass : contractor.getFees().keySet()) {
				if (item.getInvoiceFee().isMembership()
						&& item.getInvoiceFee().getFeeClass()
						.equals(contractor.getFees().get(feeClass).getNewLevel().getFeeClass())
						&& !item.getInvoiceFee().equals(contractor.getFees().get(feeClass).getNewLevel())) {
					return true;
				}
			}
		}

		return false;
	}

	public String getCountry() {
		return country;
	}

	public List<CommissionDetail> getCommissionDetails() {
		return invoiceModel.getCommissionDetails(invoice);
	}

	private <T> void notifyDataChange(DataEvent<T> dataEvent) {
		salesCommissionDataObservable.setChanged();
		salesCommissionDataObservable.notifyObservers(dataEvent);
	}

//	private User findUserForPaymentNote() {
//		int userWhoSwitchedToCurrentUser = permissions.getAdminID();
//		User userForPaymentNote = getUser();
//		if (userForPaymentNote.getId() != userWhoSwitchedToCurrentUser && userWhoSwitchedToCurrentUser > 0) {
//			return getUser(userWhoSwitchedToCurrentUser);
//		}
//
//		return userForPaymentNote;
//	}


	public InvoiceCreditMemo getCreditMemo() {
		return creditMemo;
	}

	public void setCreditMemo(InvoiceCreditMemo creditMemo) {
		this.creditMemo = creditMemo;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	@Transient
	public boolean isTransactionIsCreditMemo() {
		boolean transactionIsCreditMemo = (transaction instanceof InvoiceCreditMemo);
		return transactionIsCreditMemo;
	}

	@Transient
	public boolean isTransactionIsInvoice() {
		boolean transactionIsInvoice = (transaction instanceof Invoice);
		return transactionIsInvoice;
	}

	@Transient
	public boolean isSapEnabledForBizUnit() {
		return sapAppPropertyUtil.isSAPBusinessUnitEnabled(transaction.getAccount().getCountry().getBusinessUnit().getId());
	}

	@Transient
	public boolean isVoidEnabled() {
		boolean voidEnabled = (isEditEnabled() || contractor.getStatus().isDeclined());
		return voidEnabled;
	}

	@Transient
	public boolean isEditEnabled() {
		boolean editEnabled = false;
		if (isSapEnabledForBizUnit() && isTransactionIsInvoice()) {
			if (contractor.getStatus().isPending() && invoice.getPayments().size() == 0) {
				editEnabled = true;
			}
		} else {
			editEnabled = true;
		}
		return editEnabled;
	}

	public SapAppPropertyUtil getSapAppPropertyUtil() {
		return sapAppPropertyUtil;
	}

	public void setSapAppPropertyUtil(SapAppPropertyUtil sapAppPropertyUtil) {
		this.sapAppPropertyUtil = sapAppPropertyUtil;
	}
}
