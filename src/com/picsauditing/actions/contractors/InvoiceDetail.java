package com.picsauditing.actions.contractors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.BrainTreeService;
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
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.InvoiceItem;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class InvoiceDetail extends PicsActionSupport implements Preparable {
	private int id; //accountID
	private boolean edit = false;
	
	private InvoiceDAO invoiceDAO;
	private InvoiceFeeDAO invoiceFeeDAO;
	private InvoiceItemDAO invoiceItemDAO;
	private NoteDAO noteDAO;
	private ContractorAccountDAO conAccountDAO;
	
	private int newFeeId;
	
	private Invoice invoice;
	private ContractorAccount contractor;
	
	private List<InvoiceFee> feeList = null;
	
	AppPropertyDAO appPropDao;
	
	public InvoiceDetail(InvoiceDAO invoiceDAO, AppPropertyDAO appPropDao,
			NoteDAO noteDAO,
			ContractorAccountDAO conAccountDAO, InvoiceFeeDAO invoiceFeeDAO,
			InvoiceItemDAO invoiceItemDAO) {
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
					
					//invoiceDAO.save(invoice);
				}
			} else {
				if (button.startsWith("Charge Credit Card") && contractor.isCcOnFile()) {
					BrainTreeService paymentService = new BrainTreeService();
					paymentService.setUserName(appPropDao.find("brainTree.username").getValue());
					paymentService.setPassword(appPropDao.find("brainTree.password").getValue());
					
					try {
						paymentService.processPayment(invoice);
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
				ServletActionContext.getResponse().sendRedirect("InvoiceDetail.action?invoice.id=" + invoice.getId());
			}
		}
		
		updateTotals();
		
		return SUCCESS;
	}
	
	private void updateTotals() {
		if (!invoice.isPaid()) {
			int total = 0;
			for(InvoiceItem item : invoice.getItems())
				total += item.getAmount();
			invoice.setTotalAmount(total);
			invoice.setPaymentMethod(contractor.getPaymentMethod());
		}
		
		int balance = 0;
		for(Invoice conInvoice : contractor.getInvoices())
			if (!conInvoice.isPaid())
				balance += conInvoice.getTotalAmount();
		contractor.setBalance(balance);
	}
	private void payInvoice() {
		invoice.setPaid(true);
		invoice.setPaidDate(new Date());
		invoice.setAuditColumns(getUser());
		invoiceDAO.save(invoice);
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
		
		invoiceItemDAO.save(newItem);
		
		invoice.getItems().add(newItem);
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
