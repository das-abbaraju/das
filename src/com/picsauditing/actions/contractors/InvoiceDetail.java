package com.picsauditing.actions.contractors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.BillingCalculatorSingle;
import com.picsauditing.PICS.BrainTreeService;
import com.picsauditing.PICS.BrainTreeService.CreditCard;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.dao.InvoiceItemDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.InvoiceItem;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class InvoiceDetail extends PicsActionSupport implements Preparable {
	private int id; // accountID
	private boolean edit = false;

	private InvoiceDAO invoiceDAO;
	private InvoiceFeeDAO invoiceFeeDAO;
	private InvoiceItemDAO invoiceItemDAO;
	private NoteDAO noteDAO;
	private ContractorAccountDAO conAccountDAO;

	private int newFeeId;
	private BrainTreeService paymentService = new BrainTreeService();

	private Invoice invoice;
	private ContractorAccount contractor;

	private List<InvoiceFee> feeList = null;

	AppPropertyDAO appPropDao;

	public InvoiceDetail(InvoiceDAO invoiceDAO, AppPropertyDAO appPropDao, NoteDAO noteDAO,
			ContractorAccountDAO conAccountDAO, InvoiceFeeDAO invoiceFeeDAO, InvoiceItemDAO invoiceItemDAO) {
		this.invoiceDAO = invoiceDAO;
		this.appPropDao = appPropDao;
		this.noteDAO = noteDAO;
		this.conAccountDAO = conAccountDAO;
		this.invoiceFeeDAO = invoiceFeeDAO;
		this.invoiceItemDAO = invoiceItemDAO;
	}

	@Override
	public void prepare() {
		int invoiceId = getParameter("invoice.id");
		invoice = invoiceDAO.find(invoiceId);
		id = invoice.getAccount().getId();
		contractor = (ContractorAccount) invoice.getAccount();
	}

	public String execute() throws NoRightsException, IOException {
		if (!forceLogin())
			return LOGIN;

		if (!permissions.hasPermission(OpPerms.AllContractors)
				&& permissions.getAccountId() != invoice.getAccount().getId()) {
			throw new NoRightsException("You can't view this invoice");
		}

		if (button != null) {
			if (edit) {
				if ("Save".equals(button)) {
					edit = false;
					if (newFeeId > 0) {
						addInvoiceItem(newFeeId);
						newFeeId = 0;
					}
					updateTotals();
				}

				if (button.startsWith("Change to")) {
					changeInvoiceItem(contractor.getMembershipLevel(), contractor.getNewMembershipLevel());
					addNote("Changed invoice " + invoice.getId() + " to " + contractor.getMembershipLevel().getFee());
				}

			} else {
				if (button.startsWith("Charge Credit Card") && contractor.isCcOnFile()) {
					paymentService.setUserName(appPropDao.find("brainTree.username").getValue());
					paymentService.setPassword(appPropDao.find("brainTree.password").getValue());

					try {
						paymentService.processPayment(invoice);

						CreditCard cc = paymentService.getCreditCard(contractor.getId());
						invoice.setCcNumber(cc.getCardNumber());

						payInvoice();
						addNote("Credit Card transaction completed for $" + invoice.getTotalAmount());
					} catch (Exception e) {
						this.addActionError("Failed to charge credit card. " + e.getMessage());
						return SUCCESS;
					}
				}
				if (button.startsWith("Collect Check")) {
					payInvoice();
					addNote("Received check for $" + invoice.getTotalAmount());
				}
				if (button.startsWith("Mark Paid")) {
					markInvoicePaid();
					addNote("Marked invoice paid with amount " + invoice.getTotalAmount() + ". No payment");
				}
				if (button.startsWith("Email")) {
					try {
						EmailQueue email = emailInvoice();
						String note = "Invoice emailed to " + email.getToAddresses();
						if (Strings.isEmpty(email.getCcAddresses()))
							note += "and cc'd " + email.getCcAddresses();
						addNote(note);
						
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
				if(button.startsWith("Cancel Invoice")) {
					boolean invoiceIncludesMembership = false;
					boolean invoiceIncludesFullMembership = false;
					Iterator<InvoiceItem> inIterator = invoice.getItems().iterator();

					while (inIterator.hasNext()) {
						InvoiceItem invoiceItem = inIterator.next();
						//if (invoiceItem.getInvoiceFee().getFeeClass().equals("Membership")) {
						//	invoiceIncludesMembership = true;
						//	invoiceIncludesFullMembership = (invoiceItem.getAmount() == invoiceItem.getInvoiceFee().getAmount());
						//}
						inIterator.remove();
						invoiceItemDAO.remove(invoiceItem);
					}

					invoice.setTotalAmount(0);
					invoice.setPaid(true);
					invoice.setPaidDate(new Date());
					invoice.setAuditColumns(permissions);
					//invoice.setQbSync(true);
					invoice.setNotes("Cancelled Invoice");

					invoiceDAO.save(invoice);

					BillingCalculatorSingle billingCalculatorSingle = new BillingCalculatorSingle();
					contractor = billingCalculatorSingle.calculateCurrentBalance(contractor, invoiceIncludesMembership, invoiceIncludesFullMembership, -1);
					contractor.setAuditColumns(permissions);
					conAccountDAO.save(contractor);

					Note note = new Note();
					note.setAccount(invoice.getAccount());
					note.setAuditColumns(this.getUser());
					note.setSummary("Cancelled Invoice");
					note.setNoteCategory(NoteCategory.Billing);
					note.setViewableById(Account.PicsID);
					noteDAO.save(note);
				}
			}
			ServletActionContext.getResponse().sendRedirect("InvoiceDetail.action?invoice.id=" + invoice.getId());
		}

		updateTotals();
		invoiceDAO.save(invoice);
		conAccountDAO.save(contractor);
		return SUCCESS;
	}

	private EmailQueue emailInvoice() throws Exception {
		EmailBuilder emailBuilder = new EmailBuilder();
		emailBuilder.setTemplate(45);
		emailBuilder.setPermissions(permissions);
		emailBuilder.setContractor(contractor);
		emailBuilder.addToken("invoice", invoice);
		emailBuilder.addToken("operators", getOperators());
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

	private void updateTotals() {
		if (!invoice.isPaid()) {
			int total = 0;
			for (InvoiceItem item : invoice.getItems())
				total += item.getAmount();
			invoice.setTotalAmount(total);
			invoice.setPaymentMethod(contractor.getPaymentMethod());
		}

		int balance = 0;
		for (Invoice conInvoice : contractor.getInvoices())
			if (!conInvoice.isPaid())
				balance += conInvoice.getTotalAmount();
		contractor.setBalance(balance);
		if (balance <= 0) {
			// This contractor is fully paid up

		}
	}

	private void markInvoicePaid() {
		invoice.setPaid(true);
		invoice.setPaidDate(new Date());
		invoice.setAuditColumns(getUser());
		invoiceDAO.save(invoice);

		updateTotals();
	}

	private void payInvoice() {
		markInvoicePaid();

		if (contractor.getMembershipDate() == null) {
			for (InvoiceItem item : invoice.getItems()) {
				if (item.getInvoiceFee().getFeeClass().equals("Activation")) {
					contractor.setMembershipDate(new Date());
					contractor.setAuditColumns(getUser());
				}
			}
		}

		if (!contractor.isActiveB()) {
			for (InvoiceItem item : invoice.getItems()) {
				if (item.getInvoiceFee().getFeeClass().equals("Membership")) {
					contractor.setActive('Y');
					Calendar cal = Calendar.getInstance();
					cal.add(Calendar.YEAR, 1);
					contractor.setPaymentExpires(cal.getTime());
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

		updateTotals();
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
		newItem.setAuditColumns(getUser());

		invoice.getItems().add(newItem);
	}

	private void changeInvoiceItem(InvoiceFee currentFee, InvoiceFee newFee) {
		for (Iterator<InvoiceItem> iterator = invoice.getItems().iterator(); iterator.hasNext();) {
			InvoiceItem item = iterator.next();
			if (item.getInvoiceFee().getId() == currentFee.getId()) {
				iterator.remove();
				invoiceItemDAO.remove(item);
			}
		}

		InvoiceItem thisIsTheNewItemThatWeArePuttingOnTheInvoice = new InvoiceItem();
		thisIsTheNewItemThatWeArePuttingOnTheInvoice.setInvoiceFee(newFee);
		thisIsTheNewItemThatWeArePuttingOnTheInvoice.setAmount(newFee.getAmount());
		thisIsTheNewItemThatWeArePuttingOnTheInvoice.setAuditColumns(getUser());

		thisIsTheNewItemThatWeArePuttingOnTheInvoice.setInvoice(invoice);
		invoice.getItems().add(thisIsTheNewItemThatWeArePuttingOnTheInvoice);

		contractor.setMembershipLevel(newFee);
	}

	public String getOperators() {
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ContractorAccount getContractor() {
		return contractor;
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

}
