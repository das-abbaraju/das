package com.picsauditing.util;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.auditBuilder.AuditBuilder;
import com.picsauditing.auditBuilder.AuditPercentCalculator;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorFee;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.FeeClass;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.InvoiceItem;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.EventSubscriptionBuilder;

@SuppressWarnings("serial")
public class CreateImportPQFAudit extends ContractorActionSupport {
	@Autowired
	private InvoiceDAO invoiceDAO;
	@Autowired
	private InvoiceFeeDAO invoiceFeeDAO;
	@Autowired
	private AuditTypeDAO auditTypeDAO;
	@Autowired
	private NoteDAO noteDAO;
	@Autowired
	private AuditBuilder auditBuilder;
	@Autowired
	private AuditPercentCalculator auditPercentCalculator;

	private boolean newRegistration = false;
	private String url;

	public String execute() throws Exception {
		this.findContractor();

		InvoiceFee initialFee = invoiceFeeDAO.findByNumberOfOperatorsAndClass(FeeClass.ImportFee, 0);
		InvoiceFee fee = invoiceFeeDAO.findByNumberOfOperatorsAndClass(FeeClass.ImportFee, 1);

		// Does the contractor have a competitor membership?
		if (contractor.getCompetitorMembership() == null || contractor.getCompetitorMembership() == true) {
			// if contractor doesn't have a fee, create it
			if (!contractor.getFees().containsKey(fee.getFeeClass())) {
				ContractorFee newConFee = new ContractorFee();
				newConFee.setAuditColumns(permissions);
				newConFee.setContractor(contractor);
				newConFee.setCurrentAmount(initialFee.getAmount());
				newConFee.setNewAmount(fee.getAmount());
				newConFee.setCurrentLevel(initialFee);
				newConFee.setNewLevel(fee);
				newConFee.setFeeClass(fee.getFeeClass());
				invoiceFeeDAO.save(newConFee);

				contractor.getFees().put(fee.getFeeClass(), newConFee);

				contractor.syncBalance();
			}

			// Did the contractor already pay the ImportPQF fee?
			// Safety check if they make multiple requests
			boolean hasImportInvoice = false;

			for (Invoice invoice : contractor.getInvoices()) {
				for (InvoiceItem item : invoice.getItems()) {
					if (item.getInvoiceFee().getFeeClass().equals(FeeClass.ImportFee)) {
						hasImportInvoice = true;
						break;
					}
				}
			}

			// New registration invoices are handled during the registration process
			if (!hasImportInvoice && !newRegistration) {
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

				// Emailing Invoice to Contractor
				try {
					EmailQueue email = EventSubscriptionBuilder
							.contractorInvoiceEvent(contractor, invoice, permissions);

					String inote = "ImportPQF Invoice emailed to " + email.getToAddresses();
					if (!Strings.isEmpty(email.getCcAddresses()))
						inote += " and cc'd " + email.getCcAddresses();
					Note note = new Note(invoice.getAccount(), getUser(), inote);
					note.setNoteCategory(NoteCategory.Billing);
					note.setCanContractorView(true);
					note.setViewableById(Account.PicsID);
					noteDAO.save(note);
				} catch (Exception e) {
					Note note = new Note(invoice.getAccount(), getUser(), "Failed to send ImportPQF Invoice Email");
					note.setNoteCategory(NoteCategory.Billing);
					note.setCanContractorView(true);
					note.setViewableById(Account.PicsID);
					noteDAO.save(note);
				}
			}

			// Does the contractor already have this audit?
			// Another safety check
			boolean hasImportPQFAudit = false;

			for (ContractorAudit audit : contractor.getAudits()) {
				if (audit.getAuditType().getId() == AuditType.IMPORT_PQF && !audit.isExpired()) {
					hasImportPQFAudit = true;
					break;
				}
			}

			// creating import PQF
			if (!hasImportPQFAudit) {
				ContractorAudit importAudit = new ContractorAudit();
				importAudit.setAuditType(auditTypeDAO.find(AuditType.IMPORT_PQF));
				importAudit.setManuallyAdded(true);
				importAudit.setAuditColumns(permissions);
				importAudit.setContractorAccount(contractor);
				contractor.getAudits().add(importAudit);

				auditBuilder.buildAudits(contractor);
				auditPercentCalculator.percentCalculateComplete(importAudit);

				addNote(contractor, "Import PQF option selected.", NoteCategory.Audits, LowMedHigh.Med, true,
						Account.EVERYONE, new User(permissions.getUserId()));
			}

			contractor.setCompetitorMembership(true);
			accountDao.save(contractor);
		}

		// Finding auditID for redirect after save
		int importAuditID = 0;
		if (Strings.isEmpty(url)) {
			for (ContractorAudit audit : contractor.getAudits()) {
				if (audit.getAuditType().getId() == AuditType.IMPORT_PQF && !audit.isExpired()) {
					importAuditID = audit.getId();
					break;
				}
			}
		}

		this.redirect(Strings.isEmpty(url) ? String.format("Audit.action?auditID=%d", importAuditID) : url
				+ "?newRegistration=true");
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
