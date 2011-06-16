package com.picsauditing.util;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.auditBuilder.AuditBuilder;
import com.picsauditing.auditBuilder.AuditPercentCalculator;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorAuditOperatorPermission;
import com.picsauditing.jpa.entities.ContractorFee;
import com.picsauditing.jpa.entities.FeeClass;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.InvoiceItem;
import com.picsauditing.jpa.entities.OperatorAccount;
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

	private boolean newRegistration = false;
	private String url;

	public String execute() throws Exception {
		this.findContractor();

		InvoiceFee fee = invoiceFeeDAO.findByNumberOfOperatorsAndClass(FeeClass.ImportFee, 0);
		User conUser = new User(User.CONTRACTOR);

		if (newRegistration) {
			ContractorFee newConFee = new ContractorFee();
			newConFee.setAuditColumns(new User(User.CONTRACTOR));
			newConFee.setContractor(contractor);
			newConFee.setCurrentAmount(fee.getAmount());
			newConFee.setNewAmount(fee.getAmount());
			newConFee.setCurrentLevel(fee);
			newConFee.setNewLevel(fee);
			newConFee.setFeeClass(fee.getFeeClass());
			newConFee = (ContractorFee) invoiceFeeDAO.save(newConFee);

			contractor.getFees().put(fee.getFeeClass(), newConFee);
		} else {
			Invoice invoice = new Invoice();
			invoice.setAccount(contractor);
			invoice.setCurrency(contractor.getCurrency());
			invoice.setDueDate(new Date());
			invoice.setTotalAmount(fee.getAmount());
			invoice.setNotes("Thank you for doing business with PICS!");
			invoice.setAuditColumns(conUser);
			invoice.setQbSync(true);
			invoice = (Invoice) invoiceFeeDAO.save(invoice);

			InvoiceItem item = new InvoiceItem(fee);
			item.setInvoice(invoice);
			item.setAuditColumns(conUser);
			invoiceFeeDAO.save(item);
			invoice.getItems().add(item);

			contractor.getInvoices().add(invoice);
		}

		ContractorAudit importAudit = new ContractorAudit();
		importAudit.setAuditType(auditTypeDAO.find(AuditType.IMPORT_PQF));
		importAudit.setManuallyAdded(true);
		importAudit.setAuditColumns(conUser);
		importAudit.setContractorAccount(contractor);
		importAudit = auditDao.save(importAudit);

		ContractorAuditOperator cao = new ContractorAuditOperator();
		cao.setAudit(importAudit);
		cao.setOperator(new OperatorAccount());
		cao.getOperator().setId(4);
		cao = (ContractorAuditOperator) auditDao.save(cao);
		ContractorAuditOperatorPermission caop = new ContractorAuditOperatorPermission();
		caop.setCao(cao);
		caop.setOperator(cao.getOperator());

		contractor.getAudits().add(importAudit);

		auditBuilder.buildAudits(contractor);
		auditPercentCalculator.percentCalculateComplete(importAudit);

		contractor.setCompetitorMembership(true);
		contractor.syncBalance();
		accountDao.save(contractor);

		this.redirect(Strings.isEmpty(url) ? "Audit.action?auditID=" + importAudit.getId() : url);
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
