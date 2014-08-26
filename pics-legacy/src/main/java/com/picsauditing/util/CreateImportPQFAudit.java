package com.picsauditing.util;

import com.picsauditing.PICS.BillingService;
import com.picsauditing.PICS.FeeService;
import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.audits.AuditBuilderFactory;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.jpa.entities.*;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("serial")
public class CreateImportPQFAudit extends ContractorActionSupport {
	@Autowired
	private InvoiceFeeDAO invoiceFeeDAO;
	@Autowired
	private AuditTypeDAO auditTypeDAO;
	@Autowired
	private AuditBuilderFactory auditBuilderFactory;
	@Autowired
	private BillingService billingService;
    @Autowired
    private FeeService feeService;

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

				auditBuilderFactory.buildAudits(contractor);
                auditBuilderFactory.percentCalculateComplete(importAudit);

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
				newConFee.setCurrentAmount(FeeService.getRegionalAmountOverride(contractor, initialFee));
				newConFee.setNewAmount(FeeService.getRegionalAmountOverride(contractor, fee));
				newConFee.setCurrentLevel(initialFee);
				newConFee.setNewLevel(fee);
				newConFee.setFeeClass(fee.getFeeClass());
				invoiceFeeDAO.save(newConFee);

				contractor.getFees().put(fee.getFeeClass(), newConFee);

                billingService.syncBalance(contractor);
				feeService.calculateContractorInvoiceFees(contractor);
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
