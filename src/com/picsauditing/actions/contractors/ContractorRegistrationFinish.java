package com.picsauditing.actions.contractors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import com.picsauditing.jpa.entities.ContractorAudit;
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
	private InvoiceItemDAO invoiceItemDAO;

	private BrainTreeService paymentService = new BrainTreeService();

	private Invoice invoice;
	private Map<Integer, List<ContractorAudit>> auditMapList;
	private boolean complete = false;

	public ContractorRegistrationFinish(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			InvoiceDAO invoiceDAO, InvoiceFeeDAO invoiceFeeDAO, AppPropertyDAO appPropDAO, NoteDAO noteDAO,
			InvoiceItemDAO invoiceItemDAO) {
		super(accountDao, auditDao);
		this.invoiceDAO = invoiceDAO;
		this.invoiceFeeDAO = invoiceFeeDAO;
		this.appPropDAO = appPropDAO;
		this.noteDAO = noteDAO;
		this.invoiceItemDAO = invoiceItemDAO;
		subHeading = "Finish Registration";
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		findContractor();

		if ("Complete Registration".equals(button)) {
			if (contractor.isCcOnFile()) {
				paymentService.setUserName(appPropDAO.find("brainTree.username").getValue());
				paymentService.setPassword(appPropDAO.find("brainTree.password").getValue());

				try {
					paymentService.processPayment(invoice);

					CreditCard cc = paymentService.getCreditCard(id);
					invoice.setCcNumber(cc.getCardNumber());

					payInvoice();

					addNote("Credit Card transaction completed and emailed the receipt for $"
							+ invoice.getTotalAmount());
				} catch (Exception e) {
					addNote("Credit Card transaction failed: " + e.getMessage());
					this.addActionError("Failed to charge credit card. " + e.getMessage());
					return SUCCESS;
				}
			}

			if (contractor.getInvoices().size() == 1) {
				invoice = contractor.getInvoices().get(0);
			}
			complete = true;
			
			// Send a receipt to the contractor
			try {
				emailInvoice();
			} catch (Exception e) {
				// TODO: handle exception
			}

		} else if (!contractor.isActiveB()) {
			InvoiceFee newFee = BillingCalculatorSingle.calculateAnnualFee(contractor);
			newFee = invoiceFeeDAO.find(newFee.getId());
			contractor.setNewMembershipLevel(newFee);

			if (contractor.getInvoices().size() == 0) {
				invoice = new Invoice();
				invoice.setPaid(false);
				List<InvoiceItem> items = BillingCalculatorSingle.createInvoiceItems(contractor, invoiceFeeDAO);
				invoice.setItems(items);

				for (InvoiceItem item : items) {
					item.setInvoice(invoice);
					item.setAuditColumns(new User(User.SYSTEM));
				}
			} else {
				invoice = contractor.getInvoices().get(0);
				if (!contractor.getMembershipLevel().equals(contractor.getNewMembershipLevel())) {
					changeInvoiceItem(contractor.getMembershipLevel(), contractor.getNewMembershipLevel());
				}
			}

			if (!invoice.isPaid()) {
				invoice.setAccount(contractor);
				invoice.setAuditColumns(new User(User.SYSTEM));

				updateTotals();
				if (invoice.getTotalAmount().compareTo(BigDecimal.ZERO) > 0)
					invoice.setQbSync(true);

				invoice.setDueDate(new Date());

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
				contractor.setMembershipDate(null);
				accountDao.save(contractor);

				this.addNote(contractor, "Created invoice for $" + invoice.getTotalAmount(), NoteCategory.Billing,
						LowMedHigh.Med, false, Account.PicsID, new User(User.SYSTEM));
			}
		}

		return SUCCESS;
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
		newInvoiceItem.setAuditColumns(getUser());

		newInvoiceItem.setInvoice(invoice);
		invoice.getItems().add(newInvoiceItem);

		contractor.setMembershipLevel(newFee);
	}

	private void updateTotals() {
		if (!invoice.isPaid()) {
			invoice.setTotalAmount(BigDecimal.ZERO);
			for (InvoiceItem item : invoice.getItems())
				invoice.setTotalAmount(invoice.getTotalAmount().add(item.getAmount()));
			invoice.setPaymentMethod(contractor.getPaymentMethod());
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

	public Map<Integer, List<ContractorAudit>> getAuditListMap() {
		if (auditMapList == null) {
			final int NUM_LISTS = 2;
			auditMapList = new HashMap<Integer, List<ContractorAudit>>();

			for (int i = 0; i < NUM_LISTS; i++)
				auditMapList.put(i, new ArrayList<ContractorAudit>());

			int index = 0;
			int map = 0;
			int perCol = contractor.getAudits().size() / NUM_LISTS;
			for (ContractorAudit ca : contractor.getAudits()) {
				if (index++ >= perCol) {
					map++;
					index = 0;
				}
				auditMapList.get(map).add(ca);
			}
		}

		return auditMapList;
	}

	public boolean isComplete() {
		return complete;
	}
}
