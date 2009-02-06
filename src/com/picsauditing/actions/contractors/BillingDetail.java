package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.dao.InvoiceItemDAO;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.InvoiceItem;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.SpringUtils;

@SuppressWarnings("serial")
public class BillingDetail extends ContractorActionSupport {
	private InvoiceFee currentMemebershipFee = null;
	private InvoiceFee newMembershipFee = null;
	private InvoiceFee activationFee = null;
	private InvoiceDAO invoiceDAO = new InvoiceDAO();
	private InvoiceItemDAO invoiceItemDAO = new InvoiceItemDAO();
	private InvoiceFeeDAO invoiceFeeDAO;
	private int invoiceTotal = 0;
	
	private List<InvoiceItem> invoiceItems = new ArrayList<InvoiceItem>();
	 
	private OperatorAccount requestedBy = null;

	public BillingDetail(ContractorAccountDAO accountDao,
			ContractorAuditDAO auditDao, InvoiceDAO invoiceDAO,
			InvoiceItemDAO invoiceItemDAO, InvoiceFeeDAO invoiceFeeDAO) {
		super(accountDao, auditDao);
		this.invoiceDAO = invoiceDAO;
		this.invoiceItemDAO = invoiceItemDAO;
		this.invoiceFeeDAO = invoiceFeeDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		this.findContractor();
		currentMemebershipFee = contractor.getMembershipLevel();
		newMembershipFee = contractor.getNewMembershipLevel();
		
		String billingStatus = contractor.getBillingStatus();
		boolean firstUpgrade = true;
		
		invoiceItems.clear();
		
		// For Activation Fee and New Membership
		if ("Activation".equals(billingStatus)) {
			InvoiceFee fee = invoiceFeeDAO.find(InvoiceFee.ACTIVATION); // create Activation fee
			
			invoiceItems.add(new InvoiceItem(newMembershipFee)); // add the Membership fee
			invoiceItems.add(new InvoiceItem(fee)); // add the Activation fee
		}
		
		// For Reactivation Fee and Reactivating Membership
		if ("Reactivation".equals(contractor.getBillingStatus())) {
			InvoiceFee fee = invoiceFeeDAO.find(InvoiceFee.REACTIVATION); // create Reactivation fee
			
			invoiceItems.add(new InvoiceItem(newMembershipFee)); // add the Membership fee
			invoiceItems.add(new InvoiceItem(fee)); // add the Reactivation fee
		}
		
		// For Upgrades
		if ("Upgrade".equals(contractor.getBillingStatus())) {
			int currAmt = currentMemebershipFee.getAmount();
			int newAmt = newMembershipFee.getAmount();
			int currId = currentMemebershipFee.getId();
			
			// Loop through currentMembershipFee and newMembershipFee to grab
			// Upgrade levels
			while (currAmt < newAmt){
				if (firstUpgrade) {					
					currId += 7;
					firstUpgrade = false;
				}
				else
					currId += 1;
				
				InvoiceFee fee = invoiceFeeDAO.find(currId);
				invoiceItems.add(new InvoiceItem(fee));
				
				currAmt += fee.getAmount();
			}
		}
		
		// For Renewals
		if ("Renewal".equals(contractor.getBillingStatus())){
			invoiceItems.add(new InvoiceItem(currentMemebershipFee));
		}

		invoiceTotal = 0;
		for (InvoiceItem item : invoiceItems)
			invoiceTotal += item.getAmount();
		
		if ("Create".equalsIgnoreCase(button)) {
			
			Invoice invoice = new Invoice();
			invoice.setAccount(contractor);
			invoice.setDueDate(DateBean.addDays(new Date(), Invoice.daysUntilDue));
			invoice.setPaid(false);
			invoice.setItems(invoiceItems);
			invoice.setAuditColumns(getUser());
			
			invoiceDAO.save(invoice);
			contractor.getInvoices().add(invoice);
			
			for (InvoiceItem item : invoiceItems) {
				item.setInvoice(invoice);
				item.setAuditColumns(getUser());
				invoiceItemDAO.save(item);
			}
			invoice.setTotalAmount(invoiceTotal);
			
			//contractor.setMembershipLevel(contractor.getNewMembershipLevel());
			//TODO contractor.updateBalance();
			accountDao.save(contractor);
			
			addActionMessage("Invoice " + invoice.getId() + " has been created.");
		}

		this.subHeading = "Billing Detail";
		return SUCCESS;
	}

	public OperatorAccount getRequestedBy() {
		AccountDAO dao = (AccountDAO) SpringUtils.getBean("AccountDAO");

		if (contractor.getRequestedById() != 0)
			requestedBy = (OperatorAccount) dao.find(contractor
					.getRequestedById());

		return requestedBy;
	}

	public InvoiceFee getCurrentMemebershipFee() {
		return currentMemebershipFee;
	}

	public void setCurrentMemebershipFee(InvoiceFee currentMemebershipFee) {
		this.currentMemebershipFee = currentMemebershipFee;
	}

	public InvoiceFee getNewMembershipFee() {
		return newMembershipFee;
	}

	public void setNewMembershipFee(InvoiceFee newMembershipFee) {
		this.newMembershipFee = newMembershipFee;
	}

	public InvoiceFee getActivationFee() {
		return activationFee;
	}

	public void setActivationFee(InvoiceFee activationFee) {
		this.activationFee = activationFee;
	}

	public List<InvoiceItem> getInvoiceItems() {
		return invoiceItems;
	}

	public void setInvoiceItems(List<InvoiceItem> invoiceItems) {
		this.invoiceItems = invoiceItems;
	}

	public int getInvoiceTotal() {
		return invoiceTotal;
	}


}
