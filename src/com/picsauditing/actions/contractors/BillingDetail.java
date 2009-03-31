package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.struts2.ServletActionContext;

import com.picsauditing.PICS.BillingCalculatorSingle;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.InvoiceItem;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.SpringUtils;

@SuppressWarnings("serial")
public class BillingDetail extends ContractorActionSupport {
	private InvoiceFee activationFee = null;
	private InvoiceDAO invoiceDAO = new InvoiceDAO();
	private InvoiceFeeDAO invoiceFeeDAO;
	private int invoiceTotal = 0;

	private List<InvoiceItem> invoiceItems = new ArrayList<InvoiceItem>();

	private OperatorAccount requestedBy = null;
	
	AppPropertyDAO appPropDao;

	public BillingDetail(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, InvoiceDAO invoiceDAO,
			InvoiceFeeDAO invoiceFeeDAO, AppPropertyDAO appPropDao) {
		super(accountDao, auditDao);
		this.invoiceDAO = invoiceDAO;
		this.invoiceFeeDAO = invoiceFeeDAO;
		this.appPropDao = appPropDao;
		this.noteCategory = NoteCategory.Billing;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		this.findContractor();
		InvoiceFee newFee = BillingCalculatorSingle.calculateAnnualFee(contractor);
		newFee = invoiceFeeDAO.find(newFee.getId());
		contractor.setNewMembershipLevel(newFee);

		invoiceItems.clear();

		// For Activation Fee and New Membership
		if ("Activation".equals(contractor.getBillingStatus())) {
			if (contractor.getNewMembershipLevel().getId() != InvoiceFee.FREE
					&& contractor.getMembershipLevel().getId() == InvoiceFee.FREE) {
				InvoiceFee fee = invoiceFeeDAO.find(InvoiceFee.ACTIVATION);
	
				if (contractor.getNewMembershipLevel() != null)
					invoiceItems.add(new InvoiceItem(contractor.getNewMembershipLevel()));
	
				invoiceItems.add(new InvoiceItem(fee));
			}
		}

		// For Reactivation Fee and Reactivating Membership
		if ("Reactivation".equals(contractor.getBillingStatus())) {
			InvoiceFee fee = invoiceFeeDAO.find(InvoiceFee.REACTIVATION);

			if (contractor.getNewMembershipLevel() != null)
				invoiceItems.add(new InvoiceItem(contractor.getNewMembershipLevel()));

			invoiceItems.add(new InvoiceItem(fee));
		}

		// For Renewals
		if (contractor.getBillingStatus().startsWith("Renew")) {
			if (contractor.getMembershipLevel() != null)
				invoiceItems.add(new InvoiceItem(contractor.getMembershipLevel()));
		}

		// For Upgrades
		// Calculate a prorated amount depending on when the upgrade happens
		// and when the actual membership expires
		if ("Upgrade".equals(contractor.getBillingStatus())) {
			if (contractor.getNewMembershipLevel() != null && contractor.getMembershipLevel() != null) {
				int upgradeAmount = 0;
				String description = "";

				if (contractor.getMembershipLevel().getAmount() == 0) {
					// Free Membership Level
					upgradeAmount = contractor.getNewMembershipLevel().getAmount();
					description = "Membership Level is: $" + contractor.getNewMembershipLevel().getAmount();
					
				} else if (DateBean.getDateDifference(contractor.getPaymentExpires()) < 0) {
					// Their membership has already expired so we need to do a full renewal amount
					upgradeAmount = contractor.getNewMembershipLevel().getAmount();
					description = "Membership Level is: $" + contractor.getNewMembershipLevel().getAmount();
					
				} else {
					// Actual prorated Upgrade
					Date upgradeDate = (contractor.getLastUpgradeDate() == null) ? new Date() : contractor.getLastUpgradeDate();
					double daysUntilExpiration = DateBean.getDateDifference(upgradeDate, contractor.getPaymentExpires());
					double upgradeAmountDifference = contractor.getNewMembershipLevel().getAmount()
							- contractor.getMembershipLevel().getAmount();

					double proratedCalc = (double) (upgradeAmountDifference / 365);
					upgradeAmount = (int)Math.round(daysUntilExpiration * proratedCalc);

					description = "Upgrading from $" + contractor.getMembershipLevel().getAmount() + ". Prorated $"
							+ upgradeAmount;
				}

				InvoiceItem invoiceItem = new InvoiceItem();
				invoiceItem.setInvoiceFee(contractor.getNewMembershipLevel());
				invoiceItem.setAmount(upgradeAmount);
				invoiceItem.setDescription(description);
				invoiceItems.add(invoiceItem);
			}
		}

		invoiceTotal = 0;
		for (InvoiceItem item : invoiceItems)
			invoiceTotal += item.getAmount();

		if ("Create".equalsIgnoreCase(button)) {

			Invoice invoice = new Invoice();
			invoice.setAccount(contractor);
			invoice.setPaid(false);
			invoice.setItems(invoiceItems);
			invoice.setTotalAmount(invoiceTotal);
			invoice.setAuditColumns(getUser());
			
			if (contractor.getBillingStatus().equals("Activation") || contractor.getBillingStatus().equals("Reactivation")) {
				invoice.setDueDate(new Date());
			} else if (contractor.getBillingStatus().equals("Upgrade")) {
				invoice.setDueDate(DateBean.addDays(contractor.getLastUpgradeDate(), 30));
			} else if (contractor.getBillingStatus().startsWith("Renew")) {
				invoice.setDueDate(contractor.getPaymentExpires());
			} else {
				// For all other statuses like (Current)
				invoice.setDueDate(DateBean.addDays(new Date(), 30));
			}
			// Make sure the invoice isn't due within 7 days for active accounts
			if (contractor.isActiveB() && DateBean.getDateDifference(invoice.getDueDate()) < 7)
				invoice.setDueDate(DateBean.addDays(new Date(), 7));

			String notes = "Thank you for your business.";
			AppProperty prop = appPropDao.find("invoice_comment");
			if( prop != null ) {
				notes = prop.getValue();
			}
			invoice.setNotes(notes);
			
			contractor.getInvoices().add(invoice);

			boolean invoiceIncludesMembership = false;
			for (InvoiceItem item : invoiceItems) {
				item.setInvoice(invoice);
				item.setAuditColumns(getUser());
				if (item.getInvoiceFee().getFeeClass().equals("Membership"))
					invoiceIncludesMembership = true;
			}
			invoiceDAO.save(invoice);

			int conBalance = contractor.getBalance();
			contractor.setBalance(conBalance + invoiceTotal);
			if (invoiceIncludesMembership) {
				if (contractor.isActiveB()) {
					// Bump the paymentExpires one year
					if (contractor.getPaymentExpires() == null) {
						// This should never happen...but just in case
						contractor.setPaymentExpires(new Date());
					}
					Calendar cal = Calendar.getInstance();
					cal.setTime(contractor.getPaymentExpires());
					cal.add(Calendar.YEAR, 1);
					contractor.setPaymentExpires(cal.getTime());
				}
				contractor.setMembershipLevel(contractor.getNewMembershipLevel());
			}
			accountDao.save(contractor);

			ServletActionContext.getResponse().sendRedirect("InvoiceDetail.action?invoice.id=" + invoice.getId());
			return BLANK;
		}
		
		this.subHeading = "Billing Detail";
		return SUCCESS;
	}

	public OperatorAccount getRequestedBy() {
		AccountDAO dao = (AccountDAO) SpringUtils.getBean("AccountDAO");

		if (contractor.getRequestedById() != 0)
			requestedBy = (OperatorAccount) dao.find(contractor.getRequestedById());

		return requestedBy;
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
