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
import com.picsauditing.dao.InvoiceItemDAO;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.InvoiceItem;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.SpringUtils;

@SuppressWarnings("serial")
public class BillingDetail extends ContractorActionSupport {
	private InvoiceFee activationFee = null;
	private InvoiceDAO invoiceDAO = new InvoiceDAO();
	private InvoiceItemDAO invoiceItemDAO = new InvoiceItemDAO();
	private InvoiceFeeDAO invoiceFeeDAO;
	private int invoiceTotal = 0;

	private List<InvoiceItem> invoiceItems = new ArrayList<InvoiceItem>();

	private OperatorAccount requestedBy = null;
	
	AppPropertyDAO appPropDao;

	public BillingDetail(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, InvoiceDAO invoiceDAO,
			InvoiceItemDAO invoiceItemDAO, InvoiceFeeDAO invoiceFeeDAO, AppPropertyDAO appPropDao) {
		super(accountDao, auditDao);
		this.invoiceDAO = invoiceDAO;
		this.invoiceItemDAO = invoiceItemDAO;
		this.invoiceFeeDAO = invoiceFeeDAO;
		this.appPropDao = appPropDao;
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
			InvoiceFee fee = invoiceFeeDAO.find(InvoiceFee.ACTIVATION);

			if (contractor.getNewMembershipLevel() != null)
				invoiceItems.add(new InvoiceItem(contractor.getNewMembershipLevel()));

			invoiceItems.add(new InvoiceItem(fee));
		}

		// For Reactivation Fee and Reactivating Membership
		if ("Reactivation".equals(contractor.getBillingStatus())) {
			InvoiceFee fee = invoiceFeeDAO.find(InvoiceFee.REACTIVATION);

			if (contractor.getNewMembershipLevel() != null)
				invoiceItems.add(new InvoiceItem(contractor.getNewMembershipLevel()));

			invoiceItems.add(new InvoiceItem(fee));
		}

		// For Renewals
		if ("Renewal".equals(contractor.getBillingStatus())) {
			if (contractor.getMembershipLevel() != null)
				invoiceItems.add(new InvoiceItem(contractor.getMembershipLevel()));
		}

		// For Upgrades
		// Calculate a prorated amount depending on when the upgrade happens
		// and when the actual membership expires
		if ("Upgrade".equals(contractor.getBillingStatus())) {
			if (contractor.getNewMembershipLevel() != null && contractor.getMembershipLevel() != null) {
				double upgradeAmount = 0;
				String description = "";

				if (contractor.getMembershipLevel().getAmount() == 0) {
					upgradeAmount = contractor.getNewMembershipLevel().getAmount();
					description = "Membership Level is: $" + contractor.getNewMembershipLevel().getAmount();

				} else if (DateBean.getDateDifference(contractor.getPaymentExpires()) < 0) {
					upgradeAmount = contractor.getNewMembershipLevel().getAmount();
					description = "Membership Level is: $" + contractor.getNewMembershipLevel().getAmount();
				} else {
					double daysUntilExpiration = DateBean.getDateDifference(contractor.getPaymentExpires());
					double upgradeAmountDifference = contractor.getNewMembershipLevel().getAmount()
							- contractor.getMembershipLevel().getAmount();

					double proratedCalc = (double) (upgradeAmountDifference / 365);
					upgradeAmount = Math.round((daysUntilExpiration * proratedCalc));

					description = "Upgrading from $" + contractor.getMembershipLevel().getAmount() + ". Prorated $"
							+ (int) upgradeAmount;
				}

				InvoiceItem invoiceItem = new InvoiceItem();
				invoiceItem.setInvoiceFee(contractor.getNewMembershipLevel());
				invoiceItem.setAmount((int) upgradeAmount);
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
			invoice.setDueDate(DateBean.addDays(new Date(), Invoice.daysUntilDue));
			invoice.setPaid(false);
			invoice.setItems(invoiceItems);
			invoice.setTotalAmount(invoiceTotal);
			invoice.setAuditColumns(getUser());

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

			ServletActionContext.getResponse().sendRedirect("BillingDetail.action?id=" + id);
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
