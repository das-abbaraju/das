package com.picsauditing.actions.report;

import java.util.List;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.dao.PaymentDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.Payment;

@SuppressWarnings("serial")
public class ReportQBSyncList extends PicsActionSupport {
	
	private int id = 0;
	private String type = null;
	
	private List<ContractorAccount> contractorInsert;
	private List<ContractorAccount> contractorUpdate;

	private List<Invoice> invoiceInsert;
	private List<Invoice> invoiceUpdate;

	private List<Payment> paymentInsert;
	private List<Payment> paymentUpdate;
	
	private EmailQueue lastError;

	private ContractorAccountDAO contractorAccountDAO;
	private InvoiceDAO invoiceDAO;
	private PaymentDAO paymentDAO;
	private EmailQueueDAO emailQueueDAO;
	
	public ReportQBSyncList(ContractorAccountDAO contractorAccountDAO, InvoiceDAO invoiceDAO, PaymentDAO paymentDAO, EmailQueueDAO emailQueueDAO) {
		this.contractorAccountDAO = contractorAccountDAO;
		this.invoiceDAO = invoiceDAO;
		this.paymentDAO = paymentDAO;
		this.emailQueueDAO = emailQueueDAO;
	}
	
	public String execute() throws NoRightsException {
		if (!forceLogin())
			return LOGIN;
		
		permissions.tryPermission(OpPerms.Billing);
		
		if (type != null && id > 0) {
			if (type.equals("C")) {
				ContractorAccount obj = contractorAccountDAO.find(id);
				obj.setQbSync(false);
				if (obj.getQbListID() == null)
					obj.setQbListID("NOLOAD" + id);
				contractorAccountDAO.save(obj);
			}
			
			if (type.equals("I")) {
				Invoice obj = invoiceDAO.find(id);
				obj.setQbSync(false);
				if (obj.getQbListID() == null)
					obj.setQbListID("NOLOAD" + id);
				invoiceDAO.save(obj);
			}
			
			if (type.equals("P")) {
				Payment obj = paymentDAO.find(id);
				obj.setQbSync(false);
				if (obj.getQbListID() == null)
					obj.setQbListID("NOLOAD" + id);
				paymentDAO.save(obj);
			}
			try {
				redirect("QBSyncList.action");
				return BLANK;
			} catch (Exception e) {
			}
		}
		
		// see InsertContractors
		contractorInsert = contractorAccountDAO.findWhere("a.qbSync = true and a.qbListID is null");
		
		// see InsertInvoices
		invoiceInsert = invoiceDAO.findWhere(
				"i.account.qbListID is not null AND i.status != 'Void' AND i.qbSync = true AND i.qbListID is null "
				+ "AND i.account.qbListID not like 'NOLOAD%'", 10);

		// see InsertPayments
		paymentInsert = paymentDAO.findWhere(
				"p.account.qbListID is not null AND p.status != 'Void' AND p.qbSync = true AND p.qbListID is null "
						+ "AND p.account.qbListID not like 'NOLOAD%'", 10);

		// see GetContractorsForUpdate
		contractorUpdate = contractorAccountDAO.findWhere("a.qbListID is not null and a.qbListID not like 'NOLOAD%' and a.qbSync = true");
		
		// see GetInvoicesForUpdate
		invoiceUpdate = invoiceDAO.findWhere(
				"i.account.qbListID is not null AND i.qbListID is not null AND i.qbListID not like 'NOLOAD%' AND i.qbSync = true", 10);

		// see GetPaymentsForUpdate
		paymentUpdate = paymentDAO.findWhere(
				"p.account.qbListID is not null AND p.qbListID is not null AND p.qbListID not like 'NOLOAD%' AND p.qbSync = true", 10);

		lastError = emailQueueDAO.getQuickbooksError();
		
		return SUCCESS;
	}

	public List<ContractorAccount> getContractorInsert() {
		return contractorInsert;
	}

	public List<ContractorAccount> getContractorUpdate() {
		return contractorUpdate;
	}

	public List<Invoice> getInvoiceInsert() {
		return invoiceInsert;
	}

	public List<Invoice> getInvoiceUpdate() {
		return invoiceUpdate;
	}

	public List<Payment> getPaymentInsert() {
		return paymentInsert;
	}

	public List<Payment> getPaymentUpdate() {
		return paymentUpdate;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setType(String type) {
		this.type = type;
	}

	public EmailQueue getLastError() {
		return lastError;
	}
	
	
}
