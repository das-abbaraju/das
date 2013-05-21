package com.picsauditing.actions.report;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.intuit.developer.adaptors.GetContractorsForUpdate;
import com.intuit.developer.adaptors.GetInvoicesForUpdate;
import com.intuit.developer.adaptors.GetPaymentsForUpdate;
import com.intuit.developer.adaptors.InsertContractors;
import com.intuit.developer.adaptors.InsertInvoices;
import com.intuit.developer.adaptors.InsertPayments;
import com.picsauditing.PICS.InvoiceService;
import com.picsauditing.PICS.InvoiceValidationException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.dao.PaymentDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Currency;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.Payment;

@SuppressWarnings("serial")
public class ReportQBSyncList extends PicsActionSupport {
	@Autowired
	private InvoiceService invoiceService;
	@Autowired
	private ContractorAccountDAO contractorAccountDAO;
	@Autowired
	private InvoiceDAO invoiceDAO;
	@Autowired
	private PaymentDAO paymentDAO;
	@Autowired
	private EmailQueueDAO emailQueueDAO;

	private int id = 0;
	private String type = null;
	private Currency currency = Currency.USD;

	private List<ContractorAccount> contractorInsert;
	private List<ContractorAccount> contractorUpdate;

	private List<Invoice> invoiceInsert;
	private List<Invoice> invoiceUpdate;

	private List<Payment> paymentInsert;
	private List<Payment> paymentUpdate;

	private EmailQueue lastError;

	public String execute() throws Exception, InvoiceValidationException {
		if (!forceLogin()) {
			return LOGIN;
		}

		permissions.tryPermission(OpPerms.Billing);

		if (type != null && id > 0) {
			if (type.equals("C")) {
				ContractorAccount contractor = contractorAccountDAO.find(id);
				setQBData(contractor, currency);
				contractorAccountDAO.save(contractor);
			}

			if (type.equals("I")) {
				Invoice obj = invoiceDAO.find(id);
				obj.setQbSync(false);
				if (obj.getQbListID() == null) {
					obj.setQbListID("NOLOAD" + id);
				}
				invoiceService.saveInvoice(obj);
			}

			if (type.equals("P")) {
				Payment obj = paymentDAO.find(id);
				obj.setQbSync(false);
				if (obj.getQbListID() == null) {
					obj.setQbListID("NOLOAD" + id);
				}
				paymentDAO.save(obj);
			}
		}

		// see InsertContractors
		contractorInsert = contractorAccountDAO.findWhere(InsertContractors.getWhereClause(getQBListID(currency),
				currency.toString()));

		// see InsertInvoices
		invoiceInsert = invoiceDAO.findWhere(InsertInvoices.getWhereClause(getQBListID(currency), currency.toString()),
				10);

		// see InsertPayments
		paymentInsert = paymentDAO.findWhere(InsertPayments.getWhereClause(getQBListID(currency), currency.toString()),
				10);

		// see GetContractorsForUpdate
		contractorUpdate = contractorAccountDAO.findWhere(GetContractorsForUpdate.getWhereClause(getQBListID(currency),
				currency.toString()));

		// see GetInvoicesForUpdate
		invoiceUpdate = invoiceDAO.findWhere(
				GetInvoicesForUpdate.getWhereClause(getQBListID(currency), currency.toString()), 10);

		// see GetPaymentsForUpdate
		paymentUpdate = paymentDAO.findWhere(
				GetPaymentsForUpdate.getWhereClause(getQBListID(currency), currency.toString()), 10);

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

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public Currency getCurrency() {
		return currency;
	}

	public EmailQueue getLastError() {
		return lastError;
	}

	/**
	 * TODO: Need to fix how we store qbListIDs and break them into another
	 * table or store them more intelligently.
	 *
	 * @param currency
	 * @return
	 */
	private String getQBListID(Currency currency) {
		switch (currency) {
		case CAD:
			return "qbListCAID";

		case GBP:
			return "qbListUKID";

		case EUR:
			return "qbListEUID";

		default:
			return "qbListID";
		}
	}

	private void setQBData(ContractorAccount contractor, Currency currency) {
		contractor.setQbSync(false);
		switch (currency) {
		case CAD:
			if (contractor.getQbListCAID() == null) {
				contractor.setQbListCAID("NOLOAD" + id);
			}
			break;

		case GBP:
			if (contractor.getQbListUKID() == null) {
				contractor.setQbListUKID("NOLOAD" + id);
			}
			break;

		case EUR:
			if (contractor.getQbListEUID() == null) {
				contractor.setQbListEUID("NOLOAD" + id);
			}
			break;

		default:
			if (contractor.getQbListID() == null) {
				contractor.setQbListID("NOLOAD" + id);
			}
			break;
		}
	}
}
