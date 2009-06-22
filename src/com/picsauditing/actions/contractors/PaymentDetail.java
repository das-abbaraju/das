package com.picsauditing.actions.contractors;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.BrainTreeService;
import com.picsauditing.PICS.BrainTreeService.CreditCard;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.PaymentDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.Payment;
import com.picsauditing.jpa.entities.PaymentAppliedToInvoice;
import com.picsauditing.jpa.entities.PaymentAppliedToRefund;
import com.picsauditing.jpa.entities.PaymentMethod;
import com.picsauditing.jpa.entities.Refund;
import com.picsauditing.jpa.entities.TransactionStatus;

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

		if (contractor == null)
			findContractor();

		if (method == null)
			method = contractor.getPaymentMethod();

		paymentService.setUserName(appPropDao.find("brainTree.username").getValue());
		paymentService.setPassword(appPropDao.find("brainTree.password").getValue());

		if (payment == null || payment.getId() == 0) {
			if (method.isCreditCard()) {
				creditCard = paymentService.getCreditCard(id);
			}
			// Useful during development, we can remove this later
			for (Invoice invoice : contractor.getInvoices())
				invoice.updateAmountApplied();
		} else {
			payment.updateAmountApplied();
			for (PaymentAppliedToInvoice ip : payment.getInvoices())
				ip.getInvoice().updateAmountApplied();
		}

		if (button != null) {
			if (payment == null || payment.getId() == 0) {
				// If we have a button but no payment, then we're creating a new
				// payment
				payment.setAccount(contractor);
				payment.setAuditColumns(getUser());
				payment.setPaymentMethod(method);

				if (payment.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
					addActionError("Payments must be greater than zero");
					return SUCCESS;
				}
				if (method.isCreditCard()) {
					try {
						if (creditCard == null || creditCard.getCardNumber() == null) {
							creditCard = paymentService.getCreditCard(id);
						}
						paymentService.processPayment(payment);
						payment.setCcNumber(creditCard.getCardNumber());

						addNote("Credit Card transaction completed and emailed the receipt for $"
								+ payment.getTotalAmount());
					} catch (Exception e) {
						addNote("Credit Card transaction failed: " + e.getMessage());
						this.addActionError("Failed to charge credit card. " + e.getMessage());
					}

				} else {
					// Check

				}

				// if (invoice.getStatus().isPaid()) {
				// if (!contractor.isActiveB()) {
				// for (InvoiceItem item : invoice.getItems()) {
				// if (item.getInvoiceFee().getFeeClass().equals("Membership"))
				// {
				// contractor.setActive('Y');
				// contractor.setAuditColumns(getUser());
				// }
				// }
				// }
				// }

			}

			String message = null;
			if (button.equalsIgnoreCase("Delete")) {
				if (payment.getQbListID() != null) {
					addActionError("You can't delete a payment that has already been synced with QuickBooks.");
					return SUCCESS;
				}
				paymentDAO.remove(payment);
				redirect("PaymentDetail.action?id=" + id + "&msg=" + "Successfully Deleted Payment");
				return BLANK;
			}

			if (button.equalsIgnoreCase("voidcc")) {
				try {
					paymentService.processCancellation(payment.getTransactionID());
					message = "Successfully canceled credit card transaction";
					payment.setStatus(TransactionStatus.Void);

					unapplyAll();
				} catch (Exception e) {
					addActionError("Failed to cancel credit card transaction: " + e.getMessage());
					return SUCCESS;
				}
			}
			if (button.equalsIgnoreCase("Refund")) {
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
					refund.setAuditColumns(getUser());
					refund.setStatus(TransactionStatus.Paid);

					if (payment.getPaymentMethod().isCreditCard()) {
						paymentService.processRefund(payment.getTransactionID(), refundAmount);
						message = "Successfully refunded credit card";
						refund.setCcNumber(payment.getCcNumber());
						refund.setTransactionID(payment.getTransactionID());
					}
					paymentDAO.save(refund);

					PaymentAppliedToRefund pr = new PaymentAppliedToRefund();
					pr.setPayment(payment);
					pr.setRefund(refund);
					pr.setAmount(refund.getTotalAmount());
					pr.setAuditColumns(getUser());
					payment.getRefunds().add(pr);
					refund.getPayments().add(pr);
				} catch (Exception e) {
					addActionError("Failed to cancel credit card transaction: " + e.getMessage());
					return SUCCESS;
				}
			}

			if (amountApplyMap.size() > 0) {
				if ("Save".equals(button)) {
					button = "apply";
				}

				if (button.equals("unapply")) {
					// Find the Invoice or Refind # passed through the
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
									PaymentAppliedToInvoice pa = new PaymentAppliedToInvoice();
									pa.setPayment(payment);
									pa.setInvoice(txn);
									pa.setAmount(amountApplyMap.get(txnID));
									pa.setAuditColumns(getUser());
									payment.getInvoices().add(pa);
								}
							}
							for (Refund txn : contractor.getRefunds()) {
								if (txn.getId() == txnID) {
									PaymentAppliedToRefund pa = new PaymentAppliedToRefund();
									pa.setPayment(payment);
									pa.setRefund(txn);
									pa.setAmount(amountApplyMap.get(txnID));
									pa.setAuditColumns(getUser());
									payment.getRefunds().add(pa);
								}
							}
						}
					}
				}
			}

			payment.updateAmountApplied();
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
		Note note = new Note(payment.getAccount(), getUser(), subject);
		note.setNoteCategory(NoteCategory.Billing);
		note.setCanContractorView(true);
		note.setViewableById(Account.PicsID);
		noteDAO.save(note);
	}

	public boolean isHasUnpaidInvoices() {
		if (payment != null && !payment.getStatus().isUnpaid())
			return false;

		for (Invoice invoice : contractor.getInvoices()) {
			if (invoice.getStatus().isUnpaid())
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
			return paymentService.getTransactionCondition(payment.getTransactionID());
		} catch (Exception e) {
			return null;
		}
	}
}
