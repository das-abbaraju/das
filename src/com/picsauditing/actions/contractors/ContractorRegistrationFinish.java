package com.picsauditing.actions.contractors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.picsauditing.PICS.AuditBuilder;
import com.picsauditing.PICS.BillingCalculatorSingle;
import com.picsauditing.PICS.BrainTreeService;
import com.picsauditing.PICS.BrainTreeService.CreditCard;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.dao.InvoiceItemDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.InvoiceItem;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.TransactionStatus;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ContractorRegistrationFinish extends ContractorActionSupport {

	private InvoiceDAO invoiceDAO;
	private InvoiceFeeDAO invoiceFeeDAO;
	private AppPropertyDAO appPropDAO;
	private NoteDAO noteDAO;
	private InvoiceItemDAO invoiceItemDAO;
	private AuditBuilder auditBuilder;

	private BrainTreeService paymentService = new BrainTreeService();

	private Invoice invoice;
	private boolean complete = false;

	public ContractorRegistrationFinish(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			InvoiceDAO invoiceDAO, InvoiceFeeDAO invoiceFeeDAO, AppPropertyDAO appPropDAO, NoteDAO noteDAO,
			InvoiceItemDAO invoiceItemDAO, AuditBuilder auditBuilder) {
		super(accountDao, auditDao);
		this.invoiceDAO = invoiceDAO;
		this.invoiceFeeDAO = invoiceFeeDAO;
		this.appPropDAO = appPropDAO;
		this.noteDAO = noteDAO;
		this.invoiceItemDAO = invoiceItemDAO;
		this.auditBuilder = auditBuilder;
		subHeading = "Finish Registration";
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		findContractor();

		findUnpaidInvoice();

		auditBuilder.buildAudits(contractor);
		this.resetActiveAudits();

		if ("Complete My Registration".equals(button)) {
			// should never be possible
			if (invoice != null) {
				if (contractor.isCcOnFile()) {
					paymentService.setUserName(appPropDAO.find("brainTree.username").getValue());
					paymentService.setPassword(appPropDAO.find("brainTree.password").getValue());

					try {
						// TODO BEFORE RELEASE TREVOR!!!
						//paymentService.processPayment(invoice);

						CreditCard cc = paymentService.getCreditCard(id);
						// invoice.setCcNumber(cc.getCardNumber());

						payInvoice();

						contractor.syncBalance();

						addNote("Credit Card transaction completed and emailed the receipt for $"
								+ invoice.getTotalAmount());
					} catch (Exception e) {
						addNote("Credit Card transaction failed: " + e.getMessage());
						this.addActionError("Failed to charge credit card. " + e.getMessage());
						return SUCCESS;
					}
				}

				complete = true;

				// Send a receipt to the contractor
				try {
					emailInvoice();
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		} else if (!contractor.isActiveB()) {
			InvoiceFee newFee = BillingCalculatorSingle.calculateAnnualFee(contractor);
			newFee = invoiceFeeDAO.find(newFee.getId());
			contractor.setNewMembershipLevel(newFee);

			// There are no unpaid invoices - we should create a new one
			// (could be a re-activation)
			if (invoice == null) {
				invoice = new Invoice();
				invoice.setStatus(TransactionStatus.Unpaid);
				List<InvoiceItem> items = BillingCalculatorSingle.createInvoiceItems(contractor, invoiceFeeDAO);
				invoice.setItems(items);
				invoice.setAccount(contractor);
				invoice.setAuditColumns(new User(User.SYSTEM));
				invoice.setDueDate(new Date());

				for (InvoiceItem item : items) {
					item.setInvoice(invoice);
					item.setAuditColumns(new User(User.SYSTEM));
				}

				updateTotals();
				this.addNote(contractor, "Created invoice for $" + invoice.getTotalAmount(), NoteCategory.Billing,
						LowMedHigh.Med, false, Account.PicsID, new User(User.SYSTEM));

			} else {

				if (!contractor.getMembershipLevel().equals(contractor.getNewMembershipLevel())) {
					changeInvoiceItem(contractor.getMembershipLevel(), contractor.getNewMembershipLevel());
					updateTotals();
					this.addNote(contractor, "Modified current invoice, changed to $" + invoice.getTotalAmount(),
							NoteCategory.Billing, LowMedHigh.Med, false, Account.PicsID, new User(User.SYSTEM));

				}
			}

			if (invoice.getStatus().isUnpaid()) {

				if (invoice.getTotalAmount().compareTo(BigDecimal.ZERO) > 0)
					invoice.setQbSync(true);

				String notes = "Thank you for your business.";
				AppProperty prop = appPropDAO.find("invoice_comment");
				if (prop != null) {
					notes = prop.getValue();
				}
				invoice.setNotes(notes);

				invoice = invoiceDAO.save(invoice);

				if (!contractor.getInvoices().contains(invoice))
					contractor.getInvoices().add(invoice);

				contractor.syncBalance();
				accountDao.save(contractor);
			}
		}

		return SUCCESS;
	}

	private void findUnpaidInvoice() {
		// Get the first unpaid Invoice - this is mainly for re-activations.
		for (Invoice inv : contractor.getInvoices()) {
			if (inv.getStatus().isUnpaid()) {
				invoice = inv;
				break;
			}
		}
	}

	private void changeInvoiceItem(InvoiceFee currentFee, InvoiceFee newFee) {
		for (Iterator<InvoiceItem> iterator = invoice.getItems().iterator(); iterator.hasNext();) {
			InvoiceItem item = iterator.next();
			if (item.getInvoiceFee().getId() == currentFee.getId()) {
				iterator.remove();
				invoiceItemDAO.remove(item);
			}
		}

		InvoiceItem newInvoiceItem = new InvoiceItem();
		newInvoiceItem.setInvoiceFee(newFee);
		newInvoiceItem.setAmount(newFee.getAmount());
		newInvoiceItem.setAuditColumns(new User(User.SYSTEM));

		newInvoiceItem.setInvoice(invoice);
		invoice.getItems().add(newInvoiceItem);

		contractor.setMembershipLevel(newFee);
	}

	private void updateTotals() {
		if (!invoice.getStatus().isPaid()) {
			invoice.setTotalAmount(BigDecimal.ZERO);
			for (InvoiceItem item : invoice.getItems())
				invoice.setTotalAmount(invoice.getTotalAmount().add(item.getAmount()));
		}
	}

	private void payInvoice() {
		invoice.markPaid(getUser());
		invoiceDAO.save(invoice);

		if (!contractor.isActiveB()) {
			for (InvoiceItem item : invoice.getItems()) {
				if (item.getInvoiceFee().getFeeClass().equals("Membership")) {
					contractor.setActive('Y');
					contractor.setAuditColumns(getUser());
				}
			}
		}
	}

	private void addNote(String subject) {
		Note note = new Note(contractor, new User(User.SYSTEM), subject);
		note.setNoteCategory(NoteCategory.Billing);
		note.setCanContractorView(true);
		note.setViewableById(Account.PicsID);
		noteDAO.save(note);
	}

	private EmailQueue emailInvoice() throws Exception {
		EmailBuilder emailBuilder = new EmailBuilder();
		emailBuilder.setTemplate(45);
		emailBuilder.setPermissions(permissions);
		emailBuilder.setContractor(contractor);
		emailBuilder.addToken("invoice", invoice);
		emailBuilder.addToken("operators", getOperatorsString());
		emailBuilder.addToken("ccType", getCcType());
		emailBuilder.setFromAddress("billing@picsauditing.com");

		List<String> emailAddresses = new ArrayList<String>();

		if (contractor.getPaymentMethod().isCreditCard()) {
			if (!Strings.isEmpty(contractor.getCcEmail()))
				emailAddresses.add(contractor.getCcEmail());
		}
		if (!Strings.isEmpty(contractor.getBillingEmail()))
			emailAddresses.add(contractor.getBillingEmail());
		if (!Strings.isEmpty(contractor.getEmail())) {
			if (!emailAddresses.contains(contractor.getEmail()))
				emailAddresses.add(contractor.getEmail());
		}
		if (!Strings.isEmpty(contractor.getSecondEmail())) {
			if (!emailAddresses.contains(contractor.getSecondEmail()))
				emailAddresses.add(contractor.getSecondEmail());
		}

		emailBuilder.setToAddresses(emailAddresses.get(0));

		if (emailAddresses.size() > 1)
			emailBuilder.setCcAddresses(emailAddresses.get(1));

		emailBuilder.setBccAddresses("billing@picsauditing.com");

		EmailQueue email = emailBuilder.build();
		if (invoice.getStatus().isPaid())
			email.setSubject("PICS Payment Receipt for Invoice " + invoice.getId());
		email.setPriority(60);
		email.setHtml(true);
		EmailSender.send(email);
		return email;
	}

	public String getOperatorsString() {
		List<String> operatorsString = new ArrayList<String>();

		for (ContractorOperator co : contractor.getOperators()) {
			String doContractorsPay = co.getOperatorAccount().getDoContractorsPay();

			if (doContractorsPay.equals("Yes") || !doContractorsPay.equals("Multiple"))
				operatorsString.add(co.getOperatorAccount().getName());
		}

		Collections.sort(operatorsString);

		return "Your current list of Operators: " + Strings.implode(operatorsString, ", ");
	}

	public String getCcType() {
		BrainTreeService.CreditCard cc = new BrainTreeService.CreditCard();
		return cc.getCardType();
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public boolean isComplete() {
		return complete;
	}
}
