package com.picsauditing.actions.contractors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.BillingCalculatorSingle;
import com.picsauditing.PICS.BrainTreeService;
import com.picsauditing.PICS.BrainTreeService.CreditCard;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.dao.InvoiceFeeDAO;
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

	private BrainTreeService paymentService = new BrainTreeService();

	private Invoice invoice;

	public ContractorRegistrationFinish(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			InvoiceDAO invoiceDAO, InvoiceFeeDAO invoiceFeeDAO, AppPropertyDAO appPropDAO, NoteDAO noteDAO) {
		super(accountDao, auditDao);
		this.invoiceDAO = invoiceDAO;
		this.invoiceFeeDAO = invoiceFeeDAO;
		this.appPropDAO = appPropDAO;
		this.noteDAO = noteDAO;
		subHeading = "Finish Registration";
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		findContractor();
		if (false) {
			InvoiceFee newFee = BillingCalculatorSingle.calculateAnnualFee(contractor);
			newFee = invoiceFeeDAO.find(newFee.getId());
			contractor.setNewMembershipLevel(newFee);

			List<InvoiceItem> invoiceItems = BillingCalculatorSingle.createInvoiceItems(contractor, invoiceFeeDAO);

			BigDecimal invoiceTotal = new BigDecimal(0);
			for (InvoiceItem item : invoiceItems)
				invoiceTotal = invoiceTotal.add(item.getAmount());

			if (contractor.getInvoices().size() == 0) {
				invoice = new Invoice();
			} else {
				invoice = contractor.getInvoices().get(0);
			}

			invoice.setAccount(contractor);
			invoice.setPaid(false);
			invoice.setItems(invoiceItems);
			invoice.setTotalAmount(invoiceTotal);
			invoice.setAuditColumns(new User(User.SYSTEM));

			if (invoiceTotal.compareTo(BigDecimal.ZERO) > 0)
				invoice.setQbSync(true);

			invoice.setDueDate(new Date());

			String notes = "Thank you for your business.";
			AppProperty prop = appPropDAO.find("invoice_comment");
			if (prop != null) {
				notes = prop.getValue();
			}
			invoice.setNotes(notes);

			contractor.getInvoices().add(invoice);

			for (InvoiceItem item : invoiceItems) {
				item.setInvoice(invoice);
				item.setAuditColumns(getUser());
			}
			invoice = invoiceDAO.save(invoice);

			if (!contractor.getInvoices().contains(invoiceTotal))
				contractor.getInvoices().add(invoice);

			contractor.syncBalance();
			accountDao.save(contractor);

			this.addNote(contractor, "Created invoice for $" + invoiceTotal, NoteCategory.Billing, LowMedHigh.Med,
					false, Account.PicsID, new User(User.SYSTEM));

			if (button != null) {
				if (button.startsWith("Charge") && contractor.isCcOnFile()) {
					paymentService.setUserName(appPropDAO.find("brainTree.username").getValue());
					paymentService.setPassword(appPropDAO.find("brainTree.password").getValue());

					try {
						paymentService.processPayment(invoice);

						CreditCard cc = paymentService.getCreditCard(id);
						invoice.setCcNumber(cc.getCardNumber());

						payInvoice();

						contractor.setActive('Y');
						addNote("Credit Card transaction completed and emailed the receipt for $"
								+ invoice.getTotalAmount());
					} catch (Exception e) {
						addNote("Credit Card transaction failed: " + e.getMessage());
						this.addActionError("Failed to charge credit card. " + e.getMessage());
						return SUCCESS;
					}
				}
			}
		}
		return SUCCESS;
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

		// Send a receipt to the contractor
		try {
			emailInvoice();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void addNote(String subject) {
		Note note = new Note(invoice.getAccount(), getUser(), subject);
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
		if (invoice.isPaid())
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
		cc.setCardNumber(invoice.getCcNumber());
		return cc.getCardType();
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

}
