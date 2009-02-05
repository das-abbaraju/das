package com.picsauditing.actions.contractors;

import java.util.List;

import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.InvoiceDAO;
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
	 
	private OperatorAccount requestedBy = null;

	public BillingDetail(ContractorAccountDAO accountDao,
			ContractorAuditDAO auditDao) {
		super(accountDao, auditDao);
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		this.findContractor();
		currentMemebershipFee = contractor.getMembershipLevel();
		newMembershipFee = contractor.getNewMembershipLevel();
		
		this.subHeading = "Billing Detail";		
		
		if (button != null) {
			if (button.equalsIgnoreCase("Create")) {
				List<Invoice> contractorInvoices = contractor.getInvoices();
				Invoice invoice = new Invoice();
				InvoiceItem invoiceItem = new InvoiceItem();
				InvoiceDAO invoiceDAO = new InvoiceDAO();
				InvoiceItemDAO invoiceItemDAO = new InvoiceItemDAO();
				
				contractor.setNewMembershipLevel(newMembershipFee);
				invoiceItem.setInvoiceFee(newMembershipFee);
				
				contractorInvoices.add(invoice);
				
				invoiceItemDAO.save(invoiceItem);
				invoiceDAO.save(invoice);
				accountDao.save(contractor);
				addActionMessage("Invoice " + invoice.getId() + " has been created.");
			}
			else{
				// Because there are anomalies between browsers and how they
				// pass in the button values, this is a catch all so we can get
				// notified when the button name isn't set correctly
				throw new Exception("no button action found called " + button);
			}
		}

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
}
