package com.picsauditing.util;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.auditBuilder.AuditBuilder;
import com.picsauditing.auditBuilder.AuditPercentCalculator;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorAuditOperatorPermission;
import com.picsauditing.jpa.entities.ContractorFee;
import com.picsauditing.jpa.entities.FeeClass;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.InvoiceItem;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;

@SuppressWarnings("serial")
public class CreateImportPQFAudit extends ContractorActionSupport {
	@Autowired
	private InvoiceDAO invoiceDAO;
	@Autowired
	private InvoiceFeeDAO invoiceFeeDAO;
	@Autowired
	private AuditTypeDAO auditTypeDAO;
	@Autowired
	private AuditBuilder auditBuilder;
	@Autowired
	private AuditPercentCalculator auditPercentCalculator;
	@Autowired
	private ContractorAuditOperatorDAO contractorAuditOperatorDAO;

	private boolean newRegistration = false;
	private String url;

	public String execute() throws Exception {
		this.findContractor();

		InvoiceFee fee = invoiceFeeDAO.findByNumberOfOperatorsAndClass(FeeClass.ImportFee, 0);
		int importAuditID = 0;

		if (Strings.isEmpty(url)) {
			for (ContractorAudit audit : contractor.getAudits()) {
				if (audit.getAuditType().isPqf()) {
					importAuditID = audit.getId();
					break;
				}
			}
		}

		// Did the contractor all ready pay for this?
		if (contractor.getCompetitorMembership() == null || contractor.getCompetitorMembership() == true) {
			boolean hasImportInvoice = false;

			for (Invoice invoice : contractor.getInvoices()) {
				for (InvoiceItem item : invoice.getItems()) {
					if (item.getInvoiceFee().getFeeClass().equals(FeeClass.ImportFee)) {
						hasImportInvoice = true;
						break;
					}
				}
			}

			if (!contractor.getFees().containsKey(fee.getFeeClass()) && !hasImportInvoice) {
				if (newRegistration) {
					ContractorFee newConFee = new ContractorFee();
					newConFee.setAuditColumns(permissions);
					newConFee.setContractor(contractor);
					newConFee.setCurrentAmount(fee.getAmount());
					newConFee.setNewAmount(fee.getAmount());
					newConFee.setCurrentLevel(fee);
					newConFee.setNewLevel(fee);
					newConFee.setFeeClass(fee.getFeeClass());
					invoiceFeeDAO.save(newConFee);

					contractor.getFees().put(fee.getFeeClass(), newConFee);
				} else {
					Invoice invoice = new Invoice();
					invoice.setAccount(contractor);
					invoice.setCurrency(contractor.getCurrency());
					invoice.setDueDate(new Date());
					invoice.setTotalAmount(fee.getAmount());
					invoice.setNotes("Thank you for doing business with PICS!");
					invoice.setAuditColumns(permissions);
					invoice.setQbSync(true);
					invoiceDAO.save(invoice);

					InvoiceItem item = new InvoiceItem(fee);
					item.setInvoice(invoice);
					item.setAuditColumns(permissions);
					invoiceFeeDAO.save(item);
					invoice.getItems().add(item);

					contractor.getInvoices().add(invoice);
				}

				contractor.syncBalance();
			}

			boolean hasImportPQFAudit = false;

			for (ContractorAudit audit : contractor.getAudits()) {
				if (audit.getAuditType().getId() == AuditType.IMPORT_PQF && !audit.isExpired()) {
					hasImportPQFAudit = true;
					importAuditID = audit.getId();
					break;
				}
			}

			if (!hasImportPQFAudit) {
				ContractorAudit importAudit = new ContractorAudit();
				importAudit.setAuditType(auditTypeDAO.find(AuditType.IMPORT_PQF));
				importAudit.setManuallyAdded(true);
				importAudit.setAuditColumns(permissions);
				importAudit.setContractorAccount(contractor);
				auditDao.save(importAudit);
				importAuditID = importAudit.getId();

				ContractorAuditOperator cao = new ContractorAuditOperator();
				cao.setAudit(importAudit);
				cao.setOperator(new OperatorAccount());
				cao.getOperator().setId(4);
				contractorAuditOperatorDAO.save(cao);
				ContractorAuditOperatorPermission caop = new ContractorAuditOperatorPermission();
				caop.setCao(cao);
				caop.setOperator(cao.getOperator());

				contractor.getAudits().add(importAudit);

				auditBuilder.buildAudits(contractor);
				auditPercentCalculator.percentCalculateComplete(importAudit);

				addNote(contractor, "Import PQF option selected.", NoteCategory.Audits,
						LowMedHigh.Med, true, Account.EVERYONE, new User(permissions.getUserId()));
			}

			contractor.setCompetitorMembership(true);
			accountDao.save(contractor);
		}

		this.redirect(Strings.isEmpty(url) ? String.format("Audit.action?auditID=%d", importAuditID) : url);
		return BLANK;
	}

	public boolean isNewRegistration() {
		return newRegistration;
	}

	public void setNewRegistration(boolean newRegistration) {
		this.newRegistration = newRegistration;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
