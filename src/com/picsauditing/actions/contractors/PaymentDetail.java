package com.picsauditing.actions.contractors;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.BrainTreeService;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.dao.InvoicePaymentDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.PaymentDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceItem;
import com.picsauditing.jpa.entities.InvoicePayment;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.Payment;
import com.picsauditing.jpa.entities.PaymentMethod;

@SuppressWarnings("serial")
public class PaymentDetail extends ContractorActionSupport implements Preparable {
	private boolean edit = false;

	private InvoiceDAO invoiceDAO;
	private PaymentDAO paymentDAO;
	private InvoicePaymentDAO invoicePaymentDAO;
	private NoteDAO noteDAO;
	private AppPropertyDAO appPropDao;

	private BrainTreeService paymentService = new BrainTreeService();

	private Payment payment;

	private BigDecimal amountLeft;

	private Map<Integer, Boolean> applyMap = new HashMap<Integer, Boolean>();
	private Map<Integer, Boolean> unApplyMap = new HashMap<Integer, Boolean>();
	private Map<Integer, BigDecimal> amountApplyMap = new HashMap<Integer, BigDecimal>();

	public PaymentDetail(InvoiceDAO invoiceDAO, AppPropertyDAO appPropDao, NoteDAO noteDAO,
			ContractorAccountDAO conAccountDAO, ContractorAuditDAO auditDao, PaymentDAO paymentDAO,
			InvoicePaymentDAO invoicePaymentDAO) {
		super(conAccountDAO, auditDao);
		this.invoiceDAO = invoiceDAO;
		this.appPropDao = appPropDao;
		this.noteDAO = noteDAO;
		this.paymentDAO = paymentDAO;
		this.invoicePaymentDAO = invoicePaymentDAO;
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

		if (payment != null) {
			payment.updateAmountApplied();
			for (InvoicePayment ip : payment.getInvoices())
				ip.getInvoice().updateAmountApplied();
		} else {
			for (Invoice invoice : contractor.getInvoices())
				invoice.updateAmountApplied();
		}

		if (button != null) {
			if ("Credit Card".equals(button)) {
				if (applyMap.size() == 0)
					this.redirect("PaymentDetail.action?id=" + contractor.getId());
				payment = new Payment();
				payment.setAccount(account);
				payment.setAuditColumns(permissions);
				// for (Invoice inv : contractor.getInvoices()) {
				// if (applyMap.get(inv.getId()) != null &&
				// applyMap.get(inv.getId())) {
				// payment.setTotalAmount(payment.getTotalAmount().add(inv.getBalance()));
				// amountApplyMap.put(inv.getId(), inv.getBalance());
				// }
				// }

				payment = paymentDAO.save(payment);

				button = "Save";
			}

			if ("Save".equals(button) && payment != null) {
				for (Iterator<InvoicePayment> ip = payment.getInvoices().iterator(); ip.hasNext();) {
					InvoicePayment invoicePayment = ip.next();
					if (unApplyMap.get(invoicePayment.getId()) != null && unApplyMap.get(invoicePayment.getId())) {
						ip.remove();
						invoicePayment.getInvoice().getPayments().remove(invoicePayment);
						invoicePayment.getInvoice().updateAmountApplied();
						invoiceDAO.save(invoicePayment.getInvoice());
						invoicePaymentDAO.remove(invoicePayment);
						
						if (contractor.getPaymentMethod().isCreditCard())
							payment.setTotalAmount(payment.getTotalAmount().subtract(invoicePayment.getAmount()));
					}
				}
				payment.updateAmountApplied();

				payment.setPaymentMethod(contractor.getPaymentMethod());

				for (Invoice inv : contractor.getInvoices()) {
					if (applyMap.get(inv.getId()) != null && applyMap.get(inv.getId())) {
						if (contractor.getPaymentMethod().isCreditCard()) {
							payment.setTotalAmount(payment.getTotalAmount().add(inv.getBalance()));
							applyPayment(inv, inv.getBalance());
						} else if (amountApplyMap.get(inv.getId()) != null
								&& amountApplyMap.get(inv.getId()).compareTo(BigDecimal.ZERO) > 0) {
							applyPayment(inv, amountApplyMap.get(inv.getId()));
						}
					}
				}
			}

			if ("Collect Check".equals(button)) {
				payment.setAccount(contractor);
				payment.setAuditColumns(permissions);
				paymentDAO.save(payment);
			}

			if ("Delete".equals(button)) {
				if (payment != null) {
					paymentDAO.remove(payment);
					for (InvoicePayment ip : payment.getInvoices()) {
						ip.getInvoice().updateAmountApplied();
					}

					payment = null;
				}
			}
			//
			// if (button.startsWith("Charge Credit Card") &&
			// contractor.isCcOnFile()) {
			// paymentService.setUserName(appPropDao.find("brainTree.username").getValue());
			// paymentService.setPassword(appPropDao.find("brainTree.password").getValue());
			//
			// try {
			// Payment payment = createPayment();
			// payment.setPaymentMethod(PaymentMethod.CreditCard);
			//
			// paymentService.processPayment(payment);
			//
			// CreditCard cc = paymentService.getCreditCard(id);
			// payment.setCcNumber(cc.getCardNumber());
			//
			// applyPayment(payment);
			// addNote("Credit Card transaction completed and emailed the receipt for $"
			// + payment.getTotalAmount());
			// } catch (Exception e) {
			// addNote("Credit Card transaction failed: " + e.getMessage());
			// this.addActionError("Failed to charge credit card. " +
			// e.getMessage());
			// return SUCCESS;
			// }
			// }

			if (payment != null)
				this.redirect("PaymentDetail.action?payment.id=" + payment.getId());
			else
				this.redirect("PaymentDetail.action?id=" + contractor.getId());

			return SUCCESS;
		}

		if (payment != null) {
			if (payment.getBalance().compareTo(BigDecimal.ZERO) > 0) {
				BigDecimal leftover = payment.getBalance();
				for (Invoice inv : contractor.getInvoices()) {
					if (inv.getStatus().isUnpaid()) {
						if (leftover.compareTo(inv.getBalance()) < 0) {
							amountApplyMap.put(inv.getId(), leftover);
							applyMap.put(inv.getId(), leftover.compareTo(BigDecimal.ZERO) > 0);
							// payment.setAmountApplied(payment.getAmountApplied().add(payment.getBalance()));
							leftover = BigDecimal.ZERO;

						} else if (leftover.compareTo(BigDecimal.ZERO) > 0) {
							amountApplyMap.put(inv.getId(), inv.getBalance());
							applyMap.put(inv.getId(), leftover.compareTo(BigDecimal.ZERO) > 0);
							// payment.setAmountApplied(payment.getAmountApplied().add(inv.getBalance()));
							leftover = leftover.subtract(inv.getBalance());
						}

					}
				}
			}
		}

		return SUCCESS;
	}

	private void applyPayment(Invoice invoice, BigDecimal amount) {
		if (!paymentDAO.applyPayment(payment, invoice, getUser(), amount))
			addActionError("Payment of $" + amount + " was not applied to invoice #" + invoice.getId());

		if (invoice.getStatus().isPaid()) {
			if (!contractor.isActiveB()) {
				for (InvoiceItem item : invoice.getItems()) {
					if (item.getInvoiceFee().getFeeClass().equals("Membership")) {
						contractor.setActive('Y');
						contractor.setAuditColumns(getUser());
					}
				}
			}
		}

		// Send a receipt to the contractor
		try {
			// TODO: send Email
			// emailInvoice();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void calculateAmountApplied() {
		payment.getBalance();
	}

	private void addNote(String subject) {
		Note note = new Note(payment.getAccount(), getUser(), subject);
		note.setNoteCategory(NoteCategory.Billing);
		note.setCanContractorView(true);
		note.setViewableById(Account.PicsID);
		noteDAO.save(note);
	}

	// GETTERS AND SETTERS

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	public BigDecimal getAmountLeft(BigDecimal amount) {
		if (amountLeft == null)
			amountLeft = payment.getBalance();

		if (amountLeft.compareTo(amount) < 0) {
			BigDecimal tmp = amountLeft;
			amountLeft = BigDecimal.ZERO;
			return tmp;
		}

		amountLeft = amountLeft.subtract(amount);

		if (amountLeft.compareTo(BigDecimal.ZERO) < 0)
			return BigDecimal.ZERO;

		return amount;
	}

	public void setAmountLeft(BigDecimal amountLeft) {
		this.amountLeft = amountLeft;
	}

	public Map<Integer, Boolean> getApplyMap() {
		return applyMap;
	}

	public void setApplyMap(Map<Integer, Boolean> applyMap) {
		this.applyMap = applyMap;
	}

	public Map<Integer, Boolean> getUnApplyMap() {
		return unApplyMap;
	}

	public void setUnApplyMap(Map<Integer, Boolean> unApplyMap) {
		this.unApplyMap = unApplyMap;
	}

	public Map<Integer, BigDecimal> getAmountApplyMap() {
		return amountApplyMap;
	}

	public void setAmountApplyMap(Map<Integer, BigDecimal> amountApplyMap) {
		this.amountApplyMap = amountApplyMap;
	}

	public boolean isHasUnpaidInvoices() {
		for (Invoice invoice : contractor.getInvoices()) {
			if (invoice.getStatus().isUnpaid())
				return true;
		}

		return false;
	}

}
