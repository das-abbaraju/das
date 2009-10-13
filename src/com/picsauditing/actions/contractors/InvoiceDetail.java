package com.picsauditing.actions.contractors;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.BrainTreeService;
import com.picsauditing.PICS.PaymentProcessor;
import com.picsauditing.PICS.BrainTreeService.CreditCard;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.PaymentDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.InvoiceItem;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.Payment;
import com.picsauditing.jpa.entities.PaymentApplied;
import com.picsauditing.jpa.entities.PaymentAppliedToInvoice;
import com.picsauditing.jpa.entities.PaymentMethod;
import com.picsauditing.jpa.entities.TransactionStatus;
import com.picsauditing.mail.EventSubscriptionBuilder;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class InvoiceDetail extends ContractorActionSupport implements Preparable {
	private boolean edit = false;

	private InvoiceDAO invoiceDAO;
	private InvoiceFeeDAO invoiceFeeDAO;
	private PaymentDAO paymentDAO;
	private NoteDAO noteDAO;

	private int newFeeId;

	private Invoice invoice;

	private List<InvoiceFee> feeList = null;

	AppPropertyDAO appPropDao;

	private BrainTreeService paymentService = new BrainTreeService();

	public InvoiceDetail(InvoiceDAO invoiceDAO, AppPropertyDAO appPropDao, NoteDAO noteDAO,
			ContractorAccountDAO conAccountDAO, InvoiceFeeDAO invoiceFeeDAO, PaymentDAO paymentDAO,
			ContractorAuditDAO auditDao) {
		super(conAccountDAO, auditDao);
		this.invoiceDAO = invoiceDAO;
		this.appPropDao = appPropDao;
		this.paymentDAO = paymentDAO;
		this.noteDAO = noteDAO;
		this.invoiceFeeDAO = invoiceFeeDAO;
	}

	@Override
	public void prepare() {
		int invoiceId = getParameter("invoice.id");
		if (invoiceId > 0) {
			invoice = invoiceDAO.find(invoiceId);
			if (invoice != null) {
				id = invoice.getAccount().getId();
				account = invoice.getAccount();
				contractor = (ContractorAccount) account;
			}
		}
	}

	public String execute() throws NoRightsException, IOException {
		if (!forceLogin())
			return LOGIN;

		if (invoice == null) {
			addActionError("We could not find the invoice you were looking for");
			return BLANK;
		}

		if (!permissions.hasPermission(OpPerms.AllContractors)
				&& permissions.getAccountId() != invoice.getAccount().getId()) {
			throw new NoRightsException("You can't view this invoice");
		}

		invoice.updateAmountApplied();
		for (PaymentApplied ip : invoice.getPayments())
			ip.getPayment().updateAmountApplied();

		if (button != null) {
			String message = null;

			if ("Save".equals(button)) {
				edit = false;
				if (newFeeId > 0) {
					addInvoiceItem(newFeeId);
					newFeeId = 0;
					edit = true;
				} else {
					message = "Saved Invoice";
				}
				updateTotals();
				invoice.setQbSync(true);
			}
			if (button.startsWith("Change to")) {
				for (InvoiceItem item : invoice.getItems()) {
					if (item.getInvoiceFee().equals(contractor.getMembershipLevel())) {
						item.setInvoiceFee(contractor.getNewMembershipLevel());
						item.setAmount(contractor.getNewMembershipLevel().getAmount());
						item.setAuditColumns(permissions);
					}
				}

				contractor.setMembershipLevel(contractor.getNewMembershipLevel());
				addNote("Changed invoice " + invoice.getId() + " to " + contractor.getNewMembershipLevel().getFee());
				message = "Changed Membership Level";
			}

			if (button.startsWith("Email")) {
				try {
					EmailQueue email = EventSubscriptionBuilder
							.contractorInvoiceEvent(contractor, invoice, permissions);
					String note = "";
					if (invoice.getStatus().isPaid())
						note += "Payment Receipt for Invoice";
					else
						note += "Invoice";

					note += " emailed to " + email.getToAddresses();
					if (!Strings.isEmpty(email.getCcAddresses()))
						note += " and cc'd " + email.getCcAddresses();
					addNote(note);
					message = "Sent Email";

				} catch (Exception e) {
					message = "Sorry!! Failed to send email.";
				}
			}
			if (button.equalsIgnoreCase("Cancel")) {
				Iterator<PaymentAppliedToInvoice> paIterator = invoice.getPayments().iterator();
				if (paIterator.hasNext()) {
					PaymentAppliedToInvoice paymentAppliedToInvoice = paIterator.next();
					paymentDAO.removePaymentInvoice(paymentAppliedToInvoice, this.getUser());
				}
				invoice.setStatus(TransactionStatus.Void);
				invoice.setAuditColumns(permissions);
				invoice.setQbSync(true);
				invoice.setNotes("Cancelled Invoice");

				message = "Cancelled Invoice";

				String noteText = "Cancelled Invoice " + invoice.getId() + " for $"
						+ invoice.getTotalAmount().toString();
				addNote(noteText);
			}
			if (button.equals("pay")) {
				if (invoice != null && invoice.getTotalAmount().compareTo(BigDecimal.ZERO) > 0) {
					if (contractor.isCcValid()) {
						paymentService.setUserName(appPropDao.find("brainTree.username").getValue());
						paymentService.setPassword(appPropDao.find("brainTree.password").getValue());

						try {
							Payment payment = PaymentProcessor.PayOffInvoice(invoice, getUser(),
									PaymentMethod.CreditCard);

							paymentService.processPayment(payment);

							CreditCard creditCard = paymentService.getCreditCard(id);
							payment.setCcNumber(creditCard.getCardNumber());

							// Only if the transaction succeeds
							PaymentProcessor.ApplyPaymentToInvoice(payment, invoice, getUser(), payment
									.getTotalAmount());
							payment.setQbSync(true);

							paymentDAO.save(payment);
							invoice.updateAmountApplied();

							contractor.syncBalance();

							addNote("Credit Card transaction completed and emailed the receipt for $"
									+ invoice.getTotalAmount());
						} catch (Exception e) {
							addNote("Credit Card transaction failed: " + e.getMessage());
							this.addActionError("Failed to charge credit card. " + e.getMessage());
							return SUCCESS;
						}
					}

					// Send a receipt to the contractor
					try {
						EventSubscriptionBuilder.contractorInvoiceEvent(contractor, invoice, permissions);
					} catch (Exception theyJustDontGetAnEmail) {
					}
				}
			}

			invoiceDAO.save(invoice);
			this.redirect("InvoiceDetail.action?invoice.id=" + invoice.getId() + "&edit=" + edit
					+ (message == null ? "" : "&msg=" + message));

			return BLANK;
		}

		updateTotals();
		invoiceDAO.save(invoice);

		contractor.syncBalance();
		if (contractor.isActiveB() && contractor.getPaymentExpires().before(new Date())) {
			contractor.setActive('N');
			addNote("Automatically inactivating account based on expired membership");
		}
		contractor.setAuditColumns(permissions);
		accountDao.save(contractor);

		return SUCCESS;
	}

	private void updateTotals() {
		if (!invoice.getStatus().isPaid()) {
			invoice.setTotalAmount(BigDecimal.ZERO);
			for (InvoiceItem item : invoice.getItems())
				invoice.setTotalAmount(invoice.getTotalAmount().add(item.getAmount()));
		}
	}

	private void addNote(String subject) {
		Note note = new Note(invoice.getAccount(), getUser(), subject);
		note.setNoteCategory(NoteCategory.Billing);
		note.setCanContractorView(true);
		note.setViewableById(Account.PicsID);
		noteDAO.save(note);
	}

	private void addInvoiceItem(int feeId) {
		InvoiceItem newItem = new InvoiceItem();
		InvoiceFee newFee = invoiceFeeDAO.find(feeId);
		newItem.setInvoiceFee(newFee);
		newItem.setAmount(newFee.getAmount());
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
		if (feeList == null)
			feeList = invoiceFeeDAO.findAll();

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
		paymentService.setUserName(appPropDao.find("brainTree.username").getValue());
		paymentService.setPassword(appPropDao.find("brainTree.password").getValue());
		try {
			CreditCard creditCard = paymentService.getCreditCard(id);
			ccNumber = creditCard.getCardNumber();
		} catch (Exception e) {}
		return ccNumber;
	}
}
