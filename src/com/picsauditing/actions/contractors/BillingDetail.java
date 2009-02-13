package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.picsauditing.PICS.BillingCalculatorSingle;
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
		BillingCalculatorSingle bcs = new BillingCalculatorSingle();
		bcs.calculateAnnualFee(contractor);
		
		currentMemebershipFee = contractor.getMembershipLevel();
		newMembershipFee = contractor.getNewMembershipLevel();
		
		String billingStatus = "";
		billingStatus = contractor.getBillingStatus();
		
		invoiceItems.clear();
		
		// For Activation Fee and New Membership
		if ("Activation".equals(billingStatus)) {
			InvoiceFee fee = invoiceFeeDAO.find(InvoiceFee.ACTIVATION);
			
			if(newMembershipFee != null)
				invoiceItems.add(new InvoiceItem(newMembershipFee));
			
			invoiceItems.add(new InvoiceItem(fee));
		}
		
		// For Reactivation Fee and Reactivating Membership
		if ("Reactivation".equals(contractor.getBillingStatus())) {
			InvoiceFee fee = invoiceFeeDAO.find(InvoiceFee.REACTIVATION);
			
			if(newMembershipFee != null)
				invoiceItems.add(new InvoiceItem(newMembershipFee));
			
			invoiceItems.add(new InvoiceItem(fee));
		}
		
		// For Upgrades
		// Calculate a prorated amount depending on when the upgrade happens
		// and when the actual membership expires
		if ("Upgrade".equals(contractor.getBillingStatus())) {
			if (newMembershipFee != null && currentMemebershipFee != null) {
				double upgradeAmount = 0;
				String description = "";
				
				if (currentMemebershipFee.getAmount() == 0) {
					upgradeAmount = newMembershipFee.getAmount();
					description = "Upgrade from $"
						+ currentMemebershipFee.getAmount() + ". New Membership Level is: $ "
						+ upgradeAmount;
				}
				else {
					if (DateBean.getDateDifference(contractor.getPaymentExpires()) < 0) {
						upgradeAmount = newMembershipFee.getAmount() - currentMemebershipFee.getAmount();
						description = "Upgrade from $"
								+ currentMemebershipFee.getAmount()
								+ ". Uupgrade Amount $" + upgradeAmount;
					}
					else {
						double daysUntilExpiration = DateBean.getDateDifference(contractor.getPaymentExpires());
						double upgradeAmountDifference = newMembershipFee.getAmount() - currentMemebershipFee.getAmount();

						double proratedCalc = (double)(upgradeAmountDifference / 365);
						upgradeAmount = Math.round((daysUntilExpiration * proratedCalc));

						description = "Upgrade from $"
								+ currentMemebershipFee.getAmount()
								+ ". Prorated $" + (int)upgradeAmount;
					}
				}
					
				InvoiceItem invoiceItem = new InvoiceItem();
				invoiceItem.setAmount((int) upgradeAmount);
				invoiceItem.setDescription(description);
				invoiceItems.add(invoiceItem);
			}
		}
		
		// For Renewals
		if ("Renewal".equals(contractor.getBillingStatus())){
			if(currentMemebershipFee != null)
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
			
			int conBalance = contractor.getBalance();
			contractor.setBalance(conBalance + invoiceTotal);
			
			contractor.setMembershipLevel(contractor.getNewMembershipLevel());
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
