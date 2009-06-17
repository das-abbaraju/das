package com.picsauditing.actions.contractors;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

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
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoicePayment;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.Payment;
import com.picsauditing.jpa.entities.PaymentMethod;
import com.picsauditing.jpa.entities.User;

@SuppressWarnings("serial")
public class PaymentDetail extends ContractorActionSupport implements Preparable {
	private boolean edit = false;

	private InvoiceDAO invoiceDAO;
	private InvoiceFeeDAO invoiceFeeDAO;
	private InvoiceItemDAO invoiceItemDAO;
	private PaymentDAO paymentDAO;
	private NoteDAO noteDAO;
	private AppPropertyDAO appPropDao;

	private BrainTreeService paymentService = new BrainTreeService();

	private Payment payment;

	private BigDecimal amountLeft;

	private Map<Integer, Boolean> applyMap = new HashMap<Integer, Boolean>();
	private Map<Integer, BigDecimal> amountApplyMap = new HashMap<Integer, BigDecimal>();

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

		if (button != null) {
			if ("Apply".equals(button) && payment != null) {
				if (contractor.getPaymentMethod().isCheck()) {
					payment.setAccount(contractor);
					payment = paymentDAO.save(payment);
					for (Invoice inv : contractor.getInvoices()) {
						if (applyMap.get(inv.getId()) && BigDecimal.ZERO.compareTo(amountApplyMap.get(inv.getId())) < 0) {
							InvoicePayment ip = new InvoicePayment();
							ip.setInvoice(inv);
							ip.setPayment(payment);
							ip.setAmount(amountApplyMap.get(inv.getId()));

							payment.getInvoices().add(ip);
							paymentDAO.save(payment);
							inv.getPayments().add(ip);
							invoiceDAO.save(inv);
						}
					}
				}
			}

			if ("Collect Check".equals(button)) {
				addActionMessage("I will collect the check for $" + payment.getTotalAmount());
				payment.setAccount(contractor);
				payment.setAuditColumns(new User(User.SYSTEM));

				for (Invoice inv : contractor.getInvoices()) {
					if (inv.getStatus().isUnpaid()) {
						if (payment.getBalance().compareTo(inv.getBalance()) < 0) {
							amountApplyMap.put(inv.getId(), payment.getBalance());
							applyMap.put(inv.getId(), BigDecimal.ZERO.compareTo(amountApplyMap.get(inv.getId())) != 0);
							payment.setAmountApplied(payment.getAmountApplied().add(payment.getBalance()));

						} else {
							amountApplyMap.put(inv.getId(), inv.getBalance());
							applyMap.put(inv.getId(), BigDecimal.ZERO.compareTo(amountApplyMap.get(inv.getId())) != 0);
							payment.setAmountApplied(payment.getAmountApplied().add(inv.getBalance()));
						}

					}
				}
			}

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
		}

		return SUCCESS;
	}

	private Payment createPayment() {
		Payment payment = new Payment();
		payment.setAccount(account);
		// payment.setTotalAmount(invoice.getBalance());
		payment.setQbSync(true);
		payment.setAuditColumns(getUser());
		return paymentDAO.save(payment);
	}

	private void applyPayment(Payment payment) {
		// paymentDAO.applyPayment(payment, invoice, getUser(),
		// invoice.getBalance());
		// invoiceDAO.save(invoice);
		//
		// if (invoice.getStatus().isPaid()) {
		// if (!contractor.isActiveB()) {
		// for (InvoiceItem item : invoice.getItems()) {
		// if (item.getInvoiceFee().getFeeClass().equals("Membership")) {
		// contractor.setActive('Y');
		// contractor.setAuditColumns(getUser());
		// }
		// }
		// }
		// }

		// Send a receipt to the contractor
		try {
			// TODO: send Email
			// emailInvoice();
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

	public Map<Integer, BigDecimal> getAmountApplyMap() {
		return amountApplyMap;
	}

	public void setAmountApplyMap(Map<Integer, BigDecimal> amountApplyMap) {
		this.amountApplyMap = amountApplyMap;
	}

}
