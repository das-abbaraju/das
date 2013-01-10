package com.picsauditing.util;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.BillingCalculatorSingle;
import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.auditBuilder.AuditBuilder;
import com.picsauditing.auditBuilder.AuditPercentCalculator;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorFee;
import com.picsauditing.jpa.entities.FeeClass;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.User;

@SuppressWarnings("serial")
public class CreateImportPQFAudit extends ContractorActionSupport {
	@Autowired
	private InvoiceFeeDAO invoiceFeeDAO;
	@Autowired
	private AuditTypeDAO auditTypeDAO;
	@Autowired
	private AuditBuilder auditBuilder;
	@Autowired
	private AuditPercentCalculator auditPercentCalculator;
	@Autowired
	private BillingCalculatorSingle billingService;

	private boolean createInvoice = false;
	private String url;

	public String execute() throws Exception {
		this.findContractor();

		// Does the contractor have a competitor membership?
		if (contractor.getCompetitorMembership() == null || contractor.getCompetitorMembership() == true) {
			// Does the contractor already have this audit?
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
				auditDao.save(importAudit);

				auditBuilder.buildAudits(contractor);
				auditPercentCalculator.percentCalculateComplete(importAudit);

				addNote(contractor, "Import PQF option selected.", NoteCategory.Audits, LowMedHigh.Med, true,
						Account.EVERYONE, new User(permissions.getUserId()));
			}

			InvoiceFee initialFee = invoiceFeeDAO.findByNumberOfOperatorsAndClass(FeeClass.ImportFee, 0);
			InvoiceFee fee = invoiceFeeDAO.findByNumberOfOperatorsAndClass(FeeClass.ImportFee, 1);

			// if contractor doesn't have a fee, create it
			if (!contractor.getFees().containsKey(fee.getFeeClass())) {
				ContractorFee newConFee = new ContractorFee();
				newConFee.setAuditColumns(permissions);
				newConFee.setContractor(contractor);
				newConFee.setCurrentAmount(contractor.getCountry().getAmount(initialFee));
				newConFee.setNewAmount(contractor.getCountry().getAmount(fee));
				newConFee.setCurrentLevel(initialFee);
				newConFee.setNewLevel(fee);
				newConFee.setFeeClass(fee.getFeeClass());
				invoiceFeeDAO.save(newConFee);

				contractor.getFees().put(fee.getFeeClass(), newConFee);

				contractor.syncBalance();
				billingService.calculateContractorInvoiceFees(contractor);
			}

			contractor.setCompetitorMembership(true);
			contractorAccountDao.save(contractor);
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

		if (Strings.isEmpty(url))
			this.setUrlForRedirect(String.format("Audit.action?auditID=%d", importAuditID));
		else if (url.contains("ContractorPaymentOptions"))
			this.setUrlForRedirect(url + "?newRegistration=true");
		else
			this.setUrlForRedirect(url);

		return BLANK;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setCreateInvoice(boolean createInvoice) {
		this.createInvoice = createInvoice;
	}

	public boolean isCreateInvoice() {
		return createInvoice;
	}
}
