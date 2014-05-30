package com.picsauditing.actions.report;

import com.intuit.developer.adaptors.*;
import com.picsauditing.PICS.BillingService;
import com.picsauditing.PICS.InvoiceValidationException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.dao.PaymentDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.report.service.ReportQBService;
import com.picsauditing.service.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@SuppressWarnings("serial")
public class ReportQBSyncList extends PicsActionSupport {
	@Autowired
	private BillingService billingService;
	@Autowired
	private ReportQBService reportQBService;
	@Autowired
	private EmailService emailService;
	@Autowired
	private ContractorAccountDAO contractorAccountDAO;
	@Autowired
	private InvoiceDAO invoiceDAO;
	@Autowired
	private PaymentDAO paymentDAO;

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

	// todo: Continue to move this stuff to ReportQBService
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
                billingService.applyFinancialCalculationsAndType(obj);
                billingService.verifyAndSaveInvoice(obj);
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

        contractorInsert = reportQBService.getContractorsToInsert(currency);

        invoiceInsert = reportQBService.getInvoicesToInsert(currency);

        paymentInsert = reportQBService.getPaymentsToInsert(currency);

        contractorUpdate = reportQBService.getContractorsForUpdate(currency);

        invoiceUpdate = reportQBService.getInvoicesForUpdate(currency);

        paymentUpdate = reportQBService.getPaymentsForUpdate(currency);

        lastError = emailService.getQuickbooksError();

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
