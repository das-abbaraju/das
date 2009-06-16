package com.picsauditing.actions.contractors;

import java.math.BigDecimal;
import java.util.List;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.BrainTreeService;
import com.picsauditing.PICS.BrainTreeService.CreditCard;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.dao.InvoiceItemDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.PaymentDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.Payment;
import com.picsauditing.jpa.entities.PaymentMethod;
import com.picsauditing.jpa.entities.Transaction;

@SuppressWarnings("serial")
public class PaymentDetail extends ContractorActionSupport implements Preparable {
	private boolean edit = false;

	private InvoiceDAO invoiceDAO;
	private InvoiceFeeDAO invoiceFeeDAO;
	private InvoiceItemDAO invoiceItemDAO;
	private PaymentDAO paymentDAO;
	private NoteDAO noteDAO;

	private String checkNumber;
	private int newFeeId;
	private int refundFeeId;
	private BrainTreeService paymentService = new BrainTreeService();

	private Payment payment;
	private int invoiceID = 0;

	private List<InvoiceFee> feeList = null;

	private BigDecimal amountApply = BigDecimal.ZERO;

	AppPropertyDAO appPropDao;

	public PaymentDetail(InvoiceDAO invoiceDAO, AppPropertyDAO appPropDao, NoteDAO noteDAO,
			ContractorAccountDAO conAccountDAO, InvoiceFeeDAO invoiceFeeDAO, InvoiceItemDAO invoiceItemDAO,
			ContractorAuditDAO auditDao, PaymentDAO paymentDAO) {
		super(conAccountDAO, auditDao);
		this.invoiceDAO = invoiceDAO;
		this.appPropDao = appPropDao;
		this.noteDAO = noteDAO;
		this.invoiceFeeDAO = invoiceFeeDAO;
		this.invoiceItemDAO = invoiceItemDAO;
		this.paymentDAO = paymentDAO;
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

		if (button != null) {
			if ("Apply".equals(button) && payment != null && invoiceID > 0) {
				StringBuilder sb = new StringBuilder();

				sb.append("Payment applied for invoice #").append(invoiceID).append(" with the amomunt $").append(
						amountApply);
				addActionMessage(sb.toString());
			}

//			if (button.startsWith("unapplyPayment") && paymentID > 0) {
//				InvoicePayment ip = null;
//				for (InvoicePayment ip2 : invoice.getPayments()) {
//					if (ip2.getPayment().getId() == paymentID)
//						ip = ip2;
//				}
//				paymentDAO.removePayment(ip, getUser());
//			}
//			if (button.startsWith("Apply Existing Credit")) {
//				for (Payment payment : contractor.getPayments()) {
//					if (payment.getId() == paymentID && payment.getStatus().isUnpaid()) {
//						BigDecimal amount = null;
//						if (invoice.getBalance().compareTo(payment.getBalance()) > 0)
//							amount = payment.getBalance();
//						else
//							amount = invoice.getBalance();
//						paymentDAO.applyPayment(payment, invoice, getUser(), amount);
//					}
//				}
//			}
			if (button.startsWith("Charge Credit Card") && contractor.isCcOnFile()) {
				paymentService.setUserName(appPropDao.find("brainTree.username").getValue());
				paymentService.setPassword(appPropDao.find("brainTree.password").getValue());

				try {
					Payment payment = createPayment();
					payment.setPaymentMethod(PaymentMethod.CreditCard);

					paymentService.processPayment(payment);

					CreditCard cc = paymentService.getCreditCard(id);
					payment.setCcNumber(cc.getCardNumber());

					applyPayment(payment);
					addNote("Credit Card transaction completed and emailed the receipt for $"
							+ payment.getTotalAmount());
				} catch (Exception e) {
					addNote("Credit Card transaction failed: " + e.getMessage());
					this.addActionError("Failed to charge credit card. " + e.getMessage());
					return SUCCESS;
				}
			}
			if (button.startsWith("Collect Check")) {
				Payment payment = createPayment();
				payment.setCheckNumber(checkNumber);
				applyPayment(payment);
				addNote("Received check and emailed the receipt for $" + payment.getTotalAmount());
			}

		}

		return SUCCESS;
	}

	private Payment createPayment() {
		Payment payment = new Payment();
		payment.setAccount(account);
		//payment.setTotalAmount(invoice.getBalance());
		payment.setQbSync(true);
		payment.setAuditColumns(getUser());
		return paymentDAO.save(payment);
	}

	private void applyPayment(Payment payment) {
//		paymentDAO.applyPayment(payment, invoice, getUser(), invoice.getBalance());
//		invoiceDAO.save(invoice);
//
//		if (invoice.getStatus().isPaid()) {
//			if (!contractor.isActiveB()) {
//				for (InvoiceItem item : invoice.getItems()) {
//					if (item.getInvoiceFee().getFeeClass().equals("Membership")) {
//						contractor.setActive('Y');
//						contractor.setAuditColumns(getUser());
//					}
//				}
//			}
//		}

		// Send a receipt to the contractor
		try {
			// TODO: send Email
			//emailInvoice();
		} catch (Exception e) {
			// TODO: handle exception
		}
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

	public BigDecimal getAmountApply() {
		return amountApply;
	}

	public void setAmountApply(BigDecimal amountApply) {
		this.amountApply = amountApply;
	}

	public int getInvoiceID() {
		return invoiceID;
	}

	public void setInvoiceID(int invoiceID) {
		this.invoiceID = invoiceID;
	}
}
