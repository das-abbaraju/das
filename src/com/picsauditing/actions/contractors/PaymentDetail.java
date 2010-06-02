package com.picsauditing.actions.contractors;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.BillingCalculatorSingle;
import com.picsauditing.PICS.BrainTreeService;
import com.picsauditing.PICS.NoBrainTreeServiceResponseException;
import com.picsauditing.PICS.PaymentProcessor;
import com.picsauditing.PICS.BrainTreeService.CreditCard;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.PaymentDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.Payment;
import com.picsauditing.jpa.entities.PaymentAppliedToInvoice;
import com.picsauditing.jpa.entities.PaymentAppliedToRefund;
import com.picsauditing.jpa.entities.PaymentMethod;
import com.picsauditing.jpa.entities.Refund;
import com.picsauditing.jpa.entities.TransactionStatus;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.log.PicsLogger;

@SuppressWarnings("serial")
public class PaymentDetail extends ContractorActionSupport implements Preparable {

	private Payment payment;
	private PaymentMethod method = null;
	private CreditCard creditCard;
	private Map<Integer, BigDecimal> amountApplyMap = new HashMap<Integer, BigDecimal>();

	private PaymentDAO paymentDAO;
	private NoteDAO noteDAO;
	private AppPropertyDAO appPropDao;
	private BrainTreeService paymentService = new BrainTreeService();
	private String transactionCondition = null;
	private BigDecimal refundAmount;

	public PaymentDetail(AppPropertyDAO appPropDao, NoteDAO noteDAO, ContractorAccountDAO conAccountDAO,
			ContractorAuditDAO auditDao, PaymentDAO paymentDAO) {
		super(conAccountDAO, auditDao);
		this.appPropDao = appPropDao;
		this.noteDAO = noteDAO;
		this.paymentDAO = paymentDAO;
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

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		if ("findcc".equals(button)) {
			paymentService.setUserName(appPropDao.find("brainTree.username").getValue());
			paymentService.setPassword(appPropDao.find("brainTree.password").getValue());
			creditCard = paymentService.getCreditCard(id);
			method = PaymentMethod.CreditCard;
			return SUCCESS;
		}

		if (contractor == null)
			findContractor();

		if (method == null)
			method = contractor.getPaymentMethod();

		if (payment == null || payment.getId() == 0) {
			// Useful during development, we can remove this later
			for (Invoice invoice : contractor.getInvoices())
				invoice.updateAmountApplied();
		} else {
			payment.updateAmountApplied();
			for (PaymentAppliedToInvoice ip : payment.getInvoices())
				ip.getInvoice().updateAmountApplied();

			// Activate the contractor if an activation invoice is fully applied
			// (this will occur on the redirect)
			if (contractor.getStatus().isPendingDeactivated()) {
				for (PaymentAppliedToInvoice ip : payment.getInvoices()) {
					if (BillingCalculatorSingle.activateContractor(contractor, ip.getInvoice())) {
						accountDao.save(contractor);
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

				if (payment.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
					addActionError("Payments must be greater than zero");
					return SUCCESS;
				}
				if (method.isCreditCard()) {
					try {
						paymentService.setUserName(appPropDao.find("brainTree.username").getValue());
						paymentService.setPassword(appPropDao.find("brainTree.password").getValue());

						if (creditCard == null || creditCard.getCardNumber() == null) {
							creditCard = paymentService.getCreditCard(id);
						}
						paymentService.processPayment(payment, null);
						payment.setCcNumber(creditCard.getCardNumber());

						addNote("Credit Card transaction completed and emailed the receipt for $"
								+ payment.getTotalAmount());
					} catch (NoBrainTreeServiceResponseException re) {
						addNote("Credit Card service connection error: " + re.getMessage());

						EmailBuilder emailBuilder = new EmailBuilder();
						emailBuilder.setTemplate(106);
						emailBuilder.setFromAddress("\"PICS IT Team\"<it@picsauditing.com>");
						emailBuilder.setToAddresses("billing@picsauditing.com");
						emailBuilder.setPermissions(permissions);
						emailBuilder.addToken("permissions", permissions);
						emailBuilder.addToken("contractor", contractor);
						emailBuilder.addToken("billingusers", contractor.getUsersByRole(OpPerms.ContractorBilling));
						emailBuilder.addToken("invoice", null);

						EmailQueue emailQueue;
						try {
							emailQueue = emailBuilder.build();
							emailQueue.setPriority(90);
							emailQueue.setViewableById(Account.PicsID);
							EmailSender.send(emailQueue);
						} catch (Exception e) {
							PicsLogger
									.log("Cannot send email error message or determine credit processing status for contractor "
											+ contractor.getName() + " (" + contractor.getId() + ")");
						}

						addActionError("There has been a connection error while processing your payment. Our Billing department has been notified and will contact you after confirming the status of your payment. Please contact the PICS Billing Department at 1-(800)506-PICS x708.");

						// Assuming paid status per Aaron so that he can refund
						// or void manually.
						payment.setStatus(TransactionStatus.Unpaid);
						paymentDAO.save(payment);

						return SUCCESS;
					} catch (Exception e) {
						addNote("Credit Card transaction failed: " + e.getMessage());
						this.addActionError("Failed to charge credit card. " + e.getMessage());
						return SUCCESS;
					}

				} // Do nothing for checks
			}

			String message = null;
			if (button.equalsIgnoreCase("Delete")) {
				if (payment.getQbListID() != null) {
					addActionError("You can't delete a payment that has already been synced with QuickBooks.");
					return SUCCESS;
				}
				paymentDAO.remove(payment);
				redirect("BillingDetail.action?id=" + contractor.getId() + "&msg=" + "Successfully Deleted Payment");
				return BLANK;
			}

			if (button.equalsIgnoreCase("voidcc")) {
				try {
					paymentService.setUserName(appPropDao.find("brainTree.username").getValue());
					paymentService.setPassword(appPropDao.find("brainTree.password").getValue());

					paymentService.voidTransaction(payment.getTransactionID());
					message = "Successfully canceled credit card transaction";
					payment.setStatus(TransactionStatus.Void);
					transactionCondition = null;

					unapplyAll();

					paymentDAO.remove(payment); // per Aaron's request

					String url = "BillingDetail.action?id=" + contractor.getId();
					if (message != null)
						url += "&msg=" + message;
					redirect(url);
					return BLANK;
				} catch (Exception e) {
					addActionError("Failed to cancel credit card transaction: " + e.getMessage());
					return SUCCESS;
				}
			}
			if (button.startsWith("Refund")) {
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
					if (button.equals("Refund on BrainTree/PICS")) {
						if (payment.getPaymentMethod().isCreditCard()) {
							paymentService.setUserName(appPropDao.find("brainTree.username").getValue());
							paymentService.setPassword(appPropDao.find("brainTree.password").getValue());
							paymentService.processRefund(payment.getTransactionID(), refundAmount);
							message = "Successfully refunded credit card on BrainTree";
							refund.setCcNumber(payment.getCcNumber());
							refund.setTransactionID(payment.getTransactionID());
						}
					}
					refund.setQbSync(true);
					PaymentProcessor.ApplyPaymentToRefund(payment, refund, getUser(), refundAmount);
					paymentDAO.save(refund);

				} catch (Exception e) {
					addActionError("Failed to cancel credit card transaction: " + e.getMessage());
					return SUCCESS;
				}
			}

			if (amountApplyMap.size() > 0) {
				if ("Save".equals(button) || "Collect Payment".equals(button)) {
					button = "apply";
				}

				if (button.equals("unapply")) {
					// Find the Invoice or Refund # passed through the
					// amountApplyMap and remove it
					for (int txnID : amountApplyMap.keySet()) {
						Iterator<PaymentAppliedToInvoice> iterInvoice = payment.getInvoices().iterator();
						while (iterInvoice.hasNext()) {
							PaymentAppliedToInvoice pa = iterInvoice.next();
							if (pa.getInvoice().getId() == txnID) {
								paymentDAO.removePaymentInvoice(pa, getUser());
								redirect("PaymentDetail.action?payment.id=" + payment.getId());
								return BLANK;
							}
						}

						Iterator<PaymentAppliedToRefund> iterRefund = payment.getRefunds().iterator();
						while (iterRefund.hasNext()) {
							PaymentAppliedToRefund pa = iterRefund.next();
							if (pa.getRefund().getId() == txnID) {
								paymentDAO.removePaymentRefund(pa, getUser());
								redirect("PaymentDetail.action?payment.id=" + payment.getId());
								return BLANK;
							}
						}
					}
				}
				if (button.equals("apply")) {
					for (int txnID : amountApplyMap.keySet()) {
						if (amountApplyMap.get(txnID).compareTo(BigDecimal.ZERO) > 0) {
							for (Invoice txn : contractor.getInvoices()) {
								if (txn.getId() == txnID) {
									PaymentProcessor.ApplyPaymentToInvoice(payment, txn, getUser(), amountApplyMap
											.get(txnID));
								}
							}
							for (Refund txn : contractor.getRefunds()) {
								if (txn.getId() == txnID) {
									PaymentProcessor.ApplyPaymentToRefund(payment, txn, getUser(), amountApplyMap
											.get(txnID));
								}
							}
						}
					}
				}
			}

			payment.updateAmountApplied();
			payment.setQbSync(true);
			paymentDAO.save(payment);

			String url = "PaymentDetail.action?payment.id=" + payment.getId();
			if (message != null)
				url += "&msg=" + message;
			redirect(url);
			return BLANK;
		}

		for (Invoice invoice : contractor.getInvoices()) {
			if (!amountApplyMap.containsKey(invoice.getId()))
				amountApplyMap.put(invoice.getId(), BigDecimal.ZERO.setScale(2));
		}

		return SUCCESS;
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
		Note note = new Note(payment.getAccount(), getUser(User.SYSTEM), subject);
		note.setNoteCategory(NoteCategory.Billing);
		note.setCanContractorView(true);
		note.setViewableById(Account.PicsID);
		noteDAO.save(note);
	}

	public boolean isHasUnpaidInvoices() {
		if (payment != null && !payment.getStatus().isUnpaid())
			return false;

		for (Invoice invoice : contractor.getInvoices()) {
			if (invoice.getTotalAmount().compareTo(BigDecimal.ZERO) > 0 && invoice.getStatus().isUnpaid())
				return true;
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
		if (transactionCondition != null)
			return transactionCondition;

		try {
			paymentService.setUserName(appPropDao.find("brainTree.username").getValue());
			paymentService.setPassword(appPropDao.find("brainTree.password").getValue());
			return paymentService.getTransactionCondition(payment.getTransactionID());
		} catch (Exception e) {
			return null;
		}
	}
}
