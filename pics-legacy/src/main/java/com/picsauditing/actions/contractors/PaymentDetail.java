package com.picsauditing.actions.contractors;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.BillingService;
import com.picsauditing.billing.BrainTree;
import com.picsauditing.braintree.exception.NoBrainTreeServiceResponseException;
import com.picsauditing.PICS.PaymentProcessor;
import com.picsauditing.PICS.data.DataEvent;
import com.picsauditing.PICS.data.DataObservable;
import com.picsauditing.PICS.data.PaymentDataEvent;
import com.picsauditing.PICS.data.PaymentDataEvent.PaymentEventType;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.PaymentDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.jpa.entities.Currency;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.mail.EventSubscriptionBuilder;
import com.picsauditing.model.billing.AccountingSystemSynchronization;
import com.picsauditing.model.billing.BillingNoteModel;
import com.picsauditing.util.EmailAddressUtils;
import com.picsauditing.braintree.CreditCard;
import com.picsauditing.util.log.PicsLogger;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.*;

@SuppressWarnings("serial")
public class PaymentDetail extends ContractorActionSupport implements Preparable {

	private static final String REFUND_ON_BRAIN_TREE_PICS_BUTTON = "Refund on BrainTree/PICS";
	private static final String COLLECT_PAYMENT_BUTTON = "Collect Payment";
	private static final String SAVE_BUTTON = "Save";
	private static final String APPLY_BUTTON = "apply";
	private static final String UNAPPLY_BUTTON = "unapply";
	private static final String REFUND_BUTTON = "Refund";
	private static final String VOID_CC_BUTTON = "voidcc";
	private static final String DELETE_BUTTON = "Delete";
	private static final String FIND_CC_BUTTON = "findcc";
	@Autowired
	private PaymentDAO paymentDAO;
	@Autowired
	private NoteDAO noteDAO;
	@Autowired
	private BillingService billingService;
	@Autowired
	private EmailSender emailSender;
	@Autowired
	private BrainTree paymentService;
	@Autowired
	private DataObservable salesCommissionDataObservable;
	@Autowired
	private BillingNoteModel billingNoteModel;

	private Payment payment;
	private PaymentMethod method = null;
	private CreditCard creditCard;
	private Map<Integer, BigDecimal> amountApplyMap = new HashMap<Integer, BigDecimal>();

	private String transactionCondition = null;
	private BigDecimal refundAmount;
	private boolean changePaymentMethodOnAccount = false;

	public PaymentDetail() {
		this.subHeading = "Payment Detail";
	}

	@Override
	public void prepare() {
		int paymendId = getParameter("payment.id");
		if (paymendId > 0) {
			payment = paymentDAO.find(paymendId);
			if (payment != null) {
				id = payment.getAccount().getId();
				account = payment.getAccount();
				contractor = (ContractorAccount) account;
			}
		}
	}

	@RequiredPermission(value = OpPerms.AllContractors)
	public String execute() throws Exception {
        if (contractor == null) {
            findContractor();
        }

        if (FIND_CC_BUTTON.equals(button)) {
			creditCard = paymentService.getCreditCard(contractor);
			method = PaymentMethod.CreditCard;
			return SUCCESS;
		}

		if (method == null) {
			method = contractor.getPaymentMethod();
		}

		if (payment == null || payment.getId() == 0) {
			// Useful during development, we can remove this later
			for (Invoice invoice : contractor.getInvoices()) {
				invoice.updateAmountApplied();
			}
		} else {
			payment.updateAmountApplied();
			for (PaymentAppliedToInvoice ip : payment.getInvoices()) {
				ip.getInvoice().updateAmountApplied();
			}

			// Activate the contractor if an activation invoice is fully applied
			// (this will occur on the redirect)
			if (contractor.getStatus().isPendingOrDeactivated()) {
				for (PaymentAppliedToInvoice ip : payment.getInvoices()) {
					if (billingService.activateContractor(contractor, ip.getInvoice())) {
						contractorAccountDao.save(contractor);
						break;
					}
				}
			}
		}

		if (button != null) {
			if (payment == null || payment.getId() == 0) {
				// If we have a button but no payment, then we're creating a new
				// payment
				payment.setAccount(contractor);
				payment.setAuditColumns(permissions);
				payment.setPaymentMethod(method);
				Currency appliedInvoiceCurrency = getCurrencyOfAppliedInvoices(contractor, amountApplyMap);
				payment.setCurrency(appliedInvoiceCurrency);

				if (payment.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
					addActionError("Payments must be greater than zero");
					return SUCCESS;
				}
				if (method.isCreditCard()) {
					try {
						if (creditCard == null || creditCard.getCardNumber() == null) {
							creditCard = paymentService.getCreditCard(contractor);
						}
						paymentService.processPayment(payment, null);
						payment.setCcNumber(creditCard.getCardNumber());

						addNote("Credit Card transaction completed and emailed the receipt for "
								+ payment.getCurrency().getSymbol() + payment.getTotalAmount());
					} catch (NoBrainTreeServiceResponseException re) {
						addNote("Credit Card service connection error: " + re.getMessage());

						EmailBuilder emailBuilder = new EmailBuilder();
						emailBuilder.setTemplate(EmailTemplate.BRAIN_TREE_ERROR_EMAIL_TEMPLATE);
						emailBuilder.setFromAddress(EmailAddressUtils.PICS_IT_TEAM_EMAIL);
						emailBuilder.setToAddresses(EmailAddressUtils.getBillingEmail(contractor.getCurrency()));
						emailBuilder.setPermissions(permissions);
						emailBuilder.addToken("permissions", permissions);
						emailBuilder.addToken("contractor", contractor);
						emailBuilder.addToken("billingusers", contractor.getUsersByRole(OpPerms.ContractorBilling));
						emailBuilder.addToken("invoice", null);

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
											+ contractor.getName() + " (" + contractor.getId() + ")");
						}

						addActionError("There has been a connection error while processing your payment. Our Billing department has been notified and will contact you after confirming the status of your payment. Please contact the PICS Billing Department at "
								+ getText("PicsBillingPhone") + ".");

						// Assuming paid status per Aaron so that he can refund
						// or void manually.
						payment.setStatus(TransactionStatus.Unpaid);
						paymentDAO.save(payment);

						notifyDataChange(new PaymentDataEvent(payment, PaymentEventType.PAYMENT));

						return SUCCESS;
					} catch (Exception e) {
						addNote("Credit Card transaction failed: " + e.getMessage());
						this.addActionError("Failed to charge credit card. " + e.getMessage());
						return SUCCESS;
					}

				} // Do nothing for checks
			}

			String message = null;
			if (DELETE_BUTTON.equalsIgnoreCase(button)) {
				if (payment.getQbListID() != null) {
					addActionError("You can't delete a payment that has already been synced with QuickBooks.");
					return SUCCESS;
				}

				notifyDataChange(new PaymentDataEvent(payment, PaymentEventType.REMOVE));
				paymentDAO.remove(payment);

				addActionMessage("Successfully Deleted Payment");

				return setUrlForRedirect("BillingDetail.action?id=" + contractor.getId());
			}

			if (VOID_CC_BUTTON.equalsIgnoreCase(button)) {
				try {
					paymentService.voidTransaction(payment.getTransactionID());
					message = "Successfully canceled credit card transaction";
					payment.setStatus(TransactionStatus.Void);
					transactionCondition = null;

					unapplyAll();
					notifyDataChange(new PaymentDataEvent(payment, PaymentEventType.REMOVE));
					paymentDAO.remove(payment); // per Aaron's request

					if (message != null) {
						addActionMessage(message);
					}

					return setUrlForRedirect("BillingDetail.action?id=" + contractor.getId());
				} catch (Exception e) {
					addActionError("Failed to cancel credit card transaction: " + e.getMessage());
					return SUCCESS;
				}
			}

			if (button.startsWith(REFUND_BUTTON)) {
				if (refundAmount.compareTo(BigDecimal.ZERO) <= 0) {
					addActionError("You can't refund negative amounts");
					return SUCCESS;
				}

				if (refundAmount.compareTo(payment.getBalance()) > 0) {
					addActionError("You can't refund more than the remaining balance");
					return SUCCESS;
				}

				try {
					Refund refund = new Refund();
					refund.setTotalAmount(refundAmount);
					refund.setAccount(contractor);
					refund.setAuditColumns(permissions);
					refund.setStatus(TransactionStatus.Paid);
					if (REFUND_ON_BRAIN_TREE_PICS_BUTTON.equals(button)) {
						if (payment.getPaymentMethod().isCreditCard()) {
							paymentService.processRefund(payment.getTransactionID(), refundAmount);
							message = "Successfully refunded credit card on BrainTree";
							refund.setCcNumber(payment.getCcNumber());
							refund.setTransactionID(payment.getTransactionID());
						}
					}

					AccountingSystemSynchronization.setToSynchronize(refund);
					PaymentProcessor.ApplyPaymentToRefund(payment, refund, getUser(), refundAmount);
					paymentDAO.save(refund);

					notifyDataChange(new PaymentDataEvent(payment, PaymentEventType.REFUND));
				} catch (Exception e) {
					addActionError("Failed to cancel credit card transaction: " + e.getMessage());
					return SUCCESS;
				}
			}

			if (amountApplyMap.size() > 0) {
				boolean collected = false;
				if (SAVE_BUTTON.equals(button)) {
					button = APPLY_BUTTON;
				}

				if (COLLECT_PAYMENT_BUTTON.equals(button)) {
					collected = true;
					button = APPLY_BUTTON;
				}

				if (UNAPPLY_BUTTON.equals(button)) {
					// Find the Invoice or Refund # passed through the
					// amountApplyMap and remove it
					for (int txnID : amountApplyMap.keySet()) {
						Iterator<PaymentAppliedToInvoice> iterInvoice = payment.getInvoices().iterator();
						while (iterInvoice.hasNext()) {
							PaymentAppliedToInvoice pa = iterInvoice.next();
							if (pa.getInvoice().getId() == txnID) {
								paymentDAO.removePaymentInvoice(pa, getUser());
								notifyDataChange(new PaymentDataEvent(pa.getPayment(), PaymentEventType.REMOVE));
								return setUrlForRedirect("PaymentDetail.action?payment.id=" + payment.getId());
							}
						}

						Iterator<PaymentAppliedToRefund> iterRefund = payment.getRefunds().iterator();
						while (iterRefund.hasNext()) {
							PaymentAppliedToRefund pa = iterRefund.next();
							if (pa.getRefund().getId() == txnID) {
								paymentDAO.removePaymentRefund(pa, getUser());
								notifyDataChange(new PaymentDataEvent(pa.getPayment(), PaymentEventType.REMOVE));
								return setUrlForRedirect("PaymentDetail.action?payment.id=" + payment.getId());
							}
						}
					}
				}
				if (APPLY_BUTTON.equals(button)) {
					if (changePaymentMethodOnAccount) {
						contractor.setPaymentMethod(method);
						contractor.setAuditColumns(permissions);
						contractorAccountDao.save(contractor);
					}
					for (int txnID : amountApplyMap.keySet()) {
						if (amountApplyMap.get(txnID).compareTo(BigDecimal.ZERO) > 0) {
							for (Invoice txn : contractor.getInvoices()) {
								if (txn.getId() == txnID) {
									PaymentProcessor.ApplyPaymentToInvoice(payment, txn, getUser(),
											amountApplyMap.get(txnID));

									// Email Receipt to Contractor
									try {
										if (collected) {
											EventSubscriptionBuilder.contractorInvoiceEvent(contractor, txn);
										}
									} catch (Exception e) {
										/**
										 * The above can throw an exception if a
										 * user doesn't have an email address
										 * defined. If this happens, then a
										 * payment will be processed online
										 * without a record being saved on PICS.
										 * We should still create the Payment in
										 * PICS even if the email sending
										 * failed.
										 */
									}
								}
							}

							for (Refund txn : contractor.getRefunds()) {
								if (txn.getId() == txnID) {
									PaymentProcessor.ApplyPaymentToRefund(payment, txn, getUser(),
											amountApplyMap.get(txnID));
								}
							}
						}
					}
				}
			}

			payment.updateAmountApplied();
			AccountingSystemSynchronization.setToSynchronize(payment);
			paymentDAO.save(payment);

			notifyDataChange(new PaymentDataEvent(payment, PaymentEventType.SAVE));

			if (message != null) {
				addActionMessage(message);
			}

			return setUrlForRedirect("PaymentDetail.action?payment.id=" + payment.getId());
		}

		for (Invoice invoice : contractor.getInvoices()) {
			if (!amountApplyMap.containsKey(invoice.getId())) {
				amountApplyMap.put(invoice.getId(), BigDecimal.ZERO.setScale(2));
			}
		}

		return SUCCESS;
	}

	private Currency getCurrencyOfAppliedInvoices(ContractorAccount contractor, Map<Integer, BigDecimal> amountApplyMap) {
		List<Invoice> appliedInvoices = getAppliedInvoices(contractor, amountApplyMap);

		return getOverallInvoiceCurrency(contractor, appliedInvoices);
	}

	private Currency getOverallInvoiceCurrency(ContractorAccount contractor, List<Invoice> appliedInvoices) {
		Currency overallInvoiceCurrency = null;
		boolean isAllInvoicesSameCurrency = false;
		if (appliedInvoices.size() > 0) {
			overallInvoiceCurrency = appliedInvoices.get(0).getCurrency();
			isAllInvoicesSameCurrency = true;
		}

		for (Invoice appliedInvoice : appliedInvoices) {
			if (isAllInvoicesSameCurrency && !appliedInvoice.getCurrency().equals(overallInvoiceCurrency)) {
				isAllInvoicesSameCurrency = false;
			}
		}

		return isAllInvoicesSameCurrency ? overallInvoiceCurrency : contractor.getCountry().getCurrency();
	}

	/**
	 * Returns a list of invoices to apply a payment to.
	 */
	private List<Invoice> getAppliedInvoices(ContractorAccount contractor, Map<Integer, BigDecimal> amountApplyMap) {
		List<Invoice> appliedInvoices = new ArrayList<Invoice>();

		for (Invoice invoice : contractor.getInvoices()) {
			if (amountApplyMap.get(invoice.getId()) != null) {
				appliedInvoices.add(invoice);
			}
		}

		return appliedInvoices;
	}

	private void unapplyAll() {
		Iterator<PaymentAppliedToInvoice> iterInvoice = payment.getInvoices().iterator();
		while (iterInvoice.hasNext()) {
			PaymentAppliedToInvoice pa = iterInvoice.next();
			paymentDAO.removePaymentInvoice(pa, getUser());
		}

		Iterator<PaymentAppliedToRefund> iterRefund = payment.getRefunds().iterator();
		while (iterRefund.hasNext()) {
			PaymentAppliedToRefund pa = iterRefund.next();
			paymentDAO.removePaymentRefund(pa, getUser());
		}

		payment.getInvoices().clear();
		payment.getRefunds().clear();
	}

	private void addNote(String subject) {
		Note note = new Note(payment.getAccount(), billingNoteModel.findUserForPaymentNote(permissions), subject);
		note.setNoteCategory(NoteCategory.Billing);
		note.setCanContractorView(true);
		note.setViewableById(Account.PicsID);
		noteDAO.save(note);
	}

	public boolean isHasUnpaidInvoices() {
		if (payment != null && !payment.getStatus().isUnpaid()) {
			return false;
		}

		for (Invoice invoice : contractor.getInvoices()) {
			if (invoice.getTotalAmount().compareTo(BigDecimal.ZERO) > 0 && invoice.getStatus().isUnpaid()) {
				return true;
			}
		}

		return false;
	}

	// Getters and Setters

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	public PaymentMethod getMethod() {
		return method;
	}

	public void setMethod(PaymentMethod method) {
		this.method = method;
	}

	public Map<Integer, BigDecimal> getAmountApplyMap() {
		return amountApplyMap;
	}

	public void setAmountApplyMap(Map<Integer, BigDecimal> amountApplyMap) {
		this.amountApplyMap = amountApplyMap;
	}

	public CreditCard getCreditCard() {
		return creditCard;
	}

	public void setCreditCard(CreditCard creditCard) {
		this.creditCard = creditCard;
	}

	public BigDecimal getRefundAmount() {
		return refundAmount;
	}

	public void setRefundAmount(BigDecimal refundAmount) {
		this.refundAmount = refundAmount;
	}

	public String getTransactionCondition() {
		if (transactionCondition != null) {
			return transactionCondition;
		}

		try {
			return paymentService.getTransactionCondition(payment.getTransactionID());
		} catch (Exception e) {
			return null;
		}
	}

	public boolean isChangePaymentMethodOnAccount() {
		return changePaymentMethodOnAccount;
	}

	public void setChangePaymentMethodOnAccount(boolean changePaymentMethodOnAccount) {
		this.changePaymentMethodOnAccount = changePaymentMethodOnAccount;
	}

	private <T> void notifyDataChange(DataEvent<T> dataEvent) {
		salesCommissionDataObservable.setChanged();
		salesCommissionDataObservable.notifyObservers(dataEvent);
	}
}